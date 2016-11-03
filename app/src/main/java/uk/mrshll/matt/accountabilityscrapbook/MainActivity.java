package uk.mrshll.matt.accountabilityscrapbook;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import io.realm.Realm;

import io.realm.RealmResults;
import uk.mrshll.matt.accountabilityscrapbook.model.Scrapbook;

public class MainActivity extends AppCompatActivity

        implements NavigationView.OnNavigationItemSelectedListener {

    private Realm realm;
    private SharedPreferences preferences;
    private ScrapbookAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
//        THIS IS SCARY DEFAULT STUFF
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // The floater button is designed to add scrapbooks
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CreateScrapbookActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


//        TRY TO INITIALISE REALM DATABASE
        realm = Realm.getDefaultInstance();

//        Check to see if we have scrapbooks. If we don't, then we start off by running the activity to create one
        RealmResults<Scrapbook> results = realm.where(Scrapbook.class).findAll();
//        Toast.makeText(this, results.size(), Toast.LENGTH_SHORT).show();
        if(results.isEmpty()) // Check length of results array
        {
            Toast.makeText(this, "THere don't appear to be any scrapbooks", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, CreateScrapbookActivity.class);
            startActivityForResult(intent, 1);
        } else
        {
            // TODO Draw the collection of scrapbooks
//            // Get the gridview from the layout I suppose
            ListView grid = (ListView) findViewById(R.id.scrapbook_grid);

           // Convert the realm results into an array of scrapbooks
            ArrayList<Scrapbook> values = new ArrayList<Scrapbook>();
            for(Scrapbook s : results)
            {
                values.add(s);
            }
            this.adapter = new ScrapbookAdapter(this, values);
            grid.setAdapter(adapter);
        }


    }

    @Override
    public void onResume()
    {
        super.onResume();

    }

    /**
     * We're adding this to redraw the list with new data
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == 1)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                // Quite dirty but might work for now; reload all of the scrapbooks
                RealmResults<Scrapbook> results = realm.where(Scrapbook.class).findAll();
                ListView grid = (ListView) findViewById(R.id.scrapbook_grid);

                // Convert the realm results into an array of scrapbooks
                ArrayList<Scrapbook> values = new ArrayList<Scrapbook>();
                for(Scrapbook s : results)
                {
                    values.add(s);
                }
                this.adapter = new ScrapbookAdapter(this, values);
                grid.setAdapter(adapter);
                this.adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Start the create photo scrap
            Intent intent = new Intent(MainActivity.this, AddPhotoscrapActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_gallery) {
            Toast.makeText(getApplicationContext(), "That tickles", Toast.LENGTH_LONG).show();

        } else if (id == R.id.nav_slideshow) {

            Toast.makeText(getApplicationContext(), "That tickles", Toast.LENGTH_LONG).show();

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
