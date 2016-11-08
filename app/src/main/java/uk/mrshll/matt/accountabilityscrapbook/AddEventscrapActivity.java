package uk.mrshll.matt.accountabilityscrapbook;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class AddEventscrapActivity extends AppCompatActivity {

    String placeName;
    String placeAddress;
    LatLng placeLatLong;
    int PLACE_PICKER_REQUEST = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_eventscrap);

        // Set default values for the place
        this.placeName = null;
        this.placeAddress = null;
        this.placeLatLong = null;


        final Activity mug = this;

        final ImageButton getLocation = (ImageButton) findViewById(R.id.create_eventscrap_mapbutton);
        getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                try {

                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    startActivityForResult(builder.build(mug), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesNotAvailableException e)
                {

                } catch (GooglePlayServicesRepairableException e)
                {

                }



            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PLACE_PICKER_REQUEST)
        {
            if(resultCode == RESULT_OK)
            {
                Place place = PlacePicker.getPlace(this, data);
                String toast = String.format("Hello: %s", place.getLatLng());
                Toast.makeText(this, toast, Toast.LENGTH_LONG).show();

                this.placeAddress = place.getAddress().toString();
                this.placeLatLong = place.getLatLng();
                this.placeName = place.getName().toString();

                // Update the UI
                final TextView address = (TextView) findViewById(R.id.create_eventscrap_address);
                address.setText(this.placeName + ", " + this.placeAddress);

                // Try to fetch a static map from GMaps and set the image as the background
                AsyncTask<Void, Void, Bitmap> getMapImage = new AsyncTask<Void, Void, Bitmap>() {
                    @Override
                    protected Bitmap doInBackground(Void... voids)
                    {
                        Bitmap map = null;
                        try
                        {

                            URL url = new URL("http://maps.google.com/maps/api/staticmap?center="+placeLatLong.latitude+","+placeLatLong.longitude+"&zoom=15&size=200x200&sensor=false");
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setRequestMethod("GET");
                            InputStream stream = connection.getInputStream();
                            map = BitmapFactory.decodeStream(stream);


                        } catch (MalformedURLException e)
                        {
                            Toast.makeText(AddEventscrapActivity.this, "Malformed URL", Toast.LENGTH_SHORT).show();
                        } catch (IOException e)
                        {
                            Toast.makeText(AddEventscrapActivity.this, "IO Exception", Toast.LENGTH_SHORT).show();
                        }

                        return map;
                    }

                    @Override
                    protected void onPostExecute(Bitmap bitmap) {
                        if (bitmap != null)
                        {
                            final ImageView well = (ImageView) findViewById(R.id.create_eventcrap_imagewell);
                            well.setImageBitmap(bitmap);
                            address.setTextColor(getResources().getColor(R.color.colorWellGrey));

                        }
                    }
                };

                getMapImage.execute();



            }
        }
    }
}
