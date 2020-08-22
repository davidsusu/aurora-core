package hu.webarticum.aurora.core.model;

import java.util.ArrayList;
import java.util.List;

public class Document implements Labeled {

    private static final long serialVersionUID = 1L;

    
    private String label = "";

    private final PeriodStore periodStore = new PeriodStore();

    private final TimingSetStore timingSetStore = new TimingSetStore();
    
    private final TagStore tagStore = new TagStore();

    private final ResourceStore resourceStore = new ResourceStore();
    
    private final BlockStore blockStore = new BlockStore();

    private final BoardStore boardStore = new BoardStore();
    
    private final Value extraData = new Value();
    
    
    public Document() {
    }
    
    public Document(String label) {
        this.label = label;
    }
    
    
    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return label;
    }

    public PeriodStore getPeriodStore() {
        return periodStore;
    }

    public TimingSetStore getTimingSetStore() {
        return timingSetStore;
    }
    
    public TagStore getTagStore() {
        return tagStore;
    }

    public ResourceStore getResourceStore() {
        return resourceStore;
    }

    public BlockStore getBlockStore() {
        return blockStore;
    }

    public BoardStore getBoardStore() {
        return boardStore;
    }
    
    public Value getExtraData() {
        return extraData;
    }

    @Override
    public String toString() {
        return "Document: '" + getLabel() + "'";
    }

    
    public class PeriodStore extends Labeled.LabeledStore<Period> {

        private static final long serialVersionUID = 1L;

        public PeriodStore() {
            super(new Period.PeriodComparator());
        }

        public List<Period> getAll(int term) {
            List<Period> periods = new ArrayList<Period>();
            for (Period period: this) {
                if (period.getTerm() == term) {
                    periods.add(period);
                }
            }
            return periods;
        }

    }
    

    public class TagStore extends Labeled.LabeledStore<Tag> {

        private static final long serialVersionUID = 1L;

        
        public TagStore() {
            super(new Tag.TagComparator());
        }

        public List<Tag> getAll(Tag.Type type) {
            List<Tag> tags = new ArrayList<Tag>();
            for (Tag tag: this) {
                if (tag.getType().equals(type)) {
                    tags.add(tag);
                }
            }
            return tags;
        }

        @Override
        protected void removed(String id, Tag item) {
            super.removed(id, item);
            for (Block block: blockStore) {
                for (Activity activity: block.getActivityManager().getActivities()) {
                    activity.getTagManager().remove(item);
                }
            }
        }

    }
    

    public class ResourceStore extends Labeled.LabeledStore<Resource> {

        private static final long serialVersionUID = 1L;

        public ResourceStore() {
            super(new Resource.ResourceComparator());
        }

        public List<Resource> getAll(Resource.Type type) {
            List<Resource> resources = new ArrayList<Resource>();
            for (Resource resource: this) {
                if (resource.getType().equals(type)) {
                    resources.add(resource);
                }
            }
            return resources;
        }

    }
    

    public class BlockStore extends Labeled.LabeledStore<Block> {

        private static final long serialVersionUID = 1L;

        @Override
        public BlockList getAll() {
            return new BlockList(super.getAll());
        }

        @Override
        protected void removed(String id, Block item) {
            super.removed(id, item);
            for (Board board: boardStore) {
                board.remove(item);
            }
        }

    }
    

    public class BoardStore extends Labeled.LabeledStore<Board> {

        private static final long serialVersionUID = 1L;

    }
    

    public class TimingSetStore extends Labeled.LabeledStore<TimingSet> {

        private static final long serialVersionUID = 1L;

    }

}
