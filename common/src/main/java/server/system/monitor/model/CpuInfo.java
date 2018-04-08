package server.system.monitor.model;

public class CpuInfo {
    private double user;
    private double sys;
    private double idle;

    public CpuInfo(double user, double sys, double idle) {
        this.user = user;
        this.sys = sys;
        this.idle = idle;
    }

    public double getUser() {
        return user;
    }

    public double getSys() {
        return sys;
    }

    public double getIdle() {
        return idle;
    }

    @Override
    public String toString() {
        return "CpuInfo{" +
                "user=" + user +
                ", sys=" + sys +
                ", idle=" + idle +
                '}';
    }
}
