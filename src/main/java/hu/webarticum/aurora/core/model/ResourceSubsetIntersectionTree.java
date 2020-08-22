package hu.webarticum.aurora.core.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

public class ResourceSubsetIntersectionTree {
    
    private Node rootNode = null;
    
    private LeafIterable leafIterable = new LeafIterable();
    
    
    public ResourceSubsetIntersectionTree() {
        this(true);
    }

    public ResourceSubsetIntersectionTree(boolean whole) {
        this.rootNode = new Node();
        if (whole) {
            rootNode.children = null;
        } else {
            rootNode.children = new ArrayList<Node>();
        }
    }
    
    public ResourceSubsetIntersectionTree(Resource.Splitting.Part splittingPart) {
        this.rootNode = new Node();
        this.rootNode.children = new ArrayList<Node>();
        Node childNode = new Node();
        childNode.splittingPart = splittingPart;
        this.rootNode.children.add(childNode);
    }

    
    public Iterable<Set<Resource.Splitting.Part>> getLeafIterable() {
        return leafIterable;
    }
    
    public void unionWith(ResourceSubsetIntersectionTree other) {
        unionNodeWith(rootNode, other.rootNode);
        normalizeNode(rootNode);
    }

    public void intersectionWith(ResourceSubsetIntersectionTree other) {
        intersectionNodeWith(rootNode, other.rootNode);
        normalizeNode(rootNode);
    }

    public void differenceWith(ResourceSubsetIntersectionTree other) {
        differenceNodeWith(rootNode, other.rootNode);
        normalizeNode(rootNode);
    }

    public void symmetricDifferenceWith(ResourceSubsetIntersectionTree other) {
        symmetricDifferenceNodeWith(rootNode, other.rootNode);
        normalizeNode(rootNode);
    }

    public void negate() {
        negateNode(rootNode);
        normalizeNode(rootNode);
    }
    
