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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

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

    private HashMap<String, Scrap> jsonToScrapMap;
    private HashMap<String, ConnectedService> jsonToServiceMap;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_data);

        // Set up
        this.realm = realm.getDefaultInstance();
        this.selectedScrapbooks = new ArrayList<>();

        // Set up both maps
        this.jsonToScrapMap = new HashMap<>();
        this.jsonToServiceMap = new HashMap<>();

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

            // Add all to the HashSet, to remove all duplicates
            HashSet<Scrap> scrapHashSet = new HashSet<>();
            for(Scrapbook s : results)
            {
                scrapHashSet.addAll(s.getScrapList());
            }

            Iterator<Scrap> itr = scrapHashSet.iterator();
            while (itr.hasNext())
            {
                Scrap s = itr.next();
                if (service.getScrapLog().contains(s))
                {
                    itr.remove();
                }
            }


            // Strings for the JSON conversion and the QA data standard
            String deviceID = PreferenceManager.getDefaultSharedPreferences(this).getString("device-id", "n/a");
            String format = getResources().getString(R.string.qualitative_accounting_id_format);

//            Iterate over the set of Scraps, convert to JSON, post, and post any images.
            for (Scrap s : scrapHashSet)
            {
                // Check we don't send duplicates
                if (!service.getScrapLog().contains(s))
                {
                    // Convert the Scrap to JSON
                    String id = String.format(format, deviceID, s.getDateCreatedAsTransactionID());
                    QualitativeAccountingHandler qa = new QualitativeAccountingHandler(service.getQAMediaEndpoint(), service.getApiKey());
                    String jsonScrap = qa.scrapToJSON(s);

                    // Create entries in the map for use logging use later
                    jsonToScrapMap.put(jsonScrap, s);
                    jsonToServiceMap.put(jsonScrap, service);


                    // Fire off a task to send this to the web
                    new PostJSONToWebTask(service.getQADataEndpoint(), service.getApiKey(), this, jsonScrap).execute(jsonScrap);


                    // Check to see if we do an image posting, if so -- post it.
                    if (s.getType() == Scrap.TYPE_PHOTO)
                    {
                        new PostImageToWebTask(service.getEndpointUrl()).execute(s.getPhotoUri());
                    }
                }

            }




        } else
        {
            Toast.makeText(this, "Please select some Scrapbooks", Toast.LENGTH_SHORT).show();

        }



    }

    /**
     * This executes after EACH scrap is shared
     * @param result
     */
    public void processFinish(String result)
    {
        // Get the Scrap and the Connected service from the respective maps via the json
        realm.beginTransaction();

        Scrap scrap = jsonToScrapMap.get(result);
        ConnectedService service = jsonToServiceMap.get(result);

        // Associate them
        service.addScraptoScrapLog(scrap);

        realm.commitTransaction();



        Toast.makeText(this, "Shared Item to Web", Toast.LENGTH_SHORT).show();
    }
}
