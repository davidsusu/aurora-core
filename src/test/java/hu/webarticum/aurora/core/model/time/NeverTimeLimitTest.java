package hu.webarticum.aurora.core.model.time;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class NeverTimeLimitTest {

    @Test
    void testBasics() {
        NeverTimeLimit never = new NeverTimeLimit();
        
        assertThat(never.isAlways()).isFalse();
        assertThat(never.isNever()).isTrue();
        assertThat(never.getTimes()).isEmpty();
        assertThat(never.getTimes(false)).isEmpty();
        assertThat(never.getTimes(true)).isEmpty();
    }

}
