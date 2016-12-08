package uk.mrshll.matt.accountabilityscrapbook.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import io.realm.RealmResults;
import uk.mrshll.matt.accountabilityscrapbook.Listener.FetchScrapbookDialogListener;
import uk.mrshll.matt.accountabilityscrapbook.R;
import uk.mrshll.matt.accountabilityscrapbook.ShareDataActivity;
import uk.mrshll.matt.accountabilityscrapbook.model.ConnectedService;

/**
 * Another adapter for the shared services, this time to populate a list which can be shared out.
 * Created by marshall on 08/12/16.
 */

public class ShareServiceListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private RealmResults<ConnectedService> data;
    private Context context;

    public ShareServiceListAdapter(Context c, RealmResults<ConnectedService> data)
    {
        this.data = data;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_share_service, parent, false);

        return new ShareServiceViewHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        ShareServiceViewHolder shareServiceViewHolder = (ShareServiceViewHolder) holder;
        shareServiceViewHolder.bindItem(this.data.get(position));
    }

    @Override
    public int getItemCount()
    {
        return data.size();
    }

    class ShareServiceViewHolder extends RecyclerView.ViewHolder
    {
        private ConnectedService service;
        private TextView url;
        private Button shareButton;

        public ShareServiceViewHolder(View v)
        {
            super(v);

            this.url = (TextView) v.findViewById(R.id.share_service_list_item_url);
            this.shareButton = (Button) v.findViewById(R.id.share_service_share_button);
        }

        public void bindItem(ConnectedService item)
        {
            this.service = item;
            url.setText(item.getEndpointUrl());

            shareButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view) {

                    // Can do a thing here

                }
            });

        }
    }



}
