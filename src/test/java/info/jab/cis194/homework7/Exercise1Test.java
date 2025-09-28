package info.jab.cis194.homework7;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Test class for Exercise 1: JoinList implementation
 *
 * This exercise focuses on implementing a JoinList data structure
 * that efficiently supports concatenation using monoids.
 * JoinList is a binary tree structure that allows O(1) concatenation.
 */
public class Exercise1Test {

    @Test
    public void testEmptyJoinList() {
        // Given
        // No setup needed for empty JoinList creation

        // When
        Exercise1.JoinList<String> empty = Exercise1.empty();

        // Then
        assertThat(empty).isNotNull();
        assertThat(empty.isEmpty()).isTrue();
        assertThat(empty.size()).isZero();
    }

    @Test
    public void testSingleJoinList() {
        // Given
        String element = "hello";

        // When
        Exercise1.JoinList<String> single = Exercise1.single(element);

        // Then
        assertThat(single).isNotNull();
        assertThat(single.isEmpty()).isFalse();
        assertThat(single.size()).isEqualTo(1);
    }

    @Test
    public void testJoinListConcatenation() {
        // Given
        Exercise1.JoinList<String> list1 = Exercise1.single("hello");
        Exercise1.JoinList<String> list2 = Exercise1.single("world");

        // When
        Exercise1.JoinList<String> joined = list1.append(list2);

        // Then
        assertThat(joined).isNotNull();
        assertThat(joined.isEmpty()).isFalse();
        assertThat(joined.size()).isEqualTo(2);
    }

    @Test
    public void testJoinListToList() {
        // Given
        Exercise1.JoinList<String> list1 = Exercise1.single("hello");
        Exercise1.JoinList<String> list2 = Exercise1.single("world");
        Exercise1.JoinList<String> list3 = Exercise1.single("!");
        Exercise1.JoinList<String> joined = list1.append(list2).append(list3);

        // When
        List<String> result = joined.toList();

        // Then
        List<String> expected = Arrays.asList("hello", "world", "!");
        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void testJoinListFromList() {
        // Given
        List<String> input = Arrays.asList("a", "b", "c", "d");

        // When
        Exercise1.JoinList<String> joinList = Exercise1.fromList(input);

        // Then
        assertThat(joinList).isNotNull();
        assertThat(joinList.size()).isEqualTo(4);

        List<String> result = joinList.toList();
        assertThat(result).isEqualTo(input);
    }

    @Test
    public void testJoinListIndexing() {
        // Given
        List<String> input = Arrays.asList("first", "second", "third", "fourth");
        Exercise1.JoinList<String> joinList = Exercise1.fromList(input);

        // When & Then
        assertThat(joinList.indexJ(0)).isEqualTo("first");
        assertThat(joinList.indexJ(1)).isEqualTo("second");
        assertThat(joinList.indexJ(2)).isEqualTo("third");
        assertThat(joinList.indexJ(3)).isEqualTo("fourth");
    }

    @Test
    public void testJoinListIndexingOutOfBounds() {
        // Given
        Exercise1.JoinList<String> joinList = Exercise1.single("test");

        // When & Then
        assertThatThrownBy(() -> joinList.indexJ(-1))
            .isInstanceOf(IndexOutOfBoundsException.class);

        assertThatThrownBy(() -> joinList.indexJ(1))
            .isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    public void testJoinListDropJ() {
        // Given
        List<String> input = Arrays.asList("a", "b", "c", "d", "e");
        Exercise1.JoinList<String> joinList = Exercise1.fromList(input);

        // When
        Exercise1.JoinList<String> dropped = joinList.dropJ(2);
        List<String> result = dropped.toList();

        // Then
        List<String> expected = Arrays.asList("c", "d", "e");
        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void testJoinListTakeJ() {
        // Given
        List<String> input = Arrays.asList("a", "b", "c", "d", "e");
        Exercise1.JoinList<String> joinList = Exercise1.fromList(input);

        // When
        Exercise1.JoinList<String> taken = joinList.takeJ(3);
        List<String> result = taken.toList();

        // Then
        List<String> expected = Arrays.asList("a", "b", "c");
        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void testEmptyJoinListOperations() {
        // Given
        Exercise1.JoinList<String> empty = Exercise1.empty();

        // When & Then
        assertThat(empty.size()).isZero();
        assertThat(empty.toList()).isEmpty();

        Exercise1.JoinList<String> droppedEmpty = empty.dropJ(5);
        assertThat(droppedEmpty.isEmpty()).isTrue();

        Exercise1.JoinList<String> takenEmpty = empty.takeJ(5);
        assertThat(takenEmpty.isEmpty()).isTrue();
    }
}
