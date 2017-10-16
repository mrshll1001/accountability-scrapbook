package uk.mrshll.matt.accountabilityscrapbook.AsyncTask;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;



/**
 * Created by marshall on 20/01/17.
 */

public class PostImageToWebTask extends AsyncTask<String, Integer, Boolean>
{
    private String urlString;
    private final OkHttpClient client = new OkHttpClient();

    public PostImageToWebTask(String url)
    {
        this.urlString = url;
    }

    @Override
    protected Boolean doInBackground(String... strings)
    {
        Log.d("Post Image to Web", "Started task");

        // Treat the String as the file path
        for (String uri : strings)
        {
            Uri fileUri = Uri.parse(uri);
            File file = new File(fileUri.getPath());


            // Make the body by building a multipart form and calling the image
            RequestBody requestBody = null;
            requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("media", uri, RequestBody.create(MediaType.parse("image/png"), file)).build();

            Request request = new Request.Builder()
                    .header("Client", "Accounting Scrapbook")
                    .url("https://rosemary-accounts.co.uk/qa-media")
                    .post(requestBody)
                    .build();
            try
            {
                Log.d("Post Image to Web", "Attempting to post file " + uri);
                Response response = client.newCall(request).execute();
                if (response.code() == 500)
                {
                    Log.d("HTTP 500", response.message());
                }
            } catch (IOException e)
            {
                Log.d("Post Image to web", "File posting failed");

                e.printStackTrace();

            }
            Log.d("Post Image to Web", "Seems to have worked");




        }

        return null;
    }
}
