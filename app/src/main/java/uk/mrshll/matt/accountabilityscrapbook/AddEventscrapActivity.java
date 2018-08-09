package uk.mrshll.matt.accountabilityscrapbook;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import javax.net.ssl.HttpsURLConnection;

import io.realm.Realm;
import uk.mrshll.matt.accountabilityscrapbook.Listener.AddScrapListener;
import uk.mrshll.matt.accountabilityscrapbook.Listener.FetchScrapbookDialogListener;
import uk.mrshll.matt.accountabilityscrapbook.Utility.ScrapCreator;
import uk.mrshll.matt.accountabilityscrapbook.Utility.TagManager;
import uk.mrshll.matt.accountabilityscrapbook.model.Scrap;
import uk.mrshll.matt.accountabilityscrapbook.model.Scrapbook;
import uk.mrshll.matt.accountabilityscrapbook.model.Tag;

import static uk.mrshll.matt.accountabilityscrapbook.AddPhotoscrapActivity.REQUEST_PERMISSION_FOR_READ_STORAGE;

public class AddEventscrapActivity extends AppCompatActivity
{

    private static final int REQUEST_PERMISSION_FOR_LOCATION = 66;

    private String placeName;
    private String placeAddress;
    private LatLng placeLatLong;

    private Realm realm;
    private ArrayList<String> selectedScrapbooks;
    private HashSet<String> tags;

