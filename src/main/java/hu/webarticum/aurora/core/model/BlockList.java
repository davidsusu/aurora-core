package hu.webarticum.aurora.core.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import hu.webarticum.aurora.core.model.Block.ActivityManager;

public class BlockList extends ArrayList<Block> {

    private static final long serialVersionUID = 1L;

    public BlockList() {
        super();
    }

    public BlockList(Block... blocks) {
        super(Arrays.<Block>asList(blocks));
    }
    
    public BlockList(Collection<Block> blocks) {
        super(blocks);
    }
    
    public BlockList copy() {
        return new BlockList(this);
    }

    public Block getShortest() {
        Block shortestBlock = null;
        for (Block block: this) {
            if (shortestBlock == null || block.getLength() < shortestBlock.getLength()) {
                shortestBlock = block;
            }
        }
        return shortestBlock;
    }
    
    public Block getLongest() {
        Block longestBlock = null;
        for (Block block: this) {
            if (longestBlock == null || block.getLength() > longestBlock.getLength()) {
                longestBlock = block;
            }
        }
        return longestBlock;
    }
    
    public PeriodSet getPeriods() {
        PeriodSet periods = new PeriodSet();
        for (Block block: this) {
            periods.addAll(block.getActivityManager().getPeriods());
        }
        return periods;
    }

    public ActivityList getActivities() {
        ActivityList activities = new ActivityList();
        for (Block block: this) {
            activities.addAll(block.getActivityManager().getActivities());
        }
        return activities;
    }

    public ActivityList getActivities(Period period) {
        ActivityList activities = new ActivityList();
        for (Block block: this) {
            activities.addAll(block.getActivityManager().getActivities(period));
        }
        return activities;
    }

    public ActivityList getActivities(Collection<Period> periods) {
        return getActivities(periods, false);
    }
    
    public ActivityList getActivities(Collection<Period> periods, boolean requireAll) {
        ActivityList activities = new ActivityList();
        for (Block block: this) {
            activities.addAll(block.getActivityManager().getActivities(periods, requireAll));
        }
        return activities;
    }
    
    public boolean hasConflicts() {
        Map<Period, ActivityList> periodActivitiesMap = new HashMap<Period, ActivityList>();
        for (Block block: this) {
            ActivityManager activityManager = block.getActivityManager();
            for (Period period: activityManager.getPeriods()) {
                ActivityList activities;
                if (periodActivitiesMap.containsKey(period)) {
                    activities = periodActivitiesMap.get(period);
                } else {
                    activities = new ActivityList();
                    periodActivitiesMap.put(period, activities);
                }
                activities.addAll(activityManager.getActivities(period));
            }
        }
        for (Map.Entry<Period, ActivityList> entry: periodActivitiesMap.entrySet()) {
            ActivityList activities = entry.getValue();
            if (activities.hasConflicts()) {
                return true;
            }
        }
        return false;
    }
    
    public boolean hasConflicts(Period period) {
        ActivityList activities = new ActivityList();
        for (Block block: this) {
            activities.addAll(block.getActivityManager().getActivities(period));
        }
        return activities.hasConflicts();
    }

    public boolean conflictsWith(BlockList other) {
        Map<Period, ActivityList> periodActivitiesMap1 = createPeriodActivitiesMap(this, null);
        Map<Period, ActivityList> periodActivitiesMap2 =
            createPeriodActivitiesMap(other, periodActivitiesMap1.keySet())
        ;
        
        for (Map.Entry<Period, ActivityList> entry1: periodActivitiesMap1.entrySet()) {
            Period period = entry1.getKey();
            if (periodActivitiesMap2.containsKey(period)) {
                ActivityList activities1 = entry1.getValue();
                ActivityList activities2 = periodActivitiesMap2.get(period);

                if (activities1.conflictsWith(activities2)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private Map<Period, ActivityList> createPeriodActivitiesMap(BlockList blocks, Collection<Period> periods) {
        boolean checkPeriods = (periods != null);
        Map<Period, ActivityList> periodActivitiesMap = new HashMap<Period, ActivityList>();
        for (Block block: blocks) {
            ActivityManager activityManager = block.getActivityManager();
            for (Period period: activityManager.getPeriods()) {
                if (checkPeriods && !periods.contains(period)) {
                    continue;
                }
                
                ActivityList activities;
                if (periodActivitiesMap.containsKey(period)) {
                    activities = periodActivitiesMap.get(period);
                } else {
                    activities = new ActivityList();
                    periodActivitiesMap.put(period, activities);
                }
                activities.addAll(activityManager.getActivities(period));
            }
        }
        return periodActivitiesMap;
    }

    public boolean conflictsWith(BlockList other, Period period) {
        ActivityList activities = new ActivityList();
        for (Block block: this) {
            activities.addAll(block.getActivityManager().getActivities(period));
        }
        for (Block block: other) {
            activities.addAll(block.getActivityManager().getActivities(period));
        }
        return activities.hasConflicts();
    }
    
    public BlockList filter(BlockFilter filter) {
        BlockList result = new BlockList();
        for (Block block: this) {
            if (filter.validate(block)) {
                result.add(block);
            }
        }
        return result;
    }
    
}