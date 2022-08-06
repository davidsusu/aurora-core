package hu.webarticum.aurora.core.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import hu.webarticum.aurora.core.model.time.Interval;
import hu.webarticum.aurora.core.model.time.Time;

class TimingSetTest {

    private TimingSet timingSet;
    

    @Test
    void testContainsTime() {
        assertThat(new TimingSet().containsTime(new Time(10 * Time.HOUR))).isFalse();
        assertThat(timingSet.containsTime(new Time(10 * Time.HOUR))).isTrue();
        assertThat(timingSet.containsTime(new Time(15 * Time.HOUR))).isFalse();
    }

    @Test
    void testGetTimes() {
        assertThat(new TimingSet().getTimes()).isEmpty();
        assertThat(timingSet.getTimes()).hasSize(13);
    }

    @Test
    void testGetLimited() {
        assertThat(new TimingSet().getLimited(new Interval(0, Time.WEDNESDAY))).isEmpty();
        assertThat(timingSet.getLimited(new Interval(Time.TUESDAY, Time.THURSDAY)).getTimes()).hasSize(8);
        assertThat(timingSet.getLimited(new Interval(
            Time.WEDNESDAY + (10 * Time.HOUR),
            Time.WEDNESDAY + (11 * Time.HOUR) + (Block.DEFAULT_LENGTH - 1)
        )).getTimes()).containsExactly(
            new Time((Time.WEDNESDAY) + (10 * Time.HOUR)),
            new Time((Time.WEDNESDAY) + (11 * Time.HOUR))
        );
    }

    @Test
    void testGetLimitedWithLength() {
        assertThat(new TimingSet().getLimited(new Interval(0, Time.WEDNESDAY), Block.DEFAULT_LENGTH)).isEmpty();
        assertThat(timingSet.getLimited(
            new Interval(Time.TUESDAY, Time.THURSDAY), Block.DEFAULT_LENGTH
        ).getTimes()).hasSize(8);
        assertThat(timingSet.getLimited(new Interval(
            Time.WEDNESDAY + (10 * Time.HOUR),
            Time.WEDNESDAY + (11 * Time.HOUR) + (Block.DEFAULT_LENGTH - 1)
        ), Block.DEFAULT_LENGTH).getTimes()).containsExactly(
            new Time((Time.WEDNESDAY) + (10 * Time.HOUR))
        );
    }

    @Test
    void testSplit() {
        assertThat(new TimingSet().split(Time.DAY).isEmpty()).isTrue();
        
        Map<Time, TimingSet> splitted = timingSet.split(Time.DAY);
        assertThat(splitted).hasSize(4);
        assertThat(splitted.get(new Time(Time.MONDAY)).getTimes()).containsExactly(
            new Time(10 * Time.HOUR),
            new Time(11 * Time.HOUR),
            new Time(12 * Time.HOUR),
            new Time(13 * Time.HOUR)
        );
        assertThat(splitted.get(new Time(Time.TUESDAY)).getTimes()).containsExactly(
            new Time((Time.TUESDAY) + (9 * Time.HOUR) + (45 * Time.MINUTE)),
            new Time((Time.TUESDAY) + (10 * Time.HOUR) + (45 * Time.MINUTE)),
            new Time((Time.TUESDAY) + (11 * Time.HOUR) + (45 * Time.MINUTE)),
            new Time((Time.TUESDAY) + (12 * Time.HOUR) + (45 * Time.MINUTE))
        );
        assertThat(splitted.get(new Time(Time.WEDNESDAY)).getTimes()).containsExactly(
            new Time((Time.WEDNESDAY) + (9 * Time.HOUR)),
            new Time((Time.WEDNESDAY) + (10 * Time.HOUR)),
            new Time((Time.WEDNESDAY) + (11 * Time.HOUR)),
            new Time((Time.WEDNESDAY) + (12 * Time.HOUR))
        );
        assertThat(splitted.get(new Time(Time.FRIDAY)).getTimes()).containsExactly(
            new Time((Time.FRIDAY) + (12 * Time.HOUR))
        );
    }

    @Test
    void testSplitWithDiff() {
        assertThat(new TimingSet().split(Time.DAY, 11 * Time.HOUR).isEmpty()).isTrue();
        
        Map<Time, TimingSet> splitted = timingSet.split(Time.DAY, 11 * Time.HOUR);
        assertThat(splitted).hasSize(5);
        assertThat(splitted.get(new Time(-Time.DAY + (11 * Time.HOUR))).getTimes()).containsExactly(new Time(10 * Time.HOUR));
        assertThat(splitted.get(new Time(11 * Time.HOUR)).getTimes()).containsExactly(
            new Time(11 * Time.HOUR),
            new Time(12 * Time.HOUR),
            new Time(13 * Time.HOUR),
            new Time((Time.TUESDAY) + (9 * Time.HOUR) + (45 * Time.MINUTE)),
            new Time((Time.TUESDAY) + (10 * Time.HOUR) + (45 * Time.MINUTE))
        );
        assertThat(splitted.get(new Time(Time.TUESDAY + (11 * Time.HOUR))).getTimes()).containsExactly(
            new Time((Time.TUESDAY) + (11 * Time.HOUR) + (45 * Time.MINUTE)),
            new Time((Time.TUESDAY) + (12 * Time.HOUR) + (45 * Time.MINUTE)),
            new Time((Time.WEDNESDAY) + (9 * Time.HOUR)),
            new Time((Time.WEDNESDAY) + (10 * Time.HOUR))
        );
        assertThat(splitted.get(new Time(Time.WEDNESDAY + (11 * Time.HOUR))).getTimes()).containsExactly(
            new Time((Time.WEDNESDAY) + (11 * Time.HOUR)),
            new Time((Time.WEDNESDAY) + (12 * Time.HOUR))
        );
        assertThat(splitted.get(new Time(Time.FRIDAY + (11 * Time.HOUR))).getTimes()).containsExactly(
            new Time((Time.FRIDAY) + (12 * Time.HOUR))
        );
    }
    
    
    @BeforeEach
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
    
}
