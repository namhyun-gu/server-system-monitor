package server.system.monitor.server;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import org.apache.commons.lang3.SystemUtils;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.Swap;
import server.system.monitor.listener.OnSocketEventListener;
import server.system.monitor.listener.OnUserEventListener;
import server.system.monitor.model.Memory;
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
      Memory mem = getMemoryStatus(sigar);
      Swap swap = sigar.getSwap();

      systemStatus = new SystemStatus(cpuPerc, mem, swap);
    } catch (SigarException | IOException e) {
      socketEventListener.onError(userId, e);
    }
    return systemStatus;
  }

  private Memory getMemoryStatus(Sigar sigar) throws SigarException, IOException {
    Memory.Builder builder = new Memory.Builder();
    if (SystemUtils.IS_OS_LINUX) {
      Runtime runtime = Runtime.getRuntime();
      Process process = runtime.exec("free");

      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
      reader.readLine();
      String memInfoString = reader.readLine();
      String[] splitMemInfo = memInfoString.split("\\s+");

      final int INDEX_TOTAL = 1;
      final int INDEX_USED = 2;
      final int INDEX_FREE = 3;

      builder.total(Long.parseLong(splitMemInfo[INDEX_TOTAL]) * 1024L)
          .used(Long.parseLong(splitMemInfo[INDEX_USED]) * 1024L)
          .free(Long.parseLong(splitMemInfo[INDEX_FREE]) * 1024L);
    } else {
      Mem mem = sigar.getMem();
      builder.total(mem.getTotal()).used(mem.getUsed()).free(mem.getFree());
    }
    return builder.build();
  }
}
