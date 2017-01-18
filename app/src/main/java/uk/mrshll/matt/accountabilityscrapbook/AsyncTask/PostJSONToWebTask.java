package uk.mrshll.matt.accountabilityscrapbook.AsyncTask;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by marshall on 18/01/17.
 */

public class PostJSONToWebTask extends AsyncTask<String, Integer, Boolean>
{
    private String urlString;

    public PostJSONToWebTask(String urlString)
    {
        this.urlString = urlString;
    }

    @Override
    protected Boolean doInBackground(String... strings) {

        // Should really only be one, but still.
        for(String s : strings)
        {
            try
            {
                // Try open a connection
                HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();

                // Set Request method
                connection.setRequestMethod("POST");

                // Set do Output and Input to true. So we can write and then read a response
                connection.setDoOutput(true);
                connection.setDoInput(true);

                // Connect
                connection.connect();

                // Write the bytes of the JSON data
                connection.getOutputStream().write(s.getBytes());

                //TODO parse input
                Log.d("PostToWeb", "Seems to have worked!");

                // Close connection
                connection.disconnect();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return true;
    }
}
