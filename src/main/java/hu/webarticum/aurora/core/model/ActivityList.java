package hu.webarticum.aurora.core.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ActivityList extends ArrayList<Activity> {

    private static final long serialVersionUID = 1L;

    
    public ActivityList() {
        super();
    }

    public ActivityList(Activity... activities) {
        super(Arrays.<Activity>asList(activities));
    }
    
    public ActivityList(Collection<Activity> activities) {
        super(activities);
    }
    
    
    public ActivityList copy() {
        return new ActivityList(this);
    }
    
    public Set<Tag> getTags() {
        Set<Tag> tags = new HashSet<Tag>();
        for (Activity activity: this) {
            tags.addAll(activity.getTagManager().getTags());
        }
        return tags;
    }

    public Set<Tag> getTags(Tag.Type type) {
        Set<Tag> tags = new HashSet<Tag>();
        for (Activity activity: this) {
            tags.addAll(activity.getTagManager().getTags(type));
        }
        return tags;
    }

    public Set<Resource> getResources() {
        Set<Resource> resources = new HashSet<Resource>();
        for (Activity activity: this) {
            resources.addAll(activity.getResourceManager().getResources());
        }
        return resources;
    }

    public Set<Resource> getResources(Resource.Type type) {
        Set<Resource> resources = new HashSet<Resource>();
        for (Activity activity: this) {
            resources.addAll(activity.getResourceManager().getResources(type));
        }
        return resources;
    }

    public ResourceSubsetList getResourceSubsets() {
        ResourceSubsetList resourceSubsets = new ResourceSubsetList();
        for (Activity activity: this) {
            resourceSubsets.addAll(activity.getResourceManager().getResourceSubsets());
        }
        return resourceSubsets;
    }

    public ResourceSubsetList getResourceSubsets(Resource.Type type) {
        ResourceSubsetList resourceSubsets = new ResourceSubsetList();
        for (Activity activity: this) {
            resourceSubsets.addAll(activity.getResourceManager().getResourceSubsets(type));
        }
        return resourceSubsets;
    }
    
    public ResourceSubsetList getResourceSubsets(Resource resource) {
        ResourceSubsetList resourceSubsets = new ResourceSubsetList();
        for (Activity activity: this) {
            resourceSubsets.addAll(activity.getResourceManager().getResourceSubsets(resource));
        }
        return resourceSubsets;
    }
    
    public boolean hasConflicts() {
        Map<Resource, ResourceSubsetList> map = new HashMap<Resource, ResourceSubsetList>();
        for (Activity activity: this) {
            Activity.ResourceManager resourceManager = activity.getResourceManager();
            List<Resource> resources = resourceManager.getResources();
            for (Resource resource: resources) {
                ResourceSubsetList subsets = resourceManager.getResourceSubsets(resource);
                if (map.containsKey(resource)) {
                    map.get(resource).addAll(subsets);
                } else {
                    map.put(resource, subsets);
                }
            }
        }
        for (Map.Entry<Resource, ResourceSubsetList> entry: map.entrySet()) {
            if (hasSubsetListConflicts(entry.getValue())) {
                return true;
            }
        }
        return false;
    }
    
    public boolean conflictsWith(ActivityList other) {
        for (Map<Resource, ResourceSubsetList> map: collectMaps()) {
            for (Map.Entry<Resource, ResourceSubsetList> entry: map.entrySet()) {
                Resource resource = entry.getKey();
                ResourceSubsetList subsets = new ResourceSubsetList();
                for (Activity activity: other) {
                    subsets.addAll(activity.getResourceManager().getResourceSubsets(resource));
                }
                if (haveSubsetListsConflicts(entry.getValue(), subsets)) {
                    return true;
                }
            }
        }
        return false;
    }

    public ActivityList filter(ActivityFilter activityFilter) {
        ActivityList result = new ActivityList();
        for (Activity activity: this) {
            if (activityFilter.validate(activity)) {
                result.add(activity);
            }
        }
        return result;
    }
    
    private List<Map<Resource, ResourceSubsetList>> collectMaps() {
        Map<Resource, ResourceSubsetList> lightMap = new HashMap<Resource, ResourceSubsetList>();
        Map<Resource, ResourceSubsetList> heavyMap = new HashMap<Resource, ResourceSubsetList>();
        for (Activity activity: this) {
            Activity.ResourceManager resourceManager = activity.getResourceManager();
            for (ResourceSubset resourceSubset: resourceManager.getResourceSubsets()) {
                Resource resource = resourceSubset.getResource();
                boolean heavy = resource.getQuantity() > 1 || resource.hasSplittings();
                Map<Resource, ResourceSubsetList> map = heavy ? heavyMap : lightMap;
                ResourceSubsetList resourceSubsets = map.get(resource);
                if (resourceSubsets == null) {
                    resourceSubsets = new ResourceSubsetList();
                    map.put(resource, resourceSubsets);
                }
                resourceSubsets.add(resourceSubset);
            }
        }
        return new ArrayList<Map<Resource, ResourceSubsetList>>(Arrays.asList(lightMap, heavyMap));
    }

    private boolean hasSubsetListConflicts(ResourceSubsetList list) {
        if (list.isEmpty()) {
            return false;
        }
        Resource resource = list.get(0).getResource();
        int quantity = resource.getQuantity();
        if (list.size() <= quantity) {
            return false;
        }
        if (!resource.hasSplittings()) {
            return true;
        }
        int wholes = 0;
        for (ResourceSubset subset: list) {
            if (subset.isWhole()) {
                wholes++;
            }
        }
        if (wholes > quantity) {
            return true;
        }
        return (list.getMaximumCount(resource) > quantity);
    }
    
    private boolean haveSubsetListsConflicts(ResourceSubsetList checkedList1, ResourceSubsetList checkedList2) {
        if (checkedList1.isEmpty() || checkedList2.isEmpty()) {
            return false;
        }
        ResourceSubset first1 = checkedList1.get(0);
        ResourceSubset first2 = checkedList2.get(0);
        Resource resource = first1.getResource();
        int quantity = resource.getQuantity();
        if (quantity == 0 || checkedList1.size() + checkedList2.size() <= quantity) {
            return false;
        }
        // most likely case
        if (first1 instanceof ResourceSubset.Whole && !first2.isEmpty()) {
            return true;
        }
        int simpleWholes = 0;
        for (ResourceSubset subset: checkedList1) {
            if (subset instanceof ResourceSubset.Whole) {
                simpleWholes++;
            }
        }
        for (ResourceSubset subset: checkedList2) {
            if (subset instanceof ResourceSubset.Whole) {
                simpleWholes++;
            }
        }
        if (simpleWholes > quantity) {
            return true;
        }
        if (quantity > 1) {
            ResourceSubsetList concatenated = new ResourceSubsetList(checkedList1);
            concatenated.addAll(checkedList2);
            return (concatenated.getMaximumCount(resource) > quantity);
        }
        return haveIntersection(checkedList1, checkedList2);
    }
    
    private boolean haveIntersection(ResourceSubsetList checkedList1, ResourceSubsetList checkedList2) {
        for (ResourceSubset subset1: checkedList1) {
            for (ResourceSubset subset2: checkedList2) {
                if (subset1.intersects(subset2)) {
                    return true;
                }
            }
        }
        return false;
    }
    
}