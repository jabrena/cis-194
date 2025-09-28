package info.jab.cis194.homework4;

import java.util.List;

/**
 * Exercise 2: Tree Folding
 *
 * The goal is to implement foldTree, which generates a balanced binary tree
 * from a list of values using a fold.
 *
 * A balanced binary tree is one where the heights of the left and right subtrees
 * of any node differ by at most 1.
 *
 * The algorithm should:
 * 1. Start with an empty tree (leaf)
 * 2. For each element in the list, insert it into the tree maintaining balance
 * 3. The insertion should maintain the balanced property efficiently
 */
public class Exercise2 {

    /**
     * Builds a balanced binary tree from a list of values.
     *
     * The tree is constructed by folding over the input list, inserting each
     * element while maintaining the balanced property.
     *
     * @param <T> the type of values in the tree
     * @param values the list of values to insert into the tree
     * @return a balanced binary tree containing all the values
     * @throws IllegalArgumentException if the input list is null
     */
    public static <T> Tree<T> foldTree(List<T> values) {
        if (values == null) {
            throw new IllegalArgumentException("Input list cannot be null");
        }

        return values.stream()
            .reduce(
                Tree.leaf(),
                Exercise2::insertBalanced,
                Exercise2::mergeTrees
            );
    }

    /**
     * Inserts a value into a tree while maintaining balance.
     * This is the key function that maintains the balanced property.
     *
     * @param <T> the type of values in the tree
     * @param tree the tree to insert into
     * @param value the value to insert
     * @return a new balanced tree with the value inserted
     */
    private static <T> Tree<T> insertBalanced(Tree<T> tree, T value) {
        if (tree.isLeaf()) {
            return Tree.node(0, Tree.leaf(), value, Tree.leaf());
        }

        Tree<T> left = tree.left();
        Tree<T> right = tree.right();

        // Insert into the smaller subtree to maintain balance
        if (left.height() <= right.height()) {
            Tree<T> newLeft = insertBalanced(left, value);
            return rebalance(newLeft, tree.value(), right);
        } else {
            Tree<T> newRight = insertBalanced(right, value);
            return rebalance(left, tree.value(), newRight);
        }
    }

    /**
     * Creates a balanced tree from left subtree, value, and right subtree.
     * Calculates the correct height and ensures balance.
     *
     * @param <T> the type of values in the tree
     * @param left the left subtree
     * @param value the root value
     * @param right the right subtree
     * @return a balanced tree with correct height
     */
    private static <T> Tree<T> rebalance(Tree<T> left, T value, Tree<T> right) {
        int leftHeight = left.height();
        int rightHeight = right.height();
        int newHeight = 1 + Math.max(leftHeight, rightHeight);

        return Tree.node(newHeight, left, value, right);
    }

    /**
     * Merges two trees. This is used as the combiner function in the reduce operation.
     * For this implementation, we simply insert all elements from the second tree
     * into the first tree.
     *
     * @param <T> the type of values in the trees
     * @param tree1 the first tree
     * @param tree2 the second tree
     * @return a merged tree containing all elements from both trees
     */
    private static <T> Tree<T> mergeTrees(Tree<T> tree1, Tree<T> tree2) {
        if (tree2.isLeaf()) {
            return tree1;
        }

        // Insert all elements from tree2 into tree1
        List<T> tree2Values = tree2.inOrder();
        return tree2Values.stream()
            .reduce(tree1, Exercise2::insertBalanced, Exercise2::mergeTrees);
    }
}
