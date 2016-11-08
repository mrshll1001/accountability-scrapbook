package uk.mrshll.matt.accountabilityscrapbook.Adapter;

import android.content.Context;
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
import uk.mrshll.matt.accountabilityscrapbook.model.Scrapbook;

/**
 * Created by marshall on 08/11/16.
 */

public class ScrapbookAdapter extends RecyclerView.Adapter<ScrapbookAdapter.ViewHolder>
{

   private RealmResults<Scrapbook> data;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private TextView scrapbookName;
        private Scrapbook scrapBook;

        private static final String SCRAPBOOK_KEY = "SCRAPBOOK";

        public ViewHolder(View v)
        {
            super(v);

            this.scrapbookName = (TextView) v.findViewById(R.id.scrapbook_row_heading);

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.d("Scrapbook Recycler", "Clicked item");
        }

        public void bindScrapbook(Scrapbook s)
        {
            scrapBook = s;
            scrapbookName.setText(scrapBook.getName());
            scrapbookName.setTextColor(scrapBook.getColour());
        }


    }

    public ScrapbookAdapter(RealmResults<Scrapbook> data)
    {
        this.data = data;
    }

    @Override
    public ScrapbookAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.scrapbook_row, parent, false);

        return new ViewHolder(inflatedView);
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
