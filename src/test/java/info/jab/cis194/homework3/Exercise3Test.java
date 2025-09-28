package info.jab.cis194.homework3;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Exercise 3: Histogram Function Tests")
class Exercise3Test {

    @Nested
    @DisplayName("Basic Histogram Functionality")
    class BasicHistogramFunctionality {

        @Test
        @DisplayName("Should return empty histogram for empty input")
        void shouldReturnEmptyHistogramForEmptyInput() {
            List<Integer> input = List.of();
            String expected = "==========\n0123456789\n";

            String result = Exercise3.histogram(input);

            assertEquals(expected, result);
        }

        @Test
        @DisplayName("Should create histogram for single digit")
        void shouldCreateHistogramForSingleDigit() {
            List<Integer> input = List.of(3);
            String expected = "   *\n==========\n0123456789\n";

            String result = Exercise3.histogram(input);

            assertEquals(expected, result);
        }

        @Test
        @DisplayName("Should create histogram for multiple same digits")
        void shouldCreateHistogramForMultipleSameDigits() {
            List<Integer> input = List.of(5, 5, 5);
            String expected = "     *\n     *\n     *\n==========\n0123456789\n";

            String result = Exercise3.histogram(input);

            assertEquals(expected, result);
        }

        @Test
        @DisplayName("Should create histogram for different digits")
        void shouldCreateHistogramForDifferentDigits() {
            List<Integer> input = List.of(1, 2, 3);
            String expected = " ***\n==========\n0123456789\n";

            String result = Exercise3.histogram(input);

            assertEquals(expected, result);
        }

        @Test
        @DisplayName("Should create histogram for homework example")
        void shouldCreateHistogramForHomeworkExample() {
            List<Integer> input = List.of(1, 1, 1, 5);
            String expected = " *\n *\n *   *\n==========\n0123456789\n";

            String result = Exercise3.histogram(input);

            assertEquals(expected, result);
        }

        @Test
        @DisplayName("Should handle all digits")
        void shouldHandleAllDigits() {
            List<Integer> input = List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
            String expected = "**********\n==========\n0123456789\n";

            String result = Exercise3.histogram(input);

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
                Exercise3.histogram(null);
            });
        }

        @Test
        @DisplayName("Should ignore digits outside 0-9 range")
        void shouldIgnoreDigitsOutsideRange() {
            List<Integer> input = List.of(-1, 5, 10, 15);
            String expected = "     *\n==========\n0123456789\n";

            String result = Exercise3.histogram(input);

            assertEquals(expected, result);
        }

        @Test
        @DisplayName("Should handle mixed valid and invalid digits")
        void shouldHandleMixedValidAndInvalidDigits() {
            List<Integer> input = List.of(-5, 2, 2, 11, 7, 20);
            String expected = "  *\n  *    *\n==========\n0123456789\n";

            String result = Exercise3.histogram(input);

            assertEquals(expected, result);
        }
    }

    @Nested
    @DisplayName("Complex Histogram Patterns")
    class ComplexHistogramPatterns {

        @Test
        @DisplayName("Should create correct histogram for varying heights")
        void shouldCreateCorrectHistogramForVaryingHeights() {
            List<Integer> input = List.of(3, 5, 3, 5, 5);
            String expected = "     *\n   * *\n   * *\n==========\n0123456789\n";

            String result = Exercise3.histogram(input);

            assertEquals(expected, result);
        }

        @Test
        @DisplayName("Should handle maximum height scenario")
        void shouldHandleMaximumHeightScenario() {
            // Create a list with digit 0 appearing 5 times
            List<Integer> input = List.of(0, 0, 0, 0, 0);
            String expected = "*\n*\n*\n*\n*\n==========\n0123456789\n";

            String result = Exercise3.histogram(input);

            assertEquals(expected, result);
        }

        @Test
        @DisplayName("Should create histogram with gaps")
        void shouldCreateHistogramWithGaps() {
            List<Integer> input = List.of(0, 2, 4, 6, 8);
            String expected = "* * * * *\n==========\n0123456789\n";

            String result = Exercise3.histogram(input);

            assertEquals(expected, result);
        }

        @Test
        @DisplayName("Should handle complex pattern from homework")
        void shouldHandleComplexPatternFromHomework() {
            List<Integer> input = List.of(1, 4, 5, 4, 6, 6, 3, 4, 2, 4, 9);
            String expected = "    *\n    *\n    * *\n ******  *\n==========\n0123456789\n";

            String result = Exercise3.histogram(input);

            assertEquals(expected, result);
        }
    }

    @Nested
    @DisplayName("Format Verification")
    class FormatVerification {

        @Test
        @DisplayName("Should always end with equals line and digits line")
        void shouldAlwaysEndWithEqualsLineAndDigitsLine() {
            List<Integer> input = List.of(1, 2, 3);
            String result = Exercise3.histogram(input);

            assertTrue(result.endsWith("==========\n0123456789\n"));
        }

        @Test
        @DisplayName("Should have correct width for each line")
        void shouldHaveCorrectWidthForEachLine() {
            List<Integer> input = List.of(1, 5, 9);
            String result = Exercise3.histogram(input);
            String[] lines = result.split("\n");

            // Each line (except the last empty one) should have exactly 10 characters
            for (int i = 0; i < lines.length - 1; i++) {
                assertEquals(10, lines[i].length(), "Line " + i + " should be 10 characters wide");
            }
        }

        @Test
        @DisplayName("Should use spaces for empty positions")
        void shouldUseSpacesForEmptyPositions() {
            List<Integer> input = List.of(1, 9);
            String result = Exercise3.histogram(input);

            // Should have spaces in positions 0, 2-8
            assertTrue(result.contains(" *       *"));
        }
    }
}
