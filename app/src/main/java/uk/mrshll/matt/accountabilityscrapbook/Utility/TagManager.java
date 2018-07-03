package uk.mrshll.matt.accountabilityscrapbook.Utility;

import android.util.Log;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import uk.mrshll.matt.accountabilityscrapbook.model.Tag;

public class TagManager
{
    private Realm realm;


    public TagManager(Realm realm)
    {
        this.realm = realm;
    }


    public String[] getTagsAsStringArray()
    {
        RealmResults<Tag> results = this.realm.where(Tag.class).findAll(); // Get all tags from Realm and populate an array
        ArrayList<String> tagList = new ArrayList<>();
        for (Tag t : results)
        {
            tagList.add(t.getTagName());
            Log.d("Added Tag", t.getTagName());
        }
        return tagList.toArray(new String[tagList.size()]);
    }
}
