package hu.webarticum.aurora.core.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import hu.webarticum.aurora.core.model.time.Interval;
import hu.webarticum.aurora.core.model.time.Time;

public class BlockTest {

    private Period week1;
    private Period week2;

    private TimingSet defaultTimingSet;
    private TimingSet secondaryTimingSet;
    
    private Tag subject1;
    private Tag subject2;
    
    private Resource class1;
    private Resource class2;
    private Resource teacher1;
    private Resource teacher2;
    private Resource teacher3;
    private Resource multiResource;

    private Block emptyBlock;
    private Block independentBlock;
    private Block normalBlock1;
    private Block normalBlock2;
    private Block normalBlock3;
    private Block normalBlock4;
    private Block conflictingBlock;
    

    @Test
    public void testConstructor() {
        assertEquals(Block.DEFAULT_LENGTH, new Block().getLength());
        try {
            new Block(-99);
            fail();
        } catch (IllegalArgumentException e) {
        }
    }
    
    @Test
    public void testHasConflicts() {
        assertFalse(emptyBlock.hasConflicts());
        assertFalse(emptyBlock.hasConflicts(week1));
        assertFalse(emptyBlock.hasConflicts(week2));
        
        assertFalse(independentBlock.hasConflicts());
        assertFalse(independentBlock.hasConflicts(week1));
        assertFalse(independentBlock.hasConflicts(week2));
        
        assertFalse(normalBlock1.hasConflicts());
        assertFalse(normalBlock1.hasConflicts(week1));
        assertFalse(normalBlock1.hasConflicts(week2));
        
        assertFalse(normalBlock2.hasConflicts());
        assertFalse(normalBlock2.hasConflicts(week1));
        assertFalse(normalBlock2.hasConflicts(week2));
        
        assertFalse(normalBlock3.hasConflicts());
        assertFalse(normalBlock3.hasConflicts(week1));
        assertFalse(normalBlock3.hasConflicts(week2));
        
        assertFalse(normalBlock4.hasConflicts());
        assertFalse(normalBlock4.hasConflicts(week1));
        assertFalse(normalBlock4.hasConflicts(week2));
        
        assertTrue(conflictingBlock.hasConflicts());
        assertFalse(conflictingBlock.hasConflicts(week1));
        assertTrue(conflictingBlock.hasConflicts(week2));
    }

    @Test
    public void testConflictsWith() {
        assertFalse(emptyBlock.conflictsWith(emptyBlock));
        assertFalse(emptyBlock.conflictsWith(independentBlock));
        assertFalse(emptyBlock.conflictsWith(normalBlock1));
        assertFalse(emptyBlock.conflictsWith(normalBlock2));
        assertFalse(emptyBlock.conflictsWith(normalBlock3));
        assertFalse(emptyBlock.conflictsWith(normalBlock4));
        assertFalse(emptyBlock.conflictsWith(conflictingBlock));
        
        assertFalse(independentBlock.conflictsWith(emptyBlock));
        assertFalse(independentBlock.conflictsWith(independentBlock));
        assertFalse(independentBlock.conflictsWith(normalBlock1));
        assertFalse(independentBlock.conflictsWith(normalBlock2));
        assertFalse(independentBlock.conflictsWith(normalBlock3));
        assertFalse(independentBlock.conflictsWith(normalBlock4));
        assertFalse(independentBlock.conflictsWith(conflictingBlock));

        assertFalse(normalBlock1.conflictsWith(emptyBlock));
        assertFalse(normalBlock1.conflictsWith(independentBlock));
        assertTrue(normalBlock1.conflictsWith(normalBlock1));
        assertFalse(normalBlock1.conflictsWith(normalBlock2));
        assertTrue(normalBlock1.conflictsWith(normalBlock3));
        assertTrue(normalBlock1.conflictsWith(normalBlock4));
        assertTrue(normalBlock1.conflictsWith(conflictingBlock));

        assertFalse(normalBlock2.conflictsWith(emptyBlock));
        assertFalse(normalBlock2.conflictsWith(independentBlock));
        assertFalse(normalBlock2.conflictsWith(normalBlock1));
        assertTrue(normalBlock2.conflictsWith(normalBlock2));
        assertTrue(normalBlock2.conflictsWith(normalBlock3));
        assertTrue(normalBlock2.conflictsWith(normalBlock4));
        assertTrue(normalBlock2.conflictsWith(conflictingBlock));

        assertFalse(normalBlock3.conflictsWith(emptyBlock));
        assertFalse(normalBlock3.conflictsWith(independentBlock));
        assertTrue(normalBlock3.conflictsWith(normalBlock1));
        assertTrue(normalBlock3.conflictsWith(normalBlock2));
        assertTrue(normalBlock3.conflictsWith(normalBlock3));
        assertFalse(normalBlock3.conflictsWith(normalBlock4));
        assertTrue(normalBlock3.conflictsWith(conflictingBlock));

        assertFalse(normalBlock4.conflictsWith(emptyBlock));
        assertFalse(normalBlock4.conflictsWith(independentBlock));
        assertTrue(normalBlock4.conflictsWith(normalBlock1));
        assertTrue(normalBlock4.conflictsWith(normalBlock2));
        assertFalse(normalBlock4.conflictsWith(normalBlock3));
        assertTrue(normalBlock4.conflictsWith(normalBlock4));
        assertTrue(normalBlock4.conflictsWith(conflictingBlock));

        assertFalse(conflictingBlock.conflictsWith(emptyBlock));
        assertFalse(conflictingBlock.conflictsWith(independentBlock));
        assertTrue(conflictingBlock.conflictsWith(normalBlock1));
        assertTrue(conflictingBlock.conflictsWith(normalBlock2));
        assertTrue(conflictingBlock.conflictsWith(normalBlock3));
        assertTrue(conflictingBlock.conflictsWith(normalBlock4));
        assertTrue(conflictingBlock.conflictsWith(conflictingBlock));
    }

