package hu.webarticum.aurora.core.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class ActivityListTest {

    private Tag subject1;
    private Tag subject2;
    private Tag language;
    
    private Resource class1;
    private Resource class2;
    private Resource teacher1;
    private Resource teacher2;
    private Resource multiResource;

    private ActivityList emptyActivityList;
    private ActivityList normalActivityList1;
    private ActivityList normalActivityList2;
    private ActivityList normalActivityList3;
    private ActivityList conflictingActivityList1;
    private ActivityList conflictingActivityList2;
    
    @Test
    public void testCopy() {
        assertNotSame(emptyActivityList, emptyActivityList.copy());
        assertEquals(emptyActivityList, emptyActivityList.copy());

        assertNotSame(normalActivityList1, normalActivityList1.copy());
        assertEquals(normalActivityList1, normalActivityList1.copy());

        assertNotSame(normalActivityList2, normalActivityList2.copy());
        assertEquals(normalActivityList2, normalActivityList2.copy());

        assertNotSame(normalActivityList3, normalActivityList3.copy());
        assertEquals(normalActivityList3, normalActivityList3.copy());

        assertNotSame(conflictingActivityList1, conflictingActivityList1.copy());
        assertEquals(conflictingActivityList1, conflictingActivityList1.copy());

        assertNotSame(conflictingActivityList2, conflictingActivityList2.copy());
        assertEquals(conflictingActivityList2, conflictingActivityList2.copy());
    }
    
    @Test
    public void testTags() {
        assertEquals(0, emptyActivityList.getTags().size());
        assertEquals(0, emptyActivityList.getTags(Tag.Type.SUBJECT).size());

        assertEquals(1, normalActivityList1.getTags().size());
        assertEquals(1, normalActivityList1.getTags(Tag.Type.SUBJECT).size());

        assertEquals(2, normalActivityList2.getTags().size());
        assertEquals(1, normalActivityList2.getTags(Tag.Type.SUBJECT).size());

        assertEquals(1, normalActivityList3.getTags().size());
        assertEquals(1, normalActivityList3.getTags(Tag.Type.SUBJECT).size());

        assertEquals(1, conflictingActivityList1.getTags().size());
        assertEquals(1, conflictingActivityList1.getTags(Tag.Type.SUBJECT).size());

        assertEquals(3, conflictingActivityList2.getTags().size());
        assertEquals(2, conflictingActivityList2.getTags(Tag.Type.SUBJECT).size());
    }
    
    @Test
    public void testResources() {
        assertEquals(0, emptyActivityList.getResources().size());
        assertEquals(0, emptyActivityList.getResources(Resource.Type.CLASS).size());
        assertEquals(0, emptyActivityList.getResources(Resource.Type.PERSON).size());
        assertEquals(0, emptyActivityList.getResources(Resource.Type.OBJECT).size());
        assertEquals(0, emptyActivityList.getResourceSubsets().size());
        assertEquals(0, emptyActivityList.getResourceSubsets(Resource.Type.CLASS).size());
        assertEquals(0, emptyActivityList.getResourceSubsets(Resource.Type.PERSON).size());
        assertEquals(0, emptyActivityList.getResourceSubsets(Resource.Type.OBJECT).size());

        assertEquals(4, normalActivityList1.getResources().size());
        assertEquals(2, normalActivityList1.getResources(Resource.Type.CLASS).size());
        assertEquals(2, normalActivityList1.getResources(Resource.Type.PERSON).size());
        assertEquals(0, normalActivityList1.getResources(Resource.Type.OBJECT).size());
        assertEquals(4, normalActivityList1.getResourceSubsets().size());
        assertEquals(2, normalActivityList1.getResourceSubsets(Resource.Type.CLASS).size());
        assertEquals(2, normalActivityList1.getResourceSubsets(Resource.Type.PERSON).size());
        assertEquals(0, normalActivityList1.getResourceSubsets(Resource.Type.OBJECT).size());

        assertEquals(3, normalActivityList2.getResources().size());
        assertEquals(1, normalActivityList2.getResources(Resource.Type.CLASS).size());
        assertEquals(1, normalActivityList2.getResources(Resource.Type.PERSON).size());
        assertEquals(1, normalActivityList2.getResources(Resource.Type.OBJECT).size());
        assertEquals(3, normalActivityList2.getResourceSubsets().size());
        assertEquals(1, normalActivityList2.getResourceSubsets(Resource.Type.CLASS).size());
        assertEquals(1, normalActivityList2.getResourceSubsets(Resource.Type.PERSON).size());
        assertEquals(1, normalActivityList2.getResourceSubsets(Resource.Type.OBJECT).size());

        assertEquals(3, normalActivityList3.getResources().size());
        assertEquals(1, normalActivityList3.getResources(Resource.Type.CLASS).size());
        assertEquals(1, normalActivityList3.getResources(Resource.Type.PERSON).size());
        assertEquals(1, normalActivityList3.getResources(Resource.Type.OBJECT).size());
        assertEquals(4, normalActivityList3.getResourceSubsets().size());
        assertEquals(2, normalActivityList3.getResourceSubsets(Resource.Type.CLASS).size());
        assertEquals(1, normalActivityList3.getResourceSubsets(Resource.Type.PERSON).size());
        assertEquals(1, normalActivityList3.getResourceSubsets(Resource.Type.OBJECT).size());

        assertEquals(1, conflictingActivityList1.getResources().size());
        assertEquals(0, conflictingActivityList1.getResources(Resource.Type.CLASS).size());
        assertEquals(1, conflictingActivityList1.getResources(Resource.Type.PERSON).size());
        assertEquals(0, conflictingActivityList1.getResources(Resource.Type.OBJECT).size());
        assertEquals(2, conflictingActivityList1.getResourceSubsets().size());
        assertEquals(0, conflictingActivityList1.getResourceSubsets(Resource.Type.CLASS).size());
        assertEquals(2, conflictingActivityList1.getResourceSubsets(Resource.Type.PERSON).size());
        assertEquals(0, conflictingActivityList1.getResourceSubsets(Resource.Type.OBJECT).size());

        assertEquals(4, conflictingActivityList2.getResources().size());
        assertEquals(2, conflictingActivityList2.getResources(Resource.Type.CLASS).size());
        assertEquals(1, conflictingActivityList2.getResources(Resource.Type.PERSON).size());
        assertEquals(1, conflictingActivityList2.getResources(Resource.Type.OBJECT).size());
        assertEquals(5, conflictingActivityList2.getResourceSubsets().size());
        assertEquals(2, conflictingActivityList2.getResourceSubsets(Resource.Type.CLASS).size());
        assertEquals(2, conflictingActivityList2.getResourceSubsets(Resource.Type.PERSON).size());
        assertEquals(1, conflictingActivityList2.getResourceSubsets(Resource.Type.OBJECT).size());
    }
    
    @Test
    public void testHasConflicts() {
        assertFalse(emptyActivityList.hasConflicts());
        assertFalse(normalActivityList1.hasConflicts());
        assertFalse(normalActivityList2.hasConflicts());
        assertFalse(normalActivityList3.hasConflicts());
        assertTrue(conflictingActivityList1.hasConflicts());
        assertTrue(conflictingActivityList2.hasConflicts());
    }
    
    @Test
    public void testConflictsWith() {
        assertFalse(emptyActivityList.conflictsWith(emptyActivityList));
        assertFalse(emptyActivityList.conflictsWith(normalActivityList1));
        assertFalse(emptyActivityList.conflictsWith(normalActivityList2));
        assertFalse(emptyActivityList.conflictsWith(normalActivityList3));
        assertFalse(emptyActivityList.conflictsWith(conflictingActivityList1));
        assertFalse(emptyActivityList.conflictsWith(conflictingActivityList2));

        assertFalse(normalActivityList1.conflictsWith(emptyActivityList));
        assertTrue(normalActivityList1.conflictsWith(normalActivityList1));
        assertTrue(normalActivityList1.conflictsWith(normalActivityList2));
        assertTrue(normalActivityList1.conflictsWith(normalActivityList3));
        assertTrue(normalActivityList1.conflictsWith(conflictingActivityList1));
        assertTrue(normalActivityList1.conflictsWith(conflictingActivityList2));

        assertFalse(normalActivityList2.conflictsWith(emptyActivityList));
        assertTrue(normalActivityList2.conflictsWith(normalActivityList1));
        assertTrue(normalActivityList2.conflictsWith(normalActivityList2));
        assertFalse(normalActivityList2.conflictsWith(normalActivityList3));
        assertFalse(normalActivityList2.conflictsWith(conflictingActivityList1));
        assertTrue(normalActivityList2.conflictsWith(conflictingActivityList2));

        assertFalse(normalActivityList3.conflictsWith(emptyActivityList));
        assertTrue(normalActivityList3.conflictsWith(normalActivityList1));
        assertFalse(normalActivityList3.conflictsWith(normalActivityList2));
        assertTrue(normalActivityList3.conflictsWith(normalActivityList3));
        assertTrue(normalActivityList3.conflictsWith(conflictingActivityList1));
        assertTrue(normalActivityList3.conflictsWith(conflictingActivityList2));

        assertFalse(conflictingActivityList1.conflictsWith(emptyActivityList));
        assertTrue(conflictingActivityList1.conflictsWith(normalActivityList1));
        assertFalse(conflictingActivityList1.conflictsWith(normalActivityList2));
        assertTrue(conflictingActivityList1.conflictsWith(normalActivityList3));
        assertTrue(conflictingActivityList1.conflictsWith(conflictingActivityList1));
        assertFalse(conflictingActivityList1.conflictsWith(conflictingActivityList2));

        assertFalse(conflictingActivityList2.conflictsWith(emptyActivityList));
        assertTrue(conflictingActivityList2.conflictsWith(normalActivityList1));
        assertTrue(conflictingActivityList2.conflictsWith(normalActivityList2));
        assertTrue(conflictingActivityList2.conflictsWith(normalActivityList3));
        assertFalse(conflictingActivityList2.conflictsWith(conflictingActivityList1));
        assertTrue(conflictingActivityList2.conflictsWith(conflictingActivityList2));
    }
    
    @Test
    public void testFilter() {
        ActivityList actual = normalActivityList3.filter(new ActivityFilter.HasResource(teacher2));
        ActivityList expected = new ActivityList(normalActivityList3.get(0));
        assertEquals(expected, actual);
    }

    @Before
    public void buildThings() {
        buildTags();
        buildResources();
        buildEmptyActivityList();
        buildNormalActivityList1();
        buildNormalActivityList2();
        buildNormalActivityList3();
        buildConflictingActivityList1();
        buildConflictingActivityList2();
    }

    private void buildTags() {
        subject1 = new Tag(Tag.Type.SUBJECT, "Mathematics");
        subject2 = new Tag(Tag.Type.SUBJECT, "Literature");
        language = new Tag(Tag.Type.LANGUAGE, "German");
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

    private void buildEmptyActivityList() {
        emptyActivityList = new ActivityList();
    }
    
    private void buildNormalActivityList1() {
        normalActivityList1 = new ActivityList();
        
        Activity activity1 = new Activity();
        activity1.getTagManager().add(subject1);
        activity1.getResourceManager().add(teacher1);
        activity1.getResourceManager().add(class1);
        normalActivityList1.add(activity1);
        
        Activity activity2 = new Activity();
        activity2.getTagManager().add(subject1);
        activity2.getResourceManager().add(teacher2);
        activity2.getResourceManager().add(class2);
        normalActivityList1.add(activity2);
    }
    
    private void buildNormalActivityList2() {
        normalActivityList2 = new ActivityList();
        
        Activity activity1 = new Activity();
        activity1.getTagManager().add(subject2);
        activity1.getTagManager().add(language);
        activity1.getResourceManager().add(teacher1);
        activity1.getResourceManager().add(class1);
        activity1.getResourceManager().add(multiResource);
        normalActivityList2.add(activity1);
    }
    
    private void buildNormalActivityList3() {
        normalActivityList3 = new ActivityList();
        
        Activity activity1 = new Activity();
        activity1.getTagManager().add(subject1);
        activity1.getResourceManager().add(teacher2);
        activity1.getResourceManager().add(new ResourceSubset.SplittingPart(
            class2.getSplittingManager().getSplittings().get(0).getParts().get(0)
        ));
        activity1.getResourceManager().add(multiResource);
        normalActivityList3.add(activity1);
        
        Activity activity2 = new Activity();
        activity2.getTagManager().add(subject1);
        activity2.getResourceManager().add(new ResourceSubset.SplittingPart(
            class2.getSplittingManager().getSplittings().get(0).getParts().get(1)
        ));
        normalActivityList3.add(activity2);
    }
    
    private void buildConflictingActivityList1() {
        conflictingActivityList1 = new ActivityList();
        
        Activity activity1 = new Activity();
        activity1.getTagManager().add(subject1);
        activity1.getResourceManager().add(teacher2);
        activity1.getResourceManager().add(teacher2);
        conflictingActivityList1.add(activity1);
    }
    
    private void buildConflictingActivityList2() {
        conflictingActivityList2 = new ActivityList();
        
        Activity activity1 = new Activity();
        activity1.getTagManager().add(subject1);
        activity1.getResourceManager().add(teacher1);
        activity1.getResourceManager().add(class1);
        activity1.getResourceManager().add(multiResource);
        conflictingActivityList2.add(activity1);
        
        Activity activity2 = new Activity();
        activity2.getTagManager().add(subject2);
        activity2.getTagManager().add(language);
        activity2.getResourceManager().add(teacher1);
        activity2.getResourceManager().add(class2);
        conflictingActivityList2.add(activity2);
    }
    
}
