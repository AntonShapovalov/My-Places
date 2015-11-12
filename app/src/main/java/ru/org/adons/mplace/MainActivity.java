package ru.org.adons.mplace;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import ru.org.adons.mplace.edit.EditActivity;
import ru.org.adons.mplace.list.ListLoader;
import ru.org.adons.mplace.list.RecyclerAdapter;
import ru.org.adons.mplace.view.CategoryAdapter;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = "MPLACE";
    public static final String ACTION_ADD_PLACE = "ADD_PLACE";
    private static final int CODE_ADD_PLACE = 1;
    private DrawerLayout drawerLayout;
    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(MainActivity.LOG_TAG, "MAIN:onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                filterList(menuItem.getItemId());
                return true;
            }
        });

        // ADD PLACE
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.main_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.setAction(ACTION_ADD_PLACE);
                startActivityForResult(intent, CODE_ADD_PLACE);
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new RecyclerAdapter(this);
        getSupportLoaderManager().initLoader(0, null, new ListLoader(this, adapter));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_ADD_PLACE && resultCode == RESULT_OK) {
            // TODO: implement incremental adding to prevent reload all items via Loader
            // get rowID from Intent data
            // adapter.addPlace(rowID)
            // adapter.notifyItemInserted(0); - added always in top of list
            recyclerView.smoothScrollToPosition(0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_action_settings:
                return true;
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Perform list filtering by category
     */
    private void filterList(int category) {
        // TODO: filter list by category
        Toast.makeText(this, CategoryAdapter.categories.get(category).toString(), Toast.LENGTH_LONG).show();
    }

}
