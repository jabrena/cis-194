package info.jab.cis194.homework4;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Exercise 2: Tree Folding - foldTree")
class Exercise2Test {

    @Nested
    @DisplayName("Tree Construction Tests")
    class TreeConstructionTests {

        @Test
        @DisplayName("Should create empty tree for empty input")
        void shouldCreateEmptyTreeForEmptyInput() {
            // Given
            List<String> input = List.of();

            // When
            Tree<String> result = Exercise2.foldTree(input);

            // Then
            assertThat(result).isEqualTo(Tree.leaf());
        }

        @Test
        @DisplayName("Should create single node tree for single element")
        void shouldCreateSingleNodeTreeForSingleElement() {
            // Given
            List<String> input = List.of("A");

            // When
            Tree<String> result = Exercise2.foldTree(input);

            // Then
            assertThat(result).isEqualTo(Tree.node(0, Tree.leaf(), "A", Tree.leaf()));
        }

        @Test
        @DisplayName("Should create balanced tree for two elements")
        void shouldCreateBalancedTreeForTwoElements() {
            // Given
            List<String> input = List.of("A", "B");

            // When
            Tree<String> result = Exercise2.foldTree(input);

            // Then
            assertThat(result.height()).isEqualTo(1);
            assertThat(result.size()).isEqualTo(2);
            assertThat(isBalanced(result)).isTrue();
            assertThat(result.inOrder()).containsExactlyInAnyOrder("A", "B");
        }

        @Test
        @DisplayName("Should create balanced tree for three elements")
        void shouldCreateBalancedTreeForThreeElements() {
            // Given
            List<String> input = List.of("A", "B", "C");

            // When
            Tree<String> result = Exercise2.foldTree(input);

            // Then
            assertThat(result.height()).isEqualTo(1);
            assertThat(result.size()).isEqualTo(3);
            assertThat(isBalanced(result)).isTrue();
            assertThat(result.inOrder()).containsExactlyInAnyOrder("A", "B", "C");
        }

        @Test
        @DisplayName("Should create balanced tree for four elements")
        void shouldCreateBalancedTreeForFourElements() {
            // Given
            List<String> input = List.of("A", "B", "C", "D");

            // When
            Tree<String> result = Exercise2.foldTree(input);

            // Then
            assertThat(result.height()).isEqualTo(2);
            assertThat(result.size()).isEqualTo(4);
            assertThat(isBalanced(result)).isTrue();
            assertThat(result.inOrder()).containsExactlyInAnyOrder("A", "B", "C", "D");
        }

        @Test
        @DisplayName("Should create balanced tree for larger input")
        void shouldCreateBalancedTreeForLargerInput() {
            // Given
            List<String> input = List.of("A", "B", "C", "D", "E", "F", "G");

            // When
            Tree<String> result = Exercise2.foldTree(input);

            // Then
            // The tree should be balanced with appropriate heights
            assertThat(result.height()).isLessThanOrEqualTo(3);
            assertThat(result.size()).isEqualTo(7);
            assertThat(isBalanced(result)).isTrue();
        }
    }

    @Nested
    @DisplayName("Tree Properties Tests")
    class TreePropertiesTests {

        @Test
        @DisplayName("Should maintain balanced property")
        void shouldMaintainBalancedProperty() {
            // Given
            List<Integer> input = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

            // When
            Tree<Integer> result = Exercise2.foldTree(input);

            // Then
            assertThat(isBalanced(result)).isTrue();
        }

        @Test
        @DisplayName("Should have correct size")
        void shouldHaveCorrectSize() {
            // Given
            List<Integer> input = List.of(1, 2, 3, 4, 5);

            // When
            Tree<Integer> result = Exercise2.foldTree(input);

            // Then
            assertThat(result.size()).isEqualTo(5);
        }

