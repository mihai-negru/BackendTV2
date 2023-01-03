package backendtv.process.actiontype;

import backendtv.pagestype.PageType;
import backendtv.server.ServerApp;
import com.fasterxml.jackson.databind.node.ArrayNode;
import datafetch.ActionFetch;

import java.util.Arrays;

public class SubscribeAction implements ActionCommand {
    private final String selectedGenre;
    public SubscribeAction(final ActionFetch actionInfo) {
        selectedGenre = actionInfo.getSubscribedGenre();
    }

    @Override
    public void execute(final ArrayNode output) {
        final var server = ServerApp.connect();
        final var client = server.fetchActiveClient();
        final var database = server.fetchDatabase();

        final var parserObject = output.objectNode();
        if (client.getLoadedPage() != PageType.DETAILS) {
            parserObject.put("error", "Error");
            parserObject.putArray("currentMoviesList");
            parserObject.putNull("currentUser");
            output.add(parserObject);
        } else {
            final var movieInfo = database.collection("movies").findOne("name", client.getSeeMovie());

            if (movieInfo == null) {
                System.out.println("Movie was not found which is odd");
            } else {
                final var movieGenres = Arrays.asList(movieInfo.get("genres").split(","));

                if (movieGenres.contains(selectedGenre)) {
                    // Here the bitch subscribes to the genres
                    client.subscribeToGenre(selectedGenre);
                } else {
                    parserObject.put("error", "Error");
                    parserObject.putArray("currentMoviesList");
                    parserObject.putNull("currentUser");
                    output.add(parserObject);
                }
            }
        }
    }
}
