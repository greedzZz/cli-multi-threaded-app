package common.commands;

import common.content.Chapter;
import server.CollectionManager;

public class FilterByChapter extends Command {
    public FilterByChapter(boolean newbie, String login, String password, Chapter chapArg) {
        super(newbie, login, password);
        this.chapArg = chapArg;
    }

    @Override
    public String execute(CollectionManager cm) {
        return cm.filterByChapter(chapArg);
    }
}
