package server.system.monitor.client;

public class Utils {

    public static double toGB(long bytes) {
        return (double) bytes / 1024 / 1024 / 1024;
    }

}
