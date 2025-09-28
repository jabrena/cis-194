package info.jab.cis194.homework2;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * Exercise 1 - Log Message Parsing
 * Functional implementation of log message parsing based on CIS-194 Homework 2
 *
 * This implementation follows functional programming principles:
 * - Immutable data structures
 * - Pure functions (no side effects)
 * - Stream-based operations
 * - Pattern matching using sealed interfaces
 */
public class Exercise1 {

    /**
     * Parse a single log message line into a LogMessage.
     *
     * Expected formats:
     * - "I <timestamp> <message>" for Info messages
     * - "W <timestamp> <message>" for Warning messages
     * - "E <severity> <timestamp> <message>" for Error messages
     * - Any other format results in Unknown message
     *
     * @param line the log line to parse
     * @return LogMessage representing the parsed line
     */
    public LogMessage parseMessage(String line) {
        if (line == null || line.trim().isEmpty()) {
            return LogMessage.unknown(line == null ? "" : line);
        }

        String[] parts = line.trim().split("\\s+", 4);

        if (parts.length < 2) {
            return LogMessage.unknown(line);
        }

        return switch (parts[0]) {
            case "I" -> parseInfoMessage(parts, line);
            case "W" -> parseWarningMessage(parts, line);
            case "E" -> parseErrorMessage(parts, line);
            default -> LogMessage.unknown(line);
        };
    }

    /**
     * Parse an Info message: "I <timestamp> <message>"
     */
    private LogMessage parseInfoMessage(String[] parts, String originalLine) {
        if (parts.length < 2) {
            return LogMessage.unknown(originalLine);
        }

        try {
            int timestamp = Integer.parseInt(parts[1]);
            String message = parts.length > 2 ?
                String.join(" ", Arrays.copyOfRange(parts, 2, parts.length)) : "";
            return LogMessage.validMessage(MessageType.INFO, timestamp, message);
        } catch (NumberFormatException e) {
            return LogMessage.unknown(originalLine);
        }
    }

    /**
     * Parse a Warning message: "W <timestamp> <message>"
     */
    private LogMessage parseWarningMessage(String[] parts, String originalLine) {
        if (parts.length < 2) {
            return LogMessage.unknown(originalLine);
        }

        try {
            int timestamp = Integer.parseInt(parts[1]);
            String message = parts.length > 2 ?
                String.join(" ", Arrays.copyOfRange(parts, 2, parts.length)) : "";
            return LogMessage.validMessage(MessageType.WARNING, timestamp, message);
        } catch (NumberFormatException e) {
            return LogMessage.unknown(originalLine);
        }
    }

    /**
     * Parse an Error message: "E <severity> <timestamp> <message>"
     */
    private LogMessage parseErrorMessage(String[] parts, String originalLine) {
        if (parts.length < 3) {
            return LogMessage.unknown(originalLine);
        }

        try {
            int severity = Integer.parseInt(parts[1]);
            int timestamp = Integer.parseInt(parts[2]);
            String message = parts.length > 3 ?
                String.join(" ", Arrays.copyOfRange(parts, 3, parts.length)) : "";
            return LogMessage.validMessage(MessageType.error(severity), timestamp, message);
        } catch (NumberFormatException e) {
            return LogMessage.unknown(originalLine);
        } catch (IllegalArgumentException e) {
            return LogMessage.unknown(originalLine);
        }
    }

    /**
     * Parse an entire log file content into a list of LogMessages.
     * Each line is parsed individually using parseMessage.
     *
     * @param logContent the complete log file content
     * @return List of LogMessages, one per non-empty line
     */
    public List<LogMessage> parse(String logContent) {
        if (logContent == null || logContent.trim().isEmpty()) {
            return List.of();
        }

        return logContent.lines()
                .filter(line -> !line.trim().isEmpty())
                .map(this::parseMessage)
                .toList();
    }

    /**
     * Alternative functional implementation using Stream operations
     */
    public List<LogMessage> parseFunctional(String logContent) {
        return Stream.ofNullable(logContent)
                .filter(content -> !content.trim().isEmpty())
                .flatMap(content -> content.lines())
                .filter(line -> !line.trim().isEmpty())
                .map(this::parseMessage)
                .toList();
    }

    /**
     * Count the number of messages of each type in a log
     */
    public record MessageCounts(long info, long warning, long error, long unknown) {
        public static MessageCounts fromMessages(List<LogMessage> messages) {
            long info = messages.stream()
                .filter(msg -> msg instanceof LogMessage.ValidMessage vm && vm.messageType() == MessageType.INFO)
                .count();
            long warning = messages.stream()
                .filter(msg -> msg instanceof LogMessage.ValidMessage vm && vm.messageType() == MessageType.WARNING)
                .count();
            long error = messages.stream()
                .filter(msg -> msg instanceof LogMessage.ValidMessage vm && vm.messageType() instanceof MessageType.Error)
                .count();
            long unknown = messages.stream()
                .filter(msg -> msg instanceof LogMessage.Unknown)
                .count();
            return new MessageCounts(info, warning, error, unknown);
        }
    }

    /**
     * Get statistics about parsed messages
     */
    public MessageCounts getMessageCounts(List<LogMessage> messages) {
        return MessageCounts.fromMessages(messages);
    }
}
