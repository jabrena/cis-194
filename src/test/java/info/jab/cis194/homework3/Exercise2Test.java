package info.jab.cis194.homework3;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Exercise 2: Local Maxima Function Tests")
class Exercise2Test {

    @Nested
    @DisplayName("Basic Local Maxima Functionality")
    class BasicLocalMaximaFunctionality {

        @Test
        @DisplayName("Should return empty list for empty input")
        void shouldReturnEmptyListForEmptyInput() {
            List<Integer> input = List.of();
            List<Integer> expected = List.of();

            List<Integer> result = Exercise2.localMaxima(input);

            assertEquals(expected, result);
        }

        @Test
        @DisplayName("Should return empty list for single element")
        void shouldReturnEmptyListForSingleElement() {
            List<Integer> input = List.of(5);
            List<Integer> expected = List.of();

            List<Integer> result = Exercise2.localMaxima(input);

            assertEquals(expected, result);
        }

        @Test
        @DisplayName("Should return empty list for two elements")
        void shouldReturnEmptyListForTwoElements() {
            List<Integer> input = List.of(3, 7);
            List<Integer> expected = List.of();

            List<Integer> result = Exercise2.localMaxima(input);

            assertEquals(expected, result);
        }

        @Test
        @DisplayName("Should find local maximum in middle")
        void shouldFindLocalMaximumInMiddle() {
            List<Integer> input = List.of(1, 5, 3);
            List<Integer> expected = List.of(5);

            List<Integer> result = Exercise2.localMaxima(input);

            assertEquals(expected, result);
        }

        @Test
        @DisplayName("Should find multiple local maxima")
        void shouldFindMultipleLocalMaxima() {
            List<Integer> input = List.of(2, 9, 5, 6, 1);
            List<Integer> expected = List.of(9, 6);

            List<Integer> result = Exercise2.localMaxima(input);

            assertEquals(expected, result);
        }

        @Test
        @DisplayName("Should handle example from homework")
        void shouldHandleExampleFromHomework() {
            List<Integer> input = List.of(2, 3, 4, 1, 5);
            List<Integer> expected = List.of(4);

            List<Integer> result = Exercise2.localMaxima(input);

            assertEquals(expected, result);
        }

        @Test
        @DisplayName("Should handle ascending sequence")
        void shouldHandleAscendingSequence() {
            List<Integer> input = List.of(1, 2, 3, 4, 5);
            List<Integer> expected = List.of();

            List<Integer> result = Exercise2.localMaxima(input);

            assertEquals(expected, result);
        }

        @Test
        @DisplayName("Should handle descending sequence")
        void shouldHandleDescendingSequence() {
            List<Integer> input = List.of(5, 4, 3, 2, 1);
            List<Integer> expected = List.of();

            List<Integer> result = Exercise2.localMaxima(input);

            assertEquals(expected, result);
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Handling")
    class EdgeCasesAndErrorHandling {

        @Test
        @DisplayName("Should handle null input gracefully")
        void shouldHandleNullInputGracefully() {
            assertThrows(IllegalArgumentException.class, () -> {
                Exercise2.localMaxima(null);
            });
        }

        @Test
        @DisplayName("Should handle equal elements")
        void shouldHandleEqualElements() {
            List<Integer> input = List.of(1, 3, 3, 3, 1);
            List<Integer> expected = List.of();

            List<Integer> result = Exercise2.localMaxima(input);

            assertEquals(expected, result);
        }

        @Test
        @DisplayName("Should handle plateau in middle")
        void shouldHandlePlateauInMiddle() {
            List<Integer> input = List.of(1, 5, 5, 5, 1);
            List<Integer> expected = List.of();

            List<Integer> result = Exercise2.localMaxima(input);

            assertEquals(expected, result);
        }

        @Test
        @DisplayName("Should handle negative numbers")
        void shouldHandleNegativeNumbers() {
            List<Integer> input = List.of(-5, -2, -8);
            List<Integer> expected = List.of(-2);

            List<Integer> result = Exercise2.localMaxima(input);

            assertEquals(expected, result);
        }
    }

    @Nested
    @DisplayName("Pattern Verification")
    class PatternVerification {

        @Test
        @DisplayName("Local maximum must be greater than both neighbors")
        void localMaximumMustBeGreaterThanBothNeighbors() {
            List<Integer> input = List.of(1, 7, 2, 8, 3);
            List<Integer> result = Exercise2.localMaxima(input);

            // 7 is greater than 1 and 2
            assertTrue(result.contains(7));
            // 8 is greater than 2 and 3
            assertTrue(result.contains(8));
            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("Should maintain order of local maxima")
        void shouldMaintainOrderOfLocalMaxima() {
            List<Integer> input = List.of(1, 9, 2, 6, 4, 8, 1);
            List<Integer> result = Exercise2.localMaxima(input);

            // Should be in the same order as they appear in the input
            assertEquals(List.of(9, 6, 8), result);
        }

        @Test
        @DisplayName("Should work with larger sequences")
        void shouldWorkWithLargerSequences() {
            List<Integer> input = List.of(10, 20, 15, 25, 5, 30, 10, 40, 35);
            List<Integer> result = Exercise2.localMaxima(input);

            // 20 > 10 and 20 > 15: local max
            // 25 > 15 and 25 > 5: local max
            // 30 > 5 and 30 > 10: local max
            // 40 > 10 and 40 > 35: local max
            assertEquals(List.of(20, 25, 30, 40), result);
        }
    }
}
