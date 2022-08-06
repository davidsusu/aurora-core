package hu.webarticum.aurora.core.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import hu.webarticum.aurora.core.model.time.Interval;
import hu.webarticum.aurora.core.model.time.Time;

class BlockIntersectionTableTest {

    @Test
    void testEmpty() {
        BlockIntersectionTable intersectionTable = new BlockIntersectionTable();
        assertThat(toMap(intersectionTable)).isEmpty();
    }
    
    @Test
    void testTwoDisjointBlocks() {
        Block block1 = new Block("B1", 10);
        Block block2 = new Block("B2", 10);
        
        BlockIntersectionTable intersectionTable = new BlockIntersectionTable();
        intersectionTable.merge(block1, new Time(20));
        intersectionTable.merge(block2, new Time(60));
        Map<Interval, Set<Block>> actual = toMap(intersectionTable);
        
        Map<Interval, Set<Block>> expected = new HashMap<Interval, Set<Block>>();
        expected.put(new Interval(20, 30), setOf(block1));
        expected.put(new Interval(60, 70), setOf(block2));
        
        assertThat(actual).isEqualTo(expected);
    }
    
    @Test
    void testTwoIntersectingBlocks() {
        Block block1 = new Block("B1", 10);
        Block block2 = new Block("B2", 10);
        
        BlockIntersectionTable intersectionTable = new BlockIntersectionTable();
        intersectionTable.merge(block1, new Time(20));
        intersectionTable.merge(block2, new Time(25));
        Map<Interval, Set<Block>> actual = toMap(intersectionTable);
        
        Map<Interval, Set<Block>> expected = new HashMap<Interval, Set<Block>>();
        expected.put(new Interval(20, 25), setOf(block1));
        expected.put(new Interval(25, 30), setOf(block1, block2));
        expected.put(new Interval(30, 35), setOf(block2));

        assertThat(actual).isEqualTo(expected);
    }
    
