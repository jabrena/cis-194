package info.jab.cis194.homework5;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Exercise 2: Polymorphic Functions and Type Classes")
class Exercise2Test {

    @Nested
    @DisplayName("Eq Type Class Tests")
    class EqTypeClassTests {

        @Test
        @DisplayName("Should check equality for integers correctly")
        void shouldCheckEqualityForIntegersCorrectly() {
            // Given
            Integer a = 42;
            Integer b = 42;
            Integer c = 24;

            // When & Then
            assertThat(Exercise2.eq(a, b)).isTrue();
            assertThat(Exercise2.eq(a, c)).isFalse();
            assertThat(Exercise2.neq(a, b)).isFalse();
            assertThat(Exercise2.neq(a, c)).isTrue();
        }

        @Test
        @DisplayName("Should check equality for strings correctly")
        void shouldCheckEqualityForStringsCorrectly() {
            // Given
            String a = "hello";
            String b = "hello";
            String c = "world";

            // When & Then
            assertThat(Exercise2.eq(a, b)).isTrue();
            assertThat(Exercise2.eq(a, c)).isFalse();
            assertThat(Exercise2.neq(a, b)).isFalse();
            assertThat(Exercise2.neq(a, c)).isTrue();
        }

        @Test
        @DisplayName("Should check equality for custom objects correctly")
        void shouldCheckEqualityForCustomObjectsCorrectly() {
            // Given
            Exercise2.Point p1 = new Exercise2.Point(3, 4);
            Exercise2.Point p2 = new Exercise2.Point(3, 4);
            Exercise2.Point p3 = new Exercise2.Point(1, 2);

            // When & Then
            assertThat(Exercise2.eq(p1, p2)).isTrue();
            assertThat(Exercise2.eq(p1, p3)).isFalse();
            assertThat(Exercise2.neq(p1, p2)).isFalse();
            assertThat(Exercise2.neq(p1, p3)).isTrue();
        }
    }

    @Nested
    @DisplayName("Ord Type Class Tests")
    class OrdTypeClassTests {

        @Test
        @DisplayName("Should compare integers correctly")
        void shouldCompareIntegersCorrectly() {
            // Given
            Integer a = 10;
            Integer b = 20;
            Integer c = 10;

            // When & Then
            assertThat(Exercise2.lt(a, b)).isTrue();
            assertThat(Exercise2.lt(b, a)).isFalse();
            assertThat(Exercise2.lt(a, c)).isFalse();

            assertThat(Exercise2.lte(a, b)).isTrue();
            assertThat(Exercise2.lte(b, a)).isFalse();
            assertThat(Exercise2.lte(a, c)).isTrue();

            assertThat(Exercise2.gt(a, b)).isFalse();
            assertThat(Exercise2.gt(b, a)).isTrue();
            assertThat(Exercise2.gt(a, c)).isFalse();

            assertThat(Exercise2.gte(a, b)).isFalse();
            assertThat(Exercise2.gte(b, a)).isTrue();
            assertThat(Exercise2.gte(a, c)).isTrue();
        }

        @Test
        @DisplayName("Should compare strings correctly")
        void shouldCompareStringsCorrectly() {
            // Given
            String a = "apple";
            String b = "banana";
            String c = "apple";

            // When & Then
            assertThat(Exercise2.lt(a, b)).isTrue();
            assertThat(Exercise2.lt(b, a)).isFalse();
            assertThat(Exercise2.lt(a, c)).isFalse();

            assertThat(Exercise2.gte(b, a)).isTrue();
            assertThat(Exercise2.gte(a, b)).isFalse();
            assertThat(Exercise2.gte(a, c)).isTrue();
        }
    }

    @Nested
    @DisplayName("Show Type Class Tests")
    class ShowTypeClassTests {

        @Test
        @DisplayName("Should show integers correctly")
        void shouldShowIntegersCorrectly() {
            // Given
            Integer value = 42;

            // When
            String result = Exercise2.show(value);

            // Then
            assertThat(result).isEqualTo("42");
        }

        @Test
        @DisplayName("Should show strings correctly")
        void shouldShowStringsCorrectly() {
            // Given
            String value = "hello";

            // When
            String result = Exercise2.show(value);

            // Then
            assertThat(result).isEqualTo("\"hello\"");
        }

        @Test
        @DisplayName("Should show custom objects correctly")
        void shouldShowCustomObjectsCorrectly() {
            // Given
            Exercise2.Point point = new Exercise2.Point(3, 4);

            // When
            String result = Exercise2.show(point);

            // Then
            assertThat(result).isEqualTo("Point(3, 4)");
        }

        @Test
        @DisplayName("Should show lists correctly")
        void shouldShowListsCorrectly() {
            // Given
            List<Integer> list = List.of(1, 2, 3);

            // When
            String result = Exercise2.showList(list);

            // Then
            assertThat(result).isEqualTo("[1, 2, 3]");
        }

        @Test
        @DisplayName("Should show empty lists correctly")
        void shouldShowEmptyListsCorrectly() {
            // Given
            List<Integer> list = List.of();

            // When
            String result = Exercise2.showList(list);

            // Then
            assertThat(result).isEqualTo("[]");
        }
    }

    @Nested
    @DisplayName("Polymorphic Function Tests")
    class PolymorphicFunctionTests {

        @Test
        @DisplayName("Should find maximum element in list correctly")
        void shouldFindMaximumElementInListCorrectly() {
            // Given
            List<Integer> numbers = List.of(3, 1, 4, 1, 5, 9, 2, 6);

            // When
            Optional<Integer> result = Exercise2.maximum(numbers);

            // Then
            assertThat(result).hasValue(9);
        }

