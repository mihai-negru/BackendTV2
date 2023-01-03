package backendtv.process.actiontype;

import com.fasterxml.jackson.databind.node.ArrayNode;
import datafetch.ActionFetch;

public final class AdminDatabaseAction implements ActionCommand {
    public AdminDatabaseAction(final ActionFetch actionInfo) {
        // For now nothing
    }

    @Override
    public void execute(final ArrayNode output) {

    }
}
