package info.jab.cis194.homework2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Utility class for loading and working with the official sample.log data from CIS-194 Homework 2.
 *
 * This class provides convenient access to the sample log data for integration testing,
 * ensuring all tests use the exact same data as specified in the homework.
 */
class SampleLogData {

    private static final String SAMPLE_LOG_PATH = "src/test/resources/sample.log";

    // Lazy-loaded sample data
    private static String sampleLogContent;
    private static List<LogMessage> parsedSampleMessages;

    /**
     * Get the raw content of sample.log file
     */
    static String getSampleLogContent() {
        if (sampleLogContent == null) {
            try {
                sampleLogContent = Files.readString(Paths.get(SAMPLE_LOG_PATH));
            } catch (IOException e) {
                throw new RuntimeException("Failed to load sample.log from test resources", e);
            }
        }
        return sampleLogContent;
    }

    /**
     * Get the parsed LogMessages from sample.log
     */
    static List<LogMessage> getParsedSampleMessages() {
        if (parsedSampleMessages == null) {
            Exercise1 parser = new Exercise1();
            parsedSampleMessages = parser.parse(getSampleLogContent());
        }
        return parsedSampleMessages;
    }

    /**
     * Get only the valid messages from sample.log
     */
    static List<LogMessage.ValidMessage> getValidSampleMessages() {
        return getParsedSampleMessages().stream()
                .filter(msg -> msg instanceof LogMessage.ValidMessage)
                .map(msg -> (LogMessage.ValidMessage) msg)
                .toList();
    }

    /**
     * Get only the error messages from sample.log
     */
    static List<LogMessage.ValidMessage> getErrorSampleMessages() {
        return getValidSampleMessages().stream()
                .filter(msg -> msg.messageType() instanceof MessageType.Error)
                .toList();
    }

    /**
     * Get the expected high-severity error messages from sample.log
     * (as specified in the homework PDF)
     */
    static List<String> getExpectedHighSeverityErrors() {
        return List.of(
            "Way too many pickles",
            "Bad pickle-flange interaction detected",
            "Flange failed!"
        );
    }

    /**
     * Sample log statistics
     */
    static class SampleLogStats {
        static final int TOTAL_MESSAGES = 11;
        static final int INFO_MESSAGES = 6;
        static final int WARNING_MESSAGES = 1;
        static final int ERROR_MESSAGES = 4;
        static final int HIGH_SEVERITY_ERRORS = 3;
        static final int UNKNOWN_MESSAGES = 0; // All messages in sample.log are well-formed
    }

    /**
     * Specific message data from sample.log for detailed testing
     */
    static class SampleMessages {
        // Info messages
        static final LogMessage.ValidMessage ARMADILLO_PROCESSING =
            new LogMessage.ValidMessage(MessageType.INFO, 6, "Completed armadillo processing");
        static final LogMessage.ValidMessage NOTHING_TO_REPORT =
            new LogMessage.ValidMessage(MessageType.INFO, 1, "Nothing to report");
        static final LogMessage.ValidMessage EVERYTHING_NORMAL =
            new LogMessage.ValidMessage(MessageType.INFO, 4, "Everything normal");
        static final LogMessage.ValidMessage SELF_DESTRUCT =
            new LogMessage.ValidMessage(MessageType.INFO, 11, "Initiating self-destruct sequence");
        static final LogMessage.ValidMessage OUT_FOR_LUNCH =
            new LogMessage.ValidMessage(MessageType.INFO, 7, "Out for lunch, back in two time steps");
        static final LogMessage.ValidMessage BACK_FROM_LUNCH =
            new LogMessage.ValidMessage(MessageType.INFO, 9, "Back from lunch");

        // Warning message
        static final LogMessage.ValidMessage FLANGE_CHECKUP =
            new LogMessage.ValidMessage(MessageType.WARNING, 5, "Flange is due for a check-up");

        // Error messages
        static final LogMessage.ValidMessage TOO_MANY_PICKLES_HIGH =
            new LogMessage.ValidMessage(MessageType.error(70), 3, "Way too many pickles");
        static final LogMessage.ValidMessage PICKLE_FLANGE_INTERACTION =
            new LogMessage.ValidMessage(MessageType.error(65), 8, "Bad pickle-flange interaction detected");
        static final LogMessage.ValidMessage TOO_MANY_PICKLES_LOW =
            new LogMessage.ValidMessage(MessageType.error(20), 2, "Too many pickles");
        static final LogMessage.ValidMessage FLANGE_FAILED =
            new LogMessage.ValidMessage(MessageType.error(99), 10, "Flange failed!");
    }

    /**
     * Reset cached data (useful for testing)
     */
    static void resetCache() {
        sampleLogContent = null;
        parsedSampleMessages = null;
    }
}
