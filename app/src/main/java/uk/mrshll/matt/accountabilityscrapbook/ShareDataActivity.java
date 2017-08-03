package uk.mrshll.matt.accountabilityscrapbook;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import uk.mrshll.matt.accountabilityscrapbook.Adapter.ShareServiceListAdapter;
import uk.mrshll.matt.accountabilityscrapbook.AsyncTask.PostImageToWebTask;
import uk.mrshll.matt.accountabilityscrapbook.AsyncTask.PostJSONToWebTask;
import uk.mrshll.matt.accountabilityscrapbook.Listener.AsyncResponse;
import uk.mrshll.matt.accountabilityscrapbook.Listener.FetchScrapbookDialogListener;
import uk.mrshll.matt.accountabilityscrapbook.model.ConnectedService;
import uk.mrshll.matt.accountabilityscrapbook.model.Scrap;
import uk.mrshll.matt.accountabilityscrapbook.model.Scrapbook;

public class ShareDataActivity extends AppCompatActivity implements RecyclerViewItemClickListener, AsyncResponse
{
    private RecyclerView recycler;
    private Realm realm;
    private ArrayList<String> selectedScrapbooks;
    private RealmResults<ConnectedService> results;
    private ArrayList<String> jsonData;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_data);

        // Set up
        this.realm = realm.getDefaultInstance();
        this.selectedScrapbooks = new ArrayList<>();
        this.jsonData = new ArrayList<String>();

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
        // Retrieve scrapbooks from list of selectedScrapbooks
        if(!selectedScrapbooks.isEmpty())
        {
            // Query for the scrapbooks
            RealmQuery<Scrapbook> query = realm.where(Scrapbook.class);

            for(String s : selectedScrapbooks)
            {
                query.equalTo("name", s);
            }

            RealmResults<Scrapbook> results = query.findAll();

            // Add all to the HashSet
            HashSet<Scrap> scrapHashSet = new HashSet<>();
            for(Scrapbook s : results)
            {
                scrapHashSet.addAll(s.getScrapList());
            }

            // Convert to JSON (should be relatively quick)
            ArrayList<String> jsonData = new ArrayList<>();
            String deviceID = PreferenceManager.getDefaultSharedPreferences(this).getString("device-id", "n/a");
            String format = getResources().getString(R.string.qualitative_accounting_id_format);

            for (Scrap s : scrapHashSet)
            {
                String id = String.format(format, deviceID, s.getDateCreatedAsTransactionID());
                QualitativeAccountingHandler qa = new QualitativeAccountingHandler(service.getApiKey());
                jsonData.add(qa.scrapToJSON(s));

            }

            // Now fire off a bunch of tasks to send to the web
            for (String s : jsonData)
            {
//                Toast.makeText(this, "Posting " + s + " to " + service.getQADataEndpoint(), Toast.LENGTH_SHORT).show();
                new PostJSONToWebTask(service.getQADataEndpoint(), service.getApiKey(), this).execute(s);
            }

            // Same here for the images
            for (Scrap s: scrapHashSet)
            {
                if (s.getType() == Scrap.TYPE_PHOTO)
                {
                    try {
                        Bitmap image = BitmapFactory.decodeStream(getContentResolver().openInputStream(Uri.parse(s.getPhotoUri())));
//                        TODO FIX THIS TO ALLOW POSTING IMG FILES TO API ENDPOINT
//                        new PostImageToWebTask(service.getEndpointUrl()).execute(image);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
            }




        } else
        {
            Toast.makeText(this, "Please select some Scrapbooks", Toast.LENGTH_SHORT).show();

        }



    }

    public void processFinish(Boolean result) {
        Toast.makeText(this, "Async has finished", Toast.LENGTH_SHORT).show();
    }
}
