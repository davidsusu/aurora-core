package hu.webarticum.aurora.core.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;


public interface BlockFilter extends Serializable {

    boolean validate(Block block);

    public static class ActivityBlockFilter implements BlockFilter {

        private static final long serialVersionUID = 1L;

        private final ActivityFilter activityFilter;
        
        public ActivityBlockFilter(ActivityFilter activityFilter) {
            this.activityFilter = activityFilter;
        }
        
        @Override
        public boolean validate(Block block) {
            return validateActivities(block.getActivityManager().getActivities());
        }

        protected boolean validateActivities(List<Activity> activities) {
            for (Activity activity: activities) {
                if (activityFilter.validate(activity)) {
                    return true;
                }
            }
            return false;
        }
        
    }

    public static class PeriodBlockFilter implements BlockFilter {

        private static final long serialVersionUID = 1L;

        private final PeriodSet periods = new PeriodSet();
        
        public PeriodBlockFilter(Period period) {
            this.periods.add(period);
        }

        public PeriodBlockFilter(Collection<Period> periods) {
            this.periods.addAll(periods);
        }

        @Override
        public boolean validate(Block block) {
            PeriodSet commonPeriods = block.getActivityManager().getPeriods();
            commonPeriods.retainAll(periods);
            return !commonPeriods.isEmpty();
        }
        
    }
    
    public static class PeriodActivityBlockFilter extends ActivityBlockFilter {

        private static final long serialVersionUID = 1L;
        
        private final PeriodSet periods = new PeriodSet();

        public PeriodActivityBlockFilter(ActivityFilter activityFilter, Period period) {
            super(activityFilter);
            this.periods.add(period);
        }

        public PeriodActivityBlockFilter(ActivityFilter activityFilter, Collection<Period> periods) {
            super(activityFilter);
            this.periods.addAll(periods);
        }

        @Override
        public boolean validate(Block block) {
            List<Activity> activities = block.getActivityManager().getActivities(periods);
            return validateActivities(activities);
        }
        
    }

    public static class TrueBlockFilter implements BlockFilter {

        private static final long serialVersionUID = 1L;

        @Override
        public boolean validate(Block block) {
            return true;
        }
        
    }

    public static class FalseBlockFilter implements BlockFilter {

        private static final long serialVersionUID = 1L;

        @Override
        public boolean validate(Block block) {
            return false;
        }
        
    }

    public static class And implements BlockFilter {

        private static final long serialVersionUID = 1L;

        private final BlockFilter[] filters;
        
        public And(BlockFilter... filters) {
            this.filters = filters;
        }

        @Override
        public boolean validate(Block block) {
            for (BlockFilter filter : filters) {
                if (!filter.validate(block)) {
                    return false;
                }
            }
            return true;
        }
        
    }

    public static class Or implements BlockFilter {

        private static final long serialVersionUID = 1L;

        private final BlockFilter[] filters;
        
        public Or(BlockFilter... filters) {
            this.filters = filters;
        }

        @Override
        public boolean validate(Block block) {
            for (BlockFilter filter : filters) {
                if (filter.validate(block)) {
                    return true;
                }
            }
            return false;
        }
        
    }

    public static class Not implements BlockFilter {

        private static final long serialVersionUID = 1L;

        private final BlockFilter filter;

        public Not(BlockFilter filter) {
            this.filter = filter;
        }

        @Override
        public boolean validate(Block block) {
            return !filter.validate(block);
        }
        
    }
    
}