    @Test
    public void testActivityManagerOfEmptyBlock() {
        assertEquals(new PeriodSet(), emptyBlock.getActivityManager().getPeriods());
        assertEquals(0, emptyBlock.getActivityManager().getActivities().size());
        assertEquals(0, emptyBlock.getActivityManager().getActivities(week1).size());
        assertEquals(0, emptyBlock.getActivityManager().getActivities(week2).size());
        assertEquals(0, emptyBlock.getActivityManager().getActivities(
            Arrays.asList(week1, week2)
        ).size());
        assertEquals(0, emptyBlock.getActivityManager().getActivities(
            Arrays.asList(week1, week2), true
        ).size());
        assertEquals(new PeriodSet(), emptyBlock.getActivityManager().getResourcePeriods(class1));
        assertEquals(new PeriodSet(), emptyBlock.getActivityManager().getResourcePeriods(class2));
        assertEquals(new PeriodSet(), emptyBlock.getActivityManager().getResourcePeriods(teacher1));
        assertEquals(new PeriodSet(), emptyBlock.getActivityManager().getResourcePeriods(multiResource));
        assertEquals(new PeriodSet(), emptyBlock.getActivityManager().getResourceSubsetPeriods(
            new ResourceSubset.SplittingPart(
                class1.getSplittingManager().getSplittings().get(0).getParts().get(0)
            )
        ));
        assertFalse(emptyBlock.getActivityManager().hasPeriod(week1));
        assertFalse(emptyBlock.getActivityManager().hasPeriod(week2));
    }

    @Test
    public void testActivityManagerOfIndependentBlock() {
        assertEquals(new PeriodSet(), independentBlock.getActivityManager().getPeriods());
        assertEquals(1, independentBlock.getActivityManager().getActivities().size());
        assertEquals(0, independentBlock.getActivityManager().getActivities(week1).size());
        assertEquals(0, independentBlock.getActivityManager().getActivities(week2).size());
        assertEquals(0, independentBlock.getActivityManager().getActivities(
            Arrays.asList(week1, week2)
        ).size());
        assertEquals(0, independentBlock.getActivityManager().getActivities(
            Arrays.asList(week1, week2), true
        ).size());
        assertEquals(new PeriodSet(), independentBlock.getActivityManager().getResourcePeriods(class1));
        assertEquals(new PeriodSet(), independentBlock.getActivityManager().getResourcePeriods(class2));
        assertEquals(new PeriodSet(), independentBlock.getActivityManager().getResourcePeriods(teacher1));
        assertEquals(new PeriodSet(), independentBlock.getActivityManager().getResourcePeriods(multiResource));
        assertEquals(new PeriodSet(), independentBlock.getActivityManager().getResourceSubsetPeriods(
            new ResourceSubset.SplittingPart(
                class1.getSplittingManager().getSplittings().get(0).getParts().get(0)
            )
        ));
        assertFalse(independentBlock.getActivityManager().hasPeriod(week1));
        assertFalse(independentBlock.getActivityManager().hasPeriod(week2));
    }

