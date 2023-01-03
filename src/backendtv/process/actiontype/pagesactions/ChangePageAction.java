package backendtv.process.actiontype.pagesactions;

import backendtv.pagestype.PageType;
import backendtv.parser.JsonParser;
import backendtv.process.actiontype.ActionCommand;
import backendtv.server.ServerApp;
import com.fasterxml.jackson.databind.node.ArrayNode;
import datafetch.ActionFetch;

/**
 * <p>Specific Command class to change the page pf an
 * active client on the running server.</p>
 *
 * @since 1.0.0
 * @author Mihai Negru
 */
public final class ChangePageAction implements ActionCommand {
    private final PageType changePage;
    private final String selectedMovie;

    /**
     * <p>Extract page type from the input data.</p>
     *
     * @param actionInfo information about one action to be created.
     */
    public ChangePageAction(final ActionFetch actionInfo) {
        changePage = switch (actionInfo.getPage()) {
            case "login" -> PageType.LOGIN;
            case "register" -> PageType.REGISTER;
            case "logout" -> PageType.LOGOUT;
            case "movies" -> PageType.MOVIES;
            case "see details" -> PageType.DETAILS;
            case "upgrades" -> PageType.UPGRADES;
            default -> PageType.UNKNOWN;
        };

        if (actionInfo.getPage().equals("see details")) {
            selectedMovie = actionInfo.getMovie();
        } else {
            selectedMovie = null;
        }
    }

    /**
     * <p>Change the page of an active client on the running server if
     * possible or parses an error message to the Json File.</p>
     *
     * @param output main array node to parse the output data
     *               to a Json File.
     */
    @Override
    public void execute(final ArrayNode output) {
        final var server = ServerApp.connect();
        final var client = server.fetchActiveClient();
        final var clientPage = client.getLoadedPage();

        final var parserObject = output.objectNode();
        if ((changePage == PageType.LOGIN) || (changePage == PageType.REGISTER)) {
            if (clientPage != PageType.NO_AUTH) {
                JsonParser.parseBasicError(parserObject);
                output.add(parserObject);
            } else {
                client.changePage(changePage);
            }
        } else if (changePage == PageType.LOGOUT) {
            if ((clientPage != PageType.AUTH) && (clientPage != PageType.UPGRADES)
                && (clientPage != PageType.DETAILS) && (clientPage != PageType.MOVIES)) {

                parserObject.put("error", "Error");
                parserObject.putArray("currentMoviesList");
                JsonParser.parseClient(parserObject);
                output.add(parserObject);
            } else {
                server.logoutClient();
            }
        } else if (changePage == PageType.MOVIES) {

            if ((clientPage != PageType.AUTH) && (clientPage != PageType.DETAILS)
                && (clientPage != PageType.UPGRADES) && (clientPage != PageType.MOVIES)) {

                parserObject.put("error", "Error");
                parserObject.putArray("currentMoviesList");
            } else {
                client.setMoviesAsNonFiltered();
                client.changePage(changePage);
                parserObject.putNull("error");
                JsonParser.parseAvailableMovies(parserObject.putArray("currentMoviesList"));
            }

            JsonParser.parseClient(parserObject);
            output.add(parserObject);
        } else if (changePage == PageType.DETAILS) {
            if ((selectedMovie == null) || (clientPage != PageType.MOVIES)) {
                parserObject.put("error", "Error");
                parserObject.putArray("currentMoviesList");
//                JsonParser.parseClient(parserObject);
                parserObject.putNull("currentUser");
                output.add(parserObject);
            } else {
                if (client.areMoviesFiltered()) {
                    if (client.getFilteredMovies().contains(selectedMovie)) {
                        parserObject.putNull("error");
                        JsonParser.parseMovie(parserObject.putArray("currentMoviesList"),
                                "name", selectedMovie);
                        JsonParser.parseClient(parserObject);
                        output.add(parserObject);

                        client.changePage(changePage);
                        client.setSeeMovie(selectedMovie);
                    } else {
                        JsonParser.parseBasicError(parserObject);
                        output.add(parserObject);
                    }
                } else {
                    if (client.getAvailableMovies().contains(selectedMovie)) {
                        parserObject.putNull("error");
                        JsonParser.parseMovie(parserObject.putArray("currentMoviesList"),
                                "name", selectedMovie);
                        JsonParser.parseClient(parserObject);
                        output.add(parserObject);

                        client.changePage(changePage);
                        client.setSeeMovie(selectedMovie);
                    } else {
                        JsonParser.parseBasicError(parserObject);
                        output.add(parserObject);
                    }
                }
            }
        } else if (changePage == PageType.UPGRADES) {
            if ((clientPage != PageType.AUTH) && (clientPage != PageType.DETAILS)) {
                parserObject.put("error", "Error");
                parserObject.putArray("currentMoviesList");
                JsonParser.parseClient(parserObject);
                output.add(parserObject);
            } else {
                client.changePage(changePage);
            }
        }
    }
}
