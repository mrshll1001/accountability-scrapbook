package uk.mrshll.matt.accountabilityscrapbook;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.pavelsikun.vintagechroma.ChromaDialog;
import com.pavelsikun.vintagechroma.ChromaUtil;
import com.pavelsikun.vintagechroma.IndicatorMode;
import com.pavelsikun.vintagechroma.OnColorSelectedListener;
import com.pavelsikun.vintagechroma.colormode.ColorMode;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;
import uk.mrshll.matt.accountabilityscrapbook.model.Scrapbook;
import uk.mrshll.matt.accountabilityscrapbook.model.Tag;

public class CreateScrapbookActivity extends AppCompatActivity {

    private Realm realm;
    private int scrapbookColour;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Preamble
        setTitle("Add Scrapbook");
        scrapbookColour = R.color.colorPrimaryDark;
        realm = Realm.getDefaultInstance();

        setContentView(R.layout.activity_create_scrapbook);

//        Set the button listener for the done button
        Button doneBtn = (Button) findViewById(R.id.complete_button);
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Get the data for the scrapbook
                final EditText name = (EditText) findViewById(R.id.scrapbook_name);
                final EditText tags = (EditText) (findViewById(R.id.scrapbook_add_tags));


//                Check name isn't blank
                if (name.getText().toString().matches(""))
                {
                    Toast.makeText(CreateScrapbookActivity.this, "Please give the scrapbook a name!", Toast.LENGTH_SHORT).show();


                } else if (tags.getText().toString().matches(""))
                {
                    Toast.makeText(CreateScrapbookActivity.this, "Please enter some tags!", Toast.LENGTH_SHORT).show();
                } else
                {
                    realm.executeTransactionAsync(new Realm.Transaction(){
                        @Override
                        public void execute(Realm realm)
                        {
                            Scrapbook scrapbook = realm.createObject(Scrapbook.class);
                            scrapbook.setName(name.getText().toString());
                            scrapbook.setDateCreated(new Date());
                            scrapbook.setColour(scrapbookColour);

                            // Begin the tags
                            String[] tokens  = tags.getText().toString().split(" ");
                            for (String t : tokens)
                            {
                                Tag tag = realm.createObject(Tag.class);
                                tag.setTagName("#"+t);
                                scrapbook.tagList.add(tag);
                            }

                        }
                    }, new Realm.Transaction.OnSuccess()
                    {
                        public void onSuccess()
                        {
                            // And then finish, going back to the main menu AND PASSING THE DATA BACK TO REDRAW THE LIST
                            Intent returnIntent = new Intent();
                            setResult(Activity.RESULT_OK, returnIntent);

                            finish();
                            Toast.makeText(CreateScrapbookActivity.this, "Scrapbook " + name.getText().toString() + " created", Toast.LENGTH_SHORT).show();
                        }

                    });



                }
            }
        });


//        Set thebutton listener for the choose colour button
        final Button colourBtn = (Button) findViewById(R.id.colour_button);
        colourBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorDrawable btnColor = (ColorDrawable) colourBtn.getBackground();

                new ChromaDialog.Builder().
                        initialColor(btnColor.getColor())
                        .colorMode(ColorMode.RGB)
                        .indicatorMode(IndicatorMode.HEX)
                        .onColorSelected(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(@ColorInt int color) {

//                                Set the scrapbook colour
                                scrapbookColour = color;
                                colourBtn.setBackgroundColor(color);
                            }
                        })
                        .create()
                        .show(getSupportFragmentManager(), "ChromaDialog");
            }
        });

    }


}
