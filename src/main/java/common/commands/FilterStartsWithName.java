package common.commands;

import server.CollectionManager;

public class FilterStartsWithName extends Command {
    public FilterStartsWithName(String strArg) {
        this.strArg = strArg;
    }

    @Override
    public String execute(CollectionManager cm) {
        return cm.filterStartsWithName(strArg);
    }
}
