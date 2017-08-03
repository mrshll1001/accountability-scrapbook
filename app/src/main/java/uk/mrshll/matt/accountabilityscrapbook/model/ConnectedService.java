package uk.mrshll.matt.accountabilityscrapbook.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by marshall on 28/11/16.
 */

public class ConnectedService extends RealmObject
{
    @PrimaryKey
    String endpointUrl;
    String apiKey;
    RealmList<Scrap> scrapLog;

    public String getEndpointUrl() {
        return endpointUrl;
    }

    public void setEndpointUrl(String endpointUrl) {
        this.endpointUrl = endpointUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public RealmList<Scrap> getScrapLog() {return  this.scrapLog;}

    public void addScraptoScrapLog(Scrap s) { this.scrapLog.add(s); }

    /**
     * Returns the url with the qa-data endpoint
     * @return
     */
    public String getQADataEndpoint()
    {
        return String.format("%s/qa-data", this.endpointUrl);
    }

    /**
     * Returns the url formatted with the qa-media endpoint
     * @return
     */
    public String getQAMediaEndpoint()
    {
        return String.format("%s/qa-media", this.endpointUrl);
    }
}
