package info.jab.cis194.homework7;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test class for Exercise 1: JoinList implementation
 *
 * This exercise focuses on implementing a JoinList data structure
 * that efficiently supports concatenation using monoids.
 * JoinList is a binary tree structure that allows O(1) concatenation.
 *
 * Tests follow Given-When-Then structure and use AssertJ for fluent assertions.
 */
@DisplayName("Exercise 1: JoinList Implementation Tests")
public class Exercise1Test {

    @Nested
    @DisplayName("JoinList Creation")
    class JoinListCreation {

        @Test
        @DisplayName("Should create empty JoinList with correct properties")
        public void should_createEmptyJoinList_when_emptyMethodCalled() {
            // Given
            // No setup needed for empty JoinList creation

            // When
            Exercise1.JoinList<String> empty = Exercise1.empty();

            // Then
            assertThat(empty)
                .as("Empty JoinList should not be null")
                .isNotNull();
            assertThat(empty.isEmpty())
                .as("Empty JoinList should be empty")
                .isTrue();
            assertThat(empty.size())
                .as("Empty JoinList should have size 0")
                .isZero();
        }

        @Test
        @DisplayName("Should create single element JoinList with correct properties")
        public void should_createSingleJoinList_when_singleMethodCalledWithElement() {
            // Given
            String element = "hello";

            // When
            Exercise1.JoinList<String> single = Exercise1.single(element);

            // Then
            assertThat(single)
                .as("Single JoinList should not be null")
                .isNotNull();
            assertThat(single.isEmpty())
                .as("Single JoinList should not be empty")
                .isFalse();
            assertThat(single.size())
                .as("Single JoinList should have size 1")
                .isEqualTo(1);
        }

        @Test
        @DisplayName("Should create JoinList from regular list")
        public void should_createJoinListFromList_when_fromListMethodCalled() {
            // Given
            List<String> input = Arrays.asList("a", "b", "c", "d");

            // When
            Exercise1.JoinList<String> joinList = Exercise1.fromList(input);
            List<String> result = joinList.toList();

            // Then
            assertThat(joinList)
                .as("JoinList created from list should not be null")
                .isNotNull();
            assertThat(joinList.size())
                .as("JoinList should have same size as input list")
                .isEqualTo(4);
            assertThat(result)
                .as("JoinList converted back to list should equal original")
                .isEqualTo(input);
        }
    }

    @Nested
    @DisplayName("JoinList Operations")
    class JoinListOperations {

        @Test
        @DisplayName("Should concatenate two JoinLists correctly")
        public void should_concatenateJoinLists_when_appendMethodCalled() {
            // Given
            Exercise1.JoinList<String> list1 = Exercise1.single("hello");
            Exercise1.JoinList<String> list2 = Exercise1.single("world");

            // When
            Exercise1.JoinList<String> joined = list1.append(list2);

            // Then
            assertThat(joined)
                .as("Joined JoinList should not be null")
                .isNotNull();
            assertThat(joined.isEmpty())
                .as("Joined JoinList should not be empty")
                .isFalse();
            assertThat(joined.size())
                .as("Joined JoinList should have combined size")
                .isEqualTo(2);
        }

        @Test
        @DisplayName("Should convert JoinList to regular list correctly")
        public void should_convertToList_when_toListMethodCalled() {
            // Given
            Exercise1.JoinList<String> list1 = Exercise1.single("hello");
            Exercise1.JoinList<String> list2 = Exercise1.single("world");
            Exercise1.JoinList<String> list3 = Exercise1.single("!");
            Exercise1.JoinList<String> joined = list1.append(list2).append(list3);
            List<String> expected = Arrays.asList("hello", "world", "!");

            // When
            List<String> result = joined.toList();

            // Then
            assertThat(result)
                .as("Converted list should match expected sequence")
                .isEqualTo(expected);
        }

        @Test
        @DisplayName("Should drop first n elements correctly")
        public void should_dropFirstNElements_when_dropJMethodCalled() {
            // Given
            List<String> input = Arrays.asList("a", "b", "c", "d", "e");
            Exercise1.JoinList<String> joinList = Exercise1.fromList(input);
            List<String> expected = Arrays.asList("c", "d", "e");

            // When
            Exercise1.JoinList<String> dropped = joinList.dropJ(2);
            List<String> result = dropped.toList();

            // Then
            assertThat(result)
                .as("List after dropping 2 elements should match expected")
                .isEqualTo(expected);
        }

        @Test
        @DisplayName("Should take first n elements correctly")
        public void should_takeFirstNElements_when_takeJMethodCalled() {
            // Given
            List<String> input = Arrays.asList("a", "b", "c", "d", "e");
            Exercise1.JoinList<String> joinList = Exercise1.fromList(input);
            List<String> expected = Arrays.asList("a", "b", "c");

            // When
            Exercise1.JoinList<String> taken = joinList.takeJ(3);
            List<String> result = taken.toList();

            // Then
            assertThat(result)
                .as("List after taking 3 elements should match expected")
                .isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("JoinList Indexing")
    class JoinListIndexing {

        @Test
        @DisplayName("Should access elements by index correctly")
        public void should_accessElementsByIndex_when_indexJMethodCalled() {
            // Given
            List<String> input = Arrays.asList("first", "second", "third", "fourth");
            Exercise1.JoinList<String> joinList = Exercise1.fromList(input);

            // When & Then
            assertThat(joinList.indexJ(0))
                .as("Element at index 0 should be 'first'")
                .isEqualTo("first");
            assertThat(joinList.indexJ(1))
                .as("Element at index 1 should be 'second'")
                .isEqualTo("second");
            assertThat(joinList.indexJ(2))
                .as("Element at index 2 should be 'third'")
                .isEqualTo("third");
            assertThat(joinList.indexJ(3))
                .as("Element at index 3 should be 'fourth'")
                .isEqualTo("fourth");
        }

        @Test
        @DisplayName("Should throw IndexOutOfBoundsException for invalid indices")
        public void should_throwIndexOutOfBoundsException_when_invalidIndexProvided() {
            // Given
            Exercise1.JoinList<String> joinList = Exercise1.single("test");

            // When & Then
            assertThatThrownBy(() -> joinList.indexJ(-1))
                .as("Negative index should throw IndexOutOfBoundsException")
                .isInstanceOf(IndexOutOfBoundsException.class);

            assertThatThrownBy(() -> joinList.indexJ(1))
                .as("Index beyond size should throw IndexOutOfBoundsException")
                .isInstanceOf(IndexOutOfBoundsException.class);
        }
    }

    @Nested
    @DisplayName("Empty JoinList Edge Cases")
    class EmptyJoinListEdgeCases {

        @Test
        @DisplayName("Should handle operations on empty JoinList correctly")
        public void should_handleEmptyJoinListOperations_when_operationsCalledOnEmpty() {
            // Given
            Exercise1.JoinList<String> empty = Exercise1.empty();

            // When
            Exercise1.JoinList<String> droppedEmpty = empty.dropJ(5);
            Exercise1.JoinList<String> takenEmpty = empty.takeJ(5);

            // Then
            assertThat(empty.size())
                .as("Empty JoinList should have size 0")
                .isZero();
            assertThat(empty.toList())
                .as("Empty JoinList should convert to empty list")
                .isEmpty();
            assertThat(droppedEmpty.isEmpty())
                .as("Dropping from empty JoinList should remain empty")
                .isTrue();
            assertThat(takenEmpty.isEmpty())
                .as("Taking from empty JoinList should remain empty")
                .isTrue();
        }
    }
}
