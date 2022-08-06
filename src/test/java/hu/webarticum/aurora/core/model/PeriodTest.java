package hu.webarticum.aurora.core.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

class PeriodTest {

    @Test
    void testSort() {
        Period period1 = new Period("bar");
        Period period2 = new Period("bar", 1);
        Period period3 = new Period("", 1);
        Period period4 = new Period();
        Period period5 = new Period("zzz");
        Period period6 = new Period("", 0, 2);
        Period period7 = new Period("foo", 0, 1);
        
        List<Period> periods = new ArrayList<Period>();
        periods.add(period1);
        periods.add(period2);
        periods.add(period3);
        periods.add(period4);
        periods.add(period5);
        periods.add(period6);
        periods.add(period7);
        Collections.sort(periods, new Period.PeriodComparator());

        List<Period> expected = new ArrayList<Period>();
        expected.add(period4);
        expected.add(period1);
        expected.add(period5);
        expected.add(period3);
        expected.add(period2);
        expected.add(period7);
        expected.add(period6);
        
        assertThat(periods).isEqualTo(expected);
    }

}
