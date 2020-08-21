package hu.webarticum.aurora.core.model.time;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class PointTimeLimitTest {

    private static final Interval[] intervals = new Interval[] {
        new Interval(0, 5),
        new Interval(5, 10),
        new Interval(5, 15),
        new Interval(10, 10),
        new Interval(10, 15),
        new Interval(15, 20),
    };
    
    @Test
    public void testBasics() {
        PointTimeLimit point = new PointTimeLimit(10);
        assertFalse(point.isAlways());
        assertFalse(point.isNever());
        assertEquals(Arrays.asList(new Time(10)), point.getTimes());
        assertEquals(Arrays.asList(new Time(10)), point.getTimes(false));
        assertEquals(Arrays.asList(new Time(10)), point.getTimes(true));
    }

    @Test
    public void testLimitSortedIntervals() {
        PointTimeLimit point = new PointTimeLimit(10);
        List<Interval> expected = Arrays.asList(new Interval(10, 10));
        List<Interval> actual = point.limitSortedIntervals(Arrays.asList(intervals));
        assertEquals(expected, actual);
    }

    @Test
    public void testLimitIntersectingSortedIntervals() {
        PointTimeLimit point = new PointTimeLimit(10);
        List<Interval> expected = Arrays.asList(new Interval(5, 15));
        List<Interval> actual = point.limitIntersectingSortedIntervals(
            Arrays.asList(intervals)
        );
        assertEquals(expected, actual);
    }

    @Test
    public void testContainsInterval() {
        PointTimeLimit limit = new PointTimeLimit(10);
        
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
        PointTimeLimit limit = new PointTimeLimit(10);
        
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
        PointTimeLimit point = new PointTimeLimit(10);
        assertFalse(point.contains(new Time(5)));
        assertTrue(point.contains(new Time(10)));
        assertFalse(point.contains(new Time(15)));
    }
    
}
