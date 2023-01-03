package backendtv.process.actiontype.adminactions;

import backendtv.server.ServerApp;
import datafetch.ActionFetch;

import java.util.List;

public class DeleteDatabaseOperation implements DatabaseStrategy {
    private final String deletedMovieName;

    public DeleteDatabaseOperation(final ActionFetch initOperationInfo) {
        deletedMovieName = initOperationInfo.getDeletedMovie();
    }

    @Override
    public boolean apply() {
        final var server = ServerApp.connect();
        final var database = server.fetchDatabase();

        final var movieInfo = database.collection("movies").findOne("name", deletedMovieName);

        if (movieInfo == null) {
            return false;
        }

        database.collection("movies").delete("name", deletedMovieName);

        server.updateNotifications("DELETE",
                deletedMovieName,
                List.of(movieInfo.get("genres").split(",")),
                List.of(movieInfo.get("countriesBanned").split(","))
        );

        return true;
    }
}
