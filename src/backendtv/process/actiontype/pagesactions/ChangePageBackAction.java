package backendtv.process.actiontype.pagesactions;

import backendtv.pagestype.PageType;
import backendtv.parser.JsonParser;
import backendtv.process.actiontype.ActionCommand;
import backendtv.server.ServerApp;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * <p>Specific Command class to change the page pf an
 * active client on the running server to the last
 * successfully accessed page.</p>
 *
 * @since 1.0.0
 * @author Mihai Negru
 */
public final class ChangePageBackAction implements ActionCommand {

    /**
     * <p>Changes the page for the active client to
     * the last successfully accessed page and
     * return an log message if the process went
     * wrong or the process went successfully just
     * for some cases (Movies and See Details).</p>
     *
     * @param output main array node to parse the output data
     *               to a Json File.
     */
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
                    JsonParser.parseMovie(parserObject
                            .putArray("currentMoviesList"), "name", client.getSeeMovie());
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
