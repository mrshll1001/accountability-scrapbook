package uk.mrshll.matt.accountabilityscrapbook;

import android.content.DialogInterface;
import android.content.ServiceConnection;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import io.realm.Realm;
import io.realm.RealmResults;
import uk.mrshll.matt.accountabilityscrapbook.Adapter.ConnectedServiceAdapter;
import uk.mrshll.matt.accountabilityscrapbook.model.ConnectedService;

public class ManageConnectedServicesActivity extends AppCompatActivity
{

    Realm realm;
    RecyclerView recycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_connected_services);

        // Set up Realm
        this.realm = Realm.getDefaultInstance();

        // Set up the on click of the Button
        Button add = (Button) findViewById(R.id.manage_connections_add_button);
        add.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ManageConnectedServicesActivity.this);
                builder.setTitle("Add Service");

                // Set up the input textfield
                final EditText input = new EditText(ManageConnectedServicesActivity.this);
                // Specify input type
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setText("http://");
                input.setSelection(input.getText().length());
                builder.setView(input);

                // Set up the buttons on the dialog
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Toast.makeText(ManageConnectedServicesActivity.this, input.getText().toString(), Toast.LENGTH_SHORT).show();
                        addConnectedService(input.getText().toString());
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

    /**
     * Runs the realm operation to add the connected service item
     * @param urlString
     */
    private void addConnectedService(final String urlString)
    {
        realm.executeTransactionAsync(new Realm.Transaction()
        {

            @Override
            public void execute(Realm realm) {
                ConnectedService cs = realm.createObject(ConnectedService.class, urlString);

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
