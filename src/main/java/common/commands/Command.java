package common.commands;

import common.User;
import common.content.Chapter;
import common.content.SpaceMarine;
import server.CollectionManager;

import java.io.Serializable;

public abstract class Command implements Serializable {
    protected String strArg;
    protected Integer intArg;
    protected SpaceMarine smArg;
    protected Chapter chapArg;
    protected User user;

    public Command(boolean newbie, String login, String password) {
        user = new User(newbie, login, password);
    }

    public User getUser() {
        return user;
    }

    public abstract String execute(CollectionManager cm);
}
