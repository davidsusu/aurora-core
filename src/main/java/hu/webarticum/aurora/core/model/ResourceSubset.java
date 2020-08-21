package hu.webarticum.aurora.core.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;


public interface ResourceSubset extends Serializable {

    public Resource getResource();

    public boolean isWhole();

    public boolean isEmpty();
    
    public boolean intersects(ResourceSubset other);
    
    public boolean contains(ResourceSubset other);
    
    public ResourceSubsetList getChildren();
    
    public ResourceSubsetIntersectionTree getResourceSubsetIntersectionTree();
    
    public abstract static class AbstractResourceSubset implements ResourceSubset {
        
        private static final long serialVersionUID = 1L;

        @Override
        public boolean isWhole() {
            return getResourceSubsetIntersectionTree().isWhole();
        }

        @Override
        public boolean isEmpty() {
            return getResourceSubsetIntersectionTree().isEmpty();
        }
        
        @Override
        public boolean intersects(ResourceSubset other) {
            if (!other.getResource().equals(this.getResource())) {
                return false;
            }
            return this.getResourceSubsetIntersectionTree().intersects(
                other.getResourceSubsetIntersectionTree()
            );
        }

        @Override
        public boolean contains(ResourceSubset other) {
            if (!other.getResource().equals(this.getResource())) {
                return false;
            }
            return this.getResourceSubsetIntersectionTree().contains(
                other.getResourceSubsetIntersectionTree()
            );
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof ResourceSubset)) {
                return false;
            }
            ResourceSubset otherSubset = (ResourceSubset)other;
            if (!otherSubset.getResource().equals(this.getResource())) {
                return false;
            }
            return this.getResourceSubsetIntersectionTree().equals(
                otherSubset.getResourceSubsetIntersectionTree()
            );
        }

        @Override
        public int hashCode() {
            return this.getResource().hashCode();
        }
        
        @Override
        public String toString() {
            return "ResourceSubset." + this.getClass().getSimpleName() +
                "(" + getResource().getLabel() + ", " + this.getClass().getSimpleName() + ")"
            ;
        }
        
    }
    
    public static class Whole extends AbstractResourceSubset {

        private static final long serialVersionUID = 1L;

        private final Resource resource;
        
        public Whole(Resource resource) {
            this.resource = resource;
        }

        @Override
        public Resource getResource() {
            return resource;
        }

        @Override
        public boolean isWhole() {
            return true;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }
        
        @Override
        public boolean intersects(ResourceSubset other) {
            return (!other.isEmpty() && other.getResource().equals(resource));
        }

        @Override
        public boolean contains(ResourceSubset other) {
            return other.getResource().equals(resource);
        }

        @Override
        public ResourceSubsetList getChildren() {
            return new ResourceSubsetList();
        }

        @Override
        public ResourceSubsetIntersectionTree getResourceSubsetIntersectionTree() {
            return new ResourceSubsetIntersectionTree();
        }

    }
    
    public static class SplittingPart extends AbstractResourceSubset {

        private static final long serialVersionUID = 1L;

        private final Resource.Splitting.Part splittingPart;

        public SplittingPart(Resource.Splitting.Part splittingPart) {
            this.splittingPart = splittingPart;
        }

        public Resource.Splitting.Part getSplittingPart() {
            return splittingPart;
        }

        @Override
        public Resource getResource() {
            return splittingPart.getResource();
        }

        @Override
        public boolean isWhole() {
            return (splittingPart.getSplitting().getParts().size() == 1);
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public ResourceSubsetList getChildren() {
            return new ResourceSubsetList();
        }

        @Override
        public ResourceSubsetIntersectionTree getResourceSubsetIntersectionTree() {
            return new ResourceSubsetIntersectionTree(splittingPart);
        }

    }
    
    public static class Union extends AbstractResourceSubset {

        private static final long serialVersionUID = 1L;

        private final ResourceSubset[] subsets;

        public Union(ResourceSubset... subsets) {
            if (subsets.length == 0) {
                throw new IllegalArgumentException("At least one argument required");
            }
            boolean first = true;
            Resource baseResource = null;
            for (ResourceSubset subset: subsets) {
                if (first) {
                    baseResource = subset.getResource();
                    first = false;
                } else {
                    if (!subset.getResource().equals(baseResource)) {
                        throw new IllegalArgumentException(
                            "Subsets must be derived from the same resource"
                        );
                    }
                }
            }
            this.subsets = subsets;
        }
        
        public Union(Collection<ResourceSubset> subsets) {
            this(subsets.toArray(new ResourceSubset[subsets.size()]));
        }

        @Override
        public Resource getResource() {
            return subsets[0].getResource();
        }
        
        @Override
        public boolean isEmpty() {
            for (ResourceSubset subset : subsets) {
                if (!subset.isEmpty()) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public ResourceSubsetList getChildren() {
            return new ResourceSubsetList(subsets);
        }

        @Override
        public ResourceSubsetIntersectionTree getResourceSubsetIntersectionTree() {
            ResourceSubsetIntersectionTree result = null;
            for (ResourceSubset subset: subsets) {
                ResourceSubsetIntersectionTree subsetIntersectionTree = subset.getResourceSubsetIntersectionTree();
                if (result == null) {
                    result = subsetIntersectionTree;
                } else {
                    result.unionWith(subsetIntersectionTree);
                }
            }
            return result;
        }

    }

    public static class Intersection extends AbstractResourceSubset {

        private static final long serialVersionUID = 1L;

        private final ResourceSubset[] subsets;

        public Intersection(ResourceSubset... subsets) {
            if (subsets.length == 0) {
                throw new IllegalArgumentException("At least one argument required");
            }
            boolean first = true;
            Resource baseResource = null;
            for (ResourceSubset subset: subsets) {
                if (first) {
                    baseResource = subset.getResource();
                    first = false;
                } else {
                    if (!subset.getResource().equals(baseResource)) {
                        throw new IllegalArgumentException("Subsets must be derived from the same resource");
                    }
                }
            }
            this.subsets = subsets;
        }

        public Intersection(Collection<ResourceSubset> subsets) {
            this(subsets.toArray(new ResourceSubset[subsets.size()]));
        }

        @Override
        public Resource getResource() {
            return subsets[0].getResource();
        }

        @Override
        public ResourceSubsetList getChildren() {
            return new ResourceSubsetList(subsets);
        }

        @Override
        public ResourceSubsetIntersectionTree getResourceSubsetIntersectionTree() {
            ResourceSubsetIntersectionTree result = null;
            for (ResourceSubset subset: subsets) {
                ResourceSubsetIntersectionTree subsetIntersectionTree = subset.getResourceSubsetIntersectionTree();
                if (result == null) {
                    result = subsetIntersectionTree;
                } else {
                    result.intersectionWith(subsetIntersectionTree);
                }
            }
            return result;
        }

    }

    public static class Difference extends AbstractResourceSubset {

        private static final long serialVersionUID = 1L;

        private final ResourceSubset[] subsets;

        public Difference(ResourceSubset... subsets) {
            if (subsets.length == 0) {
                throw new IllegalArgumentException("At least one argument required");
            }
            boolean first = true;
            Resource baseResource = null;
            for (ResourceSubset subset: subsets) {
                if (first) {
                    baseResource = subset.getResource();
                    first = false;
                } else {
                    if (!subset.getResource().equals(baseResource)) {
                        throw new IllegalArgumentException("Subsets must be derived from the same resource");
                    }
                }
            }
            this.subsets = subsets;
        }

        public Difference(Collection<ResourceSubset> subsets) {
            this(subsets.toArray(new ResourceSubset[subsets.size()]));
        }

        @Override
        public Resource getResource() {
            return subsets[0].getResource();
        }

        @Override
        public ResourceSubsetList getChildren() {
            return new ResourceSubsetList(subsets);
        }

        @Override
        public ResourceSubsetIntersectionTree getResourceSubsetIntersectionTree() {
            ResourceSubsetIntersectionTree result = null;
            for (ResourceSubset subset: subsets) {
                ResourceSubsetIntersectionTree subsetIntersectionTree = subset.getResourceSubsetIntersectionTree();
                if (result == null) {
                    result = subsetIntersectionTree;
                } else {
                    result.differenceWith(subsetIntersectionTree);
                }
            }
            return result;
        }

    }

    public static class SymmetricDifference extends AbstractResourceSubset {

        private static final long serialVersionUID = 1L;

        private final ResourceSubset[] subsets;

        public SymmetricDifference(ResourceSubset... subsets) {
            if (subsets.length == 0) {
                throw new IllegalArgumentException("At least one argument required");
            }
            boolean first = true;
            Resource baseResource = null;
            for (ResourceSubset subset: subsets) {
                if (first) {
                    baseResource = subset.getResource();
                    first = false;
                } else {
                    if (!subset.getResource().equals(baseResource)) {
                        throw new IllegalArgumentException("Subsets must be derived from the same resource");
                    }
                }
            }
            this.subsets = subsets;
        }

        public SymmetricDifference(Collection<ResourceSubset> subsets) {
            this(subsets.toArray(new ResourceSubset[subsets.size()]));
        }

        @Override
        public Resource getResource() {
            return subsets[0].getResource();
        }

        @Override
        public ResourceSubsetList getChildren() {
            return new ResourceSubsetList(subsets);
        }

        @Override
        public ResourceSubsetIntersectionTree getResourceSubsetIntersectionTree() {
            ResourceSubsetIntersectionTree result = null;
            for (ResourceSubset subset: subsets) {
                ResourceSubsetIntersectionTree subsetIntersectionTree = subset.getResourceSubsetIntersectionTree();
                if (result == null) {
                    result = subsetIntersectionTree;
                } else {
                    result.symmetricDifferenceWith(subsetIntersectionTree);
                }
            }
            return result;
        }

    }

    public static class Inverse extends AbstractResourceSubset {

        private static final long serialVersionUID = 1L;

        private ResourceSubset subset;

        public Inverse(ResourceSubset subset) {
            this.subset = subset;
        }

        @Override
        public Resource getResource() {
            return subset.getResource();
        }

        @Override
        public ResourceSubsetList getChildren() {
            return new ResourceSubsetList(subset);
        }

        @Override
        public ResourceSubsetIntersectionTree getResourceSubsetIntersectionTree() {
            ResourceSubsetIntersectionTree result = subset.getResourceSubsetIntersectionTree();
            result.negate();
            return result;
        }

    }
    
    public static class ResourceSubsetComparator implements Comparator<ResourceSubset> {

        @Override
        public int compare(ResourceSubset resourceSubset1, ResourceSubset resourceSubset2) {
            Resource resource1 = resourceSubset1.getResource();
            Resource resource2 = resourceSubset2.getResource();
            if (resource1.equals(resource2)) {
                return 0;
            } else {
                Resource.ResourceComparator resourceComparator = new Resource.ResourceComparator();
                return resourceComparator.compare(resource1, resource2);
            }
        }
        
    }
    
}
