package uk.mrshll.matt.accountabilityscrapbook.Utility;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

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

    public void createImageScrap(final Date dateGiven, final String[] imageUris, final String[] tags, final String[] selectedScrapbooks )
    {
        realm.executeTransaction(new Transaction()
        {
            @Override
            public void execute(Realm realm) {
                Scrap scrap = realm.createObject(Scrap.class);
                scrap.setDateCreated(new Date());
                scrap.setDateGiven(dateGiven);
                scrap.setType(Scrap.TYPE_PHOTO);


                for (String imageUri : imageUris)
                {
                    scrap.addImage(imageUri);
                }

                scrap.setAttachedScrapbooks(0); // This is important so it has an initial value

                addTags(scrap, tags);
                addToScrapbooks(scrap, selectedScrapbooks);


                listener.realmSuccess();


            }
        });
    }

    public void createEventScrap(final Date dateGiven, final String eventName, final String placeAddress, final String placeName, final LatLng latLng, final String[] tags, final String[] selectedScrapbooks)
    {
        realm.executeTransaction(new Transaction()
        {
            @Override
            public void execute(Realm realm) {
                Scrap scrap = realm.createObject(Scrap.class);
                scrap.setDateCreated(new Date());
                scrap.setDateGiven(dateGiven);
                scrap.setType(Scrap.TYPE_EVENT);
                scrap.setName(eventName);
                scrap.setPlaceAddress(placeAddress);
                scrap.setPlaceName(placeName);

                scrap.setPlaceLatitude(String.valueOf(latLng.latitude));
                scrap.setPlaceLongitude(String.valueOf(latLng.longitude));
                scrap.setAttachedScrapbooks(0); // This is important so it has an initial value

                addTags(scrap, tags);
                addToScrapbooks(scrap, selectedScrapbooks);


                listener.realmSuccess();


            }
        });
    }

    public void createQuoteScrap(final Date dateGiven, final String quoteText, final String quoteSource, final String[] tags, final String[] selectedScrapbooks)
    {
        realm.executeTransaction(new Transaction()
        {
            @Override
            public void execute(Realm realm) {
                Scrap scrap = realm.createObject(Scrap.class);
                scrap.setDateGiven(dateGiven);
                scrap.setDateCreated(new Date());
                scrap.setQuoteText(quoteText);
                scrap.setQuoteSource(quoteSource);
                scrap.setType(Scrap.TYPE_QUOTE);
                scrap.setAttachedScrapbooks(0);

                addTags(scrap, tags);
                addToScrapbooks(scrap, selectedScrapbooks);


                listener.realmSuccess();


            }
        });
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
                addToScrapbooks(scrap, selectedScrapbooks);


                listener.realmSuccess();

            }
        });
    }

    private void addToScrapbooks(Scrap scrap, String[] selectedScrapbooks)
    {
        for (String s : selectedScrapbooks)
        {
            Scrapbook result = realm.where(Scrapbook.class).equalTo("name", s).findFirst();

            // Inherit the tags from the scrapbooks
            scrap.getInheritedTags().addAll(result.getTagList());

            result.getScrapList().add(scrap);
            scrap.setAttachedScrapbooks(scrap.getAttachedScrapbooks() + 1); // Increment by 1

        }
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
