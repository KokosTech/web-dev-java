import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class Client {
    private final String host;
    private final int port;
    private volatile boolean running = true;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Scanner scanner;
    private Thread receiveThread;

    public Client(String host, int port, Scanner scanner) {
        this.host = host;
        this.port = port;
        this.scanner = scanner;
    }

    public void start() throws IOException {
        connect();

        this.receiveThread = new Thread(this::receive);
        this.receiveThread.start();

        run();
    }

    public void run() {
        String input;

        while (running) {
            System.out.print("> ");
            input = scanner.nextLine();

            if (input.startsWith("/quit")) {
                running = false;
                out.println("/quit");
                this.stop();
            } else if (input.startsWith("/nick")) {
                String[] tokens = input.split(" ");
                if (tokens.length != 2) {
                    System.out.println("Invalid command. Please try again.");
                    continue;
                }

                String newNickname = tokens[1];
                out.println("/nick " + newNickname);
            } else if (input.startsWith("/msg")) {
                String[] tokens = input.split(" ", 2);
                if (tokens.length != 2) {
                    System.out.println("Invalid command. Please try again.");
                    continue;
                }

                String message = tokens[1];
                out.println("/msg " + message);
            }
        }
    }

    public void connect() throws IOException {
        this.socket = new Socket(this.host, this.port);
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        System.out.println("Connected to the chat server.");
    }

    public void receive() {
        try {
            String serverResponse;
            while (running && (serverResponse = in.readLine()) != null) {
                System.out.println(serverResponse);
                System.out.print("> ");
            }
            this.stop();
        } catch (IOException e) {
            if (e instanceof SocketException) {
                System.out.println("You have been disconnected from the server.");
            } else {
                System.out.println("Something went wrong. Please try again later.");
                System.out.println(e.getMessage());
            }
        }
    }

    public void stop() {
        try {
            running = false;
            this.receiveThread.interrupt();
            this.out.close();
            this.in.close();
            this.socket.close();
        } catch (IOException e) {
            System.out.println("Something went wrong. Please try again later.");
            System.out.println(e.getMessage());
        }
    }
}
