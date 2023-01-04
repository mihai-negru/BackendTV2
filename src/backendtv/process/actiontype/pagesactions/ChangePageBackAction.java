package backendtv.process.actiontype.pagesactions;

import backendtv.pagestype.PageType;
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
            } else {
                final var clientPage = client.getLoadedPage();

                if (clientPage == PageType.MOVIES) {
                    parserObject.putNull("error");
                    JsonParser.parseAvailableMovies(parserObject.putArray("currentMoviesList"));
                    JsonParser.parseClient(parserObject);

                    output.add(parserObject);
                } else if (clientPage == PageType.DETAILS) {
                    parserObject.putNull("error");
                    JsonParser.parseMovie(parserObject.putArray("currentMoviesList"), "name", client.getSeeMovie());
                    JsonParser.parseClient(parserObject);

                    output.add(parserObject);
                }
            }
        } else {
            JsonParser.parseBasicError(parserObject);
            output.add(parserObject);
        }
    }
}
