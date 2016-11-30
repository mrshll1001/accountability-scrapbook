package uk.mrshll.matt.accountabilityscrapbook;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

import io.realm.Realm;
import uk.mrshll.matt.accountabilityscrapbook.Listener.FetchScrapbookDialogListener;

public class EmailAccountsActivity extends AppCompatActivity
{

    private Realm realm;
    private ArrayList<String> selectedScrapbooks;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_accounts);

        // Set up realm
        realm = Realm.getDefaultInstance();
        selectedScrapbooks = new ArrayList<>();

        // Get the choose scrapbooks button and apply the click listener to store scrapbooks
        Button scrapbooks = (Button) findViewById(R.id.generate_accounts_scrapbook_button);
        scrapbooks.setOnClickListener(new FetchScrapbookDialogListener(this, realm, selectedScrapbooks));

        // Generate button
        Button generate = (Button) findViewById(R.id.generate_accounts_generate_button);
        generate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Toast.makeText(EmailAccountsActivity.this, selectedScrapbooks.toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }


}
