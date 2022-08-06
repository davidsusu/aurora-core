package hu.webarticum.aurora.core.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PeriodSetTest {

    private Period period00;
    private Period period01;
    private Period period02;
    private Period period10;
    private Period period11;
    private Period period12;
    private Period period20;
    private Period period21;
    private Period period22;
    
    private PeriodSet allPeriods;
    
    
    @Test
    void test() {
        assertThat((Set<Period>) allPeriods.getAllByTerm(0)).containsExactly(period00, period01, period02);
        assertThat((Set<Period>) allPeriods.getAllByTerm(1)).containsExactly(period10, period11, period12);
        assertThat((Set<Period>) allPeriods.getAllByTerm(2)).containsExactly(period20, period21, period22);
        assertThat((Set<Period>) allPeriods.getAllByPosition(1)).containsExactly(period01, period11, period21);
        assertThat((Set<Period>) allPeriods.getAllByPosition(2)).containsExactly(period02, period12, period22);
        assertThat((Set<Period>) allPeriods.getAllByTermAndPosition(1, 1)).containsExactly(period11);
        assertThat((Set<Period>) allPeriods.getAllByTermAndPosition(2, 1)).containsExactly(period21);
    }


    @BeforeEach
    public void buildThings() {
        allPeriods = new PeriodSet();
        
        period00 = new Period("Period 0/0", 0, 0);
        allPeriods.add(period00);
        
        period01 = new Period("Period 0/1", 0, 1);
        allPeriods.add(period01);
        
        period02 = new Period("Period 0/2", 0, 2);
        allPeriods.add(period02);
        
        period10 = new Period("Period 1/0", 1, 0);
        allPeriods.add(period10);
        
        period11 = new Period("Period 1/1", 1, 1);
        allPeriods.add(period11);
        
        period12 = new Period("Period 1/2", 1, 2);
        allPeriods.add(period12);
        
        period20 = new Period("Period 2/0", 2, 0);
        allPeriods.add(period20);
        
        period21 = new Period("Period 2/1", 2, 1);
        allPeriods.add(period21);
        
        period22 = new Period("Period 2/2", 2, 2);
        allPeriods.add(period22);
    }
    
}
