package uk.mrshll.matt.accountabilityscrapbook;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Date;

import io.realm.Realm;

import io.realm.RealmResults;
import uk.mrshll.matt.accountabilityscrapbook.model.Scrapbook;

public class MainActivity extends AppCompatActivity

        implements NavigationView.OnNavigationItemSelectedListener {

    private Realm realm;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
//        THIS IS SCARY DEFAULT STUFF
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CreateScrapbookActivity.class);
                MainActivity.this.startActivity(intent);
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


//        Check to see if the application has been run by checking if a preference exists
        preferences = getPreferences(Context.MODE_PRIVATE);
        if (preferences.contains("DATE_INITIALISED"))
        {
//        Make toast
            RealmResults<Scrapbook> results = realm.where(Scrapbook.class).findAll();
            String resultString = "I know of the following Scrapbooks:";
            for (Scrapbook s : results)
            {
                resultString = resultString + " " + s.getName();
            }

            Toast.makeText(this, resultString, Toast.LENGTH_SHORT).show();


        } else
        {
//        Else, create the preference, set the date, and prompt the user to create a scrapbook first thing

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("DATE_INITIALISED", "I was initialised today yo!");
            editor.apply();

            Intent intent = new Intent(MainActivity.this, CreateScrapbookActivity.class);
            MainActivity.this.startActivity(intent);

        }




    }

    /**
     * Creates the default scrapbook during initialisation.
     * @param realm
     * @return
     */
    private void createDefaultScrapbook(Realm realm)
    {
        realm.beginTransaction();
        Scrapbook default_scrapbook = realm.createObject(Scrapbook.class);
        default_scrapbook.setName("Example Scrapbook");
        realm.commitTransaction();

        Toast.makeText(this, default_scrapbook.getName() + " Created", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getApplicationContext(), "Hello Marshall. I am your app", Toast.LENGTH_LONG).show();
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