    @Test
    public void testActivityManagerOfNormalBlock1() {
        assertEquals(setOf(week1, week2), normalBlock1.getActivityManager().getPeriods());
        assertEquals(1, normalBlock1.getActivityManager().getActivities().size());
        assertEquals(1, normalBlock1.getActivityManager().getActivities(week1).size());
        assertEquals(1, normalBlock1.getActivityManager().getActivities(week2).size());
        assertEquals(1, normalBlock1.getActivityManager().getActivities(
            Arrays.asList(week1, week2)
        ).size());
        assertEquals(1, normalBlock1.getActivityManager().getActivities(
            Arrays.asList(week1, week2), true
        ).size());
        assertEquals(setOf(week1, week2), normalBlock1.getActivityManager().getResourcePeriods(class1));
        assertEquals(new PeriodSet(), normalBlock1.getActivityManager().getResourcePeriods(class2));
        assertEquals(setOf(week1, week2), normalBlock1.getActivityManager().getResourcePeriods(teacher1));
        assertEquals(setOf(week1, week2), normalBlock1.getActivityManager().getResourceSubsetPeriods(
            new ResourceSubset.SplittingPart(
                class1.getSplittingManager().getSplittings().get(0).getParts().get(0)
            )
        ));
        assertTrue(normalBlock1.getActivityManager().hasPeriod(week1));
        assertTrue(normalBlock1.getActivityManager().hasPeriod(week2));
    }

    @Test
    public void testActivityManagerOfNormalBlock2() {
        assertEquals(setOf(week1, week2), normalBlock2.getActivityManager().getPeriods());
        assertEquals(2, normalBlock2.getActivityManager().getActivities().size());
        assertEquals(2, normalBlock2.getActivityManager().getActivities(week1).size());
        assertEquals(2, normalBlock2.getActivityManager().getActivities(week2).size());
        assertEquals(2, normalBlock2.getActivityManager().getActivities(
            Arrays.asList(week1, week2)
        ).size());
        assertEquals(2, normalBlock2.getActivityManager().getActivities(
            Arrays.asList(week1, week2), true
        ).size());
        assertEquals(new PeriodSet(), normalBlock2.getActivityManager().getResourcePeriods(class1));
        assertEquals(setOf(week1, week2), normalBlock2.getActivityManager().getResourcePeriods(class2));
        assertEquals(new PeriodSet(), normalBlock2.getActivityManager().getResourcePeriods(teacher1));
        assertEquals(new PeriodSet(), normalBlock2.getActivityManager().getResourceSubsetPeriods(
            new ResourceSubset.SplittingPart(
                class1.getSplittingManager().getSplittings().get(0).getParts().get(0)
            )
        ));
        assertTrue(normalBlock2.getActivityManager().hasPeriod(week1));
        assertTrue(normalBlock2.getActivityManager().hasPeriod(week2));
    }

    @Test
    public void testActivityManagerOfNormalBlock3() {
        assertEquals(setOf(week1), normalBlock3.getActivityManager().getPeriods());
        assertEquals(2, normalBlock3.getActivityManager().getActivities().size());
        assertEquals(2, normalBlock3.getActivityManager().getActivities(week1).size());
        assertEquals(0, normalBlock3.getActivityManager().getActivities(week2).size());
        assertEquals(2, normalBlock3.getActivityManager().getActivities(
            Arrays.asList(week1, week2)
        ).size());
        assertEquals(0, normalBlock3.getActivityManager().getActivities(
            Arrays.asList(week1, week2), true
        ).size());
        assertEquals(setOf(week1), normalBlock3.getActivityManager().getResourcePeriods(class1));
        assertEquals(setOf(week1), normalBlock3.getActivityManager().getResourcePeriods(class2));
        assertEquals(setOf(week1), normalBlock3.getActivityManager().getResourcePeriods(teacher1));
        assertEquals(setOf(week1), normalBlock3.getActivityManager().getResourceSubsetPeriods(
            new ResourceSubset.SplittingPart(
                class1.getSplittingManager().getSplittings().get(0).getParts().get(0)
            )
        ));
        assertTrue(normalBlock3.getActivityManager().hasPeriod(week1));
        assertFalse(normalBlock3.getActivityManager().hasPeriod(week2));
    }

