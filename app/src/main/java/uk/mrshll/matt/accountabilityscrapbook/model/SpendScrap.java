package uk.mrshll.matt.accountabilityscrapbook.model;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by marshall on 10/11/16.
 */

public class SpendScrap extends RealmObject
{
    private String name;
    private double value;
    private Date dateOfSpend;
    private Date dateCreated;

    private RealmList<Tag> inheritedTags;
    private RealmList<Tag> customTags;

    public RealmList<Tag> getInheritedTags() {
        return inheritedTags;
    }


    public RealmList<Tag> getCustomTags() {
        return customTags;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Date getDateOfSpend() {
        return dateOfSpend;
    }

    public void setDateOfSpend(Date dateOfSpend) {
        this.dateOfSpend = dateOfSpend;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }
}
