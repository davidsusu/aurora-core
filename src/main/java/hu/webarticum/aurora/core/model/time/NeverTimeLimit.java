package hu.webarticum.aurora.core.model.time;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NeverTimeLimit implements TimeLimit {

    private static final long serialVersionUID = 1L;

    
    public static final TimeLimit INSTANCE = new NeverTimeLimit();
    
    
    @Override
    public boolean contains(Time time) {
        return false;
    }

    @Override
    public boolean contains(Time time, boolean includeStart, boolean includeEnd) {
        return false;
    }

    @Override
    public boolean contains(Interval interval) {
        return false;
    }

    @Override
    public boolean intersects(Interval interval) {
        return false;
    }

    @Override
    public boolean isAlways() {
        return false;
    }

    @Override
    public boolean isNever() {
        return true;
    }

    @Override
    public TimeLimit getSimplified() {
        return this;
    }

    @Override
    public List<Time> getTimes() {
        return Collections.<Time>emptyList();
    }

    @Override
    public List<Time> getTimes(boolean endTimes) {
        return Collections.<Time>emptyList();
    }
    
    @Override
    public <T extends Intervalable> List<T> limitSortedIntervals(
        Iterable<T> intervalables
    ) {
        return new ArrayList<T>(0);
    }

    @Override
    public <T extends Intervalable> List<T> limitIntersectingSortedIntervals(
        Iterable<T> intervalables
    ) {
        return new ArrayList<T>(0);
    }
    
    @Override
    public String toString() {
        return "TimeLimit.Never";
    }

}