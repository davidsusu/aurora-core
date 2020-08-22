package hu.webarticum.aurora.core.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import org.junit.Test;


public class MultiComparatorTest {

    @Test
    public void testSort() {
        TreeSet<String> strings = new TreeSet<String>(new MultiComparator<String>(new Comparator<String>() {

            @Override
            public int compare(String string1, String string2) {
                return string1.compareTo(string2);
            }
            
        }, true));
        
        String reference1 = new String("text1");
        String reference2 = new String("text2");
        strings.add(reference1);
        strings.add(reference2);
        strings.add(new String("text2"));
        strings.add(reference1);
        strings.add(new String("text3"));
        strings.add(new String("text4"));
        strings.add(new String("asdf"));
        strings.add(new String("zzzz"));
        
        List<String> resultList = new ArrayList<String>(strings);
        List<String> expectedList = Arrays.asList("asdf", "text1", "text2", "text3", "text4", "zzzz");
        
        assertThat(resultList).isEqualTo(expectedList);
    }

    @Test
    public void testSortStrict() {
        TreeSet<String> strings = new TreeSet<String>(new MultiComparator<String>(new Comparator<String>() {

            @Override
            public int compare(String string1, String string2) {
                return string1.compareTo(string2);
            }
            
        }, false));

        String reference1 = new String("text1");
        String reference2 = new String("text2");
        strings.add(reference1);
        strings.add(reference2);
        strings.add(new String("text2"));
        strings.add(reference1);
        strings.add(new String("text3"));
        strings.add(new String("text4"));
        strings.add(new String("asdf"));
        strings.add(new String("zzzz"));
        
        List<String> resultList = new ArrayList<String>(strings);
        List<String> expectedList = Arrays.asList("asdf", "text1", "text2", "text2", "text3", "text4", "zzzz");
        
        assertThat(resultList).isEqualTo(expectedList);
    }

}
