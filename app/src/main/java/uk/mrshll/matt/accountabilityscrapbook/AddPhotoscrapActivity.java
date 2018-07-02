package uk.mrshll.matt.accountabilityscrapbook;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
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
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
import android.content.ClipData.Item;

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

import static android.content.ClipData.*;
import static android.os.Environment.getExternalStoragePublicDirectory;

public class AddPhotoscrapActivity extends AppCompatActivity {

    static final int REQUEST_PERMISSION_FOR_READ_STORAGE = 0;
    static final int REQUEST_PERMISSION_FOR_WRITE_STORAGE = 9999;

    static final int REQUEST_IMAGE_CAPTURE = 2;
    static final int REQUEST_IMAGE_PICKER = 100;

    private ArrayList<String> imageURIs;
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
        this.imageURIs = new ArrayList<>();



        // Add the action to start the camera on the phone
        final Button cameraButton = (Button) findViewById(R.id.create_photoscrap_camerabutton);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                if (Build.VERSION.SDK_INT >= 23)
                {
                    // // Request permission for the file system here, to ensure we can read the photo url later
                    if (ContextCompat.checkSelfPermission(AddPhotoscrapActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(AddPhotoscrapActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    {
                        ActivityCompat.requestPermissions(AddPhotoscrapActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_FOR_READ_STORAGE);
                    } else
                    {
                        getImageFromGallery();
                    }

                } else
                {
                    getImageFromGallery();
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
                DatePicker datePicker = (DatePicker) findViewById(R.id.create_scrap_date_picker);

                // Perform checks
                if (photoURI == null)
                {
                    Toast.makeText(AddPhotoscrapActivity.this, "Please select a photo", Toast.LENGTH_SHORT).show();
                } else if (selectedScrapbooks.isEmpty())
                {
                    Toast.makeText(AddPhotoscrapActivity.this, "Please select some scrapbooks", Toast.LENGTH_SHORT).show();
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

                            // URI may require converting if it is of type content://
                            scrap.setPhotoUri(photoURI.toString());

                            // Dump the contents of imageUris into the scrapbook ones.
                            for (String uri : imageURIs)
                            {
                                scrap.addImage(uri);
                            }


                            scrap.setAttachedScrapbooks(0); // Initialise

                            // Add the tags
                            String[] tokens = new String[10];
                            for (String t : tokens)
                            {

                                Tag tag = realm.where(Tag.class).equalTo("tagName", t.trim()).findFirst();

                                if (tag == null)
                                {
                                    Log.d("Add Spend:", "Found a null tag, attempting to add");
                                    // Create if not null
                                    tag = realm.createObject(Tag.class, t.trim());
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

    // Fires when the user clicks the dialog box that requests permission
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        switch (requestCode)
        {
            case REQUEST_PERMISSION_FOR_READ_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    getImageFromGallery();
                }
                return;
            case REQUEST_PERMISSION_FOR_WRITE_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    // We have permission now, do the thing.

                }
                return;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case REQUEST_IMAGE_PICKER:

                if (resultCode == RESULT_OK)
                {
                    // Clear previous images
                    this.imageURIs = new ArrayList<>();

                    // If user has picked a single image
                    if (data.getData() != null)
                    {
                        Uri selectedImage = data.getData();

                        ImageView well = (ImageView) findViewById(R.id.create_photoscrap_imagewell);
                        well.setImageURI(selectedImage);
                        photoURI = selectedImage;
                        this.imageURIs.add(photoURI.toString());
                        contentURIToFileURI(photoURI);

                    }

                    // If user has picked multiple images
                    if (data.getClipData() != null)
                    {
                        Item firstItem = data.getClipData().getItemAt(0);
                        Uri uri = firstItem.getUri();
                        photoURI = uri;

                        ImageView well = (ImageView) findViewById(R.id.create_photoscrap_imagewell);
                        well.setImageURI(uri);

                        for(int i = 0; i < data.getClipData().getItemCount(); i++)
                        {
                            Item item = data.getClipData().getItemAt(i);
                            this.imageURIs.add(item.getUri().toString());
                            Log.d("Uri to List", item.getUri().toString());

                            final int takeFlags = data.getFlags()
                                    & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                            // Check for the freshest data.
                            getContentResolver().takePersistableUriPermission(uri, takeFlags);
                        }


                    }

                    Toast.makeText(this, "imageUris: " + this.imageURIs.size(), Toast.LENGTH_SHORT).show();


                }

                break;
        }
    }

    private String contentURIToFileURI(Uri uri)
    {
        if (uri != null && "content".equals(uri.getScheme()))
        {
            Cursor cursor = this.getContentResolver().query(uri, new String[]{android.provider.MediaStore.Images.ImageColumns.DATA}, null, null, null);
            cursor.moveToFirst();
            String filepath = cursor.getString(0);
            cursor.close();
            Log.d("Content->File", "Converted: " + filepath);

            return filepath;
        } else
        {
            Log.d("Content->File", "No conversion needed");
            return uri.toString();
        }
    }

    private void getImageFromGallery()
    {
        // Create the intent to the image
        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        photoPickerIntent.setType("image/*");
        photoPickerIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        photoPickerIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(photoPickerIntent, REQUEST_IMAGE_PICKER);
    }





}