    @Test
    public void testActivityManagerOfNormalBlock4() {
        assertEquals(setOf(week2), normalBlock4.getActivityManager().getPeriods());
        assertEquals(2, normalBlock4.getActivityManager().getActivities().size());
        assertEquals(0, normalBlock4.getActivityManager().getActivities(week1).size());
        assertEquals(2, normalBlock4.getActivityManager().getActivities(week2).size());
        assertEquals(2, normalBlock4.getActivityManager().getActivities(
            Arrays.asList(week1, week2)
        ).size());
        assertEquals(0, normalBlock4.getActivityManager().getActivities(
            Arrays.asList(week1, week2), true
        ).size());
        assertEquals(setOf(week2), normalBlock4.getActivityManager().getResourcePeriods(class1));
        assertEquals(setOf(week2), normalBlock4.getActivityManager().getResourcePeriods(class2));
        assertEquals(setOf(week2), normalBlock4.getActivityManager().getResourcePeriods(teacher1));
        assertEquals(setOf(week2), normalBlock4.getActivityManager().getResourceSubsetPeriods(
            new ResourceSubset.SplittingPart(
                class1.getSplittingManager().getSplittings().get(0).getParts().get(0)
            )
        ));
        assertFalse(normalBlock4.getActivityManager().hasPeriod(week1));
        assertTrue(normalBlock4.getActivityManager().hasPeriod(week2));
    }

    @Test
    public void testActivityManagerOfConflictingBlock() {
        assertEquals(setOf(week1, week2), conflictingBlock.getActivityManager().getPeriods());
        assertEquals(2, conflictingBlock.getActivityManager().getActivities().size());
        assertEquals(1, conflictingBlock.getActivityManager().getActivities(week1).size());
        assertEquals(2, conflictingBlock.getActivityManager().getActivities(week2).size());
        assertEquals(2, conflictingBlock.getActivityManager().getActivities(
            Arrays.asList(week1, week2)
        ).size());
        assertEquals(1, conflictingBlock.getActivityManager().getActivities(
            Arrays.asList(week1, week2), true
        ).size());
        assertEquals(setOf(week2), conflictingBlock.getActivityManager().getResourcePeriods(class1));
        assertEquals(setOf(week1, week2), conflictingBlock.getActivityManager().getResourcePeriods(class2));
        assertEquals(new PeriodSet(), conflictingBlock.getActivityManager().getResourcePeriods(teacher1));
        assertEquals(setOf(week2), conflictingBlock.getActivityManager().getResourceSubsetPeriods(
            new ResourceSubset.SplittingPart(
                class1.getSplittingManager().getSplittings().get(0).getParts().get(0)
            )
        ));
        assertTrue(conflictingBlock.getActivityManager().hasPeriod(week1));
        assertTrue(conflictingBlock.getActivityManager().hasPeriod(week2));
    }
    
    @Test
    public void testGetCalculatedTimeLimit() {
        assertTrue(emptyBlock.getCalculatedTimeLimit().isAlways());
        assertTrue(normalBlock1.getCalculatedTimeLimit().isAlways());
        assertEquals(
            new Interval(0, (Time.HOUR * 13) + Block.DEFAULT_LENGTH - 1),
            normalBlock2.getCalculatedTimeLimit().getSimplified()
        );
        assertTrue(normalBlock3.getCalculatedTimeLimit().isAlways());
        assertTrue(normalBlock4.getCalculatedTimeLimit().isAlways());
        assertEquals(
            new Interval(0, (Time.HOUR * 13) + Block.DEFAULT_LENGTH - 1),
            conflictingBlock.getCalculatedTimeLimit().getSimplified()
        );
    }

