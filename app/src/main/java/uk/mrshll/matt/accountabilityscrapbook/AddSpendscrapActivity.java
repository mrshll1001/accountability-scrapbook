package uk.mrshll.matt.accountabilityscrapbook;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;
import uk.mrshll.matt.accountabilityscrapbook.model.Scrapbook;

public class AddSpendscrapActivity extends AppCompatActivity {

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_spendscrap);

        // Initialise realm
        this.realm = Realm.getDefaultInstance();

        // Get the button from the view
        Button addScrapbooks = (Button) findViewById(R.id.create_spendscrap_scrapbook_button);
        // Add the alert dialogue checkbox
        addScrapbooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                // First the items
                AlertDialog dialog;

                // Now get the scrapbooks
                RealmResults<Scrapbook> results = realm.where(Scrapbook.class).findAll();

                // Construct the CharSequence of scrapbooks
                ArrayList<String> scrapbookList = new ArrayList<String>();
                for (Scrapbook s : results)
                {
                    scrapbookList.add(s.getName());
                }
                final CharSequence[] scrapbookItems = scrapbookList.toArray(new CharSequence[scrapbookList.size()]);

                final ArrayList<Integer> selectedIndexes = new ArrayList<Integer>();

                // Set up a map of the index to the scrapbook objects
                final HashMap<Integer, Scrapbook> scrapbookIntegerMap = new HashMap<Integer, Scrapbook>();
                for (Scrapbook s : results)
                {
                    // Presume that the results array is in the same order as the array
                    scrapbookIntegerMap.put(results.indexOf(s), s);
                }

                // Now a dialog builder
                AlertDialog.Builder builder = new AlertDialog.Builder(AddSpendscrapActivity.this);
                builder.setTitle("Choose Scrapbooks");
                builder.setMultiChoiceItems(scrapbookItems, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean isChecked)
                    {
                        // i is the index selected by the click listener
                        if(isChecked)
                        {
                            // Add to the list of selected items
                            selectedIndexes.add(i);

                        } else if(selectedIndexes.contains(i))
                        {
                            selectedIndexes.remove(Integer.valueOf(i));
                        }
                    }
                }).setPositiveButton("Ok", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        String toast = "I think you chose:\n";
                        for(Integer index : selectedIndexes)
                        {
                            toast = toast + scrapbookIntegerMap.get(index).getName() + "\n";
                        }

//                      // CODE TO SAVE THE items here
                        Toast.makeText(AddSpendscrapActivity.this, toast, Toast.LENGTH_SHORT).show();
                    }

                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // CODE FOR WHEN THEY CANCEL
                    }
                });


                dialog = builder.create();
                dialog.show();
            }
        });

    }
}