        @Test
        @DisplayName("Should find maximum string in list correctly")
        void shouldFindMaximumStringInListCorrectly() {
            // Given
            List<String> words = List.of("apple", "zebra", "banana", "cherry");

            // When
            Optional<String> result = Exercise2.maximum(words);

            // Then
            assertThat(result).hasValue("zebra");
        }

        @Test
        @DisplayName("Should return empty optional for empty list")
        void shouldReturnEmptyOptionalForEmptyList() {
            // Given
            List<Integer> numbers = List.of();

            // When
            Optional<Integer> result = Exercise2.maximum(numbers);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should sort list correctly")
        void shouldSortListCorrectly() {
            // Given
            List<Integer> numbers = List.of(3, 1, 4, 1, 5, 9, 2, 6);

            // When
            List<Integer> result = Exercise2.sort(numbers);

            // Then
            assertThat(result).containsExactly(1, 1, 2, 3, 4, 5, 6, 9);
        }

        @Test
        @DisplayName("Should sort strings correctly")
        void shouldSortStringsCorrectly() {
            // Given
            List<String> words = List.of("zebra", "apple", "cherry", "banana");

            // When
            List<String> result = Exercise2.sort(words);

            // Then
            assertThat(result).containsExactly("apple", "banana", "cherry", "zebra");
        }

        @Test
        @DisplayName("Should sort empty list correctly")
        void shouldSortEmptyListCorrectly() {
            // Given
            List<Integer> numbers = List.of();

            // When
            List<Integer> result = Exercise2.sort(numbers);

            // Then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Generic Programming Tests")
    class GenericProgrammingTests {

        @Test
        @DisplayName("Should implement elem function correctly")
        void shouldImplementElemFunctionCorrectly() {
            // Given
            List<Integer> numbers = List.of(1, 2, 3, 4, 5);

            // When & Then
            assertThat(Exercise2.elem(3, numbers)).isTrue();
            assertThat(Exercise2.elem(6, numbers)).isFalse();
            assertThat(Exercise2.elem(1, numbers)).isTrue();
            assertThat(Exercise2.elem(5, numbers)).isTrue();
        }

        @Test
        @DisplayName("Should implement elem function for strings correctly")
        void shouldImplementElemFunctionForStringsCorrectly() {
            // Given
            List<String> words = List.of("apple", "banana", "cherry");

            // When & Then
            assertThat(Exercise2.elem("banana", words)).isTrue();
            assertThat(Exercise2.elem("grape", words)).isFalse();
            assertThat(Exercise2.elem("apple", words)).isTrue();
        }

        @Test
        @DisplayName("Should implement nub function correctly")
        void shouldImplementNubFunctionCorrectly() {
            // Given
            List<Integer> numbersWithDuplicates = List.of(1, 2, 2, 3, 1, 4, 3, 5);

            // When
            List<Integer> result = Exercise2.nub(numbersWithDuplicates);

            // Then
            assertThat(result).containsExactly(1, 2, 3, 4, 5);
        }

        @Test
        @DisplayName("Should implement nub function for strings correctly")
        void shouldImplementNubFunctionForStringsCorrectly() {
            // Given
            List<String> wordsWithDuplicates = List.of("apple", "banana", "apple", "cherry", "banana");

            // When
            List<String> result = Exercise2.nub(wordsWithDuplicates);

            // Then
            assertThat(result).containsExactly("apple", "banana", "cherry");
        }

        @Test
        @DisplayName("Should handle empty list in nub function")
        void shouldHandleEmptyListInNubFunction() {
            // Given
            List<Integer> emptyList = List.of();

            // When
            List<Integer> result = Exercise2.nub(emptyList);

            // Then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Point Class Tests")
    class PointClassTests {

        @Test
        @DisplayName("Should create point correctly")
        void shouldCreatePointCorrectly() {
            // Given & When
            Exercise2.Point point = new Exercise2.Point(3, 4);

            // Then
            assertThat(point.x()).isEqualTo(3);
            assertThat(point.y()).isEqualTo(4);
        }

        @Test
        @DisplayName("Should compare points correctly")
        void shouldComparePointsCorrectly() {
            // Given
            Exercise2.Point p1 = new Exercise2.Point(1, 2);
            Exercise2.Point p2 = new Exercise2.Point(3, 4);
            Exercise2.Point p3 = new Exercise2.Point(1, 2);

            // When & Then
            assertThat(Exercise2.lt(p1, p2)).isTrue();
            assertThat(Exercise2.lt(p2, p1)).isFalse();
            assertThat(Exercise2.eq(p1, p3)).isTrue();
            assertThat(Exercise2.eq(p1, p2)).isFalse();
        }

        @Test
        @DisplayName("Should calculate distance correctly")
        void shouldCalculateDistanceCorrectly() {
            // Given
            Exercise2.Point p1 = new Exercise2.Point(0, 0);
            Exercise2.Point p2 = new Exercise2.Point(3, 4);

            // When
            double distance = Exercise2.distance(p1, p2);

            // Then
            assertThat(distance).isEqualTo(5.0);
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should handle null values in equality checks")
        void shouldHandleNullValuesInEqualityChecks() {
            // Given
            Integer a = null;
            Integer b = 42;

            // When & Then
            assertThatThrownBy(() -> Exercise2.eq(a, b))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Arguments cannot be null");
        }

        @Test
        @DisplayName("Should handle null lists in polymorphic functions")
        void shouldHandleNullListsInPolymorphicFunctions() {
            // Given
            List<Integer> nullList = null;

            // When & Then
            assertThatThrownBy(() -> Exercise2.maximum(nullList))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("List cannot be null");
        }
    }
}
