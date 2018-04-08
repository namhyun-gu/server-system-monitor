package server.system.monitor.server;

import com.google.gson.Gson;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import server.system.monitor.listener.OnSocketEventListener;
import server.system.monitor.listener.OnUserEventListener;
import server.system.monitor.model.CpuStatus;
import server.system.monitor.model.SwapMemory;
import server.system.monitor.model.SystemMemory;
import server.system.monitor.model.SystemStatus;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

public class UserThread extends Thread {
    private final OnSocketEventListener socketEventListener;
    private final OnUserEventListener userEventListener;
    private Socket socket;
    private int userId;

    public UserThread(Socket socket, int userId,
                      OnSocketEventListener socketEventListener, OnUserEventListener userEventListener) {
        this.socketEventListener = socketEventListener;
        this.userEventListener = userEventListener;
        this.socket = socket;
        this.userId = userId;
    }

    @Override
    public void run() {
        try {
            userEventListener.onAccepted(userId);
            while (!Thread.currentThread().isInterrupted()) {
                String jsonSystemStatus = getSystemStatus();
                if (jsonSystemStatus != null) {
                    byte[] byteSystemStatus = jsonSystemStatus.getBytes();

                    OutputStream outputStream = socket.getOutputStream();
                    outputStream.write(byteSystemStatus);

                    socketEventListener.onMessageSent(userId);
                }
                Thread.sleep(5000);
            }
            socket.close();
            userEventListener.onDisconnected(userId);
        } catch (IOException e) {
            if (e instanceof SocketException) {
                userEventListener.onDisconnected(userId);
            } else {
                socketEventListener.onError(userId, e);
            }
        } catch (InterruptedException e) {
            socketEventListener.onError(userId, e);
            userEventListener.onDisconnected(userId);
        }
    }

    private String getSystemStatus() {
        Sigar sigar = new Sigar();
        SigarWrapper sigarWrapper = new SigarWrapper(sigar);
        String jsonStr = null;
        try {
            CpuStatus cpuStatus = sigarWrapper.getCpuStatus();
            SystemMemory systemMemory = sigarWrapper.getSystemMemory();
            SwapMemory swapMemory = sigarWrapper.getSwapMemory();

            SystemStatus systemStatus = new SystemStatus(cpuStatus, systemMemory, swapMemory);

            Gson gson = new Gson();
            jsonStr = gson.toJson(systemStatus);
        } catch (SigarException e) {
            socketEventListener.onError(userId, e);
        }
        return jsonStr;
    }
}
