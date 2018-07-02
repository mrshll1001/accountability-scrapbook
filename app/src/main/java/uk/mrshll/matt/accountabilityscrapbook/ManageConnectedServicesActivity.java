package uk.mrshll.matt.accountabilityscrapbook;

import android.*;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;
import io.realm.RealmResults;
import uk.mrshll.matt.accountabilityscrapbook.Adapter.ConnectedServiceAdapter;
import uk.mrshll.matt.accountabilityscrapbook.model.ConnectedService;

public class ManageConnectedServicesActivity extends AppCompatActivity
{

    Realm realm;
    RecyclerView recycler;

    // Request code for the QR request
    public static final int REQUEST_QR_SCAN = 8888;

    public static final int REQUEST_PERMISSION_FOR_SCAN_QR = 5555;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_connected_services);

        // Set up Realm
        this.realm = Realm.getDefaultInstance();

        // Set up the click of the Scan QR button
        Button scanQRButton = (Button) findViewById(R.id.manage_connections_add_by_qr_button);
        scanQRButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View view)
            {

//                    // Check we have permission
                    if (Build.VERSION.SDK_INT >= 23)
                    {
                        // // Request permission for the file system here, to ensure we can read the photo url later
                        if (ContextCompat.checkSelfPermission(ManageConnectedServicesActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                        {
                            ActivityCompat.requestPermissions(ManageConnectedServicesActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION_FOR_SCAN_QR);
                        } else
                        {
                            startBarcodeScanner();
                        }

                    } else
                    {
                        startBarcodeScanner();
                    }

            }
        });

        // Set up the on click of the add manually Button
        Button add = (Button) findViewById(R.id.manage_connections_add_button);
        add.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                // Make a builder
                AlertDialog.Builder builder = new AlertDialog.Builder(ManageConnectedServicesActivity.this);
                builder.setTitle("Add Service");

                // Inflate layout, retrieve views, and set
                final View dialogLayout = getLayoutInflater().inflate(R.layout.component_sharing_service_dialogue, null);
                // Retrieve the values
                final EditText url = (EditText) dialogLayout.findViewById(R.id.add_sharing_service_url);
                final EditText apiKey = (EditText) dialogLayout.findViewById(R.id.add_sharing_service_api_key);

                // Do the thing to set the text entry point after http
                url.setSelection(url.getText().toString().length());

                builder.setView(dialogLayout);

               // Set up the buttons on the dialog
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        // URL cannot be null, but API Key is good.
                        if (url.getText().toString().matches(""))
                        {
                            Toast.makeText(ManageConnectedServicesActivity.this, "Please enter a url", Toast.LENGTH_SHORT).show();
                        } else if (url.getText().toString().matches("http://"))
                        {
                            Toast.makeText(ManageConnectedServicesActivity.this, "Please enter a url", Toast.LENGTH_SHORT).show();

                        } else
                        {
                            addConnectedService(url.getText().toString(), apiKey.getText().toString());
                        }

                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                builder.show();
            }
        });

        // Set up the recycler view
        recycler = (RecyclerView) findViewById(R.id.manage_connections_recycler);
        populateRecyclerView();

    }

    // Fires when the user clicks the dialog box that requests permission
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        switch (requestCode)
        {
            case REQUEST_PERMISSION_FOR_SCAN_QR:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(this, "PErmission Granted", Toast.LENGTH_SHORT).show();
                    startBarcodeScanner();
                }
                return;

        }
    }

    /**
     * Populates the recycler view
     */
    private void populateRecyclerView()
    {
        final RealmResults<ConnectedService> results = realm.where(ConnectedService.class).findAll();
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(new ConnectedServiceAdapter(this, results));


        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT)
        {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                realm.beginTransaction();
                ConnectedService cs = results.get(viewHolder.getAdapterPosition());
                cs.deleteFromRealm();
                realm.commitTransaction();
                populateRecyclerView();
            }
        };

        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(recycler);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        if (result != null)
        {
            if (result.getContents() == null)
            {
                Toast.makeText(this, "Cancelled Scan", Toast.LENGTH_SHORT).show();
            } else
            {
                // Presume contents is json encoded value
                JSONObject json = null;
                try {
                    json = new JSONObject(result.getContents());
                    String url = json.getString("url");
                    String apiKey = json.getString("token");

                    // THere's some weird bugs with Rosemary on QR code generation, so check for a "https://" and if it's not found then replace "https:/" with it
                    if (!url.contains("https://"))
                    {
                        url = url.replaceAll("https:/", "https://");
                    }

                    // Add it to realm
                    addConnectedService(url, apiKey);

                    // Repopulate the recycler view just in case
                    populateRecyclerView();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }

    }


    /**
     * Uses embedded barcode scanner activity to scan a qr code
     */
    private void startBarcodeScanner()
    {
         IntentIntegrator integrator =  new IntentIntegrator(this);
         integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
         integrator.setBeepEnabled(false);

         integrator.initiateScan();

    }



    /**
     * Runs the realm operation to add the connected service item
     * @param urlString
     */
    private void addConnectedService(final String urlString, final String apiKey)
    {
        realm.executeTransactionAsync(new Realm.Transaction()
        {

            @Override
            public void execute(Realm realm) {
                ConnectedService cs = realm.createObject(ConnectedService.class, urlString);
                cs.setApiKey(apiKey);

            }
        }, new Realm.Transaction.OnSuccess()
        {
            @Override
            public void onSuccess() {
                populateRecyclerView();
            }
        }, new Realm.Transaction.OnError()
        {
            @Override
            public void onError(Throwable error) {
                Toast.makeText(ManageConnectedServicesActivity.this, "There was an error adding this URL", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
