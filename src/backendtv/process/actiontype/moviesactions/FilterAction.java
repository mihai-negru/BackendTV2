package backendtv.process.actiontype.moviesactions;

import backendtv.pagestype.PageType;
import backendtv.parser.JsonParser;
import backendtv.process.actiontype.ActionCommand;
import backendtv.server.ServerApp;
import com.fasterxml.jackson.databind.node.ArrayNode;
import datafetch.ActionFetch;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * <p>Specific Command class to filter some movies for the
 * current active player.</p>
 *
 * @since 1.0.0
 * @author Mihai Negru
 */
public final class FilterAction implements ActionCommand {
    private final Comparator<Map<String, String>> sortComp;
    private final Predicate<Map<String, String>> filterPred;
    private final List<String> selectedActors;
    private final List<String> selectedGenres;

    /**
     * <p>Extract flags for the filtering and build the
     * lambda function for filtering and sorting.</p>
     *
     * @param actionInfo information about one action to be created.
     */
    public FilterAction(final ActionFetch actionInfo) {
        final var filters = actionInfo.getFilters();
        if (filters.getSort() != null) {
            final var rating = filters.getSort().getRating();
            final var duration = filters.getSort().getDuration();

            final Function<Map<String, String>, Double> rateCalculator = m -> {
                double ratingValue = 0;
                for (String rate : m.get("rating").split(",")) {
                    ratingValue += Double.parseDouble(rate);
                }

                final int numRatings = Integer.parseInt(m.get("numRatings"));
                if (numRatings != 0) {
                    ratingValue /= numRatings;
                } else {
                    ratingValue = 0;
                }

                return ratingValue;
            };

            Comparator<Map<String, String>> durationComp;
            Comparator<Map<String, String>> ratingComp;

            if (duration != null) {
                if (duration.equals("increasing")) {
                    durationComp = Comparator.comparingInt(m ->
                            Integer.parseInt(m.get("duration")));
                } else {
                    durationComp = (m1, m2) -> Integer.compare(Integer.parseInt(m2.get("duration")),
                            Integer.parseInt(m1.get("duration")));
                }
            } else {
                durationComp = null;
            }

            if (rating != null) {
                if (rating.equals("increasing")) {
                    ratingComp = Comparator.comparingDouble(rateCalculator::apply);
                } else {
                    ratingComp = (m1, m2) -> Double.compare(rateCalculator.apply(m2),
                            rateCalculator.apply(m1));
                }
            } else {
                ratingComp = null;
            }

            if ((durationComp == null) && (ratingComp == null)) {
                sortComp = null;
            } else if (ratingComp == null) {
                sortComp = durationComp;
            } else if (durationComp == null) {
                sortComp = ratingComp;
            } else {
                sortComp = durationComp.thenComparing(ratingComp);
            }
        } else {
            sortComp = null;
        }

        if (filters.getContains() != null) {
            selectedActors = filters.getContains().getActors();
            selectedGenres = filters.getContains().getGenre();

            if ((selectedActors == null) && (selectedGenres == null)) {
                filterPred = null;
            } else if (selectedActors == null) {
                filterPred = m -> {
                    final String genres = m.get("genres");
                    for (String genre : selectedGenres) {
                        if (!genres.contains(genre)) {
                            return false;
                        }
                    }

                    return true;
                };
            } else if (selectedGenres == null) {
                filterPred = m -> {
                    final String actors = m.get("actors");
                    for (String actor : selectedActors) {
                        if (!actors.contains(actor)) {
                            return false;
                        }
                    }

                    return true;
                };
            } else {
                filterPred = m -> {
                    final String actors = m.get("actors");
                    for (String actor : selectedActors) {
                        if (!actors.contains(actor)) {
                            return false;
                        }
                    }

                    final String genres = m.get("genres");
                    for (String genre : selectedGenres) {
                        if (!genres.contains(genre)) {
                            return false;
                        }
                    }

                    return true;
                };
            }
        } else {
            filterPred = null;
            selectedActors = null;
            selectedGenres = null;
        }
    }

    /**
     * <p>Filters some movies for the current active client
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
        if (server.fetchActiveClient().getLoadedPage() != PageType.MOVIES) {
            JsonParser.parseBasicError(parserObject);
        } else {
            final List<Map<String, String>> movies = new ArrayList<>();
            for (String movieName : client.getAvailableMovies()) {
                movies.add(server.fetchDatabase().collection("movies").findOne("name", movieName));
            }

            List<Map<String, String>> filteredMovies;
            if (filterPred == null) {
                filteredMovies = movies;
            } else {
                filteredMovies = movies.stream()
                        .filter(filterPred)
                        .toList();
            }

            if (sortComp != null) {
                filteredMovies = filteredMovies.stream()
                        .sorted(sortComp)
                        .toList();
            }

            parserObject.putNull("error");

            client.filterMovies();

            final var currentMovies = parserObject.putArray("currentMoviesList");
            for (var movie : filteredMovies) {
                JsonParser.parseMovie(currentMovies, movie);
                client.addFilteredMovie(movie.get("name"));
            }

            JsonParser.parseClient(parserObject);
        }
    }
}
