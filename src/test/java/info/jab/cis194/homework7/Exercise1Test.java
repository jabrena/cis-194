package info.jab.cis194.homework7;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Exercise 1: JoinList implementation
 *
 * This exercise focuses on implementing a JoinList data structure
 * that efficiently supports concatenation using monoids.
 * JoinList is a binary tree structure that allows O(1) concatenation.
 */
public class Exercise1Test {

    @Test
    public void testEmptyJoinList() {
        Exercise1.JoinList<String> empty = Exercise1.empty();
        assertNotNull(empty, "Empty JoinList should not be null");
        assertTrue(empty.isEmpty(), "Empty JoinList should be empty");
        assertEquals(0, empty.size(), "Empty JoinList should have size 0");
    }

    @Test
    public void testSingleJoinList() {
        Exercise1.JoinList<String> single = Exercise1.single("hello");
        assertNotNull(single, "Single JoinList should not be null");
        assertFalse(single.isEmpty(), "Single JoinList should not be empty");
        assertEquals(1, single.size(), "Single JoinList should have size 1");
    }

    @Test
    public void testJoinListConcatenation() {
        Exercise1.JoinList<String> list1 = Exercise1.single("hello");
        Exercise1.JoinList<String> list2 = Exercise1.single("world");

        Exercise1.JoinList<String> joined = list1.append(list2);

        assertNotNull(joined, "Joined JoinList should not be null");
        assertFalse(joined.isEmpty(), "Joined JoinList should not be empty");
        assertEquals(2, joined.size(), "Joined JoinList should have size 2");
    }

    @Test
    public void testJoinListToList() {
        Exercise1.JoinList<String> list1 = Exercise1.single("hello");
        Exercise1.JoinList<String> list2 = Exercise1.single("world");
        Exercise1.JoinList<String> list3 = Exercise1.single("!");

        Exercise1.JoinList<String> joined = list1.append(list2).append(list3);
        List<String> result = joined.toList();

        List<String> expected = Arrays.asList("hello", "world", "!");
        assertEquals(expected, result, "JoinList should convert to correct list");
    }

    @Test
    public void testJoinListFromList() {
        List<String> input = Arrays.asList("a", "b", "c", "d");
        Exercise1.JoinList<String> joinList = Exercise1.fromList(input);

        assertNotNull(joinList, "JoinList from list should not be null");
        assertEquals(4, joinList.size(), "JoinList should have correct size");

        List<String> result = joinList.toList();
        assertEquals(input, result, "Converting back to list should preserve order");
    }

    @Test
    public void testJoinListIndexing() {
        List<String> input = Arrays.asList("first", "second", "third", "fourth");
        Exercise1.JoinList<String> joinList = Exercise1.fromList(input);

        assertEquals("first", joinList.indexJ(0), "Should get correct element at index 0");
        assertEquals("second", joinList.indexJ(1), "Should get correct element at index 1");
        assertEquals("third", joinList.indexJ(2), "Should get correct element at index 2");
        assertEquals("fourth", joinList.indexJ(3), "Should get correct element at index 3");
    }

    @Test
    public void testJoinListIndexingOutOfBounds() {
        Exercise1.JoinList<String> joinList = Exercise1.single("test");

        assertThrows(IndexOutOfBoundsException.class, () -> {
            joinList.indexJ(-1);
        }, "Should throw exception for negative index");

        assertThrows(IndexOutOfBoundsException.class, () -> {
            joinList.indexJ(1);
        }, "Should throw exception for index >= size");
    }

    @Test
    public void testJoinListDropJ() {
        List<String> input = Arrays.asList("a", "b", "c", "d", "e");
        Exercise1.JoinList<String> joinList = Exercise1.fromList(input);

        Exercise1.JoinList<String> dropped = joinList.dropJ(2);
        List<String> result = dropped.toList();

        List<String> expected = Arrays.asList("c", "d", "e");
        assertEquals(expected, result, "dropJ should remove first n elements");
    }

    @Test
    public void testJoinListTakeJ() {
        List<String> input = Arrays.asList("a", "b", "c", "d", "e");
        Exercise1.JoinList<String> joinList = Exercise1.fromList(input);

        Exercise1.JoinList<String> taken = joinList.takeJ(3);
        List<String> result = taken.toList();

        List<String> expected = Arrays.asList("a", "b", "c");
        assertEquals(expected, result, "takeJ should take first n elements");
    }

    @Test
    public void testEmptyJoinListOperations() {
        Exercise1.JoinList<String> empty = Exercise1.empty();

        assertEquals(0, empty.size(), "Empty list should have size 0");
        assertTrue(empty.toList().isEmpty(), "Empty list should convert to empty list");

        Exercise1.JoinList<String> droppedEmpty = empty.dropJ(5);
        assertTrue(droppedEmpty.isEmpty(), "Dropping from empty should remain empty");

        Exercise1.JoinList<String> takenEmpty = empty.takeJ(5);
        assertTrue(takenEmpty.isEmpty(), "Taking from empty should remain empty");
    }
}
