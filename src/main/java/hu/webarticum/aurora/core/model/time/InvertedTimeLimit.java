package hu.webarticum.aurora.core.model.time;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;

public class InvertedTimeLimit implements TimeLimit {

    private static final long serialVersionUID = 1L;

    private final TimeLimit base;

    public InvertedTimeLimit(TimeLimit base) {
        this.base = base;
    }

    public TimeLimit getBase() {
        return base;
    }
    
    @Override
    public boolean contains(Time time) {
        return (base.getTimes().contains(time) || !base.contains(time));
    }

    @Override
    public boolean contains(Time time, boolean includeStart, boolean includeEnd) {
        if (!base.contains(time)) {
            return true;
        } else if (base.getTimes(true).contains(time)) {
            return includeStart;
        } else if (base.getTimes(false).contains(time)) {
            return includeEnd;
        } else {
            return false;
        }
    }
    
    @Override
    public boolean contains(Interval interval) {
        return !base.intersects(interval);
    }

    @Override
    public boolean intersects(Interval interval) {
        return !base.contains(interval);
    }

    @Override
    public boolean isAlways() {
        return base.isNever();
    }

    @Override
    public boolean isNever() {
        return base.isAlways();
    }

    @Override
    public TimeLimit getSimplified() {
        return new CustomTimeLimit(this).getSimplified();
    }

    @Override
    public List<Time> getTimes() {
        return base.getTimes();
    }

    @Override
    public List<Time> getTimes(boolean endTimes) {
        return base.getTimes(!endTimes);
    }

    @Override
    public <T extends Intervalable> List<T> limitSortedIntervals(
        Iterable<T> intervalables
    ) {
        IdentityHashMap<T, Void> referencesToExclude = new IdentityHashMap<T, Void>();
        for (T intervalable: base.limitIntersectingSortedIntervals(intervalables)) {
            referencesToExclude.put(intervalable, null);
        }
        List<T> result = new ArrayList<T>();
        for (T intervalable: intervalables) {
            if (!referencesToExclude.containsKey(intervalable)) {
                result.add(intervalable);
            }
        }
        return result;
    }

    @Override
    public <T extends Intervalable> List<T> limitIntersectingSortedIntervals(
        Iterable<T> intervalables
    ) {
        IdentityHashMap<T, Void> referencesToExclude = new IdentityHashMap<T, Void>();
        for (T intervalable: base.limitSortedIntervals(intervalables)) {
            referencesToExclude.put(intervalable, null);
        }
        List<T> result = new ArrayList<T>();
        for (T intervalable: intervalables) {
            if (!referencesToExclude.containsKey(intervalable)) {
                result.add(intervalable);
            }
        }
        return result;
    }
    
    @Override
    public String toString() {
        return "Inverted: " + base;
    }

}