package uk.mrshll.matt.accountabilityscrapbook.AsyncTask;

import android.content.Context;
import android.icu.util.Output;
import android.os.AsyncTask;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;

import uk.mrshll.matt.accountabilityscrapbook.QualitativeAccountingHandler;
import uk.mrshll.matt.accountabilityscrapbook.model.Scrap;

/**
 * Created by marshall on 16/01/17.
 */

public class PostScrapTask extends AsyncTask<PostScrapParams, Integer, Boolean>
{
    @Override
    protected Boolean doInBackground(PostScrapParams... postScrapParamses)
    {
        // We use the first one because there is only one.
        String urlString = postScrapParamses[0].getUrl();
        HashSet<Scrap> scraps = postScrapParamses[0].getPayload();
        Context context = postScrapParamses[0].getContext();


        QualitativeAccountingHandler qaHandler = new QualitativeAccountingHandler(context);
        for (Scrap s : scraps)
        {
            // Convert a scrap to JSON
            String jsonData = qaHandler.scrapToJSON(s);

            // Make a HTTP Post Request to the service url
            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setDoOutput(true);
                urlConnection.setChunkedStreamingMode(0);
                urlConnection.setRequestProperty("Content-Type", "application/JSON");

                OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
                out.write(jsonData.getBytes());

                urlConnection.disconnect();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }



        }

        return true;
    }
}
