package info.jab.cis194.homework4;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A generic binary tree data structure.
 *
 * Trees can be either:
 * - Leaf: An empty tree with no data
 * - Node: A tree with a height, left subtree, value, and right subtree
 *
 * @param <T> the type of values stored in the tree
 */
public abstract class Tree<T> {

    /**
     * Creates a leaf (empty tree).
     *
     * @param <T> the type of values that would be stored in the tree
     * @return a new leaf tree
     */
    public static <T> Tree<T> leaf() {
        return new Leaf<>();
    }

    /**
     * Creates a node with the given height, left subtree, value, and right subtree.
     *
     * @param <T> the type of values stored in the tree
     * @param height the height of this node
     * @param left the left subtree
     * @param value the value stored in this node
     * @param right the right subtree
     * @return a new node tree
     */
    public static <T> Tree<T> node(int height, Tree<T> left, T value, Tree<T> right) {
        return new Node<>(height, left, value, right);
    }

    /**
     * Returns true if this tree is a leaf (empty).
     *
     * @return true if this is a leaf, false otherwise
     */
    public abstract boolean isLeaf();

    /**
     * Returns the height of this tree.
     * A leaf has height -1, and a node's height is 1 + max(left height, right height).
     *
     * @return the height of this tree
     */
    public abstract int height();

    /**
     * Returns the number of nodes (non-leaf elements) in this tree.
     *
     * @return the size of this tree
     */
    public abstract int size();

    /**
     * Returns the value stored in this node.
     * Only valid for non-leaf trees.
     *
     * @return the value stored in this node
     * @throws UnsupportedOperationException if called on a leaf
     */
    public abstract T value();

    /**
     * Returns the left subtree.
     * Only valid for non-leaf trees.
     *
     * @return the left subtree
     * @throws UnsupportedOperationException if called on a leaf
     */
    public abstract Tree<T> left();

    /**
     * Returns the right subtree.
     * Only valid for non-leaf trees.
     *
     * @return the right subtree
     * @throws UnsupportedOperationException if called on a leaf
     */
    public abstract Tree<T> right();

    /**
     * Returns an in-order traversal of the tree as a list.
     *
     * @return a list containing all values in the tree in in-order
     */
    public List<T> inOrder() {
        List<T> result = new ArrayList<>();
        inOrderHelper(result);
        return result;
    }

    /**
     * Helper method for in-order traversal.
     *
     * @param result the list to add elements to
     */
    protected abstract void inOrderHelper(List<T> result);

    /**
     * Leaf implementation - represents an empty tree.
     */
    private static class Leaf<T> extends Tree<T> {

        @Override
        public boolean isLeaf() {
            return true;
        }

        @Override
        public int height() {
            return -1;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public T value() {
            throw new UnsupportedOperationException("Leaf has no value");
        }

        @Override
        public Tree<T> left() {
            throw new UnsupportedOperationException("Leaf has no left subtree");
        }

        @Override
        public Tree<T> right() {
            throw new UnsupportedOperationException("Leaf has no right subtree");
        }

        @Override
        protected void inOrderHelper(List<T> result) {
            // Leaf contributes nothing to in-order traversal
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Leaf;
        }

        @Override
        public int hashCode() {
            return 0; // All leaves are equal
        }

        @Override
        public String toString() {
            return "Leaf";
        }
    }

    /**
     * Node implementation - represents a tree with data.
     */
    private static class Node<T> extends Tree<T> {
        private final int height;
        private final Tree<T> left;
        private final T value;
        private final Tree<T> right;

        public Node(int height, Tree<T> left, T value, Tree<T> right) {
            this.height = height;
            this.left = left;
            this.value = value;
            this.right = right;
        }

        @Override
        public boolean isLeaf() {
            return false;
        }

        @Override
        public int height() {
            return height;
        }

        @Override
        public int size() {
            return 1 + left.size() + right.size();
        }

        @Override
        public T value() {
            return value;
        }

        @Override
        public Tree<T> left() {
            return left;
        }

        @Override
        public Tree<T> right() {
            return right;
        }

        @Override
        protected void inOrderHelper(List<T> result) {
            left.inOrderHelper(result);
            result.add(value);
            right.inOrderHelper(result);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Node)) return false;
            Node<?> node = (Node<?>) obj;
            return height == node.height &&
                   Objects.equals(left, node.left) &&
                   Objects.equals(value, node.value) &&
                   Objects.equals(right, node.right);
        }

        @Override
        public int hashCode() {
            return Objects.hash(height, left, value, right);
        }

        @Override
        public String toString() {
            return String.format("Node(h=%d, %s, %s, %s)", height, left, value, right);
        }
    }
}
