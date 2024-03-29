package hu.webarticum.aurora.core.model.time;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

class IntervalTest {

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
    void testBasics() {
        Interval interval = new Interval(10, 20);
        
        assertThat(interval.isAlways()).isFalse();
        assertThat(interval.isNever()).isFalse();
        assertThat(interval.getLength()).isEqualTo(10L);
        assertThat(interval.getTimes()).containsExactly(new Time(10), new Time(20));
        assertThat(interval.getTimes(false)).containsExactly(new Time(10));
        assertThat(interval.getTimes(true)).containsExactly(new Time(20));
    }

    @Test
    void testLimitSortedIntervals() {
        Interval interval = new Interval(10, 20);
        List<Interval> expected = Arrays.asList(new Interval[] { // NOSONAR comma
            new Interval(10, 10),
            new Interval(10, 15),
            new Interval(15, 20),
            new Interval(20, 20),
        });
        List<Interval> actual = interval.limitSortedIntervals(Arrays.asList(intervals));
        
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testLimitIntersectingSortedIntervals() {
        Interval interval = new Interval(10, 20);
        List<Interval> expected = Arrays.asList(new Interval[] { // NOSONAR comma
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
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testContainsInterval() {
        Interval limit = new Interval(10, 20);
        
        List<Interval> actuals = new ArrayList<Interval>();
        for (Interval interval : intervals) {
            if (limit.contains(interval)) {
                actuals.add(interval);
            }
        }
        
        List<Interval> expecteds = limit.limitSortedIntervals(Arrays.asList(intervals));

        assertThat(actuals).isEqualTo(expecteds);
    }

    @Test
    void testIntersectsInterval() {
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
        
        assertThat(actuals).isEqualTo(expecteds);
    }

    @Test
    void testContainsTime() {
        Interval limit = new Interval(10, 20);
        
        assertThat(limit.contains(new Time(5))).isFalse();
        assertThat(limit.contains(new Time(10))).isTrue();
        assertThat(limit.contains(new Time(15))).isTrue();
        assertThat(limit.contains(new Time(20))).isTrue();
        assertThat(limit.contains(new Time(25))).isFalse();
    }

    @Test
    void testContainsTimeInclusion() {
        Interval limit = new Interval(10, 20);

        assertThat(limit.contains(new Time(5), true, true)).isFalse();
        assertThat(limit.contains(new Time(5), true, false)).isFalse();
        assertThat(limit.contains(new Time(5), false, true)).isFalse();
        assertThat(limit.contains(new Time(5), false, false)).isFalse();

        assertThat(limit.contains(new Time(10), true, true)).isTrue();
        assertThat(limit.contains(new Time(10), true, false)).isTrue();
        assertThat(limit.contains(new Time(10), false, true)).isFalse();
        assertThat(limit.contains(new Time(10), false, false)).isFalse();

        assertThat(limit.contains(new Time(15), true, true)).isTrue();
        assertThat(limit.contains(new Time(15), true, false)).isTrue();
        assertThat(limit.contains(new Time(15), false, true)).isTrue();
        assertThat(limit.contains(new Time(15), false, false)).isTrue();

        assertThat(limit.contains(new Time(20), true, true)).isTrue();
        assertThat(limit.contains(new Time(20), true, false)).isFalse();
        assertThat(limit.contains(new Time(20), false, true)).isTrue();
        assertThat(limit.contains(new Time(20), false, false)).isFalse();

        assertThat(limit.contains(new Time(25), true, true)).isFalse();
        assertThat(limit.contains(new Time(25), true, false)).isFalse();
        assertThat(limit.contains(new Time(25), false, true)).isFalse();
        assertThat(limit.contains(new Time(25), false, false)).isFalse();
    }
    
}
