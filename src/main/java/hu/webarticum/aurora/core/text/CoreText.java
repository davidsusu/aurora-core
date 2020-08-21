package hu.webarticum.aurora.core.text;

public class CoreText {
    
    private static CoreTextSupplier textSupplier = new NullCoreTextSupplier();
    
    public static void setTextSupplier(CoreTextSupplier textSupplier) {
        CoreText.textSupplier = textSupplier;
    }
    
    public static String get(String coreTextPath, String defaultText) {
        String text = textSupplier.getText(coreTextPath);
        if (text != null && !text.isEmpty()) {
            return text;
        } else {
            return defaultText;
        }
    }
    
}
