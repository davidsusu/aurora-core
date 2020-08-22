package hu.webarticum.aurora.core.model.time;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.ThrowableAssert;
import org.junit.Test;

public class TimeTest {

    @Test
    public void testParse() {
        assertThat(new Time("25").getSeconds()).isEqualTo(25 * Time.SECOND);
        assertThat(new Time("11:03").getSeconds()).isEqualTo((11 * Time.MINUTE) + (3 * Time.SECOND));
        assertThat(new Time("3:01:02").getSeconds()).isEqualTo((3 * Time.HOUR) + (1 * Time.MINUTE) + (2 * Time.SECOND));
        assertThat(new Time("04:05:06").getSeconds()).isEqualTo((4 * Time.HOUR) + (5 * Time.MINUTE) + (6 * Time.SECOND));
        assertThat(new Time("D.2 00:03:07").getSeconds()).isEqualTo((2 * Time.DAY) + (3 * Time.MINUTE) + (7 * Time.SECOND));
        assertThat(new Time("W.1 D.2 02:03:10").getSeconds()).isEqualTo(
            (1 * Time.WEEK) + (2 * Time.DAY) +
            (2 * Time.HOUR) + (3 * Time.MINUTE) + (10 * Time.SECOND)
        );
        assertThat(new Time("W.-1 D.2 01:01:10").getSeconds()).isEqualTo(
            (-1 * Time.WEEK) + (2 * Time.DAY) +
            (1 * Time.HOUR) + (1 * Time.MINUTE) + (10 * Time.SECOND)
        );
    }
    
