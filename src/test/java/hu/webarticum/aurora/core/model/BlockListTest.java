package hu.webarticum.aurora.core.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
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
        assertThat(copyOfAllBlocks).isNotSameAs(allBlocks);
        assertThat(copyOfAllBlocks).isEqualTo(allBlocks);
    }

    @Test
    public void testGetShortestAndLongest() {
        assertThat(allBlocks.getShortest()).isSameAs(block5);
        assertThat(allBlocks.getLongest()).isSameAs(block6);
    }

    @Test
    public void testGetPeriods() {
        assertThat((Set<Period>) new BlockList().getPeriods()).isEmpty();
        assertThat((Set<Period>) new BlockList(block4).getPeriods()).containsExactly(week1);
        assertThat((Set<Period>) allBlocks.getPeriods()).containsExactly(week1, week2);
    }

    @Test
    public void testGetActivities() {
        assertThat((List<Activity>) new BlockList().getActivities()).isEmpty();
        assertThat((List<Activity>) new BlockList(block2, block6).getActivities()).hasSize(5);
        assertThat((List<Activity>) allBlocks.getActivities()).hasSize(9);
    }

    @Test
    public void testGetActivitiesByPeriod() {
        assertThat((List<Activity>) new BlockList().getActivities(week1)).isEmpty();
        assertThat((List<Activity>) new BlockList(block2, block6).getActivities(week1)).hasSize(4);
        assertThat((List<Activity>) allBlocks.getActivities(week1)).hasSize(7);

        assertThat((List<Activity>) new BlockList().getActivities(week2)).isEmpty();
        assertThat((List<Activity>) new BlockList(block2, block6).getActivities(week2)).hasSize(4);
        assertThat((List<Activity>) allBlocks.getActivities(week2)).hasSize(7);
    }
    
    @Test
    public void testGetActivitiesByPeriods() {
        assertThat((List<Activity>) new BlockList().getActivities(Arrays.asList(week1, week2))).isEmpty();
        assertThat((List<Activity>) new BlockList(block2, block6).getActivities(Arrays.asList(week1, week2))).hasSize(5);
        assertThat((List<Activity>) allBlocks.getActivities(Arrays.asList(week1, week2))).hasSize(9);
    }

    @Test
    public void testGetActivitiesByPeriodsRequireAll() {
        assertThat((List<Activity>) new BlockList().getActivities(Arrays.asList(week1, week2), true)).isEmpty();
        assertThat((List<Activity>) new BlockList(block2, block6).getActivities(Arrays.asList(week1, week2), true)).hasSize(3);
        assertThat((List<Activity>) allBlocks.getActivities(Arrays.asList(week1, week2), true)).hasSize(5);
    }
    
    @Test
    public void testHasConflicts() {
        assertThat(new BlockList().hasConflicts()).isFalse();
        assertThat(new BlockList(block1, block2, block3).hasConflicts()).isTrue();
        assertThat(new BlockList(block2, block3).hasConflicts()).isFalse();
        assertThat(new BlockList(block3, block4).hasConflicts()).isTrue();
        assertThat(new BlockList(block4, block5).hasConflicts()).isFalse();
    }

    @Test
    public void testHasConflictsInPeriod() {
        assertThat(new BlockList().hasConflicts(week1)).isFalse();
        assertThat(new BlockList(block1, block2, block3).hasConflicts(week1)).isTrue();
        assertThat(new BlockList(block2, block3).hasConflicts(week1)).isFalse();
        assertThat(new BlockList(block3, block4).hasConflicts(week1)).isTrue();
        assertThat(new BlockList(block4, block5).hasConflicts(week1)).isFalse();

        assertThat(new BlockList().hasConflicts(week2)).isFalse();
        assertThat(new BlockList(block1, block2, block3).hasConflicts(week2)).isTrue();
        assertThat(new BlockList(block2, block3).hasConflicts(week2)).isFalse();
        assertThat(new BlockList(block3, block4).hasConflicts(week2)).isFalse();
        assertThat(new BlockList(block4, block5).hasConflicts(week2)).isFalse();
    }

    @Test
    public void testConflictsWith() {
        assertThat(new BlockList().conflictsWith(new BlockList())).isFalse();
        assertThat(new BlockList().conflictsWith(new BlockList(block3, block4))).isFalse();
        assertThat(new BlockList(block3, block4).conflictsWith(new BlockList())).isFalse();
        assertThat(new BlockList(block3).conflictsWith(new BlockList(block4))).isTrue();
        assertThat(new BlockList(block1, block2).conflictsWith(new BlockList(block3, block4))).isTrue();
    }

    @Test
    public void testConflictsWithInPeriod() {
        assertThat(new BlockList().conflictsWith(new BlockList(), week1)).isFalse();
        assertThat(new BlockList().conflictsWith(new BlockList(block3, block4), week1)).isTrue();
        assertThat(new BlockList(block3, block4).conflictsWith(new BlockList(), week1)).isTrue();
        assertThat(new BlockList(block3).conflictsWith(new BlockList(block4), week1)).isTrue();
        assertThat(new BlockList(block1, block2).conflictsWith(new BlockList(block3, block4), week1)).isTrue();
        assertThat(new BlockList(block3).conflictsWith(new BlockList(block4), week1)).isTrue();

        assertThat(new BlockList().conflictsWith(new BlockList(), week2)).isFalse();
        assertThat(new BlockList().conflictsWith(new BlockList(block3, block4), week2)).isFalse();
        assertThat(new BlockList(block3, block4).conflictsWith(new BlockList(), week2)).isFalse();
        assertThat(new BlockList(block3).conflictsWith(new BlockList(block4), week2)).isFalse();
        assertThat(new BlockList(block1, block2).conflictsWith(new BlockList(block3, block4), week2)).isTrue();
        assertThat(new BlockList(block3).conflictsWith(new BlockList(block4), week2)).isFalse();
    }

    @Test
    public void testFilter() {
        BlockFilter teacher2Filter = new BlockFilter.ActivityBlockFilter(new ActivityFilter.HasResource(teacher2));
        assertThat(allBlocks.filter(teacher2Filter)).containsExactly(block6);
        
        BlockFilter subject2Filter = new BlockFilter.ActivityBlockFilter(new ActivityFilter.HasTag(subject2));
        assertThat(allBlocks.filter(subject2Filter)).containsExactly(block2, block3, block4, block6);
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

}
