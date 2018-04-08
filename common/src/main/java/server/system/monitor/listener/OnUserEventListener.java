package server.system.monitor.listener;

public interface OnUserEventListener {

    void onAccepted(int id);

    void onDisconnected(int id);

}
