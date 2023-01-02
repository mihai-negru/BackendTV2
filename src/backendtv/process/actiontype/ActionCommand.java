package backendtv.process.actiontype;

import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * <p>Interface to support the command design pattern.</p>
 *
 * <p>Interface binds all the specific commands for the
 * server processor.</p>
 *
 * @since 1.0.0
 * @author Mihai Negru
 */
@FunctionalInterface
public interface ActionCommand {

    /**
     * <p>Executes the command started by the server processor.</p>
     *
     * @param output main array node to parse the output data
     *               to a Json File.
     */
    void execute(ArrayNode output);
}
