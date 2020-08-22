package hu.webarticum.aurora.core.model;

import java.io.Serializable;
import java.util.Collection;

public interface ActivityFilter extends Serializable {

    public boolean validate(Activity activity);
    
    
    public static class TrueActivityFilter implements ActivityFilter {

        private static final long serialVersionUID = 1L;

        @Override
        public boolean validate(Activity activity) {
            return true;
        }
        
    }

    
    public static class FalseActivityFilter implements ActivityFilter {

        private static final long serialVersionUID = 1L;

        @Override
        public boolean validate(Activity activity) {
            return false;
        }
        
    }
    
    
    public static class HasTag implements ActivityFilter {

        private static final long serialVersionUID = 1L;

        
        private final Tag tag;
        
        
        public HasTag(Tag tag) {
            this.tag = tag;
        }

        
        @Override
        public boolean validate(Activity activity) {
            return activity.getTagManager().has(tag);
        }
        
    }

    
    public static class HasTagType implements ActivityFilter {

        private static final long serialVersionUID = 1L;

        
        private final Tag.Type tagType;
        
        
        public HasTagType(Tag.Type tagType) {
            this.tagType = tagType;
        }

        
        @Override
        public boolean validate(Activity activity) {
            return activity.getTagManager().hasType(tagType);
        }
        
    }
    
    public static class HasResource implements ActivityFilter {

        private static final long serialVersionUID = 1L;

        
        private final Resource resource;
        
        
        public HasResource(Resource resource) {
            this.resource = resource;
        }

        
        @Override
        public boolean validate(Activity activity) {
            return activity.getResourceManager().hasResource(resource);
        }
        
    }

    public static class HasResourceType implements ActivityFilter {

        private static final long serialVersionUID = 1L;

        
        private final Resource.Type resourceType;
        
        
        public HasResourceType(Resource.Type resourceType) {
            this.resourceType = resourceType;
        }

        
        @Override
        public boolean validate(Activity activity) {
            return activity.getResourceManager().hasResourceType(resourceType);
        }
        
    }

    public static class IntersectsResourceSplittingPart implements ActivityFilter {

        private static final long serialVersionUID = 1L;

        
        private final Resource.Splitting.Part splittingPart;
        
        
        public IntersectsResourceSplittingPart(Resource.Splitting.Part splittingPart) {
            this.splittingPart = splittingPart;
        }

        
        @Override
        public boolean validate(Activity activity) {
            return activity.getResourceManager().intersectsResourceSplittingPart(splittingPart);
        }
        
    }

    public static class HasAspect implements ActivityFilter {

        private static final long serialVersionUID = 1L;

        
        private final Aspect aspect;

        
        public HasAspect(Aspect aspect) {
            this.aspect = aspect;
        }

        
        @Override
        public boolean validate(Activity activity) {
            if (aspect instanceof Resource) {
                return activity.getResourceManager().hasResource((Resource)aspect);
            } else if (aspect instanceof Tag) {
                return activity.getTagManager().has((Tag)aspect);
            } else {
                return false;
            }
        }
        
    }

    public static class And implements ActivityFilter {

        private static final long serialVersionUID = 1L;

        
        private final ActivityFilter[] filters;

        
        public And(ActivityFilter... filters) {
            this.filters = filters;
        }

        public And(Collection<ActivityFilter> filters) {
            this(filters.toArray(new ActivityFilter[filters.size()]));
        }
        

        @Override
        public boolean validate(Activity activity) {
            for (ActivityFilter filter : filters) {
                if (!filter.validate(activity)) {
                    return false;
                }
            }
            return true;
        }
        
    }
    
    public static class Or implements ActivityFilter {

        private static final long serialVersionUID = 1L;

        
        private final ActivityFilter[] filters;
        
        
        public Or(ActivityFilter... filters) {
            this.filters = filters;
        }

        public Or(Collection<ActivityFilter> filters) {
            this(filters.toArray(new ActivityFilter[filters.size()]));
        }

        
        @Override
        public boolean validate(Activity activity) {
            for (ActivityFilter filter : filters) {
                if (filter.validate(activity)) {
                    return true;
                }
            }
            return false;
        }
        
    }
    
    public static class Not implements ActivityFilter {

        private static final long serialVersionUID = 1L;

        
        private final ActivityFilter filter;

        
        public Not(ActivityFilter filter) {
            this.filter = filter;
        }
        

        @Override
        public boolean validate(Activity activity) {
            return !filter.validate(activity);
        }
        
    }
    
}
