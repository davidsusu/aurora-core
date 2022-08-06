package hu.webarticum.aurora.core.text;

class NullCoreTextSupplier implements CoreTextSupplier {
    
    @Override
    public String getText(String coreTextPath) {
        return null;
    }
    
}