package common;

import java.io.Serializable;

public class User implements Serializable {
    private final boolean newbie;
    private final String login;
    private final String password;

    public User(boolean newbie, String login, String password) {
        this.newbie = newbie;
        this.login = login;
        this.password = password;
    }

    public boolean isNewbie() {
        return newbie;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
