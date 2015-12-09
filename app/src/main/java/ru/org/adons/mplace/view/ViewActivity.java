package ru.org.adons.mplace.view;

import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import ru.org.adons.mplace.MConstants;
import ru.org.adons.mplace.Place;
import ru.org.adons.mplace.R;
import ru.org.adons.mplace.db.DBContentProvider;
import ru.org.adons.mplace.db.PlaceTable;
import ru.org.adons.mplace.edit.EditActivity;

public class ViewActivity extends AppCompatActivity {

    private Place place;
    private CollapsingToolbarLayout collapsingToolbar;
    private TextView title;
    private ImageView imageView;
    private TextView location;
    private TextView date;
    private TextView description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        if (savedInstanceState != null) {
            place = (Place) savedInstanceState.getSerializable(MConstants.EXTRA_PLACE);
        } else {
            place = (Place) getIntent().getSerializableExtra(MConstants.EXTRA_PLACE);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.view_toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        title = (TextView) findViewById(R.id.view_title);
        final String nameText = place.getName() == null ? place.getAddress() : place.getName();
        if (collapsingToolbar != null) {
            collapsingToolbar.setTitle(nameText);
        } else if (title != null) {
            title.setText(nameText);
        }

        imageView = (ImageView) findViewById(R.id.view_backdrop);
        if (!TextUtils.isEmpty(place.getImagePath())) {
            Glide.with(this)
                    .load(place.getImagePath())
                    .centerCrop()
                    .into(imageView);
        }

        location = (TextView) findViewById(R.id.card_location);
        if (!TextUtils.isEmpty(place.getAddress())) {
            location.setText(place.getAddress());
        } else {
            final String text = String.valueOf(place.getLatitude()) + ", " + String.valueOf(place.getLongitude());
            location.setText(text);
        }


        date = (TextView) findViewById(R.id.card_location_date);
        date.setText(DateFormat.getLongDateFormat(this).format(place.getDate()));

        description = (TextView) findViewById(R.id.card_info);
        if (!TextUtils.isEmpty(place.getDescription())) {
            description.setText(place.getDescription());
        } else {
            CardView cv = (CardView) findViewById(R.id.card_info_holder);
            cv.setVisibility(View.INVISIBLE);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.view_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final StringBuilder sb = new StringBuilder();
                sb.append("geo:")
                        .append(place.getLatitude())
                        .append(",")
                        .append(place.getLongitude())
                        .append("?q=");
                if (!TextUtils.isEmpty(place.getAddress())) {
                    try {
                        sb.append(URLEncoder.encode(place.getAddress(), "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        Log.e(MConstants.LOG_TAG, ViewActivity.this.getLocalClassName() + " :GEO: unable encode address", e);
                    }
                } else {
                    sb.append(place.getLatitude())
                            .append(",")
                            .append(place.getLongitude());
                }
                if (!TextUtils.isEmpty(place.getName())) {
                    sb.append("(");
                    try {
                        sb.append(URLEncoder.encode(place.getName(), "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        Log.e(MConstants.LOG_TAG, ViewActivity.this.getLocalClassName() + " :GEO: unable encode name", e);
                    }
                    sb.append(")");
                }
                Log.d(MConstants.LOG_TAG, ViewActivity.this.getLocalClassName() + " :GEO: " + sb.toString());
                Uri location = Uri.parse(sb.toString());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(mapIntent, MConstants.CODE_VIEW_MAP);
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(MConstants.EXTRA_PLACE, place);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Handle click Action Bar Button 'Edit'
     */
    public void edit(MenuItem item) {
        Intent intent = new Intent(this, EditActivity.class);
        intent.setAction(MConstants.ACTION_EDIT_PLACE);
        intent.putExtra(MConstants.EXTRA_PLACE, place);
        startActivityForResult(intent, MConstants.CODE_EDIT_PLACE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MConstants.CODE_EDIT_PLACE && resultCode == RESULT_OK) {
            final Place newPlace = (Place) data.getSerializableExtra(MConstants.EXTRA_PLACE);
            place.setName(newPlace.getName());
            place.setImagePath(newPlace.getImagePath());
            place.setDate(newPlace.getDate());
            place.setDescription(newPlace.getDescription());

            final String nameText = place.getName() == null ? location.getText().toString() : place.getName();
            if (collapsingToolbar != null) {
                collapsingToolbar.setTitle(nameText);
            } else if (title != null) {
                title.setText(nameText);
            }

            Glide.with(this)
                    .load(place.getImagePath())
                    .centerCrop()
                    .into(imageView);

            date.setText(DateFormat.getLongDateFormat(this).format(place.getDate()));
            description.setText(place.getDescription());
        }
    }

    /**
     * Handle click Action Bar Button 'Delete'
     */
    public void delete(MenuItem item) {
        String where = "(" + PlaceTable._ID + " = " + place.getID() + ")";
        Uri uri = ContentUris.withAppendedId(DBContentProvider.CONTENT_ID_URI, place.getID());
        getContentResolver().delete(uri, where, null);
        finish();
    }

    @Override
    protected void onDestroy() {
        Log.d(MConstants.LOG_TAG, this.getLocalClassName() + ": destroy");
        super.onDestroy();
    }
}
