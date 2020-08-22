package hu.webarticum.aurora.core.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.assertj.core.api.ThrowableAssert;
import org.junit.Before;
import org.junit.Test;

public class ResourceSubsetTest {

    private ResourceSubset subsetXWhole;
    private ResourceSubset subsetXGroupA1;
    private ResourceSubset subsetXGroupA2;
    private ResourceSubset subsetXGroupA3;
    private ResourceSubset subsetXGroupB1;
    private ResourceSubset subsetXGroupB2;
    private ResourceSubset subsetXGroupB3;
    private ResourceSubset subsetXGroupB4;
    private ResourceSubset subsetXGroupC1;
    private ResourceSubset subsetXGroupC2;
    private ResourceSubset subsetXGroupC3;
    private ResourceSubset subsetXGroupD1;
    private ResourceSubset subsetXGroupD2;

    private ResourceSubset subsetYWhole;
    private ResourceSubset subsetYGroupA1;
    private ResourceSubset subsetYGroupA2;

    private ResourceSubset subsetZWhole;
    private ResourceSubset subsetZGroupA1;
    private ResourceSubset subsetZGroupA2;
    
    
    @Test
    public void testDifferentResources() {
        assertThatThrownBy(new ThrowableAssert.ThrowingCallable() { @Override public void call() throws Throwable {
            intersection(subsetXGroupA1, subsetYGroupA1);
        }}).isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(new ThrowableAssert.ThrowingCallable() { @Override public void call() throws Throwable {
            intersection(subsetYWhole, subsetZWhole);
        }}).isInstanceOf(IllegalArgumentException.class);
    }
    
    @Test
    public void testSimpleGroups() {
        assertThat(subsetYGroupA1).isNotEqualTo(subsetXGroupA1);
        assertThat(subsetZGroupA1).isNotEqualTo(subsetYGroupA1);

        assertThat(subsetYWhole.intersects(subsetZWhole)).isFalse();
        
        assertThat(subsetXGroupA1.intersects(subsetYGroupA1)).isFalse();
        assertThat(subsetYGroupA1.intersects(subsetZGroupA1)).isFalse();
        assertThat(subsetYGroupA2.contains(subsetZGroupA2)).isFalse();

        assertThat(subsetXGroupA1.intersects(subsetXGroupA1)).isTrue();
        assertThat(subsetXGroupA1.contains(subsetXGroupA1)).isTrue();

        assertThat(subsetXGroupA1.intersects(subsetXGroupA2)).isFalse();
    }
    
