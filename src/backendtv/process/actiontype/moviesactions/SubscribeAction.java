package backendtv.process.actiontype.moviesactions;

import backendtv.pagestype.PageType;
import backendtv.parser.JsonParser;
import backendtv.process.actiontype.ActionCommand;
import backendtv.server.ServerApp;
import com.fasterxml.jackson.databind.node.ArrayNode;
import datafetch.ActionFetch;

import java.util.Arrays;

/**
 * <p>Specific Command class to subscribe to a specific
 * genre of a film for the active client.</p>
 *
 * @since 2.0.0
 * @author Mihai Negru
 */
public final class SubscribeAction implements ActionCommand {
    private final String selectedGenre;

    /**
     * <p>Extracts the specific genre in order to subscribe.</p>
     *
     * @param actionInfo information about one action to be created.
     */
    public SubscribeAction(final ActionFetch actionInfo) {
        selectedGenre = actionInfo.getSubscribedGenre();
    }

    /**
     * <p>Subscribes the active client for just one genre.
     * If the action went successfully nothing is printed
     * to the log file otherwise the basic error message
     * is generated.</p>
     *
     * @param output main array node to parse the output data
     *               to a Json File.
     */
    @Override
    public void execute(final ArrayNode output) {
        final var server = ServerApp.connect();
        final var client = server.fetchActiveClient();
        final var database = server.fetchDatabase();

        final var parserObject = output.objectNode();
        if (client.getLoadedPage() != PageType.DETAILS) {
            JsonParser.parseBasicError(parserObject);
            output.add(parserObject);
        } else {
            final var movieInfo = database.collection("movies")
                    .findOne("name", client.getSeeMovie());

            if (movieInfo != null) {
                final var movieGenres = Arrays.asList(movieInfo.get("genres").split(","));

                if (movieGenres.contains(selectedGenre)) {
                    if (!client.subscribeToGenre(selectedGenre)) {
                        JsonParser.parseBasicError(parserObject);
                        output.add(parserObject);
                    }
                } else {
                    JsonParser.parseBasicError(parserObject);
                    output.add(parserObject);
                }
            }
        }
    }
}
