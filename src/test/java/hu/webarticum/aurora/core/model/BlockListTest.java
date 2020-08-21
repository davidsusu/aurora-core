package hu.webarticum.aurora.core.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import hu.webarticum.aurora.core.model.time.Time;

public class BlockListTest {

    private Period week1;
    private Period week2;
    
    private Tag subject1;
    private Tag subject2;
    
    private Resource class1;
    private Resource class2;
    private Resource teacher1;
    private Resource teacher2;
    private Resource multiResource;
    
    private Block block1;
    private Block block2;
    private Block block3;
    private Block block4;
    private Block block5;
    private Block block6;

    private BlockList allBlocks;
    

    @Test
    public void testCopy() {
        BlockList copyOfAllBlocks = allBlocks.copy();
        assertNotSame(allBlocks, copyOfAllBlocks);
        assertEquals(allBlocks, copyOfAllBlocks);
    }

    @Test
    public void testGetShortestAndLongest() {
        assertSame(block5, allBlocks.getShortest());
        assertSame(block6, allBlocks.getLongest());
    }

    @Test
    public void testGetPeriods() {
        assertEquals(new PeriodSet(), new BlockList().getPeriods());
        assertEquals(setOf(week1), new BlockList(block4).getPeriods());
        assertEquals(setOf(week1, week2), allBlocks.getPeriods());
    }

    @Test
    public void testGetActivities() {
        assertEquals(0, new BlockList().getActivities().size());
        assertEquals(5, new BlockList(block2, block6).getActivities().size());
        assertEquals(9, allBlocks.getActivities().size());
    }

    @Test
    public void testGetActivitiesByPeriod() {
        assertEquals(0, new BlockList().getActivities(week1).size());
        assertEquals(4, new BlockList(block2, block6).getActivities(week1).size());
        assertEquals(7, allBlocks.getActivities(week1).size());

        assertEquals(0, new BlockList().getActivities(week2).size());
        assertEquals(4, new BlockList(block2, block6).getActivities(week2).size());
        assertEquals(7, allBlocks.getActivities(week2).size());
    }
    
    @Test
    public void testGetActivitiesByPeriods() {
        assertEquals(0, new BlockList().getActivities(Arrays.asList(week1, week2)).size());
        assertEquals(5, new BlockList(block2, block6).getActivities(Arrays.asList(week1, week2)).size());
        assertEquals(9, allBlocks.getActivities(Arrays.asList(week1, week2)).size());
    }

    @Test
    public void testGetActivitiesByPeriodsRequireAll() {
        assertEquals(0, new BlockList().getActivities(Arrays.asList(week1, week2), true).size());
        assertEquals(3, new BlockList(block2, block6).getActivities(Arrays.asList(week1, week2), true).size());
        assertEquals(5, allBlocks.getActivities(Arrays.asList(week1, week2), true).size());
    }
    
    @Test
    public void testHasConflicts() {
        assertFalse(new BlockList().hasConflicts());
        assertTrue(new BlockList(block1, block2, block3).hasConflicts());
        assertFalse(new BlockList(block2, block3).hasConflicts());
        assertTrue(new BlockList(block3, block4).hasConflicts());
        assertFalse(new BlockList(block4, block5).hasConflicts());
    }

    @Test
    public void testHasConflictsInPeriod() {
        assertFalse(new BlockList().hasConflicts(week1));
        assertTrue(new BlockList(block1, block2, block3).hasConflicts(week1));
        assertFalse(new BlockList(block2, block3).hasConflicts(week1));
        assertTrue(new BlockList(block3, block4).hasConflicts(week1));
        assertFalse(new BlockList(block4, block5).hasConflicts(week1));

        assertFalse(new BlockList().hasConflicts(week2));
        assertTrue(new BlockList(block1, block2, block3).hasConflicts(week2));
        assertFalse(new BlockList(block2, block3).hasConflicts(week2));
        assertFalse(new BlockList(block3, block4).hasConflicts(week2));
        assertFalse(new BlockList(block4, block5).hasConflicts(week2));
    }

    @Test
    public void testConflictsWith() {
        assertFalse(new BlockList().conflictsWith(new BlockList()));
        assertFalse(new BlockList().conflictsWith(new BlockList(block3, block4)));
        assertFalse(new BlockList(block3, block4).conflictsWith(new BlockList()));
        assertTrue(new BlockList(block3).conflictsWith(new BlockList(block4)));
        assertTrue(new BlockList(block1, block2).conflictsWith(new BlockList(block3, block4)));
    }

    @Test
    public void testConflictsWithInPeriod() {
        assertFalse(new BlockList().conflictsWith(new BlockList(), week1));
        assertTrue(new BlockList().conflictsWith(new BlockList(block3, block4), week1));
        assertTrue(new BlockList(block3, block4).conflictsWith(new BlockList(), week1));
        assertTrue(new BlockList(block3).conflictsWith(new BlockList(block4), week1));
        assertTrue(new BlockList(block1, block2).conflictsWith(new BlockList(block3, block4), week1));
        assertTrue(new BlockList(block3).conflictsWith(new BlockList(block4), week1));

        assertFalse(new BlockList().conflictsWith(new BlockList(), week2));
        assertFalse(new BlockList().conflictsWith(new BlockList(block3, block4), week2));
        assertFalse(new BlockList(block3, block4).conflictsWith(new BlockList(), week2));
        assertFalse(new BlockList(block3).conflictsWith(new BlockList(block4), week2));
        assertTrue(new BlockList(block1, block2).conflictsWith(new BlockList(block3, block4), week2));
        assertFalse(new BlockList(block3).conflictsWith(new BlockList(block4), week2));
    }

