package uk.mrshll.matt.accountabilityscrapbook;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;
import uk.mrshll.matt.accountabilityscrapbook.Listener.FetchScrapbookDialogListener;
import uk.mrshll.matt.accountabilityscrapbook.model.Scrap;
import uk.mrshll.matt.accountabilityscrapbook.model.Scrapbook;
import uk.mrshll.matt.accountabilityscrapbook.model.Tag;

public class AddQuotescrapActivity extends AppCompatActivity {

    private Realm realm;
    private ArrayList<String> selectedScrapbooks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_quotescrap);

        // Initialise
        this.realm = Realm.getDefaultInstance();
        this.selectedScrapbooks = new ArrayList<String>();

        // Set activity for the select scrapbooks button
        Button scrapbooksButton = (Button) findViewById(R.id.create_scrap_scrapbook_button);
        scrapbooksButton.setOnClickListener(new FetchScrapbookDialogListener(this, this.realm, this.selectedScrapbooks));

        // Set activity for the done button
        Button doneBtn = (Button) findViewById(R.id.create_scrap_done);
        doneBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Get values to check
                final EditText quoteText = (EditText) findViewById(R.id.create_quotescrap_quote);
                final EditText quoteSource = (EditText) findViewById(R.id.create_quotescrap_source);
                final EditText tags = (EditText) findViewById(R.id.create_scrap_tags);

                // Perform checks
                if (quoteText.getText().toString().matches(""))
                {
                    Toast.makeText(AddQuotescrapActivity.this, "Please enter a quote", Toast.LENGTH_SHORT).show();
                } else if (quoteSource.getText().toString().matches(""))
                {
                    Toast.makeText(AddQuotescrapActivity.this, "Please add a source to the quote", Toast.LENGTH_SHORT).show();
                } else if (tags.getText().toString().matches(""))
                {
                    Toast.makeText(AddQuotescrapActivity.this, "Please add some tags", Toast.LENGTH_SHORT).show();
                } else if (selectedScrapbooks.isEmpty())
                {
                    Toast.makeText(AddQuotescrapActivity.this, "Please add some scrapbooks", Toast.LENGTH_SHORT).show();
                } else
                {
                    // Checks have passed. Get the dates
                    DatePicker datePicker = (DatePicker) findViewById(R.id.create_scrap_date_picker);
                    final Date dateCreated = new Date();
                    final Date dateGiven = new Date(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());

                    // And now we realm
                    realm.executeTransactionAsync(new Realm.Transaction()
                    {
                        @Override
                        public void execute(Realm realm)
                        {
                            // Create the Scrap
                            Scrap scrap = realm.createObject(Scrap.class);
                            scrap.setDateGiven(dateGiven);
                            scrap.setDateCreated(dateCreated);
                            scrap.setQuoteText(quoteText.getText().toString());
                            scrap.setQuoteSource(quoteSource.getText().toString());

                            // Add the tags
                            // Add the tags
                            String[] tokens = tags.getText().toString().split(" ");
                            for (String t : tokens)
                            {

                                Tag tag = realm.where(Tag.class).equalTo("tagName", "#"+t).findFirst();

                                if (tag == null)
                                {
                                    Log.d("Add Quote:", "Found a null tag, attempting to add");
                                    // Create if not null
                                    tag = realm.createObject(Tag.class, "#"+t);
                                }

                                scrap.getCustomTags().add(tag);
                            }

                            // Add the scrap to the scrapbook
                            for (String s : selectedScrapbooks)
                            {
                                Scrapbook result = realm.where(Scrapbook.class).equalTo("name", s).findFirst();

                                // Inherit the tags from the scrapbooks
                                scrap.getInheritedTags().addAll(result.getTagList());

                                result.getScrapList().add(scrap);

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
                    }, new Realm.Transaction.OnError()
                    {
                        @Override
                        public void onError(Throwable error) {
                            error.printStackTrace();
                            Toast.makeText(AddQuotescrapActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    });

                }

            }
        });
    }
}
