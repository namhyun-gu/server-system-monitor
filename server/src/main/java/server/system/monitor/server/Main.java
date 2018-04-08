package server.system.monitor.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.system.monitor.listener.OnSocketEventListener;

public class Main implements OnSocketEventListener {
    private static final int DEFAULT_PORT = 4040;
    private final Logger logger = LoggerFactory.getLogger(Main.class);
    private static int port = DEFAULT_PORT;

    public static void main(String[] args) {
        port = DEFAULT_PORT;
        if (args.length > 0) {
            port = Integer.parseInt(args[1]);
        }
        ServerThread serverThread = new ServerThread(new Main(), port);
        serverThread.start();
    }

    @Override
    public void onStarted(int id) {
        logger.info("Server started (port: " + port + ")");
    }

    @Override
    public void onMessageReceived(int id, String message) {
        // No-op
    }

    @Override
    public void onMessageSent(int id) {
        logger.info("Sent message to " + id);
    }

    @Override
    public void onError(int id, Exception e) {
        logger.error("Error occurred (" + id + ")");
        e.printStackTrace();
    }

    @Override
    public void onClosed(int id) {
        logger.info("Server closed");
    }

    @Override
    public void onAccepted(int id) {
        logger.info("Accepted client (userId: " + id + ")");
    }

    @Override
    public void onDisconnected(int id) {
        logger.info("Disconnected client (userId: " + id + ")");
    }
}
