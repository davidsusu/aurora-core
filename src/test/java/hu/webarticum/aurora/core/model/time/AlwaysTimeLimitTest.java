package hu.webarticum.aurora.core.model.time;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

public class AlwaysTimeLimitTest {

    @Test
    public void testBasics() {
        AlwaysTimeLimit always = new AlwaysTimeLimit();
        assertTrue(always.isAlways());
        assertFalse(always.isNever());
        assertEquals(Arrays.<Time>asList(), always.getTimes());
        assertEquals(Arrays.<Time>asList(), always.getTimes(false));
        assertEquals(Arrays.<Time>asList(), always.getTimes(true));
    }

}
