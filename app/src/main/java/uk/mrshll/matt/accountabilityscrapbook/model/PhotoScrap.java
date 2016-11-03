package uk.mrshll.matt.accountabilityscrapbook.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by marshall on 03/11/16.
 */

public class PhotoScrap extends RealmObject
{

    private Date dateCreated; // Date created using the app, used for primary key as always unique (seconds go forwards)
    private Date dateGiven; // Date given by the user for the item

    @PrimaryKey
    private String filepath; // Filepath for the associated image


}
