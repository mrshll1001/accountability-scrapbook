package uk.mrshll.matt.accountabilityscrapbook.model;

import io.realm.RealmObject;

/**
 * Created by matt on 27/10/16.
 */

public class Scrapbook extends RealmObject
{
    /**
     * Name of the Scrapbook
     */
    private String name;

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
}
