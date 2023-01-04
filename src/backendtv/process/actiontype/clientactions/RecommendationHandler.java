package backendtv.process.actiontype.clientactions;

import projectutils.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>Handler class that according to the algorithms generates
 * a recommendation movie for the premium active client.</p>
 *
 * <p>The Class follows the Builder Pattern.</p>
 *
 * @since 2.0.0
 * @author Mihai Negru
 */
public final class RecommendationHandler {
    private final List<Pair<String, Integer>> allowedGenres;
    private final List<Pair<String, Integer>> movieLikes;
    private final Map<String, String> movieGenres;
    private final List<String> watchedMovies;

    /**
     * <p>The Class can be instanced just by the builder class.</p>
     *
     * @param builder valid builder to construct the handler class.
     */
    private RecommendationHandler(final RecommendationHandlerBuilder builder) {
        allowedGenres = builder.allowedGenres;
        movieLikes = builder.movieLikes;
        movieGenres = builder.movieGenres;
        watchedMovies = builder.watchedMovies;
    }

    /**
     * <p>Static nested class that allows the Builder Pattern.</p>
     */
    public static final class RecommendationHandlerBuilder {
        private List<Pair<String, Integer>> allowedGenres;
        private List<Pair<String, Integer>> movieLikes;
        private Map<String, String> movieGenres;
        private List<String> watchedMovies;

        public RecommendationHandlerBuilder() {
            // The constructor is left intentionally in order
            // to signal the programmer that all the fields for
            // the builder are optional and additional required
            // or optional fields
        }

        /**
         * <p>Add a list of all genres that the client has ever watched.</p>
         *
         * @param genres the list of the genres.
         * @return builder instance.
         */
        public RecommendationHandlerBuilder addGenres(final List<Pair<String, Integer>> genres) {
            allowedGenres = genres.stream()
                    .sorted(Comparator
                            .comparingInt(Pair<String, Integer>::getValue)
                            .reversed()
                            .thenComparing(Pair::getKey))
                    .collect(Collectors.toList());

            return this;
        }

        /**
         * <p>Add a list of all available movies that the client can access.</p>
         *
         * @param movies the list of all movies and also their likes from the database.
         * @return builder instance.
         */
        public RecommendationHandlerBuilder addMovies(final List<Pair<String, Integer>> movies) {
            movieLikes = movies.stream()
                    .sorted(Comparator
                            .comparingInt(Pair<String, Integer>::getValue)
                            .reversed())
                    .collect(Collectors.toList());

            return this;
        }

        /**
         * <p>Adds all the available movies with their genres.</p>
         *
         * @param genres the genres for every movie.
         * @return builder instance.
         */
        public RecommendationHandlerBuilder addMoviesGenres(final Map<String, String> genres) {
            movieGenres = new HashMap<>(genres);
            return this;
        }

        /**
         * <p>Adds all watched movies by the active premium client.</p>
         *
         * @param movies all watched movies list.
         * @return builder instance.
         */
        public RecommendationHandlerBuilder addWatchedMovies(final List<String> movies) {
            watchedMovies = new ArrayList<>(movies);
            return this;
        }

        /**
         * <p>Creates the instance of a new Handler.</p>
         *
         * @return new created handler for recommendation.
         */
        public RecommendationHandler build() {
            return new RecommendationHandler(this);
        }
    }

    /**
     * <p>Generates a recommendation movie for the client.</p>
     *
     * @return {@code String} containing the name of the movie
     * or "No recommendation" if the algorithm generated nothing.
     */
    public String generateRecommendation() {
        if ((movieGenres == null) || (watchedMovies == null)) {
            return "No recommendation";
        }

        for (var genre : allowedGenres) {
            for (var movieName : movieLikes) {
                if (movieGenres.get(movieName.getKey()).contains(genre.getKey())
                        && !watchedMovies.contains(movieName.getKey())) {
                    return movieName.getKey();
                }
            }
        }

        return "No recommendation";
    }
}
