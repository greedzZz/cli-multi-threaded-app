package common.commands;

import server.CollectionManager;

public class Show extends Command {
    public Show(boolean newbie, String login, String password) {
        super(newbie, login, password);
    }

    @Override
    public String execute(CollectionManager cm) {
        return cm.show();
    }
}
