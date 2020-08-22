package hu.webarticum.aurora.core.model.time;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class AlwaysTimeLimitTest {

    @Test
    public void testBasics() {
        AlwaysTimeLimit always = new AlwaysTimeLimit();
        
        assertThat(always.isAlways()).isTrue();
        assertThat(always.isNever()).isFalse();
        assertThat(always.getTimes()).isEmpty();
        assertThat(always.getTimes(false)).isEmpty();
        assertThat(always.getTimes(true)).isEmpty();
    }

}