    int PLACE_PICKER_REQUEST = 3;
    int OSM_PICKER_REQUEST = 42;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_eventscrap);

        // Set default values for the place
        this.placeName = null;
        this.placeAddress = null;
        this.placeLatLong = null;

        // Set up realm and the scrapbooks
        this.realm = Realm.getDefaultInstance();
        this.selectedScrapbooks = new ArrayList<String>();

        /* Set up the AutoComplete box for the tags */
        setUpTagAutoComplete();
        setUpAddTagButton();

        // Retrieve Location button
        final Activity mug = this;
        final Button getLocation = (Button) findViewById(R.id.create_eventscrap_mapbutton);
        getLocation.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                try {

                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    startActivityForResult(builder.build(mug), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesNotAvailableException e) {
                    getRawGPSData();

                } catch (GooglePlayServicesRepairableException e) {
                    getRawGPSData();

                }


            }
        });

        // Scrapbooks button
        Button scrapbookButton = (Button) findViewById(R.id.create_scrap_scrapbook_button);
        scrapbookButton.setOnClickListener(new FetchScrapbookDialogListener(this, this.realm, this.selectedScrapbooks));

        // Done Button
        Button doneButton = (Button) findViewById(R.id.create_scrap_done);
        doneButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                // Get values
                final EditText eventName = (EditText) findViewById(R.id.create_eventscrap_name);
                DatePicker datePicker = (DatePicker) findViewById(R.id.create_scrap_date_picker);

                // Perform the checks
                if (placeLatLong == null) {
                    Toast.makeText(mug, "Please choose a place on the map", Toast.LENGTH_SHORT).show();
                } else if (eventName.getText().toString().matches("")) {
                    Toast.makeText(mug, "Please enter a name for the event", Toast.LENGTH_SHORT).show();
                } else if (selectedScrapbooks.isEmpty()) {
                    Toast.makeText(mug, "Please select some scrapbooks", Toast.LENGTH_SHORT).show();
                } else {
                    // Checks have passed, get the date
                    final Date dateGiven = new Date(datePicker.getYear() - 1900, datePicker.getMonth(), datePicker.getDayOfMonth());

                    ScrapCreator sc = new ScrapCreator(realm, new AddScrapListener()
                    {
                        @Override
                        public void realmSuccess() {
                            Intent returnIntent = new Intent();
                            setResult(Activity.RESULT_OK, returnIntent);
                            finish();
                        }

                        @Override
                        public void realmError(Throwable error) {

                        }
                    });

                    sc.createEventScrap(dateGiven,
                            eventName.getText().toString(),
                            placeAddress,
                            placeName,
                            placeLatLong,
                            tags.toArray(new String[tags.size()]),
                            selectedScrapbooks.toArray(new String[selectedScrapbooks.size()])
                            );

                }

            }
        });

    }


    private void getRawGPSData()
    {

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(AddEventscrapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_FOR_LOCATION);


            return;
        }
        Intent intent = new Intent(this, OSMPickerActivity.class);
        startActivityForResult(intent, OSM_PICKER_REQUEST);
    }

    @SuppressLint("StaticFieldLeak")
    private void getAddressFromLatLonOSM(final Double lat, final Double lon)
    {
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>()
        {

            @Override
            protected String doInBackground(Void... voids) {

                StringBuilder result = new StringBuilder();
                try
                {
//                    URL lookup = new URL("http://nominatim.openstreetmap.org/reverse?format=json&lat="+lat+"&lon="+lon+"&zoom=18&addressdetails=1");
                    URL lookup = new URL(URLEncoder.encode("https://nominatim.openstreetmap.org/reverse?format=json&lat=54.97394378741926&lon=-1.613917350769043&zoom=18&addressdetails=1"));

                    HttpURLConnection connection = (HttpURLConnection) lookup.openConnection();
                    connection.setRequestMethod("GET");
                    InputStream stream = connection.getInputStream();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                    String line;

                    while ((line = reader.readLine()) != null)
                    {
                     result.append(line);
                    }

                    Log.d("GetAddress", stream.toString());

                    return result.toString();


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            protected void onPostExecute(String address)
            {
                updateAddress(address);
//                try {
//                    JSONObject json = new JSONObject(address);
//
//                    String display = json.getString("display_name");
//
//                    updateAddress(display);
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
            }
        };

        task.execute();
    }

    private void updateAddress(String address)
    {
        // Update the UI
        final TextView addressField = (TextView) findViewById(R.id.create_eventscrap_address);
        addressField.setText(address);


    }

    private void updateImageWell(final double latitude,final double longitude)
    {
//        // Update the UI
//        final TextView address = (TextView) findViewById(R.id.create_eventscrap_address);
//        address.setText("Currently not fetching address");

        // Try to fetch a static map from GMaps and set the image as the background
        AsyncTask<Void, Void, Bitmap> getMapImage = new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... voids)
            {
                Bitmap map = null;
                try
                {
                    URL mapbox = new URL("https://api.mapbox.com/styles/v1/mrshll-ncl/cjj5vzskl0wjt2sqa3fn66s0a/static/"+longitude+","+latitude+",15,0,0/300x200?access_token=pk.eyJ1IjoibXJzaGxsLW5jbCIsImEiOiJjamo1dm9idGQxeHB5M3ZxZ2V6bDY1bW56In0.ZBBG6HuWyj04L7k6Jzewhg");
                    URL url = new URL("http://maps.google.com/maps/api/staticmap?center="+longitude+","+latitude+"&zoom=15&size=200x200&sensor=false");
                    HttpURLConnection connection = (HttpURLConnection) mapbox.openConnection();
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


                }
            }
        };

        getMapImage.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PLACE_PICKER_REQUEST)
        {
            if(resultCode == RESULT_OK)
            {
                Place place = PlacePicker.getPlace(this, data);

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

                            URL url = new URL("http://maps.google.com/maps/api/staticmap?center="+placeLatLong.latitude+","+placeLatLong.longitude+"&zoom=8&size=200x200&sensor=false");
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


                        }
                    }
                };

                getMapImage.execute();



            }


        } else if(requestCode == OSM_PICKER_REQUEST)
        {
            if (resultCode == RESULT_OK)
            {
                Double lat = data.getDoubleExtra("latitude", 0);
                Double lon = data.getDoubleExtra("longitude", 0);

                this.placeLatLong = new LatLng(lat, lon);
                getAddressFromLatLonOSM(lat, lon);
                updateImageWell(lat, lon);
            }
        }
    }



    // Fires when the user clicks the dialog box that requests permission
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        switch (requestCode)
        {
            case REQUEST_PERMISSION_FOR_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    getRawGPSData();
                }
                return;

        }
    }

    private void setUpTagAutoComplete()
    {
        final AutoCompleteTextView tagField = (AutoCompleteTextView) findViewById(R.id.autocomplete_tags);
        this.tags = new HashSet<>();

        String[] tagArray = new TagManager(realm).getTagsAsStringArray();
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, tagArray);
        tagField.setAdapter(adapter);

        tagField.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String token = tagField.getText().toString();
                tags.add(token);
                tagField.setText(null);
                updateCustomTagField();



            }
        });
    }

    private void setUpAddTagButton()
    {
        final AutoCompleteTextView tagField = (AutoCompleteTextView) findViewById(R.id.autocomplete_tags);
        final Button addTagButton = (Button) findViewById(R.id.addTagFieldButton);
        addTagButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                String[] tokens = tagField.getText().toString().split(",");

                for (String token : tokens)
                {
                    tags.add(token.trim());
                }

                tagField.setText(null);
                updateCustomTagField();
            }
        });
    }

    private void updateCustomTagField()
    {
        TextView tagField = (TextView) findViewById(R.id.currentTagsField);
        StringBuilder sb = new StringBuilder();

        for (String s : tags)
        {
            sb.append(s);
            sb.append(" ");
        }

        tagField.setText(sb.toString());
    }
}
