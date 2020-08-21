package hu.webarticum.aurora.core.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class BlockFilterTest {

    private Period period1;
    private Period period2;
    
    private Resource class1;
    private Resource class2;
    private Resource teacher1;
    private Resource teacher2;
    private Resource object;

    private Block emptyBlock;
    private Block simpleBlock;
    private Block period1Block;
    private Block period2Block;
    private Block complexBlock;

    @Test
    public void testTrue() {
        BlockFilter filter = new BlockFilter.TrueBlockFilter();
        assertTrue(filter.validate(emptyBlock));
        assertTrue(filter.validate(simpleBlock));
        assertTrue(filter.validate(period1Block));
        assertTrue(filter.validate(period2Block));
        assertTrue(filter.validate(complexBlock));
    }

    @Test
    public void testFalse() {
        BlockFilter filter = new BlockFilter.FalseBlockFilter();
        assertFalse(filter.validate(emptyBlock));
        assertFalse(filter.validate(simpleBlock));
        assertFalse(filter.validate(period1Block));
        assertFalse(filter.validate(period2Block));
        assertFalse(filter.validate(complexBlock));
    }

    @Test
    public void testActivity() {
        BlockFilter filter = new BlockFilter.ActivityBlockFilter(new ActivityFilter.HasResource(class2));
        assertFalse(filter.validate(emptyBlock));
        assertFalse(filter.validate(simpleBlock));
        assertFalse(filter.validate(period1Block));
        assertTrue(filter.validate(period2Block));
        assertTrue(filter.validate(complexBlock));
    }

    @Test
    public void testPeriod() {
        BlockFilter filter = new BlockFilter.PeriodBlockFilter(period1);
        assertFalse(filter.validate(emptyBlock));
        assertTrue(filter.validate(simpleBlock));
        assertTrue(filter.validate(period1Block));
        assertFalse(filter.validate(period2Block));
        assertTrue(filter.validate(complexBlock));
    }

    @Test
    public void testPeriodActivity() {
        BlockFilter filter1 = new BlockFilter.PeriodActivityBlockFilter(
            new ActivityFilter.HasResource(object),
            period1
        );
        assertFalse(filter1.validate(emptyBlock));
        assertTrue(filter1.validate(simpleBlock));
        assertFalse(filter1.validate(period1Block));
        assertFalse(filter1.validate(period2Block));
        assertTrue(filter1.validate(complexBlock));
        
        BlockFilter filter2 = new BlockFilter.PeriodActivityBlockFilter(
            new ActivityFilter.HasResource(object),
            period2
        );
        assertFalse(filter2.validate(emptyBlock));
        assertTrue(filter2.validate(simpleBlock));
        assertFalse(filter2.validate(period1Block));
        assertFalse(filter2.validate(period2Block));
        assertFalse(filter2.validate(complexBlock));
    }

    @Test
    public void testComplex() {
        BlockFilter filter = new BlockFilter.And(
            new BlockFilter.Or(
                new BlockFilter.PeriodActivityBlockFilter(new ActivityFilter.HasResource(class1), period2),
                new BlockFilter.ActivityBlockFilter(new ActivityFilter.HasResource(class2))
            ),
            new BlockFilter.Not(
                new BlockFilter.ActivityBlockFilter(new ActivityFilter.HasResource(object))
            )
        );
        assertFalse(filter.validate(emptyBlock));
        assertFalse(filter.validate(simpleBlock));
        assertFalse(filter.validate(period1Block));
        assertTrue(filter.validate(period2Block));
        assertFalse(filter.validate(complexBlock));
    }
    
    @Before
    public void buildThings() {
        buildPeriods();
        buildResources();
        buildBlocks();
    }

    private void buildPeriods() {
        period1 = new Period("Period1", 1);
        period2 = new Period("Period1", 2);
    }
    
    private void buildResources() {
        class1 = new Resource(Resource.Type.CLASS, "Class1");
        Resource.Splitting languageSplitting = class1.getSplittingManager().add("Language");
        languageSplitting.addPart("German");
        languageSplitting.addPart("Spanish");
        
        class2 = new Resource(Resource.Type.CLASS, "Class2");

        teacher1 = new Resource(Resource.Type.PERSON, "X Y");
        teacher2 = new Resource(Resource.Type.PERSON, "N N");
        
        object = new Resource(Resource.Type.OBJECT, "Object");
    }
    
    private void buildBlocks() {
        buildEmptyBlock();
        buildSimpleBlock();
        buildPeriod1Block();
        buildPeriod2Block();
        buildComplexBlock();
    }

    private void buildEmptyBlock() {
        emptyBlock = new Block("Empty block");
    }
    
    private void buildSimpleBlock() {
        simpleBlock = new Block("Simple block");
        
        Activity activity = new Activity("Simple block activity");
        activity.getResourceManager().add(class1);
        activity.getResourceManager().add(teacher1);
        activity.getResourceManager().add(object);
        
        simpleBlock.getActivityManager().add(activity, period1, period2);
    }
    
    private void buildPeriod1Block() {
        period1Block = new Block("Period1 block");
        
        Activity activity = new Activity("Period1 block activity");
        activity.getResourceManager().add(class1);
        activity.getResourceManager().add(teacher2);
        
        period1Block.getActivityManager().add(activity, period1);
    }
    
    private void buildPeriod2Block() {
        period2Block = new Block("Period2 block");
        
        Activity activity = new Activity("Period2 block activity");
        activity.getResourceManager().add(class2);
        activity.getResourceManager().add(teacher1);
        
        period2Block.getActivityManager().add(activity, period2);
    }
    
    private void buildComplexBlock() {
        complexBlock = new Block("Complex block");
        
        Activity activity1 = new Activity("Complex block period1 activity");
        activity1.getResourceManager().add(class1);
        activity1.getResourceManager().add(teacher1);
        activity1.getResourceManager().add(object);
        
        Activity activity2 = new Activity("Complex block period2 activity");
        activity2.getResourceManager().add(class2);
        activity2.getResourceManager().add(teacher2);

        Block.ActivityManager activityManager = complexBlock.getActivityManager();
        activityManager.add(activity1, period1);
        activityManager.add(activity2, period2);
    }
    
}
