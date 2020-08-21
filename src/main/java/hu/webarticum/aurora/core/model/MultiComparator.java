package hu.webarticum.aurora.core.model;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class MultiComparator<T> implements Comparator<T>, Serializable {
    
    private static final long serialVersionUID = 1L;

    private final Map<Integer, LinkedHashSet<T>> problematicItemsMap = new HashMap<Integer, LinkedHashSet<T>>();
    
    private final Comparator<? super T> innerComparator;
    
    private final boolean useEquals;

    public MultiComparator(Comparator<? super T> innerComparator) {
        this(innerComparator, true);
    }
    
    public MultiComparator(Comparator<? super T> innerComparator, boolean useEquals) {
        this.innerComparator = innerComparator;
        this.useEquals = useEquals;
    }
    
    @Override
    public int compare(T item1, T item2) {
        int result = innerComparator.compare(item1, item2);
        if (result != 0) {
            return result;
        }
        
        if (useEquals ? item1.equals(item2) : (item1 == item2)) {
            return 0;
        }
        
        int hashCode = System.identityHashCode(item1);
        result = Integer.compare(hashCode, System.identityHashCode(item2));
        if (result != 0) {
            return result;
        }
        
        LinkedHashSet<T> hashProblematicItems;
        if (problematicItemsMap.containsKey(hashCode)) {
            hashProblematicItems = problematicItemsMap.get(hashCode);
        } else {
            hashProblematicItems = new LinkedHashSet<T>();
            problematicItemsMap.put(hashCode, hashProblematicItems);
        }
        hashProblematicItems.add(item1);
        hashProblematicItems.add(item2);
        for (T item: hashProblematicItems) {
            if (useEquals ? item.equals(item1) : (item == item1)) {
                return -1;
            } else if (useEquals ? item.equals(item2) : (item == item2)) {
                return 1;
            }
        }
        
        return 0;
    }
    
}
