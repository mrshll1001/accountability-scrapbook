package uk.mrshll.matt.accountabilityscrapbook.model;

import io.realm.RealmObject;

/**
 * Created by marshall on 10/11/16.
 */

public class SpendScrap extends RealmObject
{
    private String name;
    private double value;



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
}
