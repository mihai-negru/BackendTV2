package backendtv.process.actiontype.adminactions;

import backendtv.storage.Database;
import datafetch.ActionFetch;

public interface DatabaseStrategy {
    boolean apply();
}
