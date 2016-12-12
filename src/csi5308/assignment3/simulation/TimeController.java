package csi5308.assignment3.simulation;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class TimeController implements Iterable<Time> {

    private class TimeIterator implements Iterator<Time> {

        private final Long endTime;
        private long curTime;
        private long interval;

        public TimeIterator(long startTime, long interval, Long endTime) {
            this.endTime = endTime;
            this.curTime = startTime;
            this.interval = interval;
        }

        @Override
        public boolean hasNext() {
            return endTime == null || this.curTime <= endTime;
        }

        @Override
        public Time next() {
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                return null;
            }
            Time t = new Time(this.curTime);
            this.curTime += 1;
            return t;
        }
    }

    private final Long endTime;
    private final long startTime;
    private final long interval;

    public TimeController(long startTime) {
        this(startTime, TimeUnit.SECONDS.toMillis(1L), null);
    }

    public TimeController(long startTime, long interval) {
        this(startTime, interval, null);
    }

    public TimeController(long startTime, long interval, Long endTime) {
        this.endTime = endTime;
        this.startTime = startTime;
        this.interval = interval;
    }

    @Override
    public Iterator<Time> iterator() {
        return new TimeIterator(startTime, interval, endTime);
    }
}
