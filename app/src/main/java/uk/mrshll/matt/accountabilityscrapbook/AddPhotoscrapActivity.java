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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import java.util.HashSet;

import io.realm.Realm;
import uk.mrshll.matt.accountabilityscrapbook.Listener.AddScrapListener;
import uk.mrshll.matt.accountabilityscrapbook.Listener.FetchScrapbookDialogListener;
import uk.mrshll.matt.accountabilityscrapbook.Utility.ScrapCreator;
import uk.mrshll.matt.accountabilityscrapbook.Utility.TagManager;
import uk.mrshll.matt.accountabilityscrapbook.model.Scrap;
import uk.mrshll.matt.accountabilityscrapbook.model.Scrapbook;
import uk.mrshll.matt.accountabilityscrapbook.model.Tag;

import static android.content.ClipData.*;
import static android.os.Environment.getExternalStoragePublicDirectory;

public class AddPhotoscrapActivity extends AppCompatActivity {

    static final int REQUEST_PERMISSION_FOR_READ_STORAGE = 0;
    static final int REQUEST_PERMISSION_FOR_WRITE_STORAGE = 9999;

    static final int REQUEST_IMAGE_PICKER = 100;

    private ArrayList<String> imageURIs;
    private ArrayList<String> selectedScrapbooks;
    private HashSet<String> tags;

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photoscrap);

        // Set up variables for database interaction
        this.realm = Realm.getDefaultInstance();
        this.selectedScrapbooks = new ArrayList<String>();
        this.imageURIs = new ArrayList<>();

        /* Set up the AutoComplete box for the tags */
        setUpTagAutoComplete();
        setUpAddTagButton();



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

                // TODO checks and get date

                if (imageURIs == null || imageURIs.size() == 0)
                {
                    Toast.makeText(AddPhotoscrapActivity.this, "Please select some images", Toast.LENGTH_SHORT).show();
                } else if (selectedScrapbooks.isEmpty())
                {
                    Toast.makeText(AddPhotoscrapActivity.this, "Please select some scrapbooks", Toast.LENGTH_SHORT).show();
                } else
                {
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

                    sc.createImageScrap(dateGiven,
                                        imageURIs.toArray(new String[imageURIs.size()]),
                                        tags.toArray(new String[tags.size()]),
                                        selectedScrapbooks.toArray(new String[selectedScrapbooks.size()])
                                        );

                }

                if (imageURIs != null)
                {
                    for (String s : imageURIs)
                    {
                        Log.d("imageURI", s.toString());

                    }
                } else {
                        Log.d("imageUri", "Array is null");
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
                        int takeFlags = data.getFlags();
                        takeFlags &= Intent.FLAG_GRANT_READ_URI_PERMISSION;
                        this.getContentResolver().takePersistableUriPermission(selectedImage, takeFlags);

                        updateImageWell(selectedImage);

                        this.imageURIs.add(selectedImage.toString());

                    }

                    // If user has picked multiple images
                    if (data.getClipData() != null)
                    {
                        Item firstItem = data.getClipData().getItemAt(0);
                        updateImageWell(firstItem.getUri());




                        for(int i = 0; i < data.getClipData().getItemCount(); i++)
                        {
                            Item item = data.getClipData().getItemAt(i);
                            Uri imageUri = item.getUri();
                            int takeFlags = data.getFlags();
                            takeFlags &= Intent.FLAG_GRANT_READ_URI_PERMISSION;
                            this.getContentResolver().takePersistableUriPermission(imageUri, takeFlags);
                            this.imageURIs.add(item.getUri().toString());
                            Log.d("Uri to List", item.getUri().toString());

                        }


                    }


                }

                break;
        }
    }





    private void getImageFromGallery()
    {
        // Create the intent to the image
        Intent photoPickerIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        photoPickerIntent.setType("image/*");
        photoPickerIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        photoPickerIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        photoPickerIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);

        photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(photoPickerIntent, REQUEST_IMAGE_PICKER);
    }

    private void updateImageWell(Uri uri)
    {
        ImageView well = (ImageView) findViewById(R.id.create_photoscrap_imagewell);
        well.setImageURI(uri);
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




    /*LEGACY: Attempts to convert the content URI to a file uri.*/

    //    private String contentURIToFileURI(Uri uri)
//    {
//        Log.d("convertURI" ,"Attemting to convert " + uri.toString());
//        if (uri != null && "content".equals(uri.getScheme()))
//        {
//            String[] projection = {MediaStore.MediaColumns.DATA};
//            String[] oldProjection = {android.provider.MediaStore.Images.ImageColumns.DATA};
//
//            Cursor cursor = this.getContentResolver().query(uri, projection, null, null, null);
//            cursor.moveToFirst();
//            String filepath = cursor.getString(0);
//            cursor.close();
//            Log.d("Content->File", "Converted: " + filepath);
//
//            if (filepath == null || filepath.equals("null"))
//            {
//                Log.d("Content->File", "Equals null, attempting new stuff");
//                try {
//                    String pathsegment[] = uri.getLastPathSegment().split(":");
//                    String id = pathsegment[0];
//                    final String[] imageColumns = { MediaStore.Images.Media.DATA };
//                    final String imageOrderBy = null;
//
//                    Uri newUri = getInternalOrExternalUri();
//                    Cursor imageCursor = this.getContentResolver().query(newUri, imageColumns,
//                            MediaStore.Images.Media._ID + "=" + id, null, null);
//
//                    if (imageCursor.moveToFirst()) {
//                       String value = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA));
//                       Log.d("New attempt", value);
//                    }
//
//                } catch (Exception e) {
//                    Toast.makeText(this, "Failed to get image", Toast.LENGTH_LONG).show();
//                }
//
//            } else
//            {
//
//                return filepath;
//            }
//
//            return filepath;
//        } else
//        {
//            Log.d("Content->File", "No conversion needed");
//            return uri.toString();
//        }
//    }



}
