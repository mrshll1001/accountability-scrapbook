package uk.mrshll.matt.accountabilityscrapbook;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import io.realm.Realm;
import io.realm.RealmResults;
import uk.mrshll.matt.accountabilityscrapbook.model.Scrap;

public class ArchiveActivity extends AppCompatActivity
{

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);

        // Initiate realm
        this.realm = Realm.getDefaultInstance();

        // Run the realm query to find all scraps without associations


    }
}
