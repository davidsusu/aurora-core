package hu.webarticum.aurora.core.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import hu.webarticum.aurora.core.model.time.Time;

class BoardTest {

    private Period week1;
    private Period week2;

    private Resource resource;
    
    private Activity activity11;
    private Activity activity21;
    private Activity activity31;
    private Activity activity41;
    private Activity activity42;
    
    private Block block1;
    private Block block2;
    private Block block3;
    private Block block4;

    private Board emptyBoard;
    private Board normalBoard1;
    private Board normalBoard2;
    private Board boardWithNegativeTime;
    private Board conflictingBoard;
    

    @Test
    void testSize() {
    	assertThat(emptyBoard.getEntries()).isEmpty();
    	assertThat(emptyBoard.size()).isZero();
    	assertThat(emptyBoard.isEmpty()).isTrue();
    	
    	assertThat(normalBoard1.getEntries()).hasSize(4);
    	assertThat(normalBoard1.size()).isEqualTo(4);
    	assertThat(normalBoard1.isEmpty()).isFalse();
    	
    	assertThat(normalBoard2.getEntries()).hasSize(1);
    	assertThat(normalBoard2.size()).isEqualTo(1);
    	assertThat(normalBoard2.isEmpty()).isFalse();
    	
    	assertThat(boardWithNegativeTime.getEntries()).hasSize(2);
    	assertThat(boardWithNegativeTime.size()).isEqualTo(2);
    	assertThat(boardWithNegativeTime.isEmpty()).isFalse();
    	
    	assertThat(conflictingBoard.getEntries()).hasSize(3);
    	assertThat(conflictingBoard.size()).isEqualTo(3);
    	assertThat(conflictingBoard.isEmpty()).isFalse();
    }
    
    @Test
    void testCopy() {
    	assertThat(normalBoard1.copy().getEntries()).isEqualTo(normalBoard1.getEntries());
    	assertThat(new Board(normalBoard1).getEntries()).isEqualTo(normalBoard1.getEntries());
    	
    	assertThat(normalBoard2.copy().getEntries()).isEqualTo(normalBoard2.getEntries());
    	assertThat(new Board(normalBoard2).getEntries()).isEqualTo(normalBoard2.getEntries());
    	
    	assertThat(boardWithNegativeTime.copy().getEntries()).isEqualTo(boardWithNegativeTime.getEntries());
    	assertThat(new Board(boardWithNegativeTime).getEntries()).isEqualTo(boardWithNegativeTime.getEntries());
    	
    	assertThat(conflictingBoard.copy().getEntries()).isEqualTo(conflictingBoard.getEntries());
    	assertThat(new Board(conflictingBoard).getEntries()).isEqualTo(conflictingBoard.getEntries());
    }

    @Test
    void testApplyFrom() {
        Block temporaryBlock = new Block();
        Board targetBoard = new Board();
        targetBoard.add(temporaryBlock, new Time(0));
        
        assertThat(targetBoard.contains(temporaryBlock)).isTrue();
        
        targetBoard.applyFrom(normalBoard1);

        assertThat(targetBoard.contains(temporaryBlock)).isFalse();
        assertThat(targetBoard.getEntries()).isEqualTo(normalBoard1.getEntries());
    }
    
    @Test
    void testGetBlocks() {
        assertThat(emptyBoard.getBlocks()).isEmpty();
        assertThat(normalBoard1.getBlocks()).containsExactly(block1, block2, block3, block4);
        assertThat(normalBoard2.getBlocks()).containsExactly(block1);
        assertThat(boardWithNegativeTime.getBlocks()).containsExactly(block1, block3);
        assertThat(conflictingBoard.getBlocks()).containsExactly(block1, block2, block4);
    }

    @Test
    void testGetPeriods() {
        assertThat((Set<Period>) emptyBoard.getPeriods()).isEmpty();
        assertThat((Set<Period>) normalBoard1.getPeriods()).containsExactlyInAnyOrder(week1, week2);
        assertThat((Set<Period>) normalBoard2.getPeriods()).containsExactlyInAnyOrder(week1);
        assertThat((Set<Period>) boardWithNegativeTime.getPeriods()).containsExactlyInAnyOrder(week1, week2);
        assertThat((Set<Period>) conflictingBoard.getPeriods()).containsExactlyInAnyOrder(week1, week2);
    }

