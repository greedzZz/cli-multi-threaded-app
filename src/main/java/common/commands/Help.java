package common.commands;

import server.CollectionManager;

public class Help extends Command {
    public Help(boolean newbie, String login, String password) {
        super(newbie, login, password);
    }

    @Override
    public String execute(CollectionManager cm) {
        return cm.help();
    }
}
