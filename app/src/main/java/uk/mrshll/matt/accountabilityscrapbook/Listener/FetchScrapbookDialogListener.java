package uk.mrshll.matt.accountabilityscrapbook.Listener;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
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
    private ArrayList<String> selectedScrapbooks; // THIS IS A REFERENCE PASSED FROM THE ACTIVITY ALLOWING US TO UPDATE IT
    private ArrayList<Integer> checkedIndexes;
    private Realm realm; // Realm for actually getting the scrapbooks

    private AlertDialog dialog;


    public FetchScrapbookDialogListener(Context context, Realm realm, ArrayList<String> selectedScrapbooks)
    {
        this.context = context;
        this.selectedScrapbooks = selectedScrapbooks;
        this.checkedIndexes = new ArrayList<>();
        this.realm = realm;
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
        final HashMap<Integer, String> scrapbookIntegerMap = new HashMap<Integer, String>();
        for (Scrapbook s : results)
        {
            // Presume that the results array is in the same order as the array
            scrapbookIntegerMap.put(results.indexOf(s), s.getName());
        }

        // Create the array of checked values
        boolean[] checked = new boolean[results.size()];
        for(Integer i : checkedIndexes)
        {
            checked[i] = true;
        }


        // Now a dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
        builder.setTitle("Choose Scrapbooks");
        builder.setMultiChoiceItems(scrapbookItems, checked, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean isChecked)
            {
                // i is the index selected by the click listener
                if(isChecked)
                {
                    // Add to the list of selected items
                    selectedIndexes.add(i);

                } else
                {
                    selectedIndexes.remove(Integer.valueOf(i));
                    selectedScrapbooks.remove(scrapbookIntegerMap.get(i));
                    checkedIndexes.remove(Integer.valueOf(i));

                }
            }
        }).setPositiveButton("Ok", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                // Create a new arraylist of strings, and then pass it back to the reference to replace the list each time.
                // Add all of the things to the selected scrapbooks via lookup
                for(Integer index : selectedIndexes)
                {
                   selectedScrapbooks.add(scrapbookIntegerMap.get(index));
                    checkedIndexes.add(index);
                }
                // Replace the selected scrapbook list with the new list


//

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
