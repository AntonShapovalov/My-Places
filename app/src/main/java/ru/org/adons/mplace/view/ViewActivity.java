package ru.org.adons.mplace.view;

import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import ru.org.adons.mplace.R;
import ru.org.adons.mplace.db.DBContentProvider;
import ru.org.adons.mplace.db.PlaceTable;
import ru.org.adons.mplace.edit.EditActivity;

public class ViewActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "id";
    public static final String EXTRA_NAME = "name";
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

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        TextView textName = (TextView) findViewById(R.id.view_name);

        if (savedInstanceState != null) {
            setValues(savedInstanceState);
        } else {
            Intent intent = getIntent();
            ID = intent.getIntExtra(EXTRA_ID, -1);
            final String name = intent.getStringExtra(EXTRA_NAME);
            collapsingToolbar.setTitle(name);
            textName.setText(name);
        }

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
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(EXTRA_ID, ID);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        setValues(savedInstanceState);
    }

    private void setValues(Bundle savedInstanceState) {
        ID = savedInstanceState.getInt(EXTRA_ID);
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
