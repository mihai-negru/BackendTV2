package backendtv.process.actionfactory;

import backendtv.process.actiontype.ActionCommand;
import backendtv.process.actiontype.BuyPremiumAction;
import backendtv.process.actiontype.BuyTokensAction;
import backendtv.process.actiontype.ChangePageAction;
import backendtv.process.actiontype.FilterAction;
import backendtv.process.actiontype.LikeAction;
import backendtv.process.actiontype.LoginAction;
import backendtv.process.actiontype.PurchaseAction;
import backendtv.process.actiontype.RateAction;
import backendtv.process.actiontype.RegisterAction;
import backendtv.process.actiontype.SearchAction;
import backendtv.process.actiontype.WatchAction;
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
        if (actionInfo.getType().equalsIgnoreCase("on page")) {
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
                default -> null;
            };
        }

        return new ChangePageAction(actionInfo);
    }
}
