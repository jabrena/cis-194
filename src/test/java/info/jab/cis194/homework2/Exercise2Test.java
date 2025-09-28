package info.jab.cis194.homework2;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.*;

/**
 * Test class for Exercise 2 - MessageTree Insert Function
 * Based on CIS-194 Homework 2
 *
 * This test suite covers the insert function for building binary search trees of log messages:
 * - Inserting valid messages into MessageTree
 * - Maintaining BST ordering by timestamp
 * - Handling Unknown messages (should not be inserted)
 * - Tree structure validation
 */
@DisplayName("Exercise 2: MessageTree Insert Function Tests")
class Exercise2Test {

    private Exercise2 exercise;

    @BeforeEach
    void setUp() {
        exercise = new Exercise2();
    }

    @Nested
    @DisplayName("Basic Insert Operations")
    class BasicInsertTests {

        @Test
        @DisplayName("Should insert single message into empty tree")
        void shouldInsertSingleMessageIntoEmptyTree() {
            // Given
            LogMessage.ValidMessage msg = new LogMessage.ValidMessage(MessageType.INFO, 10, "test");
            MessageTree emptyTree = MessageTree.leaf();

            // When
            MessageTree result = exercise.insert(msg, emptyTree);

            // Then
            assertThat(result).isInstanceOf(MessageTree.Node.class);
            MessageTree.Node node = (MessageTree.Node) result;
            assertThat(node.message()).isEqualTo(msg);
            assertThat(node.left()).isInstanceOf(MessageTree.Leaf.class);
            assertThat(node.right()).isInstanceOf(MessageTree.Leaf.class);
            assertThat(result.size()).isEqualTo(1);
        }

