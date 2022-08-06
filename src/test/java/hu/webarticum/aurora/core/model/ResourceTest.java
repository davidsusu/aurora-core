package hu.webarticum.aurora.core.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ResourceTest {

    @Test
    void testHasSplittings() {
        Resource simpleResource = new Resource();
        assertThat(simpleResource.hasSplittings()).isFalse();
        
        Resource resourceWithSplitting = new Resource();
        Resource.SplittingManager splittingManager = resourceWithSplitting.getSplittingManager();
        Resource.Splitting languageSplitting = splittingManager.add("Language");
        languageSplitting.addPart("German");
        languageSplitting.addPart("Spanish");
        assertThat(resourceWithSplitting.hasSplittings()).isTrue();
    }
    
}
