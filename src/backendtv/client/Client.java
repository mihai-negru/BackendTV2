package backendtv.client;

import projectutils.ObserverHandler;
import backendtv.pagestype.PageType;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Map;

/**
 * <p>Simple class maintaining the information about a server client.</p>
 *
 * <p>The Client can be of two types guest and active, when in guest mode
 * the client cannot perform any action on the server and can only visualize
 * the server interactions.</p>
 *
 * <p>For further implementation of the server with multiple clients connected
 * at the same time, this class encapsulated the class information and will
 * not cause any bad synchronizations when talking about multicore.</p>
 *
 * @since 1.0.0
 * @author Mihai Negru
 */
public final class Client implements ObserverHandler {
    private static final int PREMIUM_COST = 10;
    private PageType loadedPage;
    private final String name;
    private final String password;
    private String accountType;
    private final String country;
    private String seeMovie;
    private int balance;
    private int tokensCount;
    private int numFreePremiumMovies;
    private final List<String> purchasedMovies;
    private final List<String> watchedMovies;
    private final List<String> likedMovies;
    private final List<String> ratedMovies;
    private final List<String> availableMovies;
    private final List<String> filteredMovies;
    private final List<String> notifications;
    private final List<String> subscribedGenres;
    private final boolean isActive;
    private boolean areMoviesFiltered;
    private final Deque<PageType> pageStack;

    /**
     * <p>Create an active Client for the server.</p>
     *
     * @param clientData {@code Map} object containing all the
     *                              data needed for the client.
     * @param initAvailableMovies {@code List} of String containing
     *                            the names of all available
     *                            movies that a client can watch.
     */
    public Client(final Map<String, String> clientData, final List<String> initAvailableMovies) {
        loadedPage = PageType.AUTH;

        name = clientData.get("name");
        password = clientData.get("password");
        accountType = clientData.get("accountType");
        country = clientData.get("country");
        seeMovie = null;

        balance = Integer.parseInt(clientData.get("balance"));
        tokensCount = Integer.parseInt(clientData.get("tokensCount"));
        numFreePremiumMovies = Integer.parseInt(clientData.get("numFreePremiumMovies"));

        final String clientBoughtMovies = clientData.get("purchasedMovies");
        if (clientBoughtMovies.equals("null")) {
            purchasedMovies = new ArrayList<>();
        } else {
            purchasedMovies = new ArrayList<>(Arrays.asList(clientBoughtMovies.split(",")));
        }

        final String clientWatchedMovies = clientData.get("watchedMovies");
        if (clientWatchedMovies.equals("null")) {
            watchedMovies = new ArrayList<>();
        } else {
            watchedMovies = new ArrayList<>(Arrays.asList(clientWatchedMovies.split(",")));
        }

        final String clientLikedMovies = clientData.get("likedMovies");
        if (clientLikedMovies.equals("null")) {
            likedMovies = new ArrayList<>();
        } else {
            likedMovies = new ArrayList<>(Arrays.asList(clientWatchedMovies.split(",")));
        }

        final String clientRatedMovies = clientData.get("ratedMovies");
        if (clientRatedMovies.equals("null")) {
            ratedMovies = new ArrayList<>();
        } else {
            ratedMovies = new ArrayList<>(Arrays.asList(clientWatchedMovies.split(",")));
        }

        availableMovies = new ArrayList<>(initAvailableMovies);
        filteredMovies = new ArrayList<>();

        final String clientNotifications = clientData.get("notifications");
        if (clientNotifications.equals("null")) {
            notifications = new ArrayList<>();
        } else {
            notifications = new ArrayList<>(Arrays.asList(clientNotifications.split(",")));
        }

        final String clientSubscribedGenres = clientData.get("subscribedGenres");
        if (clientSubscribedGenres.equals("null")) {
            subscribedGenres = new ArrayList<>();
        } else {
            subscribedGenres = new ArrayList<>(Arrays.asList(clientNotifications.split(",")));
        }

        isActive = true;
        areMoviesFiltered = false;

        pageStack = new ArrayDeque<>();
    }

    /**
     * <p>Creates a guest client for the server.</p>
     */
    public Client() {
        loadedPage = PageType.NO_AUTH;

        name = null;
        password = null;
        accountType = null;
        country = null;
        seeMovie = null;

        balance = -1;
        tokensCount = -1;
        numFreePremiumMovies = -1;

        purchasedMovies = null;
        watchedMovies = null;
        likedMovies = null;
        ratedMovies = null;
        availableMovies = null;
        filteredMovies = null;
        notifications = null;

        subscribedGenres = null;

        isActive = false;
        areMoviesFiltered = false;

        pageStack = null;
    }

