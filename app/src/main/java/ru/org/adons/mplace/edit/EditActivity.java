package ru.org.adons.mplace.edit;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ru.org.adons.mplace.CategoryAdapter;
import ru.org.adons.mplace.MainActivity;
import ru.org.adons.mplace.Place;
import ru.org.adons.mplace.R;
import ru.org.adons.mplace.db.DBContentProvider;
import ru.org.adons.mplace.db.PlaceTable;

public class EditActivity extends AppCompatActivity {

    private static final int CODE_TAKE_IMAGE = 3;
    private Place place;
    private ImageView imageView;
    private String imagePath;
    private FloatingActionButton fab;
    private Spinner category;
    private EditText name;
    private EditText description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear_white_24dp);

        imageView = (ImageView) findViewById(R.id.edit_image);

        CategoryAdapter adapter = new CategoryAdapter(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category = (Spinner) findViewById(R.id.edit_category);
        category.setAdapter(adapter);

        name = (EditText) findViewById(R.id.edit_name);
        description = (EditText) findViewById(R.id.edit_description);
        fab = (FloatingActionButton) findViewById(R.id.edit_fab);

        // ACTION EDIT - set initial value
        if (MainActivity.ACTION_EDIT_PLACE.equals(getIntent().getAction())) {
            place = (Place) getIntent().getSerializableExtra(MainActivity.EXTRA_PLACE);
            imagePath = place.getImagePath();
            if (!TextUtils.isEmpty(imagePath)) {
                Glide.with(this)
                        .load(place.getImagePath())
                        .centerCrop()
                        .into(imageView);
                moveFab();
            }
            name.setText(place.getName());
            description.setText(place.getDescription());
        }

        // TAKE IMAGE
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    // set file to store image
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date());
                    String imageFileName = "my_place_" + timeStamp;
                    File albumFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    File imageFile = null;
                    try {
                        imageFile = File.createTempFile(imageFileName, ".jpg", albumFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (imageFile != null) {
                        imagePath = imageFile.getAbsolutePath();
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
                        startActivityForResult(intent, CODE_TAKE_IMAGE);
                    }
                }
            }
        });

        setResult(RESULT_CANCELED);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(MainActivity.LOG_TAG, this.getLocalClassName() + ": onActivityResult");
        if (requestCode == CODE_TAKE_IMAGE && resultCode == RESULT_OK) {
            // set imageView and thumbnail to store in DB and show in list
            if (!TextUtils.isEmpty(imagePath)) {
                ImageUtils.addImageToGallery(this, imagePath);
                Glide.with(this)
                        .load(imagePath)
                        .centerCrop()
                        .into(imageView);
                moveFab();
            }
        }
    }

    // move floating "Take Picture" button to right-end corner
    private void moveFab() {
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lp.setMargins(0, 0, 32, 32);
        fab.setLayoutParams(lp);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Handle click Action Bar Button 'Done'
     */
    public void done(MenuItem item) {
        // ADD PLACE
        if (MainActivity.ACTION_ADD_PLACE.equals(getIntent().getAction())) {
            getContentResolver().insert(DBContentProvider.CONTENT_URI, getContentValues());
            setResult(RESULT_OK);

            // EDIT PLACE
        } else if (MainActivity.ACTION_EDIT_PLACE.equals(getIntent().getAction())) {
            if (!TextUtils.isEmpty(imagePath) && !imagePath.equals(place.getImagePath())
                    || !name.getText().equals(place.getName())
                    || !description.getText().equals(place.getDescription())) {

                String where = "(" + PlaceTable._ID + " = " + place.getID() + ")";
                Uri uri = ContentUris.withAppendedId(DBContentProvider.CONTENT_ID_URI, place.getID());
                getContentResolver().update(uri, getContentValues(), where, null);

                place.setName(name.getText().toString());
                place.setDescription(description.getText().toString());
                place.setDate(new Date());
                place.setImagePath(imagePath);

                Intent result = new Intent();
                result.putExtra(MainActivity.EXTRA_PLACE, place);
                setResult(RESULT_OK, result);
            }
        }
        finish();
    }

    private ContentValues getContentValues() {
        Log.d(MainActivity.LOG_TAG, this.getLocalClassName() + ": CV 1");

        final ContentValues values = new ContentValues();
        values.put(PlaceTable.NAME, name.getText().toString());
        values.put(PlaceTable.DATE, new Date().getTime());
        values.put(PlaceTable.DESCRIPTION, description.getText().toString());
        // put thumbnail image
        // TODO: all DB action should be in background, include Glide.load
//        if (!TextUtils.isEmpty(imagePath)) {
//            byte[] res = new byte[1];
//            Glide.with(this)
//                    .load(imagePath)
//                    .asBitmap()
//                    .toBytes()
//                    .into(new SimpleTarget<byte[]>(96, 96) {
//                        @Override
//                        public void onResourceReady(byte[] data, GlideAnimation anim) {
//
//                        }
//                    });
//            int i = res.length;
//        }
        Bitmap thumbnail = null;
        if (!TextUtils.isEmpty(imagePath)) {
            thumbnail = ImageUtils.decodeBitmapFromFile(imagePath, 96, 96);
        }
        if (thumbnail != null) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.PNG, 0, out);
            values.put(PlaceTable.THUMBNAIL, out.toByteArray());
        }
        // put imagePath
        values.put(PlaceTable.IMAGE_PATH, imagePath);
        Log.d(MainActivity.LOG_TAG, this.getLocalClassName() + ": CV 2");
        return values;
    }

    @Override
    protected void onDestroy() {
        Log.d(MainActivity.LOG_TAG, this.getLocalClassName() + ": destroy");
        super.onDestroy();
    }
}

