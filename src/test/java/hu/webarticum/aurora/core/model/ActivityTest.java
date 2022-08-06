package hu.webarticum.aurora.core.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ActivityTest {
    
    private Tag subject1;
    private Tag subject2;
    
    private Resource class1;
    private Resource class2;
    private Resource teacher1;
    private Resource teacher2;
    private Resource multiResource;

    private Activity emptyActivity;
    private Activity simpleActivity;
    private Activity multiActivity;
    private Activity groupedActivity;
    private Activity groupedMultiActivity;

    
    @Test
    void testTags() {
        assertThat(emptyActivity.getTagManager().has(subject1)).isFalse();
        assertThat(emptyActivity.getTagManager().has(subject2)).isFalse();
        assertThat(emptyActivity.getTagManager().hasType(Tag.Type.SUBJECT)).isFalse();
        assertThat(emptyActivity.getTagManager().hasType(Tag.Type.LANGUAGE)).isFalse();

        assertThat(simpleActivity.getTagManager().has(subject1)).isTrue();
        assertThat(simpleActivity.getTagManager().has(subject2)).isFalse();
        assertThat(simpleActivity.getTagManager().hasType(Tag.Type.SUBJECT)).isTrue();
        assertThat(simpleActivity.getTagManager().hasType(Tag.Type.LANGUAGE)).isFalse();

        assertThat(multiActivity.getTagManager().has(subject1)).isTrue();
        assertThat(multiActivity.getTagManager().has(subject2)).isFalse();
        assertThat(multiActivity.getTagManager().hasType(Tag.Type.SUBJECT)).isTrue();
        assertThat(multiActivity.getTagManager().hasType(Tag.Type.LANGUAGE)).isFalse();

        assertThat(groupedActivity.getTagManager().has(subject1)).isFalse();
        assertThat(groupedActivity.getTagManager().has(subject2)).isTrue();
        assertThat(groupedActivity.getTagManager().hasType(Tag.Type.SUBJECT)).isTrue();
        assertThat(groupedActivity.getTagManager().hasType(Tag.Type.LANGUAGE)).isFalse();

        assertThat(groupedMultiActivity.getTagManager().has(subject1)).isFalse();
        assertThat(groupedMultiActivity.getTagManager().has(subject2)).isTrue();
        assertThat(groupedMultiActivity.getTagManager().hasType(Tag.Type.SUBJECT)).isTrue();
        assertThat(groupedMultiActivity.getTagManager().hasType(Tag.Type.LANGUAGE)).isFalse();
    }

    @Test
    void testResources() {
        assertThat(emptyActivity.getResourceManager().hasResource(class1)).isFalse();
        assertThat(emptyActivity.getResourceManager().hasResource(class2)).isFalse();
        assertThat(emptyActivity.getResourceManager().hasResource(teacher1)).isFalse();
        assertThat(emptyActivity.getResourceManager().hasResource(teacher2)).isFalse();
        assertThat(emptyActivity.getResourceManager().hasResource(multiResource)).isFalse();
        assertThat(emptyActivity.getResourceManager().hasResourceType(Resource.Type.CLASS)).isFalse();
        assertThat(emptyActivity.getResourceManager().hasResourceType(Resource.Type.PERSON)).isFalse();
        assertThat(emptyActivity.getResourceManager().hasResourceType(Resource.Type.OBJECT)).isFalse();
        assertThat(emptyActivity.getResourceManager().hasResourceType(Resource.Type.LOCALE)).isFalse();
        assertThat(emptyActivity.getResourceManager().getResources()).isEmpty();
        assertThat(emptyActivity.getResourceManager().getResourceSubsets()).isEmpty();

        assertThat(simpleActivity.getResourceManager().hasResource(class1)).isTrue();
        assertThat(simpleActivity.getResourceManager().hasResource(class2)).isFalse();
        assertThat(simpleActivity.getResourceManager().hasResource(teacher1)).isTrue();
        assertThat(simpleActivity.getResourceManager().hasResource(teacher2)).isFalse();
        assertThat(simpleActivity.getResourceManager().hasResource(multiResource)).isFalse();
        assertThat(simpleActivity.getResourceManager().hasResourceType(Resource.Type.CLASS)).isTrue();
        assertThat(simpleActivity.getResourceManager().hasResourceType(Resource.Type.PERSON)).isTrue();
        assertThat(simpleActivity.getResourceManager().hasResourceType(Resource.Type.OBJECT)).isFalse();
        assertThat(simpleActivity.getResourceManager().hasResourceType(Resource.Type.LOCALE)).isFalse();
        assertThat(simpleActivity.getResourceManager().getResources()).hasSize(2);
        assertThat(simpleActivity.getResourceManager().getResourceSubsets()).hasSize(2);

        assertThat(multiActivity.getResourceManager().hasResource(class1)).isTrue();
        assertThat(multiActivity.getResourceManager().hasResource(class2)).isFalse();
        assertThat(multiActivity.getResourceManager().hasResource(teacher1)).isTrue();
        assertThat(multiActivity.getResourceManager().hasResource(teacher2)).isFalse();
        assertThat(multiActivity.getResourceManager().hasResource(multiResource)).isTrue();
        assertThat(multiActivity.getResourceManager().hasResourceType(Resource.Type.CLASS)).isTrue();
        assertThat(multiActivity.getResourceManager().hasResourceType(Resource.Type.PERSON)).isTrue();
        assertThat(multiActivity.getResourceManager().hasResourceType(Resource.Type.OBJECT)).isTrue();
        assertThat(multiActivity.getResourceManager().hasResourceType(Resource.Type.LOCALE)).isFalse();
        assertThat(multiActivity.getResourceManager().getResources()).hasSize(3);
        assertThat(multiActivity.getResourceManager().getResourceSubsets()).hasSize(3);

        assertThat(groupedActivity.getResourceManager().hasResource(class1)).isFalse();
        assertThat(groupedActivity.getResourceManager().hasResource(class2)).isTrue();
        assertThat(groupedActivity.getResourceManager().hasResource(teacher1)).isTrue();
        assertThat(groupedActivity.getResourceManager().hasResource(teacher2)).isFalse();
        assertThat(groupedActivity.getResourceManager().hasResource(multiResource)).isFalse();
        assertThat(groupedActivity.getResourceManager().hasResourceType(Resource.Type.CLASS)).isTrue();
        assertThat(groupedActivity.getResourceManager().hasResourceType(Resource.Type.PERSON)).isTrue();
        assertThat(groupedActivity.getResourceManager().hasResourceType(Resource.Type.OBJECT)).isFalse();
        assertThat(groupedActivity.getResourceManager().hasResourceType(Resource.Type.LOCALE)).isFalse();
        assertThat(groupedActivity.getResourceManager().getResources()).hasSize(2);
        assertThat(groupedActivity.getResourceManager().getResourceSubsets()).hasSize(2);

        assertThat(groupedMultiActivity.getResourceManager().hasResource(class1)).isFalse();
        assertThat(groupedMultiActivity.getResourceManager().hasResource(class2)).isTrue();
        assertThat(groupedMultiActivity.getResourceManager().hasResource(teacher1)).isFalse();
        assertThat(groupedMultiActivity.getResourceManager().hasResource(teacher2)).isTrue();
        assertThat(groupedMultiActivity.getResourceManager().hasResource(multiResource)).isTrue();
        assertThat(groupedMultiActivity.getResourceManager().hasResourceType(Resource.Type.CLASS)).isTrue();
        assertThat(groupedMultiActivity.getResourceManager().hasResourceType(Resource.Type.PERSON)).isTrue();
        assertThat(groupedMultiActivity.getResourceManager().hasResourceType(Resource.Type.OBJECT)).isTrue();
        assertThat(groupedMultiActivity.getResourceManager().hasResourceType(Resource.Type.LOCALE)).isFalse();
        assertThat(groupedMultiActivity.getResourceManager().getResources()).hasSize(3);
        assertThat(groupedMultiActivity.getResourceManager().getResourceSubsets()).hasSize(4);
    }
    
    @Test
    void testConflictsWith() {
        assertThat(emptyActivity.conflictsWith(emptyActivity)).isFalse();
        assertThat(emptyActivity.conflictsWith(simpleActivity)).isFalse();
        assertThat(emptyActivity.conflictsWith(multiActivity)).isFalse();
        assertThat(emptyActivity.conflictsWith(groupedActivity)).isFalse();
        assertThat(emptyActivity.conflictsWith(groupedMultiActivity)).isFalse();

        assertThat(simpleActivity.conflictsWith(emptyActivity)).isFalse();
        assertThat(simpleActivity.conflictsWith(simpleActivity)).isTrue();
        assertThat(simpleActivity.conflictsWith(multiActivity)).isTrue();
        assertThat(simpleActivity.conflictsWith(groupedActivity)).isTrue();
        assertThat(simpleActivity.conflictsWith(groupedMultiActivity)).isFalse();

        assertThat(multiActivity.conflictsWith(emptyActivity)).isFalse();
        assertThat(multiActivity.conflictsWith(simpleActivity)).isTrue();
        assertThat(multiActivity.conflictsWith(multiActivity)).isTrue();
        assertThat(multiActivity.conflictsWith(groupedActivity)).isTrue();
        assertThat(multiActivity.conflictsWith(groupedMultiActivity)).isTrue();

        assertThat(groupedActivity.conflictsWith(emptyActivity)).isFalse();
        assertThat(groupedActivity.conflictsWith(simpleActivity)).isTrue();
        assertThat(groupedActivity.conflictsWith(multiActivity)).isTrue();
        assertThat(groupedActivity.conflictsWith(groupedActivity)).isTrue();
        assertThat(groupedActivity.conflictsWith(groupedMultiActivity)).isFalse();

        assertThat(groupedMultiActivity.conflictsWith(emptyActivity)).isFalse();
        assertThat(groupedMultiActivity.conflictsWith(simpleActivity)).isFalse();
        assertThat(groupedMultiActivity.conflictsWith(multiActivity)).isTrue();
        assertThat(groupedMultiActivity.conflictsWith(groupedActivity)).isFalse();
        assertThat(groupedMultiActivity.conflictsWith(groupedMultiActivity)).isTrue();
    }
    
    
    @BeforeEach
    public void buildThings() {
        buildTags();
        buildResources();
        buildEmptyActivity();
        buildSimpleActivity();
        buildMultiActivity();
        buildGroupedActivity();
        buildGroupedMultiActivity();
    }

    private void buildTags() {
        subject1 = new Tag(Tag.Type.SUBJECT, "Mathematics");
        subject2 = new Tag(Tag.Type.SUBJECT, "Literature");
    }

    private void buildResources() {
        class1 = new Resource(Resource.Type.CLASS, "Class1");
        addClassSplittings(class1);
        
        class2 = new Resource(Resource.Type.CLASS, "Class2");
        addClassSplittings(class2);

        teacher1 = new Resource(Resource.Type.PERSON, "X Y");
        teacher2 = new Resource(Resource.Type.PERSON, "N N");
        
        multiResource = new Resource(Resource.Type.OBJECT, "O");
        multiResource.setQuantity(2);
    }
    
    private void addClassSplittings(Resource resource) {
        Resource.SplittingManager splittingManager = resource.getSplittingManager();
        
        Resource.Splitting languageSplitting = splittingManager.add("Language");
        languageSplitting.addPart("German");
        languageSplitting.addPart("Spanish");
        
        Resource.Splitting optionalSplitting = splittingManager.add("Language");
        optionalSplitting.addPart("Physics");
        optionalSplitting.addPart("Geography");
        optionalSplitting.addPart("Biology");
    }

    private void buildEmptyActivity() {
        emptyActivity = new Activity("Empty activity");
    }

    private void buildSimpleActivity() {
        simpleActivity = new Activity("Simple activity");
        simpleActivity.getTagManager().add(subject1);
        simpleActivity.getResourceManager().add(class1);
        simpleActivity.getResourceManager().add(teacher1);
    }

    private void buildMultiActivity() {
        multiActivity = new Activity("Multi activity");
        multiActivity.getTagManager().add(subject1);
        multiActivity.getResourceManager().add(class1);
        multiActivity.getResourceManager().add(teacher1);
        multiActivity.getResourceManager().add(multiResource);
    }

    private void buildGroupedActivity() {
        groupedActivity = new Activity("Grouped activity");
        groupedActivity.getTagManager().add(subject2);
        groupedActivity.getResourceManager().add(new ResourceSubset.SplittingPart(
            class2.getSplittingManager().getSplittings().get(0).getParts().get(1)
        ));
        groupedActivity.getResourceManager().add(teacher1);
    }
    
    private void buildGroupedMultiActivity() {
        groupedMultiActivity = new Activity("Grouped multi activity");
        groupedMultiActivity.getTagManager().add(subject2);
        groupedMultiActivity.getResourceManager().add(new ResourceSubset.SplittingPart(
            class2.getSplittingManager().getSplittings().get(0).getParts().get(0)
        ));
        groupedMultiActivity.getResourceManager().add(teacher2);
        groupedMultiActivity.getResourceManager().add(multiResource);
        groupedMultiActivity.getResourceManager().add(multiResource);
    }
    
}
