package uk.mrshll.matt.accountabilityscrapbook.model;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by marshall on 15/11/16.
 */

public class QuoteScrap extends RealmObject
{

    private String quoteText; // NOTE: this isn't a primary key because some people might say the same things e.g. "brilliant!"
    private String quoteSource;

    private Date dateCreated;
    private Date dateGiven;

    private RealmList<Tag> inheritedTags;
    private RealmList<Tag> customTags;

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

    public RealmList<Tag> getCustomTags() {
        return customTags;
    }
}
