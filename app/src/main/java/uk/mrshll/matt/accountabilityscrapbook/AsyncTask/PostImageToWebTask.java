package uk.mrshll.matt.accountabilityscrapbook.AsyncTask;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
    private Context context;
    private String urlString;
    private final OkHttpClient client = new OkHttpClient();

    public PostImageToWebTask(Context context, String url)
    {
        this.context = context;
        this.urlString = url;
    }

    @Override
    protected Boolean doInBackground(String... strings)
    {
        Log.d("Post Image to Web", "Started task");

        // Treat the String as the file path
        for (String uri : strings)
        {

            try
            {
                Uri fileUri = Uri.parse(uri);
//                File file = new File(uri);


                InputStream is = context.getContentResolver().openInputStream(fileUri);
                byte[] imageBytes = streamToByteArray(is);



                // Make the body by building a multipart form and calling the image
                RequestBody requestBody = null;
                requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("media", uri, RequestBody.create(MediaType.parse("image/png"), imageBytes)).build();

                Request request = new Request.Builder()
                        .header("Client", "Accounting Scrapbook")
                        .url("https://rosemary-accounts.co.uk/qa-media")
                        .post(requestBody)
                        .build();

                Log.d("Post Image to Web", "Attempting to post file " + uri);
                Log.d("Post Image to Web", "filePath is " + fileUri.toString());

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

    private byte[] streamToByteArray(InputStream is) throws IOException
    {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[16384];

        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();

        return buffer.toByteArray();
    }
}
