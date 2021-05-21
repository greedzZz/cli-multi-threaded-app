package common.commands;

import server.CollectionManager;

public class GroupCountingByCoordinates extends Command {
    public GroupCountingByCoordinates(boolean newbie, String login, String password) {
        super(newbie, login, password);
    }

    @Override
    public String execute(CollectionManager cm) {
        return cm.groupCountingByCoordinates();
    }
}
