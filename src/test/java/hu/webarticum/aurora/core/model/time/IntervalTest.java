package hu.webarticum.aurora.core.model.time;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class IntervalTest {

    private static final Interval[] intervals = new Interval[] {
        new Interval(0, 5),
        new Interval(0, 10),
        new Interval(0, 15),
        new Interval(5, 10),
        new Interval(5, 15),
        new Interval(10, 10),
        new Interval(10, 15),
        new Interval(10, 25),
        new Interval(15, 20),
        new Interval(15, 25),
        new Interval(20, 20),
        new Interval(20, 30),
        new Interval(25, 80),
        new Interval(45, 60),
    };
    
    @Test
    public void testBasics() {
        Interval interval = new Interval(10, 20);
        assertFalse(interval.isAlways());
        assertFalse(interval.isNever());
        assertEquals(10L, interval.getLength());
        assertEquals(Arrays.asList(new Time(10), new Time(20)), interval.getTimes());
        assertEquals(Arrays.asList(new Time(10)), interval.getTimes(false));
        assertEquals(Arrays.asList(new Time(20)), interval.getTimes(true));
    }

    @Test
    public void testLimitSortedIntervals() {
        Interval interval = new Interval(10, 20);
        List<Interval> expected = Arrays.asList(new Interval[] {
            new Interval(10, 10),
            new Interval(10, 15),
            new Interval(15, 20),
            new Interval(20, 20),
        });
        List<Interval> actual = interval.limitSortedIntervals(Arrays.asList(intervals));
        assertEquals(expected, actual);
    }

    @Test
    public void testLimitIntersectingSortedIntervals() {
        Interval interval = new Interval(10, 20);
        List<Interval> expected = Arrays.asList(new Interval[] {
            new Interval(0, 15),
            new Interval(5, 15),
            new Interval(10, 15),
            new Interval(10, 25),
            new Interval(15, 20),
            new Interval(15, 25),
        });
        List<Interval> actual = interval.limitIntersectingSortedIntervals(
            Arrays.asList(intervals)
        );
        assertEquals(expected, actual);
    }

    @Test
    public void testContainsInterval() {
        Interval limit = new Interval(10, 20);
        
        List<Interval> actuals = new ArrayList<Interval>();
        for (Interval interval : intervals) {
            if (limit.contains(interval)) {
                actuals.add(interval);
            }
        }
        
        List<Interval> expecteds = limit.limitSortedIntervals(Arrays.asList(intervals));
        
        assertEquals(expecteds, actuals);
    }

    @Test
    public void testIntersectsInterval() {
        Interval limit = new Interval(10, 20);
        
        List<Interval> actuals = new ArrayList<Interval>();
        for (Interval interval : intervals) {
            if (limit.intersects(interval)) {
                actuals.add(interval);
            }
        }
        
        List<Interval> expecteds = limit.limitIntersectingSortedIntervals(
            Arrays.asList(intervals)
        );
        
        assertEquals(expecteds, actuals);
    }

    @Test
    public void testContainsTime() {
        Interval limit = new Interval(10, 20);
        assertFalse(limit.contains(new Time(5)));
        assertTrue(limit.contains(new Time(10)));
        assertTrue(limit.contains(new Time(15)));
        assertTrue(limit.contains(new Time(20)));
        assertFalse(limit.contains(new Time(25)));
    }

    @Test
    public void testContainsTimeInclusion() {
        Interval limit = new Interval(10, 20);

        assertFalse(limit.contains(new Time(5), true, true));
        assertFalse(limit.contains(new Time(5), true, false));
        assertFalse(limit.contains(new Time(5), false, true));
        assertFalse(limit.contains(new Time(5), false, false));

        assertTrue(limit.contains(new Time(10), true, true));
        assertTrue(limit.contains(new Time(10), true, false));
        assertFalse(limit.contains(new Time(10), false, true));
        assertFalse(limit.contains(new Time(10), false, false));

        assertTrue(limit.contains(new Time(15), true, true));
        assertTrue(limit.contains(new Time(15), true, false));
        assertTrue(limit.contains(new Time(15), false, true));
        assertTrue(limit.contains(new Time(15), false, false));

        assertTrue(limit.contains(new Time(20), true, true));
        assertFalse(limit.contains(new Time(20), true, false));
        assertTrue(limit.contains(new Time(20), false, true));
        assertFalse(limit.contains(new Time(20), false, false));

        assertFalse(limit.contains(new Time(25), true, true));
        assertFalse(limit.contains(new Time(25), true, false));
        assertFalse(limit.contains(new Time(25), false, true));
        assertFalse(limit.contains(new Time(25), false, false));
    }
    
}
