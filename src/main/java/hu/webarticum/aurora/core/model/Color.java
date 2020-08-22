package hu.webarticum.aurora.core.model;

import java.io.Serializable;
import java.util.Collection;

public class Color implements Serializable {

    private static final long serialVersionUID = 1L;

    
    public enum Component { RED, GREEN, BLUE }

    
    public static final Color WHITE = new Color(255, 255, 255);
    public static final Color BLACK = new Color(0, 0, 0);
    public static final Color GREY = new Color(127, 127, 127);
    public static final Color RED = new Color(255, 0, 0);
    public static final Color GREEN = new Color(0, 255, 0);
    public static final Color BLUE = new Color(0, 0, 255);
    public static final Color YELLOW = new Color(255, 255, 0);
    public static final Color PURPLE = new Color(255, 0, 255);
    public static final Color CYAN = new Color(0, 255, 255);
    
    
    private final int redValue;

    private final int greenValue;

    private final int blueValue;

    
    public Color() {
        this.redValue = 0;
        this.greenValue = 0;
        this.blueValue = 0;
    }
    
    public Color(int red, int green, int blue) {
        String errorTemplate = "Invalid color component value: %s for %s";
        if (red < 0 || red >= 256) {
            throw new IllegalArgumentException(String.format(errorTemplate, red, Component.RED));
        }
        if (green < 0 || green >= 256) {
            throw new IllegalArgumentException(String.format(errorTemplate, green, Component.GREEN));
        }
        if (blue < 0 || blue >= 256) {
            throw new IllegalArgumentException(String.format(errorTemplate, blue, Component.BLUE));
        }
        this.redValue = red;
        this.greenValue = green;
        this.blueValue = blue;
    }
    
    public Color(Component component, int value) {
        if (value < 0 || value >= 256) {
            throw new IllegalArgumentException(
                "Invalid color component value: " + value + " for " + component
            );
        }
        this.redValue = component == Component.RED ? value : 0;
        this.greenValue = component == Component.GREEN ? value : 0;
        this.blueValue = component == Component.BLUE ? value : 0;
    }
    
    public Color(int rgb) {
        this.blueValue = rgb % 256;
        int rg = (rgb - blueValue) / 256;
        this.greenValue = rg % 256;
        int r = (rg - greenValue) / 256;
        this.redValue = r % 256;
    }
    
    public Color(String colorString) {
        String upperColorString = colorString.toUpperCase();
        String[] parts = new String[3];
        if (upperColorString.matches("#?[0-9A-F]{6}")) {
            int diff = upperColorString.charAt(0) == '#' ? 1 : 0;
            parts[0] = upperColorString.substring(diff, diff + 2);
            parts[1] = upperColorString.substring(diff + 2, diff + 4);
            parts[2] = upperColorString.substring(diff + 4, diff + 6);
        } else if (upperColorString.matches("#?[0-9A-F]{3}")) {
            int diff = upperColorString.charAt(0) == '#' ? 1 : 0;
            char[] chars = new char[3];
            chars[0] = upperColorString.charAt(diff);
            chars[1] = upperColorString.charAt(diff + 1);
            chars[2] = upperColorString.charAt(diff + 2);
            parts[0] = new String(new char[] { chars[0], chars[0] });
            parts[1] = new String(new char[] { chars[1], chars[1] });
            parts[2] = new String(new char[] { chars[2], chars[2] });
        } else {
            this.redValue = 0;
            this.greenValue = 0;
            this.blueValue = 0;
            return;
        }
        this.redValue = hex2int(parts[0]);
        this.greenValue = hex2int(parts[1]);
        this.blueValue = hex2int(parts[2]);
    }
    
    
    public int getRed() {
        return redValue;
    }

    public int getGreen() {
        return greenValue;
    }

    public int getBlue() {
        return blueValue;
    }
    
    public String getHexa() {
        return ("#" + int2hex(redValue) + int2hex(greenValue) + int2hex(blueValue)).toUpperCase();
    }
    
    public int toInt() {
        return (redValue * 65536) + (greenValue * 256) + blueValue;
    }
    
    public Integer[] toArray() {
        return new Integer[] { redValue, greenValue, blueValue };
    }

    public boolean isDark() {
        return (redValue + greenValue + blueValue < 384);
    }
    
    public Color getBlackWhiteColor() {
        return isDark() ? BLACK : WHITE;
    }
    
    public Color getContrastColor() {
        int newRed = redValue > 127 ? 0 : 255;
        int newGreen = greenValue > 127 ? 0 : 255;
        int newBlue = blueValue > 127 ? 0 : 255;
        return new Color(newRed, newGreen, newBlue);
    }

    public Color getBlackWhiteContrastColor() {
        return isDark() ? WHITE : BLACK;
    }
    
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Color)) {
            return false;
        }
        
        Color otherColor = (Color)other;
        
        return (
            this.redValue == otherColor.redValue &&
            this.greenValue == otherColor.greenValue &&
            this.blueValue == otherColor.blueValue
        );
    }
    
    @Override
    public int hashCode() {
        return redValue;
    }
    
    @Override
    public String toString() {
        return getHexa();
    }
    
    private int hex2int(String value) {
        return Integer.parseInt(value, 16);
    }
    
    private String int2hex(int value) {
        String result = Integer.toHexString(value);
        if (result.length() == 1) {
            result = "0" + result;
        }
        return result;
    }
    

    public static Color avg(Collection<Color> colors) {
        return avg(colors.toArray(new Color[colors.size()]));
    }
    
    public static Color avg(Color... colors) {
        if (colors.length == 0) {
            return BLACK;
        }
        long sumRed = 0;
        long sumGreen = 0;
        long sumBlue = 0;
        for (Color color : colors) {
            sumRed += color.redValue;
            sumGreen += color.greenValue;
            sumBlue += color.blueValue;
        }
        int avgRed = (int) (sumRed / colors.length);
        int avgGreen = (int) (sumGreen / colors.length);
        int avgBlue = (int) (sumBlue / colors.length);
        return new Color(avgRed, avgGreen, avgBlue);
    }
    
}
