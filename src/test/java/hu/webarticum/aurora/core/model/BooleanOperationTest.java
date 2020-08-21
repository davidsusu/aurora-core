package hu.webarticum.aurora.core.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BooleanOperationTest {

    @Test
    public void testFalse() {
        assertFalse(BooleanOperation.FALSE.operate(false, false));
        assertFalse(BooleanOperation.FALSE.operate(false, true));
        assertFalse(BooleanOperation.FALSE.operate(true, false));
        assertFalse(BooleanOperation.FALSE.operate(true, true));
    }

    @Test
    public void testNor() {
        assertTrue(BooleanOperation.NOR.operate(false, false));
        assertFalse(BooleanOperation.NOR.operate(false, true));
        assertFalse(BooleanOperation.NOR.operate(true, false));
        assertFalse(BooleanOperation.NOR.operate(true, true));
    }

    @Test
    public void testXright() {
        assertFalse(BooleanOperation.XRIGHT.operate(false, false));
        assertTrue(BooleanOperation.XRIGHT.operate(false, true));
        assertFalse(BooleanOperation.XRIGHT.operate(true, false));
        assertFalse(BooleanOperation.XRIGHT.operate(true, true));
    }

    @Test
    public void testXleft() {
        assertFalse(BooleanOperation.XLEFT.operate(false, false));
        assertFalse(BooleanOperation.XLEFT.operate(false, true));
        assertTrue(BooleanOperation.XLEFT.operate(true, false));
        assertFalse(BooleanOperation.XLEFT.operate(true, true));
    }

    @Test
    public void testAnd() {
        assertFalse(BooleanOperation.AND.operate(false, false));
        assertFalse(BooleanOperation.AND.operate(false, true));
        assertFalse(BooleanOperation.AND.operate(true, false));
        assertTrue(BooleanOperation.AND.operate(true, true));
    }

    @Test
    public void testNLeft() {
        assertTrue(BooleanOperation.NLEFT.operate(false, false));
        assertTrue(BooleanOperation.NLEFT.operate(false, true));
        assertFalse(BooleanOperation.NLEFT.operate(true, false));
        assertFalse(BooleanOperation.NLEFT.operate(true, true));
    }

    @Test
    public void testNright() {
        assertTrue(BooleanOperation.NRIGHT.operate(false, false));
        assertFalse(BooleanOperation.NRIGHT.operate(false, true));
        assertTrue(BooleanOperation.NRIGHT.operate(true, false));
        assertFalse(BooleanOperation.NRIGHT.operate(true, true));
    }

    @Test
    public void testNxor() {
        assertTrue(BooleanOperation.NXOR.operate(false, false));
        assertFalse(BooleanOperation.NXOR.operate(false, true));
        assertFalse(BooleanOperation.NXOR.operate(true, false));
        assertTrue(BooleanOperation.NXOR.operate(true, true));
    }

    @Test
    public void testXor() {
        assertFalse(BooleanOperation.XOR.operate(false, false));
        assertTrue(BooleanOperation.XOR.operate(false, true));
        assertTrue(BooleanOperation.XOR.operate(true, false));
        assertFalse(BooleanOperation.XOR.operate(true, true));
    }

    @Test
    public void testRight() {
        assertFalse(BooleanOperation.RIGHT.operate(false, false));
        assertTrue(BooleanOperation.RIGHT.operate(false, true));
        assertFalse(BooleanOperation.RIGHT.operate(true, false));
        assertTrue(BooleanOperation.RIGHT.operate(true, true));
    }

    @Test
    public void testLeft() {
        assertFalse(BooleanOperation.LEFT.operate(false, false));
        assertFalse(BooleanOperation.LEFT.operate(false, true));
        assertTrue(BooleanOperation.LEFT.operate(true, false));
        assertTrue(BooleanOperation.LEFT.operate(true, true));
    }

    @Test
    public void testNand() {
        assertTrue(BooleanOperation.NAND.operate(false, false));
        assertTrue(BooleanOperation.NAND.operate(false, true));
        assertTrue(BooleanOperation.NAND.operate(true, false));
        assertFalse(BooleanOperation.NAND.operate(true, true));
    }

    @Test
    public void testNxleft() {
        assertTrue(BooleanOperation.NXLEFT.operate(false, false));
        assertTrue(BooleanOperation.NXLEFT.operate(false, true));
        assertFalse(BooleanOperation.NXLEFT.operate(true, false));
        assertTrue(BooleanOperation.NXLEFT.operate(true, true));
    }

    @Test
    public void testNxright() {
        assertTrue(BooleanOperation.NXRIGHT.operate(false, false));
        assertFalse(BooleanOperation.NXRIGHT.operate(false, true));
        assertTrue(BooleanOperation.NXRIGHT.operate(true, false));
        assertTrue(BooleanOperation.NXRIGHT.operate(true, true));
    }

    @Test
    public void testOr() {
        assertFalse(BooleanOperation.OR.operate(false, false));
        assertTrue(BooleanOperation.OR.operate(false, true));
        assertTrue(BooleanOperation.OR.operate(true, false));
        assertTrue(BooleanOperation.OR.operate(true, true));
    }

    @Test
    public void testTrue() {
        assertTrue(BooleanOperation.TRUE.operate(false, false));
        assertTrue(BooleanOperation.TRUE.operate(false, true));
        assertTrue(BooleanOperation.TRUE.operate(true, false));
        assertTrue(BooleanOperation.TRUE.operate(true, true));
    }

}
