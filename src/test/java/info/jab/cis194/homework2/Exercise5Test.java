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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
    private Exercise1 parseHelper;

    @BeforeEach
    void setUp() {
        exercise = new Exercise5();
        parseHelper = new Exercise1();
    }

    @Nested
    @DisplayName("Basic Functionality Tests")
    class BasicFunctionalityTests {

        @Test
        @DisplayName("Should return empty list when no messages provided")
        void shouldReturnEmptyListWhenNoMessages() {
            // Given
            List<LogMessage> emptyMessageList = List.of();

            // When
            List<String> result = exercise.whatWentWrong(emptyMessageList);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty list when no high-severity errors")
        void shouldReturnEmptyListWhenNoHighSeverityErrors() {
            // Given
            List<LogMessage> lowSeverityMessages = List.of(
                new LogMessage.ValidMessage(MessageType.INFO, 1, "Info message"),
                new LogMessage.ValidMessage(MessageType.WARNING, 2, "Warning message"),
                new LogMessage.ValidMessage(MessageType.error(10), 3, "Low severity error"),
                new LogMessage.ValidMessage(MessageType.error(49), 4, "Below threshold error")
            );

            // When
            List<String> result = exercise.whatWentWrong(lowSeverityMessages);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return high-severity error messages sorted by timestamp using sample.log data")
        void shouldReturnHighSeverityErrorsSorted() {
            // Given
            List<LogMessage> sampleMessages = SampleLogData.getParsedSampleMessages();

            // When
            List<String> result = exercise.whatWentWrong(sampleMessages);

            // Then
            assertThat(result).hasSize(SampleLogData.SampleLogStats.HIGH_SEVERITY_ERRORS);
            assertThat(result).isEqualTo(SampleLogData.getExpectedHighSeverityErrors());
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
            // Given
            List<LogMessage> highSeverityMessages = List.of(
                new LogMessage.ValidMessage(MessageType.error(severity), 1, "Error at severity " + severity)
            );

            // When
            List<String> result = exercise.whatWentWrong(highSeverityMessages);

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0)).isEqualTo("Error at severity " + severity);
        }

        @ParameterizedTest
        @DisplayName("Should exclude errors with severity below threshold")
        @ValueSource(ints = {1, 10, 25, 40, 49})
        void shouldExcludeErrorsBelowThreshold(int severity) {
            // Given
            List<LogMessage> lowSeverityMessages = List.of(
                new LogMessage.ValidMessage(MessageType.error(severity), 1, "Error at severity " + severity)
            );

            // When
            List<String> result = exercise.whatWentWrong(lowSeverityMessages);

            // Then
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
        @DisplayName("Should work with official sample log file format")
        void shouldWorkWithSampleLogFileFormat() {
            // Given - Use the official sample.log data from homework
            List<LogMessage> sampleMessages = SampleLogData.getParsedSampleMessages();

            // When
            List<String> result = exercise.whatWentWrong(sampleMessages);

            // Then - Expected result from homework PDF specification
            assertThat(result).isEqualTo(SampleLogData.getExpectedHighSeverityErrors());
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

    @Nested
    @DisplayName("Sample Log File Integration Tests")
    class SampleLogFileTests {

        @Test
        @DisplayName("Should process sample.log file correctly according to homework specification")
        void should_processSampleLogFile_when_realHomeworkDataProvided() {
            // Given
            List<LogMessage> parsedMessages = SampleLogData.getParsedSampleMessages();

            // When
            List<String> result = exercise.whatWentWrong(parsedMessages);

            // Then - Expected result from homework PDF specification
            assertThat(result).isEqualTo(SampleLogData.getExpectedHighSeverityErrors());
        }

        @Test
        @DisplayName("Should parse sample.log file and verify message counts")
        void should_parseAndCountMessages_when_sampleLogFileProvided() {
            // Given
            List<LogMessage> parsedMessages = SampleLogData.getParsedSampleMessages();

            // When
            Exercise5.ErrorSummary summary = exercise.getErrorSummary(parsedMessages);

            // Then
            assertThat(summary.totalMessages()).isEqualTo(SampleLogData.SampleLogStats.TOTAL_MESSAGES);
            assertThat(summary.totalErrors()).isEqualTo(SampleLogData.SampleLogStats.ERROR_MESSAGES);
            assertThat(summary.highSeverityErrors()).isEqualTo(SampleLogData.SampleLogStats.HIGH_SEVERITY_ERRORS);
            assertThat(summary.highSeverityErrorMessages()).hasSize(SampleLogData.SampleLogStats.HIGH_SEVERITY_ERRORS);
        }

        @Test
        @DisplayName("Should verify sample.log parsing produces correct message types")
        void should_verifyMessageTypes_when_sampleLogFileParsed() {
            // Given
            List<LogMessage> parsedMessages = SampleLogData.getParsedSampleMessages();

            // When & Then
            long infoCount = parsedMessages.stream()
                .filter(msg -> msg instanceof LogMessage.ValidMessage vm &&
                              vm.messageType() == MessageType.INFO)
                .count();
            assertThat(infoCount).isEqualTo(SampleLogData.SampleLogStats.INFO_MESSAGES);

            long warningCount = parsedMessages.stream()
                .filter(msg -> msg instanceof LogMessage.ValidMessage vm &&
                              vm.messageType() == MessageType.WARNING)
                .count();
            assertThat(warningCount).isEqualTo(SampleLogData.SampleLogStats.WARNING_MESSAGES);

            long errorCount = parsedMessages.stream()
                .filter(msg -> msg instanceof LogMessage.ValidMessage vm &&
                              vm.messageType() instanceof MessageType.Error)
                .count();
            assertThat(errorCount).isEqualTo(SampleLogData.SampleLogStats.ERROR_MESSAGES);

            // Verify no unknown messages (all should parse correctly)
            long unknownCount = parsedMessages.stream()
                .filter(msg -> msg instanceof LogMessage.Unknown)
                .count();
            assertThat(unknownCount).isEqualTo(SampleLogData.SampleLogStats.UNKNOWN_MESSAGES);
        }

        @Test
        @DisplayName("Should verify sample.log error severities and timestamps")
        void should_verifyErrorDetails_when_sampleLogFileParsed() {
            // Given
            List<LogMessage.ValidMessage> errors = SampleLogData.getErrorSampleMessages();

            // Then - Verify specific error details from sample.log using predefined data
            assertThat(errors).hasSize(SampleLogData.SampleLogStats.ERROR_MESSAGES);

            // Verify all expected error messages are present
            assertThat(errors).contains(
                SampleLogData.SampleMessages.TOO_MANY_PICKLES_HIGH,
                SampleLogData.SampleMessages.PICKLE_FLANGE_INTERACTION,
                SampleLogData.SampleMessages.TOO_MANY_PICKLES_LOW,
                SampleLogData.SampleMessages.FLANGE_FAILED
            );

            // Verify high-severity errors (>= 50)
            List<LogMessage.ValidMessage> highSeverityErrors = errors.stream()
                .filter(msg -> ((MessageType.Error) msg.messageType()).severity() >= 50)
                .toList();
            assertThat(highSeverityErrors).hasSize(SampleLogData.SampleLogStats.HIGH_SEVERITY_ERRORS);
        }

        @Test
        @DisplayName("Should demonstrate complete homework pipeline with sample.log")
        void should_demonstrateCompletePipeline_when_usingSampleLogFile() {
            // Given - Use the official sample.log data
            List<LogMessage> parsedMessages = SampleLogData.getParsedSampleMessages();

            // When - Execute the complete pipeline as described in homework
            // Step 2: Build MessageTree (Exercise 3)
            Exercise3 buildOp = new Exercise3();
            MessageTree tree = buildOp.build(parsedMessages);

            // Step 3: Get sorted messages (Exercise 4)
            Exercise4 traversalOp = new Exercise4();
            List<LogMessage.ValidMessage> sortedMessages = traversalOp.inOrder(tree);

            // Step 4: Extract high-severity errors (Exercise 5)
            List<String> highSeverityErrors = exercise.whatWentWrong(parsedMessages);

            // Then - Verify the complete pipeline works as expected
            assertThat(parsedMessages).hasSize(SampleLogData.SampleLogStats.TOTAL_MESSAGES);
            assertThat(tree.size()).isEqualTo(SampleLogData.SampleLogStats.TOTAL_MESSAGES);
            assertThat(sortedMessages).hasSize(SampleLogData.SampleLogStats.TOTAL_MESSAGES);
            assertThat(sortedMessages.stream().mapToInt(LogMessage.ValidMessage::timestamp))
                .isSorted(); // Verify timestamp ordering

            // Verify the final result matches homework specification
            assertThat(highSeverityErrors).isEqualTo(SampleLogData.getExpectedHighSeverityErrors());
        }
    }
}
