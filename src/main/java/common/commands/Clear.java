package common.commands;

import server.CollectionManager;

public class Clear extends Command {
    public Clear(boolean newbie, String login, String password) {
        super(newbie, login, password);
    }

    @Override
    public String execute(CollectionManager cm) {
        return cm.clear(user.getLogin());
    }
}
