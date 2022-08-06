package hu.webarticum.aurora.core.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import hu.webarticum.aurora.core.model.time.Interval;
import hu.webarticum.aurora.core.model.time.Time;

class ActivityFlowTest {

    @Test
    void testEmpty() {
        ActivityFlow activityFlow = new ActivityFlow();
        
        assertThat(activityFlow.isEmpty()).isTrue();
        assertThat(activityFlow.size()).isZero();
        for (ActivityFlow.Entry entry : activityFlow) {
            fail("Unexpected entry: " + entry);
        }
        assertThat(activityFlow.getActivities()).isEmpty();
    }
    
    @Test
    void testBounds() {
        Period period = new Period();
        List<Period> periods = new ArrayList<Period>(Arrays.asList(period));
        ActivityFlow activityFlow = new ActivityFlow();
        activityFlow.add(new Activity(), new Interval(150, 250), periods);
        activityFlow.add(new Activity(), new Interval(100, 200), periods);
        activityFlow.add(new Activity(), new Interval(300, 350), periods);
        activityFlow.add(new Activity(), new Interval(400, 500), periods);
        activityFlow.add(new Activity(), new Interval(420, 490), periods);
        
        assertThat(activityFlow.getStartTime()).isEqualTo(new Time(100));
        assertThat(activityFlow.getLastTime()).isEqualTo(new Time(420));
        assertThat(activityFlow.getEndTime()).isEqualTo(new Time(500));
    }

    @Test
    void testGetPeriods() {
        List<Period> periods = new ArrayList<Period>(Arrays.asList(
            new Period("Period 1a", 1, 1),
            new Period("Period 1b", 1, 1),
            new Period("Period 2/1", 2, 1),
            new Period("Period 2/2", 2, 2)
        ));
        ActivityFlow activityFlow = new ActivityFlow();
        activityFlow.add(new Activity("Activity 1"), new Interval(100, 200), periods.subList(0, 1));
        activityFlow.add(new Activity("Activity 2"), new Interval(100, 200), periods.subList(0, 1));
        activityFlow.add(new Activity("Activity 3"), new Interval(100, 200), periods.subList(1, 2));
        activityFlow.add(new Activity("Activity 4"), new Interval(100, 200), periods.subList(1, 3));
        activityFlow.add(new Activity("Activity 5"), new Interval(100, 200), periods.subList(2, 3));
        activityFlow.add(new Activity("Activity 6"), new Interval(100, 200), periods.subList(2, 4));
        
        assertThat((Object) activityFlow.getPeriods()).isEqualTo(new PeriodSet(periods));
    }
    
    @Test
    void testFilter() {
        Period period = new Period();
        List<Period> periods = new ArrayList<Period>(Arrays.asList(period));
        Tag tag1 = new Tag("Tag1");
        Tag tag2 = new Tag("Tag2");
        Activity activity1 = createSimpleActivity("Activity 1", tag1);
        Activity activity2 = createSimpleActivity("Activity 2", tag2);
        Activity activity3 = createSimpleActivity("Activity 3");
        Activity activity4 = createSimpleActivity("Activity 4", tag1, tag2);
        Activity activity5 = createSimpleActivity("Activity 5", tag2);
        ActivityFilter activityFilter = new ActivityFilter.HasTag(tag2);
        ActivityFlow activityFlow = new ActivityFlow();
        activityFlow.add(activity1, new Interval(100, 200), periods);
        activityFlow.add(activity2, new Interval(200, 300), periods);
        activityFlow.add(activity3, new Interval(300, 400), periods);
        activityFlow.add(activity4, new Interval(400, 500), periods);
        activityFlow.add(activity5, new Interval(500, 600), periods);
        ActivityFlow filteredActivityFlow = activityFlow.filter(new ActivityFlow.ActivityEntryFilter(activityFilter));
        Set<Activity> filteredActivities = new HashSet<Activity>(filteredActivityFlow.getActivities());
        Set<Activity> expectedFilteredActivities = new HashSet<Activity>(Arrays.asList(activity2, activity4, activity5));
        
        assertThat(filteredActivities).isEqualTo(expectedFilteredActivities);
    }
    
