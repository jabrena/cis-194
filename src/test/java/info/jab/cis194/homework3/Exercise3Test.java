package info.jab.cis194.homework3;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Exercise 3: Histogram Function Tests")
class Exercise3Test {

    @Nested
    @DisplayName("Basic Histogram Functionality")
    class BasicHistogramFunctionality {

        @Test
        @DisplayName("Should return empty histogram for empty input")
        void shouldReturnEmptyHistogramForEmptyInput() {
            // Given
            List<Integer> input = List.of();
            String expectedResult = "==========\n0123456789\n";

            // When
            String actualResult = Exercise3.histogram(input);

            // Then
            assertThat(actualResult).isEqualTo(expectedResult);
        }

        @Test
        @DisplayName("Should create histogram for single digit")
        void shouldCreateHistogramForSingleDigit() {
            // Given
            List<Integer> input = List.of(3);
            String expectedResult = "   *\n==========\n0123456789\n";

            // When
            String actualResult = Exercise3.histogram(input);

            // Then
            assertThat(actualResult).isEqualTo(expectedResult);
        }

        @Test
        @DisplayName("Should create histogram for multiple same digits")
        void shouldCreateHistogramForMultipleSameDigits() {
            // Given
            List<Integer> input = List.of(5, 5, 5);
            String expectedResult = "     *\n     *\n     *\n==========\n0123456789\n";

            // When
            String actualResult = Exercise3.histogram(input);

            // Then
            assertThat(actualResult).isEqualTo(expectedResult);
        }

        @Test
        @DisplayName("Should create histogram for different digits")
        void shouldCreateHistogramForDifferentDigits() {
            // Given
            List<Integer> input = List.of(1, 2, 3);
            String expectedResult = " ***\n==========\n0123456789\n";

            // When
            String actualResult = Exercise3.histogram(input);

            // Then
            assertThat(actualResult).isEqualTo(expectedResult);
        }

        @Test
        @DisplayName("Should create histogram for homework example")
        void shouldCreateHistogramForHomeworkExample() {
            // Given
            List<Integer> input = List.of(1, 1, 1, 5);
            String expectedResult = " *\n *\n *   *\n==========\n0123456789\n";

            // When
            String actualResult = Exercise3.histogram(input);

            // Then
            assertThat(actualResult).isEqualTo(expectedResult);
        }

        @Test
        @DisplayName("Should handle all digits")
        void shouldHandleAllDigits() {
            // Given
            List<Integer> input = List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
            String expectedResult = "**********\n==========\n0123456789\n";

            // When
            String actualResult = Exercise3.histogram(input);

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
            assertThatThrownBy(() -> Exercise3.histogram(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Input list cannot be null");
        }

        @Test
        @DisplayName("Should ignore digits outside 0-9 range")
        void shouldIgnoreDigitsOutsideRange() {
            // Given
            List<Integer> input = List.of(-1, 5, 10, 15);
            String expectedResult = "     *\n==========\n0123456789\n";

            // When
            String actualResult = Exercise3.histogram(input);

            // Then
            assertThat(actualResult).isEqualTo(expectedResult);
        }

        @Test
        @DisplayName("Should handle mixed valid and invalid digits")
        void shouldHandleMixedValidAndInvalidDigits() {
            // Given
            List<Integer> input = List.of(-5, 2, 2, 11, 7, 20);
            String expectedResult = "  *\n  *    *\n==========\n0123456789\n";

            // When
            String actualResult = Exercise3.histogram(input);

            // Then
            assertThat(actualResult).isEqualTo(expectedResult);
        }
    }

    @Nested
    @DisplayName("Complex Histogram Patterns")
    class ComplexHistogramPatterns {

        @Test
        @DisplayName("Should create correct histogram for varying heights")
        void shouldCreateCorrectHistogramForVaryingHeights() {
            // Given
            List<Integer> input = List.of(3, 5, 3, 5, 5);
            String expectedResult = "     *\n   * *\n   * *\n==========\n0123456789\n";

            // When
            String actualResult = Exercise3.histogram(input);

            // Then
            assertThat(actualResult).isEqualTo(expectedResult);
        }

        @Test
        @DisplayName("Should handle maximum height scenario")
        void shouldHandleMaximumHeightScenario() {
            // Given - Create a list with digit 0 appearing 5 times
            List<Integer> input = List.of(0, 0, 0, 0, 0);
            String expectedResult = "*\n*\n*\n*\n*\n==========\n0123456789\n";

            // When
            String actualResult = Exercise3.histogram(input);

            // Then
            assertThat(actualResult).isEqualTo(expectedResult);
        }

        @Test
        @DisplayName("Should create histogram with gaps")
        void shouldCreateHistogramWithGaps() {
            // Given
            List<Integer> input = List.of(0, 2, 4, 6, 8);
            String expectedResult = "* * * * *\n==========\n0123456789\n";

            // When
            String actualResult = Exercise3.histogram(input);

            // Then
            assertThat(actualResult).isEqualTo(expectedResult);
        }

        @Test
        @DisplayName("Should handle complex pattern from homework")
        void shouldHandleComplexPatternFromHomework() {
            // Given
            List<Integer> input = List.of(1, 4, 5, 4, 6, 6, 3, 4, 2, 4, 9);
            String expectedResult = "    *\n    *\n    * *\n ******  *\n==========\n0123456789\n";

            // When
            String actualResult = Exercise3.histogram(input);

            // Then
            assertThat(actualResult).isEqualTo(expectedResult);
        }
    }

    @Nested
    @DisplayName("Format Verification")
    class FormatVerification {

        @Test
        @DisplayName("Should always end with equals line and digits line")
        void shouldAlwaysEndWithEqualsLineAndDigitsLine() {
            // Given
            List<Integer> input = List.of(1, 2, 3);

            // When
            String actualResult = Exercise3.histogram(input);

            // Then
            assertThat(actualResult).endsWith("==========\n0123456789\n");
        }

        @Test
        @DisplayName("Should have correct width for each line")
        void shouldHaveCorrectWidthForEachLine() {
            // Given
            List<Integer> input = List.of(1, 5, 9);

            // When
            String actualResult = Exercise3.histogram(input);
            String[] lines = actualResult.split("\n");

            // Then
            // Each line (except the last empty one) should have at most 10 characters
            for (int i = 0; i < lines.length - 1; i++) {
                assertThat(lines[i].length())
                    .as("Line %d should have at most 10 characters", i)
                    .isLessThanOrEqualTo(10);
            }
        }

        @Test
        @DisplayName("Should use spaces for empty positions")
        void shouldUseSpacesForEmptyPositions() {
            // Given
            List<Integer> input = List.of(1, 9);

            // When
            String actualResult = Exercise3.histogram(input);

            // Then
            assertThat(actualResult).contains(" *       *");
        }
    }
}
