package hu.webarticum.aurora.core.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class ActivityFilterTest {

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
    public void testFalse() {
        ActivityFilter falseFilter = new ActivityFilter.FalseActivityFilter();
        assertFalse(falseFilter.validate(activity1));
        assertFalse(falseFilter.validate(activity2));
        assertFalse(falseFilter.validate(activity3));
        assertFalse(falseFilter.validate(activity4));
    }

    @Test
    public void testTrue() {
        ActivityFilter trueFilter = new ActivityFilter.TrueActivityFilter();
        assertTrue(trueFilter.validate(activity1));
        assertTrue(trueFilter.validate(activity2));
        assertTrue(trueFilter.validate(activity3));
        assertTrue(trueFilter.validate(activity4));
    }

    @Test
    public void testTagType() {
        ActivityFilter subjectFilter = new ActivityFilter.HasTagType(Tag.Type.SUBJECT);
        assertTrue(subjectFilter.validate(activity1));
        assertTrue(subjectFilter.validate(activity2));
        assertTrue(subjectFilter.validate(activity3));
        assertTrue(subjectFilter.validate(activity4));
        
        ActivityFilter languageFilter = new ActivityFilter.HasTagType(Tag.Type.LANGUAGE);
        assertFalse(languageFilter.validate(activity1));
        assertFalse(languageFilter.validate(activity2));
        assertFalse(languageFilter.validate(activity3));
        assertFalse(languageFilter.validate(activity4));
    }
    
    @Test
    public void testTag() {
        ActivityFilter subject1Filter = new ActivityFilter.HasTag(subject1);
        assertTrue(subject1Filter.validate(activity1));
        assertFalse(subject1Filter.validate(activity2));
        assertTrue(subject1Filter.validate(activity3));
        assertFalse(subject1Filter.validate(activity4));
        
        ActivityFilter subject2Filter = new ActivityFilter.HasTag(subject2);
        assertTrue(subject2Filter.validate(activity1));
        assertTrue(subject2Filter.validate(activity2));
        assertFalse(subject2Filter.validate(activity3));
        assertTrue(subject2Filter.validate(activity4));
    }

    @Test
    public void testResourceType() {
        ActivityFilter personFilter = new ActivityFilter.HasResourceType(Resource.Type.PERSON);
        assertTrue(personFilter.validate(activity1));
        assertTrue(personFilter.validate(activity2));
        assertTrue(personFilter.validate(activity3));
        assertTrue(personFilter.validate(activity4));
        
        ActivityFilter objectFilter = new ActivityFilter.HasResourceType(Resource.Type.OBJECT);
        assertFalse(objectFilter.validate(activity1));
        assertFalse(objectFilter.validate(activity2));
        assertTrue(objectFilter.validate(activity3));
        assertFalse(objectFilter.validate(activity4));
        
        ActivityFilter otherFilter = new ActivityFilter.HasResourceType(Resource.Type.OTHER);
        assertFalse(otherFilter.validate(activity1));
        assertFalse(otherFilter.validate(activity2));
        assertFalse(otherFilter.validate(activity3));
        assertFalse(otherFilter.validate(activity4));
    }

    @Test
    public void testResource() {
        ActivityFilter class1Filter = new ActivityFilter.HasResource(class1);
        assertTrue(class1Filter.validate(activity1));
        assertFalse(class1Filter.validate(activity2));
        assertTrue(class1Filter.validate(activity3));
        assertTrue(class1Filter.validate(activity4));
        
        ActivityFilter teacher2Filter = new ActivityFilter.HasResource(teacher2);
        assertFalse(teacher2Filter.validate(activity1));
        assertFalse(teacher2Filter.validate(activity2));
        assertFalse(teacher2Filter.validate(activity3));
        assertTrue(teacher2Filter.validate(activity4));
        
        ActivityFilter unknownFilter = new ActivityFilter.HasResource(new Resource());
        assertFalse(unknownFilter.validate(activity1));
        assertFalse(unknownFilter.validate(activity2));
        assertFalse(unknownFilter.validate(activity3));
        assertFalse(unknownFilter.validate(activity4));
    }

    @Test
    public void testIntersectsResourceSplittingPart() {
        ActivityFilter class1PartFilter = new ActivityFilter.IntersectsResourceSplittingPart(
            class1.getSplittingManager().getSplittings().get(0).getParts().get(0)
        );
        assertTrue(class1PartFilter.validate(activity1));
        assertFalse(class1PartFilter.validate(activity2));
        assertTrue(class1PartFilter.validate(activity3));
        assertFalse(class1PartFilter.validate(activity4));

        ActivityFilter class2PartFilter = new ActivityFilter.IntersectsResourceSplittingPart(
            class1.getSplittingManager().getSplittings().get(0).getParts().get(1)
        );
        assertTrue(class2PartFilter.validate(activity1));
        assertFalse(class2PartFilter.validate(activity2));
        assertFalse(class2PartFilter.validate(activity3));
        assertTrue(class2PartFilter.validate(activity4));
    }

    @Test
    public void testComplex() {
        ActivityFilter complexActivityFilter = new ActivityFilter.And(
            new ActivityFilter.HasResource(teacher1),
            new ActivityFilter.Not(new ActivityFilter.Or(
                new ActivityFilter.HasResource(class2),
                new ActivityFilter.HasResource(object)
            ))
        );
        assertTrue(complexActivityFilter.validate(activity1));
        assertFalse(complexActivityFilter.validate(activity2));
        assertFalse(complexActivityFilter.validate(activity3));
        assertFalse(complexActivityFilter.validate(activity4));
    }
    
    
    @Before
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