    @Test
    public void testGetCalculatedTimingSetUnlimited() {
        assertTrue(emptyBlock.getCalculatedTimingSet(false).isEmpty());
        assertTrue(independentBlock.getCalculatedTimingSet(false).isEmpty());
        assertEquals(defaultTimingSet.getTimes(), normalBlock1.getCalculatedTimingSet(false).getTimes());
        assertEquals(secondaryTimingSet.getTimes(), normalBlock2.getCalculatedTimingSet(false).getTimes());
        assertEquals(defaultTimingSet.getTimes(), normalBlock3.getCalculatedTimingSet(false).getTimes());
        assertEquals(secondaryTimingSet.getTimes(), normalBlock4.getCalculatedTimingSet(false).getTimes());
        assertEquals(secondaryTimingSet.getTimes(), conflictingBlock.getCalculatedTimingSet(false).getTimes());
    }

    @Test
    public void testGetCalculatedTimingSet() {
        assertTrue(emptyBlock.getCalculatedTimingSet().isEmpty());
        assertTrue(independentBlock.getCalculatedTimingSet().isEmpty());
        assertEquals(defaultTimingSet.getTimes(), normalBlock1.getCalculatedTimingSet().getTimes());
        assertEquals(
            Arrays.asList(
                new Time(9 * Time.HOUR),
                new Time(10 * Time.HOUR),
                new Time(11 * Time.HOUR),
                new Time(12 * Time.HOUR)
            ),
            new ArrayList<Time>(normalBlock2.getCalculatedTimingSet().getTimes())
        );
        assertEquals(defaultTimingSet.getTimes(), normalBlock3.getCalculatedTimingSet().getTimes());
        assertEquals(secondaryTimingSet.getTimes(), normalBlock4.getCalculatedTimingSet().getTimes());
        assertEquals(
            Arrays.asList(
                new Time(9 * Time.HOUR),
                new Time(10 * Time.HOUR),
                new Time(11 * Time.HOUR),
                new Time(12 * Time.HOUR)
            ),
            new ArrayList<Time>(conflictingBlock.getCalculatedTimingSet().getTimes())
        );
    }
    
    @Before
    public void buildThings() {
        buildPeriodsTimingSetsAndTags();
        buildResources();
        buildEmptyBlock();
        buildIndependentBlock();
        buildNormalBlock1();
        buildNormalBlock2();
        buildNormalBlock3();
        buildNormalBlock4();
        buildConflictingBlock();
    }

    private void buildPeriodsTimingSetsAndTags() {
        week1 = new Period("Week A", 0);
        week2 = new Period("Week B", 1);

        defaultTimingSet = new TimingSet();
        defaultTimingSet.add(new Time(8 * Time.HOUR));
        defaultTimingSet.add(new Time(9 * Time.HOUR));
        defaultTimingSet.add(new Time(10 * Time.HOUR));
        defaultTimingSet.add(new Time(11 * Time.HOUR));
        defaultTimingSet.add(new Time(12 * Time.HOUR));
        defaultTimingSet.add(new Time(13 * Time.HOUR));
        
        secondaryTimingSet = new TimingSet();
        secondaryTimingSet.add(new Time(9 * Time.HOUR));
        secondaryTimingSet.add(new Time(10 * Time.HOUR));
        secondaryTimingSet.add(new Time(11 * Time.HOUR));
        secondaryTimingSet.add(new Time(12 * Time.HOUR));
        secondaryTimingSet.add(new Time(13 * Time.HOUR));
        
        subject1 = new Tag(Tag.Type.SUBJECT, "Mathematics");
        subject2 = new Tag(Tag.Type.SUBJECT, "Biology");
    }
    
    private void buildResources() {
        class1 = createClass("Class 1");
        class2 = createClass("Class 2");
        class2.getTimingSetManager().setPeriodTimingSet(week2, secondaryTimingSet);
        teacher1 = new Resource(Resource.Type.PERSON, "X Y");
        teacher2 = new Resource(Resource.Type.PERSON, "N N");
        teacher3 = new Resource(Resource.Type.PERSON, "Z Z");
        teacher3.getTimeLimitManager().setDefaultTimeLimit(
            new Interval(0, (Time.HOUR * 13) + Block.DEFAULT_LENGTH - 1)
        );
        teacher3.setTimeLimitEnabled(true);
        multiResource = new Resource(Resource.Type.PERSON, "O");
        multiResource.setQuantity(2);
    }

