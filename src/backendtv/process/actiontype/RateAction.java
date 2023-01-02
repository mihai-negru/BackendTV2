package backendtv.process.actiontype;

import backendtv.pagestype.PageType;
import backendtv.parser.JsonParser;
import backendtv.server.ServerApp;
import com.fasterxml.jackson.databind.node.ArrayNode;
import datafetch.ActionFetch;

/**
 * <p>Specific Command class to rate a movie regarding
 * the active client.</p>
 *
 * @since 1.0.0
 * @author Mihai Negru
 */
public final class RateAction implements ActionCommand {
    private static final int MIN_RATE = 1;
    private static final int MAX_RATE = 5;
    private String movieToRate;
    private final int movieRate;

    /**
     * <p>Extract the movie to rate.</p>
     *
     * @param actionInfo information about one action to be created.
     */
    public RateAction(final ActionFetch actionInfo) {
        movieToRate = actionInfo.getMovie();
        movieRate = actionInfo.getRate();
    }

    /**
     * <p>Rate movie regarding the current active client if
     * possible or parses an error message to the Json File.</p>
     *
     * @param output main array node to parse the output data
     *               to a Json File.
     */
    @Override
    public void execute(final ArrayNode output) {
        final var server = ServerApp.connect();
        final var client = server.fetchActiveClient();

        final var outputObject = output.addObject();
        if ((client.getLoadedPage() != PageType.DETAILS)
                || (movieRate < MIN_RATE) || (movieRate > MAX_RATE)) {

            outputObject.put("error", "Error");
            outputObject.putArray("currentMoviesList");
            outputObject.putNull("currentUser");
        } else {
            movieToRate = client.getSeeMovie();

            if (!client.rateMovie(movieToRate)) {
                outputObject.put("error", "Error");
                outputObject.putArray("currentMoviesList");
                outputObject.putNull("currentUser");
            } else {
                final var movie = server.fetchDatabase()
                        .collection("movies")
                        .findOne("name", movieToRate);

                server.fetchDatabase()
                        .collection("movies")
                        .modifyField(
                                "name", movieToRate,
                                "numRatings", Integer.toString(
                                        Integer.parseInt(server.fetchDatabase()
                                                .collection("movies")
                                                .findOne("name", movieToRate)
                                                .get("numRatings")) + 1)
                        );

                server.fetchDatabase()
                        .collection("movies")
                        .modifyField(
                                "name", movieToRate,
                                "rating", movie.get("rating") + "," + movieRate
                        );

                outputObject.putNull("error");
                JsonParser.parseMovie(outputObject.putArray("currentMoviesList"),
                        "name", movieToRate);
                JsonParser.parseClient(outputObject);
            }
        }
    }
}
