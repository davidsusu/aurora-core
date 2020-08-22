package hu.webarticum.aurora.core.model.time;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.ThrowableAssert;
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
        assertThatThrownBy(new ThrowableAssert.ThrowingCallable() { @Override public void call() throws Throwable {
            new CustomTimeLimit(false, new Time(10), new Time(7), new Time(14));
        }}).isInstanceOf(CustomTimeLimit.InvalidTimeOrderException.class);
    }
    
    @Test
    public void testCopyStartFalse() {
        CustomTimeLimit limit = new CustomTimeLimit(false, switches);
        CustomTimeLimit copiedLimit = new CustomTimeLimit(limit);
        
        assertThat(copiedLimit).isEqualTo(limit);
    }

    @Test
    public void testCopyStartTrue() {
        CustomTimeLimit limit = new CustomTimeLimit(true, switches);
        CustomTimeLimit copiedLimit = new CustomTimeLimit(limit);
        
        assertThat(copiedLimit).isEqualTo(limit);
    }

    @Test
    public void testBasicsNever() {
        CustomTimeLimit limit = new CustomTimeLimit(false);
        
        assertThat(limit.isAlways()).isFalse();
        assertThat(limit.isNever()).isTrue();
        assertThat(limit.getStartState()).isFalse();
        assertThat(limit.getEndState()).isFalse();
        assertThat(limit.getTimes()).isEmpty();
        assertThat(limit.getTimes(false)).isEmpty();
        assertThat(limit.getTimes(true)).isEmpty();
    }

    @Test
    public void testBasicsAlways() {
        CustomTimeLimit limit = new CustomTimeLimit(true);
        
        assertThat(limit.isAlways()).isTrue();
        assertThat(limit.isNever()).isFalse();
        assertThat(limit.getStartState()).isTrue();
        assertThat(limit.getEndState()).isTrue();
        assertThat(limit.getTimes()).isEmpty();
        assertThat(limit.getTimes(false)).isEmpty();
        assertThat(limit.getTimes(true)).isEmpty();
    }

    @Test
    public void testBasicsStartFalse() {
        CustomTimeLimit limit = new CustomTimeLimit(false, switches);
        
        assertThat(limit.isAlways()).isFalse();
        assertThat(limit.isNever()).isFalse();
        assertThat(limit.getStartState()).isFalse();
        assertThat(limit.getEndState()).isFalse();
        assertThat(limit.getTimes()).containsExactly(switches);
        assertThat(limit.getTimes(false)).containsExactly(odds(switches));
        assertThat(limit.getTimes(true)).containsExactly(evens(switches));
    }

    @Test
    public void testBasicsStartTrue() {
        CustomTimeLimit limit = new CustomTimeLimit(true, switches);
        
        assertThat(limit.isAlways()).isFalse();
        assertThat(limit.isNever()).isFalse();
        assertThat(limit.getStartState()).isTrue();
        assertThat(limit.getEndState()).isTrue();
        assertThat(limit.getTimes()).containsExactly(switches);
        assertThat(limit.getTimes(false)).containsExactly(evens(switches));
        assertThat(limit.getTimes(true)).containsExactly(odds(switches));
    }
    
    @Test
    public void testLimitSortedIntervalsStartFalse() {
        CustomTimeLimit limit = new CustomTimeLimit(false, switches);
        List<Interval> actual = limit.limitSortedIntervals(Arrays.asList(intervals));
        
        List<Interval> expected = Arrays.asList(new Interval[] { // NOSONAR comma
            new Interval(10, 10),
            new Interval(10, 15),
            new Interval(15, 20),
            new Interval(50, 50),
        });
        
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testLimitSortedIntervalsStartTrue() {
        CustomTimeLimit limit = new CustomTimeLimit(true, switches);
        List<Interval> actual = limit.limitSortedIntervals(Arrays.asList(intervals));
        
        List<Interval> expected = Arrays.asList(new Interval[] { // NOSONAR comma
            new Interval(0, 10),
            new Interval(5, 10),
            new Interval(10, 10),
            new Interval(20, 30),
            new Interval(50, 50),
            new Interval(100, 150),
        });

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testLimitIntersectingSortedIntervalsStartFalse() {
        CustomTimeLimit limit = new CustomTimeLimit(false, switches);
        List<Interval> actual = limit.limitIntersectingSortedIntervals(Arrays.asList(intervals));
        
        List<Interval> expected = Arrays.asList(new Interval[] { // NOSONAR comma
            new Interval(0, 15),
            new Interval(5, 15),
            new Interval(10, 15),
            new Interval(10, 25),
            new Interval(15, 20),
            new Interval(15, 25),
            new Interval(25, 80),
            new Interval(45, 60),
        });

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testLimitIntersectingSortedIntervalsStartTrue() {
        CustomTimeLimit limit = new CustomTimeLimit(true, switches);
        List<Interval> actual = limit.limitIntersectingSortedIntervals(Arrays.asList(intervals));

        List<Interval> expected = Arrays.asList(new Interval[] { // NOSONAR comma
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

        assertThat(actual).isEqualTo(expected);
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
        
        assertThat(actuals).isEqualTo(expecteds);
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

        assertThat(actuals).isEqualTo(expecteds);
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

        assertThat(actuals).isEqualTo(expecteds);
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

        assertThat(actuals).isEqualTo(expecteds);
    }

    @Test
    public void testContainsTimeStartFalse() {
        CustomTimeLimit limit = new CustomTimeLimit(false, switches);
        assertThat(limit.contains(new Time(0))).isFalse();
        assertThat(limit.contains(new Time(10))).isTrue();
        assertThat(limit.contains(new Time(15))).isTrue();
        assertThat(limit.contains(new Time(45))).isTrue();
        assertThat(limit.contains(new Time(300))).isFalse();
    }

    @Test
    public void testContainsTimeInclusionStartFalse() {
        CustomTimeLimit limit = new CustomTimeLimit(false, switches);

        assertThat(limit.contains(new Time(0), true, true)).isFalse();
        assertThat(limit.contains(new Time(0), true, false)).isFalse();
        assertThat(limit.contains(new Time(0), false, true)).isFalse();
        assertThat(limit.contains(new Time(0), false, false)).isFalse();

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

        assertThat(limit.contains(new Time(100), true, true)).isFalse();
        assertThat(limit.contains(new Time(100), true, false)).isFalse();
        assertThat(limit.contains(new Time(100), false, true)).isFalse();
        assertThat(limit.contains(new Time(100), false, false)).isFalse();
    }
    
    @Test
    public void testContainsTimeStartTrue() {
        CustomTimeLimit limit = new CustomTimeLimit(true, switches);
        
        assertThat(limit.contains(new Time(0))).isTrue();
        assertThat(limit.contains(new Time(10))).isTrue();
        assertThat(limit.contains(new Time(15))).isFalse();
        assertThat(limit.contains(new Time(45))).isFalse();
        assertThat(limit.contains(new Time(300))).isTrue();
    }

    @Test
    public void testContainsTimeInclusionStartTrue() {
        CustomTimeLimit limit = new CustomTimeLimit(true, switches);

        assertThat(limit.contains(new Time(0), true, true)).isTrue();
        assertThat(limit.contains(new Time(0), true, false)).isTrue();
        assertThat(limit.contains(new Time(0), false, true)).isTrue();
        assertThat(limit.contains(new Time(0), false, false)).isTrue();

        assertThat(limit.contains(new Time(10), true, true)).isTrue();
        assertThat(limit.contains(new Time(10), true, false)).isFalse();
        assertThat(limit.contains(new Time(10), false, true)).isTrue();
        assertThat(limit.contains(new Time(10), false, false)).isFalse();

        assertThat(limit.contains(new Time(15), true, true)).isFalse();
        assertThat(limit.contains(new Time(15), true, false)).isFalse();
        assertThat(limit.contains(new Time(15), false, true)).isFalse();
        assertThat(limit.contains(new Time(15), false, false)).isFalse();

        assertThat(limit.contains(new Time(20), true, true)).isTrue();
        assertThat(limit.contains(new Time(20), true, false)).isTrue();
        assertThat(limit.contains(new Time(20), false, true)).isFalse();
        assertThat(limit.contains(new Time(20), false, false)).isFalse();

        assertThat(limit.contains(new Time(25), true, true)).isTrue();
        assertThat(limit.contains(new Time(25), true, false)).isTrue();
        assertThat(limit.contains(new Time(25), false, true)).isTrue();
        assertThat(limit.contains(new Time(25), false, false)).isTrue();

        assertThat(limit.contains(new Time(100), true, true)).isTrue();
        assertThat(limit.contains(new Time(100), true, false)).isTrue();
        assertThat(limit.contains(new Time(100), false, true)).isTrue();
        assertThat(limit.contains(new Time(100), false, false)).isTrue();
    }

    @Test
    public void testContainsOther() {
        CustomTimeLimit limit = new CustomTimeLimit(false, switches);
        CustomTimeLimit otherLimit1 = new CustomTimeLimit(false, new Time[] { // NOSONAR comma
            new Time(10), new Time(15),
            new Time(42), new Time(50),
        });
        assertThat(limit.contains(otherLimit1)).isTrue();

        CustomTimeLimit otherLimit2 = new CustomTimeLimit(false, new Time[] { // NOSONAR comma
            new Time(10), new Time(25),
            new Time(42), new Time(50),
        });
        
        assertThat(limit.contains(otherLimit2)).isFalse();
    }

    @Test
    public void testIntersectsOther() {
        CustomTimeLimit limit = new CustomTimeLimit(false, switches);
        CustomTimeLimit otherLimit1 = new CustomTimeLimit(false, new Time[] { // NOSONAR comma
            new Time(10), new Time(15),
            new Time(42), new Time(50),
        });
        assertThat(limit.intersects(otherLimit1)).isTrue();

        CustomTimeLimit otherLimit2 = new CustomTimeLimit(false, new Time[] { // NOSONAR comma
            new Time(5), new Time(10),
            new Time(52), new Time(55),
        });
        
        assertThat(limit.intersects(otherLimit2)).isFalse();
    }

    @Test
    public void testUnionWith() {
        CustomTimeLimit limit = new CustomTimeLimit(false, switches);
        CustomTimeLimit otherLimit = new CustomTimeLimit(false, new Time[] { // NOSONAR comma
            new Time(5), new Time(15),
            new Time(50), new Time(52),
            new Time(54), new Time(100),
            new Time(120),
        });
        
        CustomTimeLimit expected = new CustomTimeLimit(false, new Time[] { // NOSONAR comma
            new Time(5), new Time(20),
            new Time(40), new Time(52),
            new Time(54), new Time(100),
            new Time(120),
        });
        
        assertThat(limit.unionWith(otherLimit)).isEqualTo(expected);
    }

    @Test
    public void testIntersectionWith() {
        CustomTimeLimit limit = new CustomTimeLimit(false, switches);
        CustomTimeLimit otherLimit = new CustomTimeLimit(true, new Time[] { // NOSONAR comma
            new Time(15),
            new Time(50), new Time(52),
            new Time(54), new Time(100),
            new Time(120),
        });
        
        CustomTimeLimit expected = new CustomTimeLimit(false, new Time[] { // NOSONAR comma
            new Time(10), new Time(15),
            new Time(55), new Time(70),
        });
        
        assertThat(limit.intersectionWith(otherLimit)).isEqualTo(expected);
    }
    
    private Time[] evens(Time[] times) {
        Time[] result = new Time[times.length / 2];
        for (int i = 1, j = 0; i < times.length; i += 2, j++) {
            result[j] = times[i];
        }
        return result;
    }

    private Time[] odds(Time[] times) {
        Time[] result = new Time[times.length - (times.length / 2)];
        for (int i = 0, j = 0; i < times.length; i += 2, j++) {
            result[j] = times[i];
        }
        return result;
    }
    
}
