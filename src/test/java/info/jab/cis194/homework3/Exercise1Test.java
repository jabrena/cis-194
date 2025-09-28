package info.jab.cis194.homework3;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Exercise 1: Skips Function Tests")
class Exercise1Test {

    @Nested
    @DisplayName("Basic Skips Functionality")
    class BasicSkipsFunctionality {

        @Test
        @DisplayName("Should return empty list for empty input")
        void shouldReturnEmptyListForEmptyInput() {
            List<Integer> input = List.of();
            List<List<Integer>> expected = List.of();

            List<List<Integer>> result = Exercise1.skips(input);

            assertEquals(expected, result);
        }

        @Test
        @DisplayName("Should return correct result for single element")
        void shouldReturnCorrectResultForSingleElement() {
            List<String> input = List.of("a");
            List<List<String>> expected = List.of(List.of("a"));

            List<List<String>> result = Exercise1.skips(input);

            assertEquals(expected, result);
        }

        @Test
        @DisplayName("Should return correct result for two elements")
        void shouldReturnCorrectResultForTwoElements() {
            List<String> input = List.of("a", "b");
            List<List<String>> expected = List.of(
                List.of("a", "b"),
                List.of("b")
            );

            List<List<String>> result = Exercise1.skips(input);

            assertEquals(expected, result);
        }

        @Test
        @DisplayName("Should return correct result for example ABCD")
        void shouldReturnCorrectResultForABCD() {
            List<String> input = List.of("A", "B", "C", "D");
            List<List<String>> expected = List.of(
                List.of("A", "B", "C", "D"),  // skip 0, take all
                List.of("B", "D"),            // skip 1, take every 2nd
                List.of("C"),                 // skip 2, take every 3rd
                List.of("D")                  // skip 3, take every 4th
            );

            List<List<String>> result = Exercise1.skips(input);

            assertEquals(expected, result);
        }

        @Test
        @DisplayName("Should work with numbers")
        void shouldWorkWithNumbers() {
            List<Integer> input = List.of(1, 2, 3, 4, 5, 6);
            List<List<Integer>> expected = List.of(
                List.of(1, 2, 3, 4, 5, 6),   // skip 0
                List.of(2, 4, 6),             // skip 1
                List.of(3, 6),                // skip 2
                List.of(4),                   // skip 3
                List.of(5),                   // skip 4
                List.of(6)                    // skip 5
            );

            List<List<Integer>> result = Exercise1.skips(input);

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
                Exercise1.skips(null);
            });
        }

        @Test
        @DisplayName("Should return correct size of outer list")
        void shouldReturnCorrectSizeOfOuterList() {
            List<String> input = List.of("a", "b", "c", "d", "e");
            List<List<String>> result = Exercise1.skips(input);

            assertEquals(input.size(), result.size());
        }

        @Test
        @DisplayName("Should handle large lists efficiently")
        void shouldHandleLargeListsEfficiently() {
            List<Integer> input = java.util.stream.IntStream.range(1, 101)
                .boxed()
                .collect(java.util.stream.Collectors.toList());

            List<List<Integer>> result = Exercise1.skips(input);

            assertEquals(100, result.size());
            assertEquals(100, result.get(0).size()); // First list has all elements
            assertEquals(1, result.get(99).size());  // Last list has one element
        }
    }

    @Nested
    @DisplayName("Pattern Verification")
    class PatternVerification {

        @Test
        @DisplayName("Each sublist should start at correct index")
        void eachSublistShouldStartAtCorrectIndex() {
            List<Integer> input = List.of(10, 20, 30, 40, 50);
            List<List<Integer>> result = Exercise1.skips(input);

            // First sublist starts at index 0
            assertEquals(Integer.valueOf(10), result.get(0).get(0));
            // Second sublist starts at index 1
            assertEquals(Integer.valueOf(20), result.get(1).get(0));
            // Third sublist starts at index 2
            assertEquals(Integer.valueOf(30), result.get(2).get(0));
            // Fourth sublist starts at index 3
            assertEquals(Integer.valueOf(40), result.get(3).get(0));
            // Fifth sublist starts at index 4
            assertEquals(Integer.valueOf(50), result.get(4).get(0));
        }

        @Test
        @DisplayName("Each sublist should skip correct number of elements")
        void eachSublistShouldSkipCorrectNumberOfElements() {
            List<Integer> input = List.of(1, 2, 3, 4, 5, 6, 7, 8);
            List<List<Integer>> result = Exercise1.skips(input);

            // First sublist (skip 0): all elements
            assertEquals(List.of(1, 2, 3, 4, 5, 6, 7, 8), result.get(0));
            // Second sublist (skip 1): every 2nd element starting from index 1
            assertEquals(List.of(2, 4, 6, 8), result.get(1));
            // Third sublist (skip 2): every 3rd element starting from index 2
            assertEquals(List.of(3, 6), result.get(2));
            // Fourth sublist (skip 3): every 4th element starting from index 3
            assertEquals(List.of(4, 8), result.get(3));
        }
    }
}
