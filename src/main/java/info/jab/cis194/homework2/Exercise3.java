package info.jab.cis194.homework2;

import java.util.List;
import java.util.Optional;

/**
 * Exercise 3 - MessageTree Build Function
 * Functional implementation based on CIS-194 Homework 2
 *
 * This implementation follows functional programming principles:
 * - Immutable data structures
 * - Pure functions (no side effects)
 * - Function composition
 * - Optional for null safety
 *
 * Exercise 3: build - constructs MessageTree from list of LogMessages
 */
public class Exercise3 {

    private final Exercise2 insertHelper = new Exercise2();

    /**
     * Exercise 3: Build a MessageTree from a list of LogMessages using functional composition.
     *
     * This function successively inserts messages into a MessageTree starting with an empty tree.
     * Unknown messages are ignored (not inserted into the tree).
     * The resulting tree maintains BST ordering by timestamp.
     *
     * @param messages list of LogMessages to build tree from
     * @return MessageTree containing all valid messages from the list
     */
    public MessageTree build(List<LogMessage> messages) {
        return Optional.ofNullable(messages)
                .map(List::stream)
                .orElse(java.util.stream.Stream.empty())
                .reduce(MessageTree.leaf(),
                       (tree, message) -> insertHelper.insert(message, tree),
                       (tree1, tree2) -> tree2); // Combiner not used in sequential stream
    }

    /**
     * Alternative implementation using explicit fold/reduce pattern with Optional
     */
    public MessageTree buildAlternative(List<LogMessage> messages) {
        return Optional.ofNullable(messages)
                .orElse(List.of())
                .stream()
                .reduce(MessageTree.leaf(),
                       (tree, message) -> insertHelper.insert(message, tree),
                       (tree1, tree2) -> tree2);
    }

    /**
     * Build tree from valid messages only (utility method)
     */
    public MessageTree buildFromValidMessages(List<LogMessage.ValidMessage> validMessages) {
        return Optional.ofNullable(validMessages)
                .map(List::stream)
                .orElse(java.util.stream.Stream.empty())
                .map(msg -> (LogMessage) msg)
                .reduce(MessageTree.leaf(),
                       (tree, message) -> insertHelper.insert(message, tree),
                       (tree1, tree2) -> tree2);
    }

    /**
     * Count valid messages in a list (utility method)
     */
    public long countValidMessages(List<LogMessage> messages) {
        return Optional.ofNullable(messages)
                .map(List::stream)
                .orElse(java.util.stream.Stream.empty())
                .filter(msg -> msg instanceof LogMessage.ValidMessage)
                .count();
    }

    /**
     * Build tree and return statistics about the construction
     */
    public record BuildResult(MessageTree tree, long totalMessages, long validMessages, long unknownMessages) {}

    public BuildResult buildWithStatistics(List<LogMessage> messages) {
        if (messages == null) {
            return new BuildResult(MessageTree.leaf(), 0, 0, 0);
        }

        long total = messages.size();
        long valid = countValidMessages(messages);
        long unknown = total - valid;
        MessageTree tree = build(messages);

        return new BuildResult(tree, total, valid, unknown);
    }

    /**
     * Check if all messages in the list are valid
     */
    public boolean allMessagesValid(List<LogMessage> messages) {
        return Optional.ofNullable(messages)
                .map(List::stream)
                .orElse(java.util.stream.Stream.empty())
                .allMatch(msg -> msg instanceof LogMessage.ValidMessage);
    }

    /**
     * Filter out unknown messages and return only valid ones
     */
    public List<LogMessage.ValidMessage> extractValidMessages(List<LogMessage> messages) {
        return Optional.ofNullable(messages)
                .map(List::stream)
                .orElse(java.util.stream.Stream.empty())
                .filter(msg -> msg instanceof LogMessage.ValidMessage)
                .map(msg -> (LogMessage.ValidMessage) msg)
                .toList();
    }
}
