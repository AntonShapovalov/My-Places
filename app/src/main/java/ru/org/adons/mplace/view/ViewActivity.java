package ru.org.adons.mplace.view;

import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import ru.org.adons.mplace.MainActivity;
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

        place = (Place) getIntent().getSerializableExtra(MainActivity.EXTRA_PLACE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.view_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        title = (TextView) findViewById(R.id.view_title);
        if (collapsingToolbar != null) {
            collapsingToolbar.setTitle(place.getName());
        } else if (title != null) {
            title.setText(place.getName());
        }

        imageView = (ImageView) findViewById(R.id.view_backdrop);
        if (!TextUtils.isEmpty(place.getImagePath())) {
            Glide.with(this)
                    .load(place.getImagePath())
                    .centerCrop()
                    .into(imageView);
        }

        location = (TextView) findViewById(R.id.card_location);
        location.setText("Default Location");

        date = (TextView) findViewById(R.id.card_location_date);
        date.setText(DateFormat.getLongDateFormat(this).format(place.getDate()));

        description = (TextView) findViewById(R.id.card_info);
        description.setText(place.getDescription());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.view_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: go to the Map by coordinates
                Toast.makeText(view.getContext(), "GOTO MAP!", Toast.LENGTH_SHORT).show();
            }
        });
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
        intent.setAction(MainActivity.ACTION_EDIT_PLACE);
        intent.putExtra(MainActivity.EXTRA_PLACE, place);
        startActivityForResult(intent, MainActivity.CODE_EDIT_PLACE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MainActivity.CODE_EDIT_PLACE && resultCode == RESULT_OK) {
            place = (Place) data.getSerializableExtra(MainActivity.EXTRA_PLACE);
            if (collapsingToolbar != null) {
                collapsingToolbar.setTitle(place.getName());
            } else if (title != null) {
                title.setText(place.getName());
            }

            Glide.with(this)
                    .load(place.getImagePath())
                    .centerCrop()
                    .into(imageView);
            location.setText("Default Location");
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
        Log.d(MainActivity.LOG_TAG, this.getLocalClassName() + ": destroy");
        super.onDestroy();
    }
}
