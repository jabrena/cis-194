package info.jab.cis194.homework6;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for Exercise 1: Fibonacci numbers
 *
 * This exercise focuses on implementing fibonacci numbers using lazy evaluation.
 * We want to create an infinite stream of fibonacci numbers where each number
 * is computed only when needed.
 *
 * Tests follow Given-When-Then structure and use AssertJ for fluent assertions.
 */
@DisplayName("Exercise 1: Fibonacci Numbers Tests")
public class Exercise1Test {

    private Exercise1 exercise;

    @BeforeEach
    void setUp() {
        exercise = new Exercise1();
    }

    @Nested
    @DisplayName("Fibonacci Number Generation")
    class FibonacciNumberGeneration {

        @Test
        @DisplayName("Should generate first 10 fibonacci numbers correctly")
        public void should_generateFirst10FibonacciNumbers_when_indexesFrom0To9Provided() {
            // Given
            long[] expectedFibonacciNumbers = {0, 1, 1, 2, 3, 5, 8, 13, 21, 34};

            // When & Then
            for (int i = 0; i < expectedFibonacciNumbers.length; i++) {
                long actualFibonacci = exercise.fibonacci(i);

                assertThat(actualFibonacci)
                    .as("Fibonacci number at index %d should be %d", i, expectedFibonacciNumbers[i])
                    .isEqualTo(expectedFibonacciNumbers[i]);
            }
        }

        @Test
        @DisplayName("Should generate larger fibonacci numbers correctly")
        public void should_generateLargerFibonacciNumbers_when_higherIndexesProvided() {
            // Given
            int index10 = 10;
            int index11 = 11;
            int index12 = 12;
            int index20 = 20;

            // When
            long fibonacci10 = exercise.fibonacci(index10);
            long fibonacci11 = exercise.fibonacci(index11);
            long fibonacci12 = exercise.fibonacci(index12);
            long fibonacci20 = exercise.fibonacci(index20);

            // Then
            assertThat(fibonacci10).isEqualTo(55);
            assertThat(fibonacci11).isEqualTo(89);
            assertThat(fibonacci12).isEqualTo(144);
            assertThat(fibonacci20).isEqualTo(6765);
        }
    }

    @Nested
    @DisplayName("Fibonacci Stream Operations")
    class FibonacciStreamOperations {

        @Test
        @DisplayName("Should generate fibonacci stream with correct first 5 numbers")
        public void should_generateFibonacciStream_when_limitedTo5Elements() {
            // Given
            long[] expectedFirst5 = {0, 1, 1, 2, 3};

            // When
            long[] actualFirst5 = exercise.fibonacciStream()
                .limit(5)
                .toArray();

            // Then
            assertThat(actualFirst5)
                .as("First 5 fibonacci numbers from stream should match expected sequence")
                .isEqualTo(expectedFirst5);
        }

        @Test
        @DisplayName("Should demonstrate lazy evaluation of fibonacci stream")
        public void should_demonstrateLazyEvaluation_when_fibonacciStreamCreated() {
            // Given
            // No specific setup needed for lazy evaluation test

            // When
            var fibonacciStream = exercise.fibonacciStream();
            long firstFibonacci = fibonacciStream.findFirst().orElse(-1);

            // Then
            assertThat(fibonacciStream)
                .as("Fibonacci stream should not be null")
                .isNotNull();

            assertThat(firstFibonacci)
                .as("First fibonacci number should be 0")
                .isEqualTo(0);
        }
    }
}
