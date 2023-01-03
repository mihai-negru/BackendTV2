package backendtv.process.actiontype;

import com.fasterxml.jackson.databind.node.ArrayNode;

public final class ChangePageBackAction implements ActionCommand {
    public ChangePageBackAction() {
        // for now nothing
    }

    @Override
    public void execute(ArrayNode output) {
        // For now nothing
        System.out.println("Executed back action");
    }
}
