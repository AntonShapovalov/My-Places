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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ru.org.adons.mplace.CategoryAdapter;
import ru.org.adons.mplace.MainActivity;
import ru.org.adons.mplace.R;
import ru.org.adons.mplace.db.DBContentProvider;
import ru.org.adons.mplace.db.PlaceTable;
import ru.org.adons.mplace.view.ViewActivity;

public class EditActivity extends AppCompatActivity {

    private static final int CODE_TAKE_IMAGE = 3;
    private ImageView imageView;
    private String imagePath;
    private Bitmap thumbnail;
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

        // TAKE IMAGE
        fab = (FloatingActionButton) findViewById(R.id.edit_fab);
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

        CategoryAdapter adapter = new CategoryAdapter(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category = (Spinner) findViewById(R.id.edit_category);
        category.setAdapter(adapter);

        name = (EditText) findViewById(R.id.edit_name);
        description = (EditText) findViewById(R.id.edit_description);

        // ACTION EDIT - set initial value
        if (getIntent().getAction() == ViewActivity.ACTION_EDIT_PLACE) {
            imagePath = getIntent().getStringExtra(ViewActivity.EXTRA_IMAGE_PATH);
            if (!TextUtils.isEmpty(imagePath)) {
                Bitmap bitmap = ImageUtils.decodeBitmapFromFile(imagePath, imageView.getWidth(), imageView.getHeight());
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                    moveFab();
                }
            }
            name.setText(getIntent().getStringExtra(ViewActivity.EXTRA_NAME));
            description.setText(getIntent().getStringExtra(ViewActivity.EXTRA_DESCRIPTION));
        }

        setResult(RESULT_CANCELED);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_TAKE_IMAGE && resultCode == RESULT_OK) {
            // set imageView and thumbnail to store in DB and show in list
            Bitmap bitmap = null;
            if (!TextUtils.isEmpty(imagePath)) {
                ImageUtils.addImageToGallery(this, imagePath);
                bitmap = ImageUtils.decodeBitmapFromFile(imagePath, imageView.getWidth(), imageView.getHeight());
                thumbnail = ImageUtils.decodeBitmapFromFile(imagePath, 160, 160);
            }
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
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
        if (getIntent().getAction() == MainActivity.ACTION_ADD_PLACE) {
            getContentResolver().insert(DBContentProvider.CONTENT_URI, getContentValues());
            setResult(RESULT_OK);
        } else if (getIntent().getAction() == ViewActivity.ACTION_EDIT_PLACE) {
            if (!TextUtils.isEmpty(imagePath) && !imagePath.equals(getIntent().getStringExtra(ViewActivity.EXTRA_IMAGE_PATH))
                    || !name.getText().equals(getIntent().getStringExtra(ViewActivity.EXTRA_NAME))
                    || !description.getText().equals(getIntent().getStringExtra(ViewActivity.EXTRA_DESCRIPTION))) {
                int ID = getIntent().getIntExtra(ViewActivity.EXTRA_ID, -1);
                String where = "(" + PlaceTable._ID + " = " + ID + ")";
                Uri uri = ContentUris.withAppendedId(DBContentProvider.CONTENT_ID_URI, ID);
                getContentResolver().update(uri, getContentValues(), where, null);

                Intent result = new Intent();
                result.putExtra(ViewActivity.EXTRA_NAME, name.getText());
                result.putExtra(ViewActivity.EXTRA_IMAGE_PATH, imagePath);
                result.putExtra(ViewActivity.EXTRA_DESCRIPTION, description.getText());
                setResult(RESULT_OK, result);
            }
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }

    private ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(PlaceTable.NAME, name.getText().toString());
        values.put(PlaceTable.DATE, new Date().getTime());
        values.put(PlaceTable.DESCRIPTION, description.getText().toString());
        // put thumbnail image
        if (thumbnail != null) {
            byte[] bytes = null;
            try {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.PNG, 0, out);
                bytes = out.toByteArray();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            values.put(PlaceTable.THUMBNAIL, bytes);
        }
        // put imagePath
        values.put(PlaceTable.IMAGE_PATH, imagePath);
        return values;
    }
}
