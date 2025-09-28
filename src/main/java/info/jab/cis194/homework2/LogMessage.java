package info.jab.cis194.homework2;

/**
 * Represents a log message.
 * Based on CIS-194 Homework 2 LogMessage data type.
 *
 * This is a sealed interface to represent the algebraic data type:
 * - ValidMessage: Well-formed log messages with type, timestamp, and content
 * - Unknown: Malformed log messages that couldn't be parsed
 */
public sealed interface LogMessage permits LogMessage.ValidMessage, LogMessage.Unknown {

    /**
     * A well-formed log message with type, timestamp, and message content
     */
    record ValidMessage(MessageType messageType, int timestamp, String message) implements LogMessage {
        public ValidMessage {
            if (messageType == null) {
                throw new IllegalArgumentException("Message type cannot be null");
            }
            if (timestamp < 0) {
                throw new IllegalArgumentException("Timestamp must be non-negative");
            }
            if (message == null) {
                message = "";
            }
        }

        @Override
        public String toString() {
            return "LogMessage(" + messageType + ", " + timestamp + ", \"" + message + "\")";
        }

        /**
         * Check if this is an error message with severity >= threshold
         */
        public boolean isHighSeverityError(int threshold) {
            return messageType instanceof MessageType.Error error && error.severity() >= threshold;
        }

        /**
         * Get the severity if this is an error message, otherwise return 0
         */
        public int getSeverity() {
            return messageType instanceof MessageType.Error error ? error.severity() : 0;
        }
    }

    /**
     * An unknown/malformed log message that couldn't be parsed
     */
    record Unknown(String originalMessage) implements LogMessage {
        public Unknown {
            if (originalMessage == null) {
                originalMessage = "";
            }
        }

        @Override
        public String toString() {
            return "Unknown(\"" + originalMessage + "\")";
        }
    }

    /**
     * Factory method to create a ValidMessage
     */
    static LogMessage validMessage(MessageType messageType, int timestamp, String message) {
        return new ValidMessage(messageType, timestamp, message);
    }

    /**
     * Factory method to create an Unknown message
     */
    static LogMessage unknown(String originalMessage) {
        return new Unknown(originalMessage);
    }

    /**
     * Check if this log message is valid (not unknown)
     */
    default boolean isValid() {
        return this instanceof ValidMessage;
    }

    /**
     * Check if this log message is unknown
     */
    default boolean isUnknown() {
        return this instanceof Unknown;
    }
}
