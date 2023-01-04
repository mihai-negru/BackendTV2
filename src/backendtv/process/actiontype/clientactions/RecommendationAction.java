package backendtv.process.actiontype.clientactions;

import backendtv.parser.JsonParser;
import backendtv.process.actiontype.ActionCommand;
import backendtv.server.ServerApp;
import com.fasterxml.jackson.databind.node.ArrayNode;
import projectutils.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Specific Command class to generate a recommendation message
 * for a premium active client at the end of the action list.</p>
 *
 * @since 2.0.0
 * @author Mihai Negru
 */
public final class RecommendationAction implements ActionCommand {
    private final RecommendationHandler.RecommendationHandlerBuilder generator;

    /**
     * <p>Creates the recommendation generator class.</p>
     */
    public RecommendationAction() {
        generator = new RecommendationHandler.RecommendationHandlerBuilder();
    }

    /**
     * <p>Creates one recommendation for the active client according to
     * watched movies, watched genres and number of likes that an
     * available film has.</p>
     *
     * @param output main array node to parse the output data
     *               to a Json File.
     */
    @Override
    public void execute(final ArrayNode output) {
        final var server = ServerApp.connect();
        final var movies = server.fetchDatabase().collection("movies");
        final var client = server.fetchActiveClient();

        final List<Pair<String, Integer>> genrePairs = new ArrayList<>();
        for (var movieName : client.getLikedMovies()) {
            final var movieInfo = movies.findOne("name", movieName);

            for (var genre : movieInfo.get("genres").split(",")) {
                boolean pairDoesNotExist = true;
                for (var genrePair : genrePairs) {
                    if (genrePair.containsKey(genre)) {
                        genrePair.setValue(genrePair.getValue() + 1);
                        pairDoesNotExist = false;
                    }
                }

                if (pairDoesNotExist) {
                    genrePairs.add(new Pair<>(genre, 1));
                }
            }
        }

        generator.addGenres(genrePairs);

        final List<Pair<String, Integer>> movieLikePairs = new ArrayList<>();
        final Map<String, String> movieGenrePairs = new HashMap<>();
        for (var movieName : client.getAvailableMovies()) {
            final var movieInfo = movies.findOne("name", movieName);

            movieLikePairs.add(new Pair<>(movieName,
                    Integer.parseInt(movieInfo.get("numLikes"))));

            movieGenrePairs.put(movieName, movieInfo.get("genres"));
        }

        generator.addMovies(movieLikePairs)
                .addMoviesGenres(movieGenrePairs)
                .addWatchedMovies(client.getWatchedMovies());

        client.acceptRecommendation(generator.build().generateRecommendation());

        final var parserObject = output.addObject();
        parserObject.putNull("error");
        parserObject.putNull("currentMoviesList");
        JsonParser.parseClient(parserObject);
    }
}
