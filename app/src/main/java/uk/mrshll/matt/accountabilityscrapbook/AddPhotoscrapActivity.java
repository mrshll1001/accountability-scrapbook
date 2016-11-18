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

public class AddPhotoscrapActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 2;
    String currentPhotoPath;
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
        final ImageButton cameraButton = (ImageButton) findViewById(R.id.create_photoscrap_camerabutton);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Sanity check that there IS a camera activity to return!
                if (cameraIntent.resolveActivity(getPackageManager()) != null)
                {

                    File photoFile = null; // Create empty file where image will go
                    try
                    {
                        photoFile = createImageFile();
                    }catch (IOException ex)
                    {
                        Toast.makeText(AddPhotoscrapActivity.this, "Shit self when creating image file", Toast.LENGTH_SHORT).show();
                    }

                    // Continue ONLY IF SUCCESSFUL
                    if (photoFile != null)
                    {
                        photoURI = FileProvider.getUriForFile(AddPhotoscrapActivity.this, "uk.mrshll.matt.accountabilityscrapbook.fileprovider", photoFile);

                        // Tell the intent where to stick the output photo
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                    }


                }
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
                    final Date dateGiven = new Date(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());

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

                            // Add the tags
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

                            // Add the scrap to the scrapbooks
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
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
        {

            ImageView well = (ImageView) findViewById(R.id.create_photoscrap_imagewell);

            well.setImageURI(photoURI);
//            Toast.makeText(this, photoURI.toString() + " captured, good job!", Toast.LENGTH_SHORT).show();
        }
    }



    private File createImageFile() throws IOException
    {
        // Create image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        currentPhotoPath = "file:" + image.getAbsolutePath();
        return image;

    }

}
