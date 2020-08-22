package hu.webarticum.aurora.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import hu.webarticum.aurora.core.model.Labeled.LabeledComparator;
import hu.webarticum.aurora.core.model.time.Interval;
import hu.webarticum.aurora.core.model.time.Intervalable;
import hu.webarticum.aurora.core.model.time.Time;
import hu.webarticum.aurora.core.model.time.TimeLimit;

public class ActivityFlow implements Iterable<ActivityFlow.Entry>, Serializable {

    private static final long serialVersionUID = 1L;

    
    private ArrayList<Entry> entries;

    private boolean sorted = true;
    
    
    public ActivityFlow() {
        this.entries = new ArrayList<Entry>();
    }

    private ActivityFlow(Collection<Entry> entries) {
        this.entries = new ArrayList<Entry>(entries);
    }

    private ActivityFlow(ArrayList<Entry> entries) {
        this.entries = entries;
    }

    
    public List<Entry> getEntries() {
        return new ArrayList<Entry>(entries);
    }
    
    public ActivityList getActivities() {
        sort();
        ActivityList activities = new ActivityList();
        for (Entry entry : this) {
            activities.add(entry.getActivity());
        }
        return activities;
    }
    
    public PeriodSet getPeriods() {
        PeriodSet periods = new PeriodSet();
        for (Entry entry : this) {
            periods.addAll(entry.periods);
        }
        return periods;
    }
    
    public void add(Activity activity, Interval interval, Collection<Period> periods) {
        entries.add(new Entry(activity, interval, periods));
        sorted = false;
    }
    
    private void sort() {
        if (!sorted) {
            Collections.sort(entries, new SortComparator());
            sorted = true;
        }
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }
    
    public int size() {
        return entries.size();
    }
    
    @Override
    public Iterator<Entry> iterator() {
        sort();
        return entries.iterator();
    }

    public ListIterator<Entry> listIterator() {
        sort();
        return entries.listIterator();
    }

    public ActivityFlow filter(EntryFilter entryFilter) {
        ArrayList<Entry> newEntries = new ArrayList<Entry>();
        for (Entry entry: entries) {
            if (entryFilter.validate(entry)) {
                newEntries.add(entry);
            }
        }
        return new ActivityFlow(newEntries);
    }

    public Time getStartTime() {
        if (entries.isEmpty()) {
            return new Time(0);
        } else {
            sort();
            return entries.get(0).interval.getStart();
        }
    }
    
    public Time getEndTime() {
        if (entries.isEmpty()) {
            return new Time(0);
        } else {
            Time result = null;
            for (Entry entry: entries) {
                Time time = entry.interval.getEnd();
                if (result == null || time.compareTo(result) > 0) {
                    result = time;
                }
            }
            return result;
        }
    }
    
    public Time getLastTime() {
        if (entries.isEmpty()) {
            return new Time(0);
        } else {
            sort();
            return entries.get(entries.size() - 1).interval.getStart();
        }
    }

    public ActivityFlow getLimited(TimeLimit limit) {
        sort();
        return new ActivityFlow(limit.limitSortedIntervals(entries));
    }

    public ActivityFlow getIntersecting(TimeLimit limit) {
        sort();
        return new ActivityFlow(limit.limitIntersectingSortedIntervals(entries));
    }
    
    public ActivityFlow getThinned(Period period) {
        return getThinned(Arrays.asList(period));
    }

    public ActivityFlow getThinned(Collection<Period> periods) {
        ArrayList<Entry> newEntries = new ArrayList<Entry>();
        for (Entry entry: entries) {
            PeriodSet commonPeriods = entry.getPeriods();
            commonPeriods.retainAll(periods);
            if (!commonPeriods.isEmpty()) {
                newEntries.add(new Entry(entry.getActivity(), entry.getInterval(), commonPeriods));
            }
        }
        return new ActivityFlow(newEntries);
    }

    @Override
    public String toString() {
        sort();
        return entries.toString();
    }
    
    
    public class Entry implements Intervalable, Serializable {
        
        private static final long serialVersionUID = 1L;

        
        private final Activity activity;

        private final Interval interval;

        private final PeriodSet periods;
        
        
        public Entry(Activity activity, Interval interval, Collection<Period> periods) {
            this.activity = activity;
            this.interval = interval;
            this.periods = new PeriodSet(periods);
        }
        
        
        public Interval getInterval() {
            return interval;
        }

        public Activity getActivity() {
            return activity;
        }

        public PeriodSet getPeriods() {
            return new PeriodSet(periods);
        }
        
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Entry)) {
                return false;
            }
            
            Entry otherEntry = (Entry)obj;
            return (
                activity.equals(otherEntry.activity) &&
                interval.equals(otherEntry.interval) &&
                periods.equals(otherEntry.periods)
            );
        }
        
        @Override
        public int hashCode() {
            return (
                ((
                    (activity.hashCode() * 37) +
                    interval.hashCode()
                ) * 31) +
                periods.hashCode()
            );
        }
        
        @Override
        public String toString() {
            return "{" + interval + ": '" + activity + "'}";
        }
        
    }
    
    
    public interface EntryFilter {
        
        public boolean validate(Entry entry);
        
    }

    
    public static class ActivityEntryFilter implements EntryFilter {
        
        private final ActivityFilter activityFilter;
        
        
        public ActivityEntryFilter(ActivityFilter activityFilter) {
            this.activityFilter = activityFilter;
        }
        
        
        @Override
        public boolean validate(Entry entry) {
            return activityFilter.validate(entry.getActivity());
        }
        
    }
    
    
    public static class SortComparator implements Comparator<Entry> {
        
        @Override
        public int compare(Entry entry1, Entry entry2) {
            int cmp = entry1.interval.getStart().compareTo(entry2.interval.getStart());
            if (cmp != 0) {
                return cmp;
            }
            
            cmp = entry1.interval.getEnd().compareTo(entry2.interval.getEnd());
            if (cmp != 0) {
                return cmp;
            }

            cmp = entry1.periods.compareTo(entry2.periods);
            if (cmp != 0) {
                return cmp;
            }
            
            return LabeledComparator.compareStatic(entry1.activity, entry2.activity);
        }
        
    }
    
}
