package backendtv.process.actiontype.adminactions;

import backendtv.server.ServerApp;
import datafetch.ActionFetch;

import java.util.List;

/**
 * <p>The Strategy in order to delete a movie from the database
 * and to notify the server about this change. The strategy also
 * works like an observable class, however it is known that the
 * single observer that triggers other observers is the main server.</p>
 *
 * @since 2.0.0
 * @author Mihai Negru
 */
public final class DeleteDatabaseOperation implements DatabaseStrategy {
    private final String deletedMovieName;

    /**
     * <p>Extracts the information about the movie to remove from the database.</p>
     * @param initOperationInfo information about the operation process.
     */
    public DeleteDatabaseOperation(final ActionFetch initOperationInfo) {
        deletedMovieName = initOperationInfo.getDeletedMovie();
    }

    /**
     * <p>Connects to the server's database and removes
     * one movie according to its name and notifies the
     * server about the change.</p>
     *
     * @return true if the movie was removed successfully
     * or false otherwise.
     */
    @Override
    public boolean apply() {
        final var server = ServerApp.connect();
        final var moviesCollection = server.fetchDatabase().collection("movies");

        final var movieInfo = moviesCollection.findOne("name", deletedMovieName);

        if (movieInfo == null) {
            return false;
        }

        moviesCollection.delete("name", deletedMovieName);

        server.updateNotifications("DELETE",
                deletedMovieName,
                List.of(movieInfo.get("genres").split(",")),
                List.of(movieInfo.get("countriesBanned").split(","))
        );

        return true;
    }
}
