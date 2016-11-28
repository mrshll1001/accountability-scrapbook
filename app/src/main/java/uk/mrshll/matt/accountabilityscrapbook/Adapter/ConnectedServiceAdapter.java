package uk.mrshll.matt.accountabilityscrapbook.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import io.realm.RealmList;
import io.realm.RealmResults;
import uk.mrshll.matt.accountabilityscrapbook.R;
import uk.mrshll.matt.accountabilityscrapbook.model.ConnectedService;

/**
 * Created by marshall on 28/11/16.
 */

public class ConnectedServiceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{

    private RealmResults<ConnectedService> data;
    private Context context;

    public ConnectedServiceAdapter(Context c, RealmResults<ConnectedService> data)
    {
        this.data = data;
        this.context = c;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.connected_service_item, parent, false);


        return new ConnectedServiceViewHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ConnectedServiceViewHolder serviceHolder = (ConnectedServiceViewHolder) holder;
        serviceHolder.bindItem(this.data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    class ConnectedServiceViewHolder extends RecyclerView.ViewHolder
    {
        private ConnectedService item;
        private TextView itemName;

        public ConnectedServiceViewHolder(View v)
        {
            super(v);

            this.itemName = (TextView) v.findViewById(R.id.connected_service_item_text);
        }

        public void bindItem(ConnectedService item)
        {
            itemName.setText(item.getEndpointUrl());
        }

    }

}
