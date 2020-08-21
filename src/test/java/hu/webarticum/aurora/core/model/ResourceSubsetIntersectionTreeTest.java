package hu.webarticum.aurora.core.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class ResourceSubsetIntersectionTreeTest {

    private Resource resource;

    private Resource.Splitting.Part splittingPart11;
    private Resource.Splitting.Part splittingPart12;
    private Resource.Splitting.Part splittingPart13;
    private Resource.Splitting.Part splittingPart21;
    private Resource.Splitting.Part splittingPart22;
    private Resource.Splitting.Part splittingPart31;
    private Resource.Splitting.Part splittingPart32;
    private Resource.Splitting.Part splittingPart33;

    private ResourceSubsetIntersectionTree emptyTree;
    private ResourceSubsetIntersectionTree wholeTree;
    private ResourceSubsetIntersectionTree part11Tree;
    private ResourceSubsetIntersectionTree part12Tree;
    private ResourceSubsetIntersectionTree part21Tree;
    private ResourceSubsetIntersectionTree complexTree1;
    private ResourceSubsetIntersectionTree complexTree2;
    private ResourceSubsetIntersectionTree complexTree3;
    

    @Test
    public void testEmptyTree() {
        assertTrue(emptyTree.equals(emptyTree.copy()));
        assertTrue(haveSameLeafs(emptyTree, emptyTree.copy()));
        assertTrue(emptyTree.isEmpty());
        assertFalse(emptyTree.isWhole());
        assertTrue(iterableToSet(emptyTree.getLeafIterable()).isEmpty());
    }

    @Test
    public void testWholeTree() {
        assertTrue(wholeTree.equals(wholeTree.copy()));
        assertTrue(haveSameLeafs(wholeTree, wholeTree.copy()));
        assertFalse(wholeTree.isEmpty());
        assertTrue(wholeTree.isWhole());
        Set<Set<Resource.Splitting.Part>> expectedLeafs = asSet(asSet(Resource.Splitting.Part.class));
        assertEquals(expectedLeafs, iterableToSet(wholeTree.getLeafIterable()));
    }

    @Test
    public void testPart11Tree() {
        assertTrue(part11Tree.equals(part11Tree.copy()));
        assertTrue(haveSameLeafs(part11Tree, part11Tree.copy()));
        assertFalse(part11Tree.isEmpty());
        assertFalse(part11Tree.isWhole());
        Set<Set<Resource.Splitting.Part>> expectedLeafs = asSet(asSet(splittingPart11));
        assertEquals(expectedLeafs, iterableToSet(part11Tree.getLeafIterable()));
    }

    @Test
    public void testPart12Tree() {
        assertTrue(part12Tree.equals(part12Tree.copy()));
        assertTrue(haveSameLeafs(part12Tree, part12Tree.copy()));
        assertFalse(part12Tree.isEmpty());
        assertFalse(part12Tree.isWhole());
        Set<Set<Resource.Splitting.Part>> expectedLeafs = asSet(asSet(splittingPart12));
        assertEquals(expectedLeafs, iterableToSet(part12Tree.getLeafIterable()));
    }

    @Test
    public void testPart21Tree() {
        assertTrue(part21Tree.equals(part21Tree.copy()));
        assertTrue(haveSameLeafs(part21Tree, part21Tree.copy()));
        assertFalse(part21Tree.isEmpty());
        assertFalse(part21Tree.isWhole());
        Set<Set<Resource.Splitting.Part>> expectedLeafs = asSet(asSet(splittingPart21));
        assertEquals(expectedLeafs, iterableToSet(part21Tree.getLeafIterable()));
    }
    
    @Test
    public void testComplexTree1() {
        assertTrue(complexTree1.equals(complexTree1.copy()));
        assertTrue(haveSameLeafs(complexTree1, complexTree1.copy()));
        assertFalse(complexTree1.isEmpty());
        assertFalse(complexTree1.isWhole());
        Set<Set<Resource.Splitting.Part>> expectedLeafs = asSet(
            asSet(splittingPart21, splittingPart31)
        );
        assertEquals(expectedLeafs, iterableToSet(complexTree1.getLeafIterable()));
    }

    @Test
    public void testComplexTree2() {
        assertTrue(complexTree2.equals(complexTree2.copy()));
        assertTrue(haveSameLeafs(complexTree2, complexTree2.copy()));
        assertFalse(complexTree2.isEmpty());
        assertFalse(complexTree2.isWhole());
        Set<Set<Resource.Splitting.Part>> expectedLeafs = asSet(
            asSet(splittingPart22, splittingPart32)
        );
        assertEquals(expectedLeafs, iterableToSet(complexTree2.getLeafIterable()));
    }

    @Test
    public void testComplexTree3() {
        assertTrue(complexTree3.equals(complexTree3.copy()));
        assertTrue(haveSameLeafs(complexTree3, complexTree3.copy()));
        assertFalse(complexTree3.isEmpty());
        assertFalse(complexTree3.isWhole());
        Set<Set<Resource.Splitting.Part>> expectedLeafs = asSet(
            asSet(splittingPart11, splittingPart31),
            asSet(splittingPart12, splittingPart21, splittingPart31),
            asSet(splittingPart13, splittingPart21, splittingPart31)
        );
        assertEquals(expectedLeafs, iterableToSet(complexTree3.getLeafIterable()));
    }

    @Test
    public void testIntersects() {
        assertFalse(emptyTree.intersects(emptyTree));
        assertFalse(emptyTree.intersects(wholeTree));
        assertFalse(emptyTree.intersects(part11Tree));
        assertFalse(emptyTree.intersects(part12Tree));
        assertFalse(emptyTree.intersects(part21Tree));
        assertFalse(emptyTree.intersects(complexTree1));
        assertFalse(emptyTree.intersects(complexTree2));
        assertFalse(emptyTree.intersects(complexTree3));
        
        assertFalse(wholeTree.intersects(emptyTree));
        assertTrue(wholeTree.intersects(wholeTree));
        assertTrue(wholeTree.intersects(part11Tree));
        assertTrue(wholeTree.intersects(part12Tree));
        assertTrue(wholeTree.intersects(part21Tree));
        assertTrue(wholeTree.intersects(complexTree1));
        assertTrue(wholeTree.intersects(complexTree2));
        assertTrue(wholeTree.intersects(complexTree3));
        
        assertFalse(part11Tree.intersects(emptyTree));
        assertTrue(part11Tree.intersects(wholeTree));
        assertTrue(part11Tree.intersects(part11Tree));
        assertFalse(part11Tree.intersects(part12Tree));
        assertTrue(part11Tree.intersects(part21Tree));
        assertTrue(part11Tree.intersects(complexTree1));
        assertTrue(part11Tree.intersects(complexTree2));
        assertTrue(part11Tree.intersects(complexTree3));
        
        assertFalse(part12Tree.intersects(emptyTree));
        assertTrue(part12Tree.intersects(wholeTree));
        assertFalse(part12Tree.intersects(part11Tree));
        assertTrue(part12Tree.intersects(part12Tree));
        assertTrue(part12Tree.intersects(part21Tree));
        assertTrue(part12Tree.intersects(complexTree1));
        assertTrue(part12Tree.intersects(complexTree2));
        assertTrue(part12Tree.intersects(complexTree3));
        
        assertFalse(part21Tree.intersects(emptyTree));
        assertTrue(part21Tree.intersects(wholeTree));
        assertTrue(part21Tree.intersects(part11Tree));
        assertTrue(part21Tree.intersects(part12Tree));
        assertTrue(part21Tree.intersects(part21Tree));
        assertTrue(part21Tree.intersects(complexTree1));
        assertFalse(part21Tree.intersects(complexTree2));
        assertTrue(part21Tree.intersects(complexTree3));

        assertFalse(complexTree1.intersects(emptyTree));
        assertTrue(complexTree1.intersects(wholeTree));
        assertTrue(complexTree1.intersects(part11Tree));
        assertTrue(complexTree1.intersects(part12Tree));
        assertTrue(complexTree1.intersects(part21Tree));
        assertTrue(complexTree1.intersects(complexTree1));
        assertFalse(complexTree1.intersects(complexTree2));
        assertTrue(complexTree1.intersects(complexTree3));

        assertFalse(complexTree2.intersects(emptyTree));
        assertTrue(complexTree2.intersects(wholeTree));
        assertTrue(complexTree2.intersects(part11Tree));
        assertTrue(complexTree2.intersects(part12Tree));
        assertFalse(complexTree2.intersects(part21Tree));
        assertFalse(complexTree2.intersects(complexTree1));
        assertTrue(complexTree2.intersects(complexTree2));
        assertFalse(complexTree2.intersects(complexTree3));

        assertFalse(complexTree3.intersects(emptyTree));
        assertTrue(complexTree3.intersects(wholeTree));
        assertTrue(complexTree3.intersects(part11Tree));
        assertTrue(complexTree3.intersects(part12Tree));
        assertTrue(complexTree3.intersects(part21Tree));
        assertTrue(complexTree3.intersects(complexTree1));
        assertFalse(complexTree3.intersects(complexTree2));
        assertTrue(complexTree3.intersects(complexTree3));
    }

    @Test
    public void testContains() {
        assertTrue(emptyTree.contains(emptyTree));
        assertFalse(emptyTree.contains(wholeTree));
        assertFalse(emptyTree.contains(part11Tree));
        assertFalse(emptyTree.contains(part12Tree));
        assertFalse(emptyTree.contains(part21Tree));
        assertFalse(emptyTree.contains(complexTree1));
        assertFalse(emptyTree.contains(complexTree2));
        assertFalse(emptyTree.contains(complexTree3));
        
        assertTrue(wholeTree.contains(emptyTree));
        assertTrue(wholeTree.contains(wholeTree));
        assertTrue(wholeTree.contains(part11Tree));
        assertTrue(wholeTree.contains(part12Tree));
        assertTrue(wholeTree.contains(part21Tree));
        assertTrue(wholeTree.contains(complexTree1));
        assertTrue(wholeTree.contains(complexTree2));
        assertTrue(wholeTree.contains(complexTree3));
        
        assertTrue(part11Tree.contains(emptyTree));
        assertFalse(part11Tree.contains(wholeTree));
        assertTrue(part11Tree.contains(part11Tree));
        assertFalse(part11Tree.contains(part12Tree));
        assertFalse(part11Tree.contains(part21Tree));
        assertFalse(part11Tree.contains(complexTree1));
        assertFalse(part11Tree.contains(complexTree2));
        assertFalse(part11Tree.contains(complexTree3));
        
        assertTrue(part12Tree.contains(emptyTree));
        assertFalse(part12Tree.contains(wholeTree));
        assertFalse(part12Tree.contains(part11Tree));
        assertTrue(part12Tree.contains(part12Tree));
        assertFalse(part12Tree.contains(part21Tree));
        assertFalse(part12Tree.contains(complexTree1));
        assertFalse(part12Tree.contains(complexTree2));
        assertFalse(part12Tree.contains(complexTree3));
        
        assertTrue(part21Tree.contains(emptyTree));
        assertFalse(part21Tree.contains(wholeTree));
        assertFalse(part21Tree.contains(part11Tree));
        assertFalse(part21Tree.contains(part12Tree));
        assertTrue(part21Tree.contains(part21Tree));
        assertTrue(part21Tree.contains(complexTree1));
        assertFalse(part21Tree.contains(complexTree2));
        assertFalse(part21Tree.contains(complexTree3));

        assertTrue(complexTree1.contains(emptyTree));
        assertFalse(complexTree1.contains(wholeTree));
        assertFalse(complexTree1.contains(part11Tree));
        assertFalse(complexTree1.contains(part12Tree));
        assertFalse(complexTree1.contains(part21Tree));
        assertTrue(complexTree1.contains(complexTree1));
        assertFalse(complexTree1.contains(complexTree2));
        assertFalse(complexTree1.contains(complexTree3));

        assertTrue(complexTree2.contains(emptyTree));
        assertFalse(complexTree2.contains(wholeTree));
        assertFalse(complexTree2.contains(part11Tree));
        assertFalse(complexTree2.contains(part12Tree));
        assertFalse(complexTree2.contains(part21Tree));
        assertFalse(complexTree2.contains(complexTree1));
        assertTrue(complexTree2.contains(complexTree2));
        assertFalse(complexTree2.contains(complexTree3));

        assertTrue(complexTree3.contains(emptyTree));
        assertFalse(complexTree3.contains(wholeTree));
        assertFalse(complexTree3.contains(part11Tree));
        assertFalse(complexTree3.contains(part12Tree));
        assertFalse(complexTree3.contains(part21Tree));
        assertTrue(complexTree3.contains(complexTree1));
        assertFalse(complexTree3.contains(complexTree2));
        assertTrue(complexTree3.contains(complexTree3));
    }

    @Test
    public void testNegate() {
        assertTrue(negated(emptyTree).isWhole());
        assertTrue(negated(wholeTree).isEmpty());
        assertEquals(union(part(splittingPart12),part(splittingPart13)), negated(part11Tree));
        assertEquals(
            union(part(splittingPart11), part(splittingPart13)),
            negated(part12Tree)
        );
        assertEquals(part(splittingPart22), negated(part21Tree));
        assertEquals(
            union(difference(part(splittingPart21), part(splittingPart31)), part(splittingPart22)),
            negated(complexTree1)
        );
        assertEquals(
            union(difference(part(splittingPart22), part(splittingPart32)), part(splittingPart21)),
            negated(complexTree2)
        );
        assertEquals(
            union(
                difference(whole(), part(splittingPart31)),
                difference(part(splittingPart22), part(splittingPart11))
            ),
            negated(complexTree3)
        );
    }

    @Test
    public void testUnion() {
        assertEquals(empty(), union(emptyTree, emptyTree));
        assertEquals(whole(), union(emptyTree, wholeTree));
        assertEquals(whole(), union(part21Tree, part(splittingPart22)));
        assertEquals(complexTree3, union(complexTree1, complexTree3));
        assertEquals(
            union(
                intersection(part(splittingPart11), part(splittingPart21), part(splittingPart31)),
                intersection(part(splittingPart11), part(splittingPart22), part(splittingPart31)),
                intersection(part(splittingPart11), part(splittingPart22), part(splittingPart32)),
                intersection(part(splittingPart12), part(splittingPart21), part(splittingPart31)),
                intersection(part(splittingPart12), part(splittingPart22), part(splittingPart32)),
                intersection(part(splittingPart13), part(splittingPart21), part(splittingPart31)),
                intersection(part(splittingPart13), part(splittingPart22), part(splittingPart32))
            ),
            union(complexTree2, complexTree3)
        );
    }

    @Test
    public void testIntersection() {
        assertEquals(empty(), intersection(emptyTree, emptyTree));
        assertEquals(empty(), intersection(emptyTree, wholeTree));
        assertEquals(
            intersection(part(splittingPart12), part(splittingPart21), part(splittingPart31)),
            intersection(part12Tree, complexTree3)
        );
    }

    @Test
    public void testDifference() {
        assertEquals(empty(), difference(emptyTree, emptyTree));
        assertEquals(empty(), difference(emptyTree, wholeTree));
        assertEquals(part11Tree, difference(part11Tree, part12Tree));
        assertEquals(
            intersection(part11Tree, part(splittingPart22)),
            difference(part11Tree, part21Tree)
        );
        assertEquals(
            union(
                part(splittingPart21),
                intersection(part(splittingPart22), part(splittingPart31)),
                intersection(part(splittingPart22), part(splittingPart33))
            ),
            difference(wholeTree, complexTree2)
        );
    }
    
    @Test
    public void testSymmetricDifference() {
        assertEquals(empty(), symmetricDifference(emptyTree, emptyTree));
        assertEquals(whole(), symmetricDifference(emptyTree, wholeTree));
        assertEquals(part11Tree, symmetricDifference(emptyTree, part11Tree));
        assertEquals(complexTree3, symmetricDifference(emptyTree, complexTree3));
        assertEquals(whole(), symmetricDifference(wholeTree, emptyTree));
        assertEquals(
            difference(union(part11Tree, part12Tree), intersection(part11Tree, part12Tree)),
            symmetricDifference(part11Tree, part12Tree)
        );
        assertEquals(
            union(complexTree3, complexTree2),
            symmetricDifference(complexTree3, complexTree2)
        );
        assertEquals(
            intersection(part(splittingPart11), part(splittingPart22), part(splittingPart31)),
            symmetricDifference(complexTree3, complexTree1)
        );
    }
    
    @Before
    public void buildThings() {
        resource = new Resource();
        buildSplittings();
        buildTrees();
    }
    
    private void buildSplittings() {
        Resource.SplittingManager splittingManager = resource.getSplittingManager();
        Resource.Splitting splitting1 = splittingManager.add("Splitting1");
        splittingPart11 = splitting1.addPart("Part1.1");
        splittingPart12 = splitting1.addPart("Part1.2");
        splittingPart13 = splitting1.addPart("Part1.3");
        Resource.Splitting splitting2 = splittingManager.add("Splitting2");
        splittingPart21 = splitting2.addPart("Part2.1");
        splittingPart22 = splitting2.addPart("Part2.2");
        Resource.Splitting splitting3 = splittingManager.add("Splitting3");
        splittingPart31 = splitting3.addPart("Part3.1");
        splittingPart32 = splitting3.addPart("Part3.2");
        splittingPart33 = splitting3.addPart("Part3.3");
    }
    
    private void buildTrees() {
        emptyTree = empty();
        wholeTree = whole();
        part11Tree = part(splittingPart11);
        part12Tree = part(splittingPart12);
        part21Tree = part(splittingPart21);
        buildComplexTree1();
        buildComplexTree2();
        buildComplexTree3();
    }

    private void buildComplexTree1() {
        complexTree1 = intersection(part(splittingPart21), part(splittingPart31));
    }

    private void buildComplexTree2() {
        complexTree2 = intersection(part(splittingPart22), part(splittingPart32));
    }

    private void buildComplexTree3() {
        complexTree3 = intersection(
            union(part(splittingPart11), part(splittingPart21)),
            part(splittingPart31)
        );
    }
    
    private boolean haveSameLeafs(
        ResourceSubsetIntersectionTree tree1,
        ResourceSubsetIntersectionTree tree2
    ) {
        return iterableToSet(tree1.getLeafIterable()).equals(iterableToSet(tree2.getLeafIterable()));
    }

    private static ResourceSubsetIntersectionTree empty() {
        return new ResourceSubsetIntersectionTree(false);
    }

    private static ResourceSubsetIntersectionTree whole() {
        return new ResourceSubsetIntersectionTree(true);
    }
    
    private static ResourceSubsetIntersectionTree part(Resource.Splitting.Part splittingPart) {
        return new ResourceSubsetIntersectionTree(splittingPart);
    }

    private static ResourceSubsetIntersectionTree negated(ResourceSubsetIntersectionTree tree) {
        ResourceSubsetIntersectionTree result = tree.copy();
        result.negate();
        return result;
    }

    private static ResourceSubsetIntersectionTree union(
        ResourceSubsetIntersectionTree baseTree,
        ResourceSubsetIntersectionTree... treesToMerge
    ) {
        ResourceSubsetIntersectionTree result = baseTree.copy();
        for (ResourceSubsetIntersectionTree treeToMerge : treesToMerge) {
            result.unionWith(treeToMerge);
        }
        return result;
    }

    private static ResourceSubsetIntersectionTree intersection(
        ResourceSubsetIntersectionTree baseTree,
        ResourceSubsetIntersectionTree... treesToMerge
    ) {
        ResourceSubsetIntersectionTree result = baseTree.copy();
        for (ResourceSubsetIntersectionTree treeToMerge : treesToMerge) {
            result.intersectionWith(treeToMerge);
        }
        return result;
    }

    private static ResourceSubsetIntersectionTree difference(
        ResourceSubsetIntersectionTree baseTree,
        ResourceSubsetIntersectionTree... treesToMerge
    ) {
        ResourceSubsetIntersectionTree result = baseTree.copy();
        for (ResourceSubsetIntersectionTree treeToMerge : treesToMerge) {
            result.differenceWith(treeToMerge);
        }
        return result;
    }

    private static ResourceSubsetIntersectionTree symmetricDifference(
        ResourceSubsetIntersectionTree baseTree,
        ResourceSubsetIntersectionTree... treesToMerge
    ) {
        ResourceSubsetIntersectionTree result = baseTree.copy();
        for (ResourceSubsetIntersectionTree treeToMerge : treesToMerge) {
            result.symmetricDifferenceWith(treeToMerge);
        }
        return result;
    }

    private static <T> Set<T> asSet(Class<T> clazz) {
        return new HashSet<T>();
    }
    
    @SafeVarargs
    private static <T> Set<T> asSet(T... items) {
        Set<T> result = new HashSet<T>();
        for (T item : items) {
            result.add(item);
        }
        return result;
    }
    
    private static <T> Set<T> iterableToSet(Iterable<T> iterable) {
        Set<T> result = new HashSet<T>();
        for (T item : iterable) {
            result.add(item);
        }
        return result;
    }
    
}
