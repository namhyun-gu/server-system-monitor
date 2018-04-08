package server.system.monitor.model;

public class SystemStatus {
    private CpuStatus cpu;
    private SystemMemory systemMemory;
    private SwapMemory swapMemory;

    public SystemStatus(CpuStatus cpu, SystemMemory systemMemory, SwapMemory swapMemory) {
        this.cpu = cpu;
        this.systemMemory = systemMemory;
        this.swapMemory = swapMemory;
    }

    public CpuStatus getCpu() {
        return cpu;
    }

    public SystemMemory getSystemMemory() {
        return systemMemory;
    }

    public SwapMemory getSwapMemory() {
        return swapMemory;
    }

    @Override
    public String toString() {
        return "SystemStatus{" +
                "cpu=" + cpu +
                ", systemMemory=" + systemMemory +
                ", swapMemory=" + swapMemory +
                '}';
    }
}
