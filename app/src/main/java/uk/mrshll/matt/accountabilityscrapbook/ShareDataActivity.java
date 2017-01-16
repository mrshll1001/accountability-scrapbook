package uk.mrshll.matt.accountabilityscrapbook;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;

import io.realm.Realm;
import io.realm.RealmResults;
import uk.mrshll.matt.accountabilityscrapbook.Adapter.ShareServiceListAdapter;
import uk.mrshll.matt.accountabilityscrapbook.AsyncTask.PostScrapParams;
import uk.mrshll.matt.accountabilityscrapbook.AsyncTask.PostScrapTask;
import uk.mrshll.matt.accountabilityscrapbook.Listener.FetchScrapbookDialogListener;
import uk.mrshll.matt.accountabilityscrapbook.model.ConnectedService;
import uk.mrshll.matt.accountabilityscrapbook.model.Scrap;
import uk.mrshll.matt.accountabilityscrapbook.model.Scrapbook;

public class ShareDataActivity extends AppCompatActivity implements RecyclerViewItemClickListener
{
    private RecyclerView recycler;
    private Realm realm;
    private ArrayList<String> selectedScrapbooks;
    private RealmResults<ConnectedService> results;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_data);

        // Set up
        this.realm = realm.getDefaultInstance();
        this.selectedScrapbooks = new ArrayList<>();

        Button scrapbooksButton = (Button) findViewById(R.id.share_data_scrapbooks_button);
        scrapbooksButton.setOnClickListener(new FetchScrapbookDialogListener(this, realm, selectedScrapbooks));

        results = realm.where(ConnectedService.class).findAll();

        recycler = (RecyclerView) findViewById(R.id.share_data_recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(new ShareServiceListAdapter(this, results, this));

    }

    @Override
    public void recyclerViewListClicked(View v, int position)
    {
        ConnectedService service = results.get(position);
        // TODO Make post requests to the service listed for each item (asynchronously), over https

        // Retrieve scrapbooks from list of selectedScrapbooks
        if(!selectedScrapbooks.isEmpty())
        {
            // Set up a HashSet to add scraps to, removing duplicates as we go
            HashSet<Scrap> scrapSet = new HashSet<>();

            // Iterate over the selected scrapbooks
            for (String selectedScrapbookName : selectedScrapbooks)
            {
                // Query for the scrapbook
                Scrapbook scrapbook = realm.where(Scrapbook.class).equalTo("name", selectedScrapbookName).findFirst();
                // Add all scraps to the hashset (which removes duplicates)
                scrapSet.addAll(scrapbook.getScrapList());
            }

            // Now scrapSet contains all unique scraps that belong to those scrapbooks.
                    // Create an Async task to handle sharing so the UI thread doesn't crash.

             PostScrapParams params = new PostScrapParams(this, service.getEndpointUrl(), scrapSet);
            new PostScrapTask().execute(params);

                    // Convert a scrap to JSON
                    // Make a HTTP Post Request to the service url
                    // Check if using api key
                    // If yes, add to post string
                    // Transform fields into JSON (pics as a Byte array)
                    // Check if fields are null, before adding them, otherwise set them as null
                    // Post (asynchronously)


        } else
        {
            Toast.makeText(this, "Please select some Scrapbooks", Toast.LENGTH_SHORT).show();

        }



    }
}
