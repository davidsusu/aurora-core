package hu.webarticum.aurora.core.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.assertj.core.api.ThrowableAssert;
import org.junit.Test;

public class ColorTest {

    @Test
    public void testNumericConstructors() {
        assertThat(new Color()).isEqualTo(Color.BLACK);
        assertThat(new Color(0xFF0000)).isEqualTo(Color.RED);
        assertThat(new Color(0x00FF00)).isEqualTo(Color.GREEN);
        assertThat(new Color(0x0000FF)).isEqualTo(Color.BLUE);
        assertThat(new Color(0xFFFF00)).isEqualTo(Color.YELLOW);
        assertThat(new Color(0xFFFF00)).isNotEqualTo(Color.RED);
        assertThat(new Color(0xFFFF00).getRed()).isEqualTo(0xFF);
        assertThat(new Color(0xFFFF00).getGreen()).isEqualTo(0xFF);
        assertThat(new Color(0xFFFF00).getBlue()).isEqualTo(0);
        assertThat(new Color(Color.Component.BLUE, 0xFF)).isEqualTo(Color.BLUE);
        assertThat(new Color(0xFF, 0, 0xFF)).isEqualTo(Color.PURPLE);
        assertThatThrownBy(new ThrowableAssert.ThrowingCallable() { @Override public void call() throws Throwable {
            new Color(Color.Component.BLUE, 0x123);
        }}).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(new ThrowableAssert.ThrowingCallable() { @Override public void call() throws Throwable {
            new Color(0x111, 0, 0xFF);
        }}).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testStringConstructor() {
        assertThat(new Color("foo-bar")).isEqualTo(Color.BLACK);
        assertThat(new Color("#1234567")).isEqualTo(Color.BLACK);
        assertThat(new Color("#C730B2").toInt()).isEqualTo(0xC730B2);
        assertThat(new Color("#C130B2").getHexa()).isEqualTo("#C130B2");
        assertThat(new Color("#5F3").toInt()).isEqualTo(0x55FF33);
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
        assertThat(color.isDark()).isEqualTo(expectedDark);
        assertThat(color.getBlackWhiteColor()).isEqualTo(expectedDark ? Color.BLACK : Color.WHITE);
        assertThat(color.getBlackWhiteContrastColor()).isEqualTo(expectedDark ? Color.WHITE : Color.BLACK);
    }

    @Test
    public void testGetContrastColor() {
        assertThat(Color.WHITE.getContrastColor()).isEqualTo(Color.BLACK);
        assertThat(Color.BLACK.getContrastColor()).isEqualTo(Color.WHITE);
        assertThat(Color.GREY.getContrastColor()).isEqualTo(Color.WHITE);
        assertThat(Color.RED.getContrastColor()).isEqualTo(Color.CYAN);
        assertThat(Color.GREEN.getContrastColor()).isEqualTo(Color.PURPLE);
        assertThat(Color.BLUE.getContrastColor()).isEqualTo(Color.YELLOW);
        assertThat(Color.YELLOW.getContrastColor()).isEqualTo(Color.BLUE);
        assertThat(Color.PURPLE.getContrastColor()).isEqualTo(Color.GREEN);
        assertThat(Color.CYAN.getContrastColor()).isEqualTo(Color.RED);
        assertThat(new Color(0x32F3C9).getContrastColor()).isEqualTo(Color.RED);
        assertThat(new Color(0xC5B9EC).getContrastColor()).isEqualTo(Color.BLACK);
    }

    @Test
    public void testAvg() {
        assertThat(Color.avg()).isEqualTo(Color.BLACK);
        assertThat(Color.avg(
            new Color(0x100111), new Color(0x1176C7), new Color(0x15252A)
        )).isEqualTo(new Color(0x123456));
        assertThat(Color.avg(
            new Color(0xA2002E), new Color(0xFF00C5), new Color(0xAE00D2)
        )).isEqualTo(new Color(0xC50097));
    }
    
}
