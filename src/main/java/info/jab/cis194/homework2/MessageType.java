package info.jab.cis194.homework2;

/**
 * Represents the type of a log message.
 * Based on CIS-194 Homework 2 MessageType data type.
 *
 * This is a sealed interface to represent the algebraic data type:
 * - INFO: Informational messages
 * - WARNING: Warning messages
 * - ERROR: Error messages with severity level
 */
public sealed interface MessageType permits MessageType.Info, MessageType.Warning, MessageType.Error {

    /**
     * Informational message type
     */
    record Info() implements MessageType {
        @Override
        public String toString() {
            return "Info";
        }
    }

    /**
     * Warning message type
     */
    record Warning() implements MessageType {
        @Override
        public String toString() {
            return "Warning";
        }
    }

    /**
     * Error message type with severity level
     */
    record Error(int severity) implements MessageType {
        public Error {
            if (severity < 1) {
                throw new IllegalArgumentException("Error severity must be positive");
            }
        }

        @Override
        public String toString() {
            return "Error(" + severity + ")";
        }
    }

    // Singleton instances for Info and Warning
    MessageType.Info INFO = new Info();
    MessageType.Warning WARNING = new Warning();

    /**
     * Factory method to create an Error with the specified severity
     */
    static MessageType.Error error(int severity) {
        return new Error(severity);
    }
}
