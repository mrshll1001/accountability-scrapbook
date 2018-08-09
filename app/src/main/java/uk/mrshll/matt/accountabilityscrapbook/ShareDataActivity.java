package uk.mrshll.matt.accountabilityscrapbook;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

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
    private int remainingToShareCount = 0;

    private HashMap<String, Scrap> jsonToScrapMap;
    private HashMap<String, ConnectedService> jsonToServiceMap;
    private String ITEMS_REMAINING_TEXT = " items to share";

    int count = 0;

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
//            RealmQuery<Scrapbook> query = realm.where(Scrapbook.class);
            LinkedList<Scrapbook> scrapbookList = new LinkedList<>();
            for(String s : selectedScrapbooks)
            {
                Log.d("Querying for scrapbook:", s);
                scrapbookList.add(realm.where(Scrapbook.class)
                        .equalTo("name", s)
                        .findFirst());
            }

//            RealmResults<Scrapbook> results = query.findAll();
            Log.d("total scrapbooks", ""+scrapbookList.size());



            // Add all to the HashSet, to remove all duplicates
            HashSet<Scrap> scrapHashSet = new HashSet<>();

            for (Scrapbook scrapbook : scrapbookList)
            {
                for (Scrap scrap : scrapbook.getScrapList())
                {
                    if (!service.getScrapLog().contains(scrap))
                    {
                        scrapHashSet.add(scrap);
                    }
                }
            }

            remainingToShareCount = scrapHashSet.size();
            TextView shareCountTextView = (TextView) findViewById(R.id.share_data_scrap_count);
            shareCountTextView.setText(remainingToShareCount + " items to share");

//            for(Scrapbook s : results)
//            {
//                scrapHashSet.addAll(s.getScrapList());
//            }
//
//            Iterator<Scrap> itr = scrapHashSet.iterator();
//            while (itr.hasNext())
//            {
//                Scrap s = itr.next();
//                if (service.getScrapLog().contains(s))
//                {
//                    itr.remove();
//                }
//            }

            Toast.makeText(this, "Sharing " + scrapHashSet.size() + "items:", Toast.LENGTH_SHORT).show();


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
                    QualitativeAccountingHandler qa = new QualitativeAccountingHandler(this, service.getQAMediaEndpoint(), service.getApiKey());
                    String jsonScrap = qa.scrapToJSON(s);

                    // Create entries in the map for use logging use later
                    jsonToScrapMap.put(jsonScrap, s);
                    jsonToServiceMap.put(jsonScrap, service);


                    Log.d("JSON VALUE @ ShareData", jsonScrap);

//                    // Fire off a task to send this to the web
                    new PostJSONToWebTask(service.getQADataEndpoint(), service.getApiKey(), this, jsonScrap).execute(jsonScrap);
//
//
//                    // Check to see if we do an image posting, if so -- post it.
                    if (s.getType() == Scrap.TYPE_PHOTO)
                    {
                        Log.d("Posting images", "Image scrap. Posting images");
                        for (String uri : s.getImageList())
                        {
                            Log.d("Posting image", "Image: " + uri);
                            new PostImageToWebTask(this, service.getEndpointUrl()).execute(uri);

                        }
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
        this.count++;
        this.remainingToShareCount--;
        // Get the Scrap and the Connected service from the respective maps via the json
        realm.beginTransaction();

        Scrap scrap = jsonToScrapMap.get(result);
        ConnectedService service = jsonToServiceMap.get(result);

        // Associate them
        service.addScraptoScrapLog(scrap);

        realm.commitTransaction();


        TextView itemsRemaining = (TextView) findViewById(R.id.share_data_scrap_count);
        if (remainingToShareCount > 0)
        {
            itemsRemaining.setText(remainingToShareCount + this.ITEMS_REMAINING_TEXT);

        } else
        {
            itemsRemaining.setText("All Items have been shared, have a nice day");
        }
        Toast.makeText(this, "Shared Item " + count + " to Web", Toast.LENGTH_SHORT).show();
    }
}