    @Test
    public void testSimpleOperations() {
        assertThat(negation(intersection(subsetXGroupA1, subsetXGroupA2)).isWhole()).isTrue();
        assertThat(negation(intersection(subsetXGroupA1, subsetXGroupA2)).equals(subsetXWhole)).isTrue();
        assertThat(negation(union(subsetXGroupA1, subsetXGroupA2)).equals(subsetXWhole)).isFalse();

        assertThat(negation(union(subsetXGroupA1, subsetXGroupA2)).intersects(subsetXGroupA3)).isTrue();
        assertThat(negation(union(subsetXGroupA1, subsetXGroupA2)).contains(subsetXGroupA3)).isTrue();
        assertThat(negation(union(subsetXGroupA1, subsetXGroupA2)).equals(subsetXGroupA3)).isTrue();
        
        assertThat(intersection(subsetXGroupA1, subsetXGroupA1)).isEqualTo(subsetXGroupA1);
        assertThat(intersection(subsetXGroupA1, subsetXGroupA1, subsetXGroupA1)).isEqualTo(subsetXGroupA1);
        assertThat(union(subsetXGroupA1)).isEqualTo(subsetXGroupA1);
        assertThat(union(subsetXGroupA1, subsetXGroupA1)).isEqualTo(subsetXGroupA1);
        assertThat(union(subsetXGroupA1, subsetXGroupA1, subsetXGroupA1)).isEqualTo(subsetXGroupA1);

        assertThat(intersection(subsetXWhole, subsetXGroupA1)).isEqualTo(subsetXGroupA1);
        assertThat(union(subsetXWhole, subsetXGroupA1)).isEqualTo(subsetXWhole);

        assertThat(negation(subsetXGroupD2)).isEqualTo(subsetXGroupD1);
        assertThat(difference(subsetXWhole, subsetXGroupD2)).isEqualTo(subsetXGroupD1);
        assertThat(symmetricDifference(subsetXWhole, subsetXGroupD2)).isEqualTo(subsetXGroupD1);
        
        assertThat(union(subsetXGroupA1, subsetXGroupA2).intersects(subsetXGroupA3)).isFalse();
        assertThat(union(subsetXGroupA1, subsetXGroupA2).contains(subsetXGroupA3)).isFalse();
        assertThat(union(subsetXGroupA1, subsetXGroupA2).equals(subsetXGroupA3)).isFalse();
        assertThat(union(subsetXGroupA1, subsetXGroupA2).intersects(subsetXGroupA2)).isTrue();
        assertThat(union(subsetXGroupA1, subsetXGroupA2).contains(subsetXGroupA2)).isTrue();
        assertThat(union(subsetXGroupA1, subsetXGroupA2).equals(subsetXGroupA2)).isFalse();

        assertThat(negation(union(subsetXGroupA1, subsetXGroupA2)).intersects(subsetXGroupA3)).isTrue();
        assertThat(negation(union(subsetXGroupA1, subsetXGroupA2)).contains(subsetXGroupA3)).isTrue();
        assertThat(negation(union(subsetXGroupA1, subsetXGroupA2)).equals(subsetXGroupA3)).isTrue();

        assertThat(union(subsetXGroupA1, subsetXGroupA2, subsetXGroupA3).intersects(subsetXWhole)).isTrue();
        assertThat(union(subsetXGroupA1, subsetXGroupA2, subsetXGroupA3).contains(subsetXWhole)).isTrue();
        assertThat(union(subsetXGroupA1, subsetXGroupA2, subsetXGroupA3).equals(subsetXWhole)).isTrue();
    }
    
    public void testUnion() {
        assertThat(union(subsetXGroupA1, subsetXGroupA2, subsetXGroupA3).intersects(
            union(subsetXGroupB1, subsetXGroupB2)
        )).isTrue();
        assertThat(union(subsetXGroupA1, subsetXGroupA2, subsetXGroupA3).contains(
            union(subsetXGroupB1, subsetXGroupB2)
        )).isTrue();
        assertThat(union(subsetXGroupA1, subsetXGroupA2, subsetXGroupA3).equals(
            union(subsetXGroupB1, subsetXGroupB2)
        )).isTrue();

        assertThat(union(subsetXGroupA1, subsetXGroupA2, subsetXGroupA3).intersects(
            union(subsetXGroupD1, subsetXGroupD2)
        )).isTrue();
        assertThat(union(subsetXGroupA1, subsetXGroupA2, subsetXGroupA3).contains(
            union(subsetXGroupD1, subsetXGroupD2)
        )).isTrue();
        assertThat(union(subsetXGroupA1, subsetXGroupA2, subsetXGroupA3).equals(
            union(subsetXGroupD1, subsetXGroupD2)
        )).isTrue();

        assertThat(union(subsetXGroupA1, subsetXGroupA2).intersects(union(subsetXGroupA2, subsetXGroupA1))).isTrue();
        assertThat(union(subsetXGroupA1, subsetXGroupA2).contains(union(subsetXGroupA2, subsetXGroupA1))).isTrue();
        assertThat(union(subsetXGroupA1, subsetXGroupA2).equals(union(subsetXGroupA2, subsetXGroupA1))).isTrue();

        assertThat(union(subsetXGroupA1, subsetXGroupB1).intersects(union(subsetXGroupB1, subsetXGroupA1))).isTrue();
        assertThat(union(subsetXGroupA1, subsetXGroupB1).contains(union(subsetXGroupB1, subsetXGroupA1))).isTrue();
        assertThat(union(subsetXGroupA1, subsetXGroupB1).equals(union(subsetXGroupB1, subsetXGroupA1))).isTrue();
    }
    
