package backendtv.process.actiontype.pagesactions;

import backendtv.parser.JsonParser;
import backendtv.process.actiontype.ActionCommand;
import backendtv.server.ServerApp;
import com.fasterxml.jackson.databind.node.ArrayNode;

public final class ChangePageBackAction implements ActionCommand {
    public ChangePageBackAction() {
        // for now nothing
    }

    @Override
    public void execute(final ArrayNode output) {
        final var server = ServerApp.connect();
        final var client = server.fetchActiveClient();

        final var parserObject = output.objectNode();
        if (client.getStatus()) {
            if (!client.changePageBack()) {
                JsonParser.parseBasicError(parserObject);
                output.add(parserObject);
            }
        } else {
            JsonParser.parseBasicError(parserObject);
            output.add(parserObject);
        }
    }
}
