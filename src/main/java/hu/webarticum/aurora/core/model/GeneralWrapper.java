package hu.webarticum.aurora.core.model;

import java.util.Comparator;

public class GeneralWrapper<T> {

    private T item = null;

    public GeneralWrapper() {
    }

    public GeneralWrapper(T item) {
        this.item = item;
    }
    
    public synchronized void set(T item) {
        this.item = item;
    }
    
    public synchronized T get() {
        return item;
    }
    
    public synchronized boolean isNull() {
        return (item == null);
    }

    @Override
    public int hashCode() {
        return item == null ? 0 : item.hashCode();
    }
    
    public static class WrapperComparator<T> implements Comparator<GeneralWrapper<T>> {
        
        private final Comparator<? super T> innerComparator;
        
        public WrapperComparator(Comparator<? super T> innerComparator) {
            this.innerComparator = innerComparator;
        }
        
        @Override
        public int compare(GeneralWrapper<T> wrapper1, GeneralWrapper<T> wrapper2) {
            return innerComparator.compare(wrapper1.get(), wrapper2.get());
        }
        
    }
    
}
