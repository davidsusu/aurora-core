package hu.webarticum.aurora.core.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

public class ColorTest {

    @Test
    public void testNumericConstructors() {
        assertEquals(Color.BLACK, new Color());
        assertEquals(Color.RED, new Color(0xFF0000));
        assertEquals(Color.GREEN, new Color(0x00FF00));
        assertEquals(Color.BLUE, new Color(0x0000FF));
        assertEquals(Color.YELLOW, new Color(0xFFFF00));
        assertNotEquals(Color.RED, new Color(0xFFFF00));
        assertEquals(0xFF, new Color(0xFFFF00).getRed());
        assertEquals(0xFF, new Color(0xFFFF00).getGreen());
        assertEquals(0, new Color(0xFFFF00).getBlue());
        assertEquals(Color.BLUE, new Color(Color.Component.BLUE, 0xFF));
        assertEquals(Color.PURPLE, new Color(0xFF, 0, 0xFF));
        try {
            new Color(Color.Component.BLUE, 0x123);
            fail();
        } catch (IllegalArgumentException e) {
        }
        try {
            new Color(0x111, 0, 0xFF);
            fail();
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testStringConstructor() {
        assertEquals(Color.BLACK, new Color("foo-bar"));
        assertEquals(Color.BLACK, new Color("#1234567"));
        assertEquals(0xC730B2, new Color("#C730B2").toInt());
        assertEquals("#C130B2", new Color("#C130B2").getHexa());
        assertEquals(0x55FF33, new Color("#5F3").toInt());
    }

    @Test
    public void testBlackWhite() {
        testBlackWhiteProperties(true, Color.BLACK);
        testBlackWhiteProperties(false, Color.WHITE);
        testBlackWhiteProperties(true, Color.BLUE);
        testBlackWhiteProperties(false, Color.YELLOW);
        testBlackWhiteProperties(true, new Color(0x110131));
        testBlackWhiteProperties(false, new Color(0xC7F2DA));
    }
    
    private void testBlackWhiteProperties(boolean expectedDark, Color color) {
        assertEquals(expectedDark, color.isDark());
        assertEquals(expectedDark ? Color.BLACK : Color.WHITE, color.getBlackWhiteColor());
        assertEquals(expectedDark ? Color.WHITE : Color.BLACK, color.getBlackWhiteContrastColor());
    }

    @Test
    public void testGetContrastColor() {
        assertEquals(Color.BLACK, Color.WHITE.getContrastColor());
        assertEquals(Color.WHITE, Color.BLACK.getContrastColor());
        assertEquals(Color.WHITE, Color.GREY.getContrastColor());
        assertEquals(Color.CYAN, Color.RED.getContrastColor());
        assertEquals(Color.PURPLE, Color.GREEN.getContrastColor());
        assertEquals(Color.YELLOW, Color.BLUE.getContrastColor());
        assertEquals(Color.BLUE, Color.YELLOW.getContrastColor());
        assertEquals(Color.GREEN, Color.PURPLE.getContrastColor());
        assertEquals(Color.RED, Color.CYAN.getContrastColor());
        assertEquals(Color.RED, new Color(0x32F3C9).getContrastColor());
        assertEquals(Color.BLACK, new Color(0xC5B9EC).getContrastColor());
    }

    @Test
    public void testAvg() {
        assertEquals(Color.BLACK, Color.avg());
        assertEquals(new Color(0x123456), Color.avg(
            new Color(0x100111), new Color(0x1176C7), new Color(0x15252A)
        ));
        assertEquals(new Color(0xC50097), Color.avg(
            new Color(0xA2002E), new Color(0xFF00C5), new Color(0xAE00D2)
        ));
    }
    
}
