package backendtv.process.actiontype;

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
                parserObject.put("error", "Error");
                parserObject.putArray("currentMoviesList");
                parserObject.putNull("currentUser");
                output.add(parserObject);
            }
        } else {
            System.out.println("Client is not active");
            parserObject.put("error", "Error");
            parserObject.putArray("currentMoviesList");
            parserObject.putNull("currentUser");
            output.add(parserObject);
        }
    }
}
