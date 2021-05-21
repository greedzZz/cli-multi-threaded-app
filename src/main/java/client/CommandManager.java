package client;

import client.utility.*;
import common.Serializer;
import common.commands.*;
import common.content.Chapter;
import common.content.SpaceMarine;

import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class CommandManager {
    private final ElementReader elementReader;
    private final ChapterReader chapterReader;
    private final ScriptReader scriptReader;
    private final Serializer serializer;
    private final Authenticator authenticator;

    public CommandManager() {
        this.elementReader = new ElementReader();
        this.chapterReader = new ChapterReader();
        this.serializer = new Serializer();
        this.authenticator = new Authenticator();
        this.scriptReader = new ScriptReader(elementReader, chapterReader, serializer);
    }

    public void readInput(SocketAddress address, DatagramSocket socket) throws SocketTimeoutException {
        CommandSender commandSender = new CommandSender(address, socket);
        AnswerReceiver answerReceiver = new AnswerReceiver(socket, serializer);
        Scanner scanner = new Scanner(System.in);
        System.out.println("Trying to connect...\nPlease, enter a command. (Enter \"help\" to get information about available commands)");
        String command = "";
        while (scanner.hasNextLine() && !command.equals("exit")) {
            String[] input = scanner.nextLine().trim().split(" ");
            command = input[0];
            try {
                switch (command) {
                    case "help":
                        Command help = new Help(authenticator.readNewbie(scanner),
                                authenticator.readLogin(scanner), authenticator.readPassword(scanner));
                        commandSender.send(serializer.serialize(help));
                        System.out.println(answerReceiver.receive());
                        break;
                    case "info":
                        Command info = new Info(authenticator.readNewbie(scanner),
                                authenticator.readLogin(scanner), authenticator.readPassword(scanner));
                        commandSender.send(serializer.serialize(info));
                        System.out.println(answerReceiver.receive());
                        break;
                    case "show":
                        Command show = new Show(authenticator.readNewbie(scanner),
                                authenticator.readLogin(scanner), authenticator.readPassword(scanner));
                        commandSender.send(serializer.serialize(show));
                        System.out.println(answerReceiver.receive());
                        break;
                    case "insert":
                        try {
                            int key = Integer.parseInt(input[1]);
                            if (key < 0) {
                                throw new NumberFormatException();
                            }
                            SpaceMarine sm = elementReader.readElement(scanner);
                            Command insert = new Insert(authenticator.readNewbie(scanner),
                                    authenticator.readLogin(scanner), authenticator.readPassword(scanner), key, sm);
                            commandSender.send(serializer.serialize(insert));
                            System.out.println(answerReceiver.receive());
                        } catch (ArrayIndexOutOfBoundsException e) {
                            System.out.println("To execute this command, you must enter the required argument.");
                        } catch (NumberFormatException e) {
                            System.out.println("Key value must be integer. Greater than 0.");
                        }
                        break;
                    case "update":
                        try {
                            int id = Integer.parseInt(input[1]);
                            if (id < 0) {
                                throw new NumberFormatException();
                            }
                            SpaceMarine sm = elementReader.readElement(scanner);
                            Command update = new Update(authenticator.readNewbie(scanner),
                                    authenticator.readLogin(scanner), authenticator.readPassword(scanner), id, sm);
                            commandSender.send(serializer.serialize(update));
                            System.out.println(answerReceiver.receive());
                        } catch (ArrayIndexOutOfBoundsException e) {
                            System.out.println("To execute this command, you must enter the required argument.");
                        } catch (NumberFormatException e) {
                            System.out.println("ID value must be integer. Greater than 0.");
                        }
                        break;
                    case "remove_key":
                        try {
                            Integer key = Integer.parseInt(input[1]);
                            Command removeKey = new RemoveKey(authenticator.readNewbie(scanner),
                                    authenticator.readLogin(scanner), authenticator.readPassword(scanner), key);
                            commandSender.send(serializer.serialize(removeKey));
                            System.out.println(answerReceiver.receive());
                        } catch (ArrayIndexOutOfBoundsException e) {
                            System.out.println("To execute this command, you must enter the required argument.");
                        } catch (NumberFormatException e) {
                            System.out.println("The input argument is not an integer.");
                        }
                        break;
                    case "clear":
                        Command clear = new Clear(authenticator.readNewbie(scanner),
                                authenticator.readLogin(scanner), authenticator.readPassword(scanner));
                        commandSender.send(serializer.serialize(clear));
                        System.out.println(answerReceiver.receive());
                        break;
                    case "execute_script":
                        try {
                            File file = new File(input[1]);
                            scriptReader.addScript(file.getAbsolutePath());
                            System.out.println("Trying to start execution of the script.\n");
                            scriptReader.readScript(input[1], commandSender, answerReceiver, authenticator.readNewbie(scanner),
                                    authenticator.readLogin(scanner), authenticator.readPassword(scanner));
                            scriptReader.clearScripts();
                        } catch (ArrayIndexOutOfBoundsException e) {
                            System.out.println("To execute this command, you must enter the required argument.");
                        }
                        break;
                    case "exit":
                        scanner.close();
                        System.out.println("The program is finished.\n");
                        System.exit(0);
                        break;
                    case "remove_greater": {
                        SpaceMarine sm = elementReader.readElement(scanner);
                        Command removeGreater = new RemoveGreater(authenticator.readNewbie(scanner),
                                authenticator.readLogin(scanner), authenticator.readPassword(scanner), sm);
                        commandSender.send(serializer.serialize(removeGreater));
                        System.out.println(answerReceiver.receive());
                    }
                    break;
                    case "replace_if_greater":
                        try {
                            Integer key = Integer.parseInt(input[1]);
                            SpaceMarine sm = elementReader.readElement(scanner);
                            Command replaceIfGreater = new ReplaceIfGreater(authenticator.readNewbie(scanner),
                                    authenticator.readLogin(scanner), authenticator.readPassword(scanner), key, sm);
                            commandSender.send(serializer.serialize(replaceIfGreater));
                            System.out.println(answerReceiver.receive());
                        } catch (ArrayIndexOutOfBoundsException e) {
                            System.out.println("To execute this command, you must enter the required argument.");
                        } catch (NumberFormatException e) {
                            System.out.println("The input argument is not an integer.");
                        }
                        break;
                    case "remove_greater_key":
                        try {
                            Integer key = Integer.parseInt(input[1]);
                            Command removeGreaterKey = new RemoveGreaterKey(authenticator.readNewbie(scanner),
                                    authenticator.readLogin(scanner), authenticator.readPassword(scanner), key);
                            commandSender.send(serializer.serialize(removeGreaterKey));
                            System.out.println(answerReceiver.receive());
                        } catch (ArrayIndexOutOfBoundsException e) {
                            System.out.println("To execute this command, you must enter the required argument.");
                        } catch (NumberFormatException e) {
                            System.out.println("The input argument is not an integer.");
                        }
                        break;
                    case "group_counting_by_coordinates":
                        Command groupCountingByCoordinates = new GroupCountingByCoordinates(authenticator.readNewbie(scanner),
                                authenticator.readLogin(scanner), authenticator.readPassword(scanner));
                        commandSender.send(serializer.serialize(groupCountingByCoordinates));
                        System.out.println(answerReceiver.receive());
                        break;
                    case "filter_by_chapter":
                        Chapter chapter = chapterReader.readChapter(scanner);
                        Command filterByChapter = new FilterByChapter(authenticator.readNewbie(scanner),
                                authenticator.readLogin(scanner), authenticator.readPassword(scanner), chapter);
                        commandSender.send(serializer.serialize(filterByChapter));
                        System.out.println(answerReceiver.receive());
                        break;
                    case "filter_starts_with_name":
                        try {
                            Command filterStartsWithName = new FilterStartsWithName(authenticator.readNewbie(scanner),
                                    authenticator.readLogin(scanner), authenticator.readPassword(scanner), input[1]);
                            commandSender.send(serializer.serialize(filterStartsWithName));
                            System.out.println(answerReceiver.receive());
                        } catch (ArrayIndexOutOfBoundsException e) {
                            System.out.println("To execute this command, you must enter the required argument.");
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("Incorrect command input. Enter \"help\" to get information about available commands.");
                }

            } catch (SocketTimeoutException e) {
                throw new SocketTimeoutException();
            } catch (IOException | ClassNotFoundException | IllegalArgumentException | NoSuchAlgorithmException e) {
                System.out.println(e.getMessage());
            }
        }
        scanner.close();
        System.out.println("The program is finished.");
        System.exit(0);
    }
}
