package backendtv.process.actiontype.clientactions;

import backendtv.parser.JsonParser;
import backendtv.pagestype.PageType;
import backendtv.process.actiontype.ActionCommand;
import backendtv.server.ServerApp;
import com.fasterxml.jackson.databind.node.ArrayNode;
import datafetch.ActionFetch;

/**
 * <p>Specific Command class to register a guest as a new
 * active client on the running server.</p>
 *
 * @since 1.0.0
 * @author Mihai Negru
 */
public final class RegisterAction implements ActionCommand {
    private final String registerName;
    private final String registerPassword;
    private final String registerAccountType;
    private final String registerCountry;
    private final String registerBalance;

    /**
     * <p>Extract client's credentials for register.</p>
     *
     * @param actionInfo information about one action to be created.
     */
    public RegisterAction(final ActionFetch actionInfo) {
        registerName = actionInfo.getCredentials().getName();
        registerPassword = actionInfo.getCredentials().getPassword();
        registerAccountType = actionInfo.getCredentials().getAccountType();
        registerCountry = actionInfo.getCredentials().getCountry();
        registerBalance = Integer.toString(actionInfo.getCredentials().getBalance());
    }

    /**
     * <p>Register a guest to a new active client if
     * possible or parses an error message to the Json File.</p>
     *
     * @param output main array node to parse the output data
     *               to a Json File.
     */
    @Override
    public void execute(final ArrayNode output) {
        final var server = ServerApp.connect();

        if (server.fetchActiveClient().getLoadedPage() == PageType.REGISTER) {
            final var parserObject = output.addObject();

            if (!server.registerClient(registerName, registerPassword,
                    registerAccountType, registerCountry,
                    registerBalance)) {

                server.fetchActiveClient().changePage(PageType.NO_AUTH);

                JsonParser.parseBasicError(parserObject);
            } else {
                parserObject.putNull("error");
                parserObject.putArray("currentMoviesList");
                JsonParser.parseClient(parserObject);
            }
        }
    }
}
