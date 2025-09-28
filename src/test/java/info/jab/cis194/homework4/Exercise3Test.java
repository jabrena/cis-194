package info.jab.cis194.homework4;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Exercise 3: More Folds - xor and map")
class Exercise3Test {

    @Nested
    @DisplayName("xor Function Tests")
    class XorFunctionTests {

        @Test
        @DisplayName("Should return false for empty list")
        void shouldReturnFalseForEmptyList() {
            // Given
            List<Boolean> input = List.of();

            // When
            Boolean result = Exercise3.xor(input);

            // Then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Should return true for single true element")
        void shouldReturnTrueForSingleTrueElement() {
            // Given
            List<Boolean> input = List.of(true);

            // When
            Boolean result = Exercise3.xor(input);

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Should return false for single false element")
        void shouldReturnFalseForSingleFalseElement() {
            // Given
            List<Boolean> input = List.of(false);

            // When
            Boolean result = Exercise3.xor(input);

            // Then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Should return false for two true elements")
        void shouldReturnFalseForTwoTrueElements() {
            // Given
            List<Boolean> input = List.of(true, true);

            // When
            Boolean result = Exercise3.xor(input);

            // Then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Should return false for two false elements")
        void shouldReturnFalseForTwoFalseElements() {
            // Given
            List<Boolean> input = List.of(false, false);

            // When
            Boolean result = Exercise3.xor(input);

            // Then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Should return true for one true and one false")
        void shouldReturnTrueForOneTrueAndOneFalse() {
            // Given
            List<Boolean> input1 = List.of(true, false);
            List<Boolean> input2 = List.of(false, true);

            // When
            Boolean result1 = Exercise3.xor(input1);
            Boolean result2 = Exercise3.xor(input2);

            // Then
            assertThat(result1).isTrue();
            assertThat(result2).isTrue();
        }

        @Test
        @DisplayName("Should return true for odd number of true elements")
        void shouldReturnTrueForOddNumberOfTrueElements() {
            // Given
            List<Boolean> input1 = List.of(true, false, false);           // 1 true
            List<Boolean> input2 = List.of(true, true, true, false);      // 3 true
            List<Boolean> input3 = List.of(true, false, true, false, true); // 3 true

            // When
            Boolean result1 = Exercise3.xor(input1);
            Boolean result2 = Exercise3.xor(input2);
            Boolean result3 = Exercise3.xor(input3);

            // Then
            assertThat(result1).isTrue();
            assertThat(result2).isTrue();
            assertThat(result3).isTrue();
        }

        @Test
        @DisplayName("Should return false for even number of true elements")
        void shouldReturnFalseForEvenNumberOfTrueElements() {
            // Given
            List<Boolean> input1 = List.of(true, true, false, false);     // 2 true
            List<Boolean> input2 = List.of(true, true, true, true);       // 4 true
            List<Boolean> input3 = List.of(false, false, false, false);   // 0 true

            // When
            Boolean result1 = Exercise3.xor(input1);
            Boolean result2 = Exercise3.xor(input2);
            Boolean result3 = Exercise3.xor(input3);

            // Then
            assertThat(result1).isFalse();
            assertThat(result2).isFalse();
            assertThat(result3).isFalse();
        }

        @Test
        @DisplayName("Should handle large lists efficiently")
        void shouldHandleLargeListsEfficiently() {
            // Given - Create a large list with known pattern
            List<Boolean> largeListOdd = java.util.stream.Stream
                .concat(
                    java.util.stream.Stream.generate(() -> true).limit(999),  // 999 true
                    java.util.stream.Stream.generate(() -> false).limit(1000) // 1000 false
                )
                .toList();

            List<Boolean> largeListEven = java.util.stream.Stream
                .concat(
                    java.util.stream.Stream.generate(() -> true).limit(1000), // 1000 true
                    java.util.stream.Stream.generate(() -> false).limit(1000) // 1000 false
                )
                .toList();

            // When
            Boolean resultOdd = Exercise3.xor(largeListOdd);
            Boolean resultEven = Exercise3.xor(largeListEven);

            // Then
            assertThat(resultOdd).isTrue();  // 999 (odd) true elements
            assertThat(resultEven).isFalse(); // 1000 (even) true elements
        }

        @Test
        @DisplayName("Should throw exception for null input")
        void shouldThrowExceptionForNullInput() {
            // Given
            List<Boolean> input = null;

            // When & Then
            assertThatThrownBy(() -> Exercise3.xor(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Input list cannot be null");
        }
    }

    @Nested
    @DisplayName("map Function Tests")
    class MapFunctionTests {

        @Test
        @DisplayName("Should return empty list for empty input")
        void shouldReturnEmptyListForEmptyInput() {
            // Given
            List<Integer> input = List.of();

            // When
            List<String> result = Exercise3.map(Object::toString, input);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should apply function to single element")
        void shouldApplyFunctionToSingleElement() {
            // Given
            List<Integer> input = List.of(42);

            // When
            List<String> result = Exercise3.map(Object::toString, input);

            // Then
            assertThat(result).containsExactly("42");
        }

        @Test
        @DisplayName("Should apply function to multiple elements")
        void shouldApplyFunctionToMultipleElements() {
            // Given
            List<Integer> input = List.of(1, 2, 3, 4, 5);

            // When
            List<Integer> result = Exercise3.map(x -> x * 2, input);

            // Then
            assertThat(result).containsExactly(2, 4, 6, 8, 10);
        }

        @Test
        @DisplayName("Should preserve order of elements")
        void shouldPreserveOrderOfElements() {
            // Given
            List<String> input = List.of("apple", "banana", "cherry");

            // When
            List<String> result = Exercise3.map(String::toUpperCase, input);

            // Then
            assertThat(result).containsExactly("APPLE", "BANANA", "CHERRY");
        }

        @Test
        @DisplayName("Should work with different types")
        void shouldWorkWithDifferentTypes() {
            // Given
            List<String> input = List.of("1", "22", "333");

            // When
            List<Integer> result = Exercise3.map(String::length, input);

            // Then
            assertThat(result).containsExactly(1, 2, 3);
        }

        @Test
        @DisplayName("Should handle complex transformations")
        void shouldHandleComplexTransformations() {
            // Given
            List<Integer> input = List.of(-2, -1, 0, 1, 2);

            // When
            List<String> result = Exercise3.map(x -> {
                if (x > 0) return "positive";
                else if (x < 0) return "negative";
                else return "zero";
            }, input);

            // Then
            assertThat(result).containsExactly("negative", "negative", "zero", "positive", "positive");
        }

        @Test
        @DisplayName("Should handle null elements in list")
        void shouldHandleNullElementsInList() {
            // Given
            List<Integer> input = java.util.Arrays.asList(1, null, 3);

            // When
            List<String> result = Exercise3.map(x -> x == null ? "null" : x.toString(), input);

            // Then
            assertThat(result).containsExactly("1", "null", "3");
        }

        @Test
        @DisplayName("Should throw exception for null function")
        void shouldThrowExceptionForNullFunction() {
            // Given
            List<Integer> input = List.of(1, 2, 3);

            // When & Then
            assertThatThrownBy(() -> Exercise3.map(null, input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Function cannot be null");
        }

        @Test
        @DisplayName("Should throw exception for null input list")
        void shouldThrowExceptionForNullInputList() {
            // Given
            List<Integer> input = null;

            // When & Then
            assertThatThrownBy(() -> Exercise3.map(Object::toString, input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Input list cannot be null");
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should combine map and xor operations")
        void shouldCombineMapAndXorOperations() {
            // Given
            List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6);

            // When - Map numbers to their parity (odd = true, even = false), then xor
            List<Boolean> parities = Exercise3.map(x -> x % 2 == 1, numbers);
            Boolean result = Exercise3.xor(parities);

            // Then - Should be true because there are 3 odd numbers (odd count of true)
            assertThat(parities).containsExactly(true, false, true, false, true, false);
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Should work with map and xor in complex scenario")
        void shouldWorkWithMapAndXorInComplexScenario() {
            // Given
            List<String> words = List.of("a", "bb", "ccc", "dddd");

            // When - Map to length > 2, then xor
            List<Boolean> longWords = Exercise3.map(word -> word.length() > 2, words);
            Boolean result = Exercise3.xor(longWords);

            // Then - Should be false because 2 words have length > 2 (even count)
            assertThat(longWords).containsExactly(false, false, true, true);
            assertThat(result).isFalse();
        }
    }
}
