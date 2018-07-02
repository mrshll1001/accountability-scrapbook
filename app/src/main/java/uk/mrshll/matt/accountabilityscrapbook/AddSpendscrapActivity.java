package uk.mrshll.matt.accountabilityscrapbook;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import uk.mrshll.matt.accountabilityscrapbook.Listener.FetchScrapbookDialogListener;
import uk.mrshll.matt.accountabilityscrapbook.model.Scrap;
import uk.mrshll.matt.accountabilityscrapbook.model.Scrapbook;
import uk.mrshll.matt.accountabilityscrapbook.model.Tag;

public class AddSpendscrapActivity extends AppCompatActivity {

    private Realm realm;
    private ArrayList<String> selectedScrapbooks;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_spendscrap);

        // Initialise realm
        this.realm = Realm.getDefaultInstance();
        this.selectedScrapbooks = new ArrayList<String>();

        // Add the dialog box functionality
        Button addScrapbooks = (Button) findViewById(R.id.create_scrap_scrapbook_button);
        addScrapbooks.setOnClickListener(new FetchScrapbookDialogListener(this, this.realm, this.selectedScrapbooks));

        /* Set up the AutoComplete box for the tags */
        AutoCompleteTextView tagField = (AutoCompleteTextView) findViewById(R.id.autocomplete_tags);

        RealmResults<Tag> results = this.realm.where(Tag.class).findAll(); // Get all tags from Realm and populate an array
        ArrayList<String> tagList = new ArrayList<>();
        for (Tag t : results)
        {
            tagList.add(t.getTagName());
            Log.d("Added Tag", t.getTagName());
        }
        String[] tagArray = tagList.toArray(new String[tagList.size()]);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, tagArray);
        tagField.setAdapter(adapter);


        // Set the behaviour for the done button
        Button doneButton = (Button) findViewById(R.id.create_scrap_done);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                // Get the values
                final EditText nameField = (EditText) findViewById(R.id.create_spendscrap_name);
                final EditText valueField = (EditText) findViewById(R.id.create_spendscrap_value);

                DatePicker dateField = (DatePicker) findViewById(R.id.create_scrap_date_picker);


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

                    // All checks have passed -- now get the date fields
                    final Date dateCreated = new Date();
                    final Date dateOfSpend = new Date(dateField.getYear() - 1900, dateField.getMonth(), dateField.getDayOfMonth());


                    // Finally, execute the realm transaction
                    realm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm)
                        {

                            // Create the spendscrap
                            Scrap scrap = realm.createObject(Scrap.class);
                            scrap.setName(nameField.getText().toString());
                            scrap.setSpendValue(0.00 - Double.valueOf(valueField.getText().toString()));
                            scrap.setDateCreated(dateCreated);
                            scrap.setDateGiven(dateOfSpend);
                            scrap.setType(Scrap.TYPE_SPEND);
                            scrap.setAttachedScrapbooks(0);

                            // Add the tags
//                            String[] tokens = tags.getText().toString().split(" ");
                            String [] tokens = new String[10];
                            for (String t : tokens)
                            {

                                Tag tag = realm.where(Tag.class).equalTo("tagName", t).findFirst();

                                if (tag == null)
                                {
                                    Log.d("Add Spend:", "Found a null tag, attempting to add");
                                    // Create if not null
                                    tag = realm.createObject(Tag.class, t);
                                }

                                scrap.getCustomTags().add(tag);
                            }

                            // Add the scrap to the scrapbooks
                            for (String s : selectedScrapbooks)
                            {
                                Scrapbook result = realm.where(Scrapbook.class).equalTo("name", s).findFirst();

                                // Inherit the tags from the scrapbooks
                                scrap.getInheritedTags().addAll(result.getTagList());

                                result.getScrapList().add(scrap);
                                scrap.setAttachedScrapbooks(scrap.getAttachedScrapbooks() + 1); // Increment by 1

                            }


                        }
                    }, new Realm.Transaction.OnSuccess()
                    {

                        @Override
                        public void onSuccess() {

                            Intent returnIntent = new Intent();
                            setResult(Activity.RESULT_OK, returnIntent);
                            finish();

                        }
                    }, new Realm.Transaction.OnError() {
                        @Override
                        public void onError(Throwable error) {
                            Toast.makeText(AddSpendscrapActivity.this, "Error adding Spend", Toast.LENGTH_SHORT).show();
                            error.printStackTrace();
                        }
                    });


                }

            }
        });
    }
}
