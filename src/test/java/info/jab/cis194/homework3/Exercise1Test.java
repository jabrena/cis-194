package info.jab.cis194.homework3;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Exercise 1: Skips Function Tests")
class Exercise1Test {

    @Nested
    @DisplayName("Basic Skips Functionality")
    class BasicSkipsFunctionality {

        @Test
        @DisplayName("Should return empty list for empty input")
        void shouldReturnEmptyListForEmptyInput() {
            // Given
            List<Integer> input = List.of();
            List<List<Integer>> expectedResult = List.of();

            // When
            List<List<Integer>> actualResult = Exercise1.skips(input);

            // Then
            assertThat(actualResult).isEqualTo(expectedResult);
        }

        @Test
        @DisplayName("Should return correct result for single element")
        void shouldReturnCorrectResultForSingleElement() {
            // Given
            List<String> input = List.of("a");
            List<List<String>> expectedResult = List.of(List.of("a"));

            // When
            List<List<String>> actualResult = Exercise1.skips(input);

            // Then
            assertThat(actualResult).isEqualTo(expectedResult);
        }

        @Test
        @DisplayName("Should return correct result for two elements")
        void shouldReturnCorrectResultForTwoElements() {
            // Given
            List<String> input = List.of("a", "b");
            List<List<String>> expectedResult = List.of(
                List.of("a", "b"),
                List.of("b")
            );

            // When
            List<List<String>> actualResult = Exercise1.skips(input);

            // Then
            assertThat(actualResult).isEqualTo(expectedResult);
        }

        @Test
        @DisplayName("Should return correct result for example ABCD")
        void shouldReturnCorrectResultForABCD() {
            // Given
            List<String> input = List.of("A", "B", "C", "D");
            List<List<String>> expectedResult = List.of(
                List.of("A", "B", "C", "D"),  // skip 0, take all
                List.of("B", "D"),            // skip 1, take every 2nd
                List.of("C"),                 // skip 2, take every 3rd
                List.of("D")                  // skip 3, take every 4th
            );

            // When
            List<List<String>> actualResult = Exercise1.skips(input);

            // Then
            assertThat(actualResult).isEqualTo(expectedResult);
        }

        @Test
        @DisplayName("Should work with numbers")
        void shouldWorkWithNumbers() {
            // Given
            List<Integer> input = List.of(1, 2, 3, 4, 5, 6);
            List<List<Integer>> expectedResult = List.of(
                List.of(1, 2, 3, 4, 5, 6),   // skip 0
                List.of(2, 4, 6),             // skip 1
                List.of(3, 6),                // skip 2
                List.of(4),                   // skip 3
                List.of(5),                   // skip 4
                List.of(6)                    // skip 5
            );

            // When
            List<List<Integer>> actualResult = Exercise1.skips(input);

            // Then
            assertThat(actualResult).isEqualTo(expectedResult);
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Handling")
    class EdgeCasesAndErrorHandling {

        @Test
        @DisplayName("Should handle null input gracefully")
        void shouldHandleNullInputGracefully() {
            // Given
            List<Integer> input = null;

            // When & Then
            assertThatThrownBy(() -> Exercise1.skips(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Input list cannot be null");
        }

        @Test
        @DisplayName("Should return correct size of outer list")
        void shouldReturnCorrectSizeOfOuterList() {
            // Given
            List<String> input = List.of("a", "b", "c", "d", "e");
            int expectedSize = input.size();

            // When
            List<List<String>> actualResult = Exercise1.skips(input);

            // Then
            assertThat(actualResult).hasSize(expectedSize);
        }

        @Test
        @DisplayName("Should handle large lists efficiently")
        void shouldHandleLargeListsEfficiently() {
            // Given
            List<Integer> input = java.util.stream.IntStream.range(1, 101)
                .boxed()
                .toList();

            // When
            List<List<Integer>> actualResult = Exercise1.skips(input);

            // Then
            assertThat(actualResult).hasSize(100);
            assertThat(actualResult.get(0)).hasSize(100);  // First list has all elements
            assertThat(actualResult.get(99)).hasSize(1);   // Last list has one element
        }
    }

    @Nested
    @DisplayName("Pattern Verification")
    class PatternVerification {

        @Test
        @DisplayName("Each sublist should start at correct index")
        void eachSublistShouldStartAtCorrectIndex() {
            // Given
            List<Integer> input = List.of(10, 20, 30, 40, 50);

            // When
            List<List<Integer>> actualResult = Exercise1.skips(input);

            // Then
            assertThat(actualResult.get(0).get(0)).isEqualTo(10); // First sublist starts at index 0
            assertThat(actualResult.get(1).get(0)).isEqualTo(20); // Second sublist starts at index 1
            assertThat(actualResult.get(2).get(0)).isEqualTo(30); // Third sublist starts at index 2
            assertThat(actualResult.get(3).get(0)).isEqualTo(40); // Fourth sublist starts at index 3
            assertThat(actualResult.get(4).get(0)).isEqualTo(50); // Fifth sublist starts at index 4
        }

        @Test
        @DisplayName("Each sublist should skip correct number of elements")
        void eachSublistShouldSkipCorrectNumberOfElements() {
            // Given
            List<Integer> input = List.of(1, 2, 3, 4, 5, 6, 7, 8);

            // When
            List<List<Integer>> actualResult = Exercise1.skips(input);

            // Then
            assertThat(actualResult.get(0)).isEqualTo(List.of(1, 2, 3, 4, 5, 6, 7, 8)); // First sublist (skip 0): all elements
            assertThat(actualResult.get(1)).isEqualTo(List.of(2, 4, 6, 8));              // Second sublist (skip 1): every 2nd element starting from index 1
            assertThat(actualResult.get(2)).isEqualTo(List.of(3, 6));                   // Third sublist (skip 2): every 3rd element starting from index 2
            assertThat(actualResult.get(3)).isEqualTo(List.of(4, 8));                   // Fourth sublist (skip 3): every 4th element starting from index 3
        }
    }
}