    @Test
    void testBounds() {
        assertThat(emptyBoard.getStartTime()).isEqualTo(new Time(0));
        assertThat(emptyBoard.getLastTime()).isEqualTo(new Time(0));
        assertThat(emptyBoard.getEndTime()).isEqualTo(new Time(0));

        assertThat(normalBoard1.getStartTime()).isEqualTo(new Time(8 * Time.HOUR));
        assertThat(normalBoard1.getLastTime()).isEqualTo(new Time(11 * Time.HOUR));
        assertThat(normalBoard1.getEndTime()).isEqualTo(new Time(11 * Time.HOUR + Block.DEFAULT_LENGTH + Time.MINUTE));

        assertThat(normalBoard2.getStartTime()).isEqualTo(new Time(8 * Time.HOUR));
        assertThat(normalBoard2.getLastTime()).isEqualTo(new Time(8 * Time.HOUR));
        assertThat(normalBoard2.getEndTime()).isEqualTo(new Time(8 * Time.HOUR + Block.DEFAULT_LENGTH));

        assertThat(boardWithNegativeTime.getStartTime()).isEqualTo(new Time(-3 * Time.HOUR));
        assertThat(boardWithNegativeTime.getLastTime()).isEqualTo(new Time(7 * Time.HOUR));
        assertThat(boardWithNegativeTime.getEndTime()).isEqualTo(new Time(7 * Time.HOUR + Block.DEFAULT_LENGTH));

        assertThat(conflictingBoard.getStartTime()).isEqualTo(new Time(8 * Time.HOUR));
        assertThat(conflictingBoard.getLastTime()).isEqualTo(new Time(9 * Time.HOUR));
        assertThat(conflictingBoard.getEndTime()).isEqualTo(new Time(9 * Time.HOUR + Block.DEFAULT_LENGTH + Time.MINUTE));
    }

    @Test
    void testBasicOperationsOnCopy() {
        Board normalBoard1Copy = normalBoard1.copy();
        
        assertThat(normalBoard1Copy.getStartTime()).isEqualTo(new Time(8 * Time.HOUR));
        assertThat(normalBoard1Copy.getLastTime()).isEqualTo(new Time(11 * Time.HOUR));
        assertThat(normalBoard1Copy.getEndTime()).isEqualTo(new Time((11 * Time.HOUR) + (46 * Time.MINUTE)));

        normalBoard1Copy.add(new Block(Time.HOUR), new Time((11 * Time.HOUR) + (30 * Time.MINUTE)));
        normalBoard1Copy.add(new Board.Entry(new Block(10 * Time.MINUTE), new Time((11 * Time.HOUR) + (50 * Time.MINUTE))));

        assertThat(normalBoard1Copy.getStartTime()).isEqualTo(new Time(8 * Time.HOUR));
        assertThat(normalBoard1Copy.getLastTime()).isEqualTo(new Time((11 * Time.HOUR) + (50 * Time.MINUTE)));
        assertThat(normalBoard1Copy.getEndTime()).isEqualTo(new Time((12 * Time.HOUR) + (30 * Time.MINUTE)));

        normalBoard1Copy.remove(block1);

        assertThat(normalBoard1Copy.getStartTime()).isEqualTo(new Time((8 * Time.HOUR) + (30 * Time.MINUTE)));
        assertThat(normalBoard1Copy.getLastTime()).isEqualTo(new Time((11 * Time.HOUR) + (50 * Time.MINUTE)));
        assertThat(normalBoard1Copy.getEndTime()).isEqualTo(new Time((12 * Time.HOUR) + (30 * Time.MINUTE)));

        normalBoard1Copy.clear();
        assertThat(normalBoard1Copy.isEmpty()).isTrue();

        assertThat(normalBoard1.isEmpty()).isFalse();
        assertThat(normalBoard1.getStartTime()).isEqualTo(new Time(8 * Time.HOUR));
        assertThat(normalBoard1.getLastTime()).isEqualTo(new Time(11 * Time.HOUR));
        assertThat(normalBoard1.getEndTime()).isEqualTo(new Time((11 * Time.HOUR) + (46 * Time.MINUTE)));
    }

    @Test
    void testSearchBlock() {
        assertThat(normalBoard1.search(block1).getBlock()).isSameAs(block1);
        assertThat(normalBoard1.search(block1).getTime()).isEqualTo(new Time(8 * Time.HOUR));
        assertThat(normalBoard2.search(block2)).isNull();
    }

    @Test
    void testSearchActivity() {
        assertThat(normalBoard1.search(activity11).getBlock()).isSameAs(block1); 
    }
    