    public void testDifferentOperations() {
        assertThat(
            difference(subsetXWhole, subsetXGroupA1)
        ).isEqualTo(
            union(subsetXGroupA2, subsetXGroupA3)
        );
        assertThat(
            symmetricDifference(subsetXWhole, subsetXGroupA1)
        ).isEqualTo(
            union(subsetXGroupA2, subsetXGroupA3)
        );
        
        assertThat(union(subsetXGroupA1, subsetXGroupA2).intersects(
            symmetricDifference(subsetXGroupA1, subsetXGroupA2)
        )).isTrue();
        assertThat(union(subsetXGroupA1, subsetXGroupA2).contains(
            symmetricDifference(subsetXGroupA1, subsetXGroupA2)
        )).isTrue();
        assertThat(
            symmetricDifference(subsetXGroupA1, subsetXGroupA2)
        ).isEqualTo(
            union(subsetXGroupA1, subsetXGroupA2)
        );

        assertThat(union(subsetXGroupA1, subsetXGroupB1).intersects(
            symmetricDifference(subsetXGroupA1, subsetXGroupB1)
        )).isTrue();
        assertThat(union(subsetXGroupA1, subsetXGroupB1).contains(
            symmetricDifference(subsetXGroupA1, subsetXGroupB1)
        )).isTrue();
        assertThat(symmetricDifference(subsetXGroupA1, subsetXGroupB1)).isNotEqualTo(
            union(subsetXGroupA1, subsetXGroupB1)
        );
    }

    public void testComplexOperations() {
        assertThat(
            difference(subsetXGroupA1, subsetXGroupB1)
        ).isEqualTo(
            union(
                intersection(subsetXGroupA1, subsetXGroupB2),
                intersection(subsetXGroupA1, subsetXGroupB3),
                intersection(subsetXGroupA1, subsetXGroupB4)
            )
        );
        
        assertThat(
            symmetricDifference(subsetXGroupA1, subsetXGroupB1)
        ).isEqualTo(
            union(
                intersection(subsetXGroupA1, subsetXGroupB2),
                intersection(subsetXGroupA1, subsetXGroupB3),
                intersection(subsetXGroupA1, subsetXGroupB4),
                intersection(subsetXGroupA2, subsetXGroupB1),
                intersection(subsetXGroupA3, subsetXGroupB1)
            )
        );
        
        assertThat(union(subsetXGroupA1, subsetXGroupB1).intersects(union(
            symmetricDifference(subsetXGroupA1, subsetXGroupB1),
            intersection(subsetXGroupA1, subsetXGroupB1))
        )).isTrue();
        assertThat(union(subsetXGroupA1, subsetXGroupB1).contains(union(
            symmetricDifference(subsetXGroupA1, subsetXGroupB1),
            intersection(subsetXGroupA1, subsetXGroupB1))
        )).isTrue();
        assertThat(union(subsetXGroupA1, subsetXGroupB1).equals(union(
            symmetricDifference(subsetXGroupA1, subsetXGroupB1),
            intersection(subsetXGroupA1, subsetXGroupB1))
        )).isTrue();
    }
    
    @Test
    public void testMoreComplex1() {
        ResourceSubset firstSubset = difference(
            union(subsetXGroupA1, subsetXGroupB1, subsetXGroupB2),
            subsetXGroupC1
        );
        assertThat(firstSubset.isWhole()).isFalse();
        
        ResourceSubset secondSubset = union(subsetXGroupA1, subsetXGroupC2);
        assertThat(firstSubset.isWhole()).isFalse();
        
        assertThat(firstSubset.intersects(secondSubset)).isTrue();
        assertThat(firstSubset.contains(secondSubset)).isFalse();
        assertThat(firstSubset.equals(secondSubset)).isFalse();
    }

