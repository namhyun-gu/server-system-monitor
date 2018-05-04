package server.system.monitor.server;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.system.monitor.listener.OnSocketEventListener;

public class Main implements OnSocketEventListener {
    private final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        CommandLineParser parser = new GnuParser();
        Options options = new Options();
        options.addOption("p", "port", false, "port to listen");
        options.addOption("i", "interval", false, "send info interval");
        try {
            CommandLine line = parser.parse(options, args);
            int port = Integer.parseInt(line.getOptionValue("port", "4040"));
            long interval = Long.parseLong(line.getOptionValue("interval", "5000"));
            ServerThread serverThread = new ServerThread(new Main(), port, interval);
            serverThread.start();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStarted(int port, int id) {
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
