package info.jab.cis194.homework2;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Exercise 5 - whatWentWrong Function
 * Functional implementation based on CIS-194 Homework 2
 *
 * This implementation follows functional programming principles:
 * - Immutable data structures
 * - Pure functions (no side effects)
 * - Stream-based operations
 * - Function composition
 *
 * The whatWentWrong function extracts high-severity error messages from a log,
 * filters for errors with severity >= 50, and returns them sorted by timestamp.
 */
public class Exercise5 {

    private final Exercise3 buildOperations = new Exercise3();
    private final Exercise4 traversalOperations = new Exercise4();

    /**
     * Extract relevant error messages from an unsorted list of LogMessages.
     *
     * "Relevant" means errors with a severity of at least 50.
     * The function:
     * 1. Filters for error messages with severity >= 50
     * 2. Sorts them by timestamp using functional composition
     * 3. Returns the error message strings
     *
     * @param messages unsorted list of LogMessages
     * @return list of error message strings for high-severity errors, sorted by timestamp
     * @throws IllegalArgumentException if messages is null
     */
    public List<String> whatWentWrong(List<LogMessage> messages) {
        if (messages == null) {
            throw new IllegalArgumentException("Messages list cannot be null");
        }

        return messages.stream()
                .filter(this::isHighSeverityError)
                .map(msg -> (LogMessage.ValidMessage) msg) // Safe cast after filtering
                .sorted((msg1, msg2) -> Integer.compare(msg1.timestamp(), msg2.timestamp()))
                .map(LogMessage.ValidMessage::message)
                .toList();
    }

    /**
     * Alternative implementation using the MessageTree build + inOrder approach
     * This more closely follows the homework's intended approach
     */
    public List<String> whatWentWrongTreeBased(List<LogMessage> messages) {
        if (messages == null) {
            throw new IllegalArgumentException("Messages list cannot be null");
        }

        // Filter for high-severity errors
        List<LogMessage> highSeverityErrors = messages.stream()
                .filter(this::isHighSeverityError)
                .toList();

        // Build MessageTree and get sorted messages
        MessageTree tree = buildOperations.build(highSeverityErrors);
        List<LogMessage.ValidMessage> sortedMessages = traversalOperations.inOrder(tree);

        // Extract message strings
        return sortedMessages.stream()
                .map(LogMessage.ValidMessage::message)
                .toList();
    }

    /**
     * Functional composition approach using pure functional pipeline
     */
    public List<String> whatWentWrongComposed(List<LogMessage> messages) {
        return Optional.ofNullable(messages)
                .map(List::stream)
                .orElse(Stream.empty())
                .filter(this::isHighSeverityError)
                .map(msg -> (LogMessage.ValidMessage) msg)
                .sorted((msg1, msg2) -> Integer.compare(msg1.timestamp(), msg2.timestamp()))
                .map(LogMessage.ValidMessage::message)
                .toList();
    }

    /**
     * Check if a LogMessage is a high-severity error (severity >= 50)
     */
    private boolean isHighSeverityError(LogMessage message) {
        return switch (message) {
            case LogMessage.ValidMessage validMsg ->
                validMsg.messageType() instanceof MessageType.Error error &&
                error.severity() >= 50;
            case LogMessage.Unknown unknown -> false;
        };
    }

    /**
     * Sort messages by timestamp and extract message strings using functional composition
     */
    private List<String> sortAndExtractMessages(List<LogMessage.ValidMessage> messages) {
        return messages.stream()
                .sorted((msg1, msg2) -> Integer.compare(msg1.timestamp(), msg2.timestamp()))
                .map(LogMessage.ValidMessage::message)
                .toList();
    }

    /**
     * Sort ValidMessages by timestamp using natural ordering
     */
    private List<LogMessage.ValidMessage> sortMessagesByTimestamp(List<LogMessage.ValidMessage> messages) {
        return messages.stream()
                .sorted((msg1, msg2) -> Integer.compare(msg1.timestamp(), msg2.timestamp()))
                .toList();
    }

    /**
     * Alternative sorting using MessageTree (as intended by the homework)
     */
    private List<LogMessage.ValidMessage> sortUsingMessageTree(List<LogMessage.ValidMessage> messages) {
        // Convert to LogMessage list for build function
        List<LogMessage> logMessages = messages.stream()
                .map(msg -> (LogMessage) msg)
                .toList();

        MessageTree tree = buildOperations.build(logMessages);
        return traversalOperations.inOrder(tree);
    }

    /**
     * Get all high-severity errors with their details using pure functional pipeline
     */
    public List<LogMessage.ValidMessage> getHighSeverityErrors(List<LogMessage> messages) {
        return Optional.ofNullable(messages)
                .map(List::stream)
                .orElse(Stream.empty())
                .filter(this::isHighSeverityError)
                .map(msg -> (LogMessage.ValidMessage) msg)
                .sorted((msg1, msg2) -> Integer.compare(msg1.timestamp(), msg2.timestamp()))
                .toList();
    }

    /**
     * Count high-severity errors in a list using Optional for null safety
     */
    public long countHighSeverityErrors(List<LogMessage> messages) {
        return Optional.ofNullable(messages)
                .map(List::stream)
                .orElse(Stream.empty())
                .filter(this::isHighSeverityError)
                .count();
    }

    /**
     * Get the severity threshold used for filtering
     */
    public int getSeverityThreshold() {
        return 50;
    }

    /**
     * Check if a message meets the severity threshold
     */
    public boolean meetsThreshold(LogMessage message) {
        return isHighSeverityError(message);
    }

    /**
     * Get summary statistics about errors in a log
     */
    public record ErrorSummary(
        long totalMessages,
        long totalErrors,
        long highSeverityErrors,
        List<String> highSeverityErrorMessages
    ) {}

    public ErrorSummary getErrorSummary(List<LogMessage> messages) {
        if (messages == null) {
            return new ErrorSummary(0, 0, 0, List.of());
        }

        long totalMessages = messages.size();
        long totalErrors = messages.stream()
                .filter(msg -> msg instanceof LogMessage.ValidMessage validMsg &&
                              validMsg.messageType() instanceof MessageType.Error)
                .count();
        long highSeverityErrors = countHighSeverityErrors(messages);
        List<String> highSeverityErrorMessages = whatWentWrong(messages);

        return new ErrorSummary(totalMessages, totalErrors, highSeverityErrors, highSeverityErrorMessages);
    }
}
