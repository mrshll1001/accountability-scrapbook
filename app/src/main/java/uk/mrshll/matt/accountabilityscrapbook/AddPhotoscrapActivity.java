package uk.mrshll.matt.accountabilityscrapbook;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import uk.mrshll.matt.accountabilityscrapbook.Listener.FetchScrapbookDialogListener;
import uk.mrshll.matt.accountabilityscrapbook.model.Scrap;
import uk.mrshll.matt.accountabilityscrapbook.model.Scrapbook;
import uk.mrshll.matt.accountabilityscrapbook.model.Tag;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class AddPhotoscrapActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 2;
    static final int REQUEST_IMAGE_PICKER = 100;
    private ArrayList<String> selectedScrapbooks;
    private Realm realm;

    Uri photoURI;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photoscrap);

        // Set up variables for database interaction
        this.realm = Realm.getDefaultInstance();
        this.selectedScrapbooks = new ArrayList<String>();

        // Add the action to start the camera on the phone
        final Button cameraButton = (Button) findViewById(R.id.create_photoscrap_camerabutton);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                // Create the intent to the image
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, REQUEST_IMAGE_PICKER);


            }
        });

        // Set up the scrapbook button
        Button scrapbookButton = (Button) findViewById(R.id.create_scrap_scrapbook_button);
        scrapbookButton.setOnClickListener(new FetchScrapbookDialogListener(this, this.realm, this.selectedScrapbooks));

        // Set up the Done button
        Button done = (Button) findViewById(R.id.create_scrap_done);
        done.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                // Get the details
                final EditText tags = (EditText) findViewById(R.id.create_scrap_tags);
                DatePicker datePicker = (DatePicker) findViewById(R.id.create_scrap_date_picker);

                // Perform checks
                if (photoURI == null)
                {
                    Toast.makeText(AddPhotoscrapActivity.this, "Please select a photo", Toast.LENGTH_SHORT).show();
                } else if (selectedScrapbooks.isEmpty())
                {
                    Toast.makeText(AddPhotoscrapActivity.this, "Please select some scrapbooks", Toast.LENGTH_SHORT).show();
                } else if (tags.getText().toString().matches(""))
                {
                    Toast.makeText(AddPhotoscrapActivity.this, "Please add some tags", Toast.LENGTH_SHORT).show();
                } else
                {
                    // Checks have passed. Get the dates
                    final Date dateCreated = new Date();
                    final Date dateGiven = new Date(datePicker.getYear() - 1900, datePicker.getMonth(), datePicker.getDayOfMonth());

                    // Execute the realm transaction
                    realm.executeTransactionAsync(new Realm.Transaction()
                    {
                        @Override
                        public void execute(Realm realm) {

                            // Create the photo scrap
                            Scrap scrap = realm.createObject(Scrap.class);
                            scrap.setDateCreated(dateCreated);
                            scrap.setDateGiven(dateGiven);
                            scrap.setType(Scrap.TYPE_PHOTO);
                            scrap.setPhotoUri(photoURI.toString());
                            scrap.setAttachedScrapbooks(0); // Initialise

                            // Add the tags
                            String[] tokens = tags.getText().toString().split(" ");
                            for (String t : tokens)
                            {

                                Tag tag = realm.where(Tag.class).equalTo("tagName", t).findFirst();

                                if (tag == null)
                                {
                                    Log.d("Add Spend:", "Found a null tag, attempting to add");
                                    // Create if not null
                                    tag = realm.createObject(Tag.class, t);
                                }

                                scrap.getCustomTags().add(tag);
                            }

                            // Add the scrap to the scrapbooks
                            for (String s : selectedScrapbooks)
                            {
                                Scrapbook result = realm.where(Scrapbook.class).equalTo("name", s).findFirst();

                                // Inherit the tags from the scrapbooks
                                scrap.getInheritedTags().addAll(result.getTagList());

                                result.getScrapList().add(scrap);
                                scrap.setAttachedScrapbooks(scrap.getAttachedScrapbooks() + 1); // Increment by 1

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
                            Toast.makeText(AddPhotoscrapActivity.this, "Error creating Scrap!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case REQUEST_IMAGE_PICKER:

                if (resultCode == RESULT_OK)
                {
                    Uri selectedImage = data.getData();
                    ImageView well = (ImageView) findViewById(R.id.create_photoscrap_imagewell);
                    well.setImageURI(selectedImage);
                    photoURI = selectedImage;
                }

                break;
        }
    }




}
