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
 * Test class for Exercise 3 and 4 - MessageTree Build and InOrder Functions
 * Based on CIS-194 Homework 2
 *
 * This test suite covers:
 * - Exercise 3: build function (builds MessageTree from list of messages)
 * - Exercise 4: inOrder function (in-order traversal of MessageTree)
 * - Integration between build and inOrder for sorting messages
 */
@DisplayName("Exercise 3 & 4: MessageTree Build and InOrder Tests")
class Exercise3And4Test {

    private Exercise3And4 exercise;

    @BeforeEach
    void setUp() {
        exercise = new Exercise3And4();
    }

    @Nested
    @DisplayName("Build Function Tests")
    class BuildFunctionTests {

        @Test
        @DisplayName("Should build empty tree from empty list")
        void shouldBuildEmptyTreeFromEmptyList() {
            List<LogMessage> emptyMessages = List.of();

            MessageTree result = exercise.build(emptyMessages);

            assertThat(result).isInstanceOf(MessageTree.Leaf.class);
            assertThat(result.isEmpty()).isTrue();
            assertThat(result.size()).isEqualTo(0);
        }

        @Test
        @DisplayName("Should build single node tree from single valid message")
        void shouldBuildSingleNodeFromSingleMessage() {
            LogMessage.ValidMessage msg = new LogMessage.ValidMessage(MessageType.INFO, 10, "test");
            List<LogMessage> messages = List.of(msg);

            MessageTree result = exercise.build(messages);

            assertThat(result).isInstanceOf(MessageTree.Node.class);
            MessageTree.Node node = (MessageTree.Node) result;
            assertThat(node.message()).isEqualTo(msg);
            assertThat(result.size()).isEqualTo(1);
        }

        @Test
        @DisplayName("Should ignore unknown messages when building tree")
        void shouldIgnoreUnknownMessagesWhenBuilding() {
            List<LogMessage> messages = List.of(
                new LogMessage.ValidMessage(MessageType.INFO, 10, "valid1"),
                new LogMessage.Unknown("invalid message"),
                new LogMessage.ValidMessage(MessageType.WARNING, 5, "valid2"),
                new LogMessage.Unknown("another invalid"),
                new LogMessage.ValidMessage(MessageType.error(2), 15, "valid3")
            );

            MessageTree result = exercise.build(messages);

            assertThat(result.size()).isEqualTo(3); // Only valid messages
            assertThat(result.isEmpty()).isFalse();
        }

        @Test
        @DisplayName("Should build balanced tree from multiple messages")
        void shouldBuildBalancedTreeFromMultipleMessages() {
            List<LogMessage> messages = List.of(
                new LogMessage.ValidMessage(MessageType.INFO, 10, "root"),
                new LogMessage.ValidMessage(MessageType.WARNING, 5, "left"),
                new LogMessage.ValidMessage(MessageType.error(2), 15, "right"),
                new LogMessage.ValidMessage(MessageType.INFO, 3, "left-left"),
                new LogMessage.ValidMessage(MessageType.WARNING, 7, "left-right")
            );

            MessageTree result = exercise.build(messages);

            assertThat(result.size()).isEqualTo(5);
            assertThat(result.height()).isGreaterThan(1);
        }

        @ParameterizedTest
        @DisplayName("Should build tree correctly regardless of insertion order")
        @MethodSource("buildOrderTestCases")
        void shouldBuildTreeCorrectlyRegardlessOfOrder(List<Integer> timestamps) {
            List<LogMessage> messages = timestamps.stream()
                .map(ts -> new LogMessage.ValidMessage(MessageType.INFO, ts, "msg-" + ts))
                .map(msg -> (LogMessage) msg)
                .toList();

            MessageTree result = exercise.build(messages);

            assertThat(result.size()).isEqualTo(timestamps.size());
            // Verify BST property is maintained
            assertThat(isBSTOrdered(result)).isTrue();
        }

        static Stream<Arguments> buildOrderTestCases() {
            return Stream.of(
                Arguments.of(List.of(1, 2, 3, 4, 5)), // Ascending
                Arguments.of(List.of(5, 4, 3, 2, 1)), // Descending
                Arguments.of(List.of(3, 1, 4, 2, 5)), // Mixed
                Arguments.of(List.of(10, 5, 15, 3, 7, 12, 18)) // Balanced
            );
        }
    }

    @Nested
    @DisplayName("InOrder Function Tests")
    class InOrderFunctionTests {

