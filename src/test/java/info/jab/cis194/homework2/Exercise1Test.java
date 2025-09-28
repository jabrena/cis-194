package info.jab.cis194.homework2;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

/**
 * Test class for Exercise 1 - Log Message Parsing
 * Based on CIS-194 Homework 2
 *
 * This test suite covers parsing individual log messages and complete log files:
 * - parseMessage function for single log line parsing
 * - parse function for complete log file parsing
 * - Support for Info, Warning, and Error message types
 * - Handling of Unknown/malformed messages
 */
@DisplayName("Exercise 1: Log Message Parsing Tests")
class Exercise1Test {

    private Exercise1 exercise;

    @BeforeEach
    void setUp() {
        exercise = new Exercise1();
    }

    @Nested
    @DisplayName("Parse Single Message Tests")
    class ParseMessageTests {

        @ParameterizedTest
        @DisplayName("Should parse Info messages correctly")
        @CsvSource({
            "'I 29 la la la', INFO, 29, 'la la la'",
            "'I 147 mice in the air, I''m afraid', INFO, 147, 'mice in the air, I''m afraid'",
            "'I 1 Nothing to report', INFO, 1, 'Nothing to report'",
            "'I 6 Completed armadillo processing', INFO, 6, 'Completed armadillo processing'"
        })
        void shouldParseInfoMessages(String input, String expectedType, int expectedTimestamp, String expectedMessage) {
            LogMessage result = exercise.parseMessage(input);

            assertThat(result).isInstanceOf(LogMessage.ValidMessage.class);
            LogMessage.ValidMessage validMsg = (LogMessage.ValidMessage) result;
            assertThat(validMsg.messageType()).isEqualTo(MessageType.INFO);
            assertThat(validMsg.timestamp()).isEqualTo(expectedTimestamp);
            assertThat(validMsg.message()).isEqualTo(expectedMessage);
        }

        @ParameterizedTest
        @DisplayName("Should parse Warning messages correctly")
        @CsvSource({
            "'W 5 Flange is due for a check-up', WARNING, 5, 'Flange is due for a check-up'",
            "'W 100 System overheating', WARNING, 100, 'System overheating'"
        })
        void shouldParseWarningMessages(String input, String expectedType, int expectedTimestamp, String expectedMessage) {
            LogMessage result = exercise.parseMessage(input);

            assertThat(result).isInstanceOf(LogMessage.ValidMessage.class);
            LogMessage.ValidMessage validMsg = (LogMessage.ValidMessage) result;
            assertThat(validMsg.messageType()).isEqualTo(MessageType.WARNING);
            assertThat(validMsg.timestamp()).isEqualTo(expectedTimestamp);
            assertThat(validMsg.message()).isEqualTo(expectedMessage);
        }

        @ParameterizedTest
        @DisplayName("Should parse Error messages correctly")
        @CsvSource({
            "'E 2 562 help help', 2, 562, 'help help'",
            "'E 99 10 Flange failed!', 99, 10, 'Flange failed!'",
            "'E 70 3 Way too many pickles', 70, 3, 'Way too many pickles'",
            "'E 65 8 Bad pickle-flange interaction detected', 65, 8, 'Bad pickle-flange interaction detected'"
        })
        void shouldParseErrorMessages(String input, int expectedSeverity, int expectedTimestamp, String expectedMessage) {
            LogMessage result = exercise.parseMessage(input);

            assertThat(result).isInstanceOf(LogMessage.ValidMessage.class);
            LogMessage.ValidMessage validMsg = (LogMessage.ValidMessage) result;
            assertThat(validMsg.messageType()).isInstanceOf(MessageType.Error.class);
            MessageType.Error errorType = (MessageType.Error) validMsg.messageType();
            assertThat(errorType.severity()).isEqualTo(expectedSeverity);
            assertThat(validMsg.timestamp()).isEqualTo(expectedTimestamp);
            assertThat(validMsg.message()).isEqualTo(expectedMessage);
        }

        @ParameterizedTest
        @DisplayName("Should handle Unknown/malformed messages")
        @ValueSource(strings = {
            "This is not in the right format",
            "X 123 Invalid message type",
            "I invalid timestamp format",
            "E no severity",
            "",
            "I",
            "E 50"
        })
        void shouldHandleUnknownMessages(String input) {
            LogMessage result = exercise.parseMessage(input);

            assertThat(result).isInstanceOf(LogMessage.Unknown.class);
            LogMessage.Unknown unknownMsg = (LogMessage.Unknown) result;
            assertThat(unknownMsg.originalMessage()).isEqualTo(input);
        }

