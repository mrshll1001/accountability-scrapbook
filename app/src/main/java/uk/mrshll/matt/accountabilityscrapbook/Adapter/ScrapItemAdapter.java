package uk.mrshll.matt.accountabilityscrapbook.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.location.places.Place;

import java.util.ArrayList;

import io.realm.RealmList;
import uk.mrshll.matt.accountabilityscrapbook.R;
import uk.mrshll.matt.accountabilityscrapbook.model.Scrap;
import uk.mrshll.matt.accountabilityscrapbook.model.Tag;

/**
 * Created by marshall on 21/11/16.
 */

public class ScrapItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private RealmList<Scrap> data;
    private Context context;


    public ScrapItemAdapter(Context c, RealmList<Scrap> data)
    {
        this.context = c;
        this.data = data;
    }

    @Override
    public int getItemViewType(int position)
    {
        return this.data.get(position).getType();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        // This is where the views are inflated and the viewholder is created. SO switch on get item type
        switch (viewType)
        {
            case 0:
                // Find the layout and inflate and return it.
                View inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.spendscrap_card, parent, false);
                return new SpendViewHolder(inflatedView, this.context);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        // This is where the data is bound, you fetch the data and then CALL THE BIND METHOD
        // INSIDE THE HOLDER, so there'll be a switch statement there too, I think.

        // Remember to cast the generic holder type to the desired on before calling the bind method
        switch (holder.getItemViewType())
        {
            case 0:
                SpendViewHolder spendHolder = (SpendViewHolder) holder;
                Scrap s = data.get(position);

                spendHolder.bindScrap(s);
                break;
        }

    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }


    /**
     * Viewholder for the spend scrap type
     */
    class SpendViewHolder extends RecyclerView.ViewHolder
    {
        private Context context;

        // View variables here
        private Scrap spend;
        private TextView spendName;
        private TextView spendValue;
        private TextView spendDate;
        private TextView spendTags;

        public SpendViewHolder(View v, Context c)
        {
            super(v);
            this.context = c;

            this.spendName = (TextView) v.findViewById(R.id.spendscrap_name);
            this.spendValue = (TextView) v.findViewById(R.id.spendscrap_value);
            this.spendDate = (TextView) v.findViewById(R.id.spendscrap_date);
            this.spendTags = (TextView) v.findViewById(R.id.spendscrap_tags);
        }

        // This is where the data is set
        public void bindScrap(Scrap s)
        {
            this.spend = s;

            spendName.setText(this.spend.getName());
            spendDate.setText(String.format("%d/%d/%d", this.spend.getDateGiven().getDate(), this.spend.getDateGiven().getMonth() + 1, this.spend.getDateGiven().getYear()));
            spendValue.setText(String.format("£%s", this.spend.getSpendValue()));

            ArrayList<Tag> tagList = new ArrayList<Tag>();
            tagList.addAll(this.spend.getCustomTags());
            tagList.addAll(this.spend.getInheritedTags());

            // Build the tag string
            StringBuilder builder = new StringBuilder();
            for (Tag t : tagList)
            {
                builder.append(t.getTagName());
                if(tagList.indexOf(t) != tagList.size() - 1)
                {
                    builder.append(", ");
                }
            }

            spendTags.setText(builder.toString());
        }
    }


}
