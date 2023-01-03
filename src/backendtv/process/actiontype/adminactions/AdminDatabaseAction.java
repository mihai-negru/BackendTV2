package backendtv.process.actiontype.adminactions;

import backendtv.parser.JsonParser;
import backendtv.process.actiontype.ActionCommand;
import backendtv.server.ServerApp;
import com.fasterxml.jackson.databind.node.ArrayNode;
import datafetch.ActionFetch;

public final class AdminDatabaseAction implements ActionCommand {
    private final DatabaseStrategy operation;

    public AdminDatabaseAction(final ActionFetch actionInfo) {
        operation = switch (actionInfo.getFeature().toLowerCase()) {
            case "add" -> new AddDatabaseOperation(actionInfo);
            case "delete" -> new DeleteDatabaseOperation(actionInfo);
            default -> null;
        };
    }

    @Override
    public void execute(final ArrayNode output) {
        final var parserObject = output.objectNode();
        if (operation == null) {
            JsonParser.parseBasicError(parserObject);
            output.add(parserObject);
        } else {
            if (!operation.apply()) {
                JsonParser.parseBasicError(parserObject);
                output.add(parserObject);
            }
        }
    }
}
