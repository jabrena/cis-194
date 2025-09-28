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
 * Test class for Exercise 4 - MessageTree InOrder Function
 * Based on CIS-194 Homework 2
 *
 * This test suite covers:
 * - Exercise 4: inOrder function (in-order traversal of MessageTree)
 * - Tree traversal with various tree structures
 * - Timestamp ordering verification
 * - Edge cases and null handling
 */
@DisplayName("Exercise 4: MessageTree InOrder Function Tests")
class Exercise4Test {

    private Exercise4 exercise;
    private Exercise3 buildHelper; // Helper to build trees for testing

    @BeforeEach
    void setUp() {
        exercise = new Exercise4();
        buildHelper = new Exercise3();
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
            // Build tree from messages using Exercise3
            List<LogMessage> messages = timestamps.stream()
                .map(ts -> new LogMessage.ValidMessage(MessageType.INFO, ts, "msg-" + ts))
                .map(msg -> (LogMessage) msg)
                .toList();

            MessageTree tree = buildHelper.build(messages);

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
    @DisplayName("InOrder Utilities and Edge Cases")
    class InOrderUtilitiesTests {

        @Test
        @DisplayName("Should verify tree is in sorted order")
        void shouldVerifyTreeIsInSortedOrder() {
            List<LogMessage> messages = List.of(
                new LogMessage.ValidMessage(MessageType.INFO, 10, "middle"),
                new LogMessage.ValidMessage(MessageType.WARNING, 5, "left"),
                new LogMessage.ValidMessage(MessageType.error(1), 15, "right")
            );

            MessageTree tree = buildHelper.build(messages);

            assertThat(exercise.isInOrderSorted(tree)).isTrue();
            assertThat(exercise.isInOrderSortedFunctional(tree)).isTrue();
        }

        @Test
        @DisplayName("Should count messages correctly")
        void shouldCountMessagesCorrectly() {
            List<LogMessage> messages = List.of(
                new LogMessage.ValidMessage(MessageType.INFO, 1, "msg1"),
                new LogMessage.ValidMessage(MessageType.WARNING, 2, "msg2"),
                new LogMessage.ValidMessage(MessageType.error(1), 3, "msg3")
            );

            MessageTree tree = buildHelper.build(messages);

            assertThat(exercise.countMessages(tree)).isEqualTo(3);
        }

        @Test
        @DisplayName("Should find messages in timestamp range")
        void shouldFindMessagesInTimestampRange() {
            List<LogMessage> messages = List.of(
                new LogMessage.ValidMessage(MessageType.INFO, 1, "msg1"),
                new LogMessage.ValidMessage(MessageType.WARNING, 5, "msg5"),
                new LogMessage.ValidMessage(MessageType.error(1), 10, "msg10"),
                new LogMessage.ValidMessage(MessageType.INFO, 15, "msg15"),
                new LogMessage.ValidMessage(MessageType.WARNING, 20, "msg20")
            );

            MessageTree tree = buildHelper.build(messages);

            List<LogMessage.ValidMessage> result = exercise.findMessagesInRange(tree, 5, 15);

            assertThat(result).hasSize(3);
            assertThat(result.stream().mapToInt(LogMessage.ValidMessage::timestamp))
                .containsExactly(5, 10, 15);
        }

        @Test
        @DisplayName("Should get first and last messages correctly")
        void shouldGetFirstAndLastMessagesCorrectly() {
            List<LogMessage> messages = List.of(
                new LogMessage.ValidMessage(MessageType.INFO, 10, "middle"),
                new LogMessage.ValidMessage(MessageType.WARNING, 5, "first"),
                new LogMessage.ValidMessage(MessageType.error(1), 15, "last")
            );

            MessageTree tree = buildHelper.build(messages);

            assertThat(exercise.getFirstMessage(tree))
                .isPresent()
                .get()
                .satisfies(msg -> {
                    assertThat(msg.timestamp()).isEqualTo(5);
                    assertThat(msg.message()).isEqualTo("first");
                });

            assertThat(exercise.getLastMessage(tree))
                .isPresent()
                .get()
                .satisfies(msg -> {
                    assertThat(msg.timestamp()).isEqualTo(15);
                    assertThat(msg.message()).isEqualTo("last");
                });
        }

        @Test
        @DisplayName("Should extract only error messages")
        void shouldExtractOnlyErrorMessages() {
            List<LogMessage> messages = List.of(
                new LogMessage.ValidMessage(MessageType.INFO, 1, "info"),
                new LogMessage.ValidMessage(MessageType.error(50), 2, "error1"),
                new LogMessage.ValidMessage(MessageType.WARNING, 3, "warning"),
                new LogMessage.ValidMessage(MessageType.error(75), 4, "error2")
            );

            MessageTree tree = buildHelper.build(messages);

            List<LogMessage.ValidMessage> errorMessages = exercise.getErrorMessages(tree);

            assertThat(errorMessages).hasSize(2);
            assertThat(errorMessages.stream().mapToInt(LogMessage.ValidMessage::timestamp))
                .containsExactly(2, 4);
            assertThat(errorMessages.get(0).message()).isEqualTo("error1");
            assertThat(errorMessages.get(1).message()).isEqualTo("error2");
        }

        @Test
        @DisplayName("Should handle null tree gracefully")
        void shouldHandleNullTreeGracefully() {
            assertThat(exercise.inOrder(null)).isEmpty();
            assertThat(exercise.inOrderStream(null)).isEmpty();
            assertThat(exercise.getFirstMessage(null)).isEmpty();
            assertThat(exercise.getLastMessage(null)).isEmpty();
        }
    }

    @Nested
    @DisplayName("Integration with Build Function")
    class IntegrationTests {

        @Test
        @DisplayName("Should sort messages using build and inOrder combination with sample.log data")
        void shouldSortMessagesUsingBuildAndInOrder() {
            // Given
            List<LogMessage> sampleMessages = SampleLogData.getParsedSampleMessages();

            // When
            MessageTree tree = buildHelper.build(sampleMessages);
            List<LogMessage.ValidMessage> sortedMessages = exercise.inOrder(tree);

            // Then
            assertThat(sortedMessages).hasSize(SampleLogData.SampleLogStats.TOTAL_MESSAGES);

            // Should be sorted by timestamp
            List<Integer> timestamps = sortedMessages.stream()
                .mapToInt(LogMessage.ValidMessage::timestamp)
                .boxed()
                .toList();
            assertThat(timestamps).isEqualTo(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11));

            // Verify specific messages are in correct order using sample data
            assertThat(sortedMessages.get(0)).isEqualTo(SampleLogData.SampleMessages.NOTHING_TO_REPORT);
            assertThat(sortedMessages.get(2)).isEqualTo(SampleLogData.SampleMessages.TOO_MANY_PICKLES_HIGH);
            assertThat(sortedMessages.get(9)).isEqualTo(SampleLogData.SampleMessages.FLANGE_FAILED);
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

            MessageTree tree = buildHelper.build(messages);
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