    @Test
    public void testMoreComplex2() {
        ResourceSubset firstSubset = union(subsetXGroupA1, subsetXGroupB1);
        assertThat(firstSubset.isWhole()).isFalse();
        
        ResourceSubset secondSubset = union(
            subsetXGroupA1,
            intersection(subsetXGroupA2, subsetXGroupB1),
            intersection(subsetXGroupA3, subsetXGroupB1)
        );
        assertThat(secondSubset.isWhole()).isFalse();

        assertThat(firstSubset.intersects(secondSubset)).isTrue();
        assertThat(firstSubset.contains(secondSubset)).isTrue();
        assertThat(firstSubset.equals(secondSubset)).isTrue();
    }
    
    @Test
    public void testMoreComplex3() {
        ResourceSubset firstSubset = intersection(
            union(subsetXGroupA1, subsetXGroupB1, subsetXGroupC1),
            subsetXGroupC2
        );
        assertThat(firstSubset.isWhole()).isFalse();
        
        ResourceSubset secondSubset = union(
            intersection(subsetXGroupA1, subsetXGroupC2),
            intersection(subsetXGroupA2, subsetXGroupB1, subsetXGroupC2),
            intersection(subsetXGroupA3, subsetXGroupB1, subsetXGroupC2)
        );
        assertThat(secondSubset.isWhole()).isFalse();

        assertThat(firstSubset.intersects(secondSubset)).isTrue();
        assertThat(firstSubset.contains(secondSubset)).isTrue();
        assertThat(firstSubset.equals(secondSubset)).isTrue();
    }

    @Test
    public void testMoreComplex4() {
        ResourceSubset firstSubset = symmetricDifference(
            intersection(union(subsetXGroupA1, subsetXGroupB1), subsetXGroupC2),
            subsetXGroupD1
        );
        assertThat(firstSubset.isWhole()).isFalse();

        ResourceSubset secondSubset = union(
            intersection(subsetXGroupA1, subsetXGroupC1, subsetXGroupD1),
            intersection(subsetXGroupA1, subsetXGroupC2, subsetXGroupD2),
            intersection(subsetXGroupA1, subsetXGroupC3, subsetXGroupD1),
            intersection(subsetXGroupA2, subsetXGroupB1, subsetXGroupC1, subsetXGroupD1),
            intersection(subsetXGroupA2, subsetXGroupB1, subsetXGroupC2, subsetXGroupD2),
            intersection(subsetXGroupA2, subsetXGroupB1, subsetXGroupC3, subsetXGroupD1),
            intersection(subsetXGroupA2, subsetXGroupB2, subsetXGroupD1),
            intersection(subsetXGroupA2, subsetXGroupB3, subsetXGroupD1),
            intersection(subsetXGroupA2, subsetXGroupB4, subsetXGroupD1),
            intersection(subsetXGroupA3, subsetXGroupB1, subsetXGroupC1, subsetXGroupD1),
            intersection(subsetXGroupA3, subsetXGroupB1, subsetXGroupC2, subsetXGroupD2),
            intersection(subsetXGroupA3, subsetXGroupB1, subsetXGroupC3, subsetXGroupD1),
            intersection(subsetXGroupA3, subsetXGroupB2, subsetXGroupD1),
            intersection(subsetXGroupA3, subsetXGroupB3, subsetXGroupD1),
            intersection(subsetXGroupA3, subsetXGroupB4, subsetXGroupD1)
        );
        assertThat(secondSubset.isWhole()).isFalse();

        assertThat(firstSubset.intersects(secondSubset)).isTrue();
        assertThat(firstSubset.contains(secondSubset)).isTrue();
        assertThat(firstSubset.equals(secondSubset)).isTrue();
    }

