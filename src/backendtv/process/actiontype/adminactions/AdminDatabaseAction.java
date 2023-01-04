package backendtv.process.actiontype.adminactions;

import backendtv.parser.JsonParser;
import backendtv.process.actiontype.ActionCommand;
import com.fasterxml.jackson.databind.node.ArrayNode;
import datafetch.ActionFetch;

/**
 * <p>Specific Command Class to execute one of the strategies
 * for the database (add or delete).</p>
 *
 * @since 2.0.0
 * @author Mihai Negru
 */
public final class AdminDatabaseAction implements ActionCommand {
    private final DatabaseStrategy operation;

    /**
     * <p>Selects one strategy for database operations
     * that just an admin can execute.</p>
     *
     * @param actionInfo information in order to execute the strategy.
     */
    public AdminDatabaseAction(final ActionFetch actionInfo) {
        operation = switch (actionInfo.getFeature().toLowerCase()) {
            case "add" -> new AddDatabaseOperation(actionInfo);
            case "delete" -> new DeleteDatabaseOperation(actionInfo);
            default -> null;
        };
    }

    /**
     * <p>Executes the selected strategy and prints
     * the error message in the logger file if
     * something wrong happened.</p>
     *
     * @param output main array node to parse the output data
     *               to a Json File.
     */
    @Override
    public void execute(final ArrayNode output) {
        final var parserObject = output.objectNode();
        if (operation == null) {
            JsonParser.parseBasicError(parserObject);
            output.add(parserObject);
        } else {
            if (!operation.apply()) {
                JsonParser.parseBasicError(parserObject);
                output.add(parserObject);
            }
        }
    }
}
