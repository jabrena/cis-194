package info.jab.cis194.homework1;

import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Exercise 2 - Tower of Hanoi
 * Based on CIS-194 Homework 1
 */
public class Exercise2Test {

    @Test
    public void testHanoiTowerMoves() {
        Exercise2 exercise = new Exercise2();

        // Test base case: moving 1 disc from "a" to "b" using "c"
        List<Exercise2.Move> moves1 = exercise.hanoi(1, "a", "b", "c");
        assertEquals(1, moves1.size());
        assertEquals(new Exercise2.Move("a", "b"), moves1.get(0));

        // Test moving 2 discs from "a" to "b" using "c"
        List<Exercise2.Move> moves2 = exercise.hanoi(2, "a", "b", "c");
        assertEquals(3, moves2.size());
        assertEquals(new Exercise2.Move("a", "c"), moves2.get(0));
        assertEquals(new Exercise2.Move("a", "b"), moves2.get(1));
        assertEquals(new Exercise2.Move("c", "b"), moves2.get(2));

        // Test moving 3 discs
        List<Exercise2.Move> moves3 = exercise.hanoi(3, "a", "b", "c");
        assertEquals(7, moves3.size());

        // Test edge case: 0 discs should return empty list
        List<Exercise2.Move> moves0 = exercise.hanoi(0, "a", "b", "c");
        assertEquals(0, moves0.size());
    }

    @Test
    public void testHanoiMoveCount() {
        Exercise2 exercise = new Exercise2();

        // Test that the number of moves follows the formula 2^n - 1
        assertEquals(1, exercise.hanoi(1, "a", "b", "c").size()); // 2^1 - 1 = 1
        assertEquals(3, exercise.hanoi(2, "a", "b", "c").size()); // 2^2 - 1 = 3
        assertEquals(7, exercise.hanoi(3, "a", "b", "c").size()); // 2^3 - 1 = 7
        assertEquals(15, exercise.hanoi(4, "a", "b", "c").size()); // 2^4 - 1 = 15
    }

    @Test
    public void testMoveEquality() {
        Exercise2.Move move1 = new Exercise2.Move("a", "b");
        Exercise2.Move move2 = new Exercise2.Move("a", "b");
        Exercise2.Move move3 = new Exercise2.Move("b", "a");

        assertEquals(move1, move2);
        assertNotEquals(move1, move3);
        assertEquals(move1.hashCode(), move2.hashCode());
    }
}