        @Test
        @DisplayName("Should handle edge cases for message parsing")
        void shouldHandleEdgeCases() {
            // Test with extra whitespace
            LogMessage result1 = exercise.parseMessage("I 123 message with extra spaces");
            assertThat(result1).isInstanceOf(LogMessage.ValidMessage.class);

            // Test with empty message content
            LogMessage result2 = exercise.parseMessage("W 456");
            assertThat(result2).isInstanceOf(LogMessage.ValidMessage.class);
            LogMessage.ValidMessage validMsg = (LogMessage.ValidMessage) result2;
            assertThat(validMsg.message()).isEmpty();
        }
    }

    @Nested
    @DisplayName("Parse Complete Log File Tests")
    class ParseLogFileTests {

        @Test
        @DisplayName("Should parse multiple log messages correctly")
        void shouldParseMultipleMessages() {
            String logContent = """
                I 6 Completed armadillo processing
                I 1 Nothing to report
                E 99 10 Flange failed!
                I 4 Everything normal
                W 5 Flange is due for a check-up
                This is not in the right format
                E 70 3 Way too many pickles
                """;

            List<LogMessage> result = exercise.parse(logContent);

            assertThat(result).hasSize(7);

            // Check first message (Info)
            assertThat(result.get(0)).isInstanceOf(LogMessage.ValidMessage.class);
            LogMessage.ValidMessage msg1 = (LogMessage.ValidMessage) result.get(0);
            assertThat(msg1.messageType()).isEqualTo(MessageType.INFO);
            assertThat(msg1.timestamp()).isEqualTo(6);
            assertThat(msg1.message()).isEqualTo("Completed armadillo processing");

            // Check error message
            assertThat(result.get(2)).isInstanceOf(LogMessage.ValidMessage.class);
            LogMessage.ValidMessage msg3 = (LogMessage.ValidMessage) result.get(2);
            assertThat(msg3.messageType()).isInstanceOf(MessageType.Error.class);
            MessageType.Error errorType = (MessageType.Error) msg3.messageType();
            assertThat(errorType.severity()).isEqualTo(99);
            assertThat(msg3.timestamp()).isEqualTo(10);
            assertThat(msg3.message()).isEqualTo("Flange failed!");

            // Check unknown message
            assertThat(result.get(5)).isInstanceOf(LogMessage.Unknown.class);
            LogMessage.Unknown unknownMsg = (LogMessage.Unknown) result.get(5);
            assertThat(unknownMsg.originalMessage()).isEqualTo("This is not in the right format");
        }

        @Test
        @DisplayName("Should handle empty log content")
        void shouldHandleEmptyLogContent() {
            List<LogMessage> result = exercise.parse("");
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should handle log with only whitespace")
        void shouldHandleWhitespaceOnlyLog() {
            List<LogMessage> result = exercise.parse("   \n  \n  ");
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should parse sample log file format")
        void shouldParseSampleLogFormat() {
            String sampleLog = """
                I 6 Completed armadillo processing
                I 1 Nothing to report
                I 4 Everything normal
                I 11 Initiating self-destruct sequence
                E 70 3 Way too many pickles
                E 65 8 Bad pickle-flange interaction detected
                W 5 Flange is due for a check-up
                I 7 Out for lunch, back in two time steps
                E 20 2 Too many pickles
                I 9 Back from lunch
                E 99 10 Flange failed!
                """;

            List<LogMessage> result = exercise.parse(sampleLog);

            assertThat(result).hasSize(11);

            // Count message types
            long infoCount = result.stream()
                .filter(msg -> msg instanceof LogMessage.ValidMessage)
                .map(msg -> (LogMessage.ValidMessage) msg)
                .filter(msg -> msg.messageType() == MessageType.INFO)
                .count();
            assertThat(infoCount).isEqualTo(6);

            long warningCount = result.stream()
                .filter(msg -> msg instanceof LogMessage.ValidMessage)
                .map(msg -> (LogMessage.ValidMessage) msg)
                .filter(msg -> msg.messageType() == MessageType.WARNING)
                .count();
            assertThat(warningCount).isEqualTo(1);

            long errorCount = result.stream()
                .filter(msg -> msg instanceof LogMessage.ValidMessage)
                .map(msg -> (LogMessage.ValidMessage) msg)
                .filter(msg -> msg.messageType() instanceof MessageType.Error)
                .count();
            assertThat(errorCount).isEqualTo(4);
        }
    }
}
