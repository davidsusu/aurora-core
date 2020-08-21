package hu.webarticum.aurora.core.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

public class PeriodSet extends TreeSet<Period> implements Comparable<PeriodSet> {
    
    private static final long serialVersionUID = 1L;
    
    
    private Comparator<Period> comparator;
    

    public PeriodSet() {
        this((Collection<Period>) null);
    }

    public PeriodSet(Period... periods) {
        this(Arrays.asList(periods));
    }
    
    public PeriodSet(Collection<Period> periods) {
        this(periods, new MultiComparator<Period>(new Period.PeriodComparator(), false));
    }

    private PeriodSet(Collection<Period> periods, Comparator<Period> comparator) {
        super(comparator);
        this.comparator = comparator;
        if (periods != null) {
            addAll(periods);
        }
    }

    public PeriodSet getAllByTerm(int term) {
        PeriodSet result = new PeriodSet();
        for (Period period : this) {
            if (period.getTerm() == term) {
                result.add(period);
            }
        }
        return result;
    }

    public PeriodSet getAllByPosition(int position) {
        PeriodSet result = new PeriodSet();
        for (Period period : this) {
            if (period.getPosition() == position) {
                result.add(period);
            }
        }
        return result;
    }

    public PeriodSet getAllByTermAndPosition(int term, int position) {
        PeriodSet result = new PeriodSet();
        for (Period period : this) {
            if (period.getTerm() == term && period.getPosition() == position) {
                result.add(period);
            }
        }
        return result;
    }

    @Override
    public int compareTo(PeriodSet other) {
        Iterator<Period> selfIterator = iterator();
        Iterator<Period> otherIterator = other.iterator();
        while (selfIterator.hasNext() && otherIterator.hasNext()) {
            Period selfPeriod = selfIterator.next();
            Period otherPeriod = otherIterator.next();
            int cmp = comparator.compare(selfPeriod, otherPeriod);
            if (cmp != 0) {
                return cmp;
            }
        }
        if (selfIterator.hasNext()) {
            return 1;
        } else if (otherIterator.hasNext()) {
            return -1;
        } else {
            return 0;
        }
    }
    
}