package info.jab.cis194.homework2;

import java.util.List;
import java.util.Optional;

/**
 * Exercise 4 - MessageTree InOrder Function
 * Functional implementation based on CIS-194 Homework 2
 *
 * This implementation follows functional programming principles:
 * - Immutable data structures
 * - Pure functions (no side effects)
 * - Recursive operations
 * - Function composition
 * - Optional for null safety
 *
 * Exercise 4: inOrder - performs in-order traversal of MessageTree
 */
public class Exercise4 {

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
        return Optional.ofNullable(tree)
                .map(this::inOrderStreamHelper)
                .orElse(List.of());
    }

    private List<LogMessage.ValidMessage> inOrderStreamHelper(MessageTree tree) {
        return switch (tree) {
            case MessageTree.Leaf leaf -> List.of();
            case MessageTree.Node node -> {
                yield java.util.stream.Stream.of(
                    inOrderStreamHelper(node.left()).stream(),
                    java.util.stream.Stream.of(node.message()),
                    inOrderStreamHelper(node.right()).stream()
                ).flatMap(java.util.function.Function.identity())
                 .toList();
            }
        };
    }

    /**
     * Get all messages from a tree as a sorted list (convenience method)
     */
    public List<LogMessage.ValidMessage> getAllMessagesSorted(MessageTree tree) {
        return inOrder(tree);
    }

    /**
     * Verify that inOrder traversal produces sorted results
     */
    public boolean isInOrderSorted(MessageTree tree) {
        List<LogMessage.ValidMessage> messages = inOrder(tree);

        return messages.stream()
                .mapToInt(LogMessage.ValidMessage::timestamp)
                .boxed()
                .reduce(Optional.<Integer>empty(),
                       (prev, current) -> prev.map(p -> p <= current ? Optional.of(current) : Optional.<Integer>empty())
                                             .orElse(Optional.of(current)),
                       (a, b) -> b)
                .isPresent();
    }

    /**
     * Alternative sorting verification using functional approach
     */
    public boolean isInOrderSortedFunctional(MessageTree tree) {
        List<LogMessage.ValidMessage> messages = inOrder(tree);

        if (messages.size() <= 1) {
            return true;
        }

        return java.util.stream.IntStream.range(1, messages.size())
                .allMatch(i -> messages.get(i - 1).timestamp() <= messages.get(i).timestamp());
    }

    /**
     * Get the number of messages in the tree using inOrder traversal
     */
    public int countMessages(MessageTree tree) {
        return inOrder(tree).size();
    }

    /**
     * Find messages within a timestamp range using inOrder traversal
     */
    public List<LogMessage.ValidMessage> findMessagesInRange(MessageTree tree, int minTimestamp, int maxTimestamp) {
        return inOrder(tree).stream()
                .filter(msg -> msg.timestamp() >= minTimestamp && msg.timestamp() <= maxTimestamp)
                .toList();
    }

    /**
     * Get the first message (earliest timestamp) from the tree
     */
    public Optional<LogMessage.ValidMessage> getFirstMessage(MessageTree tree) {
        return inOrder(tree).stream().findFirst();
    }

    /**
     * Get the last message (latest timestamp) from the tree
     */
    public Optional<LogMessage.ValidMessage> getLastMessage(MessageTree tree) {
        List<LogMessage.ValidMessage> messages = inOrder(tree);
        return messages.isEmpty() ?
                Optional.empty() :
                Optional.of(messages.get(messages.size() - 1));
    }

    /**
     * Extract only error messages from the tree, maintaining timestamp order
     */
    public List<LogMessage.ValidMessage> getErrorMessages(MessageTree tree) {
        return inOrder(tree).stream()
                .filter(msg -> msg.messageType() instanceof MessageType.Error)
                .toList();
    }
}