    @Test
    public void testParseFail() {
        assertThatThrownBy(new ThrowableAssert.ThrowingCallable() { @Override public void call() throws Throwable {
            new Time("");
        }}).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(new ThrowableAssert.ThrowingCallable() { @Override public void call() throws Throwable {
            new Time("FOO-BAR");
        }}).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(new ThrowableAssert.ThrowingCallable() { @Override public void call() throws Throwable {
            new Time("_W.0 D.0 12:00:00");
        }}).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(new ThrowableAssert.ThrowingCallable() { @Override public void call() throws Throwable {
            new Time("W.0 D.-1 12:00:00");
        }}).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testToString() {
        assertThat(new Time(3 * Time.HOUR)).asString().isEqualTo("W.0 D.0 03:00:00");
        assertThat(new Time(
            (1 * Time.WEEK) + (2 * Time.DAY) +
            (5 * Time.HOUR) + (14 * Time.MINUTE) + (50 * Time.SECOND)
        )).asString().isEqualTo("W.1 D.2 05:14:50");
    }

    @Test
    public void testToTimeString() {
        assertThat(new Time((7 * Time.HOUR) + (4 * Time.MINUTE)).toTimeString()).isEqualTo("07:04");
        assertThat(new Time(5 * Time.HOUR).toTimeString()).isEqualTo("05:00");
        assertThat(new Time((4 * Time.HOUR) + (3 * Time.MINUTE) + (2 * Time.SECOND)).toTimeString()).isEqualTo("04:03:02");
        assertThat(new Time((3 * Time.MINUTE) + (2 * Time.SECOND)).toTimeString()).isEqualTo("00:03:02");
        assertThat(new Time(
            (1 * Time.WEEK) + (2 * Time.DAY) +
            (5 * Time.HOUR) + (14 * Time.MINUTE) + (50 * Time.SECOND)
        ).toTimeString()).isEqualTo("05:14:50");
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
        
        assertThat(timesToSort).isEqualTo(sortedTimes);
    }

    @Test
    public void testEquals() {
        Time time1 = new Time(5 * Time.MINUTE);
        Time time2 = new Time(5 * Time.MINUTE);
        Time time3 = new Time((1 * Time.WEEK) + (12 * Time.HOUR));
        Time time4 = new Time((1 * Time.WEEK) + (12 * Time.HOUR));

        assertThat(time1.equals(time1)).isTrue();
        assertThat(time1.equals(time2)).isTrue();
        assertThat(time1.equals(time3)).isFalse();
        assertThat(time1.equals(time4)).isFalse();
        assertThat(time2.equals(time1)).isTrue();
        assertThat(time2.equals(time2)).isTrue();
        assertThat(time2.equals(time3)).isFalse();
        assertThat(time2.equals(time4)).isFalse();
        assertThat(time3.equals(time1)).isFalse();
        assertThat(time3.equals(time2)).isFalse();
        assertThat(time3.equals(time3)).isTrue();
        assertThat(time3.equals(time4)).isTrue();
        assertThat(time4.equals(time1)).isFalse();
        assertThat(time4.equals(time2)).isFalse();
        assertThat(time4.equals(time3)).isTrue();
        assertThat(time4.equals(time4)).isTrue();
    }

    @Test
    public void testGetDateOnly() {
        assertThat(new Time(0).getDateOnly()).isEqualTo(new Time(0));
        assertThat(new Time((1 * Time.HOUR) + (5 * Time.MINUTE)).getDateOnly()).isEqualTo(new Time(0));
        assertThat(new Time(
            (2 * Time.WEEK) + (3 * Time.DAY)
        ).getDateOnly()).isEqualTo(new Time(
            (2 * Time.WEEK) + (3 * Time.DAY)
        ));
        assertThat(new Time(
            (3 * Time.WEEK) + (1 * Time.DAY) +
            (10 * Time.HOUR) + (2 * Time.MINUTE) + (20* Time.SECOND)
        ).getDateOnly()).isEqualTo(new Time(
            (3 * Time.WEEK) + (1 * Time.DAY)
        ));
    }

    @Test
    public void testGetTimeOnly() {
        assertThat(new Time(0).getTimeOnly()).isEqualTo(new Time(0));
        assertThat(new Time(
            (1 * Time.HOUR) + (5 * Time.MINUTE)
        ).getTimeOnly()).isEqualTo(new Time(
            (1 * Time.HOUR) + (5 * Time.MINUTE)
        ));
        assertThat(new Time((2 * Time.WEEK) + (3 * Time.DAY)).getTimeOnly()).isEqualTo(new Time(0));
        assertThat(new Time(
            (3 * Time.WEEK) + (1 * Time.DAY) +
            (10 * Time.HOUR) + (2 * Time.MINUTE) + (20* Time.SECOND)
        ).getTimeOnly()).isEqualTo(new Time(
            (10 * Time.HOUR) + (2 * Time.MINUTE) + (20* Time.SECOND)
        ));
    }

    @Test
    public void testGetMoved() {
        Time baseTime = new Time((2 * Time.DAY) + (5 * Time.HOUR));

        assertThat(baseTime.getMoved(0)).isEqualTo(baseTime);
        assertThat(baseTime.getMoved(7 * Time.MINUTE)).isEqualTo(new Time(
            (2 * Time.DAY) + (5 * Time.HOUR) + (7 * Time.MINUTE)
        ));
        assertThat(baseTime.getMoved(-3 * Time.SECOND)).isEqualTo(new Time(
            (2 * Time.DAY) + (4 * Time.HOUR) + (59 * Time.MINUTE) + (57 * Time.SECOND)
        ));
        assertThat(baseTime.getMoved(
            (1 * Time.WEEK) + (5 * Time.DAY) +
            (3 * Time.HOUR) + (12 * Time.MINUTE) + (30 * Time.SECOND)
        )).isEqualTo(new Time(
            (2 * Time.WEEK) + (0 * Time.DAY) +
            (8 * Time.HOUR) + (12 * Time.MINUTE) + (30 * Time.SECOND)
        ));
    }

    @Test
    public void testNegative() {
        Time negativeTime = new Time(-Time.DAY + (3 * Time.HOUR));

        assertThat(negativeTime.getPart(Time.PART.WEEKS)).isEqualTo(-1);
        assertThat(negativeTime.getPart(Time.PART.DAYS)).isEqualTo(6);
        assertThat(negativeTime.getPart(Time.PART.FULLDAYS)).isEqualTo(-1);
        assertThat(negativeTime.getPart(Time.PART.HOURS)).isEqualTo(3);
        assertThat(negativeTime.getPart(Time.PART.FULLHOURS)).isEqualTo(-21);
    }
    
}
