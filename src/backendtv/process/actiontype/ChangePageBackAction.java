package backendtv.process.actiontype;

import backendtv.server.ServerApp;
import com.fasterxml.jackson.databind.node.ArrayNode;

public final class ChangePageBackAction implements ActionCommand {
    public ChangePageBackAction() {
        // for now nothing
    }

    @Override
    public void execute(final ArrayNode output) {
        System.out.println("Executed back action");
        final var server = ServerApp.connect();
        final var client = server.fetchActiveClient();

        final var parserObject = output.objectNode();
        if (client.getStatus()) {
            System.out.println("Client is active");
            if (!client.changePageBack()) {
                System.out.println("Could not change the page because no page was found");
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
