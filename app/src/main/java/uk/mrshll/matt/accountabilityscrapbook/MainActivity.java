package uk.mrshll.matt.accountabilityscrapbook;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import uk.mrshll.matt.accountabilityscrapbook.Adapter.ScrapbookAdapter;
import uk.mrshll.matt.accountabilityscrapbook.model.Scrapbook;

import static android.support.design.R.styleable.RecyclerView;

public class MainActivity extends AppCompatActivity

        implements NavigationView.OnNavigationItemSelectedListener {

    private Realm realm;
    private SharedPreferences preferences;
    private ScrapbookAdapter adapter;

    private RecyclerView recyclerView;
    private ScrapbookAdapter viewAdapter;
    private RecyclerView.LayoutManager viewLayoutManager;

    private int CREATE_SCRAPBOOK_REQUEST = 1;
    private int CREATE_SPEND_REQUEST = 2;
    private int CREATE_PHOTO_REQUEST = 3;
    private int CREATE_EVENT_REQUEST = 4;
    private int CREATE_QUOTE_REQUEST = 5;

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
                startActivityForResult(intent, CREATE_SCRAPBOOK_REQUEST);
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
            Toast.makeText(this, "Add your first scrapbook here", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, CreateScrapbookActivity.class);
            startActivityForResult(intent, 1);
        } else
        {
            recyclerView = (RecyclerView) findViewById(R.id.scrapbook_recycler);
            viewLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(viewLayoutManager);

            viewAdapter = new ScrapbookAdapter(results);
            recyclerView.setAdapter(viewAdapter);

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
        if (requestCode == CREATE_SCRAPBOOK_REQUEST || requestCode == CREATE_SPEND_REQUEST || requestCode == CREATE_PHOTO_REQUEST)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                // Quite dirty but might work for now; reload all of the scrapbooks
                RealmResults<Scrapbook> results = realm.where(Scrapbook.class).findAll();
                recyclerView = (RecyclerView) findViewById(R.id.scrapbook_recycler);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.setAdapter(new ScrapbookAdapter(results));
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

        if (id == R.id.nav_add_photo) {
            // Start the create photo scrap
            Intent intent = new Intent(MainActivity.this, AddPhotoscrapActivity.class);
            startActivityForResult(intent, CREATE_PHOTO_REQUEST);

        } else if (id == R.id.nav_add_spend) {


            // Start the create spend scrap
            Intent intent =  new Intent(MainActivity.this, AddSpendscrapActivity.class);
            startActivityForResult(intent, CREATE_SPEND_REQUEST);

        } else if (id == R.id.nav_quote) {
            // Start the create quote scrap
            Intent intent  = new Intent(MainActivity.this, AddQuotescrapActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_event) {
            Intent intent = new Intent(MainActivity.this, AddEventscrapActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
