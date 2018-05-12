package server.system.monitor.model;

import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Swap;

public class SystemStatus {

  private CpuPerc cpuPerc;
  private Memory mem;
  private Swap swap;

  public SystemStatus(CpuPerc cpu, Memory mem, Swap swap) {
    this.cpuPerc = cpu;
    this.mem = mem;
    this.swap = swap;
  }

  public CpuPerc getCpu() {
    return cpuPerc;
  }

  public Memory getMem() {
    return mem;
  }

  public Swap getSwap() {
    return swap;
  }
}
