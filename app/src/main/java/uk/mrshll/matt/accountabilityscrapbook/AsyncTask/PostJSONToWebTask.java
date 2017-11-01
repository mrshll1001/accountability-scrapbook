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

public class PostJSONToWebTask extends AsyncTask<String, Integer, String>
{
    private String urlString;
    private String tokenString;
    private AsyncResponse callback;

    private String jsonData;

    public PostJSONToWebTask(String urlString, String tokenString, AsyncResponse callback, String jsonData)
    {
        this.urlString = urlString;
        this.tokenString = tokenString;
        this.callback = callback;
        this.jsonData = jsonData;
    }

    @Override
    protected void onPostExecute(String result)
    {
        callback.processFinish(result);
    }

    @Override
    protected String doInBackground(String... strings)
    {

        // Should really only be one, but still.
        Log.d("PostToWeb", "Started Job");

        for(String s : strings)
        {
            try
            {
                this.jsonData = s;
//                URL url = new URL("https://rosemary-accounts.co.uk/qa-data");
                Log.d("URL", this.urlString);
                URL url = new URL(this.urlString);
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

        return jsonData;
    }

}