    @Test
    public void testFilter() {
        BlockFilter teacher2Filter = new BlockFilter.ActivityBlockFilter(new ActivityFilter.HasResource(teacher2));
        assertEquals(new BlockList(block6), allBlocks.filter(teacher2Filter));
        
        BlockFilter subject2Filter = new BlockFilter.ActivityBlockFilter(new ActivityFilter.HasTag(subject2));
        assertEquals(new BlockList(block2, block3, block4, block6), allBlocks.filter(subject2Filter));
    }

    
    @Before
    public void buildThings() {
        buildPeriodsAndTags();
        buildResources();
        buildBlock1();
        buildBlock2();
        buildBlock3();
        buildBlock4();
        buildBlock5();
        buildBlock6();
        allBlocks = new BlockList(block1, block2, block3, block4, block5, block6);
    }

    private void buildPeriodsAndTags() {
        week1 = new Period("Week A", 0);
        week2 = new Period("Week B", 1);
        subject1 = new Tag(Tag.Type.SUBJECT, "Mathematics");
        subject2 = new Tag(Tag.Type.SUBJECT, "Biology");
    }
    
    private void buildResources() {
        class1 = createClass("Class 1");
        class2 = createClass("Class 2");
        teacher1 = new Resource(Resource.Type.PERSON, "X Y");
        teacher2 = new Resource(Resource.Type.PERSON, "N N");
        multiResource = new Resource(Resource.Type.PERSON, "O");
        multiResource.setQuantity(2);
    }

    private Resource createClass(String label) {
        Resource resource = new Resource(Resource.Type.CLASS, label);
        
        Resource.SplittingManager splittingManager = resource.getSplittingManager();
        
        Resource.Splitting splitting = splittingManager.add("Language");
        splitting.addPart("Physics");
        splitting.addPart("Geography");
        splitting.addPart("Biology");
        
        return resource;
    }

    private void buildBlock1() {
        block1 = new Block();

        Activity activity1 = new Activity("Study 1.1");
        activity1.getTagManager().add(subject1);
        activity1.getResourceManager().add(class1);
        activity1.getResourceManager().add(teacher1);
        activity1.getResourceManager().add(multiResource);
        block1.getActivityManager().add(activity1, week1, week2);
    }

    private void buildBlock2() {
        block2 = new Block();

        Activity activity1 = new Activity("Study 2.1");
        activity1.getTagManager().add(subject2);
        activity1.getResourceManager().add(new ResourceSubset.SplittingPart(
            class2.getSplittingManager().getSplittings().get(0).getParts().get(0)
        ));
        activity1.getResourceManager().add(multiResource);
        block2.getActivityManager().add(activity1, week1, week2);
    }

    private void buildBlock3() {
        block3 = new Block();

        Activity activity1 = new Activity("Study 3.1");
        activity1.getTagManager().add(subject2);
        activity1.getResourceManager().add(new ResourceSubset.SplittingPart(
            class2.getSplittingManager().getSplittings().get(0).getParts().get(1)
        ));
        activity1.getResourceManager().add(multiResource);
        block3.getActivityManager().add(activity1, week1, week2);
    }

    private void buildBlock4() {
        block4 = new Block();

        Activity activity1 = new Activity("Study 4.1");
        activity1.getTagManager().add(subject2);
        activity1.getResourceManager().add(class2);
        block4.getActivityManager().add(activity1, week1);
    }

    private void buildBlock5() {
        block5 = new Block(Block.DEFAULT_LENGTH - Time.MINUTE);

        Activity activity1 = new Activity("Study 5.1");
        activity1.getTagManager().add(subject1);
        activity1.getResourceManager().add(class2);
        block5.getActivityManager().add(activity1, week2);
    }

    private void buildBlock6() {
        block6 = new Block(Block.DEFAULT_LENGTH + Time.MINUTE);

        Activity activity1 = new Activity("Study 6.1");
        activity1.getTagManager().add(subject1);
        activity1.getResourceManager().add(new ResourceSubset.SplittingPart(
            class1.getSplittingManager().getSplittings().get(0).getParts().get(0)
        ));
        activity1.getResourceManager().add(teacher1);
        block6.getActivityManager().add(activity1, week1, week2);

        Activity activity2 = new Activity("Study 6.2");
        activity2.getTagManager().add(subject2);
        activity2.getResourceManager().add(new ResourceSubset.SplittingPart(
            class1.getSplittingManager().getSplittings().get(0).getParts().get(1)
        ));
        activity1.getResourceManager().add(teacher2);
        block6.getActivityManager().add(activity2, week1, week2);

        Activity activity3 = new Activity("Study 6.3");
        activity3.getTagManager().add(subject1);
        activity3.getResourceManager().add(class2);
        block6.getActivityManager().add(activity3, week1);

        Activity activity4 = new Activity("Study 6.4");
        activity4.getTagManager().add(subject2);
        activity4.getResourceManager().add(class2);
        block6.getActivityManager().add(activity4, week2);
    }

    @SuppressWarnings("unchecked")
    private <T> Set<T> setOf(T... items) {
        return new HashSet<T>(Arrays.asList(items));
    }
    
}
