package hu.webarticum.aurora.core.model.time;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class TimeTest {

    @Test
    public void testParse() {
        assertEquals(
            (25 * Time.SECOND),
            new Time("25").getSeconds()
        );

        assertEquals(
            (11 * Time.MINUTE) + (3 * Time.SECOND),
            new Time("11:03").getSeconds()
        );

        assertEquals(
            (3 * Time.HOUR) + (1 * Time.MINUTE) + (2 * Time.SECOND),
            new Time("3:01:02").getSeconds()
        );

        assertEquals(
            (4 * Time.HOUR) + (5 * Time.MINUTE) + (6 * Time.SECOND),
            new Time("04:05:06").getSeconds()
        );

        assertEquals(
            (2 * Time.DAY) + (3 * Time.MINUTE) + (7 * Time.SECOND),
            new Time("D.2 00:03:07").getSeconds()
        );

        assertEquals(
            (1 * Time.WEEK) + (2 * Time.DAY) +
            (2 * Time.HOUR) + (3 * Time.MINUTE) + (10 * Time.SECOND),
            new Time("W.1 D.2 02:03:10").getSeconds()
        );

        assertEquals(
            (-1 * Time.WEEK) + (2 * Time.DAY) +
            (1 * Time.HOUR) + (1 * Time.MINUTE) + (10 * Time.SECOND),
            new Time("W.-1 D.2 01:01:10").getSeconds()
        );
    }
    
    @Test
    public void testParseFail() {
        try {
            new Time("");
            fail();
        } catch (IllegalArgumentException e) {
        }
        try {
            new Time("FOO-BAR");
            fail();
        } catch (IllegalArgumentException e) {
        }
        try {
            new Time("_W.0 D.0 12:00:00");
            fail();
        } catch (IllegalArgumentException e) {
        }
        try {
            new Time("W.0 D.-1 12:00:00");
            fail();
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testToString() {
        assertEquals(
            "W.0 D.0 03:00:00",
            new Time(3 * Time.HOUR).toString()
        );

        assertEquals(
            "W.1 D.2 05:14:50",
            new Time(
                (1 * Time.WEEK) + (2 * Time.DAY) +
                (5 * Time.HOUR) + (14 * Time.MINUTE) + (50 * Time.SECOND)
            ).toString()
        );
    }

    @Test
    public void testToTimeString() {
        assertEquals(
            "07:04",
            new Time((7 * Time.HOUR) + (4 * Time.MINUTE)).toTimeString()
        );

        assertEquals(
            "05:00",
            new Time(5 * Time.HOUR).toTimeString()
        );
        
        assertEquals(
            "04:03:02",
            new Time((4 * Time.HOUR) + (3 * Time.MINUTE) + (2 * Time.SECOND)).toTimeString()
        );
        
        assertEquals(
            "00:03:02",
            new Time((3 * Time.MINUTE) + (2 * Time.SECOND)).toTimeString()
        );
        
        assertEquals(
            "05:14:50",
            new Time(
                (1 * Time.WEEK) + (2 * Time.DAY) +
                (5 * Time.HOUR) + (14 * Time.MINUTE) + (50 * Time.SECOND)
            ).toTimeString()
        );
    }

    @Test
    public void testCompare() {
        List<Time> timesToSort = new ArrayList<Time>();
        timesToSort.add(new Time(2 * Time.MINUTE));
        timesToSort.add(new Time(0));
        timesToSort.add(new Time(5 * Time.SECOND));
        timesToSort.add(new Time((2 * Time.DAY) + (30 * Time.SECOND)));
        timesToSort.add(new Time(5 * Time.WEEK));
        timesToSort.add(new Time(48 * Time.SECOND));
        timesToSort.add(new Time(2 * Time.MINUTE));
        timesToSort.add(new Time((2 * Time.MINUTE) + (30 * Time.SECOND)));
        timesToSort.add(new Time((1 * Time.HOUR) + (30 * Time.SECOND)));
        
        List<Time> sortedTimes = new ArrayList<Time>();
        sortedTimes.add(new Time(0));
        sortedTimes.add(new Time(5 * Time.SECOND));
        sortedTimes.add(new Time(48 * Time.SECOND));
        sortedTimes.add(new Time(2 * Time.MINUTE));
        sortedTimes.add(new Time(2 * Time.MINUTE));
        sortedTimes.add(new Time((2 * Time.MINUTE) + (30 * Time.SECOND)));
        sortedTimes.add(new Time((1 * Time.HOUR) + (30 * Time.SECOND)));
        sortedTimes.add(new Time((2 * Time.DAY) + (30 * Time.SECOND)));
        sortedTimes.add(new Time(5 * Time.WEEK));
        
        Collections.sort(timesToSort);
        
        assertEquals(sortedTimes, timesToSort);
    }

    @Test
    public void testEquals() {
        Time time1 = new Time(5 * Time.MINUTE);
        Time time2 = new Time(5 * Time.MINUTE);
        Time time3 = new Time((1 * Time.WEEK) + (12 * Time.HOUR));
        Time time4 = new Time((1 * Time.WEEK) + (12 * Time.HOUR));

        assertTrue(time1.equals(time1));
        assertTrue(time1.equals(time2));
        assertFalse(time1.equals(time3));
        assertFalse(time1.equals(time4));
        assertTrue(time2.equals(time1));
        assertTrue(time2.equals(time2));
        assertFalse(time2.equals(time3));
        assertFalse(time2.equals(time4));
        assertFalse(time3.equals(time1));
        assertFalse(time3.equals(time2));
        assertTrue(time3.equals(time3));
        assertTrue(time3.equals(time4));
        assertFalse(time4.equals(time1));
        assertFalse(time4.equals(time2));
        assertTrue(time4.equals(time3));
        assertTrue(time4.equals(time4));
    }

    @Test
    public void testGetDateOnly() {
        assertEquals(new Time(0), new Time(0).getDateOnly());
        assertEquals(new Time(0), new Time((1 * Time.HOUR) + (5 * Time.MINUTE)).getDateOnly());
        assertEquals(
            new Time((2 * Time.WEEK) + (3 * Time.DAY)),
            new Time((2 * Time.WEEK) + (3 * Time.DAY)).getDateOnly()
        );
        assertEquals(
            new Time((3 * Time.WEEK) + (1 * Time.DAY)),
            new Time(
                (3 * Time.WEEK) + (1 * Time.DAY) +
                (10 * Time.HOUR) + (2 * Time.MINUTE) + (20* Time.SECOND)
            ).getDateOnly()
        );
    }

    @Test
    public void testGetTimeOnly() {
        assertEquals(new Time(0), new Time(0).getTimeOnly());
        assertEquals(
            new Time((1 * Time.HOUR) + (5 * Time.MINUTE)),
            new Time((1 * Time.HOUR) + (5 * Time.MINUTE)).getTimeOnly()
        );
        assertEquals(new Time(0), new Time((2 * Time.WEEK) + (3 * Time.DAY)).getTimeOnly());
        assertEquals(
            new Time((10 * Time.HOUR) + (2 * Time.MINUTE) + (20* Time.SECOND)),
            new Time(
                (3 * Time.WEEK) + (1 * Time.DAY) +
                (10 * Time.HOUR) + (2 * Time.MINUTE) + (20* Time.SECOND)
            ).getTimeOnly()
        );
    }

    @Test
    public void testGetMoved() {
        Time baseTime = new Time((2 * Time.DAY) + (5 * Time.HOUR));

        assertEquals(baseTime, baseTime.getMoved(0));
        assertEquals(
            new Time((2 * Time.DAY) + (5 * Time.HOUR) + (7 * Time.MINUTE)),
            baseTime.getMoved(7 * Time.MINUTE)
        );
        assertEquals(
            new Time((2 * Time.DAY) + (4 * Time.HOUR) + (59 * Time.MINUTE) + (57 * Time.SECOND)),
            baseTime.getMoved(-3 * Time.SECOND)
        );
        assertEquals(
            new Time(
                (2 * Time.WEEK) + (0 * Time.DAY) +
                (8 * Time.HOUR) + (12 * Time.MINUTE) + (30 * Time.SECOND)
            ),
            baseTime.getMoved(
                (1 * Time.WEEK) + (5 * Time.DAY) +
                (3 * Time.HOUR) + (12 * Time.MINUTE) + (30 * Time.SECOND)
            )
        );
    }

    @Test
    public void testNegative() {
        Time negativeTime = new Time(-Time.DAY + (3 * Time.HOUR));

        assertEquals(-1, negativeTime.getPart(Time.PART.WEEKS));
        assertEquals(6, negativeTime.getPart(Time.PART.DAYS));
        assertEquals(-1, negativeTime.getPart(Time.PART.FULLDAYS));
        assertEquals(3, negativeTime.getPart(Time.PART.HOURS));
        assertEquals(-21, negativeTime.getPart(Time.PART.FULLHOURS));
    }
    
}
