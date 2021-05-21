package common.commands;

import server.CollectionManager;

public class FilterStartsWithName extends Command {
    public FilterStartsWithName(boolean newbie, String login, String password, String strArg) {
        super(newbie, login, password);
        this.strArg = strArg;
    }

    @Override
    public String execute(CollectionManager cm) {
        return cm.filterStartsWithName(strArg);
    }
}
