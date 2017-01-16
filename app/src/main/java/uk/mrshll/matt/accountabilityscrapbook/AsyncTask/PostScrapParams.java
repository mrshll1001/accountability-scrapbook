package uk.mrshll.matt.accountabilityscrapbook.AsyncTask;

import android.content.ContentResolver;
import android.content.Context;

import java.util.HashSet;

import uk.mrshll.matt.accountabilityscrapbook.model.Scrap;

/**
 * Container model to allow AsyncTask to take both a Url and a payload as parameters
 * Created by marshall on 16/01/17.
 */

public class PostScrapParams
{
    private Context context;
    private String url;
    private HashSet<Scrap> payload;

    public PostScrapParams(Context c, String url, HashSet<Scrap> payload)
    {
        this.url = url;
        this.payload = payload;
    }

    public Context getContext()
    {
        return this.context;
    }

    public String getUrl()
    {
        return this.url;
    }

    public HashSet<Scrap> getPayload()
    {
        return this.payload;
    }
}
