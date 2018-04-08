package server.system.monitor.server;

import org.hyperic.sigar.*;
import server.system.monitor.model.CpuInfo;
import server.system.monitor.model.CpuStatus;
import server.system.monitor.model.SwapMemory;
import server.system.monitor.model.SystemMemory;

import java.util.ArrayList;
import java.util.List;

public class SigarWrapper {
    private Sigar sigar;

    public SigarWrapper(Sigar sigar) {
        this.sigar = sigar;
    }

    public CpuStatus getCpuStatus() throws SigarException {
        CpuPerc totalCpuPerc = sigar.getCpuPerc();
        CpuInfo totalCpuInfo = new CpuInfo(totalCpuPerc.getUser(), totalCpuPerc.getSys(), totalCpuPerc.getIdle());

        CpuPerc[] cpuPercs = sigar.getCpuPercList();
        List<CpuInfo> cpuInfoList = new ArrayList<>();
        for (CpuPerc cpuPerc : cpuPercs) {
            CpuInfo cpuInfo = new CpuInfo(cpuPerc.getUser(), cpuPerc.getSys(), cpuPerc.getIdle());
            cpuInfoList.add(cpuInfo);
        }
        return new CpuStatus(totalCpuInfo, cpuInfoList);
    }

    public SystemMemory getSystemMemory() throws SigarException {
        Mem mem = sigar.getMem();
        return new SystemMemory.Builder()
                .total(mem.getTotal())
                .used(mem.getUsed())
                .free(mem.getFree())
                .actualUsed(mem.getActualUsed())
                .actualFree(mem.getActualFree())
                .build();
    }

    public SwapMemory getSwapMemory() throws SigarException {
        Swap swap = sigar.getSwap();
        return new SwapMemory(swap.getTotal(), swap.getUsed(), swap.getFree());
    }
}
