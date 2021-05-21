package common.commands;

import common.content.SpaceMarine;
import server.CollectionManager;

public class RemoveGreater extends Command {
    public RemoveGreater(boolean newbie, String login, String password, SpaceMarine smArg) {
        super(newbie, login, password);
        this.smArg = smArg;
    }

    @Override
    public String execute(CollectionManager cm) {
        return cm.removeGreater(smArg, user.getLogin());
    }
}
