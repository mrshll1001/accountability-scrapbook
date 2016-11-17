package uk.mrshll.matt.accountabilityscrapbook.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmList;
import uk.mrshll.matt.accountabilityscrapbook.R;
import uk.mrshll.matt.accountabilityscrapbook.model.SpendScrap;
import uk.mrshll.matt.accountabilityscrapbook.model.Tag;

/**
 * Created by marshall on 17/11/16.
 */

public class SpendScrapAdapter extends RecyclerView.Adapter<SpendScrapAdapter.ViewHolder>
{
    private RealmList<SpendScrap> data;
    private Context context;


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        private Context context;

        // View variables here
        private SpendScrap spend;
        private TextView spendName;
        private TextView spendValue;
        private TextView spendDate;
        private TextView spendTags;


        public ViewHolder(View v, Context c)
        {
            super(v);
            this.context = c;

            this.spendName = (TextView) v.findViewById(R.id.spendscrap_name);
            this.spendValue = (TextView) v.findViewById(R.id.spendscrap_value);
            this.spendDate = (TextView) v.findViewById(R.id.spendscrap_date);
            this.spendTags = (TextView) v.findViewById(R.id.spendscrap_tags);

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view)
        {
            Log.d("SpendScrapAdapter", "Clicked Item");

        }

        // This is where the data is set
        public void bindScrap(SpendScrap s)
        {
            this.spend = s;

            spendName.setText(this.spend.getName());
            spendDate.setText(String.format("%d/%d/%d", this.spend.getDateOfSpend().getDate(), this.spend.getDateOfSpend().getMonth() + 1, this.spend.getDateOfSpend().getYear()));
            spendValue.setText(String.format("£%s", this.spend.getValue()));

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


    public SpendScrapAdapter(Context context, RealmList<SpendScrap> data)
    {
        this.data = data;
        this.context = context;
    }


    @Override
    public SpendScrapAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.spendscrap_card, parent, false);

        return new ViewHolder(inflatedView, this.context);
    }

    @Override
    public void onBindViewHolder(SpendScrapAdapter.ViewHolder holder, int position)
    {
        SpendScrap s = data.get(position);
        holder.bindScrap(s);
    }

    @Override
    public int getItemCount()
    {
        return data.size();
    }
}
