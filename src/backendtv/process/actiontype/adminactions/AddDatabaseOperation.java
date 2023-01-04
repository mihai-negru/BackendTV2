package backendtv.process.actiontype.adminactions;

import backendtv.server.ServerApp;
import datafetch.ActionFetch;
import datafetch.MovieFetch;

import java.util.Map;

/**
 * <p>The Strategy in order to add a movie for the database
 * and to notify the server about this change. The strategy also
 * works like an observable class, however it is known that the
 * single observer that triggers other observers is the main server.</p>
 *
 * @since 2.0.0
 * @author Mihai Negru
 */
public final class AddDatabaseOperation implements DatabaseStrategy {
    private final MovieFetch addedMovieInfo;

    /**
     * <p>Extracts the information about the movie to add in the database.</p>
     * @param initOperationInfo information about the operation process.
     */
    public AddDatabaseOperation(final ActionFetch initOperationInfo) {
        addedMovieInfo = initOperationInfo.getAddedMovie();
    }

    /**
     * <p>Generates a {@code Map} representation for the
     * movie and inserts it in the database and notifies
     * the server about the change.</p>
     *
     * @return true if the movie is not already in the database
     * or false otherwise.
     */
    @Override
    public boolean apply() {
        final var server = ServerApp.connect();
        final var moviesCollection = server.fetchDatabase().collection("movies");

        final String movieName = addedMovieInfo.getName();
        final var movieGenres = addedMovieInfo.getGenres();
        final var bannedCountries = addedMovieInfo.getCountriesBanned();

        if (moviesCollection.findOne("name", movieName) != null) {
            return false;
        }

        moviesCollection.insert(Map.of(
                "name", movieName,
                "year", Integer.toString(addedMovieInfo.getYear()),
                "duration", Integer.toString(addedMovieInfo.getDuration()),
                "genres", String.join(",", movieGenres),
                "actors", String.join(",", addedMovieInfo.getActors()),
                "countriesBanned", String.join(",", bannedCountries),
                "numLikes", "0",
                "rating", "Unknown:0",
                "numRatings", "0"
        ));

        server.updateNotifications("ADD", movieName, movieGenres, bannedCountries);

        return true;
    }
}
