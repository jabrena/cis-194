package info.jab.cis194.homework3;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Exercise 2: Local Maxima Function Tests")
class Exercise2Test {

    @Nested
    @DisplayName("Basic Local Maxima Functionality")
    class BasicLocalMaximaFunctionality {

        @Test
        @DisplayName("Should return empty list for empty input")
        void shouldReturnEmptyListForEmptyInput() {
            // Given
            List<Integer> input = List.of();
            List<Integer> expectedResult = List.of();

            // When
            List<Integer> actualResult = Exercise2.localMaxima(input);

            // Then
            assertThat(actualResult).isEqualTo(expectedResult);
        }

        @Test
        @DisplayName("Should return empty list for single element")
        void shouldReturnEmptyListForSingleElement() {
            // Given
            List<Integer> input = List.of(5);
            List<Integer> expectedResult = List.of();

            // When
            List<Integer> actualResult = Exercise2.localMaxima(input);

            // Then
            assertThat(actualResult).isEqualTo(expectedResult);
        }

        @Test
        @DisplayName("Should return empty list for two elements")
        void shouldReturnEmptyListForTwoElements() {
            // Given
            List<Integer> input = List.of(3, 7);
            List<Integer> expectedResult = List.of();

            // When
            List<Integer> actualResult = Exercise2.localMaxima(input);

            // Then
            assertThat(actualResult).isEqualTo(expectedResult);
        }

        @Test
        @DisplayName("Should find local maximum in middle")
        void shouldFindLocalMaximumInMiddle() {
            // Given
            List<Integer> input = List.of(1, 5, 3);
            List<Integer> expectedResult = List.of(5);

            // When
            List<Integer> actualResult = Exercise2.localMaxima(input);

            // Then
            assertThat(actualResult).isEqualTo(expectedResult);
        }

        @Test
        @DisplayName("Should find multiple local maxima")
        void shouldFindMultipleLocalMaxima() {
            // Given
            List<Integer> input = List.of(2, 9, 5, 6, 1);
            List<Integer> expectedResult = List.of(9, 6);

            // When
            List<Integer> actualResult = Exercise2.localMaxima(input);

            // Then
            assertThat(actualResult).isEqualTo(expectedResult);
        }

        @Test
        @DisplayName("Should handle example from homework")
        void shouldHandleExampleFromHomework() {
            // Given
            List<Integer> input = List.of(2, 3, 4, 1, 5);
            List<Integer> expectedResult = List.of(4);

            // When
            List<Integer> actualResult = Exercise2.localMaxima(input);

            // Then
            assertThat(actualResult).isEqualTo(expectedResult);
        }

        @Test
        @DisplayName("Should handle ascending sequence")
        void shouldHandleAscendingSequence() {
            // Given
            List<Integer> input = List.of(1, 2, 3, 4, 5);
            List<Integer> expectedResult = List.of();

            // When
            List<Integer> actualResult = Exercise2.localMaxima(input);

            // Then
            assertThat(actualResult).isEmpty();
        }

        @Test
        @DisplayName("Should handle descending sequence")
        void shouldHandleDescendingSequence() {
            // Given
            List<Integer> input = List.of(5, 4, 3, 2, 1);
            List<Integer> expectedResult = List.of();

            // When
            List<Integer> actualResult = Exercise2.localMaxima(input);

            // Then
            assertThat(actualResult).isEmpty();
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
            assertThatThrownBy(() -> Exercise2.localMaxima(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Input list cannot be null");
        }

        @Test
        @DisplayName("Should handle equal elements")
        void shouldHandleEqualElements() {
            // Given
            List<Integer> input = List.of(1, 3, 3, 3, 1);
            List<Integer> expectedResult = List.of();

            // When
            List<Integer> actualResult = Exercise2.localMaxima(input);

            // Then
            assertThat(actualResult).isEqualTo(expectedResult);
        }

        @Test
        @DisplayName("Should handle plateau in middle")
        void shouldHandlePlateauInMiddle() {
            // Given
            List<Integer> input = List.of(1, 5, 5, 5, 1);
            List<Integer> expectedResult = List.of();

            // When
            List<Integer> actualResult = Exercise2.localMaxima(input);

            // Then
            assertThat(actualResult).isEmpty();
        }

        @Test
        @DisplayName("Should handle negative numbers")
        void shouldHandleNegativeNumbers() {
            // Given
            List<Integer> input = List.of(-5, -2, -8);
            List<Integer> expectedResult = List.of(-2);

            // When
            List<Integer> actualResult = Exercise2.localMaxima(input);

            // Then
            assertThat(actualResult).containsExactly(-2);
        }
    }

    @Nested
    @DisplayName("Pattern Verification")
    class PatternVerification {

        @Test
        @DisplayName("Local maximum must be greater than both neighbors")
        void localMaximumMustBeGreaterThanBothNeighbors() {
            // Given
            List<Integer> input = List.of(1, 7, 2, 8, 3);

            // When
            List<Integer> actualResult = Exercise2.localMaxima(input);

            // Then
            assertThat(actualResult)
                .hasSize(2)
                .contains(7, 8); // 7 is greater than 1 and 2; 8 is greater than 2 and 3
        }

        @Test
        @DisplayName("Should maintain order of local maxima")
        void shouldMaintainOrderOfLocalMaxima() {
            // Given
            List<Integer> input = List.of(1, 9, 2, 6, 4, 8, 1);

            // When
            List<Integer> actualResult = Exercise2.localMaxima(input);

            // Then
            assertThat(actualResult).containsExactly(9, 6, 8); // Should be in the same order as they appear in the input
        }

        @Test
        @DisplayName("Should work with larger sequences")
        void shouldWorkWithLargerSequences() {
            // Given
            List<Integer> input = List.of(10, 20, 15, 25, 5, 30, 10, 40, 35);

            // When
            List<Integer> actualResult = Exercise2.localMaxima(input);

            // Then
            assertThat(actualResult).containsExactly(20, 25, 30, 40);
            // 20 > 10 and 20 > 15: local max
            // 25 > 15 and 25 > 5: local max
            // 30 > 5 and 30 > 10: local max
            // 40 > 10 and 40 > 35: local max
        }
    }
}
