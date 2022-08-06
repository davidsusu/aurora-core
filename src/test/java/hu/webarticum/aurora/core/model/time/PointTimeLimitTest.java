package hu.webarticum.aurora.core.model.time;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

class PointTimeLimitTest {

    private static final Interval[] intervals = new Interval[] {
        new Interval(0, 5),
        new Interval(5, 10),
        new Interval(5, 15),
        new Interval(10, 10),
        new Interval(10, 15),
        new Interval(15, 20),
    };
    
    @Test
    void testBasics() {
        PointTimeLimit point = new PointTimeLimit(10);
        
        assertThat(point.isAlways()).isFalse();
        assertThat(point.isNever()).isFalse();
        assertThat(point.getTimes()).containsExactly(new Time(10));
        assertThat(point.getTimes(false)).containsExactly(new Time(10));
        assertThat(point.getTimes(true)).containsExactly(new Time(10));
    }

    @Test
    void testLimitSortedIntervals() {
        PointTimeLimit point = new PointTimeLimit(10);
        List<Interval> expected = Arrays.asList(new Interval(10, 10));
        List<Interval> actual = point.limitSortedIntervals(Arrays.asList(intervals));
        
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testLimitIntersectingSortedIntervals() {
        PointTimeLimit point = new PointTimeLimit(10);
        List<Interval> expected = Arrays.asList(new Interval(5, 15));
        List<Interval> actual = point.limitIntersectingSortedIntervals(Arrays.asList(intervals));
        
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testContainsInterval() {
        PointTimeLimit limit = new PointTimeLimit(10);
        
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

        assertThat(actuals).isEqualTo(expecteds);
    }

    @Test
    void testContainsTime() {
        PointTimeLimit point = new PointTimeLimit(10);
        
        assertThat(point.contains(new Time(5))).isFalse();
        assertThat(point.contains(new Time(10))).isTrue();
        assertThat(point.contains(new Time(15))).isFalse();
    }
    
}
