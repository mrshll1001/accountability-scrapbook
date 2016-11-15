package uk.mrshll.matt.accountabilityscrapbook.model;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by marshall on 03/11/16.
 */

public class PhotoScrap extends RealmObject
{
    @PrimaryKey
    private String photoURI; // Filepath for the associated image

    private Date dateCreated; // Date created using the app, used for primary key as always unique (seconds go forwards)
    private Date dateGiven; // Date given by the user for the item

    private RealmList<Tag> inheritedTags;
    private RealmList<Tag> customTags;


    
    public String getPhotoURI() {
        return photoURI;
    }

    public void setPhotoURI(String photoURI) {
        this.photoURI = photoURI;
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

    public RealmList<Tag> getInheritedTags() {
        return inheritedTags;
    }

    public void setInheritedTags(RealmList<Tag> inheritedTags) {
        this.inheritedTags = inheritedTags;
    }

    public RealmList<Tag> getCustomTags() {
        return customTags;
    }

    public void setCustomTags(RealmList<Tag> customTags) {
        this.customTags = customTags;
    }
}
