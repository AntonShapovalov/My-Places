package ru.org.adons.mplace.edit;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ru.org.adons.mplace.CategoryAdapter;
import ru.org.adons.mplace.MConstants;
import ru.org.adons.mplace.Place;
import ru.org.adons.mplace.R;
import ru.org.adons.mplace.db.DBUpdateService;

public class EditActivity extends AppCompatActivity {

    private Place place;
    private ImageView imageView;
    private String imagePath;
    private boolean isImage = false;
    private FloatingActionButton fab;
    private EditText name;
    private EditText description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setHomeAsUpIndicator(R.drawable.ic_clear_white_24dp);
        }

        imageView = (ImageView) findViewById(R.id.edit_image);

        CategoryAdapter adapter = new CategoryAdapter(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner category = (Spinner) findViewById(R.id.edit_category);
        category.setAdapter(adapter);

        name = (EditText) findViewById(R.id.edit_name);
        description = (EditText) findViewById(R.id.edit_description);
        fab = (FloatingActionButton) findViewById(R.id.edit_fab);

        // ACTION EDIT - set initial value
        if (MConstants.ACTION_EDIT_PLACE.equals(getIntent().getAction())) {
            place = (Place) getIntent().getSerializableExtra(MConstants.EXTRA_PLACE);
            imagePath = place.getImagePath();
            isImage = true;
            if (!TextUtils.isEmpty(imagePath)) {
                setImage();
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
                    @SuppressLint("SimpleDateFormat")
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
                        startActivityForResult(intent, MConstants.CODE_TAKE_IMAGE);
                    }
                }
            }
        });

        setResult(RESULT_CANCELED);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(MConstants.STATE_IMAGE_PATH, imagePath);
        outState.putBoolean(MConstants.STATE_IS_IMAGE, isImage);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        imagePath = savedInstanceState.getString(MConstants.STATE_IMAGE_PATH);
        isImage = savedInstanceState.getBoolean(MConstants.STATE_IS_IMAGE);
        if (isImage && !TextUtils.isEmpty(imagePath)) {
            setImage();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MConstants.CODE_TAKE_IMAGE && resultCode == RESULT_OK) {
            isImage = true;
            if (!TextUtils.isEmpty(imagePath)) {
                ImageUtils.addImageToGallery(this, imagePath);
                setImage();
            }
        }
    }

    private void setImage() {
        Glide.with(this)
                .load(imagePath)
                .centerCrop()
                .into(imageView);

        // move floating "Take Picture" button to right-end corner
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
        final Place newPlace = getNewPlace();

        // ADD PLACE
        if (MConstants.ACTION_ADD_PLACE.equals(getIntent().getAction())) {
            startDBService(MConstants.ACTION_ADD_PLACE, newPlace);
            setResult(RESULT_OK);

            // EDIT PLACE
        } else if (MConstants.ACTION_EDIT_PLACE.equals(getIntent().getAction())) {
            if (!TextUtils.isEmpty(imagePath) && !imagePath.equals(place.getImagePath())
                    || !name.getText().toString().equals(place.getName())
                    || !description.getText().toString().equals(place.getDescription())) {

                newPlace.setID(place.getID());
                startDBService(MConstants.ACTION_EDIT_PLACE, newPlace);

                Intent result = new Intent();
                result.putExtra(MConstants.EXTRA_PLACE, newPlace);
                setResult(RESULT_OK, result);
            }
        }
        finish();
    }

    private Place getNewPlace() {
        final Place p = new Place();
        p.setName(name.getText().toString());
        p.setDescription(description.getText().toString());
        p.setDate(new Date());
        p.setImagePath(imagePath);
        return p;
    }

    private void startDBService(String action, Place newPlace) {
        final Intent serviceIntent = new Intent(this, DBUpdateService.class)
                .setAction(action)
                .putExtra(MConstants.EXTRA_PLACE, newPlace);
        startService(serviceIntent);
    }

    @Override
    protected void onDestroy() {
        Log.d(MConstants.LOG_TAG, this.getLocalClassName() + ": destroy");
        super.onDestroy();
    }
}

