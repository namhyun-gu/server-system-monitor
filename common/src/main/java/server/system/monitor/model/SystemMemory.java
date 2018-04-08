package server.system.monitor.model;

public class SystemMemory {
    private long total;
    private long used;
    private long free;
    private long actualUsed;
    private long actualFree;

    private SystemMemory(long total, long used, long free, long actualUsed, long actualFree) {
        this.total = total;
        this.used = used;
        this.free = free;
        this.actualUsed = actualUsed;
        this.actualFree = actualFree;
    }

    public static class Builder {
        private long total;
        private long used;
        private long free;
        private long actualUsed;
        private long actualFree;

        public Builder total(long total) {
            this.total = total;
            return this;
        }

        public Builder used(long used) {
            this.used = used;
            return this;
        }

        public Builder free(long free) {
            this.free = free;
            return this;
        }

        public Builder actualUsed(long actualUsed) {
            this.actualUsed = actualUsed;
            return this;
        }

        public Builder actualFree(long actualFree) {
            this.actualFree = actualFree;
            return this;
        }

        public SystemMemory build() {
            return new SystemMemory(total, used, free, actualUsed, actualFree);
        }
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

    public long getActualUsed() {
        return actualUsed;
    }

    public long getActualFree() {
        return actualFree;
    }

    @Override
    public String toString() {
        return "SystemMemory{" +
                "total=" + total +
                ", used=" + used +
                ", free=" + free +
                ", actualUsed=" + actualUsed +
                ", actualFree=" + actualFree +
                '}';
    }
}