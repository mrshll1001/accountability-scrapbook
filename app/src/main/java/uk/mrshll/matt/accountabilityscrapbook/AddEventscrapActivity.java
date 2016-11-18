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
import android.util.Log;
import android.view.View;
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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;
import uk.mrshll.matt.accountabilityscrapbook.Listener.FetchScrapbookDialogListener;
import uk.mrshll.matt.accountabilityscrapbook.model.EventScrap;
import uk.mrshll.matt.accountabilityscrapbook.model.Scrap;
import uk.mrshll.matt.accountabilityscrapbook.model.Scrapbook;
import uk.mrshll.matt.accountabilityscrapbook.model.Tag;

public class AddEventscrapActivity extends AppCompatActivity {

    private String placeName;
    private String placeAddress;
    private LatLng placeLatLong;

    private Realm realm;
    private ArrayList<String> selectedScrapbooks;

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

        // Set up realm and the scrapbooks
        this.realm = Realm.getDefaultInstance();
        this.selectedScrapbooks = new ArrayList<String>();

        // Retrieve Location button
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
                final EditText tags = (EditText) findViewById(R.id.create_scrap_tags);
                DatePicker datePicker = (DatePicker) findViewById(R.id.create_scrap_date_picker);

                // Perform the checks
                if (placeLatLong == null)
                {
                    Toast.makeText(mug, "Please choose a place on the map", Toast.LENGTH_SHORT).show();
                } else if (eventName.getText().toString().matches(""))
                {
                    Toast.makeText(mug, "Please enter a name for the event", Toast.LENGTH_SHORT).show();
                } else if(tags.getText().toString().matches(""))
                {
                    Toast.makeText(mug, "Please add some tags", Toast.LENGTH_SHORT).show();
                } else if(selectedScrapbooks.isEmpty())
                {
                    Toast.makeText(mug, "Please select some scrapbooks", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    // Checks have passed, get the date
                    final Date dateCreated = new Date();
                    final Date dateGiven = new Date(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());

                    // Let's get realming
                    realm.executeTransactionAsync(new Realm.Transaction()
                    {
                        @Override
                        public void execute(Realm realm)
                        {
                            // Create the scraps
                            Scrap scrap = realm.createObject(Scrap.class);
                            scrap.setDateCreated(dateCreated);
                            scrap.setDateGiven(dateGiven);
                            scrap.setType(Scrap.TYPE_EVENT);
                            scrap.setName(eventName.getText().toString());
                            scrap.setPlaceAddress(placeAddress);
                            scrap.setPlaceName(placeName);
                            scrap.setPlaceLatLng(placeLatLong.toString());

                            // Sort the tags
                            String[] tokens = tags.getText().toString().split(" ");
                            for (String t : tokens)
                            {

                                Tag tag = realm.where(Tag.class).equalTo("tagName", "#"+t).findFirst();

                                if (tag == null)
                                {
                                    Log.d("Add Spend:", "Found a null tag, attempting to add");
                                    // Create if not null
                                    tag = realm.createObject(Tag.class, "#"+t);
                                }

                                scrap.getCustomTags().add(tag);
                            }

                            // Add the scrap to scrapbooks
                            for (String s : selectedScrapbooks)
                            {
                                Scrapbook result = realm.where(Scrapbook.class).equalTo("name", s).findFirst();

                                // Inherit the tags from the scrapbooks
                                scrap.getInheritedTags().addAll(result.getTagList());

                                result.getScrapList().add(scrap);


                            }



                        }
                    }, new Realm.Transaction.OnSuccess()
                    {
                        @Override
                        public void onSuccess() {
                            Intent returnIntent = new Intent();
                            setResult(Activity.RESULT_OK, returnIntent);
                            finish();
                        }
                    }, new Realm.Transaction.OnError()
                    {
                        @Override
                        public void onError(Throwable error) {
                            Toast.makeText(mug, "Error creating Scrap", Toast.LENGTH_SHORT).show();
                        }
                    });
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
