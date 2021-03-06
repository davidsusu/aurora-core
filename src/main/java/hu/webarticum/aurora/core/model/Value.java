package hu.webarticum.aurora.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import hu.webarticum.aurora.core.model.time.NeverTimeLimit;
import hu.webarticum.aurora.core.model.time.TimeLimit;

public class Value implements Serializable {
    
    private static final long serialVersionUID = 1L;

    
    public enum Type {
        NULL,
        
        BOOLEAN,
        BYTE,
        SHORT,
        INT,
        LONG,
        FLOAT,
        DOUBLE,
        CHAR,
        
        STRING,
        
        MAP,
        LIST,
        SET,
        
        PERIOD,
        TIMINGSET,
        TIMELIMIT,
        TAG,
        RESOURCE,
        RESOURCESUBSET,
        BLOCK,
        ACTIVITY,
        TIMETABLE,
    }
    

    protected Serializable value;
    
    protected Type type;
    
    
    public Value() {
        this.value = null;
        this.type = Type.NULL;
    }
    
    public Value(boolean value) {
        this.value = (Boolean)value;
        this.type = Type.BOOLEAN;
    }

    public Value(byte value) {
        this.value = (Byte)value;
        this.type = Type.BYTE;
    }
    
    public Value(short value) {
        this.value = (Short)value;
        this.type = Type.SHORT;
    }

    public Value(int value) {
        this.value = (Integer)value;
        this.type = Type.INT;
    }

    public Value(long value) {
        this.value = (Long)value;
        this.type = Type.LONG;
    }

    public Value(float value) {
        this.value = (Float)value;
        this.type = Type.FLOAT;
    }

    public Value(double value) {
        this.value = (Double)value;
        this.type = Type.DOUBLE;
    }

    public Value(char value) {
        this.value = (Character)value;
        this.type = Type.CHAR;
    }
    
    public Value(Boolean value) {
        this.value = value;
        this.type = (value == null) ? Type.NULL : Type.BOOLEAN;
    }

    public Value(Byte value) {
        this.value = value;
        this.type = (value == null) ? Type.NULL : Type.BYTE;
    }
    
    public Value(Short value) {
        this.value = value;
        this.type = (value == null) ? Type.NULL : Type.SHORT;
    }

    public Value(Integer value) {
        this.value = value;
        this.type = (value == null) ? Type.NULL : Type.INT;
    }

    public Value(Long value) {
        this.value = value;
        this.type = (value == null) ? Type.NULL : Type.LONG;
    }

    public Value(Float value) {
        this.value = value;
        this.type = (value == null) ? Type.NULL : Type.FLOAT;
    }

    public Value(Double value) {
        this.value = value;
        this.type = (value == null) ? Type.NULL : Type.DOUBLE;
    }

    public Value(Character value) {
        this.value = value;
        this.type = (value == null) ? Type.NULL : Type.CHAR;
    }

    public Value(String value) {
        this.value = value;
        this.type = (value == null) ? Type.NULL : Type.STRING;
    }
    
    public Value(Period period) {
        this.value = period;
        this.type = Type.PERIOD;
    }
    
    public Value(TimingSet timingSet) {
        this.value = timingSet;
        this.type = Type.TIMINGSET;
    }
    
    public Value(TimeLimit timeLimit) {
        this.value = timeLimit;
        this.type = Type.TIMELIMIT;
    }
    
    public Value(Tag tag) {
        this.value = tag;
        this.type = Type.TAG;
    }
    
    public Value(Resource resource) {
        this.value = resource;
        this.type = Type.RESOURCE;
    }
    
    public Value(ResourceSubset resourceSubset) {
        this.value = resourceSubset;
        this.type = Type.RESOURCESUBSET;
    }

    public Value(Block block) {
        this.value = block;
        this.type = Type.BLOCK;
    }

    public Value(Activity activity) {
        this.value = activity;
        this.type = Type.ACTIVITY;
    }

    public Value(Board board) {
        this.value = board;
        this.type = Type.TIMETABLE;
    }
    
    public Value(Type type) {
        this.value = createByType(type);
        this.type = type;
    }
    
    public Value(Value otherValue) {
        this.value = otherValue.value;
        this.type = otherValue.type;
    }

    public Value(ValueMap valueMap) {
        this.value = valueMap;
        this.type = Type.MAP;
    }

    public Value(ValueList valueList) {
        this.value = valueList;
        this.type = Type.LIST;
    }

