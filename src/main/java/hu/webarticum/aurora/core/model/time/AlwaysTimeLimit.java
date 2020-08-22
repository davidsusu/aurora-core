package hu.webarticum.aurora.core.model.time;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AlwaysTimeLimit implements TimeLimit {

    private static final long serialVersionUID = 1L;

    
    public static final TimeLimit INSTANCE = new AlwaysTimeLimit();
    
    
    @Override
    public boolean contains(Time time) {
        return true;
    }

    @Override
    public boolean contains(Time time, boolean includeStart, boolean includeEnd) {
        return true;
    }

    @Override
    public boolean contains(Interval interval) {
        return true;
    }

    @Override
    public boolean intersects(Interval interval) {
        return true;
    }

    @Override
    public boolean isAlways() {
        return true;
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
        List<T> result = new ArrayList<T>();
        for (T intervalable: intervalables) {
            result.add(intervalable);
        }
        return result;
    }

    @Override
    public <T extends Intervalable> List<T> limitIntersectingSortedIntervals(
        Iterable<T> intervalables
    ) {
        List<T> result = new ArrayList<T>();
        for (T intervalable: intervalables) {
            result.add(intervalable);
        }
        return result;
    }
    
    @Override
    public String toString() {
        return "TimeLimit.Always";
    }
    
}