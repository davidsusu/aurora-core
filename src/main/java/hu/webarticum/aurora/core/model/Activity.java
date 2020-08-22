package hu.webarticum.aurora.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Activity implements Labeled {

    private static final long serialVersionUID = 1L;

    
    private String label = "";

    private ResourceManager resourceManager = new ResourceManager();

    private TagManager tagManager = new TagManager();
    
    
    public Activity() {
    }

    public Activity(Activity other) {
        this.label = other.label;
        for (ResourceSubset resourceSubset: other.getResourceManager().getResourceSubsets()) {
            this.resourceManager.add(resourceSubset);
        }
        for (Tag tag: other.getTagManager().getTags()) {
            this.tagManager.add(tag);
        }
    }
    
    public Activity(String label) {
        this.label = label;
    }

    
    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return label;
    }

    public ResourceManager getResourceManager() {
        return resourceManager;
    }

    public TagManager getTagManager() {
        return tagManager;
    }
    
    @Override
    public String toString() {
        return "Activity: '" + label + "'";
    }
    
    public boolean conflictsWith(Activity other) {
        return new ActivityList(this).conflictsWith(new ActivityList(other));
    }
    
    
    public class ResourceManager implements Serializable {

        private static final long serialVersionUID = 1L;

        
        private ResourceSubsetList resourceSubsets = new ResourceSubsetList();
        
        
        public void add(ResourceSubset resourceSubset) {
            resourceSubsets.add(resourceSubset);
        }
        
        public void add(Resource resource) {
            resourceSubsets.add(new ResourceSubset.Whole(resource));
        }

        public boolean hasResource(Resource resource) {
            for (ResourceSubset subset: resourceSubsets) {
                if (subset.getResource().equals(resource)) {
                    return true;
                }
            }
            return false;
        }

        public boolean hasResourceType(Resource.Type type) {
            for (ResourceSubset subset: resourceSubsets) {
                if (subset.getResource().getType().equals(type)) {
                    return true;
                }
            }
            return false;
        }
        
        public boolean hasResourceWhole(Resource resource) {
            for (ResourceSubset subset: resourceSubsets) {
                Resource subsetResource = subset.getResource();
                if (subsetResource.equals(resource) && subset.isWhole()) {
                    return true;
                }
            }
            return false;
        }
        
        public boolean intersectsResourceSplittingPart(Resource.Splitting.Part resourceSplittingPart) {
            ResourceSubset.SplittingPart otherSubset = new ResourceSubset.SplittingPart(resourceSplittingPart);
            for (ResourceSubset subset: getResourceSubsets(resourceSplittingPart.getResource())) {
                if (subset.intersects(otherSubset)) {
                    return true;
                }
            }
            return false;
        }
        
        public ResourceSubsetList getResourceSubsets() {
            return new ResourceSubsetList(resourceSubsets);
        }

        public ResourceSubsetList getResourceSubsets(Resource.Type type) {
            ResourceSubsetList result = new ResourceSubsetList();
            for (ResourceSubset resourceSubset: resourceSubsets) {
                if (resourceSubset.getResource().getType().equals(type)) {
                    result.add(resourceSubset);
                }
            }
            return result;
        }
        
        public ResourceSubsetList getResourceSubsets(Resource resource) {
            ResourceSubsetList result = new ResourceSubsetList();
            for (ResourceSubset resourceSubset: resourceSubsets) {
                if (resourceSubset.getResource().equals(resource)) {
                    result.add(resourceSubset);
                }
            }
            return result;
        }
        
        public List<Resource> getResources() {
            Set<Resource> resourceSet = new HashSet<Resource>();
            for (ResourceSubset resourceSubset: resourceSubsets) {
                resourceSet.add(resourceSubset.getResource());
            }
            return new ArrayList<Resource>(resourceSet);
        }

        public List<Resource> getResources(Resource.Type type) {
            Set<Resource> resourceSet = new HashSet<Resource>();
            for (ResourceSubset resourceSubset: resourceSubsets) {
                Resource resource = resourceSubset.getResource();
                if (resource.getType().equals(type)) {
                    resourceSet.add(resource);
                }
            }
            return new ArrayList<Resource>(resourceSet);
        }
        
        public void removeResource(Resource resource) {
            Iterator<ResourceSubset> it = resourceSubsets.iterator();
            while (it.hasNext()) {
                ResourceSubset resourceSubset = it.next();
                if (resourceSubset.getResource().equals(resource)) {
                    it.remove();
                }
            }
        }

        public void clear() {
            resourceSubsets.clear();
        }

    }
    
    
    public class TagManager implements Serializable {

        private static final long serialVersionUID = 1L;

        
        private Set<Tag> tags = new HashSet<Tag>();

        
        public void add(Tag tag) {
            tags.add(tag);
        }

        public void remove(Tag tag) {
            tags.remove(tag);
        }
        
        public boolean has(Tag tag) {
            return tags.contains(tag);
        }

        public boolean hasType(Tag.Type type) {
            for (Tag tag: tags) {
                if (tag.getType().equals(type)) {
                    return true;
                }
            }
            return false;
        }
        
        public List<Tag> getTags() {
            return new ArrayList<Tag>(tags);
        }

        public Set<Tag> getTags(Tag.Type type) {
            Set<Tag> result = new HashSet<Tag>();
            for (Tag tag: tags) {
                if (tag.getType().equals(type)) {
                    result.add(tag);
                }
            }
            return result;
        }

        public void clear() {
            tags.clear();
        }

    }
    
}
