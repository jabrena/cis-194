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
 * Test class for Exercise 3 - MessageTree Build Function
 * Based on CIS-194 Homework 2
 *
 * This test suite covers:
 * - Exercise 3: build function (builds MessageTree from list of messages)
 * - Building trees from various message combinations
 * - Handling unknown messages during tree construction
 * - BST property validation after building
 */
@DisplayName("Exercise 3: MessageTree Build Function Tests")
class Exercise3Test {

    private Exercise3 exercise;

    @BeforeEach
    void setUp() {
        exercise = new Exercise3();
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
    @DisplayName("Build Statistics and Utilities")
    class BuildUtilitiesTests {

        @Test
        @DisplayName("Should provide accurate build statistics")
        void shouldProvideAccurateBuildStatistics() {
            List<LogMessage> messages = List.of(
                new LogMessage.ValidMessage(MessageType.INFO, 1, "valid1"),
                new LogMessage.Unknown("invalid1"),
                new LogMessage.ValidMessage(MessageType.WARNING, 2, "valid2"),
                new LogMessage.Unknown("invalid2"),
                new LogMessage.ValidMessage(MessageType.error(1), 3, "valid3")
            );

            Exercise3.BuildResult result = exercise.buildWithStatistics(messages);

            assertThat(result.totalMessages()).isEqualTo(5);
            assertThat(result.validMessages()).isEqualTo(3);
            assertThat(result.unknownMessages()).isEqualTo(2);
            assertThat(result.tree().size()).isEqualTo(3);
        }

        @Test
        @DisplayName("Should extract only valid messages")
        void shouldExtractOnlyValidMessages() {
            List<LogMessage> messages = List.of(
                new LogMessage.ValidMessage(MessageType.INFO, 1, "valid1"),
                new LogMessage.Unknown("invalid1"),
                new LogMessage.ValidMessage(MessageType.WARNING, 2, "valid2")
            );

            List<LogMessage.ValidMessage> validMessages = exercise.extractValidMessages(messages);

            assertThat(validMessages).hasSize(2);
            assertThat(validMessages.get(0).message()).isEqualTo("valid1");
            assertThat(validMessages.get(1).message()).isEqualTo("valid2");
        }

        @Test
        @DisplayName("Should handle null input gracefully")
        void shouldHandleNullInputGracefully() {
            MessageTree result = exercise.build(null);
            assertThat(result).isInstanceOf(MessageTree.Leaf.class);

            long count = exercise.countValidMessages(null);
            assertThat(count).isEqualTo(0);

            boolean allValid = exercise.allMessagesValid(null);
            assertThat(allValid).isTrue(); // Vacuous truth for empty stream
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
