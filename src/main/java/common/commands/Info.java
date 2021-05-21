package common.commands;

import server.CollectionManager;

public class Info extends Command {
    public Info(boolean newbie, String login, String password) {
        super(newbie, login, password);
    }

    @Override
    public String execute(CollectionManager cm) {
        return cm.info();
    }
}
