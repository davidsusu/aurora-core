package hu.webarticum.aurora.core.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ActivityFilterTest {

    private Tag subject1;
    private Tag subject2;
    
    private Resource class1;
    private Resource class2;
    private Resource teacher1;
    private Resource teacher2;
    private Resource object;

    private Activity activity1;
    private Activity activity2;
    private Activity activity3;
    private Activity activity4;
    
    
    @Test
    void testFalse() {
        ActivityFilter falseFilter = new ActivityFilter.FalseActivityFilter();
        assertThat(falseFilter.validate(activity1)).isFalse();
        assertThat(falseFilter.validate(activity2)).isFalse();
        assertThat(falseFilter.validate(activity3)).isFalse();
        assertThat(falseFilter.validate(activity4)).isFalse();
    }

    @Test
    void testTrue() {
        ActivityFilter trueFilter = new ActivityFilter.TrueActivityFilter();
        assertThat(trueFilter.validate(activity1)).isTrue();
        assertThat(trueFilter.validate(activity2)).isTrue();
        assertThat(trueFilter.validate(activity3)).isTrue();
        assertThat(trueFilter.validate(activity4)).isTrue();
    }

    @Test
    void testTagType() {
        ActivityFilter subjectFilter = new ActivityFilter.HasTagType(Tag.Type.SUBJECT);
        assertThat(subjectFilter.validate(activity1)).isTrue();
        assertThat(subjectFilter.validate(activity2)).isTrue();
        assertThat(subjectFilter.validate(activity3)).isTrue();
        assertThat(subjectFilter.validate(activity4)).isTrue();
        
        ActivityFilter languageFilter = new ActivityFilter.HasTagType(Tag.Type.LANGUAGE);
        assertThat(languageFilter.validate(activity1)).isFalse();
        assertThat(languageFilter.validate(activity2)).isFalse();
        assertThat(languageFilter.validate(activity3)).isFalse();
        assertThat(languageFilter.validate(activity4)).isFalse();
    }
    
    @Test
    void testTag() {
        ActivityFilter subject1Filter = new ActivityFilter.HasTag(subject1);
        assertThat(subject1Filter.validate(activity1)).isTrue();
        assertThat(subject1Filter.validate(activity2)).isFalse();
        assertThat(subject1Filter.validate(activity3)).isTrue();
        assertThat(subject1Filter.validate(activity4)).isFalse();
        
        ActivityFilter subject2Filter = new ActivityFilter.HasTag(subject2);
        assertThat(subject2Filter.validate(activity1)).isTrue();
        assertThat(subject2Filter.validate(activity2)).isTrue();
        assertThat(subject2Filter.validate(activity3)).isFalse();
        assertThat(subject2Filter.validate(activity4)).isTrue();
    }

    @Test
    void testResourceType() {
        ActivityFilter personFilter = new ActivityFilter.HasResourceType(Resource.Type.PERSON);
        assertThat(personFilter.validate(activity1)).isTrue();
        assertThat(personFilter.validate(activity2)).isTrue();
        assertThat(personFilter.validate(activity3)).isTrue();
        assertThat(personFilter.validate(activity4)).isTrue();
        
        ActivityFilter objectFilter = new ActivityFilter.HasResourceType(Resource.Type.OBJECT);
        assertThat(objectFilter.validate(activity1)).isFalse();
        assertThat(objectFilter.validate(activity2)).isFalse();
        assertThat(objectFilter.validate(activity3)).isTrue();
        assertThat(objectFilter.validate(activity4)).isFalse();
        
        ActivityFilter otherFilter = new ActivityFilter.HasResourceType(Resource.Type.OTHER);
        assertThat(otherFilter.validate(activity1)).isFalse();
        assertThat(otherFilter.validate(activity2)).isFalse();
        assertThat(otherFilter.validate(activity3)).isFalse();
        assertThat(otherFilter.validate(activity4)).isFalse();
    }

    @Test
    void testResource() {
        ActivityFilter class1Filter = new ActivityFilter.HasResource(class1);
        assertThat(class1Filter.validate(activity1)).isTrue();
        assertThat(class1Filter.validate(activity2)).isFalse();
        assertThat(class1Filter.validate(activity3)).isTrue();
        assertThat(class1Filter.validate(activity4)).isTrue();
        
        ActivityFilter teacher2Filter = new ActivityFilter.HasResource(teacher2);
        assertThat(teacher2Filter.validate(activity1)).isFalse();
        assertThat(teacher2Filter.validate(activity2)).isFalse();
        assertThat(teacher2Filter.validate(activity3)).isFalse();
        assertThat(teacher2Filter.validate(activity4)).isTrue();
        
        ActivityFilter unknownFilter = new ActivityFilter.HasResource(new Resource());
        assertThat(unknownFilter.validate(activity1)).isFalse();
        assertThat(unknownFilter.validate(activity2)).isFalse();
        assertThat(unknownFilter.validate(activity3)).isFalse();
        assertThat(unknownFilter.validate(activity4)).isFalse();
    }

    @Test
    void testIntersectsResourceSplittingPart() {
        ActivityFilter class1PartFilter = new ActivityFilter.IntersectsResourceSplittingPart(
            class1.getSplittingManager().getSplittings().get(0).getParts().get(0)
        );
        assertThat(class1PartFilter.validate(activity1)).isTrue();
        assertThat(class1PartFilter.validate(activity2)).isFalse();
        assertThat(class1PartFilter.validate(activity3)).isTrue();
        assertThat(class1PartFilter.validate(activity4)).isFalse();

        ActivityFilter class2PartFilter = new ActivityFilter.IntersectsResourceSplittingPart(
            class1.getSplittingManager().getSplittings().get(0).getParts().get(1)
        );
        assertThat(class2PartFilter.validate(activity1)).isTrue();
        assertThat(class2PartFilter.validate(activity2)).isFalse();
        assertThat(class2PartFilter.validate(activity3)).isFalse();
        assertThat(class2PartFilter.validate(activity4)).isTrue();
    }

    @Test
    void testComplex() {
        ActivityFilter complexActivityFilter = new ActivityFilter.And(
            new ActivityFilter.HasResource(teacher1),
            new ActivityFilter.Not(new ActivityFilter.Or(
                new ActivityFilter.HasResource(class2),
                new ActivityFilter.HasResource(object)
            ))
        );
        assertThat(complexActivityFilter.validate(activity1)).isTrue();
        assertThat(complexActivityFilter.validate(activity2)).isFalse();
        assertThat(complexActivityFilter.validate(activity3)).isFalse();
        assertThat(complexActivityFilter.validate(activity4)).isFalse();
    }
    
    
    @BeforeEach
    public void buildThings() {
        buildTags();
        buildResources();
        buildActivities();
    }

    private void buildTags() {
        subject1 = new Tag(Tag.Type.SUBJECT, "Mathematics");
        subject2 = new Tag(Tag.Type.SUBJECT, "Literature");
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

    private void buildActivities() {
        activity1 = new Activity();
        activity1.getTagManager().add(subject1);
        activity1.getTagManager().add(subject2);
        activity1.getResourceManager().add(teacher1);
        activity1.getResourceManager().add(class1);

        activity2 = new Activity();
        activity2.getTagManager().add(subject2);
        activity2.getResourceManager().add(teacher1);
        activity2.getResourceManager().add(class2);

        activity3 = new Activity();
        activity3.getTagManager().add(subject1);
        activity3.getResourceManager().add(teacher1);
        activity3.getResourceManager().add(object);
        activity3.getResourceManager().add(new ResourceSubset.SplittingPart(
            class1.getSplittingManager().getSplittings().get(0).getParts().get(0)
        ));

        activity4 = new Activity();
        activity4.getTagManager().add(subject2);
        activity4.getResourceManager().add(teacher2);
        activity4.getResourceManager().add(new ResourceSubset.SplittingPart(
            class1.getSplittingManager().getSplittings().get(0).getParts().get(1)
        ));
    }

}
