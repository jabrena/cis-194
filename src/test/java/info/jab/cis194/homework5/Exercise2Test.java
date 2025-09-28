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
            Integer equalValue1 = 42;
            Integer equalValue2 = 42;
            Integer differentValue = 24;

            // When
            boolean areEqual = Exercise2.eq(equalValue1, equalValue2);
            boolean areDifferent = Exercise2.eq(equalValue1, differentValue);
            boolean areNotEqual = Exercise2.neq(equalValue1, equalValue2);
            boolean areNotDifferent = Exercise2.neq(equalValue1, differentValue);

            // Then
            assertThat(areEqual).isTrue();
            assertThat(areDifferent).isFalse();
            assertThat(areNotEqual).isFalse();
            assertThat(areNotDifferent).isTrue();
        }

        @Test
        @DisplayName("Should check equality for strings correctly")
        void shouldCheckEqualityForStringsCorrectly() {
            // Given
            String equalString1 = "hello";
            String equalString2 = "hello";
            String differentString = "world";

            // When
            boolean stringsAreEqual = Exercise2.eq(equalString1, equalString2);
            boolean stringsAreDifferent = Exercise2.eq(equalString1, differentString);
            boolean stringsAreNotEqual = Exercise2.neq(equalString1, equalString2);
            boolean stringsAreNotDifferent = Exercise2.neq(equalString1, differentString);

            // Then
            assertThat(stringsAreEqual).isTrue();
            assertThat(stringsAreDifferent).isFalse();
            assertThat(stringsAreNotEqual).isFalse();
            assertThat(stringsAreNotDifferent).isTrue();
        }

        @Test
        @DisplayName("Should check equality for custom objects correctly")
        void shouldCheckEqualityForCustomObjectsCorrectly() {
            // Given
            Exercise2.Point equalPoint1 = new Exercise2.Point(3, 4);
            Exercise2.Point equalPoint2 = new Exercise2.Point(3, 4);
            Exercise2.Point differentPoint = new Exercise2.Point(1, 2);

            // When
            boolean pointsAreEqual = Exercise2.eq(equalPoint1, equalPoint2);
            boolean pointsAreDifferent = Exercise2.eq(equalPoint1, differentPoint);
            boolean pointsAreNotEqual = Exercise2.neq(equalPoint1, equalPoint2);
            boolean pointsAreNotDifferent = Exercise2.neq(equalPoint1, differentPoint);

            // Then
            assertThat(pointsAreEqual).isTrue();
            assertThat(pointsAreDifferent).isFalse();
            assertThat(pointsAreNotEqual).isFalse();
            assertThat(pointsAreNotDifferent).isTrue();
        }
    }

    @Nested
    @DisplayName("Ord Type Class Tests")
    class OrdTypeClassTests {

        @Test
        @DisplayName("Should compare integers correctly")
        void shouldCompareIntegersCorrectly() {
            // Given
            Integer smallerValue = 10;
            Integer largerValue = 20;
            Integer equalValue = 10;

            // When
            boolean smallerLessThanLarger = Exercise2.lt(smallerValue, largerValue);
            boolean largerLessThanSmaller = Exercise2.lt(largerValue, smallerValue);
            boolean equalLessThanEqual = Exercise2.lt(smallerValue, equalValue);

            boolean smallerLessThanOrEqualLarger = Exercise2.lte(smallerValue, largerValue);
            boolean largerLessThanOrEqualSmaller = Exercise2.lte(largerValue, smallerValue);
            boolean equalLessThanOrEqualEqual = Exercise2.lte(smallerValue, equalValue);

            boolean smallerGreaterThanLarger = Exercise2.gt(smallerValue, largerValue);
            boolean largerGreaterThanSmaller = Exercise2.gt(largerValue, smallerValue);
            boolean equalGreaterThanEqual = Exercise2.gt(smallerValue, equalValue);

            boolean smallerGreaterThanOrEqualLarger = Exercise2.gte(smallerValue, largerValue);
            boolean largerGreaterThanOrEqualSmaller = Exercise2.gte(largerValue, smallerValue);
            boolean equalGreaterThanOrEqualEqual = Exercise2.gte(smallerValue, equalValue);

            // Then
            assertThat(smallerLessThanLarger).isTrue();
            assertThat(largerLessThanSmaller).isFalse();
            assertThat(equalLessThanEqual).isFalse();

            assertThat(smallerLessThanOrEqualLarger).isTrue();
            assertThat(largerLessThanOrEqualSmaller).isFalse();
            assertThat(equalLessThanOrEqualEqual).isTrue();

            assertThat(smallerGreaterThanLarger).isFalse();
            assertThat(largerGreaterThanSmaller).isTrue();
            assertThat(equalGreaterThanEqual).isFalse();

            assertThat(smallerGreaterThanOrEqualLarger).isFalse();
            assertThat(largerGreaterThanOrEqualSmaller).isTrue();
            assertThat(equalGreaterThanOrEqualEqual).isTrue();
        }

        @Test
        @DisplayName("Should compare strings correctly")
        void shouldCompareStringsCorrectly() {
            // Given
            String earlierString = "apple";
            String laterString = "banana";
            String equalString = "apple";

            // When
            boolean earlierLessThanLater = Exercise2.lt(earlierString, laterString);
            boolean laterLessThanEarlier = Exercise2.lt(laterString, earlierString);
            boolean equalLessThanEqual = Exercise2.lt(earlierString, equalString);

            boolean laterGreaterThanOrEqualEarlier = Exercise2.gte(laterString, earlierString);
            boolean earlierGreaterThanOrEqualLater = Exercise2.gte(earlierString, laterString);
            boolean equalGreaterThanOrEqualEqual = Exercise2.gte(earlierString, equalString);

            // Then
            assertThat(earlierLessThanLater).isTrue();
            assertThat(laterLessThanEarlier).isFalse();
            assertThat(equalLessThanEqual).isFalse();

            assertThat(laterGreaterThanOrEqualEarlier).isTrue();
            assertThat(earlierGreaterThanOrEqualLater).isFalse();
            assertThat(equalGreaterThanOrEqualEqual).isTrue();
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
            Integer existingElement = 3;
            Integer nonExistingElement = 6;
            Integer firstElement = 1;
            Integer lastElement = 5;

            // When
            boolean existingElementFound = Exercise2.elem(existingElement, numbers);
            boolean nonExistingElementFound = Exercise2.elem(nonExistingElement, numbers);
            boolean firstElementFound = Exercise2.elem(firstElement, numbers);
            boolean lastElementFound = Exercise2.elem(lastElement, numbers);

            // Then
            assertThat(existingElementFound).isTrue();
            assertThat(nonExistingElementFound).isFalse();
            assertThat(firstElementFound).isTrue();
            assertThat(lastElementFound).isTrue();
        }

        @Test
        @DisplayName("Should implement elem function for strings correctly")
        void shouldImplementElemFunctionForStringsCorrectly() {
            // Given
            List<String> words = List.of("apple", "banana", "cherry");
            String existingWord = "banana";
            String nonExistingWord = "grape";
            String firstWord = "apple";

            // When
            boolean existingWordFound = Exercise2.elem(existingWord, words);
            boolean nonExistingWordFound = Exercise2.elem(nonExistingWord, words);
            boolean firstWordFound = Exercise2.elem(firstWord, words);

            // Then
            assertThat(existingWordFound).isTrue();
            assertThat(nonExistingWordFound).isFalse();
            assertThat(firstWordFound).isTrue();
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
            Exercise2.Point smallerPoint = new Exercise2.Point(1, 2);
            Exercise2.Point largerPoint = new Exercise2.Point(3, 4);
            Exercise2.Point equalPoint = new Exercise2.Point(1, 2);

            // When
            boolean smallerLessThanLarger = Exercise2.lt(smallerPoint, largerPoint);
            boolean largerLessThanSmaller = Exercise2.lt(largerPoint, smallerPoint);
            boolean pointsAreEqual = Exercise2.eq(smallerPoint, equalPoint);
            boolean pointsAreDifferent = Exercise2.eq(smallerPoint, largerPoint);

            // Then
            assertThat(smallerLessThanLarger).isTrue();
            assertThat(largerLessThanSmaller).isFalse();
            assertThat(pointsAreEqual).isTrue();
            assertThat(pointsAreDifferent).isFalse();
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
            Integer nullValue = null;
            Integer validValue = 42;

            // When
            var throwableAssert = assertThatThrownBy(() -> Exercise2.eq(nullValue, validValue));

            // Then
            throwableAssert
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Arguments cannot be null");
        }

        @Test
        @DisplayName("Should handle null lists in polymorphic functions")
        void shouldHandleNullListsInPolymorphicFunctions() {
            // Given
            List<Integer> nullList = null;

            // When
            var throwableAssert = assertThatThrownBy(() -> Exercise2.maximum(nullList));

            // Then
            throwableAssert
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("List cannot be null");
        }
    }
}
