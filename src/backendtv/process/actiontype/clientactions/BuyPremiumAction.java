package backendtv.process.actiontype.clientactions;

import backendtv.pagestype.PageType;
import backendtv.parser.JsonParser;
import backendtv.process.actiontype.ActionCommand;
import backendtv.server.ServerApp;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * <p>Specific Command class to buy a premium account for the
 * current active client.</p>
 *
 * @since 1.0.0
 * @author Mihai Negru
 */
public final class BuyPremiumAction implements ActionCommand {

    /**
     * <p>Buys a premium account for the current active client if
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
            JsonParser.parseBasicError(parserObject);
            output.add(parserObject);
        } else {
            if (!client.buyPremiumAccount()) {
                JsonParser.parseBasicError(parserObject);
                output.add(parserObject);
            }
        }
    }
}
