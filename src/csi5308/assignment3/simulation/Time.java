package csi5308.assignment3.simulation;

public class Time {

    private final long time;

    Time(long t) {
        this.time = t;
    }

    public long getTime() {
        return this.time;
    }

    public Time increment(long val) {
        return new Time(time + val);
    }

    @Override
    public int hashCode() {
        return Long.hashCode(this.time);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Time && ((Time) other).getTime() == this.time;
    }

}
