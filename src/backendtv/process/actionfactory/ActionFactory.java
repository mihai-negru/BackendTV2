package backendtv.process.actionfactory;

import backendtv.process.actiontype.*;
import datafetch.ActionFetch;

/**
 * <p>Main class of creating the commands for the server processor.</p>
 *
 * <p>The Class follows a factory design pattern, to encapsulate the
 * creation of the command. Server does not care what command is
 * executing so the creation of the command is hidden from the server.</p>
 *
 * <p>The commands are created based on their type from the input data.</p>
 *
 * @since 1.0.0
 * @author Mihai Negru
 */
public final class ActionFactory {

    /**
     * <p>Creates an command(action) from the specified
     * flags from the input data.</p>
     *
     * @param actionInfo information about one action to be created.
     * @return a new {@code ActionCommand} object to be executed, or
     * null if the flags are unknown.
     */
    public ActionCommand createAction(final ActionFetch actionInfo) {
        final String actionType = actionInfo.getType().toLowerCase();
        if (actionType.equals("on page")) {
            return switch (actionInfo.getFeature()) {
                case "register" -> new RegisterAction(actionInfo);
                case "login" -> new LoginAction(actionInfo);
                case "search" -> new SearchAction(actionInfo);
                case "filter" -> new FilterAction(actionInfo);
                case "buy tokens" -> new BuyTokensAction(actionInfo);
                case "buy premium account" -> new BuyPremiumAction();
                case "purchase" -> new PurchaseAction(actionInfo);
                case "watch" -> new WatchAction(actionInfo);
                case "like" -> new LikeAction(actionInfo);
                case "rate" -> new RateAction(actionInfo);
                case "subscribe" -> new SubscribeAction(actionInfo);
                default -> null;
            };
        }

        if (actionType.equals("back")) {
            return new ChangePageBackAction();
        }

        if (actionType.equals("database")) {
            return new AdminDatabaseAction(actionInfo);
        }

        return new ChangePageAction(actionInfo);
    }
}
