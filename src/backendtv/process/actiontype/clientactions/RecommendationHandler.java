package backendtv.process.actiontype.clientactions;

import projectutils.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class RecommendationHandler {
    private final List<Pair<String, Integer>> allowedGenres;
    private final List<Pair<String, Integer>> movieLikes;
    private final Map<String, String> movieGenres;
    private final List<String> watchedMovies;

    private RecommendationHandler(final RecommendationHandlerBuilder builder) {
        allowedGenres = builder.allowedGenres;
        movieLikes = builder.movieLikes;
        movieGenres = builder.movieGenres;
        watchedMovies = builder.watchedMovies;
    }

    public static class RecommendationHandlerBuilder {
        private List<Pair<String, Integer>> allowedGenres;
        private List<Pair<String, Integer>> movieLikes;
        private Map<String, String> movieGenres;
        private List<String> watchedMovies;

        public RecommendationHandlerBuilder() {
            // Nothing here
        }

        public RecommendationHandlerBuilder addGenres(final List<Pair<String, Integer>> genres) {
            allowedGenres = genres.stream()
                    .sorted(Comparator
                            .comparingInt(Pair<String, Integer>::getValue)
                            .reversed()
                            .thenComparing(Pair::getKey))
                    .collect(Collectors.toList());

            return this;
        }

        public RecommendationHandlerBuilder addMovies(final List<Pair<String, Integer>> movies) {
            movieLikes = movies.stream()
                    .sorted(Comparator
                            .comparingInt(Pair<String, Integer>::getValue)
                            .reversed())
                    .collect(Collectors.toList());

            return this;
        }

        public RecommendationHandlerBuilder addMoviesGenres(final Map<String, String> genres) {
            movieGenres = new HashMap<>(genres);
            return this;
        }

        public RecommendationHandlerBuilder addWatchedMovies(final List<String> movies) {
            watchedMovies = new ArrayList<>(movies);
            return this;
        }

        public RecommendationHandler build() {
            return new RecommendationHandler(this);
        }
    }

    public String generateRecommendation() {
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
