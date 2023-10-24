package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.logging.Logger;

public class Server {

    private static final int PORT = System.getenv("PORT") != null ? Integer.parseInt(System.getenv("PORT")) : 12345;
    private final Logger logger = Logger.getLogger(Server.class.getName());
    private final HashSet<ClientHandler> clients = new HashSet<>();
    private Socket clientSocket;
    private ServerSocket serverSocket;

    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            logger.info("Server started on " + serverSocket.getInetAddress() + ":" + serverSocket.getLocalPort());

            while (true) {
                Thread stopThread = new Thread(this::stopServer);
                stopThread.start();

                if (clients.isEmpty()) {
                    logger.info("Waiting for clients...");
                }

                clientSocket = serverSocket.accept();
                logger.info("Client connected: " + clientSocket);

                ClientHandler client = new ClientHandler(clientSocket, this);

                synchronized (clients) {
                    clients.add(client);
                }

                client.start();
            }
        } catch (IOException e) {
            logger.warning(e.getMessage());
        } finally {
            logger.info("Server stopped.");
        }
    }

    public void broadcastMessage(String message, ClientHandler sender) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String formattedTime = sdf.format(new Date());
        String broadcastMessage = "[" + formattedTime + "] " + sender.getNickname() + ": " + message;

        System.out.println(broadcastMessage);

        synchronized(clients) {
            for (ClientHandler client : clients) {
                if (client != sender) {
                    client.sendMessage(broadcastMessage);
                }
            }
        }
    }

    public void serverMessage(String message) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String formattedTime = sdf.format(new Date());
        String serverMessage = "[" + formattedTime + "] " + message;

        System.out.println(serverMessage);

        synchronized(clients) {
            for (ClientHandler client : clients) {
                client.sendMessage(serverMessage);
            }
        }
    }

    public void removeClient(ClientHandler client) {
        synchronized (clients) {
            clients.remove(client);
        }
    }

    public Boolean checkNickname(String nickname) {
        Boolean returnValue;
        
        synchronized(clients) {
            returnValue = clients.stream().anyMatch(client -> client.getNickname() != null && client.getNickname().equals(nickname));
        }

        return returnValue;
    }

    private void stopServer() {
        try (BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {
            String input;
            while ((input = consoleReader.readLine()) != null) {
                if (input.equals("/stop")) {
                    logger.info("Stopping server...");
                    serverMessage("Server is shutting down.");

                    synchronized (clients) {
                        for (ClientHandler client : clients) {
                            client.close();
                            clients.remove(client);
                        }
                    }

                    clientSocket.close();
                    serverSocket.close();
                    break;
                }
            }
        } catch (IOException e) {
            logger.warning(e.getMessage());
        } finally {
            logger.info("Server stopped.");
            System.exit(0);
        }
    }
}

