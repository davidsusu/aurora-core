package hu.webarticum.aurora.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import hu.webarticum.aurora.core.model.time.Interval;
import hu.webarticum.aurora.core.model.time.Intervalable;
import hu.webarticum.aurora.core.model.time.Time;
import hu.webarticum.aurora.core.model.time.TimeLimit;

public class Board implements Iterable<Board.Entry>, Labeled {

    private static final long serialVersionUID = 1L;

    
    private String label = "";
    
    private boolean sorted = false;
    
    private EntryList entries = new EntryList();

    
    public Board() {
        this.sorted = true;
    }

    public Board(Board other) {
        this.label = other.label;
        this.sorted = other.sorted;
        this.entries = new EntryList(other.entries);
    }

    private Board(EntryList entries) {
        this.entries = entries;
    }

    public Board(String label) {
        this.label = label;
        this.sorted = true;
    }

    public Board(String label, Board other) {
        this.label = label;
        applyFrom(other);
    }

    
    public Board copy() {
        return new Board(this);
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return label;
    }
    
    public void add(Block block, Time time) {
        addInternal(new Entry(block, time));
    }

    public void add(Entry entry) {
        addInternal(new Entry(entry.block, entry.time));
    }
    
    private void addInternal(Entry entry) {
        entries.add(entry);
        sorted = false;
    }
    
    public void remove(Block block) {
        Iterator<Entry> iterator = this.iterator();
        while(iterator.hasNext()) {
            Entry entry = iterator.next();
            if (entry.block == block) {
                iterator.remove();
                return;
            }
        }
    }

    public List<Entry> getEntries() {
        sort();
        return new ArrayList<Entry>(entries);
    }
    
    public PeriodSet getPeriods() {
        PeriodSet result = new PeriodSet();
        for (Entry entry: this) {
            result.addAll(entry.getBlock().getActivityManager().getPeriods());
        }
        return result;
    }
    
    public BlockList getBlocks() {
        sort();
        BlockList result = new BlockList();
        for (Entry entry: this) {
            result.add(entry.getBlock());
        }
        return result;
    }
    
    public Entry search(Block block) {
        for (Entry entry: entries) {
            if (entry.block == block) {
                return entry;
            }
        }
        return null;
    }

    public Entry search(Activity activity) {
        for (Entry entry: entries) {
            if (entry.block.getActivityManager().getActivities().contains(activity)) {
                return entry;
            }
        }
        return null;
    }

    public boolean contains(Block block) {
        return (search(block) != null);
    }

    public void clear() {
        entries.clear();
        sorted = true;
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }
    
    public int size() {
        return entries.size();
    }

    public Time getStartTime() {
        if (entries.isEmpty()) {
            return new Time(0);
        } else {
            sort();
            return entries.getFirst().getTime();
        }
    }
    
