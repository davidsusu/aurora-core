package hu.webarticum.aurora.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import hu.webarticum.aurora.core.model.Block.ActivityManager.ActivityEntry;
import hu.webarticum.aurora.core.model.time.AlwaysTimeLimit;
import hu.webarticum.aurora.core.model.time.CustomTimeLimit;
import hu.webarticum.aurora.core.model.time.Time;
import hu.webarticum.aurora.core.model.time.TimeLimit;

public class Block implements Labeled {

    private static final long serialVersionUID = 1L;

    public static final long DEFAULT_LENGTH = 45 * Time.MINUTE;
    
    private String label;
    
    private long length;

    private final ActivityManager activityManager = new ActivityManager();
    
    public Block(Block other) {
        this.label = other.label;
        this.length = other.length;
        for (ActivityManager.ActivityEntry activityEntry : other.getActivityManager()) {
            this.activityManager.add(new Activity(activityEntry.getActivity()), activityEntry.getPeriods());
        }
    }

    public Block() {
        this("", DEFAULT_LENGTH);
    }
    
    public Block(long length) {
        this("", length);
    }

    public Block(String label) {
        this(label, DEFAULT_LENGTH);
    }
    
    public Block(String label, long length) {
        if (length < 0) {
            throw new IllegalArgumentException("");
        }
        this.label = label;
        this.length = length;
    }
    
    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return label;
    }

    public void setLength(long length) {
        this.length = length;
    }
    
    public long getLength() {
        return length;
    }

    public TimeLimit getCalculatedTimeLimit() {
        CustomTimeLimit timeLimit = new CustomTimeLimit(AlwaysTimeLimit.INSTANCE);
        for (ActivityEntry activityEntry: getActivityManager()) {
            Activity activity = activityEntry.getActivity();
            for (Period period: activityEntry.getPeriods()) {
                List<Aspect> aspects = new ArrayList<Aspect>();
                aspects.addAll(activity.getResourceManager().getResources());
                aspects.addAll(activity.getTagManager().getTags());
                for (Aspect aspect: aspects) {
                    if (aspect.isTimeLimitEnabled()) {
                        TimeLimit tagPeriodTimeLimit =
                            aspect.getTimeLimitManager().getCalculatedPeriodTimeLimit(period)
                        ;
                        timeLimit = timeLimit.intersectionWith(tagPeriodTimeLimit);
                    }
                }
            }
        }
        return timeLimit;
    }

    public TimingSet getCalculatedTimingSet() {
        return getCalculatedTimingSet(true);
    }
    
    public TimingSet getCalculatedTimingSet(boolean limited) {
        TimeLimit timeLimit = getCalculatedTimeLimit();
        
        TimingSet result = null;
        
        for (ActivityEntry activityEntry: getActivityManager()) {
            List<TimingSet> activityTimingSets = getActivityEntryTimingSets(activityEntry);
            TimingSet activityTimingSet = getTimingSetIntersection(activityTimingSets);
            if (activityTimingSet.isEmpty()) {
                return new TimingSet();
            } else if (result == null) {
                result = activityTimingSet;
            } else {
                result = getTimingSetIntersection(Arrays.asList(result, activityTimingSet));
            }
        }
        
        if (result == null) {
            result = new TimingSet();
        } else if (limited) {
            result = result.getLimited(timeLimit, length);
        }
        
        return result;
    }
    
    private List<TimingSet> getActivityEntryTimingSets(ActivityEntry activityEntry) {
        Activity activity = activityEntry.getActivity();
        List<TimingSet> activityTimingSets = new ArrayList<TimingSet>();
        for (Period period: activityEntry.getPeriods()) {
            List<Aspect> aspects = new ArrayList<Aspect>();
            aspects.addAll(activity.getResourceManager().getResources());
            aspects.addAll(activity.getTagManager().getTags());
            for (Aspect aspect: aspects) {
                if (aspect.isTimingSetEnabled()) {
                    TimingSet resourcePeriodTimingSet =
                        aspect.getTimingSetManager().getCalculatedPeriodTimingSet(period)
                    ;
                    if (!resourcePeriodTimingSet.isEmpty()) {
                        activityTimingSets.add(resourcePeriodTimingSet);
                    }
                }
            }
        }
        return activityTimingSets;
    }
    
    private TimingSet getTimingSetIntersection(List<TimingSet> timingSets) {
        TimingSet result = new TimingSet();
        Iterator<TimingSet> iterator = timingSets.iterator();
        if (iterator.hasNext()) {
            result.addAll(iterator.next());
        } else {
            return result;
        }
        while (iterator.hasNext()) {
            result.retainAll(iterator.next());
        }
        return result;
    }
    
    public TimingSet getCalculatedTimingSet(Board board) {
        TimingSet base = getCalculatedTimingSet();
        TimingSet result = new TimingSet();
        for (TimingSet.TimeEntry entry: base) {
            if (!board.conflictsWithBlockAt(this, entry.getTime())) {
                result.add(entry);
            }
        }
        return result;
    }
    
    public ActivityManager getActivityManager() {
        return activityManager;
    }
    
    @Override
    public String toString() {
        return String.format("Block: '%s'", getLabel());
    }

    public boolean hasConflicts() {
        for (Period period: activityManager.getPeriods()) {
            if (hasConflicts(period)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasConflicts(Period period) {
        return this.getActivityManager().getActivities(period).hasConflicts();
    }

    public boolean conflictsWith(Block other) {
        ActivityManager otherActivityManager = other.getActivityManager();
        PeriodSet periods = activityManager.getPeriods();
        Set<List<ActivityList>> activityPairSet = new HashSet<List<ActivityList>>(1, periods.size() + 1);
        for (Period period: periods) {
            ActivityList activities1 = activityManager.getActivities(period);
            ActivityList activities2 = otherActivityManager.getActivities(period);
            activityPairSet.add(new ArrayList<ActivityList>(Arrays.asList(activities1, activities2)));
        }
        for (List<ActivityList> activitiesPair: activityPairSet) {
            if (activitiesPair.get(0).conflictsWith(activitiesPair.get(1))) {
                return true;
            }
        }
        return false;
    }
    
    
    public class ActivityManager implements Iterable<ActivityManager.ActivityEntry>, Serializable {

        private static final long serialVersionUID = 1L;

        private List<ActivityEntry> entries = new LinkedList<ActivityEntry>();
        
        public Block getBlock() {
            return Block.this;
        }
        
        public void add(Activity activity, Period period) {
            entries.add(new ActivityEntry(activity, period));
        }

        public void add(Activity activity, Collection<Period> periods) {
            entries.add(new ActivityEntry(activity, periods));
        }
        
        public void add(Activity activity, Period... periods) {
            entries.add(new ActivityEntry(activity, periods));
        }
        
        public void remove(Activity activity) {
            Iterator<ActivityEntry> iterator = entries.iterator();
            while (iterator.hasNext()) {
                ActivityEntry entry = iterator.next();
                if (entry.getActivity() == activity) {
                    iterator.remove();
                    break;
                }
            }
        }
        
        public void removePeriod(Period period) {
            Iterator<ActivityEntry> iterator = entries.iterator();
            while (iterator.hasNext()) {
                ActivityEntry entry = iterator.next();
                if (entry.hasPeriod(period)) {
                    entry.removePeriod(period);
                    if (!entry.hasPeriods()) {
                        iterator.remove();
                    }
                }
            }
        }
        
        public boolean hasPeriod(Period period) {
            for (ActivityEntry entry: this) {
                if (entry.getPeriods().contains(period)) {
                    return true;
                }
            }
            return false;
        }
        
        public void clear() {
            entries.clear();
        }
        
        public ActivityList getActivities() {
            ActivityList result = new ActivityList();
            for (ActivityEntry entry: entries) {
                result.add(entry.getActivity());
            }
            return result;
        }
        
        public ActivityList getActivities(Period period) {
            ActivityList activities = new ActivityList();
            for (ActivityEntry entry: this) {
                if (entry.getPeriods().contains(period)) {
                    activities.add(entry.getActivity());
                }
            }
            return activities;
        }

        public ActivityList getActivities(Collection<Period> periods) {
            return getActivities(periods, false);
        }
        
        public ActivityList getActivities(Collection<Period> periods, boolean requireAll) {
            ActivityList activities = new ActivityList();
            for (ActivityEntry entry: this) {
                if (checkPeriods(periods, entry, requireAll)) {
                    activities.add(entry.getActivity());
                }
            }
            return activities;
        }
        
        private boolean checkPeriods(
            Collection<Period> filterPeriods, ActivityEntry entry, boolean requireAll
        ) {
            if (requireAll) {
                return entry.getPeriods().containsAll(filterPeriods);
            } else {
                PeriodSet commonPeriods = entry.getPeriods();
                commonPeriods.retainAll(filterPeriods);
                return !commonPeriods.isEmpty();
            }
        }

        public PeriodSet getPeriods() {
            PeriodSet result = new PeriodSet();
            for (ActivityEntry entry: this) {
                result.addAll(entry.getPeriods());
            }
            return result;
        }

        public PeriodSet getActivityPeriods(Activity activity) {
            for (ActivityEntry entry: this) {
                if (entry.getActivity().equals(activity)) {
                    return entry.getPeriods();
                }
            }
            return new PeriodSet();
        }
        
        public PeriodSet getResourcePeriods(Resource resource) {
            PeriodSet periods = new PeriodSet();
            for (ActivityEntry activityEntry: entries) {
                if (activityEntry.getActivity().getResourceManager().hasResource(resource)) {
                    periods.addAll(activityEntry.getPeriods());
                }
            }
            return periods;
        }
        
        public PeriodSet getResourceSubsetPeriods(ResourceSubset resourceSubset) {
            PeriodSet periods = new PeriodSet();
            Resource resource = resourceSubset.getResource();
            for (ActivityEntry activityEntry: entries) {
                ResourceSubsetList activityResourceSubsets =
                    activityEntry.getActivity().getResourceManager().getResourceSubsets(resource)
                ;
                for (ResourceSubset _resourceSubset: activityResourceSubsets) {
                    if (_resourceSubset.intersects(resourceSubset)) {
                        periods.addAll(activityEntry.getPeriods());
                        break;
                    }
                }
            }
            return periods;
        }
        
        public ActivityEntry getActivityEntry(Activity activity) {
            for (ActivityEntry activityEntry: entries) {
                if (activityEntry.getActivity().equals(activity)) {
                    return activityEntry;
                }
            }
            return null;
        }
        
        public Map<Activity, PeriodSet> toActivityToPeriodsMap() {
            Map<Activity, PeriodSet> result = new HashMap<Activity, PeriodSet>();
            for (ActivityEntry activityEntry: entries) {
                Activity activity = activityEntry.getActivity();
                PeriodSet periods = activityEntry.getPeriods();
                result.put(activity, new PeriodSet(periods));
            }
            return result;
        }

        @Override
        public Iterator<ActivityEntry> iterator() {
            return entries.iterator();
        }
        
        public class ActivityEntry implements Serializable {

            private static final long serialVersionUID = 1L;

            private Activity activity;
            
            private PeriodSet periods;
            
            private ActivityEntry(Activity activity, Collection<Period> periods) {
                this.activity = activity;
                this.periods = new PeriodSet(periods);
            }

            private ActivityEntry(Activity activity, Period... periods) {
                this.activity = activity;
                this.periods = new PeriodSet(Arrays.asList(periods));
            }
            
            public Activity getActivity() {
                return activity;
            }
            
            public boolean hasPeriod(Period period) {
                return periods.contains(period);
            }
            
            public boolean hasPeriods(Collection<Period> periods) {
                return this.periods.containsAll(periods);
            }

            public boolean hasPeriods() {
                return !this.periods.isEmpty();
            }
            
            public void addPeriod(Period period) {
                periods.add(period);
            }
            
            public void removePeriod(Period period) {
                periods.remove(period);
            }
            
            public PeriodSet getPeriods() {
                return new PeriodSet(periods);
            }
            
        }
        
    }
    
}
