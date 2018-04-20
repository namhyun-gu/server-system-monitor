package server.system.monitor.listener;

public interface OnSocketEventListener extends OnUserEventListener {

    void onStarted(int port, int id);

    void onMessageReceived(int id, String message);

    void onMessageSent(int id);

    void onError(int id, Exception e);

    void onClosed(int id);

}
