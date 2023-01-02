package backendtv.storage;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Singleton class maintaining basic functionalities of a database.</p>
 *
 * <p>The Class is a follows the singleton design pattern, because we
 * should not have more than one instance of a database on our server.</p>
 *
 * @since 1.0.0
 * @author Mihai Negru
 */
public final class Database {
    private final Map<String, DataCollection> collections;

    /**
     * <p>Creates the workspace for the database collections.</p>
     */
    private Database() {
        collections = new HashMap<>();
    }

    private static class DatabaseHelper {
        private static final Database INSTANCE = new Database();
    }

    /**
     * <p>Fetches the database instance and returns it.</p>
     *
     * @return the instance of the database class.
     */
    public static Database connect() {
        return DatabaseHelper.INSTANCE;
    }

    /**
     * <p>Creates a new empty collection and assign a name
     * for the collection to identify later.</p>
     *
     * @param collectionName name to assign for the collection.
     * @return true if the collection was created or false otherwise.
     */
    public boolean createCollection(final String collectionName) {
        if (collectionName == null) {
            return false;
        }

        collections.put(collectionName, new DataCollection());

        return true;
    }

    /**
     * <p>Removes a collection from the database.</p>
     *
     * @param collectionName collection name to remove from the
     *                       database.
     * @return true if the collection was removes successfully or
     * false if collection was not found.
     */
    public boolean dropCollection(final String collectionName) {
        if (collectionName == null) {
            return false;
        }

        return collections.remove(collectionName) != null;
    }

    /**
     * <p>Fetches one collection from the database and returns it.</p>
     *
     * @param collectionName collection name to fetch from the database.
     * @return a {@code DataCollection} object if collection was found
     * or null if no such collection exists.
     */
    public DataCollection collection(final String collectionName) {
        if (collectionName == null) {
            return null;
        }

        return collections.get(collectionName);
    }

    /**
     * <p>Removes all collections from the database.</p>
     */
    public void dropAll() {
        collections.clear();
    }
}
