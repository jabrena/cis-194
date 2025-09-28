package info.jab.cis194.homework2;

import java.util.List;
import java.util.Optional;

/**
 * Exercise 3 & 4 - MessageTree Build and InOrder Functions
 * Functional implementation based on CIS-194 Homework 2
 *
 * This implementation follows functional programming principles:
 * - Immutable data structures
 * - Pure functions (no side effects)
 * - Recursive operations
 * - Function composition
 *
 * Exercise 3: build - constructs MessageTree from list of LogMessages
 * Exercise 4: inOrder - performs in-order traversal of MessageTree
 */
public class Exercise3And4 {

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
     * Exercise 4: Perform in-order traversal of a MessageTree using functional approach.
     *
     * Returns a list of all LogMessages in the tree, sorted by timestamp from smallest to largest.
     * This is the standard in-order traversal of a binary search tree.
     *
     * @param tree the MessageTree to traverse
     * @return List of ValidMessages sorted by timestamp (ascending order)
     */
    public List<LogMessage.ValidMessage> inOrder(MessageTree tree) {
        return Optional.ofNullable(tree)
                .map(this::inOrderFunctional)
                .orElse(List.of());
    }

    /**
     * Helper method for in-order traversal using accumulator pattern
     */
    private List<LogMessage.ValidMessage> inOrderHelper(MessageTree tree, List<LogMessage.ValidMessage> accumulator) {
        return switch (tree) {
            case MessageTree.Leaf leaf -> accumulator;
            case MessageTree.Node node -> {
                // In-order: left subtree, current node, right subtree
                inOrderHelper(node.left(), accumulator);
                accumulator.add(node.message());
                inOrderHelper(node.right(), accumulator);
                yield accumulator;
            }
        };
    }

    /**
     * Functional implementation using streams and recursion with immutable operations
     */
    public List<LogMessage.ValidMessage> inOrderFunctional(MessageTree tree) {
        return switch (tree) {
            case MessageTree.Leaf leaf -> List.of();
            case MessageTree.Node node -> {
                List<LogMessage.ValidMessage> leftMessages = inOrderFunctional(node.left());
                List<LogMessage.ValidMessage> rightMessages = inOrderFunctional(node.right());

                // Combine: left + current + right using functional composition
                yield java.util.stream.Stream.of(
                    leftMessages.stream(),
                    java.util.stream.Stream.of(node.message()),
                    rightMessages.stream()
                ).flatMap(java.util.function.Function.identity()).toList();
            }
        };
    }

    /**
     * Alternative implementation using Java streams for concatenation
     */
    public List<LogMessage.ValidMessage> inOrderStream(MessageTree tree) {
        if (tree == null) {
            throw new IllegalArgumentException("MessageTree cannot be null");
        }

        return switch (tree) {
            case MessageTree.Leaf leaf -> List.of();
            case MessageTree.Node node -> {
                yield java.util.stream.Stream.of(
                    inOrderStream(node.left()).stream(),
                    java.util.stream.Stream.of(node.message()),
                    inOrderStream(node.right()).stream()
                ).flatMap(java.util.function.Function.identity())
                 .toList();
            }
        };
    }

    /**
     * Utility method to sort a list of LogMessages using functional composition of build + inOrder
     * This demonstrates the combination of exercises 3 and 4
     */
    public List<LogMessage.ValidMessage> sortMessages(List<LogMessage> messages) {
        return Optional.ofNullable(messages)
                .map(this::build)
                .map(this::inOrder)
                .orElse(List.of());
    }

    /**
     * Get all messages from a tree as a sorted list (convenience method)
     */
    public List<LogMessage.ValidMessage> getAllMessagesSorted(MessageTree tree) {
        return inOrder(tree);
    }

    /**
     * Count valid messages in a list (utility method)
     */
    public long countValidMessages(List<LogMessage> messages) {
        return messages.stream()
                .filter(msg -> msg instanceof LogMessage.ValidMessage)
                .count();
    }

    /**
     * Verify that inOrder traversal produces sorted results
     */
    public boolean isInOrderSorted(MessageTree tree) {
        List<LogMessage.ValidMessage> messages = inOrder(tree);

        for (int i = 1; i < messages.size(); i++) {
            if (messages.get(i - 1).timestamp() > messages.get(i).timestamp()) {
                return false;
            }
        }
        return true;
    }
}