    public Time getEndTime() {
        if (entries.isEmpty()) {
            return new Time(0);
        } else {
            Time result = null;
            for (Entry entry: entries) {
                Time time = entry.getInterval().getEnd();
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
            return entries.getLast().getTime();
        }
    }

    public Board limit(TimeLimit limit) {
        sort();
        return new Board(new EntryList(limit.limitSortedIntervals(entries)));
    }

    public Board limitIntersection(TimeLimit limit) {
        sort();
        return new Board(new EntryList(limit.limitIntersectingSortedIntervals(entries)));
    }

    public Board filter(BlockFilter filter) {
        EntryList newentries = new EntryList();
        for (Entry entry : entries) {
            if (filter.validate(entry.getBlock())) {
                newentries.add(entry);
            }
        }
        return new Board(newentries);
    }

    public void applyFrom(Board other) {
        this.label = other.label;
        this.sorted = other.sorted;
        this.entries = new EntryList(other.entries);
    }
    
    public boolean hasConflicts() {
        for (BlockIntersectionTable.Entry entry: this.toBlockIntersectionTable()) {
            if (entry.getBlocks().hasConflicts()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasConflicts(Period period) {
        for (BlockIntersectionTable.Entry entry: this.toBlockIntersectionTable()) {
            if (entry.getBlocks().hasConflicts(period)) {
                return true;
            }
        }
        return false;
    }

    public BlockIntersectionTable getConflictTable() {
        List<BlockIntersectionTable.Entry> conflictEntries = new ArrayList<BlockIntersectionTable.Entry>();
        for (BlockIntersectionTable.Entry entry: this.toBlockIntersectionTable()) {
            if (entry.getBlocks().hasConflicts()) {
                conflictEntries.add(entry);
            }
        }
        return BlockIntersectionTable.fromEntries(conflictEntries);
    }

    public BlockIntersectionTable getConflictTable(Period period) {
        List<BlockIntersectionTable.Entry> conflictEntries = new ArrayList<BlockIntersectionTable.Entry>();
        for (BlockIntersectionTable.Entry entry: this.toBlockIntersectionTable()) {
            if (entry.getBlocks().hasConflicts(period)) {
                conflictEntries.add(entry);
            }
        }
        return BlockIntersectionTable.fromEntries(conflictEntries);
    }

    public boolean conflictsWithBlockAt(Block block, Time time) {
        return conflictsWithBlockAt(block, time, true);
    }
    
    public boolean conflictsWithBlockAt(Block block, Time time, boolean conflictSameBlock) {
        BlockList blockBlockList = new BlockList(block);
        for (BlockIntersectionTable.Entry entry: limitIntersection(new Interval(time, block.getLength())).toBlockIntersectionTable()) {
            BlockList blocks = entry.getBlocks();
            if (!conflictSameBlock) {
                blocks.remove(block);
            }
            if (blockBlockList.conflictsWith(blocks)) {
                return true;
            }
        }
        return false;
    }
    
    public Map<Block, Time> toBlockTimeMap() {
        Map<Block, Time> blockTimeMap = new HashMap<Block, Time>();
        for (Entry entry: this) {
            blockTimeMap.put(entry.getBlock(), entry.getTime());
        }
        return blockTimeMap;
    }
    
    public ActivityFlow toActivityFlow() {
        ActivityFlow activityFlow = new ActivityFlow();
        for (Entry entry: this) {
            Interval interval = entry.getInterval();
            Block block = entry.getBlock();
            for (Block.ActivityManager.ActivityEntry activityEntry : block.getActivityManager()) {
                activityFlow.add(activityEntry.getActivity(), interval, activityEntry.getPeriods());
            }
        }
        return activityFlow;
    }

    public ActivityFlow toActivityFlow(Period period) {
        if (period == null) {
            return toActivityFlow();
        }
        ActivityFlow activityFlow = new ActivityFlow();
        for (Entry entry: this) {
            Interval interval = entry.getInterval();
            Block block = entry.getBlock();
            for (Activity activity: block.getActivityManager().getActivities(period)) {
                activityFlow.add(activity, interval, Arrays.asList(period));
            }
        }
        return activityFlow;
    }

    public ActivityFlow toActivityFlow(Collection<Period> periods) {
        return toActivityFlow(periods, false);
    }
    
    public ActivityFlow toActivityFlow(Collection<Period> periods, boolean requireAll) {
        ActivityFlow activityFlow = new ActivityFlow();
        for (Entry entry: this) {
            Interval interval = entry.getInterval();
            Block block = entry.getBlock();
            for (Activity activity: block.getActivityManager().getActivities(periods, requireAll)) {
                Collection<Period> entryPeriods = periods;
                if (!requireAll) {
                    entryPeriods = new PeriodSet(entryPeriods);
                    entryPeriods.retainAll(periods);
                }
                activityFlow.add(activity, interval, entryPeriods);
            }
        }
        return activityFlow;
    }
    
    public BlockIntersectionTable toBlockIntersectionTable() {
        List<BlockIntersectionTable> baseItemsList = new ArrayList<BlockIntersectionTable>(size());
        for (Board.Entry entry: this) {
            BlockIntersectionTable baseItem = new BlockIntersectionTable();
            baseItem.merge(entry.getBlock(), entry.getTime());
            baseItemsList.add(baseItem);
        }
        return BlockIntersectionTable.mergeAll(baseItemsList);
    }
    
    @Override
    public Iterator<Entry> iterator() {
        sort();
        return entries.listIterator();
    }
    
    @Override
    public String toString() {
        return "Board: '" + label + "' { with " + entries.size() + " item(s) }";
    }

    private void sort() {
        if (!sorted) {
            entries.sort();
        }
        sorted = true;
    }
    
    
    public static class Entry implements Intervalable, Serializable {

        private static final long serialVersionUID = 1L;

        
        private final Block block;
        
        private final Time time;

        
        public Entry(Entry other) {
            this.block = other.block;
            this.time = other.time;
        }

        public Entry(Block block, Time time) {
            this.block = block;
            this.time = time;
        }
        
        
        public Block getBlock() {
            return block;
        }

        public Time getTime() {
            return time;
        }
        
        public Interval getInterval() {
            return new Interval(time, block.getLength());
        }
        
        @Override
        public int hashCode() {
            return block.hashCode() + (37 * time.hashCode());
        }
        
        @Override
        public boolean equals(Object other) {
            if (!(other instanceof Entry)) {
                return false;
            }
            Entry otherEntry = (Entry) other;
            return (otherEntry.block == block && otherEntry.time.equals(time));
        }
        
        @Override
        public String toString() {
            return time.toString() + " :: " + block.toString();
        }
        
    }
    
    
    public static class EntryList extends LinkedList<Entry> implements List<Entry>, Serializable {

        private static final long serialVersionUID = 1L;

        
        public EntryList() {
            super();
        }

        public EntryList(List<Entry> entries) {
            super(entries);
        }
        
        
        public void sort() {
            Collections.sort(this, new SortComparator());
        }
        
        public BlockList getBlocks() {
            BlockList result = new BlockList(this.size());
            for (Entry entry : this) {
                result.add(entry.block);
            }
            return result;
        }
        
        
        private static class SortComparator implements Comparator<Entry> {

            @Override
            public int compare(Entry entry1, Entry entry2) {
                int result = entry1.time.compareTo(entry2.time);
                if (result == 0) {
                    result = Long.compare(entry1.block.getLength(), entry2.block.getLength());
                    if (result == 0) {
                        result = Integer.compare(
                            System.identityHashCode(entry1.block), System.identityHashCode(entry2.block)
                        );
                    }
                }
                return result;
            }
            
        }
    }
    
}
