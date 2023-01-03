package backendtv.process.actiontype.clientactions;

import backendtv.parser.JsonParser;
import backendtv.pagestype.PageType;
import backendtv.process.actiontype.ActionCommand;
import backendtv.server.ServerApp;
import com.fasterxml.jackson.databind.node.ArrayNode;
import datafetch.ActionFetch;

/**
 * <p>Specific Command class to login a guest as an
 * active client on the running server.</p>
 *
 * @since 1.0.0
 * @author Mihai Negru
 */
public final class LoginAction implements ActionCommand {
    private final String loginName;
    private final String loginPassword;

    /**
     * <p>Extract client's credentials for login.</p>
     *
     * @param actionInfo information about one action to be created.
     */
    public LoginAction(final ActionFetch actionInfo) {
        loginName = actionInfo.getCredentials().getName();
        loginPassword = actionInfo.getCredentials().getPassword();
    }

    /**
     * <p>Login a guest to an active client if
     * possible or parses an error message to the Json File.</p>
     *
     * @param output main array node to parse the output data
     *               to a Json File.
     */
    @Override
    public void execute(final ArrayNode output) {
        final var server = ServerApp.connect();

        final var parserObject = output.addObject();
        if (server.fetchActiveClient().getLoadedPage() != PageType.LOGIN) {
            JsonParser.parseBasicError(parserObject);
        } else {
            if (!server.loginClient(loginName, loginPassword)) {
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