        @Test
        @DisplayName("Should have optimal height for balanced tree")
        void shouldHaveOptimalHeightForBalancedTree() {
            // Given
            List<Integer> inputs = List.of(1, 2, 3, 4, 5, 6, 7, 8);

            // When
            Tree<Integer> result = Exercise2.foldTree(inputs);

            // Then
            // For 8 nodes, the optimal height is 3 (log2(8) = 3)
            assertThat(result.height()).isEqualTo(3);
        }

        @Test
        @DisplayName("Should preserve all elements from input")
        void shouldPreserveAllElementsFromInput() {
            // Given
            List<String> input = List.of("X", "Y", "Z", "W", "V");

            // When
            Tree<String> result = Exercise2.foldTree(input);

            // Then
            List<String> inOrder = result.inOrder();
            assertThat(inOrder).containsExactlyInAnyOrder("X", "Y", "Z", "W", "V");
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Handling")
    class EdgeCasesAndErrorHandling {

        @Test
        @DisplayName("Should throw exception for null input")
        void shouldThrowExceptionForNullInput() {
            // Given
            List<String> input = null;

            // When & Then
            assertThatThrownBy(() -> Exercise2.foldTree(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Input list cannot be null");
        }

        @Test
        @DisplayName("Should handle single element correctly")
        void shouldHandleSingleElementCorrectly() {
            // Given
            List<Integer> input = List.of(42);

            // When
            Tree<Integer> result = Exercise2.foldTree(input);

            // Then
            assertThat(result.height()).isEqualTo(0);
            assertThat(result.size()).isEqualTo(1);
            assertThat(result.value()).isEqualTo(42);
        }

        @Test
        @DisplayName("Should handle duplicate elements")
        void shouldHandleDuplicateElements() {
            // Given
            List<String> input = List.of("A", "A", "B", "B", "C");

            // When
            Tree<String> result = Exercise2.foldTree(input);

            // Then
            assertThat(result.size()).isEqualTo(5);
            assertThat(isBalanced(result)).isTrue();
        }
    }

    @Nested
    @DisplayName("Type Safety Tests")
    class TypeSafetyTests {

        @Test
        @DisplayName("Should work with different types - Integer")
        void shouldWorkWithIntegers() {
            // Given
            List<Integer> input = List.of(10, 20, 30);

            // When
            Tree<Integer> result = Exercise2.foldTree(input);

            // Then
            assertThat(result.size()).isEqualTo(3);
            assertThat(result.inOrder()).containsExactlyInAnyOrder(10, 20, 30);
        }

        @Test
        @DisplayName("Should work with different types - Double")
        void shouldWorkWithDoubles() {
            // Given
            List<Double> input = List.of(1.5, 2.7, 3.14);

            // When
            Tree<Double> result = Exercise2.foldTree(input);

            // Then
            assertThat(result.size()).isEqualTo(3);
            assertThat(result.inOrder()).containsExactlyInAnyOrder(1.5, 2.7, 3.14);
        }

        @Test
        @DisplayName("Should work with different types - Custom objects")
        void shouldWorkWithCustomObjects() {
            // Given
            List<TestObject> input = List.of(
                new TestObject("first"),
                new TestObject("second"),
                new TestObject("third")
            );

            // When
            Tree<TestObject> result = Exercise2.foldTree(input);

            // Then
            assertThat(result.size()).isEqualTo(3);
        }
    }

    // Helper methods
    private <T> boolean isBalanced(Tree<T> tree) {
        if (tree.isLeaf()) {
            return true;
        }

        int leftHeight = tree.left().height();
        int rightHeight = tree.right().height();

        return Math.abs(leftHeight - rightHeight) <= 1
            && isBalanced(tree.left())
            && isBalanced(tree.right());
    }

    // Test helper class
    private static class TestObject {
        private final String value;

        public TestObject(String value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            TestObject that = (TestObject) obj;
            return value.equals(that.value);
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }

        @Override
        public String toString() {
            return "TestObject{" + value + "}";
        }
    }
}
