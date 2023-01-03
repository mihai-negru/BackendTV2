package backendtv.process.actiontype.moviesactions;

import backendtv.pagestype.PageType;
import backendtv.parser.JsonParser;
import backendtv.process.actiontype.ActionCommand;
import backendtv.server.ServerApp;
import com.fasterxml.jackson.databind.node.ArrayNode;
import datafetch.ActionFetch;

/**
 * <p>Specific Command class to like a movie regarding
 * the active client.</p>
 *
 * @since 1.0.0
 * @author Mihai Negru
 */
public final class LikeAction implements ActionCommand {
    private String movieToLike;

    /**
     * <p>Extract the movie to like.</p>
     *
     * @param actionInfo information about one action to be created.
     */
    public LikeAction(final ActionFetch actionInfo) {
        movieToLike = actionInfo.getMovie();
    }

    /**
     * <p>Like movie regarding the current active client if
     * possible or parses an error message to the Json File.</p>
     *
     * @param output main array node to parse the output data
     *               to a Json File.
     */
    @Override
    public void execute(final ArrayNode output) {
        final var server = ServerApp.connect();
        final var client = server.fetchActiveClient();

        final var parserObject = output.addObject();
        if (client.getLoadedPage() != PageType.DETAILS) {
            JsonParser.parseBasicError(parserObject);
        } else {
            movieToLike = client.getSeeMovie();

            if (!client.likeMovie(movieToLike)) {
                JsonParser.parseBasicError(parserObject);
            } else {
                server.fetchDatabase()
                        .collection("movies")
                        .modifyField(
                                "name", movieToLike,
                                "numLikes", Integer.toString(
                                        Integer.parseInt(server.fetchDatabase()
                                                .collection("movies")
                                                .findOne("name", movieToLike)
                                                .get("numLikes")) + 1)
                        );

                parserObject.putNull("error");
                JsonParser.parseMovie(parserObject.putArray("currentMoviesList"),
                        "name", movieToLike);
                JsonParser.parseClient(parserObject);
            }
        }
    }
}
