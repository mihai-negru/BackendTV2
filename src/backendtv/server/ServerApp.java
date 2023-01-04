package backendtv.server;

import backendtv.client.Client;
import projectutils.ObserverHandler;
import backendtv.process.ContextActions;
import backendtv.storage.Database;
import com.fasterxml.jackson.databind.node.ArrayNode;
import datafetch.DataFetch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * <p>Singleton class maintaining basic functionalities of a real server.</p>
 *
 * <p>The Class follows the singleton design pattern, because we should
 * have just one instance of a server that every user can access. For now
 * the server is NOT a multicore server, meaning that one user can
 * access the server at a time. For future implementations support
 * for multicore server may be added.</p>
 *
 * @since 1.0.0
 * @author Mihai Negru
 */
public final class ServerApp implements ObserverHandler {
    private final Database database;
    private ContextActions actions;
    private Client activeClient;

    /**
     * <p>Connects to the database</p>
     */
    private ServerApp() {
        database = Database.connect();
    }

    private static class ServerAppHelper {
        private static final ServerApp INSTANCE = new ServerApp();
    }

    /**
     * <p>Connects a client to the server.</p>
     *
     * @return an instance of the {@code ServerApp}
     * class.
     */
    public static ServerApp connect() {
        return ServerAppHelper.INSTANCE;
    }

    /**
     * <p>Initializes the main functionality when a client is
     * connecting to the server.</p>
     *
     * @param input input data object for database and actions processing.
     * @param output output data object to print the server messages.
     */
    public void start(final DataFetch input, final ArrayNode output) {
        init(input, output);
        process();
        close();
    }

    /**
     * <p>Initializes the database according to the input data,
     * initializes the actions processor and creates a guest
     * and connects the new client accessing the server as a guess.</p>
     *
     * @param input input data object for database and actions processing.
     * @param output output data object to print the server messages.
     */
    private void init(final DataFetch input, final ArrayNode output) {
        initDatabase(input);
        initActions(input, output);
        connectGuest();
    }

    /**
     * <p>Fetches the input data and populates the database with
     * all the data extracted from the input.</p>
     * @param input input data object for database processing.
     */
    private void initDatabase(final DataFetch input) {
        if (database.createCollection("users")) {
            final var users = database.collection("users");

            final var usersInput = input.getUsers();

            for (var userInput : usersInput) {
                final var userCredentials = userInput.getCredentials();
                users.insert(Map.ofEntries(
                        Map.entry("name", userCredentials.getName()),
                        Map.entry("password", userCredentials.getPassword()),
                        Map.entry("accountType", userCredentials.getAccountType()),
                        Map.entry("country", userCredentials.getCountry()),
                        Map.entry("balance", Integer.toString(userCredentials.getBalance())),
                        Map.entry("tokensCount", "0"),
                        Map.entry("numFreePremiumMovies", "15"),
                        Map.entry("purchasedMovies", "null"),
                        Map.entry("watchedMovies", "null"),
                        Map.entry("likedMovies", "null"),
                        Map.entry("ratedMovies", "null"),
                        Map.entry("notifications", "null"),
                        Map.entry("subscribedGenres", "null")
                ));
            }
        }

        if (database.createCollection("movies")) {
            final var movies = database.collection("movies");

            final var moviesInput = input.getMovies();

            for (var movieInput : moviesInput) {
                movies.insert(Map.of(
                        "name", movieInput.getName(),
                        "year", Integer.toString(movieInput.getYear()),
                        "duration", Integer.toString(movieInput.getDuration()),
                        "genres", String.join(",", movieInput.getGenres()),
                        "actors", String.join(",", movieInput.getActors()),
                        "countriesBanned", String.join(",", movieInput.getCountriesBanned()),
                        "numLikes", "0",
                        "rating", "Unknown:0",
                        "numRatings", "0"
                ));
            }
        }
    }

    /**
     * <p>Fetches the input data for the actions and creates
     * the actions processor of the server.</p>
     *
     * @param input input data object for actions processing.
     * @param output output data object to print the server messages.
     */
    private void initActions(final DataFetch input, final ArrayNode output) {
        actions = new ContextActions(input.getActions(), output);
    }

