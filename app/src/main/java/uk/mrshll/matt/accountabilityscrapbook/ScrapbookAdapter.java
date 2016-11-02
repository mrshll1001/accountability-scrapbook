package uk.mrshll.matt.accountabilityscrapbook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import uk.mrshll.matt.accountabilityscrapbook.model.Scrapbook;

/**
 * Created by marshall on 01/11/16.
 */

public class ScrapbookAdapter extends ArrayAdapter<Scrapbook>
{
    private final Context context;
    private final ArrayList<Scrapbook> itemList;

    public ScrapbookAdapter(Context context, ArrayList<Scrapbook> itemsList)
    {
        super(context, R.layout.scrapbook_row, itemsList);

        this.context = context;
        this.itemList = itemsList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // Create inflator
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Get the scrapbook_row view from the inflator
        View scrapbookRow = inflater.inflate(R.layout.scrapbook_row, parent, false);

        // Get out textviews
        TextView heading = (TextView) scrapbookRow.findViewById(R.id.scrapbook_row_heading);
        TextView value = (TextView) scrapbookRow.findViewById(R.id.scrapbook_row_value);

        // Modify the textviews
        heading.setText(itemList.get(position).getName());
        heading.setTextColor(itemList.get(position).getColour());
        value.setText("Created " + itemList.get(position).getDateCreated().toString() );

        // Return
        return scrapbookRow;
    }

}
