package uk.mrshll.matt.accountabilityscrapbook;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;
import uk.mrshll.matt.accountabilityscrapbook.Listener.FetchScrapbookDialogListener;
import uk.mrshll.matt.accountabilityscrapbook.model.Scrapbook;

public class AddSpendscrapActivity extends AppCompatActivity {

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_spendscrap);

        // Initialise realm
        this.realm = Realm.getDefaultInstance();

        // Get the button from the view
        Button addScrapbooks = (Button) findViewById(R.id.create_spendscrap_scrapbook_button);
        // Add the alert dialogue checkbox
        addScrapbooks.setOnClickListener(new FetchScrapbookDialogListener(this));

    }
}
