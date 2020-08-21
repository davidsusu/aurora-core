package hu.webarticum.aurora.core.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import hu.webarticum.aurora.core.model.time.AlwaysTimeLimit;
import hu.webarticum.aurora.core.model.time.TimeLimit;

public abstract class Aspect implements Labeled {
    
    private static final long serialVersionUID = 1L;
    
    private String label = "";
    
    private String acronym = "";
    
    private Color color = Color.WHITE;
    
    private boolean timingSetEnabled = false;

    private boolean timeLimitEnabled = false;
    
    private final TimingSetManager timingSetManager = new TimingSetManager();

    private final TimeLimitManager timeLimitManager = new TimeLimitManager();
    
    protected Aspect() {
    }

    public Aspect(String label) {
        this.label = label;
    }

    public Aspect(String label, Color color) {
        this.label = label;
        this.color = color;
    }

    protected Aspect(String label, String acronym, Color color) {
        this.label = label;
        this.acronym = acronym;
        this.color = color;
    }
    
    @Override
    public String getLabel() {
        return label;
    }
    
    public void setLabel(String label) {
        this.label = label;
    }

    public String getAcronym() {
        return acronym;
    }
    
    public void setAcronym(String acronym) {
        this.acronym = acronym;
    }

    public Color getColor() {
        return color;
    }
    
    public void setColor(Color color) {
        this.color = color;
    }
    
    public TimingSetManager getTimingSetManager() {
        return timingSetManager;
    }
    
    public TimeLimitManager getTimeLimitManager() {
        return timeLimitManager;
    }
    
    public void setTimingSetEnabled(boolean timingSetEnabled) {
        this.timingSetEnabled = timingSetEnabled;
    }
    
    public boolean isTimingSetEnabled() {
        return timingSetEnabled;
    }

    public void setTimeLimitEnabled(boolean timeLimitEnabled) {
        this.timeLimitEnabled = timeLimitEnabled;
    }
    
    public boolean isTimeLimitEnabled() {
        return timeLimitEnabled;
    }
    
    public class TimingSetManager implements Serializable {

        private static final long serialVersionUID = 1L;
        
        private TimingSet defaultTimingSet = null;
        
        private Map<Period, TimingSet> periodTimingSets = new HashMap<Period, TimingSet>();

        public TimingSet getDefaultTimingSet() {
            return defaultTimingSet;
        }
        
        public void setDefaultTimingSet(TimingSet timingSet) {
            this.defaultTimingSet = timingSet;
        }
        
        public void removeDefaultTimingSet() {
            defaultTimingSet = null;
        }

        public TimingSet getPeriodTimingSet(Period period) {
            return periodTimingSets.get(period);
        }
        
        public void setPeriodTimingSet(Period period, TimingSet timingSet) {
            periodTimingSets.put(period, timingSet);
        }
        
        public void removePeriodTimingSet(Period period) {
            periodTimingSets.remove(period);
        }
        
        public Map<Period, TimingSet> getPeriodTimingSets() {
            return new HashMap<Period, TimingSet>(periodTimingSets);
        }
        
        public void removePeriodTimingSets() {
            periodTimingSets.clear();
        }
        
        public TimingSet getCalculatedPeriodTimingSet(Period period) {
            TimingSet result = getPeriodTimingSet(period);
            if (result == null) {
                if (defaultTimingSet != null) {
                    result = defaultTimingSet;
                } else {
                    result = new TimingSet();
                }
            }
            return result;
        }
        
    }

    public class TimeLimitManager implements Serializable {

        private static final long serialVersionUID = 1L;
        
        private TimeLimit defaultTimeLimit = null;

        private Map<Period, TimeLimit> periodTimeLimits = new HashMap<Period, TimeLimit>();
        
        public TimeLimit getDefaultTimeLimit() {
            return defaultTimeLimit;
        }

        public void setDefaultTimeLimit(TimeLimit timeLimit) {
            this.defaultTimeLimit = timeLimit;
        }

        public void removeDefaultTimeLimit() {
            defaultTimeLimit = null;
        }

        public TimeLimit getPeriodTimeLimit(Period period) {
            return periodTimeLimits.get(period);
        }

        public void setPeriodTimeLimit(Period period, TimeLimit timeLimit) {
            periodTimeLimits.put(period, timeLimit);
        }

        public void removePeriodTimeLimit(Period period) {
            periodTimeLimits.remove(period);
        }

        public Map<Period, TimeLimit> getPeriodTimeLimits() {
            return new HashMap<Period, TimeLimit>(periodTimeLimits);
        }

        public void removePeriodTimeLimits() {
            periodTimeLimits.clear();
        }
        
        public TimeLimit getCalculatedPeriodTimeLimit(Period period) {
            TimeLimit result = getPeriodTimeLimit(period);
            if (result == null) {
                if (defaultTimeLimit != null) {
                    result = defaultTimeLimit;
                } else {
                    result = AlwaysTimeLimit.INSTANCE;
                }
            }
            return result;
        }
        
    }
    
    
}
