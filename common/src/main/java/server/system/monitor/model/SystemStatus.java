package server.system.monitor.model;

import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Swap;

public class SystemStatus {

  private CpuPerc cpuPerc;
  private Mem mem;
  private Swap swap;

  public SystemStatus(CpuPerc cpu, Mem mem, Swap swap) {
    this.cpuPerc = cpu;
    this.mem = mem;
    this.swap = swap;
  }

  public CpuPerc getCpu() {
    return cpuPerc;
  }

  public Mem getMem() {
    return mem;
  }

  public Swap getSwap() {
    return swap;
  }
}
