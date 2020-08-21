package hu.webarticum.aurora.core.model.time;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import hu.webarticum.aurora.core.model.BooleanOperation;

public class CustomTimeLimit implements TimeLimit {

    private static final long serialVersionUID = 1L;

    private final boolean startState;
    private final ArrayList<Time> switches;

    protected CustomTimeLimit(boolean startState, ArrayList<Time> switches, boolean copy, boolean check) {
        if (check) {
            Time prevTime = null;
            boolean first = true;
            for (Time time: switches) {
                if (first) {
                    first = false;
                } else {
                    if (time.compareTo(prevTime) < 0) {
                        throw new InvalidTimeOrderException();
                    }
                }
                prevTime = time;
            }
        }
        this.startState = startState;
        if (copy) {
            this.switches = new ArrayList<Time>(switches);
        } else {
            this.switches = switches;
        }
    }

    public CustomTimeLimit(boolean startState, Collection<Time> switches) {
        this(startState, new ArrayList<Time>(switches), false, true);
    }

    public CustomTimeLimit(boolean startState, Time... switches) {
        this(startState, new ArrayList<Time>(Arrays.<Time>asList(switches)));
    }

    public CustomTimeLimit(Time start, Time end) {
        this.startState = false;
        this.switches = new ArrayList<Time>();
        this.switches.add(start);
        this.switches.add(end);
    }
    
    public CustomTimeLimit(TimeLimit limit) {
        boolean startStateToSet;
        TimeLimit limitToSet = limit;
        boolean invert = false;
        while (limitToSet instanceof InvertedTimeLimit) {
            limitToSet = ((InvertedTimeLimit)limitToSet).getBase();
            invert = !invert;
        }
        if (limitToSet instanceof CustomTimeLimit) {
            CustomTimeLimit custom = (CustomTimeLimit)limitToSet;
            startStateToSet = custom.startState;
            this.switches = custom.switches;
        } else {
            this.switches = new ArrayList<Time>();
            if (limitToSet instanceof PointTimeLimit) {
                PointTimeLimit timepoint = (PointTimeLimit)limitToSet;
                startStateToSet = false;
                this.switches.add(timepoint.getTime());
                this.switches.add(timepoint.getTime());
            } else if (limitToSet instanceof Interval) {
                Interval interval = (Interval)limitToSet;
                startStateToSet = false;
                this.switches.add(interval.getStart());
                this.switches.add(interval.getEnd());
            } else if (limitToSet instanceof AlwaysTimeLimit) {
                startStateToSet = true;
            } else {
                startStateToSet = false;
            }
        }
        if (invert) {
            startStateToSet = !startStateToSet;
        }
        this.startState = startStateToSet;
    }

    @Override
    public boolean contains(Time time) {
        boolean currentState = startState;
        for (Time _time : switches) {
            int comp = time.compareTo(_time);
            if (comp < 0) {
                return currentState;
            } else if (comp == 0) {
                return true;
            }
            currentState = !currentState;
        }
        return currentState;
    }
    
    @Override
    public boolean contains(Time time, boolean includeStart, boolean includeEnd) {
        boolean currentState = startState;
        for (Time switchTime : switches) {
            int comp = time.compareTo(switchTime);
            if (comp < 0) {
                return currentState;
            } else if (comp == 0) {
                return currentState ? includeEnd : includeStart;
            }
            currentState = !currentState;
        }
        return currentState;
    }
    
    @Override
    public boolean contains(Interval interval) {
        Time start = interval.getStart();
        Time end = interval.getEnd();
        boolean currentState = startState;
        ListIterator<Time> iterator = switches.listIterator();
        while (iterator.hasNext()) {
            Time switchTime = iterator.next();
            int comp = start.compareTo(switchTime);
            if (comp > 0) {
                currentState = !currentState;
                continue;
            }
            if (start.equals(end)) {
                return (comp == 0 || currentState);
            }
            if (comp == 0) {
                currentState = !currentState;
                if (!iterator.hasNext()) {
                    return currentState;
                }
                switchTime = iterator.next();
            }
            int comp2 = end.compareTo(switchTime);
            if (comp2 > 0) {
                return false;
            } else {
                return currentState;
            }
        }
        return currentState;
    }

    @Override
    public boolean intersects(Interval interval) {
        Time start = interval.getStart();
        Time end = interval.getEnd();
        boolean eq = start.equals(end);
        boolean currentState = startState;
        ListIterator<Time> iterator = switches.listIterator();
        while (iterator.hasNext()) {
            Time switchTime = iterator.next();
            int comp = start.compareTo(switchTime);
            if (comp > 0) {
                currentState = !currentState;
                continue;
            }
            if (eq) {
                return (comp != 0 && currentState);
            } else {
                if (comp == 0) {
                    currentState = !currentState;
                    if (!iterator.hasNext()) {
                        return currentState;
                    }
                    switchTime = iterator.next();
                }
                return (currentState || end.compareTo(switchTime) > 0);
            }
        }
        return currentState;
    }

