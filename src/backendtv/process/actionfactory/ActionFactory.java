package backendtv.process.actionfactory;

import backendtv.process.actiontype.ActionCommand;
import backendtv.process.actiontype.adminactions.AdminDatabaseAction;
import backendtv.process.actiontype.clientactions.BuyPremiumAction;
import backendtv.process.actiontype.clientactions.BuyTokensAction;
import backendtv.process.actiontype.clientactions.LoginAction;
import backendtv.process.actiontype.clientactions.RegisterAction;
import backendtv.process.actiontype.moviesactions.FilterAction;
import backendtv.process.actiontype.moviesactions.LikeAction;
import backendtv.process.actiontype.moviesactions.PurchaseAction;
import backendtv.process.actiontype.moviesactions.RateAction;
import backendtv.process.actiontype.moviesactions.SearchAction;
import backendtv.process.actiontype.moviesactions.SubscribeAction;
import backendtv.process.actiontype.moviesactions.WatchAction;
import backendtv.process.actiontype.pagesactions.ChangePageAction;
import backendtv.process.actiontype.pagesactions.ChangePageBackAction;
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
        return switch (actionInfo.getType().toLowerCase()) {
            case "on page" -> switch (actionInfo.getFeature()) {
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
            case "back" -> new ChangePageBackAction();
            case "database" -> new AdminDatabaseAction(actionInfo);
            case "change page" -> new ChangePageAction(actionInfo);
            default -> null;
        };
    }
}
