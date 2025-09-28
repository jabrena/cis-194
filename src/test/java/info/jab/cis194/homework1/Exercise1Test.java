package info.jab.cis194.homework1;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

/**
 * Test class for Exercise 1 - Credit Card Validation
 * Based on CIS-194 Homework 1
 *
 * This test suite covers the Luhn algorithm implementation including:
 * - Number to digits conversion
 * - Digit doubling operations
 * - Digit summation
 * - Credit card validation
 */
@DisplayName("Exercise 1: Credit Card Validation Tests")
class Exercise1Test {

    private Exercise1 exercise;

    @BeforeEach
    void setUp() {
        exercise = new Exercise1();
    }

    @Nested
    @DisplayName("Number to Digits Conversion")
    class ToDigitsTests {

        @ParameterizedTest
        @DisplayName("Should convert positive numbers to digit lists")
        @CsvSource({
            "1234, '1,2,3,4'",
            "0, '0'",
            "5, '5'",
            "987654321, '9,8,7,6,5,4,3,2,1'"
        })
        void shouldConvertPositiveNumbersToDigits(long input, String expected) {
            List<Integer> expectedDigits = parseExpectedDigits(expected);
            assertThat(exercise.toDigits(input)).isEqualTo(expectedDigits);
        }

        @ParameterizedTest
        @DisplayName("Should return empty list for negative numbers")
        @ValueSource(longs = {-17, -1, -999})
        void shouldReturnEmptyListForNegativeNumbers(long input) {
            assertThat(exercise.toDigits(input)).isEmpty();
        }

        @ParameterizedTest
        @DisplayName("Should convert positive numbers to reversed digit lists")
        @CsvSource({
            "1234, '4,3,2,1'",
            "0, '0'",
            "5, '5'",
            "987654321, '1,2,3,4,5,6,7,8,9'"
        })
        void shouldConvertPositiveNumbersToReversedDigits(long input, String expected) {
            List<Integer> expectedDigits = parseExpectedDigits(expected);
            assertThat(exercise.toDigitsRev(input)).isEqualTo(expectedDigits);
        }

        @ParameterizedTest
        @DisplayName("Should return empty list for negative numbers (reversed)")
        @ValueSource(longs = {-17, -1, -999})
        void shouldReturnEmptyListForNegativeNumbersReversed(long input) {
            assertThat(exercise.toDigitsRev(input)).isEmpty();
        }
    }

    @Nested
    @DisplayName("Double Every Other Element")
    class DoubleEveryOtherTests {

        @ParameterizedTest
        @DisplayName("Should double every other element correctly")
        @MethodSource("doubleEveryOtherTestCases")
        void shouldDoubleEveryOtherElement(List<Integer> input, List<Integer> expected) {
            assertThat(exercise.doubleEveryOther(input)).isEqualTo(expected);
        }

        static Stream<Arguments> doubleEveryOtherTestCases() {
            return Stream.of(
                Arguments.of(List.of(1, 2, 3, 4), List.of(1, 4, 3, 8)),
                Arguments.of(List.of(2), List.of(2)),
                Arguments.of(List.of(1, 2), List.of(1, 4)),
                Arguments.of(List.of(1, 2, 3), List.of(2, 2, 6)),
                Arguments.of(List.of(), List.of())
            );
        }
    }

    @Nested
    @DisplayName("Sum Digits")
    class SumDigitsTests {

        @ParameterizedTest
        @DisplayName("Should sum all digits correctly")
        @MethodSource("sumDigitsTestCases")
        void shouldSumAllDigitsCorrectly(List<Integer> input, int expected) {
            assertThat(exercise.sumDigits(input)).isEqualTo(expected);
        }

        static Stream<Arguments> sumDigitsTestCases() {
            return Stream.of(
                // 16 -> 1+6=7, 7 -> 7, 12 -> 1+2=3, 5 -> 5, total = 7+7+3+5 = 22
                Arguments.of(List.of(16, 7, 12, 5), 22),
                // 1 -> 1, 5 -> 5, 8 -> 8, 4 -> 4, total = 1+5+8+4 = 18
                Arguments.of(List.of(1, 5, 8, 4), 18),
                Arguments.of(List.of(), 0),
                Arguments.of(List.of(5), 5),
                // Test with larger multi-digit numbers: 99 -> 9+9=18, 88 -> 8+8=16, total = 18+16 = 34
                Arguments.of(List.of(99, 88), 34)
            );
        }
    }

    @Nested
    @DisplayName("Credit Card Validation")
    class ValidationTests {

        @ParameterizedTest
        @DisplayName("Should validate valid credit card numbers")
        @ValueSource(longs = {4012888888881881L, 4111111111111111L})
        void shouldValidateValidCreditCardNumbers(long cardNumber) {
            assertThat(exercise.validate(cardNumber))
                .as("Credit card number %d should be valid", cardNumber)
                .isTrue();
        }

        @ParameterizedTest
        @DisplayName("Should reject invalid credit card numbers")
        @ValueSource(longs = {4012888888881882L, 1234567890123456L})
        void shouldRejectInvalidCreditCardNumbers(long cardNumber) {
            assertThat(exercise.validate(cardNumber))
                .as("Credit card number %d should be invalid", cardNumber)
                .isFalse();
        }

        @Test
        @DisplayName("Should handle edge cases for validation")
        void shouldHandleEdgeCasesForValidation() {
            // Test with some additional invalid credit card numbers
            assertThat(exercise.validate(1111111111111112L)).isFalse();
            assertThat(exercise.validate(5555555555555556L)).isFalse();
        }

        @Test
        @DisplayName("Should validate using functional approach")
        void shouldValidateUsingFunctionalApproach() {
            // Test that the functional validation produces same results
            assertThat(exercise.validateFunctional(4012888888881881L)).isTrue();
            assertThat(exercise.validateFunctional(4111111111111111L)).isTrue();
            assertThat(exercise.validateFunctional(4012888888881882L)).isFalse();
            assertThat(exercise.validateFunctional(1234567890123456L)).isFalse();
        }
    }

    /**
     * Helper method to parse expected digits from CSV string format
     */
    private List<Integer> parseExpectedDigits(String expected) {
        return Stream.of(expected.split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .toList();
    }
}
