package hu.webarticum.aurora.core.model.time;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

public class NeverTimeLimitTest {

    @Test
    public void testBasics() {
        NeverTimeLimit never = new NeverTimeLimit();
        assertFalse(never.isAlways());
        assertTrue(never.isNever());
        assertEquals(Arrays.<Time>asList(), never.getTimes());
        assertEquals(Arrays.<Time>asList(), never.getTimes(false));
        assertEquals(Arrays.<Time>asList(), never.getTimes(true));
    }

}
