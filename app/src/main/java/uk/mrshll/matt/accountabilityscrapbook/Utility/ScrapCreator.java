package uk.mrshll.matt.accountabilityscrapbook.Utility;

import android.content.Context;
import android.util.Log;

import java.util.Date;

import io.realm.Realm;
import io.realm.Realm.Transaction;
import uk.mrshll.matt.accountabilityscrapbook.Listener.AddScrapListener;
import uk.mrshll.matt.accountabilityscrapbook.model.Scrap;
import uk.mrshll.matt.accountabilityscrapbook.model.Scrapbook;
import uk.mrshll.matt.accountabilityscrapbook.model.Tag;

public class ScrapCreator
{
    private Realm realm;
    private AddScrapListener listener;

    public ScrapCreator(Realm realm, AddScrapListener listener) {
        this.realm = realm;
        this.listener = listener;
    }

    public void createSpendScrap(final String name, final Double spendValue, final Date dateGiven, final String[] tags, final String[] selectedScrapbooks)
    {
        realm.executeTransaction(new Transaction(){

            @Override
            public void execute(Realm realm)
            {
                Scrap scrap = realm.createObject(Scrap.class);
                scrap.setName(name);
                scrap.setSpendValue(0.00 - spendValue);
                scrap.setDateCreated(new Date());
                scrap.setDateGiven(dateGiven);
                scrap.setType(Scrap.TYPE_SPEND);
                scrap.setAttachedScrapbooks(0);

                addTags(scrap, tags);

                // Add the scrap to the scrapbooks
                for (String s : selectedScrapbooks)
                {
                    Scrapbook result = realm.where(Scrapbook.class).equalTo("name", s).findFirst();

                    // Inherit the tags from the scrapbooks
                    scrap.getInheritedTags().addAll(result.getTagList());

                    result.getScrapList().add(scrap);
                    scrap.setAttachedScrapbooks(scrap.getAttachedScrapbooks() + 1); // Increment by 1

                }

                listener.realmSuccess();

            }
        });
    }



    private void addTags(Scrap s, String[] tags)
    {
        for (String t : tags)
        {

            Tag tag = realm.where(Tag.class).equalTo("tagName", t).findFirst();

            if (tag == null)
            {
                Log.d("Add Spend:", "Found a null tag, attempting to add");
                // Create if not null
                tag = realm.createObject(Tag.class, t);
            }

            s.getCustomTags().add(tag);
        }
    }
}
