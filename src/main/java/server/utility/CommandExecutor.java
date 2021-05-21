package server.utility;

import common.commands.Command;
import server.CollectionManager;
import server.Server;

import java.sql.SQLException;

public class CommandExecutor implements Runnable {
    private final Command command;
    private final Server server;
    private final CollectionManager collectionManager;
    private final UserValidator userValidator;

    public CommandExecutor(Command command, Server server, CollectionManager collectionManager, UserValidator userValidator) {
        this.command = command;
        this.server = server;
        this.collectionManager = collectionManager;
        this.userValidator = userValidator;
    }

    @Override
    public void run() {
        try {
            if (userValidator.validate(command.getUser())) {
                String answer = server.executeCommand(command, collectionManager);
                new Thread(() -> server.sendAnswer(answer)).start();
            } else if (command.getUser().isNewbie()) {
                server.sendAnswer("Unfortunately, a user with this login is already registered.\n");
            } else {
                server.sendAnswer("Sorry, the login/password is incorrect.");
            }

        } catch (SQLException s) {
            server.sendAnswer("A database access error has occurred or connection has closed.\n");
        }
    }
}
