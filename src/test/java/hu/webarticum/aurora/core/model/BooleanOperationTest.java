package hu.webarticum.aurora.core.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class BooleanOperationTest {

    @Test
    public void testFalse() {
        assertThat(BooleanOperation.FALSE.operate(false, false)).isFalse();
        assertThat(BooleanOperation.FALSE.operate(false, true)).isFalse();
        assertThat(BooleanOperation.FALSE.operate(true, false)).isFalse();
        assertThat(BooleanOperation.FALSE.operate(true, true)).isFalse();
    }

    @Test
    public void testNor() {
        assertThat(BooleanOperation.NOR.operate(false, false)).isTrue();
        assertThat(BooleanOperation.NOR.operate(false, true)).isFalse();
        assertThat(BooleanOperation.NOR.operate(true, false)).isFalse();
        assertThat(BooleanOperation.NOR.operate(true, true)).isFalse();
    }

    @Test
    public void testXright() {
        assertThat(BooleanOperation.XRIGHT.operate(false, false)).isFalse();
        assertThat(BooleanOperation.XRIGHT.operate(false, true)).isTrue();
        assertThat(BooleanOperation.XRIGHT.operate(true, false)).isFalse();
        assertThat(BooleanOperation.XRIGHT.operate(true, true)).isFalse();
    }

    @Test
    public void testXleft() {
        assertThat(BooleanOperation.XLEFT.operate(false, false)).isFalse();
        assertThat(BooleanOperation.XLEFT.operate(false, true)).isFalse();
        assertThat(BooleanOperation.XLEFT.operate(true, false)).isTrue();
        assertThat(BooleanOperation.XLEFT.operate(true, true)).isFalse();
    }

    @Test
    public void testAnd() {
        assertThat(BooleanOperation.AND.operate(false, false)).isFalse();
        assertThat(BooleanOperation.AND.operate(false, true)).isFalse();
        assertThat(BooleanOperation.AND.operate(true, false)).isFalse();
        assertThat(BooleanOperation.AND.operate(true, true)).isTrue();
    }

    @Test
    public void testNLeft() {
        assertThat(BooleanOperation.NLEFT.operate(false, false)).isTrue();
        assertThat(BooleanOperation.NLEFT.operate(false, true)).isTrue();
        assertThat(BooleanOperation.NLEFT.operate(true, false)).isFalse();
        assertThat(BooleanOperation.NLEFT.operate(true, true)).isFalse();
    }

    @Test
    public void testNright() {
        assertThat(BooleanOperation.NRIGHT.operate(false, false)).isTrue();
        assertThat(BooleanOperation.NRIGHT.operate(false, true)).isFalse();
        assertThat(BooleanOperation.NRIGHT.operate(true, false)).isTrue();
        assertThat(BooleanOperation.NRIGHT.operate(true, true)).isFalse();
    }

    @Test
    public void testNxor() {
        assertThat(BooleanOperation.NXOR.operate(false, false)).isTrue();
        assertThat(BooleanOperation.NXOR.operate(false, true)).isFalse();
        assertThat(BooleanOperation.NXOR.operate(true, false)).isFalse();
        assertThat(BooleanOperation.NXOR.operate(true, true)).isTrue();
    }

    @Test
    public void testXor() {
        assertThat(BooleanOperation.XOR.operate(false, false)).isFalse();
        assertThat(BooleanOperation.XOR.operate(false, true)).isTrue();
        assertThat(BooleanOperation.XOR.operate(true, false)).isTrue();
        assertThat(BooleanOperation.XOR.operate(true, true)).isFalse();
    }

    @Test
    public void testRight() {
        assertThat(BooleanOperation.RIGHT.operate(false, false)).isFalse();
        assertThat(BooleanOperation.RIGHT.operate(false, true)).isTrue();
        assertThat(BooleanOperation.RIGHT.operate(true, false)).isFalse();
        assertThat(BooleanOperation.RIGHT.operate(true, true)).isTrue();
    }

    @Test
    public void testLeft() {
        assertThat(BooleanOperation.LEFT.operate(false, false)).isFalse();
        assertThat(BooleanOperation.LEFT.operate(false, true)).isFalse();
        assertThat(BooleanOperation.LEFT.operate(true, false)).isTrue();
        assertThat(BooleanOperation.LEFT.operate(true, true)).isTrue();
    }

    @Test
    public void testNand() {
        assertThat(BooleanOperation.NAND.operate(false, false)).isTrue();
        assertThat(BooleanOperation.NAND.operate(false, true)).isTrue();
        assertThat(BooleanOperation.NAND.operate(true, false)).isTrue();
        assertThat(BooleanOperation.NAND.operate(true, true)).isFalse();
    }

    @Test
    public void testNxleft() {
        assertThat(BooleanOperation.NXLEFT.operate(false, false)).isTrue();
        assertThat(BooleanOperation.NXLEFT.operate(false, true)).isTrue();
        assertThat(BooleanOperation.NXLEFT.operate(true, false)).isFalse();
        assertThat(BooleanOperation.NXLEFT.operate(true, true)).isTrue();
    }

    @Test
    public void testNxright() {
        assertThat(BooleanOperation.NXRIGHT.operate(false, false)).isTrue();
        assertThat(BooleanOperation.NXRIGHT.operate(false, true)).isFalse();
        assertThat(BooleanOperation.NXRIGHT.operate(true, false)).isTrue();
        assertThat(BooleanOperation.NXRIGHT.operate(true, true)).isTrue();
    }

    @Test
    public void testOr() {
        assertThat(BooleanOperation.OR.operate(false, false)).isFalse();
        assertThat(BooleanOperation.OR.operate(false, true)).isTrue();
        assertThat(BooleanOperation.OR.operate(true, false)).isTrue();
        assertThat(BooleanOperation.OR.operate(true, true)).isTrue();
    }

    @Test
    public void testTrue() {
        assertThat(BooleanOperation.TRUE.operate(false, false)).isTrue();
        assertThat(BooleanOperation.TRUE.operate(false, true)).isTrue();
        assertThat(BooleanOperation.TRUE.operate(true, false)).isTrue();
        assertThat(BooleanOperation.TRUE.operate(true, true)).isTrue();
    }

}
