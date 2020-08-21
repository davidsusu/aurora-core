package hu.webarticum.aurora.core.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ResourceTest {

    @Test
    public void testHasSplittings() {
        Resource simpleResource = new Resource();
        assertFalse(simpleResource.hasSplittings());
        
        Resource resourceWithSplitting = new Resource();
        Resource.SplittingManager splittingManager = resourceWithSplitting.getSplittingManager();
        Resource.Splitting languageSplitting = splittingManager.add("Language");
        languageSplitting.addPart("German");
        languageSplitting.addPart("Spanish");
        assertTrue(resourceWithSplitting.hasSplittings());
    }
    
}