    /**
     * <p>Starts the actions processor and waits until
     * all the actions are solved.</p>
     */
    private void process() {
        while (actions.hasNext()) {
            actions.next();
        }

        if (activeClient.getStatus() && activeClient.getAccountType().equals("premium")) {
            actions.premiumBenefits();
        }
    }

    /**
     * <p>Close the connection of the client with
     * the server.</p>
     */
    private void close() {
        database.dropAll();
        actions = null;
        activeClient = null;
    }

    /**
     * <p>Connects a client to the main homepage of the server.</p>
     *
     * @param clientName id of the client to identify in the database.
     * @param clientPassword password of the client to check credentials.
     * @return true if the client was connected successfully or false
     * if the connection is impossible to make.
     */
    public boolean loginClient(final String clientName, final String clientPassword) {
        final var usersCollection = database.collection("users");

        if (usersCollection == null) {
            return false;
        }

        final var clientData = usersCollection.findOne("name", clientName);
        if (clientData.containsKey("password")
                && clientData.get("password").equals(clientPassword)) {

            final var moviesCollection = database.collection("movies");

            if (moviesCollection == null) {
                return false;
            }

            final List<String> availableMovies = new ArrayList<>();
            final List<Map<String, String>> movies = moviesCollection.getMembers();

            if (movies != null) {
                for (var movie : movies) {
                    final var countriesBanned = new ArrayList<>(
                            Arrays.asList(movie.get("countriesBanned").split(",")));

                    if (!countriesBanned.contains(clientData.get("country"))) {
                        availableMovies.add(movie.get("name"));
                    }
                }
            }

            activeClient = new Client(clientData, availableMovies);
            return true;
        }

        return false;
    }

    /**
     * <p>Registers a new client in the database and connects him/her
     * immediately to the main homepage.</p>
     *
     * @param clientName id of the client to register.
     * @param clientPassword password of the client to register.
     * @param clientAccountType selected account type.
     * @param clientCountry selected country of the client.
     * @param clientBalance initial balance of the client.
     * @return true if the client was registered and logged successfully
     * on the server or false if the registration failed.
     */
    public boolean registerClient(final String clientName, final String clientPassword,
                                  final String clientAccountType, final String clientCountry,
                                  final String clientBalance) {
        final var usersCollection = database.collection("users");

        if (usersCollection == null) {
            return false;
        }

        final var checkClient = usersCollection.findOne("name", clientName);

        if (checkClient != null) {
            return false;
        }

        usersCollection.insert(Map.ofEntries(
                Map.entry("name", clientName),
                Map.entry("password", clientPassword),
                Map.entry("accountType", clientAccountType),
                Map.entry("country", clientCountry),
                Map.entry("balance", clientBalance),
                Map.entry("tokensCount", "0"),
                Map.entry("numFreePremiumMovies", "15"),
                Map.entry("purchasedMovies", "null"),
                Map.entry("watchedMovies", "null"),
                Map.entry("likedMovies", "null"),
                Map.entry("ratedMovies", "null"),
                Map.entry("notifications", "null"),
                Map.entry("subscribedGenres", "null")
        ));

        return loginClient(clientName, clientPassword);
    }

    /**
     * <p>Logouts an active client on the server and redirects
     * on the guest page.</p>
     */
    public void logoutClient() {
        final var usersCollection = database.collection("users");

        String purchasedMovies = String.join(",", activeClient.getPurchasedMovies());
        if (purchasedMovies.isEmpty() || purchasedMovies.isBlank()) {
            purchasedMovies = "null";
        }

        String watchedMovies = String.join(",", activeClient.getWatchedMovies());
        if (watchedMovies.isEmpty() || watchedMovies.isBlank()) {
            watchedMovies = "null";
        }

        String likedMovies = String.join(",", activeClient.getLikedMovies());
        if (likedMovies.isEmpty() || likedMovies.isBlank()) {
            likedMovies = "null";
        }

        String ratedMovies = String.join(",", activeClient.getRatedMovies());
        if (ratedMovies.isEmpty() || ratedMovies.isBlank()) {
            ratedMovies = "null";
        }

        String notifications = String.join(",", activeClient.getNotifications());
        if (notifications.isEmpty() || notifications.isBlank()) {
            notifications = "null";
        }

        String subscribedGenres = String.join(",", activeClient.getSubscribedGenres());
        if (subscribedGenres.isEmpty() || subscribedGenres.isBlank()) {
            subscribedGenres = "null";
        }

        final boolean errCode = usersCollection
                .modifyMember("name", activeClient.getName(), Map.ofEntries(
                        Map.entry("name", activeClient.getName()),
                        Map.entry("password", activeClient.getPassword()),
                        Map.entry("accountType", activeClient.getAccountType()),
                        Map.entry("country", activeClient.getCountry()),
                        Map.entry("balance", Integer.toString(activeClient.getBalance())),
                        Map.entry("tokensCount", Integer.toString(activeClient.getTokensCount())),
                        Map.entry("numFreePremiumMovies", Integer.toString(
                                activeClient.getNumFreePremiumMovies())),
                        Map.entry("purchasedMovies", purchasedMovies),
                        Map.entry("watchedMovies", watchedMovies),
                        Map.entry("likedMovies", likedMovies),
                        Map.entry("ratedMovies", ratedMovies),
                        Map.entry("notifications", notifications),
                        Map.entry("subscribedGenres", subscribedGenres)
                ));

        if (errCode) {
            activeClient = new Client();
        }
    }

