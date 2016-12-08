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
import java.util.HashSet;
import java.util.LinkedList;

import io.realm.Realm;
import io.realm.RealmList;
import uk.mrshll.matt.accountabilityscrapbook.Listener.FetchScrapbookDialogListener;
import uk.mrshll.matt.accountabilityscrapbook.model.Scrap;
import uk.mrshll.matt.accountabilityscrapbook.model.Scrapbook;

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
            public void onClick(View view)
            {

                if (!selectedScrapbooks.isEmpty())
                {
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
                    File budgetFile = new File(folder.getPath() + "/budget_" + System.currentTimeMillis() + ".csv");

                    try {

                        writeCSVFile(budgetFile);

                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(EmailAccountsActivity.this, "Error creating budget file", Toast.LENGTH_SHORT).show();
                    }



                    Toast.makeText(EmailAccountsActivity.this, String.format("Saved to %s", budgetFile.getAbsolutePath()), Toast.LENGTH_SHORT).show();
                } else
                {
                    Toast.makeText(EmailAccountsActivity.this, "Please select some scrapbooks", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    /**
     * Handles the write operations for the file
     * @param budgetFile
     * @throws IOException
     *
     */
    private void writeCSVFile(File budgetFile) throws IOException
    {
        // Initialise writer and begin adding lines
        CSVWriter writer = new CSVWriter(new FileWriter(budgetFile), ',', CSVWriter.NO_QUOTE_CHARACTER);
        LinkedList<String[]> lines = new LinkedList<>();
        lines.add(new String[]{"name", "value", "date"});

        // Compile a list of all the spendy-type scraps
        ArrayList<Scrap> scrapList = new ArrayList<>();
        for (String selectedScrapbookName : selectedScrapbooks)
        {
            // Get each scrapbook by querying for the name
            Scrapbook scrapbook = realm.where(Scrapbook.class).equalTo("name", selectedScrapbookName).findFirst();

            // Get the list of spends and add all to the collection
            RealmList<Scrap> spends = scrapbook.getScrapListByType(Scrap.TYPE_SPEND);
            scrapList.addAll(spends);
        }

        // Convert the ArrayList (which may contain duplicates) to a HashSet to remove duplicates added from each scrapbook
        HashSet<Scrap> scrapSet = Scrapbook.listsToSet(scrapList);

        // Write everything in the set to the writer
        for (Scrap s : scrapSet)
        {
            String[] spendLine = {s.getName(), String.valueOf(s.getSpendValue()), s.getFormattedDateString(s.getDateGiven())};
            lines.add(spendLine);
        }

        // Finally, write all lines out to a CSV file and close
        writer.writeAll(lines);
        writer.close();

    }




}
