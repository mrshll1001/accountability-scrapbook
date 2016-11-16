package uk.mrshll.matt.accountabilityscrapbook.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import io.realm.RealmList;
import io.realm.RealmResults;
import uk.mrshll.matt.accountabilityscrapbook.R;
import uk.mrshll.matt.accountabilityscrapbook.ViewScrapbookActivity;
import uk.mrshll.matt.accountabilityscrapbook.model.Scrapbook;
import uk.mrshll.matt.accountabilityscrapbook.model.Tag;

/**
 * Created by marshall on 08/11/16.
 */

public class ScrapbookAdapter extends RecyclerView.Adapter<ScrapbookAdapter.ViewHolder>
{

   private RealmResults<Scrapbook> data;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private TextView scrapbookName;
        private TextView scrapbookSubtitle;
        private TextView scrapbookItemCount;
        private Scrapbook scrapBook;
        private Context context;

        private static final String SCRAPBOOK_KEY = "SCRAPBOOK";

        public ViewHolder(View v, Context c)
        {
            super(v);
            this.context = c;

            this.scrapbookName = (TextView) v.findViewById(R.id.scrapbook_row_heading);
            this.scrapbookItemCount = (TextView) v.findViewById(R.id.scrapbook_row_itemcount);
            this.scrapbookSubtitle = (TextView) v.findViewById(R.id.scrapbook_row_subtitle);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.d("Scrapbook Recycler", "Clicked item: " + scrapbookName.getText().toString());

            Intent intent = new Intent(context, ViewScrapbookActivity.class);
            intent.putExtra("scrapbook_name", scrapbookName.getText().toString());
            context.startActivity(intent);
        }

        public void bindScrapbook(Scrapbook s)
        {
            scrapBook = s;
            scrapbookName.setText(scrapBook.getName());
            scrapbookName.setTextColor(scrapBook.getColour());


            scrapbookItemCount.setText(String.valueOf(s.getTotalItems()) + " Items");

            // Get the tags
            String tags = "";
            for (Tag t:s.getTagList())
            {
                tags = tags + t.getTagName();
            }

            scrapbookSubtitle.setText(tags);
        }


    }

    public ScrapbookAdapter(Context context, RealmResults<Scrapbook> data)
    {
        this.context = context;
        this.data = data;
    }

    @Override
    public ScrapbookAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.scrapbook_row, parent, false);

        return new ViewHolder(inflatedView, this.context);
    }

    @Override
    public void onBindViewHolder(ScrapbookAdapter.ViewHolder holder, int position) {
        Scrapbook s = data.get(position);
        holder.bindScrapbook(s);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
