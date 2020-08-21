package hu.webarticum.aurora.core.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import hu.webarticum.aurora.core.model.time.Time;

public class BoardTest {

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
    public void testSize() {
        assertEquals(0, emptyBoard.getEntries().size());
        assertEquals(0, emptyBoard.size());
        assertEquals(true, emptyBoard.isEmpty());

        assertEquals(4, normalBoard1.getEntries().size());
        assertEquals(4, normalBoard1.size());
        assertEquals(false, normalBoard1.isEmpty());

        assertEquals(1, normalBoard2.getEntries().size());
        assertEquals(1, normalBoard2.size());
        assertEquals(false, normalBoard2.isEmpty());

        assertEquals(2, boardWithNegativeTime.getEntries().size());
        assertEquals(2, boardWithNegativeTime.size());
        assertEquals(false, boardWithNegativeTime.isEmpty());

        assertEquals(3, conflictingBoard.getEntries().size());
        assertEquals(3, conflictingBoard.size());
        assertEquals(false, conflictingBoard.isEmpty());
    }
    
    @Test
    public void testCopy() {
        assertEquals(normalBoard1.getEntries(), normalBoard1.copy().getEntries());
        assertEquals(normalBoard1.getEntries(), new Board(normalBoard1).getEntries());

        assertEquals(normalBoard2.getEntries(), normalBoard2.copy().getEntries());
        assertEquals(normalBoard2.getEntries(), new Board(normalBoard2).getEntries());

        assertEquals(boardWithNegativeTime.getEntries(), boardWithNegativeTime.copy().getEntries());
        assertEquals(boardWithNegativeTime.getEntries(), new Board(boardWithNegativeTime).getEntries());

        assertEquals(conflictingBoard.getEntries(), conflictingBoard.copy().getEntries());
        assertEquals(conflictingBoard.getEntries(), new Board(conflictingBoard).getEntries());
    }

    @Test
    public void testApplyFrom() {
        Block temporaryBlock = new Block();
        Board targetBoard = new Board();
        targetBoard.add(temporaryBlock, new Time(0));
        
        assertTrue(targetBoard.contains(temporaryBlock));
        
        targetBoard.applyFrom(normalBoard1);

        assertFalse(targetBoard.contains(temporaryBlock));
        assertEquals(normalBoard1.getEntries(), targetBoard.getEntries());
    }
    
    @Test
    public void testGetBlocks() {
        assertTrue(emptyBoard.getBlocks().isEmpty());
        assertEquals(new BlockList(block1, block2, block3, block4), normalBoard1.getBlocks());
        assertEquals(new BlockList(block1), normalBoard2.getBlocks());
        assertEquals(new BlockList(block1, block3), boardWithNegativeTime.getBlocks());
        assertEquals(new BlockList(block1, block2, block4), conflictingBoard.getBlocks());
    }

    @Test
    public void testGetPeriods() {
        assertTrue(emptyBoard.getPeriods().isEmpty());
        assertEquals(new PeriodSet(week1, week2), normalBoard1.getPeriods());
        assertEquals(new PeriodSet(week1), normalBoard2.getPeriods());
        assertEquals(new PeriodSet(week1, week2), boardWithNegativeTime.getPeriods());
        assertEquals(new PeriodSet(week1, week2), conflictingBoard.getPeriods());
    }

    @Test
    public void testBounds() {
        assertEquals(new Time(0), emptyBoard.getStartTime());
        assertEquals(new Time(0), emptyBoard.getLastTime());
        assertEquals(new Time(0), emptyBoard.getEndTime());

        assertEquals(new Time(8 * Time.HOUR), normalBoard1.getStartTime());
        assertEquals(new Time(11 * Time.HOUR), normalBoard1.getLastTime());
        assertEquals(new Time(11 * Time.HOUR + Block.DEFAULT_LENGTH + Time.MINUTE), normalBoard1.getEndTime());

        assertEquals(new Time(8 * Time.HOUR), normalBoard2.getStartTime());
        assertEquals(new Time(8 * Time.HOUR), normalBoard2.getLastTime());
        assertEquals(new Time(8 * Time.HOUR + Block.DEFAULT_LENGTH), normalBoard2.getEndTime());

        assertEquals(new Time(-3 * Time.HOUR), boardWithNegativeTime.getStartTime());
        assertEquals(new Time(7 * Time.HOUR), boardWithNegativeTime.getLastTime());
        assertEquals(new Time(7 * Time.HOUR + Block.DEFAULT_LENGTH), boardWithNegativeTime.getEndTime());

        assertEquals(new Time(8 * Time.HOUR), conflictingBoard.getStartTime());
        assertEquals(new Time(9 * Time.HOUR), conflictingBoard.getLastTime());
        assertEquals(new Time(9 * Time.HOUR + Block.DEFAULT_LENGTH + Time.MINUTE), conflictingBoard.getEndTime());
    }

    @Test
    public void testBasicOperationsOnCopy() {
        Board normalBoard1Copy = normalBoard1.copy();
        
        assertEquals(new Time(8 * Time.HOUR), normalBoard1Copy.getStartTime());
        assertEquals(new Time(11 * Time.HOUR), normalBoard1Copy.getLastTime());
        assertEquals(new Time((11 * Time.HOUR) + (46 * Time.MINUTE)), normalBoard1Copy.getEndTime());

        normalBoard1Copy.add(new Block(Time.HOUR), new Time((11 * Time.HOUR) + (30 * Time.MINUTE)));
        normalBoard1Copy.add(new Board.Entry(new Block(10 * Time.MINUTE), new Time((11 * Time.HOUR) + (50 * Time.MINUTE))));

        assertEquals(new Time(8 * Time.HOUR), normalBoard1Copy.getStartTime());
        assertEquals(new Time((11 * Time.HOUR) + (50 * Time.MINUTE)), normalBoard1Copy.getLastTime());
        assertEquals(new Time((12 * Time.HOUR) + (30 * Time.MINUTE)), normalBoard1Copy.getEndTime());

        normalBoard1Copy.remove(block1);

        assertEquals(new Time((8 * Time.HOUR) + (30 * Time.MINUTE)), normalBoard1Copy.getStartTime());
        assertEquals(new Time((11 * Time.HOUR) + (50 * Time.MINUTE)), normalBoard1Copy.getLastTime());
        assertEquals(new Time((12 * Time.HOUR) + (30 * Time.MINUTE)), normalBoard1Copy.getEndTime());

        normalBoard1Copy.clear();
        assertTrue(normalBoard1Copy.isEmpty());

        assertFalse(normalBoard1.isEmpty());
        assertEquals(new Time(8 * Time.HOUR), normalBoard1.getStartTime());
        assertEquals(new Time(11 * Time.HOUR), normalBoard1.getLastTime());
        assertEquals(new Time((11 * Time.HOUR) + (46 * Time.MINUTE)), normalBoard1.getEndTime());
    }

    @Test
    public void testSearchBlock() {
        assertSame(block1, normalBoard1.search(block1).getBlock());
        assertEquals(new Time(8 * Time.HOUR), normalBoard1.search(block1).getTime());
        assertNull(normalBoard2.search(block2));
    }

    @Test
    public void testSearchActivity() {
        assertSame(block1, normalBoard1.search(activity11).getBlock()); 
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
    
    
    @Before
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