    @Test
    void testMoreIntersectingBlocks() {
        Block block1 = new Block("B1", 10);
        Block block2 = new Block("B2", 30);
        Block block3 = new Block("B3", 10);
        Block block4 = new Block("B4", 10);
        Block block5 = new Block("B5", 20);
        Block block6 = new Block("B6", 10);
        Block block7 = new Block("B7", 10);
        Block block8 = new Block("B8", 40);
        Block block9 = new Block("B9", 10);
        
        BlockIntersectionTable intersectionTable = new BlockIntersectionTable();
        intersectionTable.merge(block1, new Time(10));
        intersectionTable.merge(block2, new Time(20));
        intersectionTable.merge(block3, new Time(20));
        intersectionTable.merge(block4, new Time(45));
        intersectionTable.merge(block5, new Time(50));
        intersectionTable.merge(block6, new Time(60));
        intersectionTable.merge(block7, new Time(75));
        intersectionTable.merge(block8, new Time(80));
        intersectionTable.merge(block9, new Time(90));
        Map<Interval, Set<Block>> actual = toMap(intersectionTable);
        
        Map<Interval, Set<Block>> expected = new HashMap<Interval, Set<Block>>();
        expected.put(new Interval(10, 20), setOf(block1));
        expected.put(new Interval(20, 30), setOf(block2, block3));
        expected.put(new Interval(30, 45), setOf(block2));
        expected.put(new Interval(45, 50), setOf(block2, block4));
        expected.put(new Interval(50, 55), setOf(block4, block5));
        expected.put(new Interval(55, 60), setOf(block5));
        expected.put(new Interval(60, 70), setOf(block5, block6));
        expected.put(new Interval(75, 80), setOf(block7));
        expected.put(new Interval(80, 85), setOf(block7, block8));
        expected.put(new Interval(85, 90), setOf(block8));
        expected.put(new Interval(90, 100), setOf(block8, block9));
        expected.put(new Interval(100, 120), setOf(block8));

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testMerge() {
        Block block1 = new Block("B1", 10);
        Block block2 = new Block("B2", 30);
        Block block3 = new Block("B3", 10);
        Block block4 = new Block("B4", 10);
        Block block5 = new Block("B5", 20);
        Block block6 = new Block("B6", 10);
        Block block7 = new Block("B7", 10);
        Block block8 = new Block("B8", 40);
        Block block9 = new Block("B9", 10);

        BlockIntersectionTable intersectionTable = new BlockIntersectionTable();
        intersectionTable.merge(block1, new Time(10));
        intersectionTable.merge(block2, new Time(20));
        intersectionTable.merge(block3, new Time(22));
        intersectionTable.merge(block4, new Time(45));

        BlockIntersectionTable intersectionTableToMerge = new BlockIntersectionTable();
        intersectionTable.merge(block5, new Time(50));
        intersectionTable.merge(block6, new Time(60));
        intersectionTable.merge(block7, new Time(75));
        intersectionTable.merge(block8, new Time(83));
        intersectionTable.merge(block9, new Time(90));
        
        intersectionTable.merge(intersectionTableToMerge);

        Map<Interval, Set<Block>> actual = toMap(intersectionTable);
        
        Map<Interval, Set<Block>> expected = new HashMap<Interval, Set<Block>>();
        expected.put(new Interval(10, 20), setOf(block1));
        expected.put(new Interval(20, 22), setOf(block2));
        expected.put(new Interval(22, 32), setOf(block2, block3));
        expected.put(new Interval(32, 45), setOf(block2));
        expected.put(new Interval(45, 50), setOf(block2, block4));
        expected.put(new Interval(50, 55), setOf(block4, block5));
        expected.put(new Interval(55, 60), setOf(block5));
        expected.put(new Interval(60, 70), setOf(block5, block6));
        expected.put(new Interval(75, 83), setOf(block7));
        expected.put(new Interval(83, 85), setOf(block7, block8));
        expected.put(new Interval(85, 90), setOf(block8));
        expected.put(new Interval(90, 100), setOf(block8, block9));
        expected.put(new Interval(100, 123), setOf(block8));

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testMergeMore() {
        Block block1 = new Block("B1", 15);
        Block block2 = new Block("B2", 30);
        Block block3 = new Block("B3", 10);
        Block block4 = new Block("B4", 10);
        Block block5 = new Block("B5", 20);
        Block block6 = new Block("B6", 10);
        Block block7 = new Block("B7", 10);
        Block block8 = new Block("B8", 40);
        Block block9 = new Block("B9", 15);

        BlockIntersectionTable intersectionTable = new BlockIntersectionTable();
        intersectionTable.merge(block1, new Time(10));
        intersectionTable.merge(block2, new Time(20));
        intersectionTable.merge(block3, new Time(22));

        BlockIntersectionTable intersectionTableToMerge1 = new BlockIntersectionTable();
        intersectionTable.merge(block4, new Time(45));
        intersectionTable.merge(block5, new Time(50));
        intersectionTable.merge(block6, new Time(60));
        
        BlockIntersectionTable intersectionTableToMerge2 = new BlockIntersectionTable();
        intersectionTable.merge(block7, new Time(75));
        intersectionTable.merge(block8, new Time(83));
        intersectionTable.merge(block9, new Time(90));
        
        intersectionTable.merge(intersectionTableToMerge1, intersectionTableToMerge2);

        Map<Interval, Set<Block>> actual = toMap(intersectionTable);
        
        Map<Interval, Set<Block>> expected = new HashMap<Interval, Set<Block>>();
        expected.put(new Interval(10, 20), setOf(block1));
        expected.put(new Interval(20, 22), setOf(block1, block2));
        expected.put(new Interval(22, 25), setOf(block1, block2, block3));
        expected.put(new Interval(25, 32), setOf(block2, block3));
        expected.put(new Interval(32, 45), setOf(block2));
        expected.put(new Interval(45, 50), setOf(block2, block4));
        expected.put(new Interval(50, 55), setOf(block4, block5));
        expected.put(new Interval(55, 60), setOf(block5));
        expected.put(new Interval(60, 70), setOf(block5, block6));
        expected.put(new Interval(75, 83), setOf(block7));
        expected.put(new Interval(83, 85), setOf(block7, block8));
        expected.put(new Interval(85, 90), setOf(block8));
        expected.put(new Interval(90, 105), setOf(block8, block9));
        expected.put(new Interval(105, 123), setOf(block8));

        assertThat(actual).isEqualTo(expected);
    }
    
    @Test
    void testCopy() {
        Block block1 = new Block("B1", 10);
        Block block2 = new Block("B2", 30);
        Block block3 = new Block("B3", 10);
        Block block4 = new Block("B4", 10);
        Block block5 = new Block("B5", 20);
        Block block6 = new Block("B6", 10);

        BlockIntersectionTable intersectionTable = new BlockIntersectionTable();
        intersectionTable.merge(block1, new Time(10));
        intersectionTable.merge(block2, new Time(20));
        intersectionTable.merge(block3, new Time(20));
        intersectionTable.merge(block4, new Time(45));
        intersectionTable.merge(block5, new Time(50));
        intersectionTable.merge(block6, new Time(55));
        
        BlockIntersectionTable copiedIntersectionTable = new BlockIntersectionTable(intersectionTable);

        assertThat(toMap(copiedIntersectionTable)).isEqualTo(toMap(intersectionTable));
    }

    private Map<Interval, Set<Block>> toMap(BlockIntersectionTable intersectionTable) {
        Map<Interval, Set<Block>> result = new HashMap<Interval, Set<Block>>();
        for (BlockIntersectionTable.Entry entry : intersectionTable) {
            result.put(entry.getInterval(), new HashSet<Block>(entry.getBlocks()));
        }
        return result;
    }
    
    private Set<Block> setOf(Block... blocks) {
        return new HashSet<Block>(Arrays.asList(blocks));
    }

}
