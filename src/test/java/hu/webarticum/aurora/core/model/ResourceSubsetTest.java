package hu.webarticum.aurora.core.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
        try {
            intersection(subsetXGroupA1, subsetYGroupA1);
            fail();
        } catch (IllegalArgumentException e) {
        }

        try {
            intersection(subsetYWhole, subsetZWhole);
            fail();
        } catch (IllegalArgumentException e) {
        }
    }
    
    @Test
    public void testSimpleGroups() {
        assertNotEquals(subsetXGroupA1, subsetYGroupA1);
        assertNotEquals(subsetYGroupA1, subsetZGroupA1);

        assertFalse(subsetYWhole.intersects(subsetZWhole));
        
        assertFalse(subsetXGroupA1.intersects(subsetYGroupA1));
        assertFalse(subsetYGroupA1.intersects(subsetZGroupA1));
        assertFalse(subsetYGroupA2.contains(subsetZGroupA2));

        assertTrue(subsetXGroupA1.intersects(subsetXGroupA1));
        assertTrue(subsetXGroupA1.contains(subsetXGroupA1));

        assertFalse(subsetXGroupA1.intersects(subsetXGroupA2));
    }
    
    @Test
    public void testSimpleOperations() {
        assertTrue(negation(intersection(subsetXGroupA1, subsetXGroupA2)).isWhole());
        assertTrue(negation(intersection(subsetXGroupA1, subsetXGroupA2)).equals(subsetXWhole));
        assertFalse(negation(union(subsetXGroupA1, subsetXGroupA2)).equals(subsetXWhole));

        assertTrue(negation(union(subsetXGroupA1, subsetXGroupA2)).intersects(subsetXGroupA3));
        assertTrue(negation(union(subsetXGroupA1, subsetXGroupA2)).contains(subsetXGroupA3));
        assertTrue(negation(union(subsetXGroupA1, subsetXGroupA2)).equals(subsetXGroupA3));
        
        assertEquals(subsetXGroupA1, intersection(subsetXGroupA1, subsetXGroupA1));
        assertEquals(subsetXGroupA1, intersection(subsetXGroupA1, subsetXGroupA1, subsetXGroupA1));
        assertEquals(subsetXGroupA1, union(subsetXGroupA1));
        assertEquals(subsetXGroupA1, union(subsetXGroupA1, subsetXGroupA1));
        assertEquals(subsetXGroupA1, union(subsetXGroupA1, subsetXGroupA1, subsetXGroupA1));

        assertEquals(subsetXGroupA1, intersection(subsetXWhole, subsetXGroupA1));
        assertEquals(subsetXWhole, union(subsetXWhole, subsetXGroupA1));

        assertEquals(subsetXGroupD1, negation(subsetXGroupD2));
        assertEquals(subsetXGroupD1, difference(subsetXWhole, subsetXGroupD2));
        assertEquals(subsetXGroupD1, symmetricDifference(subsetXWhole, subsetXGroupD2));
        
        assertFalse(union(subsetXGroupA1, subsetXGroupA2).intersects(subsetXGroupA3));
        assertFalse(union(subsetXGroupA1, subsetXGroupA2).contains(subsetXGroupA3));
        assertFalse(union(subsetXGroupA1, subsetXGroupA2).equals(subsetXGroupA3));
        assertTrue(union(subsetXGroupA1, subsetXGroupA2).intersects(subsetXGroupA2));
        assertTrue(union(subsetXGroupA1, subsetXGroupA2).contains(subsetXGroupA2));
        assertFalse(union(subsetXGroupA1, subsetXGroupA2).equals(subsetXGroupA2));

        assertTrue(negation(union(subsetXGroupA1, subsetXGroupA2)).intersects(subsetXGroupA3));
        assertTrue(negation(union(subsetXGroupA1, subsetXGroupA2)).contains(subsetXGroupA3));
        assertTrue(negation(union(subsetXGroupA1, subsetXGroupA2)).equals(subsetXGroupA3));

        assertTrue(union(subsetXGroupA1, subsetXGroupA2, subsetXGroupA3).intersects(subsetXWhole));
        assertTrue(union(subsetXGroupA1, subsetXGroupA2, subsetXGroupA3).contains(subsetXWhole));
        assertTrue(union(subsetXGroupA1, subsetXGroupA2, subsetXGroupA3).equals(subsetXWhole));
    }
    
    public void testUnion() {
        assertTrue(union(subsetXGroupA1, subsetXGroupA2, subsetXGroupA3).intersects(
            union(subsetXGroupB1, subsetXGroupB2)
        ));
        assertTrue(union(subsetXGroupA1, subsetXGroupA2, subsetXGroupA3).contains(
            union(subsetXGroupB1, subsetXGroupB2)
        ));
        assertFalse(union(subsetXGroupA1, subsetXGroupA2, subsetXGroupA3).equals(
            union(subsetXGroupB1, subsetXGroupB2)
        ));

        assertTrue(union(subsetXGroupA1, subsetXGroupA2, subsetXGroupA3).intersects(
            union(subsetXGroupD1, subsetXGroupD2)
        ));
        assertTrue(union(subsetXGroupA1, subsetXGroupA2, subsetXGroupA3).contains(
            union(subsetXGroupD1, subsetXGroupD2)
        ));
        assertTrue(union(subsetXGroupA1, subsetXGroupA2, subsetXGroupA3).equals(
            union(subsetXGroupD1, subsetXGroupD2)
        ));

        assertTrue(union(subsetXGroupA1, subsetXGroupA2).intersects(union(subsetXGroupA2, subsetXGroupA1)));
        assertTrue(union(subsetXGroupA1, subsetXGroupA2).contains(union(subsetXGroupA2, subsetXGroupA1)));
        assertTrue(union(subsetXGroupA1, subsetXGroupA2).equals(union(subsetXGroupA2, subsetXGroupA1)));

        assertTrue(union(subsetXGroupA1, subsetXGroupB1).intersects(union(subsetXGroupB1, subsetXGroupA1)));
        assertTrue(union(subsetXGroupA1, subsetXGroupB1).contains(union(subsetXGroupB1, subsetXGroupA1)));
        assertTrue(union(subsetXGroupA1, subsetXGroupB1).equals(union(subsetXGroupB1, subsetXGroupA1)));
    }
    
    public void testDifferentOperations() {
        assertEquals(
            union(subsetXGroupA2, subsetXGroupA3),
            difference(subsetXWhole, subsetXGroupA1)
        );
        assertEquals(
            union(subsetXGroupA2, subsetXGroupA3),
            symmetricDifference(subsetXWhole, subsetXGroupA1)
        );
        
        assertTrue(union(subsetXGroupA1, subsetXGroupA2).intersects(
            symmetricDifference(subsetXGroupA1, subsetXGroupA2)
        ));
        assertTrue(union(subsetXGroupA1, subsetXGroupA2).contains(
            symmetricDifference(subsetXGroupA1, subsetXGroupA2)
        ));
        assertEquals(
            union(subsetXGroupA1, subsetXGroupA2),
            symmetricDifference(subsetXGroupA1, subsetXGroupA2)
        );

        assertTrue(union(subsetXGroupA1, subsetXGroupB1).intersects(
            symmetricDifference(subsetXGroupA1, subsetXGroupB1)
        ));
        assertTrue(union(subsetXGroupA1, subsetXGroupB1).contains(
            symmetricDifference(subsetXGroupA1, subsetXGroupB1)
        ));
        assertNotEquals(
            union(subsetXGroupA1, subsetXGroupB1),
            symmetricDifference(subsetXGroupA1, subsetXGroupB1)
        );
    }

    public void testComplexOperations() {
        assertEquals(union(
            intersection(subsetXGroupA1, subsetXGroupB2),
            intersection(subsetXGroupA1, subsetXGroupB3),
            intersection(subsetXGroupA1, subsetXGroupB4)
        ), difference(subsetXGroupA1, subsetXGroupB1));
        
        assertEquals(union(
            intersection(subsetXGroupA1, subsetXGroupB2),
            intersection(subsetXGroupA1, subsetXGroupB3),
            intersection(subsetXGroupA1, subsetXGroupB4),
            intersection(subsetXGroupA2, subsetXGroupB1),
            intersection(subsetXGroupA3, subsetXGroupB1)
        ), symmetricDifference(subsetXGroupA1, subsetXGroupB1));
        
        assertTrue(union(subsetXGroupA1, subsetXGroupB1).intersects(union(
            symmetricDifference(subsetXGroupA1, subsetXGroupB1),
            intersection(subsetXGroupA1, subsetXGroupB1))
        ));
        assertTrue(union(subsetXGroupA1, subsetXGroupB1).contains(union(
            symmetricDifference(subsetXGroupA1, subsetXGroupB1),
            intersection(subsetXGroupA1, subsetXGroupB1))
        ));
        assertTrue(union(subsetXGroupA1, subsetXGroupB1).equals(union(
            symmetricDifference(subsetXGroupA1, subsetXGroupB1),
            intersection(subsetXGroupA1, subsetXGroupB1))
        ));
    }
    
    @Test
    public void testMoreComplex1() {
        ResourceSubset firstSubset = difference(
            union(subsetXGroupA1, subsetXGroupB1, subsetXGroupB2),
            subsetXGroupC1
        );
        assertFalse(firstSubset.isWhole());
        
        ResourceSubset secondSubset = union(subsetXGroupA1, subsetXGroupC2);
        assertFalse(firstSubset.isWhole());
        
        assertTrue(firstSubset.intersects(secondSubset));
        assertFalse(firstSubset.contains(secondSubset));
        assertFalse(firstSubset.equals(secondSubset));
    }

    @Test
    public void testMoreComplex2() {
        ResourceSubset firstSubset = union(subsetXGroupA1, subsetXGroupB1);
        assertFalse(firstSubset.isWhole());
        
        ResourceSubset secondSubset = union(
            subsetXGroupA1,
            intersection(subsetXGroupA2, subsetXGroupB1),
            intersection(subsetXGroupA3, subsetXGroupB1)
        );
        assertFalse(secondSubset.isWhole());

        assertTrue(firstSubset.intersects(secondSubset));
        assertTrue(firstSubset.contains(secondSubset));
        assertTrue(firstSubset.equals(secondSubset));
    }
    
    @Test
    public void testMoreComplex3() {
        ResourceSubset firstSubset = intersection(
            union(subsetXGroupA1, subsetXGroupB1, subsetXGroupC1),
            subsetXGroupC2
        );
        assertFalse(firstSubset.isWhole());
        
        ResourceSubset secondSubset = union(
            intersection(subsetXGroupA1, subsetXGroupC2),
            intersection(subsetXGroupA2, subsetXGroupB1, subsetXGroupC2),
            intersection(subsetXGroupA3, subsetXGroupB1, subsetXGroupC2)
        );
        assertFalse(secondSubset.isWhole());

        assertTrue(firstSubset.intersects(secondSubset));
        assertTrue(firstSubset.contains(secondSubset));
        assertTrue(firstSubset.equals(secondSubset));
    }

    @Test
    public void testMoreComplex4() {
        ResourceSubset firstSubset = symmetricDifference(
            intersection(union(subsetXGroupA1, subsetXGroupB1), subsetXGroupC2),
            subsetXGroupD1
        );
        assertFalse(firstSubset.isWhole());

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
        assertFalse(secondSubset.isWhole());

        assertTrue(firstSubset.intersects(secondSubset));
        assertTrue(firstSubset.contains(secondSubset));
        assertTrue(firstSubset.equals(secondSubset));
    }

    @Test
    public void testMoreComplex5() {
        ResourceSubset firstSubset = symmetricDifference(
            intersection(union(subsetXGroupA1, subsetXGroupB1), subsetXGroupC2),
            subsetXGroupD1
        );
        assertFalse(firstSubset.isWhole());

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
        assertFalse(secondSubset.isWhole());

        assertTrue(firstSubset.intersects(secondSubset));
        assertTrue(firstSubset.contains(secondSubset));
        assertFalse(firstSubset.equals(secondSubset));
    }

    @Test
    public void testMoreComplex6() {
        ResourceSubset firstSubset = symmetricDifference(
            intersection(union(subsetXGroupA1, subsetXGroupB1), subsetXGroupC2),
            subsetXGroupD1
        );
        assertFalse(firstSubset.isWhole());

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
        assertFalse(secondSubset.isWhole());

        assertTrue(firstSubset.intersects(secondSubset));
        assertFalse(firstSubset.contains(secondSubset));
        assertFalse(firstSubset.equals(secondSubset));
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
