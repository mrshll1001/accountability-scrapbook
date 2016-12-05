package uk.mrshll.matt.accountabilityscrapbook.model;

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
}
