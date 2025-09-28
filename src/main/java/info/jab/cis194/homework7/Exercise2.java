package info.jab.cis194.homework7;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Function;

/**
 * Exercise 2: Monoids and Monoid Laws
 *
 * A monoid is an algebraic structure consisting of:
 * 1. A set of elements
 * 2. An associative binary operation (mappend)
 * 3. An identity element (mempty)
 *
 * Monoid laws:
 * 1. Associativity: (a <> b) <> c = a <> (b <> c)
 * 2. Left identity: mempty <> a = a
 * 3. Right identity: a <> mempty = a
 */
public class Exercise2 {

    /**
     * Monoid interface defining the operations every monoid must have
     */
    public interface Monoid<T> {
        /**
         * The identity element of the monoid
         */
        T mempty();

        /**
         * The associative binary operation
         */
        T mappend(T a, T b);

        /**
         * Convert this monoid to a BinaryOperator for use with streams
         */
        default BinaryOperator<T> asBinaryOperator() {
            return this::mappend;
        }

        /**
         * Create a function that appends a fixed value to the left
         */
        default Function<T, T> appendLeft(T value) {
            return other -> mappend(value, other);
        }

        /**
         * Create a function that appends a fixed value to the right
         */
        default Function<T, T> appendRight(T value) {
            return other -> mappend(other, value);
        }

        /**
         * Compose this monoid with a function to create a new monoid
         */
        default <U> Monoid<U> compose(Function<U, T> f, Function<T, U> g) {
            return new Monoid<U>() {
                @Override
                public U mempty() {
                    return g.apply(Monoid.this.mempty());
                }

                @Override
                public U mappend(U a, U b) {
                    return g.apply(Monoid.this.mappend(f.apply(a), f.apply(b)));
                }
            };
        }
    }

    /**
     * Sum monoid for integers - addition with 0 as identity
     */
    public static class SumMonoid implements Monoid<Integer> {
        @Override
        public Integer mempty() {
            return 0;
        }

        @Override
        public Integer mappend(Integer a, Integer b) {
            return a + b;
        }
    }

    /**
     * Product monoid for integers - multiplication with 1 as identity
     */
    public static class ProductMonoid implements Monoid<Integer> {
        @Override
        public Integer mempty() {
            return 1;
        }

        @Override
        public Integer mappend(Integer a, Integer b) {
            return a * b;
        }
    }

    /**
     * Boolean AND monoid - logical AND with true as identity
     */
    public static class BooleanAndMonoid implements Monoid<Boolean> {
        @Override
        public Boolean mempty() {
            return true;
        }

        @Override
        public Boolean mappend(Boolean a, Boolean b) {
            return a && b;
        }
    }

    /**
     * Boolean OR monoid - logical OR with false as identity
     */
    public static class BooleanOrMonoid implements Monoid<Boolean> {
        @Override
        public Boolean mempty() {
            return false;
        }

        @Override
        public Boolean mappend(Boolean a, Boolean b) {
            return a || b;
        }
    }

    /**
     * List monoid - list concatenation with empty list as identity
     */
    public static class ListMonoid<T> implements Monoid<List<T>> {
        @Override
        public List<T> mempty() {
            return new ArrayList<>();
        }

        @Override
        public List<T> mappend(List<T> a, List<T> b) {
            List<T> result = new ArrayList<>(a);
            result.addAll(b);
            return result;
        }
    }

    /**
     * Concatenate a list of monoid values using the monoid operation
     * This is the generalized fold operation for monoids
     */
    public static <T> T mconcat(Monoid<T> monoid, List<T> values) {
        T result = monoid.mempty();
        for (T value : values) {
            result = monoid.mappend(result, value);
        }
        return result;
    }

    /**
     * Alternative implementation of mconcat using reduce pattern
     */
    public static <T> T mconcatReduce(Monoid<T> monoid, List<T> values) {
        return values.stream()
                    .reduce(monoid.mempty(), monoid::mappend);
    }

    /**
     * Fold a list using a monoid - same as mconcat but more explicit name
     */
    public static <T> T foldMonoid(Monoid<T> monoid, List<T> values) {
        return mconcat(monoid, values);
    }

    /**
     * Functional approach using Optional for safe monoid operations
     */
    public static <T> Optional<T> mconcatSafe(Monoid<T> monoid, List<T> values) {
        return values.isEmpty() ?
            Optional.empty() :
            Optional.of(mconcat(monoid, values));
    }

    /**
     * Create a curried version of mconcat
     */
    public static <T> Function<List<T>, T> curriedMconcat(Monoid<T> monoid) {
        return values -> mconcat(monoid, values);
    }

    /**
     * Lift a binary operation to work with Optional values
     */
    public static <T> BinaryOperator<Optional<T>> liftOptional(Monoid<T> monoid) {
        return (opt1, opt2) -> {
            if (opt1.isEmpty()) return opt2;
            if (opt2.isEmpty()) return opt1;
            return Optional.of(monoid.mappend(opt1.get(), opt2.get()));
        };
    }

    /**
     * Create a monoid for Optional values
     */
    public static <T> Monoid<Optional<T>> optionalMonoid(Monoid<T> innerMonoid) {
        return new Monoid<Optional<T>>() {
            @Override
            public Optional<T> mempty() {
                return Optional.empty();
            }

            @Override
            public Optional<T> mappend(Optional<T> a, Optional<T> b) {
                return liftOptional(innerMonoid).apply(a, b);
            }
        };
    }

    /**
     * Parallel mconcat for better performance with large lists
     */
    public static <T> T mconcatParallel(Monoid<T> monoid, List<T> values) {
        return values.parallelStream()
                    .reduce(monoid.mempty(), monoid::mappend);
    }

    /**
     * Create a function that maps over a list and then concatenates using a monoid
     */
    public static <T, U> Function<List<T>, U> mapThenConcat(Function<T, U> mapper, Monoid<U> monoid) {
        return list -> list.stream()
                          .map(mapper)
                          .reduce(monoid.mempty(), monoid::mappend);
    }
}