    @Test
    void testGetLimited() {
        Period period = new Period();
        List<Period> periods = new ArrayList<Period>(Arrays.asList(period));
        ActivityFlow activityFlow = new ActivityFlow();
        Activity activity1 = createSimpleActivity("Activity 1");
        Activity activity2 = createSimpleActivity("Activity 2");
        Activity activity3 = createSimpleActivity("Activity 3");
        Activity activity4 = createSimpleActivity("Activity 4");
        Activity activity5 = createSimpleActivity("Activity 5");
        activityFlow.add(activity1, new Interval(150, 250), periods);
        activityFlow.add(activity2, new Interval(100, 200), periods);
        activityFlow.add(activity3, new Interval(300, 350), periods);
        activityFlow.add(activity4, new Interval(400, 500), periods);
        activityFlow.add(activity5, new Interval(420, 490), periods);
        
        assertThat(activityFlow.getLimited(new Interval(0, 10)).isEmpty()).isTrue();

        ActivityFlow limitedActivityFlow = activityFlow.getLimited(new Interval(150, 495));
        Set<Activity> limitedActivities = new HashSet<Activity>(limitedActivityFlow.getActivities());
        Set<Activity> expectedLimitedActivities = new HashSet<Activity>(Arrays.asList(activity1, activity3, activity5));
        
        assertThat(limitedActivities).isEqualTo(expectedLimitedActivities);
        assertThat(limitedActivityFlow.getStartTime()).isEqualTo(new Time(150));
        assertThat(limitedActivityFlow.getLastTime()).isEqualTo(new Time(420));
        assertThat(limitedActivityFlow.getEndTime()).isEqualTo(new Time(490));
    }

    @Test
    void testGetLimitedIntersecting() {
        Period period = new Period();
        List<Period> periods = new ArrayList<Period>(Arrays.asList(period));
        ActivityFlow activityFlow = new ActivityFlow();
        Activity activity1 = createSimpleActivity("Activity 1");
        Activity activity2 = createSimpleActivity("Activity 2");
        Activity activity3 = createSimpleActivity("Activity 3");
        Activity activity4 = createSimpleActivity("Activity 4");
        Activity activity5 = createSimpleActivity("Activity 5");
        activityFlow.add(activity1, new Interval(150, 250), periods);
        activityFlow.add(activity2, new Interval(100, 200), periods);
        activityFlow.add(activity3, new Interval(300, 350), periods);
        activityFlow.add(activity4, new Interval(400, 500), periods);
        activityFlow.add(activity5, new Interval(420, 490), periods);
        
        assertThat(activityFlow.getIntersecting(new Interval(0, 10)).isEmpty()).isTrue();

        ActivityFlow intersectingActivityFlow = activityFlow.getIntersecting(new Interval(220, 420));
        Set<Activity> intersectingActivities = new HashSet<Activity>(intersectingActivityFlow.getActivities());
        Set<Activity> expectedIntersectingActivities = new HashSet<Activity>(Arrays.asList(
            activity1, activity3, activity4
        ));
        
        assertThat(intersectingActivities).isEqualTo(expectedIntersectingActivities);
        assertThat(intersectingActivityFlow.getStartTime()).isEqualTo(new Time(150));
        assertThat(intersectingActivityFlow.getLastTime()).isEqualTo(new Time(400));
        assertThat(intersectingActivityFlow.getEndTime()).isEqualTo(new Time(500));
    }

    @Test
    void testGetThinned() {
        List<Period> periods = new ArrayList<Period>(Arrays.asList(
            new Period("Period 1a", 1, 1),
            new Period("Period 1b", 1, 1),
            new Period("Period 2/1", 2, 1),
            new Period("Period 2/2", 2, 2)
        ));
        Activity activity1 = new Activity("Activity 1");
        Activity activity2 = new Activity("Activity 2");
        Activity activity3 = new Activity("Activity 3");
        Activity activity4 = new Activity("Activity 4");
        Activity activity5 = new Activity("Activity 5");
        Activity activity6 = new Activity("Activity 6");
        Activity activity7 = new Activity("Activity 7");
        ActivityFlow activityFlow = new ActivityFlow();
        activityFlow.add(activity1, new Interval(100, 200), periods.subList(0, 1));
        activityFlow.add(activity2, new Interval(100, 200), periods.subList(0, 1));
        activityFlow.add(activity3, new Interval(100, 200), periods.subList(1, 2));
        activityFlow.add(activity4, new Interval(100, 200), periods.subList(1, 3));
        activityFlow.add(activity5, new Interval(100, 200), periods.subList(2, 3));
        activityFlow.add(activity6, new Interval(100, 200), periods.subList(2, 4));
        activityFlow.add(activity7, new Interval(100, 200),
            Arrays.asList(periods.get(1), periods.get(3))
        );
        
        assertThat(activityFlow.getThinned(new Period()).isEmpty()).isTrue();
        assertThat(activityFlow.getThinned(periods.get(1)).getActivities()).containsExactly(
            activity3, activity4, activity7
        );
        assertThat(activityFlow.getThinned(Arrays.asList(
            periods.get(0), periods.get(3)
        )).getActivities()).containsExactly(
            activity1, activity2, activity6, activity7
        );
    }
    
    private Activity createSimpleActivity(String label, Tag... tags) {
        Activity activity = new Activity(label);
        Activity.TagManager tagManager = activity.getTagManager();
        for (Tag tag : tags) {
            tagManager.add(tag);
        }
        return activity;
    }
    
}
