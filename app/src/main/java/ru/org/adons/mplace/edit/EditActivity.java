package ru.org.adons.mplace.edit;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Date;

import ru.org.adons.mplace.MainActivity;
import ru.org.adons.mplace.R;
import ru.org.adons.mplace.db.DBContentProvider;
import ru.org.adons.mplace.db.PlaceTable;
import ru.org.adons.mplace.view.CategoryAdapter;
import ru.org.adons.mplace.view.ViewActivity;

public class EditActivity extends AppCompatActivity {

    private Spinner category;
    private EditText name;
    private EditText description;
    private String action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear_white_24dp);

        CategoryAdapter adapter = new CategoryAdapter(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category = (Spinner) findViewById(R.id.edit_category);
        category.setAdapter(adapter);

        name = (EditText) findViewById(R.id.edit_name);
        description = (EditText) findViewById(R.id.edit_description);

        action = getIntent().getAction();

        setResult(RESULT_CANCELED);
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
        if (action == MainActivity.ACTION_ADD_PLACE) {
            ContentValues values = new ContentValues();
            values.put(PlaceTable.NAME, name.getText().toString());
            values.put(PlaceTable.DATE, new Date().getTime());
            values.put(PlaceTable.DESCRIPTION, description.getText().toString());
            getContentResolver().insert(DBContentProvider.CONTENT_URI, values);
        } else if (action == ViewActivity.ACTION_EDIT_PLACE) {
            // TODO: perform update
        }
        setResult(RESULT_OK);
        finish();
    }
}
