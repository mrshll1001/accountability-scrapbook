package uk.mrshll.matt.accountabilityscrapbook.AsyncTask;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by marshall on 18/01/17.
 */

public class PostJSONToWebTask extends AsyncTask<String, Integer, Boolean>
{
    private String urlString;
    private String tokenString;

    public PostJSONToWebTask(String urlString, String tokenString)
    {
        this.urlString = urlString;
        this.tokenString = tokenString;

    }

    @Override
    protected Boolean doInBackground(String... strings) {

        // Should really only be one, but still.
        Log.d("PostToWeb", "Started Job");

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

                // Set the output stuff
                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

                writer.write(String.format("token=%s&data=%s", this.tokenString, s));
                writer.flush();
                writer.close();
                os.close();

                // Connect
                connection.connect();


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