    public boolean contains(CustomTimeLimit other) {
        return filledWithOperationWith(other, BooleanOperation.NXRIGHT);
    }

    public boolean intersects(CustomTimeLimit other) {
        return hasOperationWith(other, BooleanOperation.AND);
    }
    
    public boolean hasOperationWith(CustomTimeLimit other, BooleanOperation operation) {
        for (CustomTimeLimit.MergeEntry entry: getMerge(this, other)) {
            if (operation.operate(entry.state1, entry.state2)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean filledWithOperationWith(CustomTimeLimit other, BooleanOperation operation) {
        for (CustomTimeLimit.MergeEntry entry: getMerge(this, other)) {
            if (!operation.operate(entry.state1, entry.state2)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean isAlways() {
        return (startState && switches.isEmpty());
    }

    @Override
    public boolean isNever() {
        return (!startState && switches.isEmpty());
    }
    
    public boolean getStartState() {
        return startState;
    }

    public boolean getEndState() {
        return ((switches.size() % 2 == 0) == startState);
    }

    @Override
    public List<Time> getTimes() {
        return new ArrayList<Time>(switches);
    }

    @Override
    public List<Time> getTimes(boolean endTimes) {
        List<Time> result = new ArrayList<Time>((switches.size() / 2) + 1);
        boolean includeNext = (startState == endTimes);
        for (Time time: switches) {
            if (includeNext) {
                result.add(time);
            }
            includeNext = !includeNext;
        }
        return result;
    }
    
    @Override
    public <T extends Intervalable> List<T> limitSortedIntervals(Iterable<T> intervalables) {
        List<T> result = new ArrayList<T>();
        boolean currentState =  startState;
        Iterator<Time> switchIterator = switches.iterator();
        BackableIterator<T> intervalableIterator = new BackableIterator<T>(intervalables.iterator());
        boolean firstSwitch = true;
        while (switchIterator.hasNext()) {
            if (firstSwitch) {
                firstSwitch = false;
                if (currentState) {
                    Time time = switchIterator.next();
                    collectContainedIntervalsUntil(time, intervalableIterator, result);
                }
                currentState = false;
                continue;
            }
            Time time1 = switchIterator.next();
            skipCrossingIntervals(time1, intervalableIterator);
            if (switchIterator.hasNext()) {
                Time time2 = switchIterator.next();
                collectContainedIntervalsUntil(time2, intervalableIterator, result);
            } else {
                currentState = true;
            }
        }
        if (currentState) {
            collectRemainingIntervals(intervalableIterator, result);
        }
        return result;
    }
    
    @Override
    public <T extends Intervalable> List<T> limitIntersectingSortedIntervals(Iterable<T> intervalables) {
        List<T> result = new ArrayList<T>();
        boolean currentState =  startState;
        Iterator<Time> switchIterator = switches.iterator();
        BackableIterator<T> intervalableIterator = new BackableIterator<T>(intervalables.iterator());
        boolean firstSwitch = true;
        while (switchIterator.hasNext()) {
            if (firstSwitch) {
                firstSwitch = false;
                if (currentState) {
                    Time time = switchIterator.next();
                    collectIntersectingIntervalsUntil(time, intervalableIterator, result);
                    currentState = false;
                }
                continue;
            }
            Time time1 = switchIterator.next();
            if (switchIterator.hasNext()) {
                Time time2 = switchIterator.next();
                collectIntersectingIntervalsUntil(time2, intervalableIterator, result, time1);
            } else {
                collectIntersectionCrossingIntervals(time1, intervalableIterator, result);
                currentState = true;
            }
        }
        if (currentState) {
            collectRemainingIntervals(intervalableIterator, result);
        }
        return result;
    }

    private <T extends Intervalable> void collectContainedIntervalsUntil(
        Time time, BackableIterator<T> intervalableIterator, List<T> result
    ) {
        while (intervalableIterator.hasNext()) {
            T intervalable = intervalableIterator.next();
            Interval interval = intervalable.getInterval();
            int comp = interval.getStart().compareTo(time);
            if (comp == 0 && interval.getStart().equals(interval.getEnd())) {
                result.add(intervalable);
            } else if (comp >= 0) {
                intervalableIterator.back();
                break;
            } else if (intervalable.getInterval().getEnd().compareTo(time) <= 0) {
                result.add(intervalable);
            }
        }
    }

    private <T extends Intervalable> void skipCrossingIntervals(
        Time time, BackableIterator<T> intervalableIterator
    ) {
        while (intervalableIterator.hasNext()) {
            T intervalable = intervalableIterator.next();
            if (intervalable.getInterval().getStart().compareTo(time) >= 0) {
                intervalableIterator.back();
                break;
            }
        }
    }

    private <T extends Intervalable> void collectRemainingIntervals(
            BackableIterator<T> intervalableIterator, List<T> result
    ) {
        while (intervalableIterator.hasNext()) {
            result.add(intervalableIterator.next());
        }
    }
    
    private <T extends Intervalable> void collectIntersectingIntervalsUntil(
        Time time2, BackableIterator<T> intervalableIterator, List<T> result
    ) {
        collectIntersectingIntervalsUntil(time2, intervalableIterator, result, null);
    }
    
    private <T extends Intervalable> void collectIntersectingIntervalsUntil(
        Time untilTime, BackableIterator<T> intervalableIterator, List<T> result, Time startTime
    ) {
        boolean noStartTime = (startTime == null);
        while (intervalableIterator.hasNext()) {
            T intervalable = intervalableIterator.next();
            if (intervalable.getInterval().getStart().compareTo(untilTime) >= 0) {
                intervalableIterator.back();
                break;
            } else if (noStartTime || intervalable.getInterval().getEnd().compareTo(startTime) > 0) {
                result.add(intervalable);
            }
        }
    }
    
    private <T extends Intervalable> void collectIntersectionCrossingIntervals(
        Time time, BackableIterator<T> intervalableIterator, List<T> result
    ) {
        while (intervalableIterator.hasNext()) {
            T intervalable = intervalableIterator.next();
            Interval interval = intervalable.getInterval();
            int comp = interval.getStart().compareTo(time);
            if (comp == 0 && interval.getStart().equals(interval.getEnd())) {
                // nothing to do
            } else if (comp >= 0) {
                intervalableIterator.back();
                break;
            } else if (intervalable.getInterval().getEnd().compareTo(time) > 0) {
                result.add(intervalable);
            }
        }
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof CustomTimeLimit)) {
            return false;
        }
        CustomTimeLimit otherLimit = (CustomTimeLimit)other;
        
        if (otherLimit.startState != startState) {
            return false;
        }
        
        return otherLimit.switches.equals(switches);
    }
    
    @Override
    public int hashCode() {
        int result = switches.hashCode();
        if (startState) {
            result *= 37;
        }
        return result;
    }
    
    public CustomTimeLimit inverse() {
        return new CustomTimeLimit(!startState, switches, false, false);
    }
    
    public CustomTimeLimit intersectionWith(CustomTimeLimit other) {
        return operationWith(other, BooleanOperation.AND);
    }
    
    public CustomTimeLimit unionWith(CustomTimeLimit other) {
        return operationWith(other, BooleanOperation.OR);
    }
    
    public CustomTimeLimit differenceWith(CustomTimeLimit other) {
        return operationWith(other, BooleanOperation.XLEFT);
    }
    
    public CustomTimeLimit symmetricDifferenceWith(CustomTimeLimit other) {
        return operationWith(other, BooleanOperation.XOR);
    }

    public CustomTimeLimit operationWith(CustomTimeLimit other, BooleanOperation operation) {
        Iterator<CustomTimeLimit.MergeEntry> im = getMerge(this, other).iterator();
        CustomTimeLimit.MergeEntry first = im.next();
        boolean newStartState = operation.operate(first.state1, first.state2);
        boolean latestState = newStartState;
        ArrayList<Time> newSwitches = new ArrayList<Time>();
        while (im.hasNext()) {
            CustomTimeLimit.MergeEntry current = im.next();
            boolean currentState = operation.operate(current.state1, current.state2);
            if (currentState != latestState) {
                newSwitches.add(current.time);
                latestState = currentState;
            }
        }
        return new CustomTimeLimit(newStartState, newSwitches, false, false);
    }

    public boolean contains(TimeLimit timeLimit) {
        return contains(from(timeLimit));
    }

    public boolean intersects(TimeLimit timeLimit) {
        return intersects(from(timeLimit));
    }

    public boolean hasOperationWith(TimeLimit timeLimit, BooleanOperation operation) {
        return hasOperationWith(from(timeLimit), operation);
    }
    
    public CustomTimeLimit intersectionWith(TimeLimit timeLimit) {
        return intersectionWith(from(timeLimit));
    }
    
    public CustomTimeLimit unionWith(TimeLimit timeLimit) {
        return unionWith(from(timeLimit));
    }
    
    public CustomTimeLimit differenceWith(TimeLimit timeLimit) {
        return differenceWith(from(timeLimit));
    }
    
    public CustomTimeLimit symmetricDifferenceWith(TimeLimit timeLimit) {
        return symmetricDifferenceWith(from(timeLimit));
    }

    public CustomTimeLimit operationWith(TimeLimit timeLimit, BooleanOperation operation) {
        return operationWith(from(timeLimit), operation);
    }

    @Override
    public TimeLimit getSimplified() {
        if (isNever()) {
            return new NeverTimeLimit();
        } else if (isAlways()) {
            return new AlwaysTimeLimit();
        } else if (!startState && switches.size() == 2) {
            Time startTime = switches.get(0);
            Time endTime = switches.get(1);
            if (startTime.equals(endTime)) {
                return new PointTimeLimit(startTime);
            } else {
                return new Interval(startTime, endTime);
            }
        } else {
            return this;
        }
    }
    
    public static CustomTimeLimit from(TimeLimit timeLimit) {
        if (timeLimit instanceof CustomTimeLimit) {
            return (CustomTimeLimit)timeLimit;
        } else {
            return new CustomTimeLimit(timeLimit);
        }
    }
    
    private static List<CustomTimeLimit.MergeEntry> getMerge(CustomTimeLimit custom1, CustomTimeLimit custom2) {
        List<CustomTimeLimit.MergeEntry> merge = new ArrayList<CustomTimeLimit.MergeEntry>();
        boolean state1 = custom1.startState;
        boolean state2 = custom2.startState;
        merge.add(new MergeEntry(null, state1, state2));
        ListIterator<Time> iterator2 = custom2.switches.listIterator();
        for (Time time1: custom1.switches) {
            while (iterator2.hasNext()) {
                Time time2 = iterator2.next();
                int comp = time1.compareTo(time2);
                if (comp > 0) {
                    state2 = !state2;
                    merge.add(new MergeEntry(time2, state1, state2));
                } else if (comp == 0) {
                    state2 = !state2;
                } else {
                    iterator2.previous();
                    break;
                }
            }
            state1 = !state1;
            merge.add(new MergeEntry(time1, state1, state2));
        }
        while (iterator2.hasNext()) {
            Time time2 = iterator2.next();
            state2 = !state2;
            merge.add(new MergeEntry(time2, state1, state2));
        }
        return merge;
    }
    
    private static class MergeEntry implements Comparable<CustomTimeLimit.MergeEntry> {
        
        public final Time time;
        
        public final boolean state1;
        
        public final boolean state2;
        
        public MergeEntry(Time time, boolean state1, boolean state2) {
            this.time = time;
            this.state1 = state1;
            this.state2 = state2;
        }

        @Override
        public int compareTo(CustomTimeLimit.MergeEntry other) {
            return this.time.compareTo(other.time);
        }

        @Override
        public String toString() {
            return time + ": " + state1 + ", " + state2;
        }
        
    }
    
    @Override
    public String toString() {
        StringBuilder resultBuilder = new StringBuilder();
        resultBuilder.append("TimeLimit.Custom [");
        resultBuilder.append(" ");
        boolean state = startState;
        if (state) {
            resultBuilder.append("{ ...  --->  ");
        }
        boolean first = true;
        for (Time time : switches) {
            if (first) {
                first = false;
            } else {
                resultBuilder.append(", ");
            }
            if (!state) {
                resultBuilder.append("{ ");
            }
            resultBuilder.append(time.toString());
            if (state) {
                resultBuilder.append(" }");
            } else {
                resultBuilder.append("  --->  ");
            }
            state = !state;
        }
        if (state) {
            resultBuilder.append("... } ");
        }
        resultBuilder.append(" ]");
        return resultBuilder.toString();
    }
    
    private static class BackableIterator<T> implements Iterator<T> {

        Iterator<T> innerIterator;
        
        boolean hasPrevious = false;
        
        boolean isBack = false;
        
        T previous = null;
        
        BackableIterator(Iterator<T> innerIterator) {
            this.innerIterator = innerIterator;
        }
        
        @Override
        public boolean hasNext() {
            return isBack || innerIterator.hasNext();
        }

        @Override
        public T next() {
            if (isBack) {
                isBack = false;
            } else {
                previous = innerIterator.next();
                hasPrevious = true;
            }
            return previous;
        }
        
        public void back() {
            if (isBack || !hasPrevious) {
                throw new NoSuchElementException();
            }
            
            isBack = true;
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
        
    }

    public static class InvalidTimeOrderException extends RuntimeException {

        private static final long serialVersionUID = 1L;

        @Override
        public String getMessage() {
            return "Invalid time order!";
        }

    }
    
}