package client.utility;

import java.util.Scanner;

public class Authenticator {

    public boolean readNewbie(Scanner scanner) throws IllegalArgumentException {
        System.out.println("Want to register a new account? (\"yes\"/\"no\")");
        String answer;
        while (scanner.hasNextLine()) {
            answer = scanner.nextLine();
            if (answer.equals("yes")) {
                return true;
            } else if (answer.equals("no")) {
                return false;
            }
            System.out.println("Please enter \"yes\" or \"no\".");
        }
        throw new IllegalArgumentException("Further reading of the commands is impossible.");
    }

    public String readLogin(Scanner scanner) throws IllegalArgumentException {
        System.out.println("Please enter login:");
        String answer;
        while (scanner.hasNextLine()) {
            answer = scanner.nextLine().trim();
            if (!answer.equals("")) {
                return answer;
            }
            System.out.println("Login must not be empty word.");
        }
        throw new IllegalArgumentException("Further reading of the commands is impossible.");
    }

    public String readPassword(Scanner scanner) throws IllegalArgumentException {
        System.out.println("Please enter password:");
        String answer;
        while (scanner.hasNextLine()) {
            answer = scanner.nextLine();
            if (!answer.equals("")) {
                return answer;
            }
            System.out.println("Password must not be empty word.");
        }
        throw new IllegalArgumentException("Further reading of the commands is impossible.");
    }
}