    public Value(ValueSet valueSet) {
        this.value = valueSet;
        this.type = Type.SET;
    }

    public Value(Object object) {
        this((Serializable) object);
    }
    
    public Value(Serializable object) {
        if (object instanceof Type) {
            Type type = (Type)object;
            this.type = type;
            this.value = createByType(type);
        } else if (object instanceof Value) {
            Value otherValue = (Value)object;
            this.value = otherValue.value;
            this.type = otherValue.type;
        } else {
            this.value = object;
            this.type = getTypeFor(object);
        }
    }
    
    
    protected Serializable createByType(Type type) {
        switch (type) {
            case BOOLEAN:
                return Boolean.valueOf(false);
            case BYTE:
                return Byte.valueOf((byte)0);
            case SHORT:
                return Short.valueOf((short)0);
            case INT:
                return Integer.valueOf(0);
            case LONG:
                return Long.valueOf(0);
            case FLOAT:
                return Float.valueOf(0);
            case DOUBLE:
                return Double.valueOf(0);
            case CHAR:
                return Character.valueOf('\0');
            case STRING:
                return "";
            case MAP:
                return new ValueMap();
            case LIST:
                return new ValueList();
            case SET:
                return new ValueSet();
            case PERIOD:
                return new Period();
            case TIMINGSET:
                return new TimingSet();
            case TIMELIMIT:
                return new NeverTimeLimit();
            case TAG:
                return new Tag();
            case RESOURCE:
                return new Resource();
            case RESOURCESUBSET:
                return new ResourceSubset.Whole(new Resource());
            case BLOCK:
                return new Block();
            case ACTIVITY:
                return new Activity();
            case TIMETABLE:
                return new Board();
            case NULL:
            default:
                return null;
        }
    }
    
    protected Type getTypeFor(Object object) {
        if (object == null) {
            return Type.NULL;
        } else if (object instanceof Boolean) {
            return Type.BOOLEAN;
        } else if (object instanceof Byte) {
            return Type.BYTE;
        } else if (object instanceof Short) {
            return Type.SHORT;
        } else if (object instanceof Integer) {
            return Type.INT;
        } else if (object instanceof Long) {
            return Type.LONG;
        } else if (object instanceof Float) {
            return Type.FLOAT;
        } else if (object instanceof Double) {
            return Type.DOUBLE;
        } else if (object instanceof Character) {
            return Type.CHAR;
        } else if (object instanceof String) {
            return Type.STRING;
        } else if (object instanceof ValueMap) {
            return Type.MAP;
        } else if (object instanceof ValueList) {
            return Type.LIST;
        } else if (object instanceof ValueSet) {
            return Type.SET;
        } else if (object instanceof Period) {
            return Type.PERIOD;
        } else if (object instanceof TimingSet) {
            return Type.TIMINGSET;
        } else if (object instanceof TimeLimit) {
            return Type.TIMELIMIT;
        } else if (object instanceof Tag) {
            return Type.TAG;
        } else if (object instanceof Resource) {
            return Type.RESOURCE;
        } else if (object instanceof ResourceSubset) {
            return Type.RESOURCESUBSET;
        } else if (object instanceof Block) {
            return Type.BLOCK;
        } else if (object instanceof Activity) {
            return Type.ACTIVITY;
        } else if (object instanceof Board) {
            return Type.TIMETABLE;
        } else {
            throw new IllegalArgumentException(String.format("Unknown type: %s", object.getClass().getName()));
        }
    }
    
    public Type getType() {
        return type;
    }
    
    public boolean isNull() {
        return (type == Type.NULL);
    }

    public Object get() {
        return value;
    }
    
    public Boolean getAsBoolean() {
        if (value == null) {
            return false;
        } else if (value instanceof Boolean) {
            return (Boolean)value;
        } else {
            return (getAsNumber().floatValue() != 0);
        }
    }
    
    public Number getAsNumber() {
        if (value == null) {
            return 0;
        } else if (value instanceof Number) {
            return (Number) value;
        } else if (value instanceof Boolean) {
            return ((Boolean) value).booleanValue() ? 1 : 0;
        } else if (value instanceof String) {
            int result = 0;
            try {
                result = Integer.parseInt((String)value);
            } catch (NumberFormatException e) {
                // zero will be used
            }
            return result;
        } else {
            return 0;
        }
    }

