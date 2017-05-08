package uk.mrshll.matt.accountabilityscrapbook.AsyncTask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import uk.mrshll.matt.accountabilityscrapbook.Listener.AsyncResponse;

/**
 * Created by marshall on 18/01/17.
 */

public class PostJSONToWebTask extends AsyncTask<String, Integer, Boolean>
{
    private String urlString;
    private String tokenString;
    private AsyncResponse callback;

    public PostJSONToWebTask(String urlString, String tokenString, AsyncResponse callback)
    {
        this.urlString = urlString;
        this.tokenString = tokenString;
    }

    protected void onPostExecute(String result)
    {
        callback.processFinish(result);
    }

    @Override
    protected Boolean doInBackground(String... strings)
    {

        // Should really only be one, but still.
        Log.d("PostToWeb", "Started Job");

        for(String s : strings)
        {
            try
            {
                URL url = new URL("https://rosemary-accounts.co.uk/qa-data");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");


                Log.d("PostToWeb", "Sending data: \n" + s);
                OutputStream out = connection.getOutputStream();
                out.write(s.getBytes());
                out.flush();
                out.close();
                int responseCode = connection.getResponseCode();

                //TODO parse input
                Log.d("PostToWeb", "ResponseCode: " + responseCode+": "+connection.getResponseMessage());

                // Close connection
                connection.disconnect();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

}
