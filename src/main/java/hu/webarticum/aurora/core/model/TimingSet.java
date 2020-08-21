package hu.webarticum.aurora.core.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

import hu.webarticum.aurora.core.model.time.Interval;
import hu.webarticum.aurora.core.model.time.Time;
import hu.webarticum.aurora.core.model.time.TimeLimit;


// TODO: do not be a TreeSet

public class TimingSet extends TreeSet<TimingSet.TimeEntry> implements Labeled {

    private static final long serialVersionUID = 1L;

    private String label = "";

    public TimingSet() {
        super();
    }

    public TimingSet(TimingSet other) {
        super();
        this.label = other.label;
        for (TimeEntry timeEntry: other) {
            add(new TimeEntry(timeEntry));
        }
    }

    public TimingSet(Time... times) {
        super();
        for (Time time: times) {
            add(new TimeEntry(time, ""));
        }
    }
    
    public TimingSet(String label) {
        super();
        this.label = label;
    }

    public TimingSet(String label, Time... times) {
        super();
        this.label = label;
        for (Time time: times) {
            add(new TimeEntry(time, ""));
        }
    }
    
    public TimingSet(String label, Collection<TimeEntry> timeEntries) {
        super();
        this.label = label;
        for (TimeEntry timeEntry: timeEntries) {
            add(new TimeEntry(timeEntry));
        }
    }
    
    public TimingSet(String label, TimeEntry... timeEntries) {
        super();
        this.label = label;
        for (TimeEntry timeEntry: timeEntries) {
            add(new TimeEntry(timeEntry));
        }
    }

    public void setLabel(String label) {
        this.label = label;
    }
    
    @Override
    public String getLabel() {
        return label;
    }

    // TODO: use timeLimit.limitSortedIntervals (?)
    public TimingSet getLimited(TimeLimit timeLimit) {
        TimingSet result = new TimingSet();
        for (TimeEntry timeEntry: this) {
            if (timeLimit.contains(timeEntry.getTime())) {
                result.add(timeEntry);
            }
        }
        return result;
    }

    // TODO: this.toIntervals(long length)
    // TODO: use timeLimit.timeLimitlimitSortedIntervals
    public TimingSet getLimited(TimeLimit timeLimit, long length) {
        TimingSet result = new TimingSet();
        for (TimeEntry timeEntry: this) {
            if (timeLimit.contains(new Interval(timeEntry.getTime(), length))) {
                result.add(timeEntry);
            }
        }
        return result;
    }

    public TreeMap<Time, TimingSet> split(Time splitTime) {
        return split(splitTime, new Time(0));
    }

    public TreeMap<Time, TimingSet> split(long splitTimeSeconds) {
        return split(new Time(splitTimeSeconds), new Time(0));
    }

    public TreeMap<Time, TimingSet> split(Time splitTime, Time offsetTime) {
        return split(splitTime.getSeconds(), offsetTime.getSeconds());
    }

    public TreeMap<Time, TimingSet> split(long splitSeconds, long offsetSeconds) {
        TreeMap<Time, TimingSet> result = new TreeMap<Time, TimingSet>();
        Time previousFloorTime = null;
        TimingSet currentTimingSet = null;
        for (TimeEntry entry: this) {
            long entrySeconds = entry.getTime().getSeconds();
            long movedSeconds = entrySeconds - offsetSeconds;
            long floorSeconds = entrySeconds - mod(movedSeconds, splitSeconds);
            Time floorTime = new Time(floorSeconds);
            if (previousFloorTime == null || !floorTime.equals(previousFloorTime)) {
                currentTimingSet = new TimingSet();
                result.put(floorTime, currentTimingSet);
            }
            currentTimingSet.add(entry);
            previousFloorTime = floorTime;
        }
        return result;
    }
    
    private long mod(long number, long base) {
        long mod = number % base;
        return (mod < 0 ? mod + base : mod);
    }
    
    public boolean add(Time time) {
        return add(new TimeEntry(time));
    }
    
    public boolean add(Time time, String label) {
        return add(new TimeEntry(time, label));
    }
    
    public boolean remove(Time time) {
        Iterator<TimeEntry> iterator = this.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getTime().equals(time)) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    public boolean removeAll(TimingSet other) {
        boolean result = false;
        Iterator<TimeEntry> iterator = this.iterator();
        while (iterator.hasNext()) {
            if (other.containsTime(iterator.next().getTime())) {
                iterator.remove();
                result = true;
            }
        }
        return result;
    }

    public boolean containsTime(Time time) {
        for (TimeEntry entry: this) {
            if (entry.getTime().equals(time)) {
                return true;
            }
        }
        return false;
    }
    
    public TreeSet<Time> getTimes() {
        TreeSet<Time> times = new TreeSet<Time>();
        for (TimeEntry entry: this) {
            times.add(entry.getTime());
        }
        return times;
    }
    
    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }
    
    @Override
    public boolean equals(Object object) {
        return (this == object);
    }
    
    public static class TimeEntry implements Labeled, Comparable<TimeEntry> {
        
        private static final long serialVersionUID = 1L;

        private Time time;

        private String label;

        public TimeEntry(Time time) {
            this.time = time;
            this.label = "";
        }

        public TimeEntry(Time time, String label) {
            this.time = time;
            this.label = label;
        }

        public TimeEntry(TimeEntry other) {
            this.time = other.time;
            this.label = other.label;
        }
        
        public void setTime(Time time) {
            this.time = time;
        }
        
        public Time getTime() {
            return time;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        @Override
        public String getLabel() {
            return label;
        }

        @Override
        public int compareTo(TimeEntry other) {
            return this.time.compareTo(other.time);
        }

        @Override
        public boolean equals(Object other) {
            if (other instanceof TimeEntry) {
                return this.time.equals(((TimeEntry) other).time);
            } else {
                return false;
            }
        }
        
        @Override
        public int hashCode() {
            return this.time.hashCode();
        }
        
        @Override
        public String toString() {
            if (label.isEmpty()) {
                return time.toString();
            } else {
                return time + " (" + label + ")";
            }
        }
        
        public String toReadableString() {
            if (label.isEmpty()) {
                return time.toReadableString();
            } else {
                return label + " :: " + time.toReadableString();
            }
        }
        
    }
    
}
