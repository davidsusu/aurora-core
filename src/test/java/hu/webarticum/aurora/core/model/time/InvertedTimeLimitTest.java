package hu.webarticum.aurora.core.model.time;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class InvertedTimeLimitTest {

    @Test
    public void testAlwaysAndNever() {
        assertThat(new InvertedTimeLimit(new AlwaysTimeLimit()).isAlways()).isFalse();
        assertThat(new InvertedTimeLimit(new AlwaysTimeLimit()).isNever()).isTrue();
        assertThat(new InvertedTimeLimit(new NeverTimeLimit()).isAlways()).isTrue();
        assertThat(new InvertedTimeLimit(new NeverTimeLimit()).isNever()).isFalse();
        assertThat(new InvertedTimeLimit(new Interval(0, 10)).isAlways()).isFalse();
        assertThat(new InvertedTimeLimit(new Interval(0, 10)).isNever()).isFalse();
    }

    @Test
    public void testGetTimes() {
        assertThat(new InvertedTimeLimit(new AlwaysTimeLimit()).getTimes()).isEmpty();;
        assertThat(new InvertedTimeLimit(new Interval(5, 12)).getTimes()).containsExactly(new Time(5), new Time(12));
        assertThat(new InvertedTimeLimit(new Interval(5, 12)).getTimes(false)).containsExactly(new Time(12));
        assertThat(new InvertedTimeLimit(new Interval(5, 12)).getTimes(true)).containsExactly(new Time(5));
    }
    
    @Test
    public void testContainsTime() {
        assertThat(new InvertedTimeLimit(new Interval(5, 10)).contains(new Time(3))).isTrue();
        assertThat(new InvertedTimeLimit(new Interval(5, 10)).contains(new Time(5))).isTrue();
        assertThat(new InvertedTimeLimit(new Interval(5, 10)).contains(new Time(7))).isFalse();
        assertThat(new InvertedTimeLimit(new Interval(5, 10)).contains(new Time(10))).isTrue();
        assertThat(new InvertedTimeLimit(new Interval(5, 10)).contains(new Time(12))).isTrue();
    }

    @Test
    public void testContainsInterval() {
        assertThat(new InvertedTimeLimit(new Interval(5, 10)).contains(new Interval(2, 4))).isTrue();
        assertThat(new InvertedTimeLimit(new Interval(5, 10)).contains(new Interval(2, 7))).isFalse();
        assertThat(new InvertedTimeLimit(new Interval(5, 10)).contains(new Interval(5, 8))).isFalse();
        assertThat(new InvertedTimeLimit(new Interval(5, 10)).contains(new Interval(5, 20))).isFalse();
    }
    
}
