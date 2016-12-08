package uk.mrshll.matt.accountabilityscrapbook;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import uk.mrshll.matt.accountabilityscrapbook.Adapter.ShareServiceListAdapter;
import uk.mrshll.matt.accountabilityscrapbook.Listener.FetchScrapbookDialogListener;
import uk.mrshll.matt.accountabilityscrapbook.model.ConnectedService;
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

        // Query for scrapbooks
        // Get all scraps
        // Merge the fuckers
        // Turn merged list into a hashset to remove duplicates

        // For each item in set
        // Check if using api key
        // If yes, add to post string
        // Transform fields into JSON (pics as a Byte array)
        // Check if fields are null, before adding them, otherwise set them as null
        // Post (asynchronously)

    }
}