    /*
    Board(Board other) {
    Board copy() {
    void add(Block block, Time time) {
    void add(Entry entry) {
    void remove(Block block) {
    List<Entry> getEntries() {
    PeriodSet getPeriods() {
    BlockList getBlocks() {
    Entry search(Block block) {
    Entry search(Activity activity) {
    boolean contains(Block block) {
    void clear() {
    boolean isEmpty() {
    int size() {
    Time getStartTime() {
    Time getEndTime() {
    Time getLastTime() {
 x  Board limit(TimeLimit limit) {
 x  Board limitIntersection(TimeLimit limit) {
 x  Board filter(BlockFilter filter) {
    void applyFrom(Board other) {
 x  boolean hasConflicts() {
 x  boolean hasConflicts(Period period) {
 -  BlockIntersectionTable getConflictTable() {
 -  BlockIntersectionTable getConflictTable(Period period) {
 x  boolean conflictsWithBlockAt(Block block, Time time) {
 x  boolean conflictsWithBlockAt(Block block, Time time, boolean conflictSameBlock) {
 -  Map<Block, Time> toBlockTimeMap() {
 -  ActivityFlow toActivityFlow() {
 -  ActivityFlow toActivityFlow(Period period) {
 -  ActivityFlow toActivityFlow(Collection<Period> periods) {
 -  ActivityFlow toActivityFlow(Collection<Period> periods, boolean requireAll) {
 -  BlockIntersectionTable toBlockIntersectionTable() {
 x  Iterator<Entry> iterator() {
    */
    
    
    @BeforeEach
    public void buildThings() {
        buildPeriods();
        buildResources();
        buildBlocks();
        buildBoards();
    }
    
    private void buildPeriods() {
        week1 = new Period("Week A", 0);
        week2 = new Period("Week B", 1);
    }

    private void buildResources() {
        resource = new Resource(Resource.Type.OTHER, "Resource");
    }
    
    private void buildBlocks() {
        buildBlock1();
        buildBlock2();
        buildBlock3();
        buildBlock4();
    }

    private void buildBlock1() {
        block1 = new Block("Block 1");

        activity11 = new Activity("Study 1.1");
        activity11.getResourceManager().add(resource);
        block1.getActivityManager().add(activity11, week1);
    }

    private void buildBlock2() {
        block2 = new Block("Block 2");

        activity21 = new Activity("Study 2.1");
        activity21.getResourceManager().add(resource);
        block2.getActivityManager().add(activity21, week2);
    }

    private void buildBlock3() {
        block3 = new Block("Block 3");

        activity31 = new Activity("Study 3.1");
        activity31.getResourceManager().add(resource);
        block3.getActivityManager().add(activity31, week1, week2);
    }

    private void buildBlock4() {
        block4 = new Block("Block 4", Block.DEFAULT_LENGTH + Time.MINUTE);

        activity41 = new Activity("Study 4.1");
        activity41.getResourceManager().add(resource);
        block4.getActivityManager().add(activity41, week1);

        activity42 = new Activity("Study 4.2");
        activity42.getResourceManager().add(resource);
        block4.getActivityManager().add(activity42, week2);
    }
    
    private void buildBoards() {
        buildEmptyBoard();
        buildNormalBoard1();
        buildNormalBoard2();
        buildBoardWithNegativeTime();
        buildConflictingBoard();
    }

    private void buildEmptyBoard() {
        emptyBoard = new Board();
    }
    
    private void buildNormalBoard1() {
        normalBoard1 = new Board();
        normalBoard1.add(block1, new Time(8 * Time.HOUR));
        normalBoard1.add(block2, new Time(8 * Time.HOUR + 30 * Time.MINUTE));
        normalBoard1.add(block3, new Time(10 * Time.HOUR));
        normalBoard1.add(block4, new Time(11 * Time.HOUR));
    }

    private void buildNormalBoard2() {
        normalBoard2 = new Board();
        normalBoard2.add(block1, new Time(8 * Time.HOUR));
    }

    private void buildBoardWithNegativeTime() {
        boardWithNegativeTime = new Board();
        boardWithNegativeTime.add(block1, new Time(-3 * Time.HOUR));
        boardWithNegativeTime.add(block3, new Time(7 * Time.HOUR));
    }

    private void buildConflictingBoard() {
        conflictingBoard = new Board();
        conflictingBoard.add(block1, new Time(8 * Time.HOUR));
        conflictingBoard.add(block2, new Time(8 * Time.HOUR + 30 * Time.MINUTE));
        conflictingBoard.add(block4, new Time(9 * Time.HOUR));
    }

}
