package hu.webarticum.aurora.core.model;

import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(emptyActivityList.copy()).isNotSameAs(emptyActivityList);
        assertThat(emptyActivityList.copy()).isEqualTo(emptyActivityList);

        assertThat(normalActivityList1.copy()).isNotSameAs(normalActivityList1);
        assertThat(normalActivityList1.copy()).isEqualTo(normalActivityList1);

        assertThat(normalActivityList2.copy()).isNotSameAs(normalActivityList2);
        assertThat(normalActivityList2.copy()).isEqualTo(normalActivityList2);

        assertThat(normalActivityList3.copy()).isNotSameAs(normalActivityList3);
        assertThat(normalActivityList3.copy()).isEqualTo(normalActivityList3);

        assertThat(conflictingActivityList1.copy()).isNotSameAs(conflictingActivityList1);
        assertThat(conflictingActivityList1.copy()).isEqualTo(conflictingActivityList1);

        assertThat(conflictingActivityList2.copy()).isNotSameAs(conflictingActivityList2);
        assertThat(conflictingActivityList2.copy()).isEqualTo(conflictingActivityList2);
    }
    
    @Test
    public void testTags() {
        assertThat(emptyActivityList.getTags()).hasSize(0);
        assertThat(emptyActivityList.getTags(Tag.Type.SUBJECT)).hasSize(0);

        assertThat(normalActivityList1.getTags()).hasSize(1);
        assertThat(normalActivityList1.getTags(Tag.Type.SUBJECT)).hasSize(1);

        assertThat(normalActivityList2.getTags()).hasSize(2);
        assertThat(normalActivityList2.getTags(Tag.Type.SUBJECT)).hasSize(1);

        assertThat(normalActivityList3.getTags()).hasSize(1);
        assertThat(normalActivityList3.getTags(Tag.Type.SUBJECT)).hasSize(1);

        assertThat(conflictingActivityList1.getTags()).hasSize(1);
        assertThat(conflictingActivityList1.getTags(Tag.Type.SUBJECT)).hasSize(1);

        assertThat(conflictingActivityList2.getTags()).hasSize(3);
        assertThat(conflictingActivityList2.getTags(Tag.Type.SUBJECT)).hasSize(2);
    }
    
    @Test
    public void testResources() {
        assertThat(emptyActivityList.getResources()).hasSize(0);
        assertThat(emptyActivityList.getResources(Resource.Type.CLASS)).hasSize(0);
        assertThat(emptyActivityList.getResources(Resource.Type.PERSON)).hasSize(0);
        assertThat(emptyActivityList.getResources(Resource.Type.OBJECT)).hasSize(0);
        assertThat(emptyActivityList.getResourceSubsets()).hasSize(0);
        assertThat(emptyActivityList.getResourceSubsets(Resource.Type.CLASS)).hasSize(0);
        assertThat(emptyActivityList.getResourceSubsets(Resource.Type.PERSON)).hasSize(0);
        assertThat(emptyActivityList.getResourceSubsets(Resource.Type.OBJECT)).hasSize(0);

        assertThat(normalActivityList1.getResources()).hasSize(4);
        assertThat(normalActivityList1.getResources(Resource.Type.CLASS)).hasSize(2);
        assertThat(normalActivityList1.getResources(Resource.Type.PERSON)).hasSize(2);
        assertThat(normalActivityList1.getResources(Resource.Type.OBJECT)).hasSize(0);
        assertThat(normalActivityList1.getResourceSubsets()).hasSize(4);
        assertThat(normalActivityList1.getResourceSubsets(Resource.Type.CLASS)).hasSize(2);
        assertThat(normalActivityList1.getResourceSubsets(Resource.Type.PERSON)).hasSize(2);
        assertThat(normalActivityList1.getResourceSubsets(Resource.Type.OBJECT)).hasSize(0);

        assertThat(normalActivityList2.getResources()).hasSize(3);
        assertThat(normalActivityList2.getResources(Resource.Type.CLASS)).hasSize(1);
        assertThat(normalActivityList2.getResources(Resource.Type.PERSON)).hasSize(1);
        assertThat(normalActivityList2.getResources(Resource.Type.OBJECT)).hasSize(1);
        assertThat(normalActivityList2.getResourceSubsets()).hasSize(3);
        assertThat(normalActivityList2.getResourceSubsets(Resource.Type.CLASS)).hasSize(1);
        assertThat(normalActivityList2.getResourceSubsets(Resource.Type.PERSON)).hasSize(1);
        assertThat(normalActivityList2.getResourceSubsets(Resource.Type.OBJECT)).hasSize(1);

        assertThat(normalActivityList3.getResources()).hasSize(3);
        assertThat(normalActivityList3.getResources(Resource.Type.CLASS)).hasSize(1);
        assertThat(normalActivityList3.getResources(Resource.Type.PERSON)).hasSize(1);
        assertThat(normalActivityList3.getResources(Resource.Type.OBJECT)).hasSize(1);
        assertThat(normalActivityList3.getResourceSubsets()).hasSize(4);
        assertThat(normalActivityList3.getResourceSubsets(Resource.Type.CLASS)).hasSize(2);
        assertThat(normalActivityList3.getResourceSubsets(Resource.Type.PERSON)).hasSize(1);
        assertThat(normalActivityList3.getResourceSubsets(Resource.Type.OBJECT)).hasSize(1);

        assertThat(conflictingActivityList1.getResources()).hasSize(1);
        assertThat(conflictingActivityList1.getResources(Resource.Type.CLASS)).hasSize(0);
        assertThat(conflictingActivityList1.getResources(Resource.Type.PERSON)).hasSize(1);
        assertThat(conflictingActivityList1.getResources(Resource.Type.OBJECT)).hasSize(0);
        assertThat(conflictingActivityList1.getResourceSubsets()).hasSize(2);
        assertThat(conflictingActivityList1.getResourceSubsets(Resource.Type.CLASS)).hasSize(0);
        assertThat(conflictingActivityList1.getResourceSubsets(Resource.Type.PERSON)).hasSize(2);
        assertThat(conflictingActivityList1.getResourceSubsets(Resource.Type.OBJECT)).hasSize(0);

        assertThat(conflictingActivityList2.getResources()).hasSize(4);
        assertThat(conflictingActivityList2.getResources(Resource.Type.CLASS)).hasSize(2);
        assertThat(conflictingActivityList2.getResources(Resource.Type.PERSON)).hasSize(1);
        assertThat(conflictingActivityList2.getResources(Resource.Type.OBJECT)).hasSize(1);
        assertThat(conflictingActivityList2.getResourceSubsets()).hasSize(5);
        assertThat(conflictingActivityList2.getResourceSubsets(Resource.Type.CLASS)).hasSize(2);
        assertThat(conflictingActivityList2.getResourceSubsets(Resource.Type.PERSON)).hasSize(2);
        assertThat(conflictingActivityList2.getResourceSubsets(Resource.Type.OBJECT)).hasSize(1);
    }
    
    @Test
    public void testHasConflicts() {
        assertThat(emptyActivityList.hasConflicts()).isFalse();
        assertThat(normalActivityList1.hasConflicts()).isFalse();
        assertThat(normalActivityList2.hasConflicts()).isFalse();
        assertThat(normalActivityList3.hasConflicts()).isFalse();
        assertThat(conflictingActivityList1.hasConflicts()).isTrue();
        assertThat(conflictingActivityList2.hasConflicts()).isTrue();
    }
    
    @Test
    public void testConflictsWith() {
        assertThat(emptyActivityList.conflictsWith(emptyActivityList)).isFalse(); // NOSONAR
        assertThat(emptyActivityList.conflictsWith(normalActivityList1)).isFalse();
        assertThat(emptyActivityList.conflictsWith(normalActivityList2)).isFalse();
        assertThat(emptyActivityList.conflictsWith(normalActivityList3)).isFalse();
        assertThat(emptyActivityList.conflictsWith(conflictingActivityList1)).isFalse();
        assertThat(emptyActivityList.conflictsWith(conflictingActivityList2)).isFalse();

        assertThat(normalActivityList1.conflictsWith(emptyActivityList)).isFalse();
        assertThat(normalActivityList1.conflictsWith(normalActivityList1)).isTrue(); // NOSONAR
        assertThat(normalActivityList1.conflictsWith(normalActivityList2)).isTrue();
        assertThat(normalActivityList1.conflictsWith(normalActivityList3)).isTrue();
        assertThat(normalActivityList1.conflictsWith(conflictingActivityList1)).isTrue();
        assertThat(normalActivityList1.conflictsWith(conflictingActivityList2)).isTrue();

        assertThat(normalActivityList2.conflictsWith(emptyActivityList)).isFalse();
        assertThat(normalActivityList2.conflictsWith(normalActivityList1)).isTrue();
        assertThat(normalActivityList2.conflictsWith(normalActivityList2)).isTrue(); // NOSONAR
        assertThat(normalActivityList2.conflictsWith(normalActivityList3)).isFalse();
        assertThat(normalActivityList2.conflictsWith(conflictingActivityList1)).isFalse();
        assertThat(normalActivityList2.conflictsWith(conflictingActivityList2)).isTrue();

        assertThat(normalActivityList3.conflictsWith(emptyActivityList)).isFalse();
        assertThat(normalActivityList3.conflictsWith(normalActivityList1)).isTrue();
        assertThat(normalActivityList3.conflictsWith(normalActivityList2)).isFalse();
        assertThat(normalActivityList3.conflictsWith(normalActivityList3)).isTrue(); // NOSONAR
        assertThat(normalActivityList3.conflictsWith(conflictingActivityList1)).isTrue();
        assertThat(normalActivityList3.conflictsWith(conflictingActivityList2)).isTrue();

        assertThat(conflictingActivityList1.conflictsWith(emptyActivityList)).isFalse();
        assertThat(conflictingActivityList1.conflictsWith(normalActivityList1)).isTrue();
        assertThat(conflictingActivityList1.conflictsWith(normalActivityList2)).isFalse();
        assertThat(conflictingActivityList1.conflictsWith(normalActivityList3)).isTrue();
        assertThat(conflictingActivityList1.conflictsWith(conflictingActivityList1)).isTrue(); // NOSONAR
        assertThat(conflictingActivityList1.conflictsWith(conflictingActivityList2)).isFalse();

        assertThat(conflictingActivityList2.conflictsWith(emptyActivityList)).isFalse();
        assertThat(conflictingActivityList2.conflictsWith(normalActivityList1)).isTrue();
        assertThat(conflictingActivityList2.conflictsWith(normalActivityList2)).isTrue();
        assertThat(conflictingActivityList2.conflictsWith(normalActivityList3)).isTrue();
        assertThat(conflictingActivityList2.conflictsWith(conflictingActivityList1)).isFalse();
        assertThat(conflictingActivityList2.conflictsWith(conflictingActivityList2)).isTrue(); // NOSONAR
    }
    
    @Test
    public void testFilter() {
        ActivityList actual = normalActivityList3.filter(new ActivityFilter.HasResource(teacher2));
        ActivityList expected = new ActivityList(normalActivityList3.get(0));
        assertThat(actual).isEqualTo(expected);
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
