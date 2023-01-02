package backendtv.process.actiontype;

import backendtv.pagestype.PageType;
import backendtv.server.ServerApp;
import com.fasterxml.jackson.databind.node.ArrayNode;
import datafetch.ActionFetch;

/**
 * <p>Specific Command class to buy tokens for the
 * current active client.</p>
 *
 * @since 1.0.0
 * @author Mihai Negru
 */
public final class BuyTokensAction implements ActionCommand {
    private final int tokensToBuy;

    /**
     * <p>Extract the number of tokens to buy.</p>
     *
     * @param actionInfo information about one action to be created.
     */
    public BuyTokensAction(final ActionFetch actionInfo) {
        tokensToBuy = actionInfo.getCount();
    }

    /**
     * <p>Buys tokens for the current active client if
     * possible or parses an error message to the Json File.</p>
     *
     * @param output main array node to parse the output data
     *               to a Json File.
     */
    @Override
    public void execute(final ArrayNode output) {
        final var client = ServerApp.connect().fetchActiveClient();

        final var parserObject = output.objectNode();
        if (client.getLoadedPage() != PageType.UPGRADES) {
            parserObject.put("error", "Error");
            parserObject.putArray("currentMoviesList");
            parserObject.putNull("currentUser");

            output.add(parserObject);
        } else {
            if (!client.buyTokens(tokensToBuy)) {
                parserObject.put("error", "Error");
                parserObject.putArray("currentMoviesList");
                parserObject.putNull("currentUser");

                output.add(parserObject);
            }
        }
    }
}
