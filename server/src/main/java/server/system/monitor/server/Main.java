package server.system.monitor.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.system.monitor.listener.OnSocketEventListener;

public class Main implements OnSocketEventListener {

  private static final int DEFAULT_PORT = 4040;
  private static final int DEFAULT_INTERVAL = 1000;
  private final Logger logger = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) {
    int port = DEFAULT_PORT;
    int interval = DEFAULT_INTERVAL;
    if (args.length > 0) {
      port = Integer.parseInt(args[0]);
      if (args.length > 1) {
        interval = Integer.parseInt(args[1]);
      }
    }
    ServerThread serverThread = new ServerThread(new Main(), port, interval);
    serverThread.start();
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
