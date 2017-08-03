package uk.mrshll.matt.accountabilityscrapbook.Listener;

import uk.mrshll.matt.accountabilityscrapbook.model.ConnectedService;
import uk.mrshll.matt.accountabilityscrapbook.model.Scrap;

/**
 * Created by marshall on 09/05/17.
 */

public interface ScrapShareLogUpdater
{
    void markScrapAsSharedWithService(Scrap s, ConnectedService c);
}
