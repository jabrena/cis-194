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
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

/**
 * Test class for Exercise 5 - whatWentWrong Function
 * Based on CIS-194 Homework 2
 *
 * This test suite covers the whatWentWrong function which:
 * - Takes an unsorted list of LogMessages
 * - Filters for errors with severity >= 50
 * - Returns error messages sorted by timestamp
 */
@DisplayName("Exercise 5: whatWentWrong Function Tests")
class Exercise5Test {

    private Exercise5 exercise;

    @BeforeEach
    void setUp() {
        exercise = new Exercise5();
    }

    @Nested
    @DisplayName("Basic Functionality Tests")
    class BasicFunctionalityTests {

        @Test
        @DisplayName("Should return empty list when no messages provided")
        void shouldReturnEmptyListWhenNoMessages() {
            List<LogMessage> emptyMessages = List.of();

            List<String> result = exercise.whatWentWrong(emptyMessages);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty list when no high-severity errors")
        void shouldReturnEmptyListWhenNoHighSeverityErrors() {
            List<LogMessage> messages = List.of(
                new LogMessage.ValidMessage(MessageType.INFO, 1, "Info message"),
                new LogMessage.ValidMessage(MessageType.WARNING, 2, "Warning message"),
                new LogMessage.ValidMessage(MessageType.error(10), 3, "Low severity error"),
                new LogMessage.ValidMessage(MessageType.error(49), 4, "Below threshold error")
            );

            List<String> result = exercise.whatWentWrong(messages);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return high-severity error messages sorted by timestamp")
        void shouldReturnHighSeverityErrorsSorted() {
            List<LogMessage> messages = List.of(
                new LogMessage.ValidMessage(MessageType.INFO, 6, "Completed armadillo processing"),
                new LogMessage.ValidMessage(MessageType.INFO, 1, "Nothing to report"),
                new LogMessage.ValidMessage(MessageType.error(99), 10, "Flange failed!"),
                new LogMessage.ValidMessage(MessageType.INFO, 4, "Everything normal"),
                new LogMessage.ValidMessage(MessageType.INFO, 11, "Initiating self-destruct sequence"),
                new LogMessage.ValidMessage(MessageType.error(70), 3, "Way too many pickles"),
                new LogMessage.ValidMessage(MessageType.error(65), 8, "Bad pickle-flange interaction detected"),
                new LogMessage.ValidMessage(MessageType.WARNING, 5, "Flange is due for a check-up"),
                new LogMessage.ValidMessage(MessageType.INFO, 7, "Out for lunch, back in two time steps"),
                new LogMessage.ValidMessage(MessageType.error(20), 2, "Too many pickles"),
                new LogMessage.ValidMessage(MessageType.INFO, 9, "Back from lunch")
            );

            List<String> result = exercise.whatWentWrong(messages);

            assertThat(result).hasSize(3);
            assertThat(result).containsExactly(
                "Way too many pickles",
                "Bad pickle-flange interaction detected",
                "Flange failed!"
            );
        }

        @Test
        @DisplayName("Should ignore unknown messages")
        void shouldIgnoreUnknownMessages() {
            List<LogMessage> messages = List.of(
                new LogMessage.ValidMessage(MessageType.error(99), 10, "Critical error"),
                new LogMessage.Unknown("This is not in the right format"),
                new LogMessage.ValidMessage(MessageType.error(75), 5, "Severe error"),
                new LogMessage.Unknown("Another malformed message")
            );

            List<String> result = exercise.whatWentWrong(messages);

            assertThat(result).hasSize(2);
            assertThat(result).containsExactly("Severe error", "Critical error");
        }
    }

    @Nested
    @DisplayName("Severity Threshold Tests")
    class SeverityThresholdTests {

        @ParameterizedTest
        @DisplayName("Should include errors with severity exactly at threshold")
        @ValueSource(ints = {50, 51, 60, 75, 90, 99, 100})
        void shouldIncludeErrorsAtOrAboveThreshold(int severity) {
            List<LogMessage> messages = List.of(
                new LogMessage.ValidMessage(MessageType.error(severity), 1, "Error at severity " + severity)
            );

            List<String> result = exercise.whatWentWrong(messages);

            assertThat(result).hasSize(1);
            assertThat(result.get(0)).isEqualTo("Error at severity " + severity);
        }

        @ParameterizedTest
        @DisplayName("Should exclude errors with severity below threshold")
        @ValueSource(ints = {1, 10, 25, 40, 49})
        void shouldExcludeErrorsBelowThreshold(int severity) {
            List<LogMessage> messages = List.of(
                new LogMessage.ValidMessage(MessageType.error(severity), 1, "Error at severity " + severity)
            );

            List<String> result = exercise.whatWentWrong(messages);

            assertThat(result).isEmpty();
        }

        @ParameterizedTest
        @DisplayName("Should handle mixed severity levels correctly")
        @MethodSource("mixedSeverityTestCases")
        void shouldHandleMixedSeverityLevels(List<Integer> severities, List<String> expectedMessages) {
            List<LogMessage> messages = severities.stream()
                .map(severity -> new LogMessage.ValidMessage(
                    MessageType.error(severity),
                    severity, // Use severity as timestamp for predictable ordering
                    "Error " + severity))
                .map(msg -> (LogMessage) msg)
                .toList();

            List<String> result = exercise.whatWentWrong(messages);

            assertThat(result).isEqualTo(expectedMessages);
        }

        static Stream<Arguments> mixedSeverityTestCases() {
            return Stream.of(
                Arguments.of(
                    List.of(10, 50, 75, 30, 60, 45, 90),
                    List.of("Error 50", "Error 60", "Error 75", "Error 90")
                ),
                Arguments.of(
                    List.of(100, 25, 80, 15, 55),
                    List.of("Error 55", "Error 80", "Error 100")
                ),
                Arguments.of(
                    List.of(1, 2, 3, 49), // All below threshold
                    List.of()
                )
            );
        }
    }

    @Nested
    @DisplayName("Sorting and Ordering Tests")
    class SortingTests {

        @Test
        @DisplayName("Should sort error messages by timestamp in ascending order")
        void shouldSortErrorMessagesByTimestamp() {
            List<LogMessage> messages = List.of(
                new LogMessage.ValidMessage(MessageType.error(80), 15, "Third error"),
                new LogMessage.ValidMessage(MessageType.error(60), 5, "First error"),
                new LogMessage.ValidMessage(MessageType.error(90), 25, "Fourth error"),
                new LogMessage.ValidMessage(MessageType.error(70), 10, "Second error")
            );

            List<String> result = exercise.whatWentWrong(messages);

            assertThat(result).containsExactly(
                "First error",
                "Second error",
                "Third error",
                "Fourth error"
            );
        }

        @Test
        @DisplayName("Should handle duplicate timestamps correctly")
        void shouldHandleDuplicateTimestamps() {
            List<LogMessage> messages = List.of(
                new LogMessage.ValidMessage(MessageType.error(75), 10, "Error A"),
                new LogMessage.ValidMessage(MessageType.error(85), 10, "Error B"),
                new LogMessage.ValidMessage(MessageType.error(95), 10, "Error C"),
                new LogMessage.ValidMessage(MessageType.error(65), 5, "Earlier error")
            );

            List<String> result = exercise.whatWentWrong(messages);

            assertThat(result).hasSize(4);
            assertThat(result.get(0)).isEqualTo("Earlier error");
            // The three errors at timestamp 10 should all be present
            assertThat(result.subList(1, 4)).containsExactlyInAnyOrder("Error A", "Error B", "Error C");
        }
    }

    @Nested
    @DisplayName("Message Type Filtering Tests")
    class MessageTypeFilteringTests {

        @Test
        @DisplayName("Should only include error messages, not info or warnings")
        void shouldOnlyIncludeErrorMessages() {
            List<LogMessage> messages = List.of(
                new LogMessage.ValidMessage(MessageType.INFO, 1, "Info message"),
                new LogMessage.ValidMessage(MessageType.WARNING, 2, "Warning message"),
                new LogMessage.ValidMessage(MessageType.error(75), 3, "Error message"),
                new LogMessage.ValidMessage(MessageType.INFO, 4, "Another info"),
                new LogMessage.ValidMessage(MessageType.error(60), 5, "Another error")
            );

            List<String> result = exercise.whatWentWrong(messages);

            assertThat(result).hasSize(2);
            assertThat(result).containsExactly("Error message", "Another error");
        }

        @Test
        @DisplayName("Should work with sample log file format")
        void shouldWorkWithSampleLogFileFormat() {
            // This matches the example from the homework PDF
            List<LogMessage> messages = List.of(
                new LogMessage.ValidMessage(MessageType.INFO, 6, "Completed armadillo processing"),
                new LogMessage.ValidMessage(MessageType.INFO, 1, "Nothing to report"),
                new LogMessage.ValidMessage(MessageType.error(99), 10, "Flange failed!"),
                new LogMessage.ValidMessage(MessageType.INFO, 4, "Everything normal"),
                new LogMessage.ValidMessage(MessageType.INFO, 11, "Initiating self-destruct sequence"),
                new LogMessage.ValidMessage(MessageType.error(70), 3, "Way too many pickles"),
                new LogMessage.ValidMessage(MessageType.error(65), 8, "Bad pickle-flange interaction detected"),
                new LogMessage.ValidMessage(MessageType.WARNING, 5, "Flange is due for a check-up"),
                new LogMessage.ValidMessage(MessageType.INFO, 7, "Out for lunch, back in two time steps"),
                new LogMessage.ValidMessage(MessageType.error(20), 2, "Too many pickles"),
                new LogMessage.ValidMessage(MessageType.INFO, 9, "Back from lunch")
            );

            List<String> result = exercise.whatWentWrong(messages);

            // Expected result from homework PDF
            assertThat(result).containsExactly(
                "Way too many pickles",
                "Bad pickle-flange interaction detected",
                "Flange failed!"
            );
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Handling")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle null input gracefully")
        void shouldHandleNullInputGracefully() {
            assertThatThrownBy(() -> exercise.whatWentWrong(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null");
        }

        @Test
        @DisplayName("Should handle large lists efficiently")
        void shouldHandleLargeListsEfficiently() {
            // Create a large list with some high-severity errors
            List<LogMessage> messages = Stream.concat(
                // 1000 low-severity errors and other messages
                Stream.iterate(1, i -> i + 1)
                    .limit(1000)
                    .map(i -> new LogMessage.ValidMessage(MessageType.error((i % 49) + 1), i, "Error " + i)), // Ensure severity is 1-49
                // A few high-severity errors
                Stream.of(
                    new LogMessage.ValidMessage(MessageType.error(75), 1001, "Critical error 1"),
                    new LogMessage.ValidMessage(MessageType.error(85), 1002, "Critical error 2")
                )
            ).map(msg -> (LogMessage) msg).toList();

            List<String> result = exercise.whatWentWrong(messages);

            assertThat(result).hasSize(2);
            assertThat(result).containsExactly("Critical error 1", "Critical error 2");
        }
    }
}