    @Test
    public void testMoreComplex5() {
        ResourceSubset firstSubset = symmetricDifference(
            intersection(union(subsetXGroupA1, subsetXGroupB1), subsetXGroupC2),
            subsetXGroupD1
        );
        assertThat(firstSubset.isWhole()).isFalse();

        ResourceSubset secondSubset = union(
            intersection(subsetXGroupA1, subsetXGroupC1, subsetXGroupD1),
            intersection(subsetXGroupA1, subsetXGroupC2, subsetXGroupD2),
            intersection(subsetXGroupA1, subsetXGroupC3, subsetXGroupD1),
            intersection(subsetXGroupA2, subsetXGroupB1, subsetXGroupC1, subsetXGroupD1),
            intersection(subsetXGroupA2, subsetXGroupB1, subsetXGroupC2, subsetXGroupD2),
            intersection(subsetXGroupA2, subsetXGroupB1, subsetXGroupC3, subsetXGroupD1),
            intersection(subsetXGroupA2, subsetXGroupB2, subsetXGroupD1),
            intersection(subsetXGroupA2, subsetXGroupB3, subsetXGroupD1),
            intersection(subsetXGroupA2, subsetXGroupB4, subsetXGroupD1),
            intersection(subsetXGroupA3, subsetXGroupB1, subsetXGroupC1, subsetXGroupD1),
            intersection(subsetXGroupA3, subsetXGroupB1, subsetXGroupC2, subsetXGroupD2),
            intersection(subsetXGroupA3, subsetXGroupB1, subsetXGroupC3, subsetXGroupD1),
            intersection(subsetXGroupA3, subsetXGroupB2, subsetXGroupD1),
            intersection(subsetXGroupA3, subsetXGroupB3, subsetXGroupD1)
            // intersection of (X_A3, X_B4, X_D1) removed
        );
        assertThat(secondSubset.isWhole()).isFalse();

        assertThat(firstSubset.intersects(secondSubset)).isTrue();
        assertThat(firstSubset.contains(secondSubset)).isTrue();
        assertThat(firstSubset.equals(secondSubset)).isFalse();
    }

    @Test
    public void testMoreComplex6() {
        ResourceSubset firstSubset = symmetricDifference(
            intersection(union(subsetXGroupA1, subsetXGroupB1), subsetXGroupC2),
            subsetXGroupD1
        );
        assertThat(firstSubset.isWhole()).isFalse();

        ResourceSubset secondSubset = union(
            intersection(subsetXGroupA1, subsetXGroupC1), // without X_D1
            intersection(subsetXGroupA1, subsetXGroupC2, subsetXGroupD2),
            intersection(subsetXGroupA1, subsetXGroupC3, subsetXGroupD1),
            intersection(subsetXGroupA2, subsetXGroupB1, subsetXGroupC1, subsetXGroupD1),
            intersection(subsetXGroupA2, subsetXGroupB1, subsetXGroupC2, subsetXGroupD2),
            intersection(subsetXGroupA2, subsetXGroupB1, subsetXGroupC3, subsetXGroupD1),
            intersection(subsetXGroupA2, subsetXGroupB2, subsetXGroupD1),
            intersection(subsetXGroupA2, subsetXGroupB3, subsetXGroupD1),
            intersection(subsetXGroupA2, subsetXGroupB4, subsetXGroupD1),
            intersection(subsetXGroupA3, subsetXGroupB1, subsetXGroupC1, subsetXGroupD1),
            intersection(subsetXGroupA3, subsetXGroupB1, subsetXGroupC2, subsetXGroupD2),
            intersection(subsetXGroupA3, subsetXGroupB1, subsetXGroupC3, subsetXGroupD1),
            intersection(subsetXGroupA3, subsetXGroupB2, subsetXGroupD1),
            intersection(subsetXGroupA3, subsetXGroupB3, subsetXGroupD1),
            intersection(subsetXGroupA3, subsetXGroupB4, subsetXGroupD1)
        );
        assertThat(secondSubset.isWhole()).isFalse();

        assertThat(firstSubset.intersects(secondSubset)).isTrue();
        assertThat(firstSubset.contains(secondSubset)).isFalse();
        assertThat(firstSubset.equals(secondSubset)).isFalse();
    }

    private ResourceSubset union(ResourceSubset... subsets) {
        return new ResourceSubset.Union(subsets);
    }

