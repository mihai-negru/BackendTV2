package backendtv.process.actiontype.adminactions;

/**
 * <p>The basic interface that characterizes a strategy
 * for the database. The strategies are allowed just by
 * the administrators of the main server.</p>
 *
 * @since 2.0.0
 * @author Mihai Negru
 */
@FunctionalInterface
public interface DatabaseStrategy {

    /**
     * <p>Applies the operation on the database.</p>
     * @return true if the operation applied successfully or
     * false otherwise.
     */
    boolean apply();
}