        @Test
        @DisplayName("Should not insert Unknown messages")
        void shouldNotInsertUnknownMessages() {
            // Given
            LogMessage.Unknown unknownMessage = new LogMessage.Unknown("invalid message");
            MessageTree emptyTree = MessageTree.leaf();

            // When
            MessageTree result = exercise.insert(unknownMessage, emptyTree);

            // Then
            assertThat(result).isSameAs(emptyTree);
            assertThat(result.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("Should maintain BST property when inserting smaller timestamp")
        void shouldInsertSmallerTimestampToLeft() {
            // Given
            LogMessage.ValidMessage rootMessage = new LogMessage.ValidMessage(MessageType.INFO, 10, "root");
            LogMessage.ValidMessage smallerMessage = new LogMessage.ValidMessage(MessageType.WARNING, 5, "smaller");
            MessageTree initialTree = MessageTree.node(MessageTree.leaf(), rootMessage, MessageTree.leaf());

            // When
            MessageTree result = exercise.insert(smallerMessage, initialTree);

            // Then
            assertThat(result).isInstanceOf(MessageTree.Node.class);
            MessageTree.Node node = (MessageTree.Node) result;
            assertThat(node.message()).isEqualTo(rootMessage);
            assertThat(node.left()).isInstanceOf(MessageTree.Node.class);
            assertThat(node.right()).isInstanceOf(MessageTree.Leaf.class);

            MessageTree.Node leftNode = (MessageTree.Node) node.left();
            assertThat(leftNode.message()).isEqualTo(smallerMessage);
        }

        @Test
        @DisplayName("Should maintain BST property when inserting larger timestamp")
        void shouldInsertLargerTimestampToRight() {
            // Given
            LogMessage.ValidMessage rootMessage = new LogMessage.ValidMessage(MessageType.INFO, 10, "root");
            LogMessage.ValidMessage largerMessage = new LogMessage.ValidMessage(MessageType.WARNING, 15, "larger");
            MessageTree initialTree = MessageTree.node(MessageTree.leaf(), rootMessage, MessageTree.leaf());

            // When
            MessageTree result = exercise.insert(largerMessage, initialTree);

            // Then
            assertThat(result).isInstanceOf(MessageTree.Node.class);
            MessageTree.Node node = (MessageTree.Node) result;
            assertThat(node.message()).isEqualTo(rootMessage);
            assertThat(node.left()).isInstanceOf(MessageTree.Leaf.class);
            assertThat(node.right()).isInstanceOf(MessageTree.Node.class);

            MessageTree.Node rightNode = (MessageTree.Node) node.right();
            assertThat(rightNode.message()).isEqualTo(largerMessage);
        }
    }

    @Nested
    @DisplayName("Complex Tree Building")
    class ComplexTreeTests {

        @Test
        @DisplayName("Should build balanced tree with multiple inserts")
        void shouldBuildTreeWithMultipleInserts() {
            // Given
            List<LogMessage.ValidMessage> testMessages = List.of(
                new LogMessage.ValidMessage(MessageType.INFO, 10, "root"),
                new LogMessage.ValidMessage(MessageType.WARNING, 5, "left"),
                new LogMessage.ValidMessage(MessageType.error(2), 15, "right"),
                new LogMessage.ValidMessage(MessageType.INFO, 3, "left-left"),
                new LogMessage.ValidMessage(MessageType.WARNING, 7, "left-right"),
                new LogMessage.ValidMessage(MessageType.INFO, 12, "right-left"),
                new LogMessage.ValidMessage(MessageType.error(1), 18, "right-right")
            );
            MessageTree initialTree = MessageTree.leaf();

            // When
            MessageTree tree = initialTree;
            for (LogMessage.ValidMessage msg : testMessages) {
                tree = exercise.insert(msg, tree);
            }

            // Then
            assertThat(tree.size()).isEqualTo(7);
            assertThat(tree.isEmpty()).isFalse();
            assertThat(isBSTOrdered(tree)).isTrue();
        }

        @Test
        @DisplayName("Should handle duplicate timestamps correctly")
        void shouldHandleDuplicateTimestamps() {
            // Given
            LogMessage.ValidMessage firstMessage = new LogMessage.ValidMessage(MessageType.INFO, 10, "first");
            LogMessage.ValidMessage secondMessage = new LogMessage.ValidMessage(MessageType.WARNING, 10, "second");
            MessageTree emptyTree = MessageTree.leaf();

            // When
            MessageTree tree = emptyTree;
            tree = exercise.insert(firstMessage, tree);
            tree = exercise.insert(secondMessage, tree);

            // Then
            assertThat(tree.size()).isEqualTo(2);
            // Both messages should be in the tree (duplicate timestamps allowed)
        }

        @ParameterizedTest
        @DisplayName("Should maintain BST property with various insertion orders")
        @MethodSource("insertionOrderTestCases")
        void shouldMaintainBSTPropertyWithVariousOrders(List<Integer> timestamps) {
            // Given
            MessageTree initialTree = MessageTree.leaf();
            List<LogMessage.ValidMessage> testMessages = timestamps.stream()
                .map(timestamp -> new LogMessage.ValidMessage(MessageType.INFO, timestamp, "msg-" + timestamp))
                .toList();

            // When
            MessageTree tree = initialTree;
            for (LogMessage.ValidMessage msg : testMessages) {
                tree = exercise.insert(msg, tree);
            }

            // Then
            assertThat(tree.size()).isEqualTo(timestamps.size());
            assertThat(isBSTOrdered(tree)).isTrue();
        }

        static Stream<Arguments> insertionOrderTestCases() {
            return Stream.of(
                Arguments.of(List.of(1, 2, 3, 4, 5)), // Ascending order
                Arguments.of(List.of(5, 4, 3, 2, 1)), // Descending order
                Arguments.of(List.of(3, 1, 4, 2, 5)), // Mixed order
                Arguments.of(List.of(10, 5, 15, 3, 7, 12, 18)), // Balanced insertion
                Arguments.of(List.of(1, 3, 2, 5, 4, 7, 6)) // Random order
            );
        }
    }

    @Nested
    @DisplayName("Error Handling and Edge Cases")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should handle null message gracefully")
        void shouldHandleNullMessage() {
            MessageTree tree = MessageTree.leaf();

            assertThatThrownBy(() -> exercise.insert(null, tree))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("Should handle null tree gracefully")
        void shouldHandleNullTree() {
            LogMessage.ValidMessage msg = new LogMessage.ValidMessage(MessageType.INFO, 10, "test");

            assertThatThrownBy(() -> exercise.insert(msg, null))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("Should handle mixed valid and unknown messages")
        void shouldHandleMixedValidAndUnknownMessages() {
            MessageTree tree = MessageTree.leaf();

            // Insert valid message
            LogMessage.ValidMessage validMsg = new LogMessage.ValidMessage(MessageType.INFO, 10, "valid");
            tree = exercise.insert(validMsg, tree);
            assertThat(tree.size()).isEqualTo(1);

            // Try to insert unknown message
            LogMessage.Unknown unknownMsg = new LogMessage.Unknown("unknown");
            tree = exercise.insert(unknownMsg, tree);
            assertThat(tree.size()).isEqualTo(1); // Should remain unchanged

            // Insert another valid message
            LogMessage.ValidMessage validMsg2 = new LogMessage.ValidMessage(MessageType.WARNING, 5, "valid2");
            tree = exercise.insert(validMsg2, tree);
            assertThat(tree.size()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("Tree Properties Validation")
    class TreePropertiesTests {

        @Test
        @DisplayName("Should maintain correct tree size after multiple operations")
        void shouldMaintainCorrectTreeSize() {
            MessageTree tree = MessageTree.leaf();
            assertThat(tree.size()).isEqualTo(0);

            // Insert 5 valid messages
            for (int i = 1; i <= 5; i++) {
                LogMessage.ValidMessage msg = new LogMessage.ValidMessage(MessageType.INFO, i * 10, "msg" + i);
                tree = exercise.insert(msg, tree);
                assertThat(tree.size()).isEqualTo(i);
            }

            // Try to insert 3 unknown messages (should not change size)
            for (int i = 0; i < 3; i++) {
                LogMessage.Unknown unknown = new LogMessage.Unknown("unknown" + i);
                tree = exercise.insert(unknown, tree);
                assertThat(tree.size()).isEqualTo(5); // Should remain 5
            }
        }

        @Test
        @DisplayName("Should correctly identify empty and non-empty trees")
        void shouldCorrectlyIdentifyEmptyTrees() {
            MessageTree emptyTree = MessageTree.leaf();
            assertThat(emptyTree.isEmpty()).isTrue();

            LogMessage.ValidMessage msg = new LogMessage.ValidMessage(MessageType.INFO, 10, "test");
            MessageTree nonEmptyTree = exercise.insert(msg, emptyTree);
            assertThat(nonEmptyTree.isEmpty()).isFalse();
        }
    }

    /**
     * Helper method to verify BST ordering property
     */
    private boolean isBSTOrdered(MessageTree tree) {
        return isBSTOrderedHelper(tree, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    private boolean isBSTOrderedHelper(MessageTree tree, int min, int max) {
        return switch (tree) {
            case MessageTree.Leaf leaf -> true;
            case MessageTree.Node node -> {
                int timestamp = node.timestamp();
                yield timestamp > min && timestamp < max
                    && isBSTOrderedHelper(node.left(), min, timestamp)
                    && isBSTOrderedHelper(node.right(), timestamp, max);
            }
        };
    }
}
