package backendtv.notification;

import java.util.List;

public interface ObserverHandler {
    void updateNotifications(String message, String movieName, List<String> genres, List<String> bannedCountries);
}
