package uk.mrshll.matt.accountabilityscrapbook;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import io.realm.Realm;
import uk.mrshll.matt.accountabilityscrapbook.Listener.FetchScrapbookDialogListener;

public class EmailAccountsActivity extends AppCompatActivity
{

    private Realm realm;
    private ArrayList<String> selectedScrapbooks;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_accounts);

        // Set up realm
        realm = Realm.getDefaultInstance();
        selectedScrapbooks = new ArrayList<>();

        // Get the choose scrapbooks button and apply the click listener to store scrapbooks
        Button scrapbooks = (Button) findViewById(R.id.generate_accounts_scrapbook_button);
        scrapbooks.setOnClickListener(new FetchScrapbookDialogListener(this, realm, selectedScrapbooks));

        // Generate button
        Button generate = (Button) findViewById(R.id.generate_accounts_generate_button);
        generate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {

                // Set up the folders and files
                File folder = new File(Environment.getExternalStorageDirectory() + "/contextual_accounts");

                // Attempt to create the folder if it doesn't exist
                if (!folder.exists())
                {
                    if(!folder.mkdir())
                    {
                        Toast.makeText(EmailAccountsActivity.this, "Error creating folder", Toast.LENGTH_SHORT).show();
                    }
                }

                // Create a new CSV file
                File budgetFile = new File(folder.getPath() + System.currentTimeMillis());
                try {

                    CSVWriter writer = new CSVWriter(new FileWriter(budgetFile));

                } catch (IOException e) {
                    e.printStackTrace();
                }

                // TODO get each scrapbook by querying for the name

                // TODO get the list of spends and iterate through them, writing each as a line in a CSV

                // TODO finish and inform the user, passing out the fileURI as a variable for sharing

                // TODO attach this to an intent for the sharing to an email or dropbox

                Toast.makeText(EmailAccountsActivity.this, null, Toast.LENGTH_SHORT).show();
            }
        });

    }




}
