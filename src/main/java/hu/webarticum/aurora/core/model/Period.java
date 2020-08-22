package hu.webarticum.aurora.core.model;

import java.io.Serializable;
import java.util.Comparator;

public class Period implements Labeled {

    private static final long serialVersionUID = 1L;

    
    private String label = "";

    private int term = 0;

    private int position = 0;

    
    public Period() {
    }
    
    public Period(String label) {
        this.label = label;
    }

    public Period(String label, int position) {
        this.label = label;
        this.position = position;
    }

    public Period(String label, int term, int position) {
        this.label = label;
        this.term = term;
        this.position = position;
    }

    
    public void setLabel(String label) {
        this.label = label;
    }
    
    @Override
    public String getLabel() {
        return label;
    }

    public void setTerm(int term) {
        this.term = term;
    }
    
    public int getTerm() {
        return term;
    }

    public void setPosition(int position) {
        this.position = position;
    }
    
    public int getPosition() {
        return position;
    }
    
    @Override
    public String toString() {
        return String.format("Period: '%s' (%d/%d)", label, term, position);
    }
    

    public static class PeriodComparator implements Comparator<Period>, Serializable {

        private static final long serialVersionUID = 1L;

        @Override
        public int compare(Period period1, Period period2) {
            int result = Integer.compare(period1.term, period2.term);
            if (result == 0) {
                result = Integer.compare(period1.position, period2.position);
                if (result == 0) {
                    result = Labeled.LabeledComparator.compareStatic(period1, period2);
                }
            }
            return result;
        }
        
    }
    
}
