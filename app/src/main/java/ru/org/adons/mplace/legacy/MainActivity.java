package ru.org.adons.mplace.legacy;

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

import java.util.TreeMap;

import ru.org.adons.mplace.R;
import ru.org.adons.mplace.legacy.edit.AddActivity;
import ru.org.adons.mplace.legacy.list.RecyclerAdapter;
import ru.org.adons.mplace.legacy.logcat.LogcatMainActivity;

public class MainActivity extends AppCompatActivity {

    public static final TreeMap<Integer, String> categories = new TreeMap<>();
    private DrawerLayout drawerLayout;
    private RecyclerView recyclerView;
    //TODO: handle add request - private boolean isInsertRequested = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(MConstants.LOG_TAG, this.getLocalClassName() + ":onCreate");
        super.onCreate(savedInstanceState);
        if (categories.size() == 0) {
            categories.put(R.id.nav_place, getString(R.string.nav_place));
            categories.put(R.id.nav_shop, getString(R.string.nav_shop));
            categories.put(R.id.nav_cafe, getString(R.string.nav_cafe));
            categories.put(R.id.nav_picnic, getString(R.string.nav_picnic));
            categories.put(R.id.nav_favorite, getString(R.string.nav_favorite));
        }
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setHomeAsUpIndicator(R.drawable.ic_menu);
            ab.setDisplayHomeAsUpEnabled(true);
        }

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
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                intent.setAction(MConstants.ACTION_ADD_PLACE);
                startActivityForResult(intent, MConstants.CODE_ADD_PLACE);
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        RecyclerAdapter adapter = new RecyclerAdapter(this);
        getSupportLoaderManager().initLoader(0, null, adapter);
        recyclerView.setAdapter(adapter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MConstants.CODE_ADD_PLACE && resultCode == RESULT_OK) {
            // TODO: implement incremental adding to prevent reload all items via Loader
            // get rowID from Intent data
            // adapter.addPlace(rowID)
            // adapter.notifyItemInserted(0); - added always in top of list
            recyclerView.smoothScrollToPosition(0);
            //RecyclerAdapter adapter = (RecyclerAdapter) recyclerView.getAdapter();
            //adapter.notifyItemChanged(0);
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
            case R.id.main_action_logcat:
                Intent intent = new Intent(this, LogcatMainActivity.class);
                startActivity(intent);
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
        Toast.makeText(this, categories.get(category), Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        Log.d(MConstants.LOG_TAG, this.getLocalClassName() + ": destroy");
        super.onDestroy();
    }

}
