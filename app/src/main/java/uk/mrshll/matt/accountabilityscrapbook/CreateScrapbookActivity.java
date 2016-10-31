package uk.mrshll.matt.accountabilityscrapbook;

import android.graphics.Color;
import android.graphics.PorterDuff;
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

import java.util.Date;

import io.realm.Realm;
import uk.mrshll.matt.accountabilityscrapbook.model.Scrapbook;

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
                EditText name = (EditText) findViewById(R.id.scrapbook_name);


//                Check name isn't blank
                if (name.getText().toString().matches("")) {
                    Toast.makeText(CreateScrapbookActivity.this, "Please give the scrapbook a name!", Toast.LENGTH_SHORT).show();


                } else {
//                    Make the scrapbook
                    realm.beginTransaction();
                    Scrapbook new_scrapbook = realm.createObject(Scrapbook.class);
                    new_scrapbook.setName(name.getText().toString());
                    new_scrapbook.setDateCreated(new Date());

                    realm.commitTransaction();

//                And then finish, going back to the main menu
                    Toast.makeText(CreateScrapbookActivity.this, "Scrapbook " + name.getText().toString() + " created", Toast.LENGTH_SHORT).show();
                    finish();

                }
            }
        });


//        Set thebutton listener for the choose colour button
        final Button colourBtn = (Button) findViewById(R.id.colour_button);
        colourBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ChromaDialog.Builder().
                        initialColor(Color.BLUE)
                        .colorMode(ColorMode.RGB)
                        .indicatorMode(IndicatorMode.HEX)
                        .onColorSelected(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(@ColorInt int color) {

//                                Set the scrapbook colour
                                scrapbookColour = color;
                                Toast.makeText(CreateScrapbookActivity.this, "Well done on your new colour " + ChromaUtil.getFormattedColorString(color, false), Toast.LENGTH_SHORT).show();
                                colourBtn.setBackgroundColor(color);

                                ImageView indicator = (ImageView) findViewById(R.id.colour_indicator);
                                indicator.setBackgroundT
                            }
                        })
                        .create()
                        .show(getSupportFragmentManager(), "ChromaDialog");
            }
        });

    }


}
