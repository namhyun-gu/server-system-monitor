package server.system.monitor.client;

import server.system.monitor.listener.OnSocketEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;


public class ClientThread extends Thread {
    private final OnSocketEventListener socketEventListener;
    private int serverId;
    private String endpointString;
    private InetAddress endpoint;
    private int port;

    public ClientThread(OnSocketEventListener socketEventListener, int serverId, String endpoint, int port) {
        this.socketEventListener = socketEventListener;
        this.serverId = serverId;
        this.endpointString = endpoint;
        this.port = port;
    }

    public ClientThread(OnSocketEventListener socketEventListener, int serverId, InetAddress endpoint, int port) {
        this.socketEventListener = socketEventListener;
        this.serverId = serverId;
        this.endpoint = endpoint;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            Socket socket = initSocket();
            if (socket.isConnected()) {
                socketEventListener.onStarted(port, serverId);
                while (!Thread.currentThread().isInterrupted()) {
                    InputStream inputStream = socket.getInputStream();
                    byte[] bytes = new byte[1024];
                    int size = inputStream.read(bytes);
                    if (size > 0) {
                        String response = new String(bytes, StandardCharsets.UTF_8);
                        socketEventListener.onMessageReceived(serverId, response);
                    }
                }
                socket.close();
                socketEventListener.onClosed(serverId);
            }
        } catch (IOException e) {
            socketEventListener.onError(serverId, e);
        }
    }

    private Socket initSocket() {
        Socket socket = new Socket();
        try {
            SocketAddress socketAddress;
            InetAddress inetAddress;
            if (endpoint != null) {
                inetAddress = endpoint;
            } else {
                if (endpointString != null) {
                    inetAddress = InetAddress.getByName(endpointString);
                } else {
                    inetAddress = InetAddress.getLocalHost();
                }
            }
            socketAddress = new InetSocketAddress(inetAddress, port);
            socket.connect(socketAddress);
        } catch (IOException e) {
            socketEventListener.onError(serverId, e);
        }
        return socket;
    }
}
