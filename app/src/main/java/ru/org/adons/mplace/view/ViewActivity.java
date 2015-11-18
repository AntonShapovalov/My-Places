package ru.org.adons.mplace.view;

import android.content.ContentUris;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import ru.org.adons.mplace.R;
import ru.org.adons.mplace.db.DBContentProvider;
import ru.org.adons.mplace.db.PlaceTable;
import ru.org.adons.mplace.edit.EditActivity;

public class ViewActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "id";
    public static final String EXTRA_NAME = "name";
    public static final String EXTRA_DATE = "date";
    public static final String EXTRA_DESCRIPTION = "description";
    public static final String EXTRA_IMAGE_PATH = "image_path";
    public static final String ACTION_EDIT_PLACE = "EDIT_PLACE";
    private static final int CODE_EDIT_PLACE = 2;
    private int ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.view_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ID = getIntent().getIntExtra(EXTRA_ID, -1);

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        final String name = getIntent().getStringExtra(EXTRA_NAME);
        collapsingToolbar.setTitle(name);

        TextView textDesc = (TextView) findViewById(R.id.view_desc);
        final String description = getIntent().getStringExtra(EXTRA_DESCRIPTION);
        textDesc.setText(description);

        ImageView imageView = (ImageView) findViewById(R.id.view_backdrop);
        final String imagePath = getIntent().getStringExtra(EXTRA_IMAGE_PATH);
        if (!TextUtils.isEmpty(imagePath)) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
        }

        TextView textDate = (TextView) findViewById(R.id.view_date);
        final String date = getIntent().getStringExtra(EXTRA_DATE);
        textDate.setText(date);

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
        intent.setAction(ACTION_EDIT_PLACE);
        intent.putExtra(EXTRA_IMAGE_PATH, getIntent().getStringExtra(EXTRA_IMAGE_PATH));
        intent.putExtra(EXTRA_NAME, getIntent().getStringExtra(EXTRA_NAME));
        intent.putExtra(EXTRA_DESCRIPTION, getIntent().getStringExtra(EXTRA_DESCRIPTION));
        startActivityForResult(intent, CODE_EDIT_PLACE);
    }

    /**
     * Handle click Action Bar Button 'Delete'
     */
    public void delete(MenuItem item) {
        String where = "(" + PlaceTable._ID + " = " + ID + ")";
        Uri uri = ContentUris.withAppendedId(DBContentProvider.CONTENT_ID_URI, ID);
        getContentResolver().delete(uri, where, null);
        finish();
    }

}
