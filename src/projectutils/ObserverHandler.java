package projectutils;

import java.util.List;

/**
 * <p>Basic handler for an observer object.</p>
 *
 * @since 2.0.0
 * @author Mihai Negru
 */
@FunctionalInterface
public interface ObserverHandler {

    /**
     * <p>Updates the content for an observer.</p>
     *
     * @param message the message that called the update method.
     * @param movieName the movie that needs to be notified.
     * @param genres movie genres.
     * @param bannedCountries countries that are not allowed to watch the movie.
     */
    void updateNotifications(String message, String movieName,
                             List<String> genres, List<String> bannedCountries);
}
