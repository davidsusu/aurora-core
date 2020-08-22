package hu.webarticum.aurora.core.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import java.util.Set;

import org.assertj.core.api.ThrowableAssert;
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
        assertThat(new Block().getLength()).isEqualTo(Block.DEFAULT_LENGTH);
        assertThatThrownBy(new ThrowableAssert.ThrowingCallable() { @Override public void call() throws Throwable {
            new Block(-99);
        }}).isInstanceOf(IllegalArgumentException.class);
    }
    
    @Test
    public void testHasConflicts() {
        assertThat(emptyBlock.hasConflicts()).isFalse();
        assertThat(emptyBlock.hasConflicts(week1)).isFalse();
        assertThat(emptyBlock.hasConflicts(week2)).isFalse();
        
        assertThat(independentBlock.hasConflicts()).isFalse();
        assertThat(independentBlock.hasConflicts(week1)).isFalse();
        assertThat(independentBlock.hasConflicts(week2)).isFalse();
        
        assertThat(normalBlock1.hasConflicts()).isFalse();
        assertThat(normalBlock1.hasConflicts(week1)).isFalse();
        assertThat(normalBlock1.hasConflicts(week2)).isFalse();
        
        assertThat(normalBlock2.hasConflicts()).isFalse();
        assertThat(normalBlock2.hasConflicts(week1)).isFalse();
        assertThat(normalBlock2.hasConflicts(week2)).isFalse();
        
        assertThat(normalBlock3.hasConflicts()).isFalse();
        assertThat(normalBlock3.hasConflicts(week1)).isFalse();
        assertThat(normalBlock3.hasConflicts(week2)).isFalse();
        
        assertThat(normalBlock4.hasConflicts()).isFalse();
        assertThat(normalBlock4.hasConflicts(week1)).isFalse();
        assertThat(normalBlock4.hasConflicts(week2)).isFalse();
        
        assertThat(conflictingBlock.hasConflicts()).isTrue();
        assertThat(conflictingBlock.hasConflicts(week1)).isFalse();
        assertThat(conflictingBlock.hasConflicts(week2)).isTrue();
    }

    @Test
    public void testConflictsWith() {
        assertThat(emptyBlock.conflictsWith(emptyBlock)).isFalse();
        assertThat(emptyBlock.conflictsWith(independentBlock)).isFalse();
        assertThat(emptyBlock.conflictsWith(normalBlock1)).isFalse();
        assertThat(emptyBlock.conflictsWith(normalBlock2)).isFalse();
        assertThat(emptyBlock.conflictsWith(normalBlock3)).isFalse();
        assertThat(emptyBlock.conflictsWith(normalBlock4)).isFalse();
        assertThat(emptyBlock.conflictsWith(conflictingBlock)).isFalse();
        
        assertThat(independentBlock.conflictsWith(emptyBlock)).isFalse();
        assertThat(independentBlock.conflictsWith(independentBlock)).isFalse();
        assertThat(independentBlock.conflictsWith(normalBlock1)).isFalse();
        assertThat(independentBlock.conflictsWith(normalBlock2)).isFalse();
        assertThat(independentBlock.conflictsWith(normalBlock3)).isFalse();
        assertThat(independentBlock.conflictsWith(normalBlock4)).isFalse();
        assertThat(independentBlock.conflictsWith(conflictingBlock)).isFalse();

        assertThat(normalBlock1.conflictsWith(emptyBlock)).isFalse();
        assertThat(normalBlock1.conflictsWith(independentBlock)).isFalse();
        assertThat(normalBlock1.conflictsWith(normalBlock1)).isTrue();
        assertThat(normalBlock1.conflictsWith(normalBlock2)).isFalse();
        assertThat(normalBlock1.conflictsWith(normalBlock3)).isTrue();
        assertThat(normalBlock1.conflictsWith(normalBlock4)).isTrue();
        assertThat(normalBlock1.conflictsWith(conflictingBlock)).isTrue();

        assertThat(normalBlock2.conflictsWith(emptyBlock)).isFalse();
        assertThat(normalBlock2.conflictsWith(independentBlock)).isFalse();
        assertThat(normalBlock2.conflictsWith(normalBlock1)).isFalse();
        assertThat(normalBlock2.conflictsWith(normalBlock2)).isTrue();
        assertThat(normalBlock2.conflictsWith(normalBlock3)).isTrue();
        assertThat(normalBlock2.conflictsWith(normalBlock4)).isTrue();
        assertThat(normalBlock2.conflictsWith(conflictingBlock)).isTrue();

        assertThat(normalBlock3.conflictsWith(emptyBlock)).isFalse();
        assertThat(normalBlock3.conflictsWith(independentBlock)).isFalse();
        assertThat(normalBlock3.conflictsWith(normalBlock1)).isTrue();
        assertThat(normalBlock3.conflictsWith(normalBlock2)).isTrue();
        assertThat(normalBlock3.conflictsWith(normalBlock3)).isTrue();
        assertThat(normalBlock3.conflictsWith(normalBlock4)).isFalse();
        assertThat(normalBlock3.conflictsWith(conflictingBlock)).isTrue();

        assertThat(normalBlock4.conflictsWith(emptyBlock)).isFalse();
        assertThat(normalBlock4.conflictsWith(independentBlock)).isFalse();
        assertThat(normalBlock4.conflictsWith(normalBlock1)).isTrue();
        assertThat(normalBlock4.conflictsWith(normalBlock2)).isTrue();
        assertThat(normalBlock4.conflictsWith(normalBlock3)).isFalse();
        assertThat(normalBlock4.conflictsWith(normalBlock4)).isTrue();
        assertThat(normalBlock4.conflictsWith(conflictingBlock)).isTrue();

        assertThat(conflictingBlock.conflictsWith(emptyBlock)).isFalse();
        assertThat(conflictingBlock.conflictsWith(independentBlock)).isFalse();
        assertThat(conflictingBlock.conflictsWith(normalBlock1)).isTrue();
        assertThat(conflictingBlock.conflictsWith(normalBlock2)).isTrue();
        assertThat(conflictingBlock.conflictsWith(normalBlock3)).isTrue();
        assertThat(conflictingBlock.conflictsWith(normalBlock4)).isTrue();
        assertThat(conflictingBlock.conflictsWith(conflictingBlock)).isTrue();
    }

    @Test
    public void testActivityManagerOfEmptyBlock() {
        assertThat((Set<Period>) emptyBlock.getActivityManager().getPeriods()).isEmpty();
        assertThat(emptyBlock.getActivityManager().getActivities()).isEmpty();
        assertThat(emptyBlock.getActivityManager().getActivities(week1)).isEmpty();
        assertThat(emptyBlock.getActivityManager().getActivities(week2)).isEmpty();
        assertThat(emptyBlock.getActivityManager().getActivities(Arrays.asList(week1, week2))).isEmpty();
        assertThat(emptyBlock.getActivityManager().getActivities(Arrays.asList(week1, week2), true)).isEmpty();
        assertThat((Set<Period>) emptyBlock.getActivityManager().getResourcePeriods(class1)).isEmpty();
        assertThat((Set<Period>) emptyBlock.getActivityManager().getResourcePeriods(class2)).isEmpty();
        assertThat((Set<Period>) emptyBlock.getActivityManager().getResourcePeriods(teacher1)).isEmpty();
        assertThat((Set<Period>) emptyBlock.getActivityManager().getResourcePeriods(multiResource)).isEmpty();
        assertThat((Set<Period>) emptyBlock.getActivityManager().getResourceSubsetPeriods(
            new ResourceSubset.SplittingPart(class1.getSplittingManager().getSplittings().get(0).getParts().get(0))
        )).isEmpty();
        assertThat(emptyBlock.getActivityManager().hasPeriod(week1)).isFalse();
        assertThat(emptyBlock.getActivityManager().hasPeriod(week2)).isFalse();
    }

    @Test
    public void testActivityManagerOfIndependentBlock() {
        assertThat((Set<Period>) independentBlock.getActivityManager().getPeriods()).isEmpty();
        assertThat(independentBlock.getActivityManager().getActivities()).hasSize(1);
        assertThat(independentBlock.getActivityManager().getActivities(week1)).isEmpty();
        assertThat(independentBlock.getActivityManager().getActivities(week2)).isEmpty();
        assertThat(independentBlock.getActivityManager().getActivities(
            Arrays.asList(week1, week2)
        )).isEmpty();
        assertThat(independentBlock.getActivityManager().getActivities(
            Arrays.asList(week1, week2), true
        )).isEmpty();
        assertThat((Set<Period>) independentBlock.getActivityManager().getResourcePeriods(class1)).isEmpty();
        assertThat((Set<Period>) independentBlock.getActivityManager().getResourcePeriods(class2)).isEmpty();
        assertThat((Set<Period>) independentBlock.getActivityManager().getResourcePeriods(teacher1)).isEmpty();
        assertThat((Set<Period>) independentBlock.getActivityManager().getResourcePeriods(multiResource)).isEmpty();
        assertThat((Set<Period>) independentBlock.getActivityManager().getResourceSubsetPeriods(
            new ResourceSubset.SplittingPart(
                class1.getSplittingManager().getSplittings().get(0).getParts().get(0)
            )
        )).isEmpty();
        assertThat(independentBlock.getActivityManager().hasPeriod(week1)).isFalse();
        assertThat(independentBlock.getActivityManager().hasPeriod(week2)).isFalse();
    }

    @Test
    public void testActivityManagerOfNormalBlock1() {
        assertThat((Set<Period>) normalBlock1.getActivityManager().getPeriods()).containsExactly(week1, week2);
        assertThat(normalBlock1.getActivityManager().getActivities()).hasSize(1);
        assertThat(normalBlock1.getActivityManager().getActivities(week1)).hasSize(1);
        assertThat(normalBlock1.getActivityManager().getActivities(week2)).hasSize(1);
        assertThat(normalBlock1.getActivityManager().getActivities(
            Arrays.asList(week1, week2)
        )).hasSize(1);
        assertThat(normalBlock1.getActivityManager().getActivities(
            Arrays.asList(week1, week2), true
        )).hasSize(1);
        assertThat((Set<Period>) normalBlock1.getActivityManager().getResourcePeriods(class1)).containsExactly(week1, week2);
        assertThat((Set<Period>) normalBlock1.getActivityManager().getResourcePeriods(class2)).isEmpty();
        assertThat((Set<Period>) normalBlock1.getActivityManager().getResourcePeriods(teacher1)).containsExactly(week1, week2);
        assertThat((Set<Period>) normalBlock1.getActivityManager().getResourceSubsetPeriods(
            new ResourceSubset.SplittingPart(
                class1.getSplittingManager().getSplittings().get(0).getParts().get(0)
            )
        )).containsExactly(week1, week2);
        assertThat(normalBlock1.getActivityManager().hasPeriod(week1)).isTrue();
        assertThat(normalBlock1.getActivityManager().hasPeriod(week2)).isTrue();
    }

    @Test
    public void testActivityManagerOfNormalBlock2() {
        assertThat((Set<Period>) normalBlock2.getActivityManager().getPeriods()).containsExactly(week1, week2);
        assertThat(normalBlock2.getActivityManager().getActivities()).hasSize(2);
        assertThat(normalBlock2.getActivityManager().getActivities(week1)).hasSize(2);
        assertThat(normalBlock2.getActivityManager().getActivities(week2)).hasSize(2);
        assertThat(normalBlock2.getActivityManager().getActivities(
            Arrays.asList(week1, week2)
        )).hasSize(2);
        assertThat(normalBlock2.getActivityManager().getActivities(
            Arrays.asList(week1, week2), true
        )).hasSize(2);
        assertThat((Set<Period>) normalBlock2.getActivityManager().getResourcePeriods(class1)).isEmpty();
        assertThat((Set<Period>) normalBlock2.getActivityManager().getResourcePeriods(class2)).containsExactly(week1, week2);
        assertThat((Set<Period>) normalBlock2.getActivityManager().getResourcePeriods(teacher1)).isEmpty();
        assertThat((Set<Period>) normalBlock2.getActivityManager().getResourceSubsetPeriods(
            new ResourceSubset.SplittingPart(
                class1.getSplittingManager().getSplittings().get(0).getParts().get(0)
            )
        )).isEmpty();
        assertThat(normalBlock2.getActivityManager().hasPeriod(week1)).isTrue();
        assertThat(normalBlock2.getActivityManager().hasPeriod(week2)).isTrue();
    }

    @Test
    public void testActivityManagerOfNormalBlock3() {
        assertThat((Set<Period>) normalBlock3.getActivityManager().getPeriods()).containsExactly(week1);
        assertThat(normalBlock3.getActivityManager().getActivities()).hasSize(2);
        assertThat(normalBlock3.getActivityManager().getActivities(week1)).hasSize(2);
        assertThat(normalBlock3.getActivityManager().getActivities(week2)).isEmpty();
        assertThat(normalBlock3.getActivityManager().getActivities(
            Arrays.asList(week1, week2)
        )).hasSize(2);
        assertThat(normalBlock3.getActivityManager().getActivities(
            Arrays.asList(week1, week2), true
        )).isEmpty();
        assertThat((Set<Period>) normalBlock3.getActivityManager().getResourcePeriods(class1)).containsExactly(week1);
        assertThat((Set<Period>) normalBlock3.getActivityManager().getResourcePeriods(class2)).containsExactly(week1);
        assertThat((Set<Period>) normalBlock3.getActivityManager().getResourcePeriods(teacher1)).containsExactly(week1);
        assertThat((Set<Period>) normalBlock3.getActivityManager().getResourceSubsetPeriods(
            new ResourceSubset.SplittingPart(
                class1.getSplittingManager().getSplittings().get(0).getParts().get(0)
            )
        )).containsExactly(week1);
        assertThat(normalBlock3.getActivityManager().hasPeriod(week1)).isTrue();
        assertThat(normalBlock3.getActivityManager().hasPeriod(week2)).isFalse();
    }

    @Test
    public void testActivityManagerOfNormalBlock4() {
        assertThat((Set<Period>) normalBlock4.getActivityManager().getPeriods()).containsExactly(week2);
        assertThat(normalBlock4.getActivityManager().getActivities()).hasSize(2);
        assertThat(normalBlock4.getActivityManager().getActivities(week1)).isEmpty();
        assertThat(normalBlock4.getActivityManager().getActivities(week2)).hasSize(2);
        assertThat(normalBlock4.getActivityManager().getActivities(
            Arrays.asList(week1, week2)
        )).hasSize(2);
        assertThat(normalBlock4.getActivityManager().getActivities(
            Arrays.asList(week1, week2), true
        )).isEmpty();
        assertThat((Set<Period>) normalBlock4.getActivityManager().getResourcePeriods(class1)).containsExactly(week2);
        assertThat((Set<Period>) normalBlock4.getActivityManager().getResourcePeriods(class2)).containsExactly(week2);
        assertThat((Set<Period>) normalBlock4.getActivityManager().getResourcePeriods(teacher1)).containsExactly(week2);
        assertThat((Set<Period>) normalBlock4.getActivityManager().getResourceSubsetPeriods(
            new ResourceSubset.SplittingPart(
                class1.getSplittingManager().getSplittings().get(0).getParts().get(0)
            )
        )).containsExactly(week2);
        assertThat(normalBlock4.getActivityManager().hasPeriod(week1)).isFalse();
        assertThat(normalBlock4.getActivityManager().hasPeriod(week2)).isTrue();
    }

    @Test
    public void testActivityManagerOfConflictingBlock() {
        assertThat((Set<Period>) conflictingBlock.getActivityManager().getPeriods()).containsExactly(week1, week2);
        assertThat(conflictingBlock.getActivityManager().getActivities()).hasSize(2);
        assertThat(conflictingBlock.getActivityManager().getActivities(week1)).hasSize(1);
        assertThat(conflictingBlock.getActivityManager().getActivities(week2)).hasSize(2);
        assertThat(conflictingBlock.getActivityManager().getActivities(
            Arrays.asList(week1, week2)
        )).hasSize(2);
        assertThat(conflictingBlock.getActivityManager().getActivities(
            Arrays.asList(week1, week2), true
        )).hasSize(1);
        assertThat((Set<Period>) conflictingBlock.getActivityManager().getResourcePeriods(class1)).containsExactly(week2);
        assertThat((Set<Period>) conflictingBlock.getActivityManager().getResourcePeriods(class2)).containsExactly(week1, week2);
        assertThat((Set<Period>) conflictingBlock.getActivityManager().getResourcePeriods(teacher1)).isEmpty();
        assertThat((Set<Period>) conflictingBlock.getActivityManager().getResourceSubsetPeriods(
            new ResourceSubset.SplittingPart(
                class1.getSplittingManager().getSplittings().get(0).getParts().get(0)
            )
        )).containsExactly(week2);
        assertThat(conflictingBlock.getActivityManager().hasPeriod(week1)).isTrue();
        assertThat(conflictingBlock.getActivityManager().hasPeriod(week2)).isTrue();
    }
    
    @Test
    public void testGetCalculatedTimeLimit() {
        assertThat(emptyBlock.getCalculatedTimeLimit().isAlways()).isTrue();
        assertThat(normalBlock1.getCalculatedTimeLimit().isAlways()).isTrue();
        assertThat(normalBlock2.getCalculatedTimeLimit().getSimplified()).isEqualTo(
            new Interval(0, (Time.HOUR * 13) + Block.DEFAULT_LENGTH - 1)
        );
        assertThat(normalBlock3.getCalculatedTimeLimit().isAlways()).isTrue();
        assertThat(normalBlock4.getCalculatedTimeLimit().isAlways()).isTrue();
        assertThat(conflictingBlock.getCalculatedTimeLimit().getSimplified()).isEqualTo(
            new Interval(0, (Time.HOUR * 13) + Block.DEFAULT_LENGTH - 1)
        );
    }

    @Test
    public void testGetCalculatedTimingSetUnlimited() {
        assertThat(emptyBlock.getCalculatedTimingSet(false).isEmpty()).isTrue();
        assertThat(independentBlock.getCalculatedTimingSet(false).isEmpty()).isTrue();
        assertThat(normalBlock1.getCalculatedTimingSet(false).getTimes()).isEqualTo(defaultTimingSet.getTimes());
        assertThat(normalBlock2.getCalculatedTimingSet(false).getTimes()).isEqualTo(secondaryTimingSet.getTimes());
        assertThat(normalBlock3.getCalculatedTimingSet(false).getTimes()).isEqualTo(defaultTimingSet.getTimes());
        assertThat(normalBlock4.getCalculatedTimingSet(false).getTimes()).isEqualTo(secondaryTimingSet.getTimes());
        assertThat(conflictingBlock.getCalculatedTimingSet(false).getTimes()).isEqualTo(secondaryTimingSet.getTimes());
    }

    @Test
    public void testGetCalculatedTimingSet() {
        assertThat(emptyBlock.getCalculatedTimingSet().isEmpty()).isTrue();
        assertThat(independentBlock.getCalculatedTimingSet().isEmpty()).isTrue();
        assertThat(normalBlock1.getCalculatedTimingSet().getTimes()).isEqualTo(defaultTimingSet.getTimes());
        assertThat(normalBlock2.getCalculatedTimingSet().getTimes()).containsExactly(new Time[] { // NOSONAR
            new Time(9 * Time.HOUR),
            new Time(10 * Time.HOUR),
            new Time(11 * Time.HOUR),
            new Time(12 * Time.HOUR),
        });
        assertThat(normalBlock3.getCalculatedTimingSet().getTimes()).isEqualTo(defaultTimingSet.getTimes());
        assertThat(normalBlock4.getCalculatedTimingSet().getTimes()).isEqualTo(secondaryTimingSet.getTimes());
        assertThat(conflictingBlock.getCalculatedTimingSet().getTimes()).containsExactly(new Time[] { // NOSONAR
            new Time(9 * Time.HOUR),
            new Time(10 * Time.HOUR),
            new Time(11 * Time.HOUR),
            new Time(12 * Time.HOUR),
        });
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
    
}