    public PageType getLoadedPage() {
        return loadedPage;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getAccountType() {
        return accountType;
    }

    public String getCountry() {
        return country;
    }

    public String getSeeMovie() {
        return seeMovie;
    }

    public int getBalance() {
        return balance;
    }

    public int getTokensCount() {
        return tokensCount;
    }

    public int getNumFreePremiumMovies() {
        return numFreePremiumMovies;
    }

    public List<String> getPurchasedMovies() {
        return purchasedMovies;
    }

    public List<String> getWatchedMovies() {
        return watchedMovies;
    }

    public List<String> getLikedMovies() {
        return likedMovies;
    }

    public List<String> getRatedMovies() {
        return ratedMovies;
    }

    public List<String> getAvailableMovies() {
        return availableMovies;
    }

    public List<String> getFilteredMovies() {
        return filteredMovies;
    }

    public List<String> getNotifications() {
        return notifications;
    }

    public List<String> getSubscribedGenres() {
        return subscribedGenres;
    }

    /**
     * <p>Adds one genre to the subscribed genres.</p>
     *
     * @param genre new genre to add for the subscription.
     * @return true if the genre was added successfully or
     * false if the genre already existed or the genre is not valid.
     */
    public boolean subscribeToGenre(final String genre) {
        if ((genre == null) || (subscribedGenres.contains(genre))) {
            return false;
        }

        return subscribedGenres.add(genre);
    }

    /**
     * <p>Sets the current movie selected for the
     * details page.</p>
     *
     * @param movie movie to see the details.
     */
    public void setSeeMovie(final String movie) {
        if (movie != null) {
            seeMovie = movie;
        }
    }

    /**
     * <p>Checks if the client is a guest or an active client.</p>
     * @return true if client is active or false if it is a guest.
     */
    public boolean getStatus() {
        return isActive;
    }

    /**
     * <p>Changes the current loaded page to another page.
     * If the page type is known.</p>
     *
     * @param page new page to load for the current client.
     */
    public void changePage(final PageType page) {
        if (page != PageType.UNKNOWN) {
            if ((loadedPage != PageType.LOGIN)
                    && (loadedPage != PageType.NO_AUTH)
                    && (loadedPage != PageType.REGISTER)) {
                pageStack.push(loadedPage);
            }

            loadedPage = page;
        }
    }

    /**
     * <p>Changes the current loaded page to the previous loaded page.</p>
     *
     * @return true if the loaded page went back, or false if the operation
     * is not allowed.
     */
    public boolean changePageBack() {
        if (pageStack.isEmpty()) {
            return false;
        }

        final var lastPage = loadedPage;
        loadedPage = pageStack.pop();

        if ((lastPage == PageType.DETAILS) && (loadedPage == PageType.MOVIES)) {
            setMoviesAsNonFiltered();
        }

        return true;
    }

    /**
     * <p>Prepares the client for a filter action.</p>
     */
    public void filterMovies() {
        if (!filteredMovies.isEmpty()) {
            filteredMovies.clear();
        }

        areMoviesFiltered = true;
    }

    /**
     * <p>Adds a movie to the filter movies list.</p>
     *
     * @param movieName movie to add in the filter list.
     */
    public void addFilteredMovie(final String movieName) {
        filteredMovies.add(movieName);
    }

    /**
     * <p>Checks if movies are marked as filtered.</p>
     *
     * @return true if movies are filtered or false otherwise.
     */
    public boolean areMoviesFiltered() {
        return areMoviesFiltered;
    }

    /**
     * <p>Marks movies as non-filtered.</p>
     */
    public void setMoviesAsNonFiltered() {
        areMoviesFiltered = false;
    }

    /**
     * <p>Buys an amount of tokens for the current client.</p>
     *
     * @param tokens number of tokens to buy.
     * @return true if the purchase went successfully or false
     * if client has not enough money to buy tokens.
     */
    public boolean buyTokens(final int tokens) {
        if (balance < tokens) {
            return false;
        }

        balance -= tokens;
        tokensCount += tokens;

        return true;
    }

    /**
     * <p>Buys a premium account for the current client.</p>
     *
     * @return true if purchase went successfully or false
     * if the client has not enough tokens to buy the
     * premium account.
     */
    public boolean buyPremiumAccount() {
        if (tokensCount < PREMIUM_COST) {
            return false;
        }

        tokensCount -= PREMIUM_COST;

        accountType = "premium";

        return true;
    }

    /**
     * <p>Purchases a movie from the available movies list
     * for the current active client.</p>
     *
     * @param movieName movie to buy for the client.
     * @return true if acquisition went successfully or
     * false if client has not enough tokens or enough
     * free premium movies.
     */
    public boolean purchaseMovie(final String movieName) {
        if (movieName == null) {
            return false;
        }

        if (!availableMovies.contains(movieName)) {
            return false;
        }

        if (purchasedMovies.contains(movieName)) {
            return false;
        }

        if (accountType.equals("premium")) {
            if (numFreePremiumMovies <= 0 && tokensCount < 2) {
                return false;
            }

            if (numFreePremiumMovies >= 1) {
                --numFreePremiumMovies;
            } else {
                tokensCount -= 2;
            }
        } else if (accountType.equals("standard")) {
            if (tokensCount < 2) {
                return false;
            }

            tokensCount -= 2;
        } else {
            return false;
        }

        return purchasedMovies.add(movieName);
    }

    /**
     * <p>Watches a movie from the list of the
     * purchased movies.</p>
     *
     * @param movieName movie to watch.
     * @return true if client can watch the movie,
     * or false if client has not purchased the movie.
     */
    public boolean watchMovie(final String movieName) {
        if (movieName == null) {
            return false;
        }

        if (!purchasedMovies.contains(movieName)) {
            return false;
        }

        if (watchedMovies.contains(movieName)) {
            return true;
        }

        return watchedMovies.add(movieName);
    }

    /**
     * <p>Likes a movie from the list of the
     * watched movies.</p>
     *
     * @param movieName movie to like.
     * @return true if client can like the movie,
     * or false if client has not watched the movie.
     */
    public boolean likeMovie(final String movieName) {
        if (movieName == null) {
            return false;
        }

        if (!watchedMovies.contains(movieName)) {
            return false;
        }

        if (likedMovies.contains(movieName)) {
            return true;
        }

        return likedMovies.add(movieName);
    }

    /**
     * <p>Checks if the client has already rated the selected movie.</p>
     *
     * @param movieName the name of the movie to check.
     * @return true if the movie was already rated, or false otherwise.
     */
    public boolean hasRatedMovie(final String movieName) {
        return ratedMovies.contains(movieName);
    }

    /**
     * <p>Rates a movie from the list of the
     * watched movies.</p>
     *
     * @param movieName movie to rate.
     * @return true if client can rate the movie,
     * or false if client has not watched the movie.
     */
    public boolean rateMovie(final String movieName) {
        if (movieName == null) {
            return false;
        }

        if (!watchedMovies.contains(movieName)) {
            return false;
        }

        if (ratedMovies.contains(movieName)) {
            return true;
        }

        return ratedMovies.add(movieName);
    }

    /**
     * <p>Depending on the message that triggered the method call,
     * the client will add or delete a movie from the available list.
     * For the deletion any instance (name) of the movie will be
     * removed.</p>
     *
     * @param message the message that called the update method.
     * @param movieName the movie that needs to be notified.
     * @param genres movie genres.
     * @param bannedCountries countries that are not allowed to watch the movie.
     */
    @Override
    public void updateNotifications(final String message, final String movieName,
                                    final List<String> genres, final List<String> bannedCountries) {
        if ((message == null) || (movieName == null) || (genres == null)) {
            return;
        }

        if (bannedCountries.contains(country)) {
            return;
        }

        if (message.equals("ADD")) {
            addAvailableMovie(movieName);
        } else if (message.equals("DELETE")) {
            removeAvailableMovie(movieName);
        }

        for (var genre : subscribedGenres) {
            if (genres.contains(genre)) {
                notifications.add(movieName + ";" + message);

                return;
            }
        }
    }

    /**
     * <p>Adds one movie to the available list of movies that
     * the client can access.</p>
     *
     * @param movieName the name of the movie to add to the list.
     */
    public void addAvailableMovie(final String movieName) {
        if (movieName == null) {
            return;
        }

        availableMovies.add(movieName);
    }

    /**
     * <p>Removes one movie from the available list of movies that
     * the client can access.</p>
     *
     * @param movieName the name of the movie to remove from the list.
     */
    public void removeAvailableMovie(final String movieName) {
        if (movieName == null) {
            return;
        }

        if (!availableMovies.contains(movieName)) {
            return;
        }

        availableMovies.remove(movieName);
        purchasedMovies.remove(movieName);
        watchedMovies.remove(movieName);
        likedMovies.remove(movieName);
        ratedMovies.remove(movieName);
        filteredMovies.remove(movieName);

        if (accountType.equals("premium")) {
            ++numFreePremiumMovies;
        } else if (accountType.equals("standard")) {
            tokensCount += 2;
        }
    }

    /**
     * <p>Gets the name of the film to recommend and
     * appends it to the notifications queue of the client
     * as a notification message.</p>
     *
     * @param movieName the name of the movie to recommend or
     *                  "No Recommendation" {@code String} if
     *                  no film can be recommended.
     */
    public void acceptRecommendation(final String movieName) {
        if (movieName == null) {
            return;
        }

        notifications.add(movieName + ";" + "Recommendation");
    }
}
