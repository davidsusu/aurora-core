package hu.webarticum.aurora.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class Store<T> implements Iterable<T>, Serializable {
    
    private static final long serialVersionUID = 1L;
    
    
    protected final Map<String, T> idItemMap = new HashMap<String, T>();
    
    protected final Map<T, String> itemIdMap;
    
    protected final List<StoreListener<T>> listeners = new ArrayList<StoreListener<T>>(1);
    
    
    public enum REGISTER_MODE {
        INSERT_OR_UPDATE,
        INSERT_AUTO,
        INSERT_STRICT,
        INSERT_PUSH_ASIDE,
        UPDATE,
    }
    
    public static final REGISTER_MODE DEFAULT_REGISTER_MODE = REGISTER_MODE.INSERT_AUTO;
    

    public Store() {
        this(null);
    }
    
    public Store(Comparator<T> comparator) {
        itemIdMap = createItemIdMap(comparator);
    }
    
    private static <T> Map<T, String> createItemIdMap(Comparator<T> comparator) {
        if (comparator == null) {
            return new HashMap<T, String>();
        } else {
            return new TreeMap<T, String>(new MultiComparator<T>(comparator));
        }
    }

    public String register(T item) {
        return register(item, getDefaultIdFor(item), DEFAULT_REGISTER_MODE);
    }

    public String register(T item, String id) {
        return register(item, id, DEFAULT_REGISTER_MODE);
    }

    public String register(T item, REGISTER_MODE mode) {
        return register(item, getDefaultIdFor(item), mode);
    }
    
    public String register(T item, String id, REGISTER_MODE mode) {
        if (mode != REGISTER_MODE.INSERT_PUSH_ASIDE && itemIdMap.containsKey(item)) {
            return itemIdMap.get(item);
        } else {
            switch (mode) {
                case INSERT_OR_UPDATE:
                    return registerInsertOrUpdate(item, id);
                case INSERT_AUTO:
                    return registerInsertAuto(item, id);
                case INSERT_STRICT:
                    return registerInsertStrict(item, id);
                case INSERT_PUSH_ASIDE:
                    return registerInsertPushAside(item, id);
                case UPDATE:
                    return registerUpdate(item, id);
                default:
                    return null;
            }
        }
    }

    private String registerInsertOrUpdate(T item, String id) {
        if (idItemMap.containsKey(id)) {
            T oldItem = idItemMap.get(id);
            idItemMap.remove(id);
            itemIdMap.remove(oldItem);
            removed(id, oldItem);
        }
        idItemMap.put(id, item);
        itemIdMap.put(item, id);
        registered(id, item);
        return id;
    }

    private String registerInsertAuto(T item, String id) {
        String uniqueId = getUniqueString(id, idItemMap.keySet());
        idItemMap.put(uniqueId, item);
        itemIdMap.put(item, uniqueId);
        registered(uniqueId, item);
        return uniqueId;
    }

    private String registerInsertStrict(T item, String id) {
        if (!idItemMap.containsKey(id)) {
            idItemMap.put(id, item);
            itemIdMap.put(item, id);
            registered(id, item);
            return id;
        } else {
            return null;
        }
    }

    private String registerInsertPushAside(T item, String id) {
        boolean idUsed = idItemMap.containsKey(id);
        T existingItem = null;
        if (idUsed) {
            existingItem = idItemMap.get(id);
            idItemMap.remove(id);
            itemIdMap.remove(existingItem);
            removed(id, existingItem);
        }
        idItemMap.put(id, item);
        itemIdMap.put(item, id);
        registered(id, item);
        if (idUsed) {
            String newUniqueId = getUniqueString(id, idItemMap.keySet());
            idItemMap.put(newUniqueId, existingItem);
            itemIdMap.put(existingItem, newUniqueId);
            registered(newUniqueId, existingItem);
        }
        return id;
    }

    private String registerUpdate(T item, String id) {
        if (idItemMap.containsKey(id)) {
            T oldItem = idItemMap.get(id);
            idItemMap.remove(id);
            itemIdMap.remove(oldItem);
            removed(id, oldItem);
            idItemMap.put(id, item);
            itemIdMap.put(item, id);
            registered(id, item);
            return id;
        } else {
            return null;
        }
    }

    public void registerAll(Store<? extends T> otherStore) {
        registerAll(otherStore, DEFAULT_REGISTER_MODE);
    }

    public void registerAll(Store<? extends T> otherStore, REGISTER_MODE mode) {
        for (Map.Entry<String, ? extends T> entry: otherStore.entries()) {
            register(entry.getValue(), entry.getKey(), mode);
        }
    }

    public T get(String id) {
        return idItemMap.get(id);
    }

    public String getId(T item) {
        return itemIdMap.get(item);
    }

    public List<String> getIds(Collection<T> items) {
        List<String> ids = new ArrayList<String>();
        for (T item: items) {
            String itemId = itemIdMap.get(item);
            if (itemId != null) {
                ids.add(itemId);
            }
        }
        return ids;
    }

    public boolean remove(T item) {
        if (itemIdMap.containsKey(item)) {
            String id = itemIdMap.get(item);
            itemIdMap.remove(item);
            idItemMap.remove(id);
            removed(id, item);
            return true;
        } else {
            return false;
        }
    }

    public boolean removeId(String id) {
        if (idItemMap.containsKey(id)) {
            T item = idItemMap.get(id);
            idItemMap.remove(id);
            itemIdMap.remove(item);
            removed(id, item);
            return true;
        } else {
            return false;
        }
    }
    
    public boolean contains(T item) {
        return itemIdMap.containsKey(item);
    }

    public boolean containsId(String id) {
        return idItemMap.containsKey(id);
    }

    public boolean isEmpty() {
        return idItemMap.isEmpty();
    }
    
    public int size() {
        return idItemMap.size();
    }
    
    public List<T> getAll() {
        return new ArrayList<T>(itemIdMap.keySet());
    }
    
    public Iterable<Map.Entry<String, T>> entries() {
        return new StoreEntryIterable();
    }
    
    @Override
    public Iterator<T> iterator() {
        return new StoreIterator();
    }
    
    public Iterator<Map.Entry<String, T>> entryIterator() {
        return new StoreEntryIterator();
    }
    
    public String getDefaultIdFor(T item) {
        return Integer.toHexString(System.identityHashCode(item));
    }
    
    public boolean isSorted() {
        return (itemIdMap instanceof SortedMap);
    }
    
    public Map<T, String> getItemIdMap() {
        if (itemIdMap instanceof SortedMap) {
            return new TreeMap<T, String>((SortedMap<T, String>) itemIdMap);
        } else {
            return new HashMap<T, String>(itemIdMap);
        }
    }

    public void refresh(T item) {
        MapSearchResult<T, String> result = searchMutableKey(itemIdMap, item);
        if (result.found) {
            String id = result.value;
            result.iterator.remove();
            itemIdMap.put(item, id);
            itemRefreshed(id, item);
        }
    }

    public void refresh(boolean reorder) {
        if (reorder) {
            refresh();
        } else {
            storeRefreshed(false);
        }
    }
    
    public void refresh() {
        if (itemIdMap instanceof TreeMap) {
            Map<T, String> itemIdMapCopy = new HashMap<T, String>(itemIdMap);
            itemIdMap.clear();
            itemIdMap.putAll(itemIdMapCopy);
            storeRefreshed(true);
        } else {
            storeRefreshed(false);
        }
    }

    private <K, V> MapSearchResult<K, V> searchMutableKey(Map<K, V> map, K key) {
        Iterator<Map.Entry<K, V>> entryIterator = map.entrySet().iterator();
        while (entryIterator.hasNext()) {
            Map.Entry<K, V> entry = entryIterator.next();
            if (key.equals(entry.getKey())) {
                return new MapSearchResult<K, V>(true, entry.getValue(), entryIterator);
            }
        }
        return new MapSearchResult<K, V>(false, null, null);
    }
    
    @Override
    public String toString() {
        return idItemMap.toString();
    }
    
    public void addStoreListener(StoreListener<T> storeListener) {
        listeners.add(storeListener);
    }

    public void removeStoreListener(StoreListener<T> storeListener) {
        listeners.remove(storeListener);
    }

    protected void registered(String id, T item) {
        StoreListener.RegisterEvent<T> registerEvent = new StoreListener.RegisterEvent<T>(this, id, item);
        for (StoreListener<T> storeListener: listeners) {
            storeListener.registered(registerEvent);
        }
    }
    
    protected void removed(String id, T item) {
        StoreListener.RemoveEvent<T> removeEvent = new StoreListener.RemoveEvent<T>(this, id, item);
        for (StoreListener<T> storeListener: listeners) {
            storeListener.removed(removeEvent);
        }
    }

    protected void itemRefreshed(String id, T item) {
        StoreListener.ItemRefreshEvent<T> itemRefreshEvent = new StoreListener.ItemRefreshEvent<T>(this, id, item);
        for (StoreListener<T> storeListener: listeners) {
            storeListener.itemRefreshed(itemRefreshEvent);
        }
    }
    
    protected void storeRefreshed(boolean reordered) {
        StoreListener.StoreRefreshEvent<T> storeRefreshEvent = new StoreListener.StoreRefreshEvent<T>(this, reordered);
        for (StoreListener<T> storeListener: listeners) {
            storeListener.storeRefreshed(storeRefreshEvent);
        }
    }
    
    protected String getUniqueString(String initialValue, Collection<String> existingValues) {
        String result = initialValue;
        for (int i = 2; existingValues.contains(result); i++) {
            result = initialValue + "_" + i;
        }
        return result;
    }
    
    
    public interface StoreListener<T> extends EventListener {

        public void registered(RegisterEvent<T> registerEvent);

        public void removed(RemoveEvent<T> removeEvent);

        public void itemRefreshed(ItemRefreshEvent<T> itemRefreshEvent);
        
        public void storeRefreshed(StoreRefreshEvent<T> storeRefreshEvent);
        
        public static class RegisterEvent<T> {

            private final Store<T> store;
            
            private final String id;
            
            private final T item;
            
            public RegisterEvent(Store<T> store, String id, T item) {
                this.store = store;
                this.id = id;
                this.item = item;
            }

            public Store<T> getStore() {
                return store;
            }

            public String getId() {
                return id;
            }
            
            public T getItem() {
                return item;
            }
            
        }

        public static class RemoveEvent<T> {
            
            private final Store<T> store;
            
            private final String id;
            
            private final T item;
            
            public RemoveEvent(Store<T> store, String id, T item) {
                this.store = store;
                this.id = id;
                this.item = item;
            }

            public Store<T> getStore() {
                return store;
            }

            public String getId() {
                return id;
            }
            
            public T getItem() {
                return item;
            }
            
        }

        public static class ItemRefreshEvent<T> {

            private final Store<T> store;
            
            private final String id;
            
            private final T item;
            
            public ItemRefreshEvent(Store<T> store, String id, T item) {
                this.store = store;
                this.id = id;
                this.item = item;
            }

            public Store<T> getStore() {
                return store;
            }

            public String getId() {
                return id;
            }
            
            public T getItem() {
                return item;
            }
            
        }
        
        public static class StoreRefreshEvent<T> {
            
            private final Store<T> store;

            private final boolean reordered;
            
            public StoreRefreshEvent(Store<T> store, boolean reordered) {
                this.store = store;
                this.reordered = reordered;
            }
            
            public Store<T> getStore() {
                return store;
            }
            
            public boolean isReordered() {
                return reordered;
            }

        }
        
    }
    
    
    private class StoreIterator implements Iterator<T> {
        
        private final Iterator<T> innerIterator;
        
        private T currentItem = null;
        
        StoreIterator() {
            this.innerIterator = itemIdMap.keySet().iterator();
        }
        
        @Override
        public boolean hasNext() {
            return innerIterator.hasNext();
        }

        @Override
        public T next() {
            currentItem = innerIterator.next();
            return currentItem;
        }

        @Override
        public void remove() {
            if (currentItem != null) {
                String id = itemIdMap.get(currentItem);
                idItemMap.remove(id);
                innerIterator.remove();
                removed(id, currentItem);
            }
        }
        
    }
    
    private class StoreEntryIterator implements Iterator<Map.Entry<String, T>> {
        
        private final Iterator<Map.Entry<T, String>> innerIterator;
        
        private StoreEntry currentEntry = null;
        
        StoreEntryIterator() {
            this.innerIterator = itemIdMap.entrySet().iterator();
        }
        
        @Override
        public boolean hasNext() {
            return innerIterator.hasNext();
        }

        @Override
        public Map.Entry<String, T> next() {
            Map.Entry<T, String> innerEntry = innerIterator.next();
            currentEntry = new StoreEntry(innerEntry.getValue(), innerEntry.getKey());
            return currentEntry;
        }

        @Override
        public void remove() {
            if (currentEntry != null) {
                String id = currentEntry.getKey();
                T item = currentEntry.getValue();
                idItemMap.remove(id);
                innerIterator.remove();
                removed(id, item);
            }
        }
        
        private class StoreEntry implements Map.Entry<String, T> {

            private final String id;

            private final T item;
            
            StoreEntry(String id, T item) {
                this.id = id;
                this.item = item;
            }
            
            @Override
            public String getKey() {
                return id;
            }

            @Override
            public T getValue() {
                return item;
            }

            @Override
            public T setValue(T value) {
                throw new UnsupportedOperationException();
            }
            
        }
        
    }
    
    private class StoreEntryIterable implements Iterable<Map.Entry<String, T>> {

        @Override
        public Iterator<Map.Entry<String, T>> iterator() {
            return new StoreEntryIterator();
        }
        
    }
    
    private static class MapSearchResult<K, V> {

        public final boolean found;
        
        public final V value;
        
        public final Iterator<Map.Entry<K, V>> iterator;
        
        public MapSearchResult(boolean found, V value, Iterator<Map.Entry<K, V>> iterator) {
            this.found = found;
            this.value = value;
            this.iterator = iterator;
        }
        
    }
    
}
