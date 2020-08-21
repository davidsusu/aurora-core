package hu.webarticum.aurora.core.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class ActivityTest {
    
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
    public void testTags() {
        assertFalse(emptyActivity.getTagManager().has(subject1));
        assertFalse(emptyActivity.getTagManager().has(subject2));
        assertFalse(emptyActivity.getTagManager().hasType(Tag.Type.SUBJECT));
        assertFalse(emptyActivity.getTagManager().hasType(Tag.Type.LANGUAGE));

        assertTrue(simpleActivity.getTagManager().has(subject1));
        assertFalse(simpleActivity.getTagManager().has(subject2));
        assertTrue(simpleActivity.getTagManager().hasType(Tag.Type.SUBJECT));
        assertFalse(simpleActivity.getTagManager().hasType(Tag.Type.LANGUAGE));

        assertTrue(multiActivity.getTagManager().has(subject1));
        assertFalse(multiActivity.getTagManager().has(subject2));
        assertTrue(multiActivity.getTagManager().hasType(Tag.Type.SUBJECT));
        assertFalse(multiActivity.getTagManager().hasType(Tag.Type.LANGUAGE));

        assertFalse(groupedActivity.getTagManager().has(subject1));
        assertTrue(groupedActivity.getTagManager().has(subject2));
        assertTrue(groupedActivity.getTagManager().hasType(Tag.Type.SUBJECT));
        assertFalse(groupedActivity.getTagManager().hasType(Tag.Type.LANGUAGE));

        assertFalse(groupedMultiActivity.getTagManager().has(subject1));
        assertTrue(groupedMultiActivity.getTagManager().has(subject2));
        assertTrue(groupedMultiActivity.getTagManager().hasType(Tag.Type.SUBJECT));
        assertFalse(groupedMultiActivity.getTagManager().hasType(Tag.Type.LANGUAGE));
    }

    @Test
    public void testResources() {
        assertFalse(emptyActivity.getResourceManager().hasResource(class1));
        assertFalse(emptyActivity.getResourceManager().hasResource(class2));
        assertFalse(emptyActivity.getResourceManager().hasResource(teacher1));
        assertFalse(emptyActivity.getResourceManager().hasResource(teacher2));
        assertFalse(emptyActivity.getResourceManager().hasResource(multiResource));
        assertFalse(emptyActivity.getResourceManager().hasResourceType(Resource.Type.CLASS));
        assertFalse(emptyActivity.getResourceManager().hasResourceType(Resource.Type.PERSON));
        assertFalse(emptyActivity.getResourceManager().hasResourceType(Resource.Type.OBJECT));
        assertFalse(emptyActivity.getResourceManager().hasResourceType(Resource.Type.LOCALE));
        assertEquals(0, emptyActivity.getResourceManager().getResources().size());
        assertEquals(0, emptyActivity.getResourceManager().getResourceSubsets().size());

        assertTrue(simpleActivity.getResourceManager().hasResource(class1));
        assertFalse(simpleActivity.getResourceManager().hasResource(class2));
        assertTrue(simpleActivity.getResourceManager().hasResource(teacher1));
        assertFalse(simpleActivity.getResourceManager().hasResource(teacher2));
        assertFalse(simpleActivity.getResourceManager().hasResource(multiResource));
        assertTrue(simpleActivity.getResourceManager().hasResourceType(Resource.Type.CLASS));
        assertTrue(simpleActivity.getResourceManager().hasResourceType(Resource.Type.PERSON));
        assertFalse(simpleActivity.getResourceManager().hasResourceType(Resource.Type.OBJECT));
        assertFalse(simpleActivity.getResourceManager().hasResourceType(Resource.Type.LOCALE));
        assertEquals(2, simpleActivity.getResourceManager().getResources().size());
        assertEquals(2, simpleActivity.getResourceManager().getResourceSubsets().size());

        assertTrue(multiActivity.getResourceManager().hasResource(class1));
        assertFalse(multiActivity.getResourceManager().hasResource(class2));
        assertTrue(multiActivity.getResourceManager().hasResource(teacher1));
        assertFalse(multiActivity.getResourceManager().hasResource(teacher2));
        assertTrue(multiActivity.getResourceManager().hasResource(multiResource));
        assertTrue(multiActivity.getResourceManager().hasResourceType(Resource.Type.CLASS));
        assertTrue(multiActivity.getResourceManager().hasResourceType(Resource.Type.PERSON));
        assertTrue(multiActivity.getResourceManager().hasResourceType(Resource.Type.OBJECT));
        assertFalse(multiActivity.getResourceManager().hasResourceType(Resource.Type.LOCALE));
        assertEquals(3, multiActivity.getResourceManager().getResources().size());
        assertEquals(3, multiActivity.getResourceManager().getResourceSubsets().size());

        assertFalse(groupedActivity.getResourceManager().hasResource(class1));
        assertTrue(groupedActivity.getResourceManager().hasResource(class2));
        assertTrue(groupedActivity.getResourceManager().hasResource(teacher1));
        assertFalse(groupedActivity.getResourceManager().hasResource(teacher2));
        assertFalse(groupedActivity.getResourceManager().hasResource(multiResource));
        assertTrue(groupedActivity.getResourceManager().hasResourceType(Resource.Type.CLASS));
        assertTrue(groupedActivity.getResourceManager().hasResourceType(Resource.Type.PERSON));
        assertFalse(groupedActivity.getResourceManager().hasResourceType(Resource.Type.OBJECT));
        assertFalse(groupedActivity.getResourceManager().hasResourceType(Resource.Type.LOCALE));
        assertEquals(2, groupedActivity.getResourceManager().getResources().size());
        assertEquals(2, groupedActivity.getResourceManager().getResourceSubsets().size());

        assertFalse(groupedMultiActivity.getResourceManager().hasResource(class1));
        assertTrue(groupedMultiActivity.getResourceManager().hasResource(class2));
        assertFalse(groupedMultiActivity.getResourceManager().hasResource(teacher1));
        assertTrue(groupedMultiActivity.getResourceManager().hasResource(teacher2));
        assertTrue(groupedMultiActivity.getResourceManager().hasResource(multiResource));
        assertTrue(groupedMultiActivity.getResourceManager().hasResourceType(Resource.Type.CLASS));
        assertTrue(groupedMultiActivity.getResourceManager().hasResourceType(Resource.Type.PERSON));
        assertTrue(groupedMultiActivity.getResourceManager().hasResourceType(Resource.Type.OBJECT));
        assertFalse(groupedMultiActivity.getResourceManager().hasResourceType(Resource.Type.LOCALE));
        assertEquals(3, groupedMultiActivity.getResourceManager().getResources().size());
        assertEquals(4, groupedMultiActivity.getResourceManager().getResourceSubsets().size());
    }
    
    @Test
    public void testConflictsWith() {
        assertFalse(emptyActivity.conflictsWith(emptyActivity));
        assertFalse(emptyActivity.conflictsWith(simpleActivity));
        assertFalse(emptyActivity.conflictsWith(multiActivity));
        assertFalse(emptyActivity.conflictsWith(groupedActivity));
        assertFalse(emptyActivity.conflictsWith(groupedMultiActivity));

        assertFalse(simpleActivity.conflictsWith(emptyActivity));
        assertTrue(simpleActivity.conflictsWith(simpleActivity));
        assertTrue(simpleActivity.conflictsWith(multiActivity));
        assertTrue(simpleActivity.conflictsWith(groupedActivity));
        assertFalse(simpleActivity.conflictsWith(groupedMultiActivity));

        assertFalse(multiActivity.conflictsWith(emptyActivity));
        assertTrue(multiActivity.conflictsWith(simpleActivity));
        assertTrue(multiActivity.conflictsWith(multiActivity));
        assertTrue(multiActivity.conflictsWith(groupedActivity));
        assertTrue(multiActivity.conflictsWith(groupedMultiActivity));

        assertFalse(groupedActivity.conflictsWith(emptyActivity));
        assertTrue(groupedActivity.conflictsWith(simpleActivity));
        assertTrue(groupedActivity.conflictsWith(multiActivity));
        assertTrue(groupedActivity.conflictsWith(groupedActivity));
        assertFalse(groupedActivity.conflictsWith(groupedMultiActivity));

        assertFalse(groupedMultiActivity.conflictsWith(emptyActivity));
        assertFalse(groupedMultiActivity.conflictsWith(simpleActivity));
        assertTrue(groupedMultiActivity.conflictsWith(multiActivity));
        assertFalse(groupedMultiActivity.conflictsWith(groupedActivity));
        assertTrue(groupedMultiActivity.conflictsWith(groupedMultiActivity));
    }
    
    
    @Before
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
