package uk.mrshll.matt.accountabilityscrapbook.Adapter;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.fitness.result.FileUriResult;
import com.google.android.gms.location.places.Place;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.DecimalFormat;
import java.util.ArrayList;

import io.realm.RealmList;
import uk.mrshll.matt.accountabilityscrapbook.R;
import uk.mrshll.matt.accountabilityscrapbook.model.Scrap;
import uk.mrshll.matt.accountabilityscrapbook.model.Tag;

import static uk.mrshll.matt.accountabilityscrapbook.model.Scrap.TYPE_SPEND;

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
                View spendCard = LayoutInflater.from(parent.getContext()).inflate(R.layout.spendscrap_card, parent, false);
                return new SpendViewHolder(spendCard, this.context);
            case 1:
                // Find the layout and inflate and return
                View quoteCard = LayoutInflater.from(parent.getContext()).inflate(R.layout.quotescrap_card, parent, false);
                return new QuoteViewHolder(quoteCard, this.context);
            case 2:
                // Find layout and return
                View eventCard = LayoutInflater.from(parent.getContext()).inflate(R.layout.eventscrap_card, parent, false);
                return new EventViewHolder(eventCard, this.context);
            case 4:
                // Find the layout and inflate and return
                View photoCard = LayoutInflater.from(parent.getContext()).inflate(R.layout.photoscrap_card, parent, false);
                return new PhotoViewHolder(photoCard, this.context);
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
                Scrap spend = data.get(position);

                spendHolder.bindScrap(spend);
                break;
            case 1:
                QuoteViewHolder quoteHolder = (QuoteViewHolder) holder;
                Scrap quote = data.get(position);

                quoteHolder.bindScrap(quote);
                break;
            case 2:
                EventViewHolder eventHolder = (EventViewHolder) holder;
                Scrap event = data.get(position);

                eventHolder.bindScrap(event);
                break;
            case 4:
                PhotoViewHolder photoHolder = (PhotoViewHolder) holder;
                Scrap photo = data.get(position);

                photoHolder.bindScrap(photo);
        }

    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }


    /**
     * Viewholder for the Event View
     */
    class EventViewHolder extends RecyclerView.ViewHolder
    {
        private Context context;

        // View variables
        private Scrap scrap;
        private TextView eventName;
        private TextView eventAddress;
        private TextView date;
        private TextView tags;
        private ImageView icon;

        public EventViewHolder(View v, Context c)
        {
            super(v);
            this.context = c;

            this.eventName = (TextView) v.findViewById(R.id.eventcard_content_title);
            this.eventAddress = (TextView) v.findViewById(R.id.eventcard_content_address);
            this.date = (TextView) v.findViewById(R.id.scrapcard_date);
            this.icon = (ImageView) v.findViewById(R.id.scrapcard_icon);
            this.tags = (TextView) v.findViewById(R.id.scrapcard_tags_content);
        }

        // Set the data
        public void bindScrap(Scrap s)
        {
            this.scrap = s;
            eventName.setText(s.getName());
            eventAddress.setText(s.getPlaceName());
            date.setText(this.scrap.getFormattedDateString(this.scrap.getDateGiven()));
            icon.setBackground(itemView.getResources().getDrawable(R.drawable.ic_calendar_clock));

            tags.setText(this.scrap.getFormattedTagString(true, true));


        }
    }

    /**
     * Viewholder for the Quote view
     */
    class QuoteViewHolder extends RecyclerView.ViewHolder
    {
        private Context context;

        // View variables
        private Scrap scrap;
        private TextView quoteText;
        private TextView quoteSource;
        private TextView tags;
        private TextView date;
        private ImageView icon;



        public QuoteViewHolder(View v, Context c)
        {
            super(v);
            this.context = c;

            this.quoteText = (TextView) v.findViewById(R.id.quotecard_content_quotetext);
            this.tags = (TextView) v.findViewById(R.id.scrapcard_tags_content);
            this.date = (TextView) v.findViewById(R.id.scrapcard_date);
            this.quoteSource = (TextView) v.findViewById(R.id.quotecard_content_source);
            this.icon = (ImageView) v.findViewById(R.id.scrapcard_icon);

        }

        // This is where the data is set
        public void bindScrap(Scrap s)
        {
            this.scrap = s;

            quoteText.setText(String.format("\"%s\"", s.getQuoteText()));
            quoteSource.setText(String.format("- %s", s.getQuoteSource()));
            date.setText(this.scrap.getFormattedDateString(this.scrap.getDateGiven()));
            icon.setBackground(itemView.getResources().getDrawable(R.drawable.ic_quotebubble));
            tags.setText(this.scrap.getFormattedTagString(true, true));
        }


    }


    /**
     * Viewholder for the spend scrap type
     */
    class SpendViewHolder extends RecyclerView.ViewHolder
    {
        private Context context;

        // View variables here
        private Scrap scrap;
        private TextView name;
        private TextView value;
        private TextView date;
        private TextView tags;
        private ImageView icon;

        public SpendViewHolder(View v, Context c)
        {
            super(v);
            this.context = c;

            this.name = (TextView) v.findViewById(R.id.spendcard_content_name);
            this.value = (TextView) v.findViewById(R.id.spendcard_content_value);
            this.date = (TextView) v.findViewById(R.id.scrapcard_date);
            this.icon = (ImageView) v.findViewById(R.id.scrapcard_icon);
            this.tags = (TextView) v.findViewById(R.id.scrapcard_tags_content);
        }

        // This is where the data is set
        public void bindScrap(Scrap s)
        {
            this.scrap = s;

            name.setText(this.scrap.getName());
            date.setText(this.scrap.getFormattedDateString(this.scrap.getDateGiven()));
            DecimalFormat df = new DecimalFormat("#.00");
            value.setText(String.format("£%s", df.format(Math.abs(this.scrap.getSpendValue())))); // Use the absolute value to ensure it's displayed correctly
            icon.setBackground(itemView.getResources().getDrawable(R.drawable.ic_coin_stack));

            tags.setText(this.scrap.getFormattedTagString(true, true));

        }
    }

    /**
     * ViewHolder for the photo scrap type
     */
    class PhotoViewHolder extends RecyclerView.ViewHolder
    {
        private Context context;

        // View variables here
        private Scrap scrap;
        private ImageView imageView;
        private TextView date;
        private TextView tags;
        private ImageView icon;
        private TextView imageCount;

        public PhotoViewHolder(View v, Context c)
        {
            super(v);
            this.context = c;

            this.imageView = (ImageView) v.findViewById(R.id.photoscrap_imageview);
            this.imageCount = (TextView) v.findViewById(R.id.photoscrap_imagecount);
            this.date = (TextView) v.findViewById(R.id.scrapcard_date);
            this.icon = (ImageView) v.findViewById(R.id.scrapcard_icon);
            this.tags = (TextView) v.findViewById(R.id.scrapcard_tags_content);
        }

        // This is where the data is set
        public void bindScrap(Scrap s)
        {
            this.scrap = s;

            date.setText(this.scrap.getFormattedDateString(this.scrap.getDateGiven()));
            icon.setBackground(itemView.getResources().getDrawable(R.drawable.ic_menu_gallery));
            tags.setText(this.scrap.getFormattedTagString(true, true));
            imageCount.setText(this.scrap.getImageList().size() + " images");

            // Load a smaller bitmap so that it doesn't crap itself
//
            try {
                Bitmap bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeStream(context.getContentResolver().openInputStream(Uri.parse(scrap.getImageList().first()))),200,200);
                imageView.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


        }



    }



}
