package info.jab.cis194.homework9;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Exercise 1 - Functor Implementation
 * Functional implementation of Functor typeclass based on CIS-194 Homework 9
 *
 * This implementation follows functional programming principles:
 * - Immutable data structures
 * - Pure functions (no side effects)
 * - Functor laws compliance
 * - Generic type safety
 */
public class Exercise1 {

    /**
     * Maybe type - a functor that represents optional values
     * Equivalent to Haskell's Maybe type
     */
    public static final class Maybe<T> {
        private final T value;
        private final boolean isPresent;

        private Maybe(T value, boolean isPresent) {
            this.value = value;
            this.isPresent = isPresent;
        }

        /**
         * Create a Maybe containing a value (Just constructor)
         */
        public static <T> Maybe<T> just(T value) {
            Objects.requireNonNull(value, "Value cannot be null for Just");
            return new Maybe<>(value, true);
        }

        /**
         * Create an empty Maybe (Nothing constructor)
         */
        public static <T> Maybe<T> nothing() {
            return new Maybe<>(null, false);
        }

        /**
         * Check if this Maybe contains a value
         */
        public boolean isJust() {
            return isPresent;
        }

        /**
         * Check if this Maybe is empty
         */
        public boolean isNothing() {
            return !isPresent;
        }

        /**
         * Get the value if present, throws exception if empty
         */
        public T getValue() {
            if (!isPresent) {
                throw new IllegalStateException("Cannot get value from Nothing");
            }
            return value;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Maybe<?> maybe = (Maybe<?>) obj;
            return isPresent == maybe.isPresent &&
                   Objects.equals(value, maybe.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value, isPresent);
        }

        @Override
        public String toString() {
            return isPresent ? "Just(" + value + ")" : "Nothing";
        }
    }

    /**
     * Functor map operation for Maybe type
     * fmap :: (a -> b) -> Maybe a -> Maybe b
     *
     * @param f function to apply
     * @param maybe the Maybe to map over
     * @return new Maybe with function applied, or Nothing if input was Nothing
     */
    public <A, B> Maybe<B> mapMaybe(Function<A, B> f, Maybe<A> maybe) {
        Objects.requireNonNull(f, "Function cannot be null");
        Objects.requireNonNull(maybe, "Maybe cannot be null");

        if (maybe.isNothing()) {
            return Maybe.nothing();
        }

        return Maybe.just(f.apply(maybe.getValue()));
    }

    /**
     * Functor map operation for List type
     * fmap :: (a -> b) -> [a] -> [b]
     *
     * @param f function to apply to each element
     * @param list the list to map over
     * @return new list with function applied to each element
     */
    public <A, B> List<B> mapList(Function<A, B> f, List<A> list) {
        Objects.requireNonNull(f, "Function cannot be null");
        Objects.requireNonNull(list, "List cannot be null");

        return list.stream()
                  .map(f)
                  .toList();
    }

    /**
     * Total function - sum all Just values in a list of Maybes
     * Returns Nothing if any element is Nothing, otherwise returns Just of the sum
     *
     * This demonstrates working with functors in a practical context
     *
     * @param maybes list of Maybe integers
     * @return Maybe containing the sum, or Nothing if any input is Nothing
     */
    public Maybe<Integer> total(List<Maybe<Integer>> maybes) {
        Objects.requireNonNull(maybes, "List cannot be null");

        if (maybes.isEmpty()) {
            return Maybe.just(0);
        }

        int sum = 0;
        for (Maybe<Integer> maybe : maybes) {
            if (maybe.isNothing()) {
                return Maybe.nothing();
            }
            sum += maybe.getValue();
        }

        return Maybe.just(sum);
    }

    /**
     * Alternative functional implementation of total using streams and reduce
     */
    public Maybe<Integer> totalFunctional(List<Maybe<Integer>> maybes) {
        Objects.requireNonNull(maybes, "List cannot be null");

        if (maybes.isEmpty()) {
            return Maybe.just(0);
        }

        // Check if any element is Nothing
        boolean hasNothing = maybes.stream().anyMatch(Maybe::isNothing);
        if (hasNothing) {
            return Maybe.nothing();
        }

        // Sum all values
        int sum = maybes.stream()
                       .mapToInt(maybe -> maybe.getValue())
                       .sum();

        return Maybe.just(sum);
    }
}
