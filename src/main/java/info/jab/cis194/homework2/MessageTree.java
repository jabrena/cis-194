package info.jab.cis194.homework2;

/**
 * Represents a binary search tree of log messages.
 * Based on CIS-194 Homework 2 MessageTree data type.
 *
 * This is a sealed interface to represent the algebraic data type:
 * - Leaf: Empty tree
 * - Node: Tree node with left subtree, message, and right subtree
 *
 * The tree is ordered by timestamp: left < current < right
 * Unknown messages are not stored in the tree.
 */
public sealed interface MessageTree permits MessageTree.Leaf, MessageTree.Node {

    /**
     * Empty tree (leaf node)
     */
    record Leaf() implements MessageTree {
        @Override
        public String toString() {
            return "Leaf";
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public int height() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }
    }

    /**
     * Tree node with left subtree, log message, and right subtree
     */
    record Node(MessageTree left, LogMessage.ValidMessage message, MessageTree right)
            implements MessageTree {

        public Node {
            if (message == null) {
                throw new IllegalArgumentException("Message cannot be null");
            }
            if (left == null) {
                left = new Leaf();
            }
            if (right == null) {
                right = new Leaf();
            }
        }

        @Override
        public String toString() {
            return "Node(" + left + ", " + message + ", " + right + ")";
        }

        @Override
        public int size() {
            return 1 + left.size() + right.size();
        }

        @Override
        public int height() {
            return 1 + Math.max(left.height(), right.height());
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        /**
         * Get the timestamp of the message in this node
         */
        public int timestamp() {
            return message.timestamp();
        }
    }

    // Singleton instance for empty tree
    MessageTree LEAF = new Leaf();

    /**
     * Factory method to create a leaf (empty tree)
     */
    static MessageTree leaf() {
        return LEAF;
    }

    /**
     * Factory method to create a node
     */
    static MessageTree node(MessageTree left, LogMessage.ValidMessage message, MessageTree right) {
        return new Node(left, message, right);
    }

    /**
     * Get the number of messages in the tree
     */
    int size();

    /**
     * Get the height of the tree
     */
    int height();

    /**
     * Check if the tree is empty
     */
    boolean isEmpty();

    /**
     * Check if this tree contains a message with the given timestamp
     */
    default boolean contains(int timestamp) {
        return switch (this) {
            case Leaf leaf -> false;
            case Node node -> {
                int nodeTimestamp = node.timestamp();
                if (timestamp == nodeTimestamp) {
                    yield true;
                } else if (timestamp < nodeTimestamp) {
                    yield node.left().contains(timestamp);
                } else {
                    yield node.right().contains(timestamp);
                }
            }
        };
    }

    /**
     * Find the minimum timestamp in the tree
     */
    default java.util.Optional<Integer> minTimestamp() {
        return switch (this) {
            case Leaf leaf -> java.util.Optional.empty();
            case Node node -> {
                if (node.left().isEmpty()) {
                    yield java.util.Optional.of(node.timestamp());
                } else {
                    yield node.left().minTimestamp();
                }
            }
        };
    }

    /**
     * Find the maximum timestamp in the tree
     */
    default java.util.Optional<Integer> maxTimestamp() {
        return switch (this) {
            case Leaf leaf -> java.util.Optional.empty();
            case Node node -> {
                if (node.right().isEmpty()) {
                    yield java.util.Optional.of(node.timestamp());
                } else {
                    yield node.right().maxTimestamp();
                }
            }
        };
    }
}
