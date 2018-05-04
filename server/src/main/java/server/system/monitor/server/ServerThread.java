package server.system.monitor.server;

import server.system.monitor.listener.OnSocketEventListener;
import server.system.monitor.listener.OnUserEventListener;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread extends Thread {
    public static final int DEFAULT_PORT = 4040;
    public static final int DEFAULT_INTERVAL = 5000;

    private static final int SERVER_ID = 0;
    private static final int UNALLOCATED_ID = -1;
    private static final int MAX_USER = 100;

    private final OnSocketEventListener socketEventListener;
    private final OnUserEventListener userEventListener;

    private int port;
    private long interval;
    private boolean[] userIds;

    public ServerThread(OnSocketEventListener socketEventListener, int port, long interval) {
        this.userIds = new boolean[MAX_USER];
        this.socketEventListener = socketEventListener;
        this.port = port;
        this.interval = interval;
        this.userEventListener = new OnUserEventListener() {
            @Override
            public void onAccepted(int id) {
                socketEventListener.onAccepted(id);
            }

            @Override
            public void onDisconnected(int id) {
                userIds[id] = false;
                socketEventListener.onDisconnected(id);
            }
        };
    }



    @Override
    public void run() {
        try {
            ServerSocket socket = initSocket();
            socketEventListener.onStarted(port, SERVER_ID);
            while (!Thread.currentThread().isInterrupted()) {
                Socket userSocket = socket.accept();
                int userId = allocateId();
                if (userId != UNALLOCATED_ID) {
                    UserThread userThread = new UserThread(userSocket, userId, socketEventListener, userEventListener, interval);
                    userThread.start();
                } else {
                    userSocket.close();
                }
            }
            socket.close();
            socketEventListener.onClosed(SERVER_ID);
        } catch (IOException e) {
            socketEventListener.onError(SERVER_ID, e);
            socketEventListener.onClosed(SERVER_ID);
        }
    }

    private ServerSocket initSocket() throws IOException {
        ServerSocket socket = new ServerSocket();
        socket.bind(new InetSocketAddress(InetAddress.getLocalHost(), port));
        return socket;
    }

    private int allocateId() {
        int allocatedId = UNALLOCATED_ID;
        for (int id = 1; id < MAX_USER; id++) {
            if (!userIds[id]) {
                userIds[id] = true;
                allocatedId = id;
                break;
            }
        }
        return allocatedId;
    }
}
