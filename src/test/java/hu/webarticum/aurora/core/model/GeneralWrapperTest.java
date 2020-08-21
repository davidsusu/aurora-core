package hu.webarticum.aurora.core.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class GeneralWrapperTest {

    @Test
    public void testValue() {
        GeneralWrapper<Integer> numberWrapper = new GeneralWrapper<Integer>();
        assertTrue(numberWrapper.isNull());
        assertNull(numberWrapper.get());
        
        numberWrapper.set(4);
        assertFalse(numberWrapper.isNull());
        assertEquals(Integer.valueOf(4), numberWrapper.get());
    }
    

}
