package uk.mrshll.matt.accountabilityscrapbook.model;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by matt on 27/10/16.
 * Scrapbook model.
 */

public class Scrapbook extends RealmObject
{
    private String name;        // Name

    private Date dateCreated;   // Date the scrapbook was registered
    private int colour;      // Colour used to sort the scrapbook
    public RealmList<Tag> tagList; // All dem tags it's been tagged with

    private RealmList<SpendScrap> spendList; // All the spends it has
    private RealmList<PhotoScrap> photoList; // All the photos it has



    public RealmList<PhotoScrap> getPhotoList() {
        return photoList;
    }

    public void setPhotoList(RealmList<PhotoScrap> photoList) {
        this.photoList = photoList;
    }

    /**
     * Returns name
     * @return
     */
    public String getName() {
        return name;
    }

    public void setTagList(RealmList<Tag> tagList) {
        this.tagList = tagList;
    }

    public RealmList<SpendScrap> getSpendList() {
        return spendList;
    }

    public void setSpendList(RealmList<SpendScrap> spendList) {
        this.spendList = spendList;
    }

    /**
     * Sets the name
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the date created
     * @return
     */
    public Date getDateCreated() {
        return dateCreated;
    }

    /**
     * Returns the colour string
     * @return
     */
    public int getColour() {
        return colour;
    }

    /**
     * Sets colopur
     * @param colour
     */
    public void setColour(int colour) {
        this.colour = colour;
    }

    /**
     * Sets the date (expects Date object)
     * @param dateCreated
     */
    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    /**
     * Adds a tag to the database
     * @param tag
     */
    public void addTag(Tag tag)
    {
        this.tagList.add(tag);
    }

    /**
     * Returns the tag list
     * @return
     */
    public RealmList<Tag> getTagList()
    {
        return this.tagList;
    }

    /**
     * Returns a total of all of the items
     * @return
     */
    public int getTotalItems()
    {
        return spendList.size() + photoList.size();
    }
}
