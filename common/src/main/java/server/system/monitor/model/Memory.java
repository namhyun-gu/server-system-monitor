package server.system.monitor.model;

public class Memory {

  private long total;
  private long used;
  private long free;

  public Memory(Builder builder) {
    this.total = builder.total;
    this.used = builder.used;
    this.free = builder.free;
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

  public String toString() {
    return "Memory: " + this.total / 1024L + "K av, " + this.used / 1024L + "K used, "
        + this.free / 1024L + "K free";
  }

  public static class Builder {

    protected long total;
    protected long used;
    protected long free;

    public Builder() {
    }

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

    public Memory build() {
      return new Memory(this);
    }
  }
}
