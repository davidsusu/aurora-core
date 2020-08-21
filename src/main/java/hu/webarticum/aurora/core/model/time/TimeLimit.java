package hu.webarticum.aurora.core.model.time;

import java.io.Serializable;
import java.util.List;

public interface TimeLimit extends Serializable {

    public boolean contains(Time time);
    
    public boolean contains(Time time, boolean includeStart, boolean includeEnd);
    
    public boolean contains(Interval interval);
    
    public boolean intersects(Interval interval);

    public boolean isAlways();

    public boolean isNever();
    
    public TimeLimit getSimplified();

    public List<Time> getTimes();

    public List<Time> getTimes(boolean endTimes);
    
    public <T extends Intervalable> List<T> limitSortedIntervals(Iterable<T> intervalables);

    public <T extends Intervalable> List<T> limitIntersectingSortedIntervals(Iterable<T> intervalables);
    
}
