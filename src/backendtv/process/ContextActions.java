package backendtv.process;

import backendtv.process.actionfactory.ActionFactory;
import backendtv.process.actiontype.ActionCommand;
import backendtv.process.actiontype.clientactions.RecommendationAction;
import com.fasterxml.jackson.databind.node.ArrayNode;
import datafetch.ActionFetch;

import java.util.LinkedList;
import java.util.List;

/**
 * <p>A context class that maintains the execution of the
 * server actions.</p>
 *
 * <p>The class follows a command design pattern and uses a
 * factory design pattern to create the commands(actions) for
 * the execution.</p>
 *
 * <p>Commands(Actions) are executes sequentially maintaining
 * the order from the input data so no strategy design pattern
 * is needed.</p>
 *
 * @since 1.0.0
 * @author Mihai Negru
 */
public final class ContextActions {
    private final List<ActionCommand> readyQueue;
    private final ArrayNode output;

    /**
     * <p>Creates all the commands from the input</p>
     *
     * @param initInput {@code List} of actions data to create
     *                              all the commands.
     * @param initOutput main array node to print the output
     *                   to the Json File.
     */
    public ContextActions(final List<ActionFetch> initInput, final ArrayNode initOutput) {
        final ActionFactory generator = new ActionFactory();

        readyQueue = new LinkedList<>();
        initInput.forEach(actionInfo -> readyQueue.add(generator.createAction(actionInfo)));

        output = initOutput;
    }

    /**
     * <p>Checks if server can process the next action.</p>
     *
     * @return true if next action exists and is ready for execution,
     * or false if no more actions are left in the ready queue.
     */
    public boolean hasNext() {
        return !readyQueue.isEmpty();
    }

    /**
     * <p>Executes the next command(action) from
     * the ready queue.</p>
     *
     * <p>User should first check if there exists an
     * action left in the ready queue.</p>
     *
     * <p>Works as an iterator.</p>
     */
    public void next() {
        readyQueue.remove(0).execute(output);
    }

    /**
     * <p>Generate and execute an action that
     * gives to the current premium client
     * a movie for recommendation.</p>
     */
    public void premiumBenefits() {
        new RecommendationAction().execute(output);
    }
}
