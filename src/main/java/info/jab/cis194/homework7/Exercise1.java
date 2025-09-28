package info.jab.cis194.homework7;

import java.util.ArrayList;
import java.util.List;

/**
 * Exercise 1: JoinList implementation
 *
 * JoinList is a data structure that efficiently supports concatenation.
 * It's a binary tree where leaves contain elements and internal nodes
 * represent concatenations. This allows O(1) concatenation operations.
 */
public class Exercise1 {

    /**
     * JoinList data structure using algebraic data types pattern
     * This is an abstract base class with concrete implementations for
     * Empty, Single, and Append cases.
     */
    public static abstract class JoinList<T> {

        /**
         * Returns the size of this JoinList
         */
        public abstract int size();

        /**
         * Returns true if this JoinList is empty
         */
        public abstract boolean isEmpty();

        /**
         * Appends another JoinList to this one, returning a new JoinList
         */
        public abstract JoinList<T> append(JoinList<T> other);

        /**
         * Converts this JoinList to a regular List
         */
        public abstract List<T> toList();

        /**
         * Gets the element at the specified index
         */
        public abstract T indexJ(int index);

        /**
         * Drops the first n elements from this JoinList
         */
        public abstract JoinList<T> dropJ(int n);

        /**
         * Takes the first n elements from this JoinList
         */
        public abstract JoinList<T> takeJ(int n);
    }

    /**
     * Empty JoinList case
     */
    private static class Empty<T> extends JoinList<T> {
        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public JoinList<T> append(JoinList<T> other) {
            return other;
        }

        @Override
        public List<T> toList() {
            return new ArrayList<>();
        }

        @Override
        public T indexJ(int index) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for empty JoinList");
        }

        @Override
        public JoinList<T> dropJ(int n) {
            return this;
        }

        @Override
        public JoinList<T> takeJ(int n) {
            return this;
        }
    }

    /**
     * Single element JoinList case
     */
    private static class Single<T> extends JoinList<T> {
        private final T element;

        public Single(T element) {
            this.element = element;
        }

        @Override
        public int size() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public JoinList<T> append(JoinList<T> other) {
            if (other.isEmpty()) {
                return this;
            }
            return new Append<>(this, other);
        }

        @Override
        public List<T> toList() {
            List<T> result = new ArrayList<>();
            result.add(element);
            return result;
        }

        @Override
        public T indexJ(int index) {
            if (index == 0) {
                return element;
            }
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for JoinList of size 1");
        }

        @Override
        public JoinList<T> dropJ(int n) {
            if (n <= 0) {
                return this;
            }
            return empty();
        }

        @Override
        public JoinList<T> takeJ(int n) {
            if (n <= 0) {
                return empty();
            }
            return this;
        }
    }

    /**
     * Append JoinList case (internal node)
     */
    private static class Append<T> extends JoinList<T> {
        private final JoinList<T> left;
        private final JoinList<T> right;
        private final int size;

        public Append(JoinList<T> left, JoinList<T> right) {
            this.left = left;
            this.right = right;
            this.size = left.size() + right.size();
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public boolean isEmpty() {
            return false; // Append nodes are never empty
        }

        @Override
        public JoinList<T> append(JoinList<T> other) {
            if (other.isEmpty()) {
                return this;
            }
            return new Append<>(this, other);
        }

        @Override
        public List<T> toList() {
            List<T> result = new ArrayList<>();
            result.addAll(left.toList());
            result.addAll(right.toList());
            return result;
        }

        @Override
        public T indexJ(int index) {
            if (index < 0 || index >= size) {
                throw new IndexOutOfBoundsException("Index " + index + " out of bounds for JoinList of size " + size);
            }

            int leftSize = left.size();
            if (index < leftSize) {
                return left.indexJ(index);
            } else {
                return right.indexJ(index - leftSize);
            }
        }

        @Override
        public JoinList<T> dropJ(int n) {
            if (n <= 0) {
                return this;
            }
            if (n >= size) {
                return empty();
            }

            int leftSize = left.size();
            if (n < leftSize) {
                return left.dropJ(n).append(right);
            } else {
                return right.dropJ(n - leftSize);
            }
        }

        @Override
        public JoinList<T> takeJ(int n) {
            if (n <= 0) {
                return empty();
            }
            if (n >= size) {
                return this;
            }

            int leftSize = left.size();
            if (n <= leftSize) {
                return left.takeJ(n);
            } else {
                return left.append(right.takeJ(n - leftSize));
            }
        }
    }

    /**
     * Creates an empty JoinList
     */
    public static <T> JoinList<T> empty() {
        return new Empty<>();
    }

    /**
     * Creates a JoinList with a single element
     */
    public static <T> JoinList<T> single(T element) {
        return new Single<>(element);
    }

    /**
     * Creates a JoinList from a regular List
     */
    public static <T> JoinList<T> fromList(List<T> list) {
        if (list.isEmpty()) {
            return empty();
        }

        // Build a balanced tree for better performance
        return fromListBalanced(list, 0, list.size());
    }

    /**
     * Helper method to build a balanced JoinList from a sublist
     */
    private static <T> JoinList<T> fromListBalanced(List<T> list, int start, int end) {
        if (start >= end) {
            return empty();
        }
        if (end - start == 1) {
            return single(list.get(start));
        }

        int mid = start + (end - start) / 2;
        JoinList<T> left = fromListBalanced(list, start, mid);
        JoinList<T> right = fromListBalanced(list, mid, end);

        return left.append(right);
    }
}
