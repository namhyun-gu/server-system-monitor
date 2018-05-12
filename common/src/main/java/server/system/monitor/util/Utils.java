package server.system.monitor.util;

public class Utils {

  private static final String MEM_FORMAT = "%.1f %s";
  private static final String UNIT_BYTES = "B";
  private static final String UNIT_KB = "KB";
  private static final String UNIT_MB = "MB";
  private static final String UNIT_GB = "GB";

  public static String toMemoryFormat(long bytes) {
    if (bytes < 1024L) {
      return bytes + " " + UNIT_BYTES;
    }
    double kilobytes = (double) bytes / 1024;
    if (kilobytes < 1024) {
      return String.format(MEM_FORMAT, kilobytes, UNIT_KB);
    }
    double megabytes = kilobytes / 1024;
    if (megabytes < 1024) {
      return String.format(MEM_FORMAT, megabytes, UNIT_MB);
    }
    double gigabytes = megabytes / 1024;
    return String.format(MEM_FORMAT, gigabytes, UNIT_GB);
  }

}
