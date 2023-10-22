package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.UUID;
import java.util.logging.Logger;

public class ClientHandler extends Thread {
    private final Socket clientSocket;
    private final Server server;
    private final Logger logger = Logger.getLogger(ClientHandler.class.getName());
    private PrintWriter out;
    private BufferedReader in;
    private String nickname;


    public ClientHandler(Socket socket, Server server) {
        this.clientSocket = socket;
        this.server = server;
    }

    public String getNickname() {
        return nickname;
    }

    public void sendMessage(String message) {
        this.out.println(message);
    }

    @Override
    public void run() {
        try {
            this.out = new PrintWriter(clientSocket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String newNickname = UUID.randomUUID().toString();

            if (server.checkNickname(newNickname)) {
                logger.warning("Nickname collision detected, generating new nickname...");
                logger.info("Old Nickname: " + newNickname);

                while (server.checkNickname(newNickname)) {
                    newNickname = UUID.randomUUID().toString();
                }

                logger.info("New Nickname: " + newNickname);
            }

            this.nickname = newNickname;

            this.out.println("Your nickname is " + this.nickname + ". Type /nick <nickname> to change it.");
            this.server.serverMessage(nickname + " has joined the chat.");

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.startsWith("/nick ")) {
                    String[] tokens = inputLine.split(" ");
                    if (tokens.length != 2) {
                        this.out.println("[SERVER] Invalid command. Please try again.");
                        continue;
                    }

                    newNickname = tokens[1];

                    if (server.checkNickname(newNickname)) {
                        this.out.println("[SERVER] This nickname is already taken. Please try again.");
                        continue;
                    }

                    this.server.serverMessage(nickname + " changed their nickname to " + newNickname);
                    this.nickname = newNickname;

                } else if (inputLine.startsWith("/msg ")) {
                    String msg = inputLine.substring(5);
                    this.server.broadcastMessage(msg, this);
                } else if (inputLine.startsWith("/quit")) {
                    break;
                } else {
                    this.out.println("[SERVER] Invalid command. Please try again.");
                }
            }
        } catch (IOException e) {
            logger.warning(e.getMessage());
        } finally {
            this.out.println("You have left the chat.");
            this.server.removeClient(this);
            this.server.serverMessage(nickname + " has left the chat.");
            this.close();
        }
    }

    public void close() {
        try {
            this.out.close();
            this.in.close();
            this.clientSocket.close();

            logger.info("Closed socket " + this.clientSocket);
            this.interrupt();
        } catch (IOException e) {
            logger.warning(e.getMessage());
        }
    }
}