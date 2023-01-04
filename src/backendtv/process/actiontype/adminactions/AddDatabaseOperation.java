package backendtv.process.actiontype.adminactions;

import backendtv.server.ServerApp;
import backendtv.storage.Database;
import datafetch.ActionFetch;
import datafetch.MovieFetch;

import java.util.Map;

public class AddDatabaseOperation implements DatabaseStrategy {
    private final MovieFetch addedMovieInfo;

    public AddDatabaseOperation(final ActionFetch initOperationInfo) {
        addedMovieInfo = initOperationInfo.getAddedMovie();
    }

    @Override
    public boolean apply() {
        final var server = ServerApp.connect();
        final var database = server.fetchDatabase();

        final String movieName = addedMovieInfo.getName();
        final var movieGenres = addedMovieInfo.getGenres();
        final var bannedCountries = addedMovieInfo.getCountriesBanned();

        if (database.collection("movies").findOne("name", movieName) != null) {
            return false;
        }

        database.collection("movies").insert(Map.of(
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
