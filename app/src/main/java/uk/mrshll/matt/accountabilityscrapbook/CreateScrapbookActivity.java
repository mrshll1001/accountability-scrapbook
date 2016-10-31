package uk.mrshll.matt.accountabilityscrapbook;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Date;

import io.realm.Realm;
import uk.mrshll.matt.accountabilityscrapbook.model.Scrapbook;

public class CreateScrapbookActivity extends AppCompatActivity {

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Preamble
        setTitle("Add Scrapbook");
        realm = Realm.getDefaultInstance();

        setContentView(R.layout.activity_create_scrapbook);

//        Set the button listener for the done button
        Button doneBtn = (Button) findViewById(R.id.complete_button);
        doneBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
//                Get the data for the scrapbook
                EditText name = (EditText) findViewById(R.id.scrapbook_name);


//                Check name isn't blank
                if (name.getText().toString().matches(""))
                {
                    Toast.makeText(CreateScrapbookActivity.this, "Please give the scrapbook a name!", Toast.LENGTH_SHORT).show();


                } else
                {
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

    }
}
