package hu.webarticum.aurora.core.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

class StoreTest {

    @Test
    void testSimpleRegister() {
        Store<String> store = new Store<String>();
        assertThat(store.isSorted()).isFalse();
        
        assertThat(store.size()).isEqualTo(0);
        assertThat(store.isEmpty()).isTrue();
        assertThat(store.getAll()).isEmpty();;
        
        store.register("Apple");
        assertThat(store.contains("Apple")).isTrue();
        assertThat(store.size()).isEqualTo(1);
        assertThat(store.isEmpty()).isFalse();
        assertThat(store.getAll()).containsExactly("Apple");
    }
    
    @Test
    void testRegisterModes() {
        Store<String> store = new Store<String>();
        assertThat(store.size()).isEqualTo(0);
        
        String appleId = store.register("Apple", "apple");
        assertThat(appleId).isEqualTo("apple");
        assertThat(store.getId("Apple")).isEqualTo("apple");
        assertThat(store.size()).isEqualTo(1);
        assertThat(store.getAll()).containsExactly("Apple");
        assertThat(store.get("apple")).isEqualTo("Apple");
        
        store.register("Banana", "banana", Store.REGISTER_MODE.INSERT_STRICT);
        assertThat(store.size()).isEqualTo(2);
        assertThat(sorted(store.getAll())).containsExactly("Apple", "Banana");
        assertThat(store.get("apple")).isEqualTo("Apple");
        assertThat(store.get("banana")).isEqualTo("Banana");
        
        String apple2Id = store.register("Apple2", "apple", Store.REGISTER_MODE.INSERT_STRICT);
        assertThat(apple2Id).isNull();
        assertThat(store.size()).isEqualTo(2);
        assertThat(sorted(store.getAll())).containsExactly("Apple", "Banana");
        assertThat(store.get("apple")).isEqualTo("Apple");
        assertThat(store.get("banana")).isEqualTo("Banana");
        
        String apple3Id = store.register("Apple3", "apple", Store.REGISTER_MODE.INSERT_OR_UPDATE);
        assertThat(apple3Id).isEqualTo("apple");
        assertThat(store.getId("Apple3")).isEqualTo("apple");
        assertThat(store.getId("Apple")).isNull();
        assertThat(store.size()).isEqualTo(2);
        assertThat(sorted(store.getAll())).containsExactly("Apple3", "Banana");
        assertThat(store.get("apple")).isEqualTo("Apple3");
        assertThat(store.get("banana")).isEqualTo("Banana");
        
        String orangeId = store.register("Orange", "orange", Store.REGISTER_MODE.UPDATE);
        assertThat(orangeId).isNull();
        assertThat(store.getId("Orange")).isNull();
        assertThat(store.size()).isEqualTo(2);
        assertThat(sorted(store.getAll())).containsExactly("Apple3", "Banana");
        assertThat(store.get("apple")).isEqualTo("Apple3");
        assertThat(store.get("banana")).isEqualTo("Banana");
        assertThat(store.get("orange")).isNull();
        
        store.register("Banana2", "banana", Store.REGISTER_MODE.UPDATE);
        assertThat(store.size()).isEqualTo(2);
        assertThat(sorted(store.getAll())).containsExactly("Apple3", "Banana2");
        assertThat(store.get("apple")).isEqualTo("Apple3");
        assertThat(store.get("banana")).isEqualTo("Banana2");

        Map<String, String> expectedItemIdMap = new HashMap<String, String>();
        expectedItemIdMap.put("Apple3", "apple");
        expectedItemIdMap.put("Banana2", "banana");
        assertThat(store.getItemIdMap()).isEqualTo(expectedItemIdMap);
        
        String banana3Id = store.register("Banana3", "banana", Store.REGISTER_MODE.INSERT_AUTO);
        assertThat(banana3Id).isNotEqualTo("banana");
        assertThat(store.size()).isEqualTo(3);
        assertThat(sorted(store.getAll())).containsExactly("Apple3", "Banana2", "Banana3");
        assertThat(store.get("apple")).isEqualTo("Apple3");
        assertThat(store.get("banana")).isEqualTo("Banana2");
        
        String banana4Id = store.register("Banana4", "banana", Store.REGISTER_MODE.INSERT_PUSH_ASIDE);
        assertThat(banana4Id).isNotNull();
        assertThat(store.getId("Banana4")).isEqualTo("banana");
        assertThat(store.getId("Banana2")).isNotEqualTo("banana");
        assertThat(store.size()).isEqualTo(4);
        assertThat(sorted(store.getAll())).containsExactly("Apple3", "Banana2", "Banana3", "Banana4");
        assertThat(store.get("apple")).isEqualTo("Apple3");
        assertThat(store.get("banana")).isEqualTo("Banana4");
        
        store.register("Orange2", "orange", Store.REGISTER_MODE.INSERT_PUSH_ASIDE);
        assertThat(store.size()).isEqualTo(5);
        assertThat(sorted(store.getAll())).containsExactly("Apple3", "Banana2", "Banana3", "Banana4", "Orange2");
        assertThat(store.get("apple")).isEqualTo("Apple3");
        assertThat(store.get("banana")).isEqualTo("Banana4");
        assertThat(store.get("orange")).isEqualTo("Orange2");
        
    }

    @Test
    void testRemove() {
        Store<String> store = new Store<String>();
        store.register("Apple", "apple");
        store.register("Banana", "banana");
        store.register("Orange", "orange");
        assertThat(store.size()).isEqualTo(3);
        assertThat(sorted(store.getAll())).containsExactly("Apple", "Banana", "Orange");
        
        boolean bananaRemoved = store.remove("Banana");
        assertThat(bananaRemoved).isTrue();
        assertThat(store.size()).isEqualTo(2);
        assertThat(sorted(store.getAll())).containsExactly("Apple", "Orange");
        
        boolean appleRemoved = store.removeId("apple");
        assertThat(appleRemoved).isTrue();
        assertThat(store.size()).isEqualTo(1);
        assertThat(store.getAll()).containsExactly("Orange");

        boolean appleRemovedAgain = store.removeId("apple");
        assertThat(appleRemovedAgain).isFalse();
        assertThat(store.size()).isEqualTo(1);
        assertThat(store.getAll()).containsExactly("Orange");

        boolean orangeRemoved = store.removeId("orange");
        assertThat(orangeRemoved).isTrue();
        assertThat(store.size()).isEqualTo(0);
        assertThat(store.isEmpty()).isTrue();
    }

    @Test
    void testSorted() {
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

        assertThat(sortedStore.isSorted()).isTrue();
        
        assertThat(sortedStore.get("apple")).isEqualTo("Apple");

        assertThat(sortedStore.getAll()).containsExactly("Apple", "Banana", "Kiwi", "Orange", "Watermelon");
    }

    @Test
    void testListener() {
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
        
        assertThat(eventStringBuilder.toString()).isEqualTo(expectedEventString);
        
        store.removeStoreListener(storeListener);
        
        store.register("Kiwi");
        store.refresh(true);
        store.remove("Kiwi");

        assertThat(eventStringBuilder.toString()).isEqualTo(expectedEventString);
    }
    
    private <T extends Comparable<T>> List<T> sorted(Collection<T> list) {
        List<T> result = new ArrayList<T>(list);
        Collections.sort(result);
        return result;
    }
    
}
