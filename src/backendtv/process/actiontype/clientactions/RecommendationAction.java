package backendtv.process.actiontype.clientactions;

import backendtv.parser.JsonParser;
import backendtv.process.actiontype.ActionCommand;
import backendtv.server.ServerApp;
import com.fasterxml.jackson.databind.node.ArrayNode;
import projectutils.Pair;

import java.util.*;

public class RecommendationAction implements ActionCommand {
    @Override
    public void execute(ArrayNode output) {
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

        genrePairs.sort(Comparator
                .comparingInt(Pair<String, Integer>::getValue)
                .reversed()
                .thenComparing(Pair::getKey)
        );

        final List<Pair<String, Integer>> movieLikePairs = new ArrayList<>();
        final Map<String, String> movieGenrePairs = new HashMap<>();
        for (var movieName : client.getAvailableMovies()) {
            final var movieInfo = movies.findOne("name", movieName);

            movieLikePairs.add(new Pair<>(movieName,
                    Integer.parseInt(movieInfo.get("numLikes"))));

            movieGenrePairs.put(movieName, movieInfo.get("genres"));
        }

        movieLikePairs.sort(Comparator
                .comparingInt(Pair<String, Integer>::getValue)
                .reversed()
        );

        String recommendedMovie = null;
        boolean foundMovie = false;
        final List<String> clientWatchedMovies = new ArrayList<>(client.getWatchedMovies());
        for (var genre : genrePairs) {
            if (foundMovie) {
                break;
            }

            for (var movieName : movieLikePairs) {
                if (movieGenrePairs.get(movieName.getKey()).contains(genre.getKey())
                        && !clientWatchedMovies.contains(movieName.getKey())) {
                    recommendedMovie = movieName.getKey();

                    foundMovie = true;

                    break;
                }
            }
        }

        if (recommendedMovie == null) {
            recommendedMovie = "No recommendation";
        }

        client.getRecommendation(recommendedMovie);

        final var parserObject = output.addObject();
        parserObject.putNull("error");
        parserObject.putNull("currentMoviesList");
        JsonParser.parseClient(parserObject);
    }
}
