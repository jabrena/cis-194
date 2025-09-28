package info.jab.cis194.homework4;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static info.jab.cis194.homework4.Exercise1.fun1;
import static info.jab.cis194.homework4.Exercise1.fun2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Exercise 1: Wholemeal Programming - fun1 and fun2")
class Exercise1Test {

    @Nested
    @DisplayName("fun1 Tests")
    class Fun1Tests {

        @Test
        @DisplayName("Should return empty list for empty input")
        void shouldReturnEmptyListForEmptyInput() {
            // Given
            List<Integer> input = List.of();
            List<Integer> expected = List.of();

            // When
            List<Integer> result = fun1(input);

            // Then
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("Should process single even number correctly")
        void shouldProcessSingleEvenNumberCorrectly() {
            // Given
            List<Integer> input = List.of(4);
            List<Integer> expected = List.of(3);

            // When
            List<Integer> result = fun1(input);

            // Then
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("Should process single odd number correctly")
        void shouldProcessSingleOddNumberCorrectly() {
            // Given
            List<Integer> input = List.of(3);
            List<Integer> expected = List.of(10);

            // When
            List<Integer> result = fun1(input);

            // Then
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("Should process mixed numbers correctly")
        void shouldProcessMixedNumbersCorrectly() {
            // Given
            List<Integer> input = List.of(1, 2, 3, 4, 5, 6);
            List<Integer> expected = List.of(4, 1, 10, 3, 16, 5);

            // When
            List<Integer> result = fun1(input);

            // Then
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("Should handle negative numbers")
        void shouldHandleNegativeNumbers() {
            // Given
            List<Integer> input = List.of(-2, -3, -4);
            List<Integer> expected = List.of(-3, -8, -5);

            // When
            List<Integer> result = fun1(input);

            // Then
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("Should handle zero")
        void shouldHandleZero() {
            // Given
            List<Integer> input = List.of(0);
            List<Integer> expected = List.of(-1);

            // When
            List<Integer> result = fun1(input);

            // Then
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("Should throw exception for null input")
        void shouldThrowExceptionForNullInput() {
            // Given
            List<Integer> input = null;

            // When & Then
            assertThatThrownBy(() -> fun1(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Input list cannot be null");
        }
    }

    @Nested
    @DisplayName("fun2 Tests")
    class Fun2Tests {

        @Test
        @DisplayName("Should return empty list for empty input")
        void shouldReturnEmptyListForEmptyInput() {
            // Given
            List<Integer> input = List.of();
            List<Integer> expected = List.of();

            // When
            List<Integer> result = fun2(input);

            // Then
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("Should process single even number correctly")
        void shouldProcessSingleEvenNumberCorrectly() {
            // Given
            List<Integer> input = List.of(4);
            List<Integer> expected = List.of(3);

            // When
            List<Integer> result = fun2(input);

            // Then
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("Should process single odd number correctly")
        void shouldProcessSingleOddNumberCorrectly() {
            // Given
            List<Integer> input = List.of(3);
            List<Integer> expected = List.of(10);

            // When
            List<Integer> result = fun2(input);

            // Then
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("Should process mixed numbers correctly")
        void shouldProcessMixedNumbersCorrectly() {
            // Given
            List<Integer> input = List.of(1, 2, 3, 4, 5, 6);
            List<Integer> expected = List.of(4, 1, 10, 3, 16, 5);

            // When
            List<Integer> result = fun2(input);

            // Then
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("Should handle negative numbers")
        void shouldHandleNegativeNumbers() {
            // Given
            List<Integer> input = List.of(-2, -3, -4);
            List<Integer> expected = List.of(-3, -8, -5);

            // When
            List<Integer> result = fun2(input);

            // Then
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("Should handle zero")
        void shouldHandleZero() {
            // Given
            List<Integer> input = List.of(0);
            List<Integer> expected = List.of(-1);

            // When
            List<Integer> result = fun2(input);

            // Then
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("Should throw exception for null input")
        void shouldThrowExceptionForNullInput() {
            // Given
            List<Integer> input = null;

            // When & Then
            assertThatThrownBy(() -> fun2(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Input list cannot be null");
        }
    }

    @Nested
    @DisplayName("Comparison Tests")
    class ComparisonTests {

        @Test
        @DisplayName("fun1 and fun2 should produce identical results")
        void fun1AndFun2ShouldProduceIdenticalResults() {
            // Given
            List<Integer> input = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

            // When
            List<Integer> result1 = fun1(input);
            List<Integer> result2 = fun2(input);

            // Then
            assertThat(result1).isEqualTo(result2);
        }

        @Test
        @DisplayName("fun1 and fun2 should produce identical results for edge cases")
        void fun1AndFun2ShouldProduceIdenticalResultsForEdgeCases() {
            // Given
            List<List<Integer>> testCases = List.of(
                List.of(),
                List.of(0),
                List.of(-1),
                List.of(-2),
                List.of(1, 3, 5, 7, 9),
                List.of(2, 4, 6, 8, 10),
                List.of(-5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5)
            );

            // When & Then
            for (List<Integer> testCase : testCases) {
                List<Integer> result1 = fun1(testCase);
                List<Integer> result2 = fun2(testCase);
                assertThat(result1).isEqualTo(result2);
            }
        }
    }
}
