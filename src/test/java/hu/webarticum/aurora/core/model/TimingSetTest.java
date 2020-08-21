package hu.webarticum.aurora.core.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;

import hu.webarticum.aurora.core.model.time.Interval;
import hu.webarticum.aurora.core.model.time.Time;

public class TimingSetTest {

    private TimingSet timingSet;
    

    @Test
    public void testContainsTime() {
        assertFalse(new TimingSet().containsTime(new Time(10 * Time.HOUR)));
        assertTrue(timingSet.containsTime(new Time(10 * Time.HOUR)));
        assertFalse(timingSet.containsTime(new Time(15 * Time.HOUR)));
    }

    @Test
    public void testGetTimes() {
        assertEquals(timeSet(), new TimingSet().getTimes());
        assertEquals(13, timingSet.getTimes().size());
    }

    @Test
    public void testGetLimited() {
        assertEquals(timeSet(), new TimingSet().getLimited(new Interval(0, Time.WEDNESDAY)));
        assertEquals(8, timingSet.getLimited(
            new Interval(Time.TUESDAY, Time.THURSDAY)
        ).getTimes().size());
        assertEquals(timeSet(
            new Time((Time.WEDNESDAY) + (10 * Time.HOUR)),
            new Time((Time.WEDNESDAY) + (11 * Time.HOUR))
        ), timingSet.getLimited(new Interval(
            Time.WEDNESDAY + (10 * Time.HOUR),
            Time.WEDNESDAY + (11 * Time.HOUR) + (Block.DEFAULT_LENGTH - 1)
        )).getTimes());
    }

    @Test
    public void testGetLimitedWithLength() {
        assertEquals(
            timeSet(),
            new TimingSet().getLimited(new Interval(0, Time.WEDNESDAY), Block.DEFAULT_LENGTH)
        );
        assertEquals(8, timingSet.getLimited(
            new Interval(Time.TUESDAY, Time.THURSDAY), Block.DEFAULT_LENGTH
        ).getTimes().size());
        assertEquals(timeSet(
            new Time((Time.WEDNESDAY) + (10 * Time.HOUR))
        ), timingSet.getLimited(new Interval(
            Time.WEDNESDAY + (10 * Time.HOUR),
            Time.WEDNESDAY + (11 * Time.HOUR) + (Block.DEFAULT_LENGTH - 1)
        ), Block.DEFAULT_LENGTH).getTimes());
    }

    @Test
    public void testSplit() {
        assertTrue(new TimingSet().split(Time.DAY).isEmpty());
        
        Map<Time, TimingSet> splitted = timingSet.split(Time.DAY);
        assertEquals(4, splitted.size());
        assertEquals(timeSet(
            new Time(10 * Time.HOUR),
            new Time(11 * Time.HOUR),
            new Time(12 * Time.HOUR),
            new Time(13 * Time.HOUR)
        ), splitted.get(new Time(Time.MONDAY)).getTimes());
        assertEquals(timeSet(
            new Time((Time.TUESDAY) + (9 * Time.HOUR) + (45 * Time.MINUTE)),
            new Time((Time.TUESDAY) + (10 * Time.HOUR) + (45 * Time.MINUTE)),
            new Time((Time.TUESDAY) + (11 * Time.HOUR) + (45 * Time.MINUTE)),
            new Time((Time.TUESDAY) + (12 * Time.HOUR) + (45 * Time.MINUTE))
        ), splitted.get(new Time(Time.TUESDAY)).getTimes());
        assertEquals(timeSet(
            new Time((Time.WEDNESDAY) + (9 * Time.HOUR)),
            new Time((Time.WEDNESDAY) + (10 * Time.HOUR)),
            new Time((Time.WEDNESDAY) + (11 * Time.HOUR)),
            new Time((Time.WEDNESDAY) + (12 * Time.HOUR))
        ), splitted.get(new Time(Time.WEDNESDAY)).getTimes());
        assertEquals(timeSet(
            new Time((Time.FRIDAY) + (12 * Time.HOUR))
        ), splitted.get(new Time(Time.FRIDAY)).getTimes());
    }

    @Test
    public void testSplitWithDiff() {
        assertTrue(new TimingSet().split(Time.DAY, 11 * Time.HOUR).isEmpty());
        
        Map<Time, TimingSet> splitted = timingSet.split(Time.DAY, 11 * Time.HOUR);
        assertEquals(5, splitted.size());
        assertEquals(timeSet(
            new Time(10 * Time.HOUR)
        ), splitted.get(new Time(-Time.DAY + (11 * Time.HOUR))).getTimes());
        assertEquals(timeSet(
            new Time(11 * Time.HOUR),
            new Time(12 * Time.HOUR),
            new Time(13 * Time.HOUR),
            new Time((Time.TUESDAY) + (9 * Time.HOUR) + (45 * Time.MINUTE)),
            new Time((Time.TUESDAY) + (10 * Time.HOUR) + (45 * Time.MINUTE))
        ), splitted.get(new Time(11 * Time.HOUR)).getTimes());
        assertEquals(timeSet(
            new Time((Time.TUESDAY) + (11 * Time.HOUR) + (45 * Time.MINUTE)),
            new Time((Time.TUESDAY) + (12 * Time.HOUR) + (45 * Time.MINUTE)),
            new Time((Time.WEDNESDAY) + (9 * Time.HOUR)),
            new Time((Time.WEDNESDAY) + (10 * Time.HOUR))
        ), splitted.get(new Time(Time.TUESDAY + (11 * Time.HOUR))).getTimes());
        assertEquals(timeSet(
            new Time((Time.WEDNESDAY) + (11 * Time.HOUR)),
            new Time((Time.WEDNESDAY) + (12 * Time.HOUR))
        ), splitted.get(new Time(Time.WEDNESDAY + (11 * Time.HOUR))).getTimes());
        assertEquals(timeSet(
            new Time((Time.FRIDAY) + (12 * Time.HOUR))
        ), splitted.get(new Time(Time.FRIDAY + (11 * Time.HOUR))).getTimes());
    }
    
    
    @Before
    public void buildTimingSet() {
        timingSet = new TimingSet(new Time(10 * Time.HOUR), new Time(11 * Time.HOUR));
        timingSet.add(new Time(12 * Time.HOUR));
        timingSet.add(new Time(13 * Time.HOUR));

        timingSet.add(new Time((Time.TUESDAY) + (9 * Time.HOUR) + (45 * Time.MINUTE)));
        timingSet.add(new Time((Time.TUESDAY) + (10 * Time.HOUR) + (45 * Time.MINUTE)));
        timingSet.add(new Time((Time.TUESDAY) + (11 * Time.HOUR) + (45 * Time.MINUTE)));
        timingSet.add(new Time((Time.TUESDAY) + (12 * Time.HOUR) + (45 * Time.MINUTE)));

        timingSet.add(new Time((Time.WEDNESDAY) + (9 * Time.HOUR)));
        timingSet.add(new Time((Time.WEDNESDAY) + (10 * Time.HOUR)));
        timingSet.add(new Time((Time.WEDNESDAY) + (11 * Time.HOUR)));
        timingSet.add(new Time((Time.WEDNESDAY) + (12 * Time.HOUR)));
        
        timingSet.add(new Time((Time.FRIDAY) + (12 * Time.HOUR)));
    }
    
    private SortedSet<Time> timeSet(Time... times) {
        return new TreeSet<Time>(Arrays.asList(times));
    }

}
