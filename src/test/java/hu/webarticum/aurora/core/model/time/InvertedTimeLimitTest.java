package hu.webarticum.aurora.core.model.time;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

public class InvertedTimeLimitTest {

    @Test
    public void testAlwaysAndNever() {
        assertFalse(new InvertedTimeLimit(new AlwaysTimeLimit()).isAlways());
        assertTrue(new InvertedTimeLimit(new AlwaysTimeLimit()).isNever());
        assertTrue(new InvertedTimeLimit(new NeverTimeLimit()).isAlways());
        assertFalse(new InvertedTimeLimit(new NeverTimeLimit()).isNever());
        assertFalse(new InvertedTimeLimit(new Interval(0, 10)).isAlways());
        assertFalse(new InvertedTimeLimit(new Interval(0, 10)).isNever());
    }

    @Test
    public void testGetTimes() {
        assertEquals(new ArrayList<Time>(), new InvertedTimeLimit(new AlwaysTimeLimit()).getTimes());
        assertEquals(
            Arrays.asList(new Time(5), new Time(12)),
            new InvertedTimeLimit(new Interval(5, 12)).getTimes()
        );
        assertEquals(
            Arrays.asList(new Time(12)),
            new InvertedTimeLimit(new Interval(5, 12)).getTimes(false)
        );
        assertEquals(
            Arrays.asList(new Time(5)),
            new InvertedTimeLimit(new Interval(5, 12)).getTimes(true)
        );
    }
    
    @Test
    public void testContainsTime() {
        assertTrue(new InvertedTimeLimit(new Interval(5, 10)).contains(new Time(3)));
        assertTrue(new InvertedTimeLimit(new Interval(5, 10)).contains(new Time(5)));
        assertFalse(new InvertedTimeLimit(new Interval(5, 10)).contains(new Time(7)));
        assertTrue(new InvertedTimeLimit(new Interval(5, 10)).contains(new Time(10)));
        assertTrue(new InvertedTimeLimit(new Interval(5, 10)).contains(new Time(12)));
    }

    @Test
    public void testContainsInterval() {
        assertTrue(new InvertedTimeLimit(new Interval(5, 10)).contains(new Interval(2, 4)));
        assertFalse(new InvertedTimeLimit(new Interval(5, 10)).contains(new Interval(2, 7)));
        assertFalse(new InvertedTimeLimit(new Interval(5, 10)).contains(new Interval(5, 8)));
        assertFalse(new InvertedTimeLimit(new Interval(5, 10)).contains(new Interval(5, 20)));
    }
    
}
