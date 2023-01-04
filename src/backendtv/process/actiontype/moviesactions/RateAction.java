package backendtv.process.actiontype.moviesactions;

import backendtv.pagestype.PageType;
import backendtv.parser.JsonParser;
import backendtv.process.actiontype.ActionCommand;
import backendtv.server.ServerApp;
import com.fasterxml.jackson.databind.node.ArrayNode;
import datafetch.ActionFetch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

        final var parserObject = output.addObject();
        if ((client.getLoadedPage() != PageType.DETAILS)
                || (movieRate < MIN_RATE) || (movieRate > MAX_RATE)) {

            JsonParser.parseBasicError(parserObject);
        } else {
            movieToRate = client.getSeeMovie();

            final boolean hasRatedThisMovieAlready = client.hasRatedMovie(movieToRate);

            if (!client.rateMovie(movieToRate)) {
                JsonParser.parseBasicError(parserObject);
            } else {
                final var movie = server.fetchDatabase()
                        .collection("movies")
                        .findOne("name", movieToRate);

                if (hasRatedThisMovieAlready) {
                    final List<String> movieRatings = new ArrayList<>(Arrays.asList(
                            movie.get("rating").split(",")));

                    movieRatings.removeIf(name -> name.startsWith(client.getName()));
                    movieRatings.add(client.getName() + ":" + movieRate);

                    movie.put("rating", String.join(",", movieRatings));
                } else {
                    movie.put("rating",
                            movie.get("rating") + "," + client.getName() + ":" + movieRate);
                    movie.put("numRatings",
                            Integer.toString(Integer.parseInt(movie.get("numRatings")) + 1));
                }

                server.fetchDatabase()
                        .collection("movies")
                        .modifyMember("name", movieToRate, movie);

                parserObject.putNull("error");
                JsonParser.parseMovie(parserObject.putArray("currentMoviesList"),
                        "name", movieToRate);
                JsonParser.parseClient(parserObject);
            }
        }
    }
}
