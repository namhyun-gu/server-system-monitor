package server.system.monitor.model;

import java.util.List;

public class CpuStatus {
    private CpuInfo totalCpuInfo;
    private List<CpuInfo> cpuInfoList;

    public CpuStatus(CpuInfo totalCpuInfo, List<CpuInfo> cpuInfoList) {
        this.totalCpuInfo = totalCpuInfo;
        this.cpuInfoList = cpuInfoList;
    }

    public CpuInfo getTotalCpuInfo() {
        return totalCpuInfo;
    }

    public List<CpuInfo> getCpuInfoList() {
        return cpuInfoList;
    }

    @Override
    public String toString() {
        return "CpuStatus{" +
                "totalCpuInfo=" + totalCpuInfo +
                ", cpuInfoList=" + cpuInfoList +
                '}';
    }
}
