package uk.mrshll.matt.accountabilityscrapbook;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import java.util.ArrayList;

import io.realm.Realm;
import uk.mrshll.matt.accountabilityscrapbook.Listener.FetchScrapbookDialogListener;

public class AddQuotescrapActivity extends AppCompatActivity {

    private Realm realm;
    private ArrayList<String> selectedScrapbooks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_quotescrap);

        // Initialise
        this.realm = Realm.getDefaultInstance();
        this.selectedScrapbooks = new ArrayList<String>();

        // Set activity for the select scrapbooks button
        Button scrapbooksButton = (Button) findViewById(R.id.create_scrap_scrapbook_button);
        scrapbooksButton.setOnClickListener(new FetchScrapbookDialogListener(this, this.realm, this.selectedScrapbooks));

        // Set activity for the done button
        
    }
}
