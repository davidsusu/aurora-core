package hu.webarticum.aurora.core.model;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class Resource extends Aspect {

    private static final long serialVersionUID = 1L;

    private Type type = Type.OTHER;
    
    private String email = "";

    private int quantity = 1;
    
    private SplittingManager splittingManager = this.new SplittingManager();
    
    public enum Type {
        CLASS, PERSON, LOCALE, OBJECT, OTHER
    }

    public Resource() {
        super();
    }

    public Resource(Type type) {
        super();
        this.type = type;
    }

    public Resource(String label) {
        super(label);
    }

    public Resource(Type type, String label) {
        super(label);
        this.type = type;
    }

    public Resource(Type type, String label, Color color) {
        super(label, color);
        this.type = type;
    }

    public Resource(Type type, String label, String acronym, Color color) {
        super(label, acronym, color);
        this.type = type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }
    
    public boolean hasSplittings() {
        return !splittingManager.splittings.isEmpty();
    }
    
    public SplittingManager getSplittingManager() {
        return splittingManager;
    }
    
    @Override
    public String toString() {
        return "Resource: '" + getLabel() + "'";
    }

    public class SplittingManager implements Serializable {

        private static final long serialVersionUID = 1L;

        private final List<Splitting> splittings = new ArrayList<Splitting>();
        
        public Resource getResource() {
            return Resource.this;
        }
        
        public List<Splitting> getSplittings() {
            return splittings;
        }
        
        public List<Resource.Splitting.Part> getSplittingParts() {
            List<Resource.Splitting.Part> splittingParts = new ArrayList<Resource.Splitting.Part>();
            for (Resource.Splitting splitting : splittings) {
                for (Resource.Splitting.Part splittingPart : splitting.getParts()) {
                    splittingParts.add(splittingPart);
                }
            }
            return splittingParts;
        }
        
        public void add(Splitting splitting) {
            splittings.add(splitting);
        }

        public Splitting add(String label) {
            return add(label, new String[]{});
        }

        public Splitting add(String label, Collection<String> partlabels) {
            Splitting splitting = Resource.this.new Splitting(label, partlabels);
            splittings.add(splitting);
            return splitting;
        }

        public Splitting add(String label, String[] partlabels) {
            Splitting splitting = Resource.this.new Splitting(label, partlabels);
            splittings.add(splitting);
            return splitting;
        }
        
        @Override
        public String toString() {
            StringBuilder resultBuilder = new StringBuilder();
            resultBuilder.append("Resource.SplittingManager: '" + getResource().getLabel() + "' [ ");
            boolean first = true;
            for (Splitting splitting: splittings) {
                if (first) {
                    first = false;
                } else {
                    resultBuilder.append(", ");
                }
                resultBuilder.append(splitting.toString());
            }
            resultBuilder.append(" ]");
            return resultBuilder.toString();
        }
        
    }
    
    public class Splitting implements Labeled {

        private static final long serialVersionUID = 1L;

        private String label = "";
        
        private List<Part> parts = new ArrayList<Part>();

        public Splitting() {
        }
        
        public Splitting(String label) {
            this.label = label;
        }
        
        public Splitting(String label, Collection<String> partlabels) {
            this.label = label;
            for (String partlabel: partlabels) {
                parts.add(this.new Part(partlabel));
            }
        }

        public Splitting(String label, String[] partlabels) {
            this.label = label;
            for (String partlabel: partlabels) {
                parts.add(this.new Part(partlabel));
            }
        }
        
        public Resource getResource() {
            return Resource.this;
        }
        
        public void addPart(Part part) {
            parts.add(part);
        }
        
        public Part addPart(String label) {
            Part part = new Part(label);
            parts.add(part);
            return part;
        }
        
        public List<Part> getParts() {
            return parts;
        }

        @Override
        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }
        
        @Override
        public String toString() {
            StringBuilder resultBuilder = new StringBuilder();
            resultBuilder.append("Resource.Splitting: ");
            resultBuilder.append("'" + getResource().getLabel() + "'/'" + label + "' { ");
            boolean first = true;
            for (Part part: parts) {
                if (first) {
                    first = false;
                } else {
                    resultBuilder.append(", ");
                }
                resultBuilder.append("'" + part.getLabel() + "'");
            }
            resultBuilder.append(" }");
            return resultBuilder.toString();
        }
        
        public class Part implements Labeled {

            private static final long serialVersionUID = 1L;

            private String label;
            
            public Part(String label) {
                this.label = label;
            }
            
            public Resource getResource() {
                return Resource.this;
            }
            
            public SplittingManager getSplittingManager() {
                return getResource().getSplittingManager();
            }
            
            public Splitting getSplitting() {
                return Splitting.this;
            }
            
            @Override
            public String getLabel() {
                return label;
            }

            public void setLabel(String label) {
                this.label = label;
            }
            
            @Override
            public String toString() {
                return "Resource.Splitting.Part: '"
                    + getResource().getLabel() + "'/'" + getSplitting().getLabel() + "'/'" + getLabel()
                + "'";
            }
            
        }

    }
    
    
    public static class ResourceComparator implements Comparator<Resource>, Serializable {
        
        private static final long serialVersionUID = 1L;

        @Override
        public int compare(Resource resource1, Resource resource2) {
            Type type1 = resource1.getType();
            Type type2 = resource2.getType();
            if (type1 == type2) {
                return Labeled.LabeledComparator.compareStatic(resource1, resource2);
            } else {
                return type1.compareTo(type2);
            }
        }
        
    }
    
}
