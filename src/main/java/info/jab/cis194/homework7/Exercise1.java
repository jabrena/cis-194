package info.jab.cis194.homework7;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Exercise 1: JoinList implementation
 *
 * JoinList is a data structure that efficiently supports concatenation.
 * It's a binary tree where leaves contain elements and internal nodes
 * represent concatenations. This allows O(1) concatenation operations.
 */
public class Exercise1 {

    /**
     * Trampoline interface for stack-safe recursion
     */
    @FunctionalInterface
    private interface Trampoline<T> {
        Trampoline<T> apply();

        default boolean isComplete() {
            return false;
        }

        default T result() {
            throw new UnsupportedOperationException("Not completed yet");
        }

        default T evaluate() {
            Trampoline<T> current = this;
            while (!current.isComplete()) {
                current = current.apply();
            }
            return current.result();
        }

        static <T> Trampoline<T> complete(T result) {
            return new Trampoline<T>() {
                @Override
                public Trampoline<T> apply() {
                    throw new UnsupportedOperationException("Already completed");
                }

                @Override
                public boolean isComplete() {
                    return true;
                }

                @Override
                public T result() {
                    return result;
                }
            };
        }

        static <T> Trampoline<T> more(Supplier<Trampoline<T>> supplier) {
            return new Trampoline<T>() {
                @Override
                public Trampoline<T> apply() {
                    return supplier.get();
                }
            };
        }
    }

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

        /**
         * Apply a function to each element in the JoinList, creating a new JoinList
         * Functional map operation
         */
        public abstract <U> JoinList<U> map(Function<T, U> mapper);

        /**
         * Filter elements in the JoinList based on a predicate
         */
        public abstract JoinList<T> filter(Function<T, Boolean> predicate);

        /**
         * Fold the JoinList from the left using a binary operation
         */
        public abstract <U> U foldLeft(U identity, Function<U, Function<T, U>> combiner);

        /**
         * Fold the JoinList from the right using a binary operation
         */
        public abstract <U> U foldRight(U identity, Function<T, Function<U, U>> combiner);
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

        @Override
        public <U> JoinList<U> map(Function<T, U> mapper) {
            return empty();
        }

        @Override
        public JoinList<T> filter(Function<T, Boolean> predicate) {
            return this;
        }

        @Override
        public <U> U foldLeft(U identity, Function<U, Function<T, U>> combiner) {
            return identity;
        }

        @Override
        public <U> U foldRight(U identity, Function<T, Function<U, U>> combiner) {
            return identity;
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

        @Override
        public <U> JoinList<U> map(Function<T, U> mapper) {
            return single(mapper.apply(element));
        }

        @Override
        public JoinList<T> filter(Function<T, Boolean> predicate) {
            return predicate.apply(element) ? this : empty();
        }

        @Override
        public <U> U foldLeft(U identity, Function<U, Function<T, U>> combiner) {
            return combiner.apply(identity).apply(element);
        }

        @Override
        public <U> U foldRight(U identity, Function<T, Function<U, U>> combiner) {
            return combiner.apply(element).apply(identity);
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

        @Override
        public <U> JoinList<U> map(Function<T, U> mapper) {
            return left.map(mapper).append(right.map(mapper));
        }

        @Override
        public JoinList<T> filter(Function<T, Boolean> predicate) {
            return left.filter(predicate).append(right.filter(predicate));
        }

        @Override
        public <U> U foldLeft(U identity, Function<U, Function<T, U>> combiner) {
            U leftResult = left.foldLeft(identity, combiner);
            return right.foldLeft(leftResult, combiner);
        }

        @Override
        public <U> U foldRight(U identity, Function<T, Function<U, U>> combiner) {
            U rightResult = right.foldRight(identity, combiner);
            return left.foldRight(rightResult, combiner);
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
     * Creates a JoinList from a regular List using functional approach
     */
    public static <T> JoinList<T> fromList(List<T> list) {
        if (list.isEmpty()) {
            return empty();
        }

        // Build a balanced tree for better performance using trampoline
        return fromListBalancedTrampoline(list, 0, list.size()).evaluate();
    }

    /**
     * Stack-safe helper method to build a balanced JoinList using trampoline
     */
    private static <T> Trampoline<JoinList<T>> fromListBalancedTrampoline(List<T> list, int start, int end) {
        if (start >= end) {
            return Trampoline.complete(empty());
        }
        if (end - start == 1) {
            return Trampoline.complete(single(list.get(start)));
        }

        int mid = start + (end - start) / 2;
        return Trampoline.more(() -> {
            JoinList<T> left = fromListBalancedTrampoline(list, start, mid).evaluate();
            JoinList<T> right = fromListBalancedTrampoline(list, mid, end).evaluate();
            return Trampoline.complete(left.append(right));
        });
    }

    /**
     * Alternative functional approach using fold
     */
    public static <T> JoinList<T> fromListFold(List<T> list) {
        return list.stream()
                .map(Exercise1::<T>single)
                .reduce(empty(), JoinList::append);
    }

    /**
     * Create JoinList using functional composition
     */
    public static <T> Function<List<T>, JoinList<T>> joinListBuilder() {
        return Exercise1::fromList;
    }

    /**
     * Functional utility to transform a regular list operation to JoinList operation
     */
    public static <T, U> Function<JoinList<T>, JoinList<U>> liftFunction(Function<List<T>, List<U>> listFunction) {
        return joinList -> fromList(listFunction.apply(joinList.toList()));
    }
}
