package hu.webarticum.aurora.core.model.time;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class CustomTimeLimitTest {

    private static final Interval[] intervals = new Interval[] {
        new Interval(0, 10),
        new Interval(0, 15),
        new Interval(5, 10),
        new Interval(5, 15),
        new Interval(10, 10),
        new Interval(10, 15),
        new Interval(10, 25),
        new Interval(15, 20),
        new Interval(15, 25),
        new Interval(20, 30),
        new Interval(25, 80),
        new Interval(45, 60),
        new Interval(50, 50),
        new Interval(100, 150),
    };
    
    private static final Time[] switches = new Time[] {
        new Time(10),
        new Time(20),
        new Time(40),
        new Time(50),
        new Time(55),
        new Time(70),
    };

    @Test
    public void testThrowInvalidTimeOrder() {
        try {
            new CustomTimeLimit(false, new Time(10), new Time(7), new Time(14));
            fail();
        } catch (CustomTimeLimit.InvalidTimeOrderException e) {
        }
    }
    
    @Test
    public void testCopyStartFalse() {
        CustomTimeLimit limit = new CustomTimeLimit(false, switches);
        CustomTimeLimit copiedLimit = new CustomTimeLimit(limit);
        assertEquals(limit, copiedLimit);
    }

    @Test
    public void testCopyStartTrue() {
        CustomTimeLimit limit = new CustomTimeLimit(true, switches);
        CustomTimeLimit copiedLimit = new CustomTimeLimit(limit);
        assertEquals(limit, copiedLimit);
    }

    @Test
    public void testBasicsNever() {
        CustomTimeLimit limit = new CustomTimeLimit(false);
        assertFalse(limit.isAlways());
        assertTrue(limit.isNever());
        assertFalse(limit.getStartState());
        assertFalse(limit.getEndState());
        assertEquals(Arrays.<Time>asList(), limit.getTimes());
        assertEquals(Arrays.<Time>asList(), limit.getTimes(false));
        assertEquals(Arrays.<Time>asList(), limit.getTimes(true));
    }

    @Test
    public void testBasicsAlways() {
        CustomTimeLimit limit = new CustomTimeLimit(true);
        assertTrue(limit.isAlways());
        assertFalse(limit.isNever());
        assertTrue(limit.getStartState());
        assertTrue(limit.getEndState());
        assertEquals(Arrays.<Time>asList(), limit.getTimes());
        assertEquals(Arrays.<Time>asList(), limit.getTimes(false));
        assertEquals(Arrays.<Time>asList(), limit.getTimes(true));
    }

    @Test
    public void testBasicsStartFalse() {
        CustomTimeLimit limit = new CustomTimeLimit(false, switches);
        assertFalse(limit.isAlways());
        assertFalse(limit.isNever());
        assertFalse(limit.getStartState());
        assertFalse(limit.getEndState());
        assertEquals(Arrays.asList(switches), limit.getTimes());
        assertEquals(getOdds(Arrays.asList(switches)), limit.getTimes(false));
        assertEquals(getEvens(Arrays.asList(switches)), limit.getTimes(true));
    }

    @Test
    public void testBasicsStartTrue() {
        CustomTimeLimit limit = new CustomTimeLimit(true, switches);
        assertFalse(limit.isAlways());
        assertFalse(limit.isNever());
        assertTrue(limit.getStartState());
        assertTrue(limit.getEndState());
        assertEquals(Arrays.asList(switches), limit.getTimes());
        assertEquals(getEvens(Arrays.asList(switches)), limit.getTimes(false));
        assertEquals(getOdds(Arrays.asList(switches)), limit.getTimes(true));
    }
    
    @Test
    public void testLimitSortedIntervalsStartFalse() {
        CustomTimeLimit limit = new CustomTimeLimit(false, switches);
        List<Interval> actual = limit.limitSortedIntervals(Arrays.asList(intervals));
        
        List<Interval> expected = Arrays.asList(new Interval[] {
            new Interval(10, 10),
            new Interval(10, 15),
            new Interval(15, 20),
            new Interval(50, 50),
        });
        
        assertEquals(expected, actual);
    }

    @Test
    public void testLimitSortedIntervalsStartTrue() {
        CustomTimeLimit limit = new CustomTimeLimit(true, switches);
        List<Interval> actual = limit.limitSortedIntervals(Arrays.asList(intervals));
        
        List<Interval> expected = Arrays.asList(new Interval[] {
            new Interval(0, 10),
            new Interval(5, 10),
            new Interval(10, 10),
            new Interval(20, 30),
            new Interval(50, 50),
            new Interval(100, 150),
        });
        
        assertEquals(expected, actual);
    }

    @Test
    public void testLimitIntersectingSortedIntervalsStartFalse() {
        CustomTimeLimit limit = new CustomTimeLimit(false, switches);
        List<Interval> actual = limit.limitIntersectingSortedIntervals(Arrays.asList(intervals));
        
        List<Interval> expected = Arrays.asList(new Interval[] {
            new Interval(0, 15),
            new Interval(5, 15),
            new Interval(10, 15),
            new Interval(10, 25),
            new Interval(15, 20),
            new Interval(15, 25),
            new Interval(25, 80),
            new Interval(45, 60),
        });
        
        assertEquals(expected, actual);
    }

    @Test
    public void testLimitIntersectingSortedIntervalsStartTrue() {
        CustomTimeLimit limit = new CustomTimeLimit(true, switches);
        List<Interval> actual = limit.limitIntersectingSortedIntervals(Arrays.asList(intervals));

        List<Interval> expected = Arrays.asList(new Interval[] {
            new Interval(0, 10),
            new Interval(0, 15),
            new Interval(5, 10),
            new Interval(5, 15),
            new Interval(10, 25),
            new Interval(15, 25),
            new Interval(20, 30),
            new Interval(25, 80),
            new Interval(45, 60),
            new Interval(100, 150),
        });
        
        assertEquals(expected, actual);
    }

    @Test
    public void testContainsIntervalStartFalse() {
        CustomTimeLimit limit = new CustomTimeLimit(false, switches);
        
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
    public void testContainsIntervalStartTrue() {
        CustomTimeLimit limit = new CustomTimeLimit(true, switches);
        
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
    public void testIntersectsIntervalStartFalse() {
        CustomTimeLimit limit = new CustomTimeLimit(false, switches);
        
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
    public void testIntersectsIntervalStartTrue() {
        CustomTimeLimit limit = new CustomTimeLimit(true, switches);
        
        List<Interval> actuals = new ArrayList<Interval>();
        for (Interval interval : intervals) {
            if (limit.intersects(interval)) {
                actuals.add(interval);
            }
        }
        
        List<Interval> expecteds = limit.limitIntersectingSortedIntervals(Arrays.asList(intervals));
        
        assertEquals(expecteds, actuals);
    }

    @Test
    public void testContainsTimeStartFalse() {
        CustomTimeLimit limit = new CustomTimeLimit(false, switches);
        assertFalse(limit.contains(new Time(0)));
        assertTrue(limit.contains(new Time(10)));
        assertTrue(limit.contains(new Time(15)));
        assertTrue(limit.contains(new Time(45)));
        assertFalse(limit.contains(new Time(300)));
    }

    @Test
    public void testContainsTimeInclusionStartFalse() {
        CustomTimeLimit limit = new CustomTimeLimit(false, switches);

        assertFalse(limit.contains(new Time(0), true, true));
        assertFalse(limit.contains(new Time(0), true, false));
        assertFalse(limit.contains(new Time(0), false, true));
        assertFalse(limit.contains(new Time(0), false, false));

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

        assertFalse(limit.contains(new Time(100), true, true));
        assertFalse(limit.contains(new Time(100), true, false));
        assertFalse(limit.contains(new Time(100), false, true));
        assertFalse(limit.contains(new Time(100), false, false));
    }
    
    @Test
    public void testContainsTimeStartTrue() {
        CustomTimeLimit limit = new CustomTimeLimit(true, switches);
        assertTrue(limit.contains(new Time(0)));
        assertTrue(limit.contains(new Time(10)));
        assertFalse(limit.contains(new Time(15)));
        assertFalse(limit.contains(new Time(45)));
        assertTrue(limit.contains(new Time(300)));
    }

    @Test
    public void testContainsTimeInclusionStartTrue() {
        CustomTimeLimit limit = new CustomTimeLimit(true, switches);

        assertTrue(limit.contains(new Time(0), true, true));
        assertTrue(limit.contains(new Time(0), true, false));
        assertTrue(limit.contains(new Time(0), false, true));
        assertTrue(limit.contains(new Time(0), false, false));

        assertTrue(limit.contains(new Time(10), true, true));
        assertFalse(limit.contains(new Time(10), true, false));
        assertTrue(limit.contains(new Time(10), false, true));
        assertFalse(limit.contains(new Time(10), false, false));

        assertFalse(limit.contains(new Time(15), true, true));
        assertFalse(limit.contains(new Time(15), true, false));
        assertFalse(limit.contains(new Time(15), false, true));
        assertFalse(limit.contains(new Time(15), false, false));

        assertTrue(limit.contains(new Time(20), true, true));
        assertTrue(limit.contains(new Time(20), true, false));
        assertFalse(limit.contains(new Time(20), false, true));
        assertFalse(limit.contains(new Time(20), false, false));

        assertTrue(limit.contains(new Time(25), true, true));
        assertTrue(limit.contains(new Time(25), true, false));
        assertTrue(limit.contains(new Time(25), false, true));
        assertTrue(limit.contains(new Time(25), false, false));

        assertTrue(limit.contains(new Time(100), true, true));
        assertTrue(limit.contains(new Time(100), true, false));
        assertTrue(limit.contains(new Time(100), false, true));
        assertTrue(limit.contains(new Time(100), false, false));
    }

    @Test
    public void testContainsOther() {
        CustomTimeLimit limit = new CustomTimeLimit(false, switches);
        CustomTimeLimit otherLimit1 = new CustomTimeLimit(false, new Time[] {
            new Time(10), new Time(15),
            new Time(42), new Time(50),
        });
        assertTrue(limit.contains(otherLimit1));

        CustomTimeLimit otherLimit2 = new CustomTimeLimit(false, new Time[] {
            new Time(10), new Time(25),
            new Time(42), new Time(50),
        });
        assertFalse(limit.contains(otherLimit2));
    }

    @Test
    public void testIntersectsOther() {
        CustomTimeLimit limit = new CustomTimeLimit(false, switches);
        CustomTimeLimit otherLimit1 = new CustomTimeLimit(false, new Time[] {
            new Time(10), new Time(15),
            new Time(42), new Time(50),
        });
        assertTrue(limit.intersects(otherLimit1));

        CustomTimeLimit otherLimit2 = new CustomTimeLimit(false, new Time[] {
            new Time(5), new Time(10),
            new Time(52), new Time(55),
        });
        assertFalse(limit.intersects(otherLimit2));
    }

    @Test
    public void testUnionWith() {
        CustomTimeLimit limit = new CustomTimeLimit(false, switches);
        CustomTimeLimit otherLimit = new CustomTimeLimit(false, new Time[] {
            new Time(5), new Time(15),
            new Time(50), new Time(52),
            new Time(54), new Time(100),
            new Time(120),
        });
        
        CustomTimeLimit expected = new CustomTimeLimit(false, new Time[] {
            new Time(5), new Time(20),
            new Time(40), new Time(52),
            new Time(54), new Time(100),
            new Time(120),
        });
        
        assertEquals(expected, limit.unionWith(otherLimit));
    }

    @Test
    public void testIntersectionWith() {
        CustomTimeLimit limit = new CustomTimeLimit(false, switches);
        CustomTimeLimit otherLimit = new CustomTimeLimit(true, new Time[] {
            new Time(15),
            new Time(50), new Time(52),
            new Time(54), new Time(100),
            new Time(120),
        });
        
        CustomTimeLimit expected = new CustomTimeLimit(false, new Time[] {
            new Time(10), new Time(15),
            new Time(55), new Time(70),
        });
        
        assertEquals(expected, limit.intersectionWith(otherLimit));
    }
    
    private <T> List<T> getEvens(List<T> list) {
        List<T> result = new ArrayList<T>();
        boolean even = false;
        for (T item : list) {
            if (even) {
                result.add(item);
            }
            even = !even;
        }
        return result;
    }

    private <T> List<T> getOdds(List<T> list) {
        List<T> result = new ArrayList<T>();
        boolean odd = true;
        for (T item : list) {
            if (odd) {
                result.add(item);
            }
            odd = !odd;
        }
        return result;
    }
    
}
