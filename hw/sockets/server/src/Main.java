import server.Server;

import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) {
        Logger logger = Logger.getLogger(Main.class.getName());
        logger.info("Starting server...");

        Server server = new Server();

        try {
            server.start();
        } catch (Exception e) {
            logger.warning(e.getMessage());
        }
    }
}