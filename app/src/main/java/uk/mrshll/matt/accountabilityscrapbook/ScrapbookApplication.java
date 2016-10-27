package uk.mrshll.matt.accountabilityscrapbook;

import android.app.Application;

/**
 * Created by matt on 27/10/16.
 * Realm said that it's good to initialise the realm database in a subclass of Application, so here we are
 */

import io.realm.Realm;

public class ScrapbookApplication extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        Realm.init(this);
    }
}
