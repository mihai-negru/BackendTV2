package backendtv.process.actiontype.moviesactions;

import backendtv.pagestype.PageType;
import backendtv.parser.JsonParser;
import backendtv.process.actiontype.ActionCommand;
import backendtv.server.ServerApp;
import com.fasterxml.jackson.databind.node.ArrayNode;
import datafetch.ActionFetch;

/**
 * <p>Specific Command class to watch a movie regarding
 * the active client.</p>
 *
 * @since 1.0.0
 * @author Mihai Negru
 */
public final class WatchAction implements ActionCommand {
    private String movieToWatch;

    /**
     * <p>Extracts the movie to watch.</p>
     *
     * @param actionInfo information about one action to be created.
     */
    public WatchAction(final ActionFetch actionInfo) {
        movieToWatch = actionInfo.getMovie();
    }

    /**
     * <p>Watch movie regarding the current active client if
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
            movieToWatch = client.getSeeMovie();

            if (!client.watchMovie(movieToWatch)) {
                JsonParser.parseBasicError(parserObject);
            } else {
                parserObject.putNull("error");
                JsonParser.parseMovie(parserObject.putArray("currentMoviesList"),
                        "name", movieToWatch);
                JsonParser.parseClient(parserObject);
            }
        }
    }
}