    private ResourceSubset intersection(ResourceSubset... subsets) {
        return new ResourceSubset.Intersection(subsets);
    }

    private ResourceSubset difference(ResourceSubset... subsets) {
        return new ResourceSubset.Difference(subsets);
    }

    private ResourceSubset symmetricDifference(ResourceSubset... subsets) {
        return new ResourceSubset.SymmetricDifference(subsets);
    }

    private ResourceSubset negation(ResourceSubset subset) {
        return new ResourceSubset.Inverse(subset);
    }

    @Before
    public void buildThings() {
        buildX();
        buildY();
        buildZ();
    }
    
    public void buildX() {
        Resource resourceX = new Resource("X");
        subsetXWhole = new ResourceSubset.Whole(resourceX);
        
        Resource.SplittingManager splittingManagerX = resourceX.getSplittingManager();
        
        Resource.Splitting splittingXA = resourceX.new Splitting("X/A");
        subsetXGroupA1 = new ResourceSubset.SplittingPart(splittingXA.addPart("X/A1"));
        subsetXGroupA2 = new ResourceSubset.SplittingPart(splittingXA.addPart("X/A2"));
        subsetXGroupA3 = new ResourceSubset.SplittingPart(splittingXA.addPart("X/A3"));
        splittingManagerX.add(splittingXA);
        
        Resource.Splitting splittingXB = resourceX.new Splitting("X/B");
        subsetXGroupB1 = new ResourceSubset.SplittingPart(splittingXB.addPart("X/B1"));
        subsetXGroupB2 = new ResourceSubset.SplittingPart(splittingXB.addPart("X/B2"));
        subsetXGroupB3 = new ResourceSubset.SplittingPart(splittingXB.addPart("X/B3"));
        subsetXGroupB4 = new ResourceSubset.SplittingPart(splittingXB.addPart("X/B4"));
        splittingManagerX.add(splittingXB);
        
        Resource.Splitting splittingXC = resourceX.new Splitting("X/C");
        subsetXGroupC1 = new ResourceSubset.SplittingPart(splittingXC.addPart("X/C1"));
        subsetXGroupC2 = new ResourceSubset.SplittingPart(splittingXC.addPart("X/C2"));
        subsetXGroupC3 = new ResourceSubset.SplittingPart(splittingXC.addPart("X/C3"));
        splittingManagerX.add(splittingXC);
        
        Resource.Splitting splittingXD = resourceX.new Splitting("X/D");
        subsetXGroupD1 = new ResourceSubset.SplittingPart(splittingXD.addPart("X/D1"));
        subsetXGroupD2 = new ResourceSubset.SplittingPart(splittingXD.addPart("X/D2"));
        splittingManagerX.add(splittingXD);
    }
    
    public void buildY() {
        Resource resourceY = new Resource("Y");
        subsetYWhole = new ResourceSubset.Whole(resourceY);
        
        Resource.SplittingManager splittingManagerY = resourceY.getSplittingManager();
        
        Resource.Splitting splittingYA = resourceY.new Splitting("Y/A");
        subsetYGroupA1 = new ResourceSubset.SplittingPart(splittingYA.addPart("Y/A1"));
        subsetYGroupA2 = new ResourceSubset.SplittingPart(splittingYA.addPart("Y/A2"));
        splittingManagerY.add(splittingYA);

    }
    
    public void buildZ() {
        Resource resourceZ = new Resource("Z");
        subsetZWhole = new ResourceSubset.Whole(resourceZ);
        
        Resource.SplittingManager splittingManagerZ = resourceZ.getSplittingManager();
        
        Resource.Splitting splittingZA = resourceZ.new Splitting("Z/A");
        subsetZGroupA1 = new ResourceSubset.SplittingPart(splittingZA.addPart("Z/A1"));
        subsetZGroupA2 = new ResourceSubset.SplittingPart(splittingZA.addPart("Z/A2"));
        splittingManagerZ.add(splittingZA);
        
    }
    
}
