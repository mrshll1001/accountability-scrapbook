package uk.mrshll.matt.accountabilityscrapbook;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;
import uk.mrshll.matt.accountabilityscrapbook.Listener.FetchScrapbookDialogListener;
import uk.mrshll.matt.accountabilityscrapbook.model.Scrapbook;
import uk.mrshll.matt.accountabilityscrapbook.model.SpendScrap;

public class AddSpendscrapActivity extends AppCompatActivity {

    private Realm realm;
    private ArrayList<Scrapbook> selectedScrapbooks;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_spendscrap);

        // Initialise realm
        this.realm = Realm.getDefaultInstance();
        this.selectedScrapbooks = new ArrayList<Scrapbook>();

        // Add the dialog box functionality
        Button addScrapbooks = (Button) findViewById(R.id.create_spendscrap_scrapbook_button);
        addScrapbooks.setOnClickListener(new FetchScrapbookDialogListener(this, this.realm, this.selectedScrapbooks));

        // Set the behaviour for the done button
        Button doneButton = (Button) findViewById(R.id.create_spendscrap_done);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                // Get the values
                EditText nameField = (EditText) findViewById(R.id.create_spendscrap_name);
                EditText valueField = (EditText) findViewById(R.id.create_spendscrap_value);



                // Perform the checks
                if (nameField.getText().toString().matches(""))
                {
                    Toast.makeText(AddSpendscrapActivity.this, "Please add a name", Toast.LENGTH_SHORT).show();

                } else if (valueField.getText().toString().matches(""))
                {
                    Toast.makeText(AddSpendscrapActivity.this, "Please add the spend amount", Toast.LENGTH_SHORT).show();
                } else if (selectedScrapbooks.isEmpty())
                {
                    Toast.makeText(AddSpendscrapActivity.this, "Please select some scrapbooks", Toast.LENGTH_SHORT).show();
                } else
                {
                    // All checks have passed -- get the proper values and get realming
//                   final String name = nameField.getText().toString();
//                   final double value = Double.valueOf(valueField.getText().toString());
                    realm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm)
                        {
                            SpendScrap scrap = realm.createObject(SpendScrap.class);
                            scrap.setName("Test");
                            scrap.setValue(11.99);

                            for (Scrapbook s : selectedScrapbooks)
                            {
                                s.spendList.add(scrap);
                            }



                        }
                    }, new Realm.Transaction.OnSuccess()
                    {

                        @Override
                        public void onSuccess() {
                            finish();
                        }
                    });


                }

            }
        });
    }
}
