package hu.webarticum.aurora.core.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

public class ValueTest {

    private Resource resource1 = new Resource("p1");
    private Resource resource2 = new Resource("p2");
    private Resource resource3 = new Resource("p3");
    private Resource resource4 = new Resource("p4");
    private Resource resource5 = new Resource("p5");
    private Resource resource6 = new Resource("p6");
    private Resource resource7 = new Resource("p7");
    private Resource resource8 = new Resource("p8");

    private Value rootValue;
    
    @Test
    public void testAccess() {
        assertEquals("x", rootValue.getAccess("apple").get().get());
        assertEquals(4, rootValue.getAccess("pear").get().getAsSet().size());
        assertEquals("n", rootValue.getAccess("pear[].a{0}", resource1).get().get());
        assertEquals("m", rootValue.getAccess("pear[].a{0}", resource2).get().get());
        assertTrue(Arrays.asList("$", "%").contains(rootValue.getAccess("pear[].b").get().get()));
        assertEquals("o", rootValue.getAccess("pear[].a{0}", resource3).get().get());
        assertEquals("p", rootValue.getAccess("pear[].a{0}", resource4).get().get());
        assertEquals(
            Arrays.asList(new Value("w"), new Value("xxx")),
            rootValue.getAccess("cherry{0}[0]", resource5).get().get()
        );
        assertEquals("w", rootValue.getAccess("cherry{0}[0][0]", resource5).get().get());
        assertEquals(
            resource8,
            rootValue.getAccess("cherry{0}[1]{1}", resource5, resource7).get().get()
        );
        assertTrue(rootValue.getAccess("cherry{0}.fake", resource5).get().isNull());
        assertFalse(rootValue.getAccess("pear[2]{0}", resource2).exists());
    }

    @Test
    public void testIncompatibleConversion() {
        rootValue.getAccess("apple").get().getAsList().add(new Value(3));
        assertEquals(Value.Type.STRING, rootValue.getAccess("apple").get().getType());
        
        rootValue.getAccess("apple[0]").set(3);
        assertEquals(Value.Type.LIST, rootValue.getAccess("apple").get().getType());
        assertEquals(Integer.valueOf(3), rootValue.getAccess("apple").get().getAsList().get(0).get());
        assertEquals(Integer.valueOf(3), rootValue.getAccess("apple[0]").get().get());
        
        rootValue.getAccess("cherry{0}[1]{1}.sub", resource5, resource6).set("SUBVAL");
        assertEquals(
            Value.Type.MAP,
            rootValue.getAccess("cherry{0}[1]{1}", resource5, resource6).get().getType()
        );
        assertEquals(
            1,
            rootValue.getAccess("cherry{0}[1]{1}", resource5, resource6).get().getAsMap().size()
        );
        assertEquals(
            "SUBVAL",
            rootValue.getAccess("cherry{0}[1]{1}", resource5, resource6).get().getAsMap()
                .get(new Value("sub")).get()
        );
        assertEquals(
            "SUBVAL",
            rootValue.getAccess("cherry{0}[1]{1}.sub", resource5, resource6).get().get()
        );
    }
    
    @Test
    public void testListToMapAutoConversion() {
        Value value = rootValue.getAccess("orange").get();
        
        assertEquals(2, value.getAccess("[1]").get().get());
        
        value.getAccess("[1]").set(111);
        
        assertEquals(111, value.getAccess("[1]").get().get());
        assertEquals(Value.Type.LIST, value.getType());

        value.getAccess("[4]").set(777);
        
        assertEquals(777, value.getAccess("[4]").get().get());
        assertEquals(Value.Type.LIST, value.getType());

        value.getAccess("[-1]").set(999);
        
        assertEquals(999, value.getAccess("[-1]").get().get());
        assertEquals(Value.Type.MAP, value.getType());
    }

    @Test
    public void testSetToListAutoConversion() {
        Value value = rootValue.getAccess("orange").get();

        assertEquals(2, value.getAccess("[1]").get().get());
        
        value.getAccess("[1]").set(111);
        
        assertEquals(111, value.getAccess("[1]").get().get());
        assertEquals(Value.Type.LIST, value.getType());
    }
    
    @Test
    public void testSetToMapAutoConversion() {
        Value value = new Value(Value.Type.SET);
        value.getAccess("[]").set(1);
        value.getAccess("[]").set(2);
        value.getAccess("[]").set(3);
        
        assertEquals(2, value.getAccess("[1]").get().get());
        
        value.getAccess("[4]").set(111);

        assertEquals(111, value.getAccess("[4]").get().get());
        assertEquals(Value.Type.MAP, value.getType());
    }

    @Test
    public void testRemove() {
        assertTrue(rootValue.getAccess("cherry").exists());
        rootValue.getAsMap().remove(new Value("cherry"));
        assertFalse(rootValue.getAccess("cherry").exists());
        assertTrue(rootValue.getAccess("cherry").get().isNull());
        assertNull(rootValue.getAccess("cherry").get().get());
        assertNull(rootValue.getAsMap().get(new Value("cherry")));
    }

    @Test
    public void testSetEquality() {
        Value.ValueSet set1 = new Value(Value.Type.SET).getAsSet();
        set1.addRaw(1);
        set1.addRaw(3);
        set1.addRaw(4);
        Value.ValueSet set2 = new Value(Value.Type.SET).getAsSet();
        set2.addRaw(3);
        set2.addRaw(4);
        set2.addRaw(1);
        assertEquals(set1, set2);
    }
    
    @Before
    public void buildThings() {
        resource1 = new Resource("p1");
        resource2 = new Resource("p2");
        resource3 = new Resource("p3");
        resource4 = new Resource("p4");
        resource5 = new Resource("p5");
        resource6 = new Resource("p6");
        resource7 = new Resource("p7");
        resource8 = new Resource("p8");

        rootValue = new Value(Value.Type.MAP);
        rootValue.getAccess("apple").set("x");
        rootValue.getAccess("pear[]").set("y");
        rootValue.getAccess("pear[]").set("z");

        Value setSubMap1Value = new Value(Value.Type.MAP);
        setSubMap1Value.getAccess("a{0}", resource1).set("n");
        setSubMap1Value.getAccess("a{0}", resource2).set("m");
        setSubMap1Value.getAccess("b").set("$");
        
        rootValue.getAccess("pear[]").set(setSubMap1Value);

        Value setSubMap2Value = new Value(Value.Type.MAP);
        setSubMap1Value.getAccess("a{0}", resource3).set("o");
        setSubMap1Value.getAccess("a{0}", resource4).set("p");
        setSubMap1Value.getAccess("b").set("%");
        
        rootValue.getAccess("pear[]").set(setSubMap2Value);

        rootValue.getAccess("cherry{0}[0][0]", resource5).set("w");
        rootValue.getAccess("cherry{0}[0][1]", resource5).set("xxx");

        rootValue.getAccess("cherry{0}[1]{1}", resource5, resource6).set(Value.Type.NULL);
        rootValue.getAccess("cherry{0}[1]{1}", resource5, resource7).set(resource8);
        
        Value simpleSet = new Value(Value.Type.SET);
        simpleSet.getAccess("[]").set(1);
        simpleSet.getAccess("[]").set(2);
        simpleSet.getAccess("[]").set(3);
        rootValue.getAccess("orange").set(simpleSet);
    }
    
}
