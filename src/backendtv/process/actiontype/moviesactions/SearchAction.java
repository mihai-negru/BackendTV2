package backendtv.process.actiontype.moviesactions;

import backendtv.pagestype.PageType;
import backendtv.parser.JsonParser;
import backendtv.process.actiontype.ActionCommand;
import backendtv.server.ServerApp;
import com.fasterxml.jackson.databind.node.ArrayNode;
import datafetch.ActionFetch;

/**
 * <p>Specific Command class to search a movie for the
 * current active player.</p>
 *
 * @since 1.0.0
 * @author Mihai Negru
 */
public final class SearchAction implements ActionCommand {
    private final String startString;

    /**
     * <p>Extract starting of the movie to search for in the
     * database.</p>
     *
     * @param actionInfo information about one action to be created.
     */
    public SearchAction(final ActionFetch actionInfo) {
        startString = actionInfo.getStartsWith();
    }

    /**
     * <p>Search a movie for the current active client
     * possible or parses an error message to the Json File.</p>
     *
     * @param output main array node to parse the output data
     *               to a Json File.
     */
    @Override
    public void execute(final ArrayNode output) {
        final var server = ServerApp.connect();

        final var parserObject = output.addObject();
        if (server.fetchActiveClient().getLoadedPage() != PageType.MOVIES) {
            JsonParser.parseBasicError(parserObject);
        } else {
            parserObject.putNull("error");

            final var movies = parserObject.putArray("currentMoviesList");
            for (String movieName : server.fetchActiveClient().getAvailableMovies()) {
                if (movieName.startsWith(startString)) {
                    JsonParser.parseMovie(movies, "name", movieName);
                }
            }

            JsonParser.parseClient(parserObject);
        }
    }
}
