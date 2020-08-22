package hu.webarticum.aurora.core.model;

import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(rootValue.getAccess("apple").get().get()).isEqualTo("x");
        assertThat(rootValue.getAccess("pear").get().getAsSet()).hasSize(4);
        assertThat(rootValue.getAccess("pear[].a{0}", resource1).get().get()).isEqualTo("n");
        assertThat(rootValue.getAccess("pear[].a{0}", resource2).get().get()).isEqualTo("m");
        assertThat(rootValue.getAccess("pear[].b").get().get()).isIn("$", "%");
        assertThat(rootValue.getAccess("pear[].a{0}", resource3).get().get()).isEqualTo("o");
        assertThat(rootValue.getAccess("pear[].a{0}", resource4).get().get()).isEqualTo("p");
        assertThat(rootValue.getAccess("cherry{0}[0]", resource5).get().getAsList()).containsExactly(
            new Value("w"), new Value("xxx")
        );
        assertThat(rootValue.getAccess("cherry{0}[0][0]", resource5).get().get()).isEqualTo("w");
        assertThat(rootValue.getAccess("cherry{0}[1]{1}", resource5, resource7).get().get()).isEqualTo(resource8);
        assertThat(rootValue.getAccess("cherry{0}.fake", resource5).get().isNull()).isTrue();
        assertThat(rootValue.getAccess("pear[2]{0}", resource2).exists()).isFalse();
    }

    @Test
    public void testIncompatibleConversion() {
        rootValue.getAccess("apple").get().getAsList().add(new Value(3));
        assertThat(rootValue.getAccess("apple").get().getType()).isEqualTo(Value.Type.STRING);
        
        rootValue.getAccess("apple[0]").set(3);
        assertThat(rootValue.getAccess("apple").get().getType()).isEqualTo(Value.Type.LIST);
        assertThat(rootValue.getAccess("apple").get().getAsList().get(0).getAsInt()).isEqualTo(3);
        assertThat(rootValue.getAccess("apple[0]").get().getAsInt()).isEqualTo(3);
        
        rootValue.getAccess("cherry{0}[1]{1}.sub", resource5, resource6).set("SUBVAL");
        assertThat(
            rootValue.getAccess("cherry{0}[1]{1}", resource5, resource6).get().getType()
        ).isEqualTo(Value.Type.MAP);
        assertThat(rootValue.getAccess("cherry{0}[1]{1}", resource5, resource6).get().getAsMap()).hasSize(1);
        assertThat(
            rootValue.getAccess("cherry{0}[1]{1}", resource5, resource6).get().getAsMap().get(new Value("sub")).get()
        ).isEqualTo("SUBVAL");
        assertThat(
            rootValue.getAccess("cherry{0}[1]{1}.sub", resource5, resource6).get().get()
        ).isEqualTo("SUBVAL");
    }
    
    @Test
    public void testListToMapAutoConversion() {
        Value value = rootValue.getAccess("orange").get();
        
        assertThat(value.getAccess("[1]").get().get()).isEqualTo(2);
        
        value.getAccess("[1]").set(111);
        
        assertThat(value.getAccess("[1]").get().get()).isEqualTo(111);
        assertThat(value.getType()).isEqualTo(Value.Type.LIST);

        value.getAccess("[4]").set(777);
        
        assertThat(value.getAccess("[4]").get().get()).isEqualTo(777);
        assertThat(value.getType()).isEqualTo(Value.Type.LIST);

        value.getAccess("[-1]").set(999);
        
        assertThat(value.getAccess("[-1]").get().get()).isEqualTo(999);
        assertThat(value.getType()).isEqualTo(Value.Type.MAP);
    }

    @Test
    public void testSetToListAutoConversion() {
        Value value = rootValue.getAccess("orange").get();

        assertThat(value.getAccess("[1]").get().get()).isEqualTo(2);
        
        value.getAccess("[1]").set(111);
        
        assertThat(value.getAccess("[1]").get().get()).isEqualTo(111);
        assertThat(value.getType()).isEqualTo(Value.Type.LIST);
    }
    
    @Test
    public void testSetToMapAutoConversion() {
        Value value = new Value(Value.Type.SET);
        value.getAccess("[]").set(1);
        value.getAccess("[]").set(2);
        value.getAccess("[]").set(3);
        
        assertThat(value.getAccess("[1]").get().get()).isEqualTo(2);
        
        value.getAccess("[4]").set(111);

        assertThat(value.getAccess("[4]").get().get()).isEqualTo(111);
        assertThat(value.getType()).isEqualTo(Value.Type.MAP);
    }

    @Test
    public void testRemove() {
        assertThat(rootValue.getAccess("cherry").exists()).isTrue();
        
        rootValue.getAsMap().remove(new Value("cherry"));
        
        assertThat(rootValue.getAccess("cherry").exists()).isFalse();
        assertThat(rootValue.getAccess("cherry").get().isNull()).isTrue();
        assertThat(rootValue.getAccess("cherry").get().get()).isNull();;
        assertThat(rootValue.getAsMap().get(new Value("cherry"))).isNull();
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
        
        assertThat(set2).isEqualTo(set1);
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
