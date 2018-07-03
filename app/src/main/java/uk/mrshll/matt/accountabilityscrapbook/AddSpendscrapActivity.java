package uk.mrshll.matt.accountabilityscrapbook;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
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

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import uk.mrshll.matt.accountabilityscrapbook.Listener.AddScrapListener;
import uk.mrshll.matt.accountabilityscrapbook.Listener.FetchScrapbookDialogListener;
import uk.mrshll.matt.accountabilityscrapbook.Utility.ScrapCreator;
import uk.mrshll.matt.accountabilityscrapbook.Utility.TagManager;
import uk.mrshll.matt.accountabilityscrapbook.model.Scrap;
import uk.mrshll.matt.accountabilityscrapbook.model.Scrapbook;
import uk.mrshll.matt.accountabilityscrapbook.model.Tag;

public class AddSpendscrapActivity extends AppCompatActivity
{

    private Realm realm;
    private ArrayList<String> selectedScrapbooks;
    private HashSet<String> tags;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_spendscrap);

        // Initialise realm
        this.realm = Realm.getDefaultInstance();
        this.selectedScrapbooks = new ArrayList<>();

        // Add the dialog box functionality
        Button addScrapbooks = (Button) findViewById(R.id.create_scrap_scrapbook_button);
        addScrapbooks.setOnClickListener(new FetchScrapbookDialogListener(this, this.realm, this.selectedScrapbooks));

        /* Set up the AutoComplete box for the tags */
        setUpTagAutoComplete();
        setUpAddTagButton();



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
                    final Date dateOfSpend = new Date(dateField.getYear() - 1900, dateField.getMonth(), dateField.getDayOfMonth());

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

                    sc.createSpendScrap(nameField.getText().toString(),
                            Double.valueOf(valueField.getText().toString()),
                            dateOfSpend,
                            tags.toArray(new String[tags.size()]),
                            selectedScrapbooks.toArray(new String[selectedScrapbooks.size()])
                    );




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
