package uk.mrshll.matt.accountabilityscrapbook.AsyncTask;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by marshall on 20/01/17.
 */

public class PostImageToWebTask extends AsyncTask<Bitmap, Integer, Boolean>
{
    private String urlString;

    public PostImageToWebTask(String url)
    {
        this.urlString = url;
    }

    @Override
    protected Boolean doInBackground(Bitmap... bitmaps)
    {
        Log.d("Post Image to Web", "Started task");

        // Treat the String as the file path
        for (Bitmap b : bitmaps)
        {
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setUseCaches(false);
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("Content-Type", "multipart/form-data");

                DataOutputStream req = new DataOutputStream(connection.getOutputStream());
                OutputStream output = connection.getOutputStream();
                b.compress(Bitmap.CompressFormat.JPEG, 100, output);
                output.close();

                connection.connect();

                Log.d("Post Image to Web", "Seems to have worked");


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
