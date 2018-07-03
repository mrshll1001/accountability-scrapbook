package uk.mrshll.matt.accountabilityscrapbook;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import io.realm.Realm;
import uk.mrshll.matt.accountabilityscrapbook.Listener.AddScrapListener;
import uk.mrshll.matt.accountabilityscrapbook.Listener.FetchScrapbookDialogListener;
import uk.mrshll.matt.accountabilityscrapbook.Utility.ScrapCreator;
import uk.mrshll.matt.accountabilityscrapbook.Utility.TagManager;
import uk.mrshll.matt.accountabilityscrapbook.model.Scrap;
import uk.mrshll.matt.accountabilityscrapbook.model.Scrapbook;
import uk.mrshll.matt.accountabilityscrapbook.model.Tag;

public class AddQuotescrapActivity extends AppCompatActivity {

    private Realm realm;
    private ArrayList<String> selectedScrapbooks;
    private HashSet<String> tags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_quotescrap);

        // Initialise
        this.realm = Realm.getDefaultInstance();
        this.selectedScrapbooks = new ArrayList<String>();

        /* Set up the AutoComplete box for the tags */
        setUpTagAutoComplete();
        setUpAddTagButton();

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

                // Perform checks
                if (quoteText.getText().toString().matches(""))
                {
                    Toast.makeText(AddQuotescrapActivity.this, "Please enter a quote", Toast.LENGTH_SHORT).show();
                } else if (quoteSource.getText().toString().matches(""))
                {
                    Toast.makeText(AddQuotescrapActivity.this, "Please add a source to the quote", Toast.LENGTH_SHORT).show();
                } else if (selectedScrapbooks.isEmpty())
                {
                    Toast.makeText(AddQuotescrapActivity.this, "Please add some scrapbooks", Toast.LENGTH_SHORT).show();
                } else
                {
                    // Checks have passed. Get the dates
                    DatePicker datePicker = (DatePicker) findViewById(R.id.create_scrap_date_picker);
                    final Date dateGiven = new Date(datePicker.getYear() - 1900, datePicker.getMonth(), datePicker.getDayOfMonth());

                    ScrapCreator sc = new ScrapCreator(realm, new AddScrapListener()
                    {
                        @Override
                        public void realmSuccess() {
                            Intent returnIntent = new Intent();
                            setResult(Activity.RESULT_OK, returnIntent);
                            finish();
                        }

                        @Override
                        public void realmError(Throwable error) {

                        }
                    });

                    sc.createQuoteScrap(dateGiven, quoteText.getText().toString(), quoteSource.getText().toString(), tags.toArray(new String[tags.size()]), selectedScrapbooks.toArray(new String[selectedScrapbooks.size()]));


                }

            }
        });
    }

    private void setUpTagAutoComplete()
    {
        final AutoCompleteTextView tagField = (AutoCompleteTextView) findViewById(R.id.autocomplete_tags);
        this.tags = new HashSet<>();

        String[] tagArray = new TagManager(realm).getTagsAsStringArray();
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, tagArray);
        tagField.setAdapter(adapter);

        tagField.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String token = tagField.getText().toString();
                tags.add(token);
                tagField.setText(null);
                updateCustomTagField();



            }
        });
    }

    private void setUpAddTagButton()
    {
        final AutoCompleteTextView tagField = (AutoCompleteTextView) findViewById(R.id.autocomplete_tags);
        final Button addTagButton = (Button) findViewById(R.id.addTagFieldButton);
        addTagButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                String[] tokens = tagField.getText().toString().split(",");

                for (String token : tokens)
                {
                    tags.add(token.trim());
                }

                tagField.setText(null);
                updateCustomTagField();
            }
        });
    }

    private void updateCustomTagField()
    {
        TextView tagField = (TextView) findViewById(R.id.currentTagsField);
        StringBuilder sb = new StringBuilder();

        for (String s : tags)
        {
            sb.append(s);
            sb.append(" ");
        }

        tagField.setText(sb.toString());
    }
}
