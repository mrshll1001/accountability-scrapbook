package uk.mrshll.matt.accountabilityscrapbook.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by marshall on 02/11/16.
 */

public class Tag extends RealmObject
{
    @PrimaryKey
    private String tagName;

    /**
     * Return tag name
     * @return
     */
    public String getTagName()
    {
        return this.tagName;
    }

    public void setTagName(String tagName)
    {
        this.tagName = tagName;
    }

}
