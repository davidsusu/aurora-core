package hu.webarticum.aurora.core.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ResourceSubsetList extends ArrayList<ResourceSubset> {

    private static final long serialVersionUID = 1L;

    
    public ResourceSubsetList() {
        super();
    }
    
    public ResourceSubsetList(Collection<ResourceSubset> resourceSubsets) {
        super(resourceSubsets);
    }
    
    public ResourceSubsetList(ResourceSubset... resourceSubsets) {
        super(Arrays.<ResourceSubset>asList(resourceSubsets));
    }
    

    public List<Resource> getResources() {
        Set<Resource> resourceSet = new LinkedHashSet<Resource>();
        for (ResourceSubset resourceSubset : this) {
            resourceSet.add(resourceSubset.getResource());
        }
        return new ArrayList<Resource>(resourceSet);
    }
    
    public int getMaximumCount(Resource resource) {
        Resource.SplittingManager splittingManager = resource.getSplittingManager();
        
        int wholeCount = 0;
        
        Map<Resource.Splitting.Part, GeneralWrapper<Integer>> partCounts = new HashMap<Resource.Splitting.Part, GeneralWrapper<Integer>>();
        for (Resource.Splitting.Part part: splittingManager.getSplittingParts()) {
            partCounts.put(part, new GeneralWrapper<Integer>(0));
        }
        
        for (ResourceSubset resourceSubset: this) {
            if (resourceSubset.getResource().equals(resource)) {
                if (resourceSubset instanceof ResourceSubset.Whole) {
                    wholeCount++;
                } else if (resourceSubset instanceof ResourceSubset.SplittingPart) {
                    Resource.Splitting.Part part = ((ResourceSubset.SplittingPart)resourceSubset).getSplittingPart();
                    GeneralWrapper<Integer> countWrapper = partCounts.get(part);
                    countWrapper.set(countWrapper.get() + 1); 
                } else {
                    // general case fallback
                    return getMaximumCountWithIntersectionCounter(resource);
                }
            }
        }
        
        int count = wholeCount;
        for (Resource.Splitting splitting: splittingManager.getSplittings()) {
            int splittingMaxCount = 0;
            for (Resource.Splitting.Part splittingPart: splitting.getParts()) {
                int splittingPartCount = partCounts.get(splittingPart).get();
                if (splittingPartCount > splittingMaxCount) {
                    splittingMaxCount = splittingPartCount;
                }
            }
            count += splittingMaxCount;
        }
        return count;
    }

    private int getMaximumCountWithIntersectionCounter(Resource resource) {
        IntersectionCounter intersectionCounter = null;
        for (ResourceSubset resourceSubset: this) {
            if (resourceSubset.getResource().equals(resource)) {
                Iterable<Set<Resource.Splitting.Part>> leafIterable = resourceSubset.getResourceSubsetIntersectionTree().getLeafIterable();
                if (intersectionCounter == null) {
                    intersectionCounter = new IntersectionCounter(leafIterable);
                } else {
                    intersectionCounter.merge(leafIterable);
                }
            }
        }
        
        if (intersectionCounter == null) {
            return 0;
        } else {
            return intersectionCounter.getMaximumCount();
        }
    }
    
    
    private static class IntersectionCounter {

        LinkedList<Entry> entries = new LinkedList<Entry>();
        
        
        IntersectionCounter(Iterable<Set<Resource.Splitting.Part>> initialIntersections) {
            for (Set<Resource.Splitting.Part> splittingParts: initialIntersections) {
                entries.add(new Entry(new HashSet<Resource.Splitting.Part>(splittingParts), 1));
            }
        }

        void merge(Iterable<Set<Resource.Splitting.Part>> intersections) {
            for (Set<Resource.Splitting.Part> intersection : intersections) {
                merge(intersection);
            }
        }
        
        void merge(Set<Resource.Splitting.Part> intersection) {
            List<Entry> entriesToAppend = new ArrayList<Entry>();
            Iterator<Entry> entryIterator = entries.iterator();
            while (entryIterator.hasNext()) {
                Entry currentEntry = entryIterator.next();
                if (currentEntry.splittingParts.equals(intersection)) {
                    currentEntry.count++;
                } else {
                    Merge merge = createMerge(currentEntry.splittingParts, intersection);
                    for (Set<Resource.Splitting.Part> splittingParts: merge.diff1) {
                        entriesToAppend.add(new Entry(splittingParts, currentEntry.count));
                    }
                    if (merge.intersection != null) {
                        entriesToAppend.add(new Entry(merge.intersection, currentEntry.count + 1));
                    }
                    for (Set<Resource.Splitting.Part> splittingParts: merge.diff2) {
                        collectEntriesFromDiff2Parts(splittingParts, entriesToAppend);
                    }
                    entryIterator.remove();
                }
            }
            entries.addAll(entriesToAppend);
        }
        
        void collectEntriesFromDiff2Parts(
            Set<Resource.Splitting.Part> splittingParts, List<Entry> entriesToAppend
        ) {
            boolean exists = false;
            for (Entry checkEntry: entries) {
                if (checkEntry.splittingParts.equals(splittingParts)) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                for (Entry checkEntry: entriesToAppend) {
                    if (checkEntry.splittingParts.equals(splittingParts)) {
                        exists = true;
                        break;
                    }
                }
            }
            if (!exists) {
                entriesToAppend.add(new Entry(splittingParts, 1));
            }
        }
        
        Merge createMerge(Set<Resource.Splitting.Part> intersection1, Set<Resource.Splitting.Part> intersection2) {
            Set<Resource.Splitting.Part> only1 = new HashSet<Resource.Splitting.Part>(intersection1);
            only1.removeAll(intersection2);
            
            Set<Resource.Splitting.Part> only2 = new HashSet<Resource.Splitting.Part>(intersection2);
            only2.removeAll(intersection1);
            
            Set<Resource.Splitting.Part> common = new HashSet<Resource.Splitting.Part>(intersection1);
            common.retainAll(intersection2);
            
            Set<Resource.Splitting> splittings1 = new HashSet<Resource.Splitting>();
            for (Resource.Splitting.Part splittingPart: only1) {
                splittings1.add(splittingPart.getSplitting());
            }

            Set<Resource.Splitting> splittings2 = new HashSet<Resource.Splitting>();
            for (Resource.Splitting.Part splittingPart: only2) {
                splittings2.add(splittingPart.getSplitting());
            }

            Set<Resource.Splitting> commonSplittings = new HashSet<Resource.Splitting>(splittings1);
            commonSplittings.retainAll(splittings2);

            Merge merge = new Merge();
            
            if (!commonSplittings.isEmpty()) {
                merge.diff1.add(intersection1);
                merge.diff2.add(intersection2);
                return merge;
            }
            
            Set<Resource.Splitting.Part> diffBase1 = new HashSet<Resource.Splitting.Part>(intersection1);
            for (Resource.Splitting.Part part2: only2) {
                Resource.Splitting splitting2 = part2.getSplitting();
                for (Resource.Splitting.Part partOut2: splitting2.getParts()) {
                    if (!partOut2.equals(part2)) {
                        Set<Resource.Splitting.Part> diffItem1 = new HashSet<Resource.Splitting.Part>(diffBase1);
                        diffItem1.add(partOut2);
                        merge.diff1.add(diffItem1);
                    }
                }
                diffBase1.add(part2);
            }
            
            Set<Resource.Splitting.Part> diffBase2 = new HashSet<Resource.Splitting.Part>(intersection2);
            for (Resource.Splitting.Part part1: only1) {
                Resource.Splitting splitting1 = part1.getSplitting();
                for (Resource.Splitting.Part partOut1: splitting1.getParts()) {
                    if (!partOut1.equals(part1)) {
                        Set<Resource.Splitting.Part> diffItem2 = new HashSet<Resource.Splitting.Part>(diffBase2);
                        diffItem2.add(partOut1);
                        merge.diff2.add(diffItem2);
                    }
                }
                diffBase2.add(part1);
            }
            
            merge.intersection = new HashSet<Resource.Splitting.Part>(intersection1);
            merge.intersection.addAll(only2);
            return merge;
        }
        
        int getMaximumCount() {
            int count = 0;
            for (Entry entry: entries) {
                if (entry.count>count) {
                    count = entry.count;
                }
            }
            return count;
        }

        @Override
        public String toString() {
            return entries.toString();
        }
        
        
        private static class Entry {
            
            Set<Resource.Splitting.Part> splittingParts;
            
            int count;
            
            Entry(Set<Resource.Splitting.Part> splittingParts, int count) {
                this.splittingParts = splittingParts;
                this.count = count;
            }
            
            @Override
            public String toString() {
                return splittingParts.toString() + ": " + count;
            }
            
        }
        
        
        private static class Merge {

            List<Set<Resource.Splitting.Part>> diff1 = new ArrayList<Set<Resource.Splitting.Part>>();

            Set<Resource.Splitting.Part> intersection = null;

            List<Set<Resource.Splitting.Part>> diff2 = new ArrayList<Set<Resource.Splitting.Part>>();

            @Override
            public String toString() {
                return "{intersection: " + intersection + ", diff1: " + diff1 + ", diff2: " + diff2 + "}";
            }
            
        }
        
    }
    
}