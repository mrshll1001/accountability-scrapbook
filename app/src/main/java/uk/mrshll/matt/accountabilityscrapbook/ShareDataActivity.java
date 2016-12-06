package uk.mrshll.matt.accountabilityscrapbook;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import java.util.ArrayList;

import io.realm.Realm;
import uk.mrshll.matt.accountabilityscrapbook.Listener.FetchScrapbookDialogListener;
import uk.mrshll.matt.accountabilityscrapbook.model.Scrapbook;

public class ShareDataActivity extends AppCompatActivity
{

    private Realm realm;
    private ArrayList<String> selectedScrapbooks;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_data);

        // Set up
        this.realm = realm.getDefaultInstance();
        this.selectedScrapbooks = new ArrayList<>();

        Button scrapbooksButton = (Button) findViewById(R.id.share_data_scrapbooks_button);
        scrapbooksButton.setOnClickListener(new FetchScrapbookDialogListener(this, realm, selectedScrapbooks));



    }
}
