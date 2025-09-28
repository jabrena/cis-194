package info.jab.cis194.homework2;

import java.util.Optional;

/**
 * Exercise 2 - MessageTree Insert Function
 * Functional implementation of binary search tree insertion based on CIS-194 Homework 2
 *
 * This implementation follows functional programming principles:
 * - Immutable data structures (creates new trees instead of modifying existing ones)
 * - Pure functions (no side effects)
 * - Pattern matching using sealed interfaces
 * - Recursive tree operations
 */
public class Exercise2 {

    /**
     * Insert a LogMessage into a MessageTree, producing a new MessageTree using functional approach.
     *
     * The tree maintains BST ordering by timestamp:
     * - Messages with smaller timestamps go to the left subtree
     * - Messages with larger timestamps go to the right subtree
     * - Duplicate timestamps are allowed (inserted to the right)
     * - Unknown messages are not inserted (tree returned unchanged)
     *
     * @param logMessage the message to insert
     * @param tree the tree to insert into
     * @return new MessageTree containing the inserted message (if valid)
     */
    public MessageTree insert(LogMessage logMessage, MessageTree tree) {
        return Optional.ofNullable(logMessage)
                .filter(msg -> tree != null)
                .map(msg -> switch (msg) {
                    case LogMessage.ValidMessage validMessage -> insertValid(validMessage, tree);
                    case LogMessage.Unknown unknown -> tree; // Return unchanged tree
                })
                .orElseThrow(() -> new IllegalArgumentException("LogMessage and MessageTree cannot be null"));
    }

    /**
     * Insert a valid log message into the tree
     */
    private MessageTree insertValid(LogMessage.ValidMessage message, MessageTree tree) {
        return switch (tree) {
            case MessageTree.Leaf leaf ->
                // Create new node with the message
                MessageTree.node(MessageTree.leaf(), message, MessageTree.leaf());

            case MessageTree.Node node -> {
                int messageTimestamp = message.timestamp();
                int nodeTimestamp = node.timestamp();

                if (messageTimestamp <= nodeTimestamp) {
                    // Insert into left subtree
                    MessageTree newLeft = insertValid(message, node.left());
                    yield MessageTree.node(newLeft, node.message(), node.right());
                } else {
                    // Insert into right subtree
                    MessageTree newRight = insertValid(message, node.right());
                    yield MessageTree.node(node.left(), node.message(), newRight);
                }
            }
        };
    }

    /**
     * Alternative implementation using Optional and functional composition
     */
    public MessageTree insertAlternative(LogMessage logMessage, MessageTree tree) {
        return Optional.ofNullable(logMessage)
                .filter(msg -> tree != null)
                .flatMap(msg -> msg instanceof LogMessage.ValidMessage validMessage ?
                        Optional.of(insertRecursive(validMessage, tree)) :
                        Optional.of(tree))
                .orElseThrow(() -> new IllegalArgumentException("Arguments cannot be null"));
    }

    private MessageTree insertRecursive(LogMessage.ValidMessage message, MessageTree tree) {
        if (tree instanceof MessageTree.Leaf) {
            return MessageTree.node(MessageTree.leaf(), message, MessageTree.leaf());
        }

        if (tree instanceof MessageTree.Node node) {
            int messageTimestamp = message.timestamp();
            int nodeTimestamp = node.timestamp();

            if (messageTimestamp <= nodeTimestamp) {
                MessageTree newLeft = insertRecursive(message, node.left());
                return MessageTree.node(newLeft, node.message(), node.right());
            } else {
                MessageTree newRight = insertRecursive(message, node.right());
                return MessageTree.node(node.left(), node.message(), newRight);
            }
        }

        throw new IllegalStateException("Unexpected MessageTree type");
    }

    /**
     * Utility method to insert multiple messages into a tree using functional composition
     */
    public MessageTree insertAll(java.util.List<LogMessage> messages, MessageTree initialTree) {
        return Optional.ofNullable(messages)
                .filter(msgs -> initialTree != null)
                .map(msgs -> msgs.stream()
                        .reduce(initialTree,
                               (tree, message) -> insert(message, tree),
                               (tree1, tree2) -> tree2))
                .orElseThrow(() -> new IllegalArgumentException("Arguments cannot be null"));
    }

    /**
     * Check if a tree is properly ordered (BST property)
     */
    public boolean isValidBST(MessageTree tree) {
        return isValidBSTHelper(tree, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    private boolean isValidBSTHelper(MessageTree tree, int minValue, int maxValue) {
        return switch (tree) {
            case MessageTree.Leaf leaf -> true;
            case MessageTree.Node node -> {
                int timestamp = node.timestamp();
                yield timestamp > minValue
                    && timestamp < maxValue
                    && isValidBSTHelper(node.left(), minValue, timestamp)
                    && isValidBSTHelper(node.right(), timestamp, maxValue);
            }
        };
    }

    /**
     * Find a message with a specific timestamp in the tree
     */
    public java.util.Optional<LogMessage.ValidMessage> findByTimestamp(MessageTree tree, int timestamp) {
        return switch (tree) {
            case MessageTree.Leaf leaf -> java.util.Optional.empty();
            case MessageTree.Node node -> {
                int nodeTimestamp = node.timestamp();
                if (timestamp == nodeTimestamp) {
                    yield java.util.Optional.of(node.message());
                } else if (timestamp < nodeTimestamp) {
                    yield findByTimestamp(node.left(), timestamp);
                } else {
                    yield findByTimestamp(node.right(), timestamp);
                }
            }
        };
    }

    /**
     * Count the number of valid messages in the tree
     */
    public int countMessages(MessageTree tree) {
        return tree.size();
    }

    /**
     * Get the height of the tree
     */
    public int getHeight(MessageTree tree) {
        return tree.height();
    }
}
