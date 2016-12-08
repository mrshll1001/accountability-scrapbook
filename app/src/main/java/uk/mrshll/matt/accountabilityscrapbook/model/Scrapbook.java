package uk.mrshll.matt.accountabilityscrapbook.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.realm.Realm;
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

    public RealmList<Scrap> scrapList; // All the scrapss



    /**
     * Returns name
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Returns access to the scraplist
     * @return
     */
    public RealmList<Scrap> getScrapList()
    {
        return this.scrapList;
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
        return this.scrapList.size();
    }

    /**
     * Returns a list of scraps filtered by a type argument
     * @param type
     * @return
     */
    public RealmList<Scrap> getScrapListByType(int type)
    {
        RealmList<Scrap> list = new RealmList<>();

        for (Scrap s : this.scrapList)
        {
            if (s.getType() == type)
            {
                list.add(s);
            }
        }

        return list;
    }

    /**
     * Removes duplicates
     * @return
     */
    public static HashSet<Scrap> listsToSet(List<Scrap> list)
    {
        return new HashSet<>(list);
    }
}
