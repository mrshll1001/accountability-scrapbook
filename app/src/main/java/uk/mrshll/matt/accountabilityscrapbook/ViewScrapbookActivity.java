package uk.mrshll.matt.accountabilityscrapbook;

import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import io.realm.Realm;
import uk.mrshll.matt.accountabilityscrapbook.Adapter.SpendScrapAdapter;
import uk.mrshll.matt.accountabilityscrapbook.model.Scrapbook;

public class ViewScrapbookActivity extends AppCompatActivity
{

    private Realm realm;
    private String scrapbookName;

//    final private int SPINNER_SELECTED_ALL = 0;
    final private int SPINNER_SELECTED_PHOTOGRAPHS = 0;
    final private int SPINNER_SELECTED_SPENDS = 1;
    final private int SPINNER_SELECTED_QUOTES = 2;
    final private int SPINNER_SELECTED_EVENTS = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_scrapbook2);

        // Set up realm
        this.realm = Realm.getDefaultInstance();

        // Get the Scrapbook name and give it to Realm
        this.scrapbookName = getIntent().getStringExtra("scrapbook_name");
        this.setTitle(this.scrapbookName);

        final Scrapbook scrapbook = realm.where(Scrapbook.class)
                                    .equalTo("name", this.scrapbookName)
                                    .findFirst();

        // Get a bit cute with the interface
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(scrapbook.getColour()));

        // Get the spinner and attach an event listener to fire on item select
        Spinner spinner = (Spinner) findViewById(R.id.view_scrapbook_filter_spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id)
            {
                // Set up variable to hold results of query
                String text = "";

                // Switch on the position to determine which query to run.
                switch (pos)
                {
//                    case SPINNER_SELECTED_ALL:
//                        text = "All";
//                        break;
                    case SPINNER_SELECTED_PHOTOGRAPHS:
                        text = String.valueOf(scrapbook.getPhotoList().size());
                        break;
                    case SPINNER_SELECTED_SPENDS:
                        RecyclerView recycler = (RecyclerView) findViewById(R.id.view_scrapbook_recycler);
                        recycler.setLayoutManager(new LinearLayoutManager(ViewScrapbookActivity.this));
//                        recycler.setLayoutManager(new GridLayoutManager(ViewScrapbookActivity.this, ));
                        recycler.setAdapter(new SpendScrapAdapter(ViewScrapbookActivity.this, scrapbook.getSpendList()));


                        break;
                    case SPINNER_SELECTED_QUOTES:
                        text = String.valueOf(scrapbook.getQuoteList().size());
                        break;
                    case SPINNER_SELECTED_EVENTS:
                        text = String.valueOf(scrapbook.getEventList().size());
                        break;
                    default:
                        Toast.makeText(ViewScrapbookActivity.this, "Error has occurred, invalid option", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {

            }
        });

    }
}
