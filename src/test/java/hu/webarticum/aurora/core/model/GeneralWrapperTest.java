package hu.webarticum.aurora.core.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class GeneralWrapperTest {

    @Test
    public void testValue() {
        GeneralWrapper<Integer> numberWrapper = new GeneralWrapper<Integer>();
        
        assertThat(numberWrapper.isNull()).isTrue();
        assertThat(numberWrapper.get()).isNull();
        
        numberWrapper.set(4);
        
        assertThat(numberWrapper.isNull()).isFalse();
        assertThat(numberWrapper.get()).isEqualTo(4);
    }
    

}
