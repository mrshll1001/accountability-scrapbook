package uk.mrshll.matt.accountabilityscrapbook;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

public class AddEventscrapActivity extends AppCompatActivity {

    String thePlace;
    int PLACE_PICKER_REQUEST = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_eventscrap);

        this.thePlace = "";

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
                Place place = PlacePicker.getPlace(data, this);
                String toast = String.format("You have chosen: %s", place.getAddress());
                Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();

            }
        }
    }
}
