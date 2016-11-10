package uk.mrshll.matt.accountabilityscrapbook.Listener;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmResults;
import uk.mrshll.matt.accountabilityscrapbook.AddSpendscrapActivity;
import uk.mrshll.matt.accountabilityscrapbook.model.Scrapbook;

/**
 * Created by marshall on 10/11/16.
 */

public class FetchScrapbookDialogListener implements View.OnClickListener
{

    private Context context;

    private Realm realm; // Realm for actually getting the scrapbooks

    private AlertDialog dialog;


    public FetchScrapbookDialogListener(Context context)
    {
        this.context = context;

        // Set up Realm
        this.realm = Realm.getDefaultInstance();
    }

    @Override
    public void onClick(View view)
    {

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
        AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
        final Context theContext = this.context;
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
                Toast.makeText(theContext, toast, Toast.LENGTH_SHORT).show();
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
}
