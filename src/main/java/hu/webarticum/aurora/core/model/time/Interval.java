package hu.webarticum.aurora.core.model.time;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Interval implements Intervalable, TimeLimit, Comparable<Interval> {

    private static final long serialVersionUID = 1L;

    private final Time start;
    private final Time end;
    
    public Interval(Time time1, Time time2) {
        if (time1.compareTo(time2) < 0) {
            this.start = time1;
            this.end = time2;
        } else {
            this.start = time2;
            this.end = time1;
        }
    }
    
    public Interval(long seconds1, long seconds2) {
        this(new Time(seconds1), new Time(seconds2));
    }

    public Interval(Time start, long length) {
        this(start, new Time(start.getSeconds() + length));
    }
    
    public Interval getInterval() {
        return this;
    }
    
    public Time getStart() {
        return start;
    }

    public Time getEnd() {
        return end;
    }

    @Override
    public boolean contains(Time time) {
        return (start.compareTo(time) <= 0 && end.compareTo(time) >= 0);
    }

    @Override
    public boolean contains(Time time, boolean includeStart, boolean includeEnd) {
        int startComp = start.compareTo(time);
        int endComp = end.compareTo(time);
        boolean out = (
            startComp > 0 || (!includeStart && startComp == 0) ||
            endComp < 0 || (!includeEnd && endComp == 0)
        );
        return !out;
    }
    
    @Override
    public boolean contains(Interval interval) {
        return (this.start.compareTo(interval.start) <= 0 && this.end.compareTo(interval.end) >= 0);
    }

    @Override
    public boolean intersects(Interval interval) {
        return (this.start.compareTo(interval.end) < 0 && this.end.compareTo(interval.start) > 0);
    }

    @Override
    public boolean isAlways() {
        return false;
    }

    @Override
    public TimeLimit getSimplified() {
        if (start.equals(end)) {
            return new PointTimeLimit(start);
        }
        return this;
    }
    
    @Override
    public boolean isNever() {
        return false;
    }

    @Override
    public List<Time> getTimes() {
        return new ArrayList<Time>(Arrays.<Time>asList(start, end));
    }

    @Override
    public List<Time> getTimes(boolean endTimes) {
        return new ArrayList<Time>(Arrays.<Time>asList(endTimes?end:start));
    }
    
    public long getLength() {
        return end.getSeconds() - start.getSeconds();
    }
    
    @Override
    public <T extends Intervalable> List<T> limitSortedIntervals(
        Iterable<T> intervalables
    ) {
        List<T> result = new ArrayList<T>();
        for (T item: intervalables) {
            Interval interval = item.getInterval();
            if (contains(interval)) {
                result.add(item);
            } else if (interval.getStart().compareTo(end) > 0) {
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
        for (T item: intervalables) {
            Interval interval = item.getInterval();
            if (intersects(interval)) {
                result.add(item);
            } else if (interval.getStart().compareTo(end) > 0) {
                break;
            }
        }
        return result;
    }
    
    @Override
    public int compareTo(Interval other) {
        int comp = this.start.compareTo(other.start);
        if (comp == 0) {
            return this.end.compareTo(other.end);
        } else {
            return comp;
        }
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Interval)) {
            return false;
        }
        
        Interval otherInterval = (Interval)other;
        return (this.start.equals(otherInterval.start) && this.end.equals(otherInterval.end));
    }
    
    @Override
    public int hashCode() {
        int result = 97;
        result = (31 * result) + start.hashCode();
        result = (37 * result) + end.hashCode();
        return result;
    }
    
    @Override
    public String toString() {
        return "TimeLimit.Interval { " + start.toString() + "  --->  " + end.toString() + " }";
    }
    
}