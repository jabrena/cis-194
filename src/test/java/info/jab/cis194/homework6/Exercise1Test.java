package info.jab.cis194.homework6;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Exercise 1: Fibonacci numbers
 *
 * This exercise focuses on implementing fibonacci numbers using lazy evaluation.
 * We want to create an infinite stream of fibonacci numbers where each number
 * is computed only when needed.
 */
public class Exercise1Test {

    @Test
    public void testFibonacciFirst10Numbers() {
        Exercise1 exercise = new Exercise1();

        // Test first 10 fibonacci numbers: 0, 1, 1, 2, 3, 5, 8, 13, 21, 34
        long[] expected = {0, 1, 1, 2, 3, 5, 8, 13, 21, 34};

        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], exercise.fibonacci(i),
                "Fibonacci number at index " + i + " should be " + expected[i]);
        }
    }

    @Test
    public void testFibonacciLargerNumbers() {
        Exercise1 exercise = new Exercise1();

        // Test some larger fibonacci numbers
        assertEquals(55, exercise.fibonacci(10));
        assertEquals(89, exercise.fibonacci(11));
        assertEquals(144, exercise.fibonacci(12));
        assertEquals(6765, exercise.fibonacci(20));
    }

    @Test
    public void testFibonacciStream() {
        Exercise1 exercise = new Exercise1();

        // Test that we can get a stream of fibonacci numbers
        long[] first5 = exercise.fibonacciStream().limit(5).toArray();
        long[] expected = {0, 1, 1, 2, 3};

        assertArrayEquals(expected, first5, "First 5 fibonacci numbers from stream should match expected");
    }

    @Test
    public void testFibonacciStreamLaziness() {
        Exercise1 exercise = new Exercise1();

        // Test that the stream is truly lazy - we should be able to create it
        // without computing all values immediately
        var stream = exercise.fibonacciStream();
        assertNotNull(stream, "Fibonacci stream should not be null");

        // Take just the first element
        long first = stream.findFirst().orElse(-1);
        assertEquals(0, first, "First fibonacci number should be 0");
    }
}