    private Resource createClass(String label) {
        Resource resource = new Resource(Resource.Type.CLASS, label);
        
        Resource.SplittingManager splittingManager = resource.getSplittingManager();
        
        Resource.Splitting languageSplitting = splittingManager.add("Language");
        languageSplitting.addPart("German");
        languageSplitting.addPart("Spanish");
        
        Resource.Splitting optionalSplitting = splittingManager.add("Language");
        optionalSplitting.addPart("Physics");
        optionalSplitting.addPart("Geography");
        optionalSplitting.addPart("Biology");
        
        resource.getTimingSetManager().setDefaultTimingSet(defaultTimingSet);
        resource.setTimingSetEnabled(true);
        
        return resource;
    }

    private void buildEmptyBlock() {
        emptyBlock = new Block();
    }

    private void buildIndependentBlock() {
        independentBlock = new Block();
        
        Activity activity1 = new Activity("Study X.1");
        activity1.getTagManager().add(subject1);
        activity1.getResourceManager().add(class1);
        activity1.getResourceManager().add(teacher1);
        independentBlock.getActivityManager().add(activity1);
    }
    
    private void buildNormalBlock1() {
        normalBlock1 = new Block();
        
        Activity activity1 = new Activity("Study 1.1");
        activity1.getTagManager().add(subject1);
        activity1.getResourceManager().add(class1);
        activity1.getResourceManager().add(teacher1);
        activity1.getResourceManager().add(multiResource);
        normalBlock1.getActivityManager().add(activity1, week1, week2);
    }
    
    private void buildNormalBlock2() {
        normalBlock2 = new Block();
        
        Activity activity1 = new Activity("Study 2.1");
        activity1.getTagManager().add(subject1);
        activity1.getResourceManager().add(new ResourceSubset.SplittingPart(
            class2.getSplittingManager().getSplittings().get(0).getParts().get(0)
        ));
        activity1.getResourceManager().add(teacher2);
        normalBlock2.getActivityManager().add(activity1, week1, week2);
        
        Activity activity2 = new Activity("Study 2.2");
        activity2.getTagManager().add(subject1);
        activity2.getResourceManager().add(new ResourceSubset.SplittingPart(
            class2.getSplittingManager().getSplittings().get(0).getParts().get(1)
        ));
        activity2.getResourceManager().add(teacher3);
        activity2.getResourceManager().add(multiResource);
        normalBlock2.getActivityManager().add(activity2, week1, week2);
    }

    private void buildNormalBlock3() {
        normalBlock3 = new Block();
        
        Activity activity1 = new Activity("Study 3.1");
        activity1.getTagManager().add(subject2);
        activity1.getResourceManager().add(class1);
        activity1.getResourceManager().add(teacher1);
        normalBlock3.getActivityManager().add(activity1, week1);
        
        Activity activity2 = new Activity("Study 3.2");
        activity2.getTagManager().add(subject2);
        activity2.getResourceManager().add(class2);
        activity2.getResourceManager().add(teacher2);
        activity2.getResourceManager().add(multiResource);
        normalBlock3.getActivityManager().add(activity2, week1);
    }

    private void buildNormalBlock4() {
        normalBlock4 = new Block();
        
        Activity activity1 = new Activity("Study 4.1");
        activity1.getTagManager().add(subject2);
        activity1.getResourceManager().add(class1);
        activity1.getResourceManager().add(teacher1);
        activity1.getResourceManager().add(multiResource);
        normalBlock4.getActivityManager().add(activity1, week2);
        
        Activity activity2 = new Activity("Study 4.2");
        activity2.getTagManager().add(subject2);
        activity2.getResourceManager().add(class2);
        activity2.getResourceManager().add(teacher2);
        normalBlock4.getActivityManager().add(activity2, week2);
    }
    
    private void buildConflictingBlock() {
        conflictingBlock = new Block();
        
        Activity activity1 = new Activity("Study 5.1");
        activity1.getTagManager().add(subject1);
        activity1.getResourceManager().add(class1);
        activity1.getResourceManager().add(teacher3);
        conflictingBlock.getActivityManager().add(activity1, week2);
        
        Activity activity2 = new Activity("Study 5.2");
        activity2.getTagManager().add(subject2);
        activity2.getResourceManager().add(class2);
        activity2.getResourceManager().add(teacher3);
        activity2.getResourceManager().add(multiResource);
        conflictingBlock.getActivityManager().add(activity2, week1, week2);
    }
    
    @SuppressWarnings("unchecked")
    private <T> Set<T> setOf(T... items) {
        return new HashSet<T>(Arrays.asList(items));
    }
    
}
