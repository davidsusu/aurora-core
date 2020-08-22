package hu.webarticum.aurora.core.model;
import java.io.Serializable;
import java.util.Comparator;

public class Tag extends Aspect {

    private static final long serialVersionUID = 1L;

    public enum Type { SUBJECT, LANGUAGE, OTHER }

    
    private Type type = Type.OTHER;
    
    
    public Tag() {
        super();
    }

    public Tag(Type type) {
        super();
        this.type = type;
    }

    public Tag(String label) {
        super(label);
    }

    public Tag(Type type, String label) {
        super(label);
        this.type = type;
    }

    public Tag(Type type, String label, Color color) {
        super(label, color);
        this.type = type;
    }

    public Tag(Type type, String label, String acronym, Color color) {
        super(label, acronym, color);
        this.type = type;
    }

    
    public void setType(Type type) {
        this.type = type;
    }
    
    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Tag: '" + getLabel() + "'";
    }
    
    
    public static class TagComparator implements Comparator<Tag>, Serializable {
        
        private static final long serialVersionUID = 1L;

        @Override
        public int compare(Tag tag1, Tag tag2) {
            Type type1 = tag1.getType();
            Type type2 = tag2.getType();
            if (type1 == type2) {
                return Labeled.LabeledComparator.compareStatic(tag1, tag2);
            } else {
                return type1.compareTo(type2);
            }
        }
        
    }
    
}
