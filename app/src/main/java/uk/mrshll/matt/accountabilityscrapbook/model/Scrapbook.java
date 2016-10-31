package uk.mrshll.matt.accountabilityscrapbook.model;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by matt on 27/10/16.
 * Scrapbook model.
 */

public class Scrapbook extends RealmObject
{
    private String name;        // Name
    private Date dateCreated;   // Date the scrapbook was registered
    private int colour;      // Colour used to sort the scrapbook

    /**
     * Returns name
     * @return
     */
    public String getName() {
        return name;
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
     * Returns the colour string (expects hex)
     * @return
     */
    public int getColour() {
        return colour;
    }

    /**
     * Sets colopur (expects hex)
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
}
