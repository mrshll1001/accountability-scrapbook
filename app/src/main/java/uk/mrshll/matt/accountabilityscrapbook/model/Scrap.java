package uk.mrshll.matt.accountabilityscrapbook.model;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Generic Scrap class to replace all of the distinct object types.
 * This allows everything to be collated, and allows the data structure flexibility to
 * encapsulate more contexts, together, in the future.
 *
 * Created by marshall on 18/11/16.
 */

public class Scrap extends RealmObject
{
    // Type definitions
    public static int TYPE_SPEND = 0;
    public static int TYPE_QUOTE = 1;
    public static int TYPE_EVENT = 2;
    public static int TYPE_PHOTO = 4;

    // Meta. Type and date
    private Date dateCreated;               // Date created on system
    private Date dateGiven;                 // Date given by the user, so that they can retrotag it
    private int type;                       // Type, used for interface stuff.

    // Lists of tags
    private RealmList<Tag> inheritedTags;   // Tags inherited from parent scrapbooks
    private RealmList<Tag> customTags;      // Tags individual to the Scrap

    // Number of Scrapbooks
    private int attachedScrapbooks;         // Maintain a number of Scrapbooks it's affiliated with in order to make orphan detection easy

    // Independent fields
    private String name;                    // Name of the item

    private double spendValue;              // If item is a spend type, this is its value

    private String quoteText;               // If item is a quote, this is its content
    private String quoteSource;             // If item is a quote, this its speaker/origin
    private String placeLatitude;           // If item is an event, this is the address
    private String placeLongitude;          // If item is an event, this is the address
    private String placeName;               // If item is an event, this is the address
    private String placeAddress;            // If item is an event, this is the address

    private String photoUri;                // If item is a photo, this is the uro of the file


    public RealmList<Tag> getInheritedTags() {
        return inheritedTags;
    }

    public RealmList<Tag> getCustomTags() {
        return customTags;
    }

    public HashSet<Tag> getAllUniqueTags()
    {
        HashSet<Tag> tagSet = new HashSet<>();

        tagSet.addAll(getInheritedTags());
        tagSet.addAll(getCustomTags());

        return tagSet;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateGiven() {
        return dateGiven;
    }

    public void setDateGiven(Date dateGiven) {
        this.dateGiven = dateGiven;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getSpendValue() {
        return spendValue;
    }

    public void setSpendValue(double spendValue) {
        this.spendValue = spendValue;
    }

    public String getQuoteText() {
        return quoteText;
    }

    public void setQuoteText(String quoteText) {
        this.quoteText = quoteText;
    }

    public String getQuoteSource() {
        return quoteSource;
    }

    public void setQuoteSource(String quoteSource) {
        this.quoteSource = quoteSource;
    }

    public String getPlaceLatitude() {
        return placeLatitude;
    }

    public void setPlaceLatitude(String latitude) {
        this.placeLatitude = latitude;
    }

    public String getPlaceLongitude() { return placeLongitude; }

    public void setPlaceLongitude(String longitude) { this.placeLongitude = longitude;}

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public String getPlaceAddress() {
        return placeAddress;
    }

    public void setPlaceAddress(String placeAddress) {
        this.placeAddress = placeAddress;
    }

    public int getAttachedScrapbooks() {
        return attachedScrapbooks;
    }

    public void setAttachedScrapbooks(int attachedScrapbooks) {
        this.attachedScrapbooks = attachedScrapbooks;
    }


    /**
     * Returns a datetime in the format yyyy-mm-dd-hhmm, used for transaction ids
     * @return
     */
    public String getDateCreatedAsTransactionID()
    {
        return String.format("%d-%d-%d-%d%d", dateCreated.getYear(), dateCreated.getMonth()+1, dateCreated.getDate(), dateCreated.getHours(), dateCreated.getMinutes());
    }

    /**
     * Returns a formatted string comprised of all tags
     * @return
     */
    public String getFormattedTagString(boolean includeInherited, boolean includeCustom)
    {
        ArrayList<Tag> tagList = new ArrayList<>();

        if (includeCustom)
        {
            tagList.addAll(this.getCustomTags());
        }

        if (includeInherited)
        {
            tagList.addAll(this.getInheritedTags());
        }

        // Build the tag string
        StringBuilder builder = new StringBuilder();
        for (Tag t : tagList)
        {
            builder.append(t.getAsHashtag());
            if(tagList.indexOf(t) != tagList.size() - 1)
            {
                builder.append(", ");
            }
        }

        return builder.toString();

    }

    /**
     * Returns a formatted date string for the object in dd/mm/yyyy
     * @param date
     * @return
     */
    public String getFormattedDateString(Date date)
    {
       return String.format("%d/%d/%d", date.getDate(), date.getMonth() + 1, date.getYear());

    }

}