    public ResourceSubsetIntersectionTree copy() {
        ResourceSubsetIntersectionTree copiedTree = new ResourceSubsetIntersectionTree();
        copiedTree.rootNode = copyNode(this.rootNode);
        return copiedTree;
    }
    
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ResourceSubsetIntersectionTree)) {
            return false;
        }
        Node testNode = copyNode(rootNode);
        Node otherNode = ((ResourceSubsetIntersectionTree)object).rootNode;
        symmetricDifferenceNodeWith(testNode, otherNode);
        normalizeNode(testNode);
        return testNode.isEmpty();
    }
    
    @Override
    public int hashCode() {
        return 0; // FIXME would be too hard to compute
    }
    
    public boolean intersects(ResourceSubsetIntersectionTree other) {
        Node testNode = copyNode(rootNode);
        Node otherNode = other.rootNode;
        intersectionNodeWith(testNode, otherNode);
        normalizeNode(testNode);
        return (!testNode.isEmpty());
    }

    public boolean contains(ResourceSubsetIntersectionTree other) {
        Node testNode = copyNode(other.rootNode);
        differenceNodeWith(testNode, rootNode);
        normalizeNode(testNode);
        return testNode.isEmpty();
    }
    
    public boolean isWhole() {
        return (rootNode.splittingPart == null && rootNode.isWhole());
    }

    public boolean isEmpty() {
        return rootNode.isEmpty();
    }
    
    private void negateNode(Node node) {
        if (node.isWhole()) {
            node.makeEmpty();
        } else if (node.isEmpty()) {
            node.makeWhole();
        } else {
            Resource.Splitting splitting = node.children.get(0).splittingPart.getSplitting();
            Set<Resource.Splitting.Part> nonIncludedSplittingParts = new HashSet<Resource.Splitting.Part>(splitting.getParts());
            for (Node childNode: node.children) {
                negateNode(childNode);
                nonIncludedSplittingParts.remove(childNode.splittingPart);
            }
            for (Resource.Splitting.Part nonIncludedSplittingPart: nonIncludedSplittingParts) {
                Node additionalNode = new Node();
                additionalNode.splittingPart = nonIncludedSplittingPart;
                node.children.add(additionalNode);
            }
        }
    }
    
    private void intersectionNodeWith(Node node, Node otherNode) {
        if (node.isEmpty()) {
            return;
        }

        if (otherNode.isEmpty()) {
            node.makeEmpty();
            return;
        }
        
        if (otherNode.splittingPart == null && otherNode.isWhole()) {
            return;
        }
        
        Node checkNode = copyNode(otherNode);
        if (node.splittingPart != null) {
            intersectOutNodeSplittingPart(checkNode, node.splittingPart);
            normalizeNode(checkNode);
        }
        
        if (checkNode.isEmpty()) {
            node.makeEmpty();
            return;
        }
        
        if (node.isWhole()) {
            if (checkNode.splittingPart == null) {
                node.children = checkNode.children;
            } else {
                node.children = new ArrayList<Node>();
                node.children.add(checkNode);
            }
        } else {
            for (Node childNode: node.children) {
                intersectionNodeWith(childNode, checkNode);
            }
        }
    }
    
    private void unionNodeWith(Node node, Node otherNode) {
        if (node.isWhole() || otherNode.isEmpty()) {
            return;
        }
        
        if (otherNode.splittingPart == null && otherNode.isWhole()) {
            node.makeWhole();
            return;
        }
        
        Node checkNode = copyNode(otherNode);
        if (node.splittingPart != null) {
            intersectOutNodeSplittingPart(checkNode, node.splittingPart);
            normalizeNode(checkNode);
        }

        if (node.isEmpty()) {
            node.children = checkNode.children;
            return;
        }

        Resource.Splitting splitting = node.children.get(0).splittingPart.getSplitting();
        Set<Resource.Splitting.Part> nonIncludedSplittingParts = new HashSet<Resource.Splitting.Part>(splitting.getParts());
        for (Node childNode: node.children) {
            unionNodeWith(childNode, checkNode);
            nonIncludedSplittingParts.remove(childNode.splittingPart);
        }
        for (Resource.Splitting.Part nonIncludedSplittingPart: nonIncludedSplittingParts) {
            Node additionalNode = new Node();
            additionalNode.splittingPart = nonIncludedSplittingPart;
            additionalNode.children = new ArrayList<Node>();
            node.children.add(additionalNode);
            unionNodeWith(additionalNode, checkNode);
        }
    }
    
    private void differenceNodeWith(Node node, Node otherNode) {
        Node negatedNode = copyNode(otherNode);
        negateNode(negatedNode);
        normalizeNode(negatedNode);
        intersectionNodeWith(node, negatedNode);
    }
    
    private void symmetricDifferenceNodeWith(Node node, Node otherNode) {
        if (otherNode.isEmpty()) {
            return;
        }
        
        Node checkNode = copyNode(otherNode);
        if (node.splittingPart != null) {
            intersectOutNodeSplittingPart(checkNode, node.splittingPart);
            normalizeNode(checkNode);
            if (checkNode.isEmpty()) {
                return;
            }
        }

        boolean isEmpty = node.isEmpty();
        boolean isWhole = node.isWhole();
        
        if (isEmpty || isWhole) {
            if (isWhole) {
                negateNode(checkNode);
            }
            if (checkNode.splittingPart == null) {
                node.children = checkNode.children;
            } else {
                node.children = new ArrayList<Node>();
                node.children.add(checkNode);
            }
            return;
        }
        
        Resource.Splitting splitting = node.children.get(0).splittingPart.getSplitting();
        Set<Resource.Splitting.Part> nonIncludedSplittingParts = new HashSet<Resource.Splitting.Part>(splitting.getParts());
        for (Node childNode: node.children) {
            symmetricDifferenceNodeWith(childNode, checkNode);
            nonIncludedSplittingParts.remove(childNode.splittingPart);
        }
        for (Resource.Splitting.Part nonIncludedSplittingPart: nonIncludedSplittingParts) {
            Node additionalNode = new Node();
            additionalNode.splittingPart = nonIncludedSplittingPart;
            additionalNode.children = new ArrayList<Node>();
            node.children.add(additionalNode);
            unionNodeWith(additionalNode, checkNode); // (yes, union)
        }
    }
    
    private void normalizeNode(Node node) {
        if (node.isWhole() || node.isEmpty()) {
            return;
        }
        
        boolean allWhole = true;
        Iterator<Node> nodeIterator = node.children.iterator();
        while (nodeIterator.hasNext()) {
            Node childNode = nodeIterator.next();
            normalizeNode(childNode);
            if (childNode.isEmpty()) {
                nodeIterator.remove();
            }
            if (!childNode.isWhole()) {
                allWhole = false;
            }
        }
        if (allWhole && !node.children.isEmpty()) {
            Resource.Splitting splitting = node.children.get(0).splittingPart.getSplitting();
            if (splitting.getParts().size() == node.children.size()) {
                node.children = null;
            }
        }
    }

    private void intersectOutNodeSplittingPart(Node node, Resource.Splitting.Part splittingPart) {
        Resource.Splitting splitting = splittingPart.getSplitting();

        if (node.isEmpty()) {
            return;
        }
        
        if (node.splittingPart != null) {
            if (node.splittingPart == splittingPart) {
                if (node.children != null && node.children.size() == 1) {
                    Node childNode = node.children.get(0);
                    node.splittingPart = childNode.splittingPart;
                    node.children = childNode.children;
                } else {
                    node.splittingPart = null;
                }
                return;
            } else if (node.splittingPart.getSplitting() == splitting) {
                node.makeEmpty();
                return;
            }
        }
        
        if (node.children == null || node.children.isEmpty()) {
            return;
        }
        
        Resource.Splitting childSplitting = node.children.get(0).splittingPart.getSplitting();
        if (childSplitting != splitting) {
            for (Node childNode: node.children) {
                intersectOutNodeSplittingPart(childNode, splittingPart);
            }
            return;
        }
        
        intersectOutNodeFamiliarSplittingPart(node, splittingPart);
    }
    
    private void intersectOutNodeFamiliarSplittingPart(Node node, Resource.Splitting.Part splittingPart) {
        boolean splittingPartFound = false;
        List<Node> foundChildren = null;
        Iterator<Node> childNodeIterator = node.children.iterator();
        while (childNodeIterator.hasNext()) {
            Node childNode = childNodeIterator.next();
            if (childNode.splittingPart == splittingPart) {
                foundChildren = childNode.children;
                splittingPartFound = true;
            } else {
                childNodeIterator.remove();
            }
        }
        if (splittingPartFound) {
            node.children = foundChildren;
        }
    }

    private Node copyNode(Node node) {
        Node newNode = new Node();
        newNode.splittingPart = node.splittingPart;
        if (node.children != null) {
            newNode.children = new ArrayList<Node>();
            for (Node childNode: node.children) {
                newNode.children.add(copyNode(childNode));
            }
        }
        return newNode;
    }
    
    
    private class Node {
        
        Resource.Splitting.Part splittingPart = null;
        
        List<Node> children = null;
        
        
        boolean isWhole() {
            return (children == null);
        }
        
        boolean isEmpty() {
            return (children != null && children.isEmpty());
        }
        
        void makeEmpty() {
            if (children == null) {
                children = new ArrayList<Node>();
            } else {
                children.clear();
            }
        }
        
        void makeWhole() {
            children = null;
        }
        
    }
    
    
    private class LeafIterable implements Iterable<Set<Resource.Splitting.Part>> {

        @Override
        public Iterator<Set<Resource.Splitting.Part>> iterator() {
            return new LeafIterator();
        }
        
    }
    
    
    public class LeafIterator implements Iterator<Set<Resource.Splitting.Part>> {

        private boolean hasNext;
        
        private Set<Resource.Splitting.Part> next;
        
        private LinkedList<Node> nodeStack = new LinkedList<Node>();
        
        private LinkedList<Iterator<Node>> levelIterators = new LinkedList<Iterator<Node>>();
        
        
        private LeafIterator() {
            List<Node> topLevelNodeList = new ArrayList<Node>();
            topLevelNodeList.add(rootNode);
            nodeStack.add(rootNode);
            levelIterators.add(topLevelNodeList.iterator());
            step();
        }
        
        
        @Override
        public boolean hasNext() {
            return hasNext;
        }

        @Override
        public Set<Resource.Splitting.Part> next() {
            if (!hasNext) {
                throw new NoSuchElementException();
            }
            Set<Resource.Splitting.Part> savedNext = this.next;
            step();
            return savedNext;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
        
        private void step() {
            while (!tryStep());
        }
        
        private boolean tryStep() {
            Iterator<Node> currentIterator;
            while (true) {
                if (levelIterators.isEmpty()) {
                    hasNext = false;
                    next = null;
                    return true;
                }
                
                currentIterator = levelIterators.getLast();
                if (currentIterator.hasNext()) {
                    break;
                }
                
                nodeStack.removeLast();
                levelIterators.removeLast();
            }
            
            Node currentNode = currentIterator.next();
            if (currentNode.isWhole()) {
                hasNext = true;
                Set<Resource.Splitting.Part> splittingParts = new HashSet<Resource.Splitting.Part>();
                for (Node node: nodeStack) {
                    if (node.splittingPart != null) {
                        splittingParts.add(node.splittingPart);
                    }
                }
                if (currentNode.splittingPart != null) {
                    splittingParts.add(currentNode.splittingPart);
                }
                next = splittingParts;
                return true;
            }
            
            nodeStack.add(currentNode);
            levelIterators.add(currentNode.children.iterator());
            
            return false;
        }
        
    }
    
}
