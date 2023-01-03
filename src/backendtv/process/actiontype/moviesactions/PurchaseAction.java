package backendtv.process.actiontype.moviesactions;

import backendtv.pagestype.PageType;
import backendtv.parser.JsonParser;
import backendtv.process.actiontype.ActionCommand;
import backendtv.server.ServerApp;
import com.fasterxml.jackson.databind.node.ArrayNode;
import datafetch.ActionFetch;

/**
 * <p>Specific Command class to purchase a movie for
 * the active client.</p>
 *
 * @since 1.0.0
 * @author Mihai Negru
 */
public final class PurchaseAction implements ActionCommand {
    private String movieToPurchase;

    /**
     * <p>Extract the movie to purchase.</p>
     *
     * @param actionInfo information about one action to be created.
     */
    public PurchaseAction(final ActionFetch actionInfo) {
        movieToPurchase = actionInfo.getMovie();
    }

    /**
     * <p>Purchase movie for the current active client if
     * possible or parses an error message to the Json File.</p>
     *
     * @param output main array node to parse the output data
     *               to a Json File.
     */
    @Override
    public void execute(final ArrayNode output) {
        final var client = ServerApp.connect().fetchActiveClient();

        final var parserObject = output.addObject();
        if (client.getLoadedPage() != PageType.DETAILS) {
            JsonParser.parseBasicError(parserObject);
        } else {
            movieToPurchase = client.getSeeMovie();

            if (!client.purchaseMovie(movieToPurchase)) {
                JsonParser.parseBasicError(parserObject);
            } else {
                parserObject.putNull("error");
                JsonParser.parseMovie(parserObject.putArray("currentMoviesList"),
                        "name", movieToPurchase);
                JsonParser.parseClient(parserObject);
            }
        }
    }
}
