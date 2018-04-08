package server.system.monitor.model;

public class SwapMemory {
    private long total;
    private long used;
    private long free;

    public SwapMemory(long total, long used, long free) {
        this.total = total;
        this.used = used;
        this.free = free;
    }

    public long getTotal() {
        return total;
    }

    public long getUsed() {
        return used;
    }

    public long getFree() {
        return free;
    }

    @Override
    public String toString() {
        return "SwapMemory{" +
                "total=" + total +
                ", used=" + used +
                ", free=" + free +
                '}';
    }
}