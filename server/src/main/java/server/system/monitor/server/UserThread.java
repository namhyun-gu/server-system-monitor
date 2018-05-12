package server.system.monitor.server;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.Swap;
import server.system.monitor.listener.OnSocketEventListener;
import server.system.monitor.listener.OnUserEventListener;
import server.system.monitor.model.SystemStatus;

public class UserThread extends Thread {

  private final OnSocketEventListener socketEventListener;
  private final OnUserEventListener userEventListener;
  private Socket socket;
  private int userId;
  private long interval;

  public UserThread(Socket socket, int userId, OnSocketEventListener socketEventListener,
      OnUserEventListener userEventListener, long interval) {
    this.socketEventListener = socketEventListener;
    this.userEventListener = userEventListener;
    this.socket = socket;
    this.userId = userId;
    this.interval = interval;
  }

  @Override
  public void run() {
    try {
      userEventListener.onAccepted(userId);
      while (!Thread.currentThread().isInterrupted()) {
        SystemStatus systemStatus = getSystemStatus();
        Gson gson = new Gson();
        String jsonSystemStatus = gson.toJson(systemStatus);
        if (jsonSystemStatus != null) {
          byte[] byteSystemStatus = jsonSystemStatus.getBytes();

          OutputStream outputStream = socket.getOutputStream();
          outputStream.write(byteSystemStatus);

          socketEventListener.onMessageSent(userId);
          System.out.println(systemStatus.getCpu());
          System.out.println(systemStatus.getMem());
          System.out.println(systemStatus.getSwap());
        }
        Thread.sleep(interval);
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

  private SystemStatus getSystemStatus() {
    Sigar sigar = new Sigar();
    SystemStatus systemStatus = null;
    try {
      CpuPerc cpuPerc = sigar.getCpuPerc();
      Mem mem = sigar.getMem();
      Swap swap = sigar.getSwap();

      systemStatus = new SystemStatus(cpuPerc, mem, swap);
    } catch (SigarException e) {
      socketEventListener.onError(userId, e);
    }
    return systemStatus;
  }
}