    /**
     * <p>Connects a guest to the server.</p>
     */
    public void connectGuest() {
        activeClient = new Client();
    }

    /**
     * <p>Fetches the active client from the server.</p>
     * @return the active client at this moment of time.
     */
    public Client fetchActiveClient() {
        return activeClient;
    }

    /**
     * <p>Fetches the database of the server.</p>
     *
     * @return instance of the current used database.
     */
    public Database fetchDatabase() {
        return database;
    }

    @Override
    public void updateNotifications(final String message, final String movieName, final List<String> genres,
                                    final List<String> bannedCountries) {
        activeClient.updateNotifications(message, movieName, genres, bannedCountries);

        final var users = database.collection("users").getMembers();

        if (users == null) {
            return;
        }

        for (var user : users) {
            if (bannedCountries.contains(user.get("country"))) {
                continue;
            }

            if (message.equals("DELETE")) {
                var tempMoviesString = user.get("purchasedMovies");
                if (!tempMoviesString.equals("null")) {
                    List<String> tempMoviesList = new ArrayList<>(Arrays.asList(tempMoviesString.split(",")));

                    if (tempMoviesList.contains(movieName)) {
                        final String userAccountType = user.get("accountType");

                        if (userAccountType.equals("premium")) {
                            user.put("numFreePremiumMovies",
                                    Integer.toString(Integer.parseInt(user.get("numFreePremiumMovies")) + 1));
                        } else if (userAccountType.equals("standard")) {
                            user.put("tokensCount",
                                    Integer.toString(Integer.parseInt(user.get("tokensCount")) + 2));
                        }

                        tempMoviesList.remove(movieName);
                        user.put("purchasedMovies", String.join(",", tempMoviesList));

                        tempMoviesString = user.get("watchedMovies");
                        if (!tempMoviesString.equals("null")) {
                            tempMoviesList = new ArrayList<>(Arrays.asList(tempMoviesString.split(",")));

                            tempMoviesList.remove(movieName);
                            user.put("watchedMovies", String.join(",", tempMoviesList));
                        }

                        tempMoviesString = user.get("likedMovies");
                        if (!tempMoviesString.equals("null")) {
                            tempMoviesList = new ArrayList<>(Arrays.asList(tempMoviesString.split(",")));

                            tempMoviesList.remove(movieName);
                            user.put("likedMovies", String.join(",", tempMoviesList));
                        }

                        tempMoviesString = user.get("ratedMovies");
                        if (!tempMoviesString.equals("null")) {
                            tempMoviesList = new ArrayList<>(Arrays.asList(tempMoviesString.split(",")));

                            tempMoviesList.remove(movieName);
                            user.put("ratedMovies", String.join(",", tempMoviesList));
                        }
                    }
                }
            }

            for (var genre : user.get("subscribedGenres").split(",")) {
                if (genres.contains(genre)) {
                    final String userNotifications = user.get("notifications");

                    if (userNotifications.equals("null")) {
                        user.put("notifications", movieName + ";" + message);
                    } else {
                        user.put("notifications", userNotifications + "," + movieName + ";" + message);
                    }

                    break;
                }
            }

            database.collection("users").modifyMember("name", user.get("name"), user);
        }
    }
}