    public int getAsIndex(int fallback) {
        if (value == null) {
            return fallback;
        } else if (value instanceof Number) {
            return ((Number) value).intValue();
        } else if (value instanceof Boolean) {
            return ((Boolean) value).booleanValue() ? 1 : 0;
        } else if (value instanceof String) {
            int result = fallback;
            try {
                result = Integer.parseInt((String)value);
            } catch (NumberFormatException e) {
                // fallback will be used
            }
            return result;
        } else {
            return fallback;
        }
    }
    
    public Byte getAsByte() {
        return getAsNumber().byteValue();
    }

    public Short getAsShort() {
        return getAsNumber().shortValue();
    }

    public Integer getAsInt() {
        return getAsNumber().intValue();
    }

    public Long getAsLong() {
        return getAsNumber().longValue();
    }

    public Float getAsFloat() {
        return getAsNumber().floatValue();
    }

    public Double getAsDouble() {
        return getAsNumber().doubleValue();
    }

    public Character getAsChar() {
        String stringValue = getAsString();
        if (stringValue.length() > 0) {
            return stringValue.charAt(0);
        } else {
            return '\0';
        }
    }
    
    public String getAsString() {
        if (value != null) {
            return value.toString();
        } else {
            return "";
        }
    }

    public Object getAsObject() {
        if (value == null) {
            return new Object();
        } else {
            return value;
        }
    }
    
    public ValueMap getAsMap() {
        if (type == Type.MAP) {
            return (ValueMap)value;
        } else {
            return new ValueMap();
        }
    }

    public ValueList getAsList() {
        if (type == Type.LIST) {
            return (ValueList)value;
        } else if (type == Type.SET) {
            return new ValueList((ValueSet)value);
        } else {
            return new ValueList();
        }
    }

    public ValueSet getAsSet() {
        if (type == Type.SET) {
            return (ValueSet)value;
        } else if (type == Type.LIST) {
            return new ValueSet((ValueList)value);
        } else {
            return new ValueSet();
        }
    }
    
    public Period getAsPeriod() {
        if (type == Type.PERIOD) {
            return (Period)value;
        } else {
            return new Period();
        }
    }
    
    public TimingSet getAsTimingSet() {
        if (type == Type.TIMINGSET) {
            return (TimingSet)value;
        } else {
            return new TimingSet();
        }
    }
    
    public TimeLimit getAsTimeLimit() {
        if (type == Type.TIMELIMIT) {
            return (TimeLimit)value;
        } else {
            return new NeverTimeLimit();
        }
    }
    
    public Tag getAsTag() {
        if (type == Type.TAG) {
            return (Tag)value;
        } else {
            return new Tag();
        }
    }
    
    public Resource getAsResource() {
        if (type == Type.RESOURCE) {
            return (Resource)value;
        } else {
            return new Resource();
        }
    }
    
    public ResourceSubset getAsResourceSubset() {
        if (type == Type.RESOURCESUBSET) {
            return (ResourceSubset)value;
        } else {
            return new ResourceSubset.Whole(new Resource());
        }
    }

    public Block getAsBlock() {
        if (type == Type.BLOCK) {
            return (Block)value;
        } else {
            return new Block();
        }
    }

    public Activity getAsActivity() {
        if (type == Type.ACTIVITY) {
            return (Activity)value;
        } else {
            return new Activity();
        }
    }

    public Board getAsBoard() {
        if (type == Type.TIMETABLE) {
            return (Board)value;
        } else {
            return new Board();
        }
    }
    
    public void walk(WalkCallback callback) {
        walk(this, null, new Value[] {}, callback);
    }
    
    private void walk(Value value, Value parentValue, Value[] path, WalkCallback callback) {
        Value.Type valueType = value.getType();
        if (valueType == Value.Type.MAP) {
            for (Map.Entry<Value, Value> entry: value.getAsMap().entrySet()) {
                Value keyValue = entry.getKey();
                Value valueValue = entry.getValue();
                walk(valueValue, keyValue, extendValueArray(path, keyValue), callback);
            }
        } else if (valueType == Value.Type.LIST || valueType == Value.Type.SET) {
            int i = 0;
            for (Value subValue: value.getAsList()) {
                walk(subValue, value, extendValueArray(path, new Value(i)), callback);
                i++;
            }
        }
        callback.touch(value, parentValue, Arrays.copyOf(path, path.length));
    }
    
    private Value[] extendValueArray(Value[] baseArray, Value valueToAppend) {
        Value[] result = Arrays.copyOf(baseArray, baseArray.length + 1);
        result[baseArray.length] = valueToAppend;
        return result;
    }
    
