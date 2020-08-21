package hu.webarticum.aurora.core.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class StoreTest {

    @Test
    public void testSimpleRegister() {
        Store<String> store = new Store<String>();
        assertFalse(store.isSorted());
        
        assertEquals(0, store.size());
        assertTrue(store.isEmpty());
        assertEquals(new ArrayList<String>(), store.getAll());
        
        store.register("Apple");
        assertTrue(store.contains("Apple"));
        assertEquals(1, store.size());
        assertFalse(store.isEmpty());
        assertEquals(Arrays.asList("Apple"), store.getAll());
    }
    
    @Test
    public void testRegisterModes() {
        Store<String> store = new Store<String>();
        assertEquals(0, store.size());
        
        String appleId = store.register("Apple", "apple");
        assertEquals("apple", appleId);
        assertEquals("apple", store.getId("Apple"));
        assertEquals(1, store.size());
        assertEquals(Arrays.asList("Apple"), store.getAll());
        assertEquals("Apple", store.get("apple"));
        
        store.register("Banana", "banana", Store.REGISTER_MODE.INSERT_STRICT);
        assertEquals(2, store.size());
        assertEquals(Arrays.asList("Apple", "Banana"), sorted(store.getAll()));
        assertEquals("Apple", store.get("apple"));
        assertEquals("Banana", store.get("banana"));
        
        String apple2Id = store.register("Apple2", "apple", Store.REGISTER_MODE.INSERT_STRICT);
        assertNull(apple2Id);
        assertEquals(2, store.size());
        assertEquals(Arrays.asList("Apple", "Banana"), sorted(store.getAll()));
        assertEquals("Apple", store.get("apple"));
        assertEquals("Banana", store.get("banana"));
        
        String apple3Id = store.register("Apple3", "apple", Store.REGISTER_MODE.INSERT_OR_UPDATE);
        assertEquals("apple", apple3Id);
        assertEquals("apple", store.getId("Apple3"));
        assertNull(store.getId("Apple"));
        assertEquals(2, store.size());
        assertEquals(Arrays.asList("Apple3", "Banana"), sorted(store.getAll()));
        assertEquals("Apple3", store.get("apple"));
        assertEquals("Banana", store.get("banana"));
        
        String orangeId = store.register("Orange", "orange", Store.REGISTER_MODE.UPDATE);
        assertNull(orangeId);
        assertNull(store.getId("Orange"));
        assertEquals(2, store.size());
        assertEquals(Arrays.asList("Apple3", "Banana"), sorted(store.getAll()));
        assertEquals("Apple3", store.get("apple"));
        assertEquals("Banana", store.get("banana"));
        assertNull(store.get("orange"));
        
        store.register("Banana2", "banana", Store.REGISTER_MODE.UPDATE);
        assertEquals(2, store.size());
        assertEquals(Arrays.asList("Apple3", "Banana2"), sorted(store.getAll()));
        assertEquals("Apple3", store.get("apple"));
        assertEquals("Banana2", store.get("banana"));

        Map<String, String> expectedItemIdMap = new HashMap<String, String>();
        expectedItemIdMap.put("Apple3", "apple");
        expectedItemIdMap.put("Banana2", "banana");
        assertEquals(expectedItemIdMap, store.getItemIdMap());
        
        String banana3Id = store.register("Banana3", "banana", Store.REGISTER_MODE.INSERT_AUTO);
        assertNotEquals("banana", banana3Id);
        assertEquals(3, store.size());
        assertEquals(Arrays.asList("Apple3", "Banana2", "Banana3"), sorted(store.getAll()));
        assertEquals("Apple3", store.get("apple"));
        assertEquals("Banana2", store.get("banana"));
        
        String banana4Id = store.register("Banana4", "banana", Store.REGISTER_MODE.INSERT_PUSH_ASIDE);
        assertNotNull(banana4Id);
        assertEquals("banana", store.getId("Banana4"));
        assertNotEquals("banana", store.getId("Banana2"));
        assertEquals(4, store.size());
        assertEquals(Arrays.asList("Apple3", "Banana2", "Banana3", "Banana4"), sorted(store.getAll()));
        assertEquals("Apple3", store.get("apple"));
        assertEquals("Banana4", store.get("banana"));
        
        store.register("Orange2", "orange", Store.REGISTER_MODE.INSERT_PUSH_ASIDE);
        assertEquals(5, store.size());
        assertEquals(
            Arrays.asList("Apple3", "Banana2", "Banana3", "Banana4", "Orange2"),
            sorted(store.getAll())
        );
        assertEquals("Apple3", store.get("apple"));
        assertEquals("Banana4", store.get("banana"));
        assertEquals("Orange2", store.get("orange"));
        
    }

    @Test
    public void testRemove() {
        Store<String> store = new Store<String>();
        store.register("Apple", "apple");
        store.register("Banana", "banana");
        store.register("Orange", "orange");
        assertEquals(3, store.size());
        assertEquals(Arrays.asList("Apple", "Banana", "Orange"), sorted(store.getAll()));
        
        boolean bananaRemoved = store.remove("Banana");
        assertTrue(bananaRemoved);
        assertEquals(2, store.size());
        assertEquals(Arrays.asList("Apple", "Orange"), sorted(store.getAll()));
        
        boolean appleRemoved = store.removeId("apple");
        assertTrue(appleRemoved);
        assertEquals(1, store.size());
        assertEquals(Arrays.asList("Orange"), store.getAll());

        boolean appleRemovedAgain = store.removeId("apple");
        assertFalse(appleRemovedAgain);
        assertEquals(1, store.size());
        assertEquals(Arrays.asList("Orange"), store.getAll());

        boolean orangeRemoved = store.removeId("orange");
        assertTrue(orangeRemoved);
        assertEquals(0, store.size());
        assertTrue(store.isEmpty());
    }

    @Test
    public void testSorted() {
        Store<String> sortedStore = new Store<String>(new Comparator<String>() {

            @Override
            public int compare(String string1, String string2) {
                return string1.compareTo(string2);
            }
            
        });
        sortedStore.register("Banana", "zzz-banana");
        sortedStore.register("Watermelon", "watermelon");
        sortedStore.register("Orange");
        sortedStore.register("Apple", "apple");
        sortedStore.register("Kiwi", "aaa-kiwi");

        assertTrue(sortedStore.isSorted());
        
        assertEquals("Apple", sortedStore.get("apple"));

        assertEquals(Arrays.asList(
            "Apple", "Banana", "Kiwi", "Orange", "Watermelon"
        ), sortedStore.getAll());
    }

    @Test
    public void testListener() {
        Store<String> store = new Store<String>();
        
        final StringBuilder eventStringBuilder = new StringBuilder();
        Store.StoreListener<String> storeListener = new Store.StoreListener<String>() {
            
            @Override
            public void registered(RegisterEvent<String> registerEvent) {
                eventStringBuilder.append("A");
            }

            @Override
            public void removed(RemoveEvent<String> removeEvent) {
                eventStringBuilder.append("D");
            }
            
            @Override
            public void itemRefreshed(ItemRefreshEvent<String> itemRefreshEvent) {
                eventStringBuilder.append("I");
            }

            @Override
            public void storeRefreshed(StoreRefreshEvent<String> storeRefreshEvent) {
                eventStringBuilder.append("S");
            }
            
        };
        store.addStoreListener(storeListener);
        
        store.refresh("Non-existing");
        store.register("Apple", "apple");
        store.refresh("Apple");
        store.register("Apple2", "apple", Store.REGISTER_MODE.INSERT_OR_UPDATE);
        store.register("Orange", "orange");
        store.refresh();
        store.refresh(true);
        store.removeId("apple");
        store.remove("Orange");
        
        String expectedEventString = "AIDAASSDD";
        assertEquals(expectedEventString, eventStringBuilder.toString());
        
        store.removeStoreListener(storeListener);
        
        store.register("Kiwi");
        store.refresh(true);
        store.remove("Kiwi");

        assertEquals(expectedEventString, eventStringBuilder.toString());
    }
    
    private <T extends Comparable<T>> List<T> sorted(Collection<T> list) {
        List<T> result = new ArrayList<T>(list);
        Collections.sort(result);
        return result;
    }
    
}
