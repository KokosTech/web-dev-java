import java.io.IOException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class DialUpMenu {
    public static void printMenu() {
        System.out.println("Welcome to the chatroom!");
        System.out.println("To connect to the chatroom, type: /connect <ip of chat server>:<port>");
        System.out.println("To disconnect from the chatroom, type: /quit");
        System.out.println("Upon joining, type: /nick <nickname>");
        System.out.println("To send a message, type: /msg <message>");
        System.out.println("To see a list of commands, type: /help");
    }

    public static void start() {
        Scanner scanner = new Scanner(System.in);
        printMenu();

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine();

            if (input.equals("/quit")) {
                System.out.println("Goodbye!");
                break;
            } else if (input.equals("/help")) {
                printMenu();
            } else if (input.startsWith("/connect")) {
                String[] tokens = input.split(" ");
                if (tokens.length != 2) {
                    System.out.println("Invalid command. Please try again.");
                    continue;
                }

                String[] address = tokens[1].split(":");
                if (address.length != 2) {
                    System.out.println("Invalid command. Please try again.");
                    continue;
                }

                String host = address[0];
                int port = Integer.parseInt(address[1]);


                Client client = new Client(host, port, scanner);
                try {
                    client.start();
                } catch (IOException e) {
                    if (e instanceof ConnectException) {
                        System.out.println("Unable to connect to server. Please try again later.");
                    } else if (e instanceof UnknownHostException) {
                        System.out.println("Unable to connect to server. Unknown host.");
                    } else if (e instanceof NoRouteToHostException) {
                        System.out.println("Unable to connect to server. No route to host.");
                    } else if (e instanceof SocketException) {
                        System.out.println("Unable to connect to server. Socket exception.");
                    } else {
                        System.out.println("Something went wrong. Please try again later.");
                        System.out.println(e.getMessage());
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid port number or host address. Please try again.");
                }
            } else {
                System.out.println("Invalid command. Please try again.");
            }
        }
    }
}