    public Access getAccess(String path) {
        return new Access(path);
    }

    public Access getAccess(String path, Object... bindings) {
        return new Access(path, bindings);
    }

    public Access getAccess(String path, Value... bindings) {
        return new Access(path, bindings);
    }

    public Access getAccess(List<Value> pathList) {
        return new Access(pathList);
    }
    
    public Access getAccess(Value... pathArray) {
        return new Access(pathArray);
    }

    public Access getAccess(Object[] pathObjectArray) {
        return new Access(pathObjectArray);
    }
    
    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (other instanceof Value) {
            Value otherValue = (Value)other;
            if (this.isNull()) {
                return otherValue.isNull();
            }
            return (this.type == otherValue.type && this.value.equals(otherValue.value));
        } else {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        if (value != null) {
            return value.hashCode();
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return getAsString();
    }
    

    public static Value[] wrapToValues(Object... objects) {
        Value[] result = new Value[objects.length];
        int i = 0;
        for (Object object: objects) {
            result[i++] = new Value(object);
        }
        return result;
    }
    
    
    public interface WalkCallback {
        
        public void touch(Value value, Value parentValue, Value[] path);
        
    }
    
    
    public class ValueMap extends TreeMap<Value, Value> {
        
        private static final long serialVersionUID = 1L;

        
        protected ValueMap() {
            super(new ValueComparator());
        }

        protected ValueMap(Map<Value, Value> map) {
            super(new ValueComparator());
            putAll(map);
        }
        
        
        public void putRaw(Object keyObject, Object valueObject) {
            put(new Value(keyObject), new Value(valueObject));
        }

        public void putAllRaw(Map<? extends Object, ? extends Object> map) {
            for (Map.Entry<? extends Object, ? extends Object> entry: map.entrySet()) {
                put(new Value(entry.getKey()), new Value(entry.getValue()));
            }
        }
        
        @Override
        public ValueSet keySet() {
            return new ValueSet(super.keySet());
        }

        @Override
        public ValueList values() {
            return new ValueList(super.values());
        }
        
    }

    
    public class ValueList extends ArrayList<Value> {
        
        private static final long serialVersionUID = 1L;

        
        protected ValueList() {
            super();
        }

        protected ValueList(Collection<Value> values) {
            super(values);
        }
        
        
        public void addRaw(Object object) {
            add(new Value(object));
        }

        public void addRaw(int index, Object object) {
            add(index, new Value(object));
        }

        public void addAllRaw(Collection<Object> objects) {
            for (Object object: objects) {
                addRaw(object);
            }
        }
        
    }
    

    public class ValueSet extends TreeSet<Value> {
        
        private static final long serialVersionUID = 1L;

        
        protected ValueSet() {
            super(new ValueComparator());
        }

        protected ValueSet(Collection<Value> values) {
            super(new ValueComparator());
            addAll(values);
        }
        
        
        public void addRaw(Object object) {
            add(new Value(object));
        }

        public void addAllRaw(Collection<? extends Object> objects) {
            for (Object object: objects) {
                addRaw(object);
            }
        }
        
    }
    
    
    public class ValueComparator implements Comparator<Value>, Serializable {
        
        private static final long serialVersionUID = 1L;

        @Override
        public int compare(Value value1, Value value2) {
            Value.Type type1 = value1.getType();
            Value.Type type2 = value2.getType();
            int typeResult = type1.compareTo(type2);
            if (typeResult != 0) {
                return typeResult;
            }
            switch (type1) {
                case NULL:
                    return 0;
                case BOOLEAN:
                    return value1.getAsBoolean().compareTo(value2.getAsBoolean());
                case CHAR:
                case BYTE:
                case SHORT:
                case INT:
                case LONG:
                    return value1.getAsLong().compareTo(value2.getAsLong());
                case FLOAT:
                case DOUBLE:
                    return value1.getAsDouble().compareTo(value2.getAsDouble());
                case STRING:
                    return value1.getAsString().compareTo(value2.getAsString());
                case LIST:
                case SET:
                    return compareLists(value1.getAsList(), value2.getAsList());
                case MAP:
                    ValueMap map1 = value1.getAsMap();
                    ValueMap map2 = value2.getAsMap();
                    int keyCmp = compareLists(new ValueList(map1.keySet()), new ValueList(map2.keySet()));
                    if (keyCmp != 0) {
                        return keyCmp;
                    }
                    return compareLists(map1.values(), map2.values());
                default:
                    Object object1 = value1.get();
                    Object object2 = value2.get();
                    if (object1 instanceof Labeled) {
                        if (object2 instanceof Labeled) {
                            return ((Labeled)object1).getLabel().compareTo(((Labeled)object2).getLabel());
                        } else {
                            return 1;
                        }
                    } else if (object2 instanceof Labeled) {
                        return -1;
                    } else {
                        return Integer.compare(System.identityHashCode(object1), System.identityHashCode(object2));
                    }
            }
        }
        
        private int compareLists(ValueList list1, ValueList list2) {
            int size1 = list1.size();
            int size2 = list2.size();
            int commonSize = Math.min(size1, size2);
            for (int i = 0; i < commonSize; i++) {
                int cmp = compare(list1.get(i), list2.get(i));
                if (cmp != 0) {
                    return cmp;
                }
            }
            return Integer.compare(size1, size2);
        }
        
    }

    public class Access {
        
        public static final char PATH_ESCAPER = '\\';
        public static final char PATH_SEPARATOR = '.';
        public static final char PATH_INDEX_BEGIN = '[';
        public static final char PATH_INDEX_END = ']';
        public static final char PATH_BINDING_BEGIN = '{';
        public static final char PATH_BINDING_END = '}';

        
        public final List<Value> pathList;

        
        public Access(String path, Object... bindings) {
            this(path, wrapToValues(bindings));
        }
        
        public Access(String path, Value... bindings) {
            List<String> pathTokens = tokenizePath(path);
            this.pathList = new ArrayList<Value>(pathTokens.size());
            for (String token: pathTokens) {
                Value nextValue;
                if (token.length() < 2) {
                    nextValue = new Value(token);
                } else {
                    char firstChar = token.charAt(0);
                    if (firstChar == PATH_INDEX_BEGIN) {
                        String indexToken = token.substring(1, token.length() - 1);
                        if (indexToken.isEmpty()) {
                            nextValue = new Value(Value.Type.NULL);
                        } else {
                            int index = -1;
                            try {
                                index = Integer.parseInt(indexToken);
                            } catch(NumberFormatException e) {
                                // -1 will be used
                            }
                            if (index >= 0) {
                                nextValue = new Value(Integer.valueOf(index));
                            } else {
                                nextValue = new Value(indexToken);
                            }
                        }
                    } else if (firstChar == PATH_BINDING_BEGIN) {
                        String bindingIndexToken = token.substring(1, token.length() - 1);
                        int bindingIndex = -1;
                        try {
                            bindingIndex = Integer.parseInt(bindingIndexToken);
                        } catch(NumberFormatException e) {
                            // will be handled later
                        }
                        if (bindingIndex < 0 || bindingIndex >= bindings.length) {
                            throw new AccessPathSyntaxException("Wrong binding index: " + bindingIndexToken);
                        }
                        nextValue = bindings[bindingIndex];
                    } else {
                        nextValue = new Value(token);
                    }
                }
                this.pathList.add(nextValue);
            }
        }
        
        public Access(List<Value> pathList) {
            this.pathList = pathList;
        }
        
        public Access(Value... pathArray) {
            this.pathList = Arrays.asList(pathArray);
        }

        public Access(Object[] pathObjectArray) {
            this.pathList = new ArrayList<Value>();
            for (Object object: pathObjectArray) {
                this.pathList.add(new Value(object));
            }
        }
        
        
        public boolean exists() {
            RouteResult routeResult = route();
            return routeResult.exists();
        }
        
        public void remove() {
            RouteResult routeResult = route();
            if (routeResult.exists()) {
                Value parentKey = pathList.get(pathList.size() - 1);
                List<Value> existingParents = routeResult.getExistingParents();
                Value parentValue = existingParents.get(existingParents.size() - 1);
                switch (parentValue.getType()) {
                    case MAP:
                        parentValue.getAsMap().remove(parentKey);
                        break;
                    case LIST:
                        parentValue.getAsList().remove((int)parentKey.getAsInt());
                        break;
                    case SET:
                        int i = 0;
                        int nth = (int) parentKey.getAsInt();
                        Iterator<Value> iterator = parentValue.getAsList().iterator();
                        while (iterator.hasNext()) {
                            iterator.next();
                            if (i == nth) {
                                iterator.remove();
                                break;
                            }
                            i++;
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        
        public Value get() {
            RouteResult routeResult = route();
            if (routeResult.exists()) {
                return routeResult.getValue();
            } else {
                return new Value(Type.NULL);
            }
        }

        public Value get(Type requiredType) {
            RouteResult routeResult = route();
            if (routeResult.exists()) {
                return routeResult.getValue();
            } else {
                return new Value(requiredType);
            }
        }

        public Value get(Value fallbackValue) {
            RouteResult routeResult = route();
            if (routeResult.exists()) {
                return routeResult.getValue();
            } else {
                return fallbackValue;
            }
        }

        public Value getReferenced() {
            RouteResult routeResult = route();
            if (routeResult.exists()) {
                return routeResult.getValue();
            } else {
                Value fallbackValue = new Value();
                set(fallbackValue);
                return fallbackValue;
            }
        }

        public Value getReferenced(Type requiredType) {
            RouteResult routeResult = route();
            if (routeResult.exists()) {
                return routeResult.getValue();
            } else {
                Value fallbackValue = new Value(requiredType);
                set(fallbackValue);
                return fallbackValue;
            }
        }

        public Value getReferenced(Value fallbackValue) {
            RouteResult routeResult = route();
            if (routeResult.exists()) {
                return routeResult.getValue();
            } else {
                set(fallbackValue);
                return fallbackValue;
            }
        }

        public Value getReferenced(Type requiredType, Value fallbackValue) {
            RouteResult routeResult = route();
            Value foundValue = routeResult.getValue();
            if (routeResult.exists() && foundValue.getType().equals(requiredType)) {
                return foundValue;
            } else {
                set(fallbackValue);
                return fallbackValue;
            }
        }

        public boolean contains(Object object) {
            return contains(new Value(object));
        }

        public boolean contains(Value value) {
            Value parentValue = get();
            switch (parentValue.getType()) {
                case MAP:
                    return parentValue.getAsMap().containsValue(value);
                case LIST:
                    return parentValue.getAsList().contains(value);
                case SET:
                    return parentValue.getAsSet().contains(value);
                default:
                    return false;
            }
        }

        public void set(Object object) {
            set(new Value(object));
        }

        public void set(Type type) {
            set(new Value(type));
        }
        
        public void set(Value value) {
            setValue(Value.this, value, pathList);
        }
        
        protected void setValue(Value value, Value valueToSet, List<Value> pathList) {
            if (pathList.isEmpty()) {
                value.value = valueToSet.value;
                value.type = valueToSet.type;
            } else {
                Type valueType = value.getType();
                Value nextKey = pathList.get(0);
                List<Value> subPathList = pathList.subList(1, pathList.size());
                if (valueType == Type.MAP) {
                    ValueMap map = value.getAsMap();
                    Value subValue;
                    if (map.containsKey(nextKey)) {
                        subValue = map.get(nextKey);
                    } else {
                        subValue = new Value();
                        map.put(nextKey, subValue);
                    }
                    setValue(subValue, valueToSet, subPathList);
                } else if (valueType == Type.LIST) {
                    ValueList list = value.getAsList();
                    int indexKey = nextKey.getAsIndex(-1);
                    if (nextKey.isNull()) {
                        indexKey = list.size();
                    }
                    if (indexKey < 0) {
                        ValueMap map = value.new ValueMap();
                        value.value = map;
                        value.type = Type.MAP;
                        int listSize = list.size();
                        for (int i = 0; i < listSize; i++) {
                            map.put(new Value(i), list.get(i));
                        }
                        Value subValue;
                        if (map.containsKey(nextKey)) {
                            subValue = map.get(nextKey);
                        } else {
                            subValue = new Value();
                            map.put(nextKey, subValue);
                        }
                        setValue(subValue, valueToSet, subPathList);
                    } else {
                        Value subValue;
                        int listSize = list.size();
                        if (indexKey < listSize) {
                            subValue = list.get(indexKey);
                        } else {
                            subValue = new Value();
                            for (int i = listSize; i < indexKey; i++) {
                                list.add(new Value());
                            }
                            list.add(subValue);
                        }
                        setValue(subValue, valueToSet, subPathList);
                    }
                } else if (valueType == Type.SET) {
                    ValueSet set = value.getAsSet();
                    if (nextKey.isNull()) {
                        Value subValue = new Value();
                        setValue(subValue, valueToSet, subPathList);
                        set.remove(subValue);
                        set.add(subValue);
                    } else {
                        int nextIndex = nextKey.getAsIndex(-1);
                        int size = set.size();
                        if (nextIndex >= 0 && nextIndex < size) {
                            ValueList list = value.new ValueList();
                            value.value = list;
                            value.type = Type.LIST;
                            Value subValue = new Value();
                            for (Value existingValue: set) {
                                list.add(existingValue);
                            }
                            list.set(nextIndex, subValue);
                            setValue(subValue, valueToSet, subPathList);
                        } else {
                            ValueMap map = value.new ValueMap();
                            value.value = map;
                            value.type = Type.MAP;
                            {
                                int i = 0;
                                for (Value existingValue: set) {
                                    map.put(new Value(i), new Value(existingValue));
                                    i++;
                                }
                            }
                            Value subValue = new Value();
                            map.put(nextKey, subValue);
                            setValue(subValue, valueToSet, subPathList);
                        }
                    }
                } else {
                    if (nextKey.isNull()) {
                        ValueSet set = value.new ValueSet();
                        value.value = set;
                        value.type = Type.SET;
                        Value subValue = new Value();
                        set.add(subValue);
                        setValue(subValue, valueToSet, subPathList);
                    } else {
                        int intNextKey = nextKey.getAsInt();
                        if (nextKey.getType() == Type.INT && intNextKey >= 0) {
                            ValueList list = value.new ValueList();
                            value.value = list;
                            value.type = Type.LIST;
                            Value subValue = new Value();
                            for (int i = 0; i < intNextKey; i++) {
                                list.add(new Value());
                            }
                            list.add(subValue);
                            setValue(subValue, valueToSet, subPathList);
                        } else {
                            ValueMap map = value.new ValueMap();
                            value.value = map;
                            value.type = Type.MAP;
                            Value subValue = new Value();
                            map.put(nextKey, subValue);
                            setValue(subValue, valueToSet, subPathList);
                        }
                    }
                }
            }
        }
        
        protected List<String> tokenizePath(String path) {
            List<String> result = new ArrayList<String>();
            
            if (path.isEmpty()) {
                result.add("");
                return result;
            } else if (path.equals("" + PATH_ESCAPER)) {
                return result;
            }

            final int MODE_STATUS_INITIAL = 0;
            final int MODE_STATUS_CLOSED = 1;
            final int MODE_STATUS_STRING = 2;
            final int MODE_STATUS_INDEX = 3;
            final int MODE_STATUS_BINDING = 4;
            
            boolean escapedStatus = false;
            int modeStatus = MODE_STATUS_INITIAL;
            StringBuilder itemBuilder = new StringBuilder();
            int pathLength = path.length();
            for (int i = 0; i < pathLength; i++) {
                char c = path.charAt(i);
                if (escapedStatus) {
                    switch (c) {
                        case 'n':
                            itemBuilder.append('\n');
                            break;
                        case 'r':
                            itemBuilder.append('\r');
                            break;
                        case 't':
                            itemBuilder.append('\t');
                            break;
                        case '0':
                            itemBuilder.append('\0');
                            break;
                        default:
                            itemBuilder.append(c);
                    }
                    escapedStatus = false;
                } else if (c == PATH_ESCAPER) {
                    escapedStatus = true;
                } else if (
                    modeStatus == MODE_STATUS_INITIAL ||
                    modeStatus == MODE_STATUS_CLOSED
                ) {
                    if (c == PATH_INDEX_BEGIN) {
                        itemBuilder.append(c);
                        modeStatus = MODE_STATUS_INDEX;
                    } else if (c == PATH_BINDING_BEGIN) {
                        itemBuilder.append(c);
                        modeStatus = MODE_STATUS_BINDING;
                    } else if (c == PATH_SEPARATOR) {
                        if (modeStatus == MODE_STATUS_INITIAL) {
                            result.add("");
                        }
                        modeStatus = MODE_STATUS_STRING;
                    } else {
                        if (modeStatus == MODE_STATUS_CLOSED) {
                            throw new AccessPathSyntaxException("Unexpected character at " + i);
                        }
                        modeStatus = MODE_STATUS_STRING;
                        itemBuilder.append(c);
                    }
                } else if (modeStatus == MODE_STATUS_STRING) {
                    if (c == PATH_INDEX_BEGIN) {
                        jumpTokenizerWith(result, itemBuilder, c);
                        modeStatus = MODE_STATUS_INDEX;
                    } else if (c == PATH_BINDING_BEGIN) {
                        jumpTokenizerWith(result, itemBuilder, c);
                        modeStatus = MODE_STATUS_BINDING;
                    } else if (c == PATH_SEPARATOR) {
                        jumpTokenizer(result, itemBuilder);
                        modeStatus = MODE_STATUS_STRING;
                    } else {
                        itemBuilder.append(c);
                    }
                } else if (modeStatus == MODE_STATUS_INDEX) {
                    itemBuilder.append(c);
                    if (c == PATH_INDEX_END) {
                        modeStatus = MODE_STATUS_CLOSED;
                        jumpTokenizer(result, itemBuilder);
                    }
                } else { // modeStatus == MODE_STATUS_BINDING
                    itemBuilder.append(c);
                    if (c == PATH_BINDING_END) {
                        jumpTokenizer(result, itemBuilder);
                        modeStatus = MODE_STATUS_CLOSED;
                    }
                }
            }
            if (modeStatus != MODE_STATUS_CLOSED) {
                if (modeStatus != MODE_STATUS_STRING) {
                    throw new AccessPathSyntaxException("Unexpected end");
                }
                result.add(itemBuilder.toString());
            }
            return result;
        }

        private void jumpTokenizer(List<String> stringList, StringBuilder stringBuilder) {
            stringList.add(stringBuilder.toString());
            stringBuilder.setLength(0);
        }

        private void jumpTokenizerWith(List<String> stringList, StringBuilder stringBuilder, char appendChar) {
            jumpTokenizer(stringList, stringBuilder);
            stringBuilder.append(appendChar);
        }
        
        protected RouteResult route() {
            return routeValue(Value.this, pathList);
        }
        
        private RouteResult routeValue(Value value, List<Value> pathList) {
            if (pathList.isEmpty()) {
                return new RouteResult(true, value);
            }
            Value nextKey = pathList.get(0);
            List<Value> subPathList = pathList.subList(1, pathList.size());
            Value.Type valueType = value.getType();
            if (valueType == Value.Type.LIST) {
                int index = nextKey.getAsIndex(-1);
                List<Value> list = value.getAsList();
                if (index < 0 || index >= list.size()) {
                    return new RouteResult(false, null);
                }
                Value subValue = list.get(index);
                RouteResult subResult = routeValue(subValue, subPathList);
                subResult.wrapParent(value);
                return subResult;
            } else if (valueType == Value.Type.MAP) {
                Map<Value, Value> map = value.getAsMap();
                if (!map.containsKey(nextKey)) {
                    return new RouteResult(false, null);
                }
                Value subValue = map.get(nextKey);
                RouteResult subResult = routeValue(subValue, subPathList);
                subResult.wrapParent(value);
                return subResult;
            } else if (valueType == Value.Type.SET) {
                Set<Value> set = value.getAsSet();
                if (!nextKey.isNull()) {
                    if (nextKey.getType() == Value.Type.INT) {
                        int index = nextKey.getAsInt();
                        if (index < set.size()) {
                            int i = 0;
                            for (Value subValue: set) {
                                if (i == index) {
                                    return routeValue(subValue, subPathList);
                                }
                                i++;
                            }
                        }
                    }
                    return new RouteResult(false, null);
                }
                for (Value subValue: set) {
                    RouteResult subResult = routeValue(subValue, subPathList);
                    if (subResult.exists()) {
                        subResult.wrapParent(value);
                        return subResult;
                    }
                }
                return new RouteResult(false, null);
                
            } else {
                return new RouteResult(false, null);
            }
        }
        
        
        private class RouteResult {
            
            public final boolean exists;
            
            public final Value value;
            
            public final List<Value> existingParents = new LinkedList<Value>();

            
            public RouteResult(boolean exists, Value value) {
                this.exists = exists;
                this.value = value;
            }
            
            // TODO: use with set() ...
            public RouteResult(boolean exists, Value value, List<Value> existingParents) {
                this.exists = exists;
                this.value = value;
                this.existingParents.addAll(existingParents);
            }
            
            public void wrapParent(Value value) {
                existingParents.add(0, value);
            }
            
            public boolean exists() {
                return exists;
            }
            
            public Value getValue() {
                return value;
            }

            public List<Value> getExistingParents() {
                return existingParents;
            }
            
        }
        
        public class AccessPathSyntaxException extends RuntimeException {
            
            private static final long serialVersionUID = 1L;

            public AccessPathSyntaxException(String message) {
                super(message);
            }
            
        }

    }
    
}
