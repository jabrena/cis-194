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
            LogMessage.ValidMessage msg = new LogMessage.ValidMessage(MessageType.INFO, 10, "test");
            MessageTree tree = MessageTree.leaf();

            MessageTree result = exercise.insert(msg, tree);

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
            LogMessage.Unknown unknown = new LogMessage.Unknown("invalid message");
            MessageTree tree = MessageTree.leaf();

            MessageTree result = exercise.insert(unknown, tree);

            assertThat(result).isSameAs(tree);
            assertThat(result.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("Should maintain BST property when inserting smaller timestamp")
        void shouldInsertSmallerTimestampToLeft() {
            LogMessage.ValidMessage root = new LogMessage.ValidMessage(MessageType.INFO, 10, "root");
            LogMessage.ValidMessage smaller = new LogMessage.ValidMessage(MessageType.WARNING, 5, "smaller");

            MessageTree tree = MessageTree.node(MessageTree.leaf(), root, MessageTree.leaf());
            MessageTree result = exercise.insert(smaller, tree);

            assertThat(result).isInstanceOf(MessageTree.Node.class);
            MessageTree.Node node = (MessageTree.Node) result;
            assertThat(node.message()).isEqualTo(root);
            assertThat(node.left()).isInstanceOf(MessageTree.Node.class);
            assertThat(node.right()).isInstanceOf(MessageTree.Leaf.class);

            MessageTree.Node leftNode = (MessageTree.Node) node.left();
            assertThat(leftNode.message()).isEqualTo(smaller);
        }

        @Test
        @DisplayName("Should maintain BST property when inserting larger timestamp")
        void shouldInsertLargerTimestampToRight() {
            LogMessage.ValidMessage root = new LogMessage.ValidMessage(MessageType.INFO, 10, "root");
            LogMessage.ValidMessage larger = new LogMessage.ValidMessage(MessageType.WARNING, 15, "larger");

            MessageTree tree = MessageTree.node(MessageTree.leaf(), root, MessageTree.leaf());
            MessageTree result = exercise.insert(larger, tree);

            assertThat(result).isInstanceOf(MessageTree.Node.class);
            MessageTree.Node node = (MessageTree.Node) result;
            assertThat(node.message()).isEqualTo(root);
            assertThat(node.left()).isInstanceOf(MessageTree.Leaf.class);
            assertThat(node.right()).isInstanceOf(MessageTree.Node.class);

            MessageTree.Node rightNode = (MessageTree.Node) node.right();
            assertThat(rightNode.message()).isEqualTo(larger);
        }
    }

    @Nested
    @DisplayName("Complex Tree Building")
    class ComplexTreeTests {

        @Test
        @DisplayName("Should build balanced tree with multiple inserts")
        void shouldBuildTreeWithMultipleInserts() {
            List<LogMessage.ValidMessage> messages = List.of(
                new LogMessage.ValidMessage(MessageType.INFO, 10, "root"),
                new LogMessage.ValidMessage(MessageType.WARNING, 5, "left"),
                new LogMessage.ValidMessage(MessageType.error(2), 15, "right"),
                new LogMessage.ValidMessage(MessageType.INFO, 3, "left-left"),
                new LogMessage.ValidMessage(MessageType.WARNING, 7, "left-right"),
                new LogMessage.ValidMessage(MessageType.INFO, 12, "right-left"),
                new LogMessage.ValidMessage(MessageType.error(1), 18, "right-right")
            );

            MessageTree tree = MessageTree.leaf();
            for (LogMessage.ValidMessage msg : messages) {
                tree = exercise.insert(msg, tree);
            }

            assertThat(tree.size()).isEqualTo(7);
            assertThat(tree.isEmpty()).isFalse();

            // Verify BST property
            assertThat(isBSTOrdered(tree)).isTrue();
        }

        @Test
        @DisplayName("Should handle duplicate timestamps correctly")
        void shouldHandleDuplicateTimestamps() {
            LogMessage.ValidMessage msg1 = new LogMessage.ValidMessage(MessageType.INFO, 10, "first");
            LogMessage.ValidMessage msg2 = new LogMessage.ValidMessage(MessageType.WARNING, 10, "second");

            MessageTree tree = MessageTree.leaf();
            tree = exercise.insert(msg1, tree);
            tree = exercise.insert(msg2, tree);

            assertThat(tree.size()).isEqualTo(2);
            // Both messages should be in the tree (duplicate timestamps allowed)
        }

        @ParameterizedTest
        @DisplayName("Should maintain BST property with various insertion orders")
        @MethodSource("insertionOrderTestCases")
        void shouldMaintainBSTPropertyWithVariousOrders(List<Integer> timestamps) {
            MessageTree tree = MessageTree.leaf();

            for (int timestamp : timestamps) {
                LogMessage.ValidMessage msg = new LogMessage.ValidMessage(
                    MessageType.INFO, timestamp, "msg-" + timestamp);
                tree = exercise.insert(msg, tree);
            }

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
