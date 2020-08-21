package hu.webarticum.aurora.core.model.time;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PointTimeLimit implements TimeLimit, Comparable<PointTimeLimit> {

    private static final long serialVersionUID = 1L;

    private final Time time;
    
    public PointTimeLimit(Time time) {
        this.time = time;
    }

    public PointTimeLimit(long seconds) {
        this(new Time(seconds));
    }

    public Time getTime() {
        return time;
    }

    @Override
    public boolean contains(Time time) {
        return this.time.equals(time);
    }

    @Override
    public boolean contains(Time time, boolean includeStart, boolean includeEnd) {
        return ((includeStart || includeEnd) && this.time.equals(time));
    }

    @Override
    public boolean contains(Interval interval) {
        return (
            this.time.equals(interval.getStart()) &&
            this.time.equals(interval.getEnd())
        );
    }

    @Override
    public boolean intersects(Interval interval) {
        return (
            this.time.compareTo(interval.getStart()) > 0 &&
            this.time.compareTo(interval.getEnd()) < 0
        );
    }

    @Override
    public boolean isAlways() {
        return false;
    }

    @Override
    public boolean isNever() {
        return false;
    }

    @Override
    public TimeLimit getSimplified() {
        return this;
    }

    @Override
    public List<Time> getTimes() {
        return new ArrayList<Time>(Arrays.asList(time));
    }

    @Override
    public List<Time> getTimes(boolean endTimes) {
        return new ArrayList<Time>(Arrays.asList(time));
    }
    
    @Override
    public <T extends Intervalable> List<T> limitSortedIntervals(
        Iterable<T> intervalables
    ) {
        List<T> result = new ArrayList<T>();
        for (T intervalable: intervalables) {
            Interval interval = intervalable.getInterval();
            if (contains(interval)) {
                result.add(intervalable);
            } else if (interval.getStart().compareTo(time) > 0) {
                break;
            }
        }
        return result;
    }

    @Override
    public <T extends Intervalable> List<T> limitIntersectingSortedIntervals(
        Iterable<T> intervalables
    ) {
        List<T> result = new ArrayList<T>();
        for (T intervalable: intervalables) {
            Interval interval = intervalable.getInterval();
            if (intersects(interval)) {
                result.add(intervalable);
            } else if (interval.getStart().compareTo(time) > 0) {
                break;
            }
        }
        return result;
    }
    
    @Override
    public int compareTo(PointTimeLimit other) {
        return this.time.compareTo(other.time);
    }
    
    @Override
    public boolean equals(Object other) {
        if (other instanceof PointTimeLimit) {
            return this.time.equals(((PointTimeLimit) other).time);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return time.hashCode();
    }
    
    @Override
    public String toString() {
        return "TimeLimit.TimePoint { " + time + " }";
    }
    
}