package hu.webarticum.aurora.core.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
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
        assertThat(emptyTree).isEqualTo(emptyTree.copy());
        assertThat(emptyTree.getLeafIterable()).containsExactlyInAnyOrderElementsOf(emptyTree.copy().getLeafIterable());
        assertThat(emptyTree.isEmpty()).isTrue();
        assertThat(emptyTree.isWhole()).isFalse();
        assertThat(emptyTree.getLeafIterable()).isEmpty();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testWholeTree() {
        assertThat(wholeTree).isEqualTo(wholeTree.copy());
        assertThat(wholeTree.getLeafIterable()).containsExactlyInAnyOrderElementsOf(wholeTree.copy().getLeafIterable());
        assertThat(wholeTree.isEmpty()).isFalse();
        assertThat(wholeTree.isWhole()).isTrue();
        assertThat(wholeTree.getLeafIterable()).containsExactlyInAnyOrder(partSet());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testPart11Tree() {
        assertThat(part11Tree).isEqualTo(part11Tree.copy());
        assertThat(part11Tree.getLeafIterable()).containsExactlyInAnyOrderElementsOf(
            part11Tree.copy().getLeafIterable()
        );
        assertThat(part11Tree.isEmpty()).isFalse();
        assertThat(part11Tree.isWhole()).isFalse();
        assertThat(part11Tree.getLeafIterable()).containsExactlyInAnyOrder(partSet(splittingPart11));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testPart12Tree() {
        assertThat(part12Tree).isEqualTo(part12Tree.copy());
        assertThat(part12Tree.getLeafIterable()).containsExactlyInAnyOrderElementsOf(
            part12Tree.copy().getLeafIterable()
        );
        assertThat(part12Tree.isEmpty()).isFalse();
        assertThat(part12Tree.isWhole()).isFalse();
        assertThat(part12Tree.getLeafIterable()).containsExactly(partSet(splittingPart12));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testPart21Tree() {
        assertThat(part21Tree).isEqualTo(part21Tree.copy());
        assertThat(part21Tree.getLeafIterable()).containsExactlyInAnyOrderElementsOf(
            part21Tree.copy().getLeafIterable()
        );
        assertThat(part21Tree.isEmpty()).isFalse();
        assertThat(part21Tree.isWhole()).isFalse();
        assertThat(part21Tree.getLeafIterable()).containsExactlyInAnyOrder(partSet(splittingPart21));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testComplexTree1() {
        assertThat(complexTree1).isEqualTo(complexTree1.copy());
        assertThat(complexTree1.getLeafIterable()).containsExactlyInAnyOrderElementsOf(
            complexTree1.copy().getLeafIterable()
        );
        assertThat(complexTree1.isEmpty()).isFalse();
        assertThat(complexTree1.isWhole()).isFalse();
        assertThat(complexTree1.getLeafIterable()).containsExactlyInAnyOrder(partSet(splittingPart21, splittingPart31));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testComplexTree2() {
        assertThat(complexTree2).isEqualTo(complexTree2.copy());
        assertThat(complexTree2.getLeafIterable()).containsExactlyInAnyOrderElementsOf(
            complexTree2.copy().getLeafIterable()
        );
        assertThat(complexTree2.isEmpty()).isFalse();
        assertThat(complexTree2.isWhole()).isFalse();
        assertThat(complexTree2.getLeafIterable()).containsExactlyInAnyOrder(partSet(splittingPart22, splittingPart32));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testComplexTree3() {
        assertThat(complexTree3).isEqualTo(complexTree3.copy());
        assertThat(complexTree3.getLeafIterable()).containsExactlyInAnyOrderElementsOf(
            complexTree3.copy().getLeafIterable()
        );
        assertThat(complexTree3.isEmpty()).isFalse();
        assertThat(complexTree3.isWhole()).isFalse();
        assertThat(complexTree3.getLeafIterable()).containsExactlyInAnyOrder(
            partSet(splittingPart11, splittingPart31),
            partSet(splittingPart12, splittingPart21, splittingPart31),
            partSet(splittingPart13, splittingPart21, splittingPart31)
        );
    }

    @Test
    public void testIntersects() {
        assertThat(emptyTree.intersects(emptyTree)).isFalse();
        assertThat(emptyTree.intersects(wholeTree)).isFalse();
        assertThat(emptyTree.intersects(part11Tree)).isFalse();
        assertThat(emptyTree.intersects(part12Tree)).isFalse();
        assertThat(emptyTree.intersects(part21Tree)).isFalse();
        assertThat(emptyTree.intersects(complexTree1)).isFalse();
        assertThat(emptyTree.intersects(complexTree2)).isFalse();
        assertThat(emptyTree.intersects(complexTree3)).isFalse();
        
        assertThat(wholeTree.intersects(emptyTree)).isFalse();
        assertThat(wholeTree.intersects(wholeTree)).isTrue();
        assertThat(wholeTree.intersects(part11Tree)).isTrue();
        assertThat(wholeTree.intersects(part12Tree)).isTrue();
        assertThat(wholeTree.intersects(part21Tree)).isTrue();
        assertThat(wholeTree.intersects(complexTree1)).isTrue();
        assertThat(wholeTree.intersects(complexTree2)).isTrue();
        assertThat(wholeTree.intersects(complexTree3)).isTrue();
        
        assertThat(part11Tree.intersects(emptyTree)).isFalse();
        assertThat(part11Tree.intersects(wholeTree)).isTrue();
        assertThat(part11Tree.intersects(part11Tree)).isTrue();
        assertThat(part11Tree.intersects(part12Tree)).isFalse();
        assertThat(part11Tree.intersects(part21Tree)).isTrue();
        assertThat(part11Tree.intersects(complexTree1)).isTrue();
        assertThat(part11Tree.intersects(complexTree2)).isTrue();
        assertThat(part11Tree.intersects(complexTree3)).isTrue();
        
        assertThat(part12Tree.intersects(emptyTree)).isFalse();
        assertThat(part12Tree.intersects(wholeTree)).isTrue();
        assertThat(part12Tree.intersects(part11Tree)).isFalse();
        assertThat(part12Tree.intersects(part12Tree)).isTrue();
        assertThat(part12Tree.intersects(part21Tree)).isTrue();
        assertThat(part12Tree.intersects(complexTree1)).isTrue();
        assertThat(part12Tree.intersects(complexTree2)).isTrue();
        assertThat(part12Tree.intersects(complexTree3)).isTrue();
        
        assertThat(part21Tree.intersects(emptyTree)).isFalse();
        assertThat(part21Tree.intersects(wholeTree)).isTrue();
        assertThat(part21Tree.intersects(part11Tree)).isTrue();
        assertThat(part21Tree.intersects(part12Tree)).isTrue();
        assertThat(part21Tree.intersects(part21Tree)).isTrue();
        assertThat(part21Tree.intersects(complexTree1)).isTrue();
        assertThat(part21Tree.intersects(complexTree2)).isFalse();
        assertThat(part21Tree.intersects(complexTree3)).isTrue();

        assertThat(complexTree1.intersects(emptyTree)).isFalse();
        assertThat(complexTree1.intersects(wholeTree)).isTrue();
        assertThat(complexTree1.intersects(part11Tree)).isTrue();
        assertThat(complexTree1.intersects(part12Tree)).isTrue();
        assertThat(complexTree1.intersects(part21Tree)).isTrue();
        assertThat(complexTree1.intersects(complexTree1)).isTrue();
        assertThat(complexTree1.intersects(complexTree2)).isFalse();
        assertThat(complexTree1.intersects(complexTree3)).isTrue();

        assertThat(complexTree2.intersects(emptyTree)).isFalse();
        assertThat(complexTree2.intersects(wholeTree)).isTrue();
        assertThat(complexTree2.intersects(part11Tree)).isTrue();
        assertThat(complexTree2.intersects(part12Tree)).isTrue();
        assertThat(complexTree2.intersects(part21Tree)).isFalse();
        assertThat(complexTree2.intersects(complexTree1)).isFalse();
        assertThat(complexTree2.intersects(complexTree2)).isTrue();
        assertThat(complexTree2.intersects(complexTree3)).isFalse();

        assertThat(complexTree3.intersects(emptyTree)).isFalse();
        assertThat(complexTree3.intersects(wholeTree)).isTrue();
        assertThat(complexTree3.intersects(part11Tree)).isTrue();
        assertThat(complexTree3.intersects(part12Tree)).isTrue();
        assertThat(complexTree3.intersects(part21Tree)).isTrue();
        assertThat(complexTree3.intersects(complexTree1)).isTrue();
        assertThat(complexTree3.intersects(complexTree2)).isFalse();
        assertThat(complexTree3.intersects(complexTree3)).isTrue();
    }

    @Test
    public void testContains() {
        assertThat(emptyTree.contains(emptyTree)).isTrue();
        assertThat(emptyTree.contains(wholeTree)).isFalse();
        assertThat(emptyTree.contains(part11Tree)).isFalse();
        assertThat(emptyTree.contains(part12Tree)).isFalse();
        assertThat(emptyTree.contains(part21Tree)).isFalse();
        assertThat(emptyTree.contains(complexTree1)).isFalse();
        assertThat(emptyTree.contains(complexTree2)).isFalse();
        assertThat(emptyTree.contains(complexTree3)).isFalse();
        
        assertThat(wholeTree.contains(emptyTree)).isTrue();
        assertThat(wholeTree.contains(wholeTree)).isTrue();
        assertThat(wholeTree.contains(part11Tree)).isTrue();
        assertThat(wholeTree.contains(part12Tree)).isTrue();
        assertThat(wholeTree.contains(part21Tree)).isTrue();
        assertThat(wholeTree.contains(complexTree1)).isTrue();
        assertThat(wholeTree.contains(complexTree2)).isTrue();
        assertThat(wholeTree.contains(complexTree3)).isTrue();
        
        assertThat(part11Tree.contains(emptyTree)).isTrue();
        assertThat(part11Tree.contains(wholeTree)).isFalse();
        assertThat(part11Tree.contains(part11Tree)).isTrue();
        assertThat(part11Tree.contains(part12Tree)).isFalse();
        assertThat(part11Tree.contains(part21Tree)).isFalse();
        assertThat(part11Tree.contains(complexTree1)).isFalse();
        assertThat(part11Tree.contains(complexTree2)).isFalse();
        assertThat(part11Tree.contains(complexTree3)).isFalse();
        
        assertThat(part12Tree.contains(emptyTree)).isTrue();
        assertThat(part12Tree.contains(wholeTree)).isFalse();
        assertThat(part12Tree.contains(part11Tree)).isFalse();
        assertThat(part12Tree.contains(part12Tree)).isTrue();
        assertThat(part12Tree.contains(part21Tree)).isFalse();
        assertThat(part12Tree.contains(complexTree1)).isFalse();
        assertThat(part12Tree.contains(complexTree2)).isFalse();
        assertThat(part12Tree.contains(complexTree3)).isFalse();
        
        assertThat(part21Tree.contains(emptyTree)).isTrue();
        assertThat(part21Tree.contains(wholeTree)).isFalse();
        assertThat(part21Tree.contains(part11Tree)).isFalse();
        assertThat(part21Tree.contains(part12Tree)).isFalse();
        assertThat(part21Tree.contains(part21Tree)).isTrue();
        assertThat(part21Tree.contains(complexTree1)).isTrue();
        assertThat(part21Tree.contains(complexTree2)).isFalse();
        assertThat(part21Tree.contains(complexTree3)).isFalse();

        assertThat(complexTree1.contains(emptyTree)).isTrue();
        assertThat(complexTree1.contains(wholeTree)).isFalse();
        assertThat(complexTree1.contains(part11Tree)).isFalse();
        assertThat(complexTree1.contains(part12Tree)).isFalse();
        assertThat(complexTree1.contains(part21Tree)).isFalse();
        assertThat(complexTree1.contains(complexTree1)).isTrue();
        assertThat(complexTree1.contains(complexTree2)).isFalse();
        assertThat(complexTree1.contains(complexTree3)).isFalse();

        assertThat(complexTree2.contains(emptyTree)).isTrue();
        assertThat(complexTree2.contains(wholeTree)).isFalse();
        assertThat(complexTree2.contains(part11Tree)).isFalse();
        assertThat(complexTree2.contains(part12Tree)).isFalse();
        assertThat(complexTree2.contains(part21Tree)).isFalse();
        assertThat(complexTree2.contains(complexTree1)).isFalse();
        assertThat(complexTree2.contains(complexTree2)).isTrue();
        assertThat(complexTree2.contains(complexTree3)).isFalse();

        assertThat(complexTree3.contains(emptyTree)).isTrue();
        assertThat(complexTree3.contains(wholeTree)).isFalse();
        assertThat(complexTree3.contains(part11Tree)).isFalse();
        assertThat(complexTree3.contains(part12Tree)).isFalse();
        assertThat(complexTree3.contains(part21Tree)).isFalse();
        assertThat(complexTree3.contains(complexTree1)).isTrue();
        assertThat(complexTree3.contains(complexTree2)).isFalse();
        assertThat(complexTree3.contains(complexTree3)).isTrue();
    }

    @Test
    public void testNegate() {
        assertThat(negated(emptyTree).isWhole()).isTrue();
        assertThat(negated(wholeTree).isEmpty()).isTrue();
        assertThat(negated(part11Tree)).isEqualTo(union(part(splittingPart12), part(splittingPart13)));
        assertThat(negated(part12Tree)).isEqualTo(union(part(splittingPart11), part(splittingPart13)));
        assertThat(negated(part21Tree)).isEqualTo(part(splittingPart22));
        assertThat(negated(complexTree1)).isEqualTo(
            union(difference(part(splittingPart21), part(splittingPart31)), part(splittingPart22))
        );
        assertThat(negated(complexTree2)).isEqualTo(
            union(difference(part(splittingPart22), part(splittingPart32)), part(splittingPart21))
        );
        assertThat(negated(complexTree3)).isEqualTo(
            union(
                difference(whole(), part(splittingPart31)),
                difference(part(splittingPart22), part(splittingPart11))
            )
        );
    }

    @Test
    public void testUnion() {
        assertThat(union(emptyTree, emptyTree)).isEqualTo(empty());
        assertThat(union(emptyTree, wholeTree)).isEqualTo(whole());
        assertThat(union(part21Tree, part(splittingPart22))).isEqualTo(whole());
        assertThat(union(complexTree1, complexTree3)).isEqualTo(complexTree3);
        assertThat(union(complexTree2, complexTree3)).isEqualTo(
            union(
                intersection(part(splittingPart11), part(splittingPart21), part(splittingPart31)),
                intersection(part(splittingPart11), part(splittingPart22), part(splittingPart31)),
                intersection(part(splittingPart11), part(splittingPart22), part(splittingPart32)),
                intersection(part(splittingPart12), part(splittingPart21), part(splittingPart31)),
                intersection(part(splittingPart12), part(splittingPart22), part(splittingPart32)),
                intersection(part(splittingPart13), part(splittingPart21), part(splittingPart31)),
                intersection(part(splittingPart13), part(splittingPart22), part(splittingPart32))
            )
        );
    }

    @Test
    public void testIntersection() {
        assertThat(intersection(emptyTree, emptyTree)).isEqualTo(empty());
        assertThat(intersection(emptyTree, wholeTree)).isEqualTo(empty());
        assertThat(intersection(part12Tree, complexTree3)).isEqualTo(
            intersection(part(splittingPart12), part(splittingPart21), part(splittingPart31))
        );
    }

    @Test
    public void testDifference() {
        assertThat(difference(emptyTree, emptyTree)).isEqualTo(empty());
        assertThat(difference(emptyTree, wholeTree)).isEqualTo(empty());
        assertThat(difference(part11Tree, part12Tree)).isEqualTo(part11Tree);
        assertThat(difference(part11Tree, part21Tree)).isEqualTo(
            intersection(part11Tree, part(splittingPart22))
        );
        assertThat(difference(wholeTree, complexTree2)).isEqualTo(
            union(
                part(splittingPart21),
                intersection(part(splittingPart22), part(splittingPart31)),
                intersection(part(splittingPart22), part(splittingPart33))
            )
        );
    }
    
    @Test
    public void testSymmetricDifference() {
        assertThat(symmetricDifference(emptyTree, emptyTree)).isEqualTo(empty());
        assertThat(symmetricDifference(emptyTree, wholeTree)).isEqualTo(whole());
        assertThat(symmetricDifference(emptyTree, part11Tree)).isEqualTo(part11Tree);
        assertThat(symmetricDifference(emptyTree, complexTree3)).isEqualTo(complexTree3);
        assertThat(symmetricDifference(wholeTree, emptyTree)).isEqualTo(whole());
        assertThat(symmetricDifference(part11Tree, part12Tree)).isEqualTo(
            difference(union(part11Tree, part12Tree), intersection(part11Tree, part12Tree))
        );
        assertThat(symmetricDifference(complexTree3, complexTree2)).isEqualTo(
            union(complexTree3, complexTree2)
        );
        assertThat(symmetricDifference(complexTree3, complexTree1)).isEqualTo(
            intersection(part(splittingPart11), part(splittingPart22), part(splittingPart31))
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

    private static Set<Resource.Splitting.Part> partSet(Resource.Splitting.Part... parts) {
        return new HashSet<Resource.Splitting.Part>(Arrays.asList(parts));
    }
    
}
