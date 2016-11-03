package uk.mrshll.matt.accountabilityscrapbook;

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
import java.util.Calendar;
import java.util.Date;

public class AddPhotoscrapActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 2;
    String currentPhotoPath;

    Uri photoURI;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photoscrap);

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



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
        {

            ImageView well = (ImageView) findViewById(R.id.create_photoscrap_imagewell);

            well.setImageURI(photoURI);
            Toast.makeText(this, photoURI.toString() + " captured, good job!", Toast.LENGTH_SHORT).show();
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