        @Test
        @DisplayName("Should return empty list for empty tree")
        void shouldReturnEmptyListForEmptyTree() {
            MessageTree emptyTree = MessageTree.leaf();

            List<LogMessage.ValidMessage> result = exercise.inOrder(emptyTree);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return single message for single node tree")
        void shouldReturnSingleMessageForSingleNode() {
            LogMessage.ValidMessage msg = new LogMessage.ValidMessage(MessageType.INFO, 10, "test");
            MessageTree tree = MessageTree.node(MessageTree.leaf(), msg, MessageTree.leaf());

            List<LogMessage.ValidMessage> result = exercise.inOrder(tree);

            assertThat(result).hasSize(1);
            assertThat(result.get(0)).isEqualTo(msg);
        }

        @Test
        @DisplayName("Should return messages in timestamp order")
        void shouldReturnMessagesInTimestampOrder() {
            // Build tree manually to ensure specific structure
            LogMessage.ValidMessage msg1 = new LogMessage.ValidMessage(MessageType.INFO, 5, "first");
            LogMessage.ValidMessage msg2 = new LogMessage.ValidMessage(MessageType.WARNING, 10, "second");
            LogMessage.ValidMessage msg3 = new LogMessage.ValidMessage(MessageType.error(1), 15, "third");

            MessageTree tree = MessageTree.node(
                MessageTree.node(MessageTree.leaf(), msg1, MessageTree.leaf()),
                msg2,
                MessageTree.node(MessageTree.leaf(), msg3, MessageTree.leaf())
            );

            List<LogMessage.ValidMessage> result = exercise.inOrder(tree);

            assertThat(result).hasSize(3);
            assertThat(result.get(0)).isEqualTo(msg1);
            assertThat(result.get(1)).isEqualTo(msg2);
            assertThat(result.get(2)).isEqualTo(msg3);

            // Verify timestamps are in ascending order
            assertThat(result.stream().mapToInt(LogMessage.ValidMessage::timestamp))
                .isSorted();
        }

        @ParameterizedTest
        @DisplayName("Should maintain sorted order for various tree structures")
        @MethodSource("inOrderTestCases")
        void shouldMaintainSortedOrderForVariousStructures(List<Integer> timestamps) {
            // Build tree from messages
            List<LogMessage> messages = timestamps.stream()
                .map(ts -> new LogMessage.ValidMessage(MessageType.INFO, ts, "msg-" + ts))
                .map(msg -> (LogMessage) msg)
                .toList();

            MessageTree tree = exercise.build(messages);

            List<LogMessage.ValidMessage> result = exercise.inOrder(tree);

            assertThat(result).hasSize(timestamps.size());

            // Verify all messages are present and in sorted order
            List<Integer> resultTimestamps = result.stream()
                .mapToInt(LogMessage.ValidMessage::timestamp)
                .boxed()
                .toList();

            List<Integer> expectedSorted = timestamps.stream().sorted().toList();
            assertThat(resultTimestamps).isEqualTo(expectedSorted);
        }

        static Stream<Arguments> inOrderTestCases() {
            return Stream.of(
                Arguments.of(List.of(10, 5, 15, 3, 7, 12, 18)),
                Arguments.of(List.of(1, 2, 3, 4, 5)),
                Arguments.of(List.of(5, 4, 3, 2, 1)),
                Arguments.of(List.of(3, 1, 4, 2, 5, 6)),
                Arguments.of(List.of(100, 50, 150, 25, 75, 125, 175))
            );
        }
    }

    @Nested
    @DisplayName("Build and InOrder Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should sort messages using build and inOrder combination")
        void shouldSortMessagesUsingBuildAndInOrder() {
            List<LogMessage> unsortedMessages = List.of(
                new LogMessage.ValidMessage(MessageType.INFO, 6, "Completed armadillo processing"),
                new LogMessage.ValidMessage(MessageType.INFO, 1, "Nothing to report"),
                new LogMessage.ValidMessage(MessageType.error(99), 10, "Flange failed!"),
                new LogMessage.ValidMessage(MessageType.INFO, 4, "Everything normal"),
                new LogMessage.ValidMessage(MessageType.WARNING, 5, "Flange is due for a check-up"),
                new LogMessage.Unknown("This is not in the right format"),
                new LogMessage.ValidMessage(MessageType.error(70), 3, "Way too many pickles")
            );

            MessageTree tree = exercise.build(unsortedMessages);
            List<LogMessage.ValidMessage> sortedMessages = exercise.inOrder(tree);

            // Should have 6 valid messages (excluding the Unknown one)
            assertThat(sortedMessages).hasSize(6);

            // Should be sorted by timestamp
            List<Integer> timestamps = sortedMessages.stream()
                .mapToInt(LogMessage.ValidMessage::timestamp)
                .boxed()
                .toList();
            assertThat(timestamps).isEqualTo(List.of(1, 3, 4, 5, 6, 10));

            // Verify specific messages are in correct order
            assertThat(sortedMessages.get(0).message()).isEqualTo("Nothing to report");
            assertThat(sortedMessages.get(1).message()).isEqualTo("Way too many pickles");
            assertThat(sortedMessages.get(5).message()).isEqualTo("Flange failed!");
        }

        @Test
        @DisplayName("Should handle duplicate timestamps correctly")
        void shouldHandleDuplicateTimestampsCorrectly() {
            List<LogMessage> messages = List.of(
                new LogMessage.ValidMessage(MessageType.INFO, 10, "first"),
                new LogMessage.ValidMessage(MessageType.WARNING, 10, "second"),
                new LogMessage.ValidMessage(MessageType.error(1), 10, "third"),
                new LogMessage.ValidMessage(MessageType.INFO, 5, "earlier")
            );

            MessageTree tree = exercise.build(messages);
            List<LogMessage.ValidMessage> result = exercise.inOrder(tree);

            assertThat(result).hasSize(4);
            assertThat(result.get(0).timestamp()).isEqualTo(5);
            // The three messages with timestamp 10 should all be present
            assertThat(result.stream().skip(1).mapToInt(LogMessage.ValidMessage::timestamp))
                .allMatch(ts -> ts == 10);
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
