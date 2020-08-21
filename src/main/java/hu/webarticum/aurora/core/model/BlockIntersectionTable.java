package hu.webarticum.aurora.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import hu.webarticum.aurora.core.model.time.Interval;
import hu.webarticum.aurora.core.model.time.Time;


public class BlockIntersectionTable implements Iterable<BlockIntersectionTable.Entry>, Serializable {

    private static final long serialVersionUID = 1L;
    
    private List<Entry> entries = new ArrayList<Entry>();
    
    public BlockIntersectionTable() {
    }
    
    public BlockIntersectionTable(BlockIntersectionTable other) {
        for (Entry entry: other) {
            entries.add(entry);
        }
    }
    
    @Override
    public Iterator<Entry> iterator() {
        return entries.iterator();
    }
    
    public void merge(Block block, Time time) {
        List<Entry> otherEntries = new ArrayList<Entry>(1);
        List<Block> blocks = new ArrayList<Block>();
        blocks.add(block);
        otherEntries.add(new Entry(blocks, new Interval(time, block.getLength())));
        this.entries = mergeEntryLists(Arrays.asList(this.entries, otherEntries));
    }

    public void merge(BlockIntersectionTable other) {
        this.entries = mergeEntryLists(Arrays.asList(this.entries, other.entries));
    }
    
    public void merge(BlockIntersectionTable... others) {
        List<List<Entry>> moreEntries = new ArrayList<List<Entry>>(others.length + 1);
        moreEntries.add(this.entries);
        for (BlockIntersectionTable table : others) {
            moreEntries.add(table.entries);
        }
        this.entries = mergeEntryLists(moreEntries);
    }

    public static BlockIntersectionTable fromEntries(Collection<Entry> entryCollection) {
        return fromEntries(entryCollection.toArray(new Entry[entryCollection.size()]));
    }

    public static BlockIntersectionTable fromEntries(Entry... entryArray) {
        List<List<Entry>> entryLists = new ArrayList<List<Entry>>(entryArray.length);
        for (int i=0; i<entryArray.length; i++) {
            entryLists.add(new ArrayList<Entry>(Arrays.asList(entryArray[i])));
        }
        BlockIntersectionTable table = new BlockIntersectionTable();
        table.entries = mergeEntryLists(entryLists);
        return table;
    }
    
    public static BlockIntersectionTable mergeAll(Collection<BlockIntersectionTable> others) {
        return mergeAll(others.toArray(new BlockIntersectionTable[others.size()]));
    }
    
    public static BlockIntersectionTable mergeAll(BlockIntersectionTable... others) {
        List<List<Entry>> moreEntries = new ArrayList<List<Entry>>(others.length);
        for (BlockIntersectionTable table: others) {
            moreEntries.add(table.entries);
        }
        BlockIntersectionTable table = new BlockIntersectionTable();
        table.entries = mergeEntryLists(moreEntries);
        return table;
    }
    
    private static List<Entry> mergeEntryLists(List<List<Entry>> moreEntries) {
        List<EntryIteratorProgress> progresses = new ArrayList<EntryIteratorProgress>(moreEntries.size());
        for (List<Entry> entries: moreEntries) {
            progresses.add(new EntryIteratorProgress(entries));
        }
        List<Entry> mergedEntries = new ArrayList<Entry>();
        Set<Block> blocks = null;
        Time prevTime = null;
        while (true) {
            Time nextTime = fetchNextTime(progresses);
            if (nextTime == null) {
                break;
            }
            if (blocks != null && !blocks.isEmpty()) {
                mergedEntries.add(new Entry(blocks, new Interval(prevTime, nextTime)));
            }
            blocks = fetchBlocksAt(progresses, nextTime);
            prevTime = nextTime;
        }
        return mergedEntries;
    }
    
    private static Time fetchNextTime(List<EntryIteratorProgress> progresses) {
        Time nextTime = null;
        for (Iterator<EntryIteratorProgress> it = progresses.iterator(); it.hasNext(); ) {
            EntryIteratorProgress progress = it.next();
            Time breakTime = progress.getNextTime();
            if (breakTime == null) {
                it.remove();
            } else if (nextTime == null) {
                nextTime = breakTime;
            } else if (breakTime.compareTo(nextTime) < 0) {
                nextTime = breakTime;
            }
        }
        return nextTime;
    }
    
    private static Set<Block> fetchBlocksAt(List<EntryIteratorProgress> progresses, Time time) {
        Set<Block> blocks = new HashSet<Block>();
        for (EntryIteratorProgress progress: progresses) {
            progress.moveTo(time);
            Entry progressEntry = progress.getCurrentEntry();
            if (progressEntry != null) {
                blocks.addAll(progressEntry.getBlocksInternal());
            }
        }
        return blocks;
    }

    public static class Entry implements Serializable {

        private static final long serialVersionUID = 1L;
        
        private final Interval interval;
        
        private final Set<Block> blocks = new HashSet<Block>();
        
        public Entry(Entry other) {
            this(other.getBlocks(), other.getInterval());
        }

        public Entry(Collection<Block> blocks, Interval interval) {
            this.interval = interval;
            this.blocks.addAll(blocks);
        }
        
        public Interval getInterval() {
            return interval;
        }
        
        private Set<Block> getBlocksInternal() {
            return blocks;
        }

        public BlockList getBlocks() {
            return new BlockList(blocks);
        }
        
    }
    
    private static class EntryIteratorProgress {
        
        final Iterator<Entry> iterator;
        
        Time time = null;
        
        Entry loadedEntry = null;
        
        EntryIteratorProgress(Collection<Entry> entries) {
            this(entries.iterator());
        }

        EntryIteratorProgress(Iterator<Entry> iterator) {
            this.iterator = iterator;
            if (iterator.hasNext()) {
                loadedEntry = iterator.next();
            }
        }

        Time getNextTime() {
            if (loadedEntry == null) {
                return null;
            } else {
                Interval interval = loadedEntry.getInterval();
                Time start = interval.getStart();
                Time end = interval.getEnd();
                if (time == null || time.compareTo(start) < 0) {
                    return start;
                } else {
                    return end;
                }
            }
        }

        Entry getCurrentEntry() {
            if (time == null) {
                return null;
            } else if (loadedEntry == null) {
                return null;
            } else if (time.compareTo(loadedEntry.getInterval().getStart()) < 0) {
                return null;
            } else {
                return loadedEntry;
            }
        }

        void moveTo(Time time) {
            if (this.time != null && time.compareTo(this.time) <= 0) {
                return;
            }
            while (loadedEntry != null && loadedEntry.getInterval().getEnd().compareTo(time) <= 0) {
                if (iterator.hasNext()) {
                    loadedEntry = iterator.next();
                } else {
                    loadedEntry = null;
                }
            }
            this.time = time;
        }
        
    }

}
