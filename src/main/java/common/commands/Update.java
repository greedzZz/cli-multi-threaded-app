package common.commands;

import common.content.SpaceMarine;
import server.CollectionManager;

public class Update extends Command {
    public Update(boolean newbie, String login, String password, Integer intArg, SpaceMarine smArg) {
        super(newbie, login, password);
        this.intArg = intArg;
        this.smArg = smArg;
    }

    @Override
    public String execute(CollectionManager cm) {
        return cm.update(intArg, smArg, user.getLogin());
    }
}
