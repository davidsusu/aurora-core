package hu.webarticum.aurora.core.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

public class ResourceSubsetListTest {

    private Resource resourceX;
    
    private ResourceSubset subsetX;
    private ResourceSubset subsetXGroupA1;
    private ResourceSubset subsetXGroupA2;
    private ResourceSubset subsetXGroupB1;
    private ResourceSubset subsetXGroupB2;
    
    
    @Test
    public void testSetListIntersectionCount() {
        assertThat(new ResourceSubsetList(subsetX).getMaximumCount(resourceX)).isEqualTo(1);
        assertThat(new ResourceSubsetList(subsetXGroupA1, subsetXGroupB1).getMaximumCount(resourceX)).isEqualTo(2);
        assertThat(new ResourceSubsetList(subsetXGroupA1, subsetXGroupA2).getMaximumCount(resourceX)).isEqualTo(1);
        assertThat(new ResourceSubsetList(subsetX, subsetXGroupA1, subsetXGroupA2, subsetXGroupB2).getMaximumCount(resourceX)).isEqualTo(3);
        assertThat(new ResourceSubsetList(
            subsetXGroupB1,
            new ResourceSubset.Difference(subsetXGroupA2, subsetXGroupB2),
            new ResourceSubset.Difference(subsetXGroupB2, subsetXGroupA2)
        ).getMaximumCount(resourceX)).isEqualTo(2);
    }
    
    @Before
    public void buildThings() {
        resourceX = new Resource("X");
        subsetX = new ResourceSubset.Whole(resourceX);
        
        Resource.SplittingManager splittingManagerX = resourceX.getSplittingManager();
        
        Resource.Splitting splittingXA = resourceX.new Splitting("X/A");
        subsetXGroupA1 = new ResourceSubset.SplittingPart(splittingXA.addPart("X/A1"));
        subsetXGroupA2 = new ResourceSubset.SplittingPart(splittingXA.addPart("X/A2"));
        splittingManagerX.add(splittingXA);
        
        Resource.Splitting splittingXB = resourceX.new Splitting("X/B");
        subsetXGroupB1 = new ResourceSubset.SplittingPart(splittingXB.addPart("X/B1"));
        subsetXGroupB2 = new ResourceSubset.SplittingPart(splittingXB.addPart("X/B2"));
        splittingManagerX.add(splittingXB);
    }

}
