package info.jab.cis194.homework11;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Exercise 1: Advanced Monadic Operations and Functional Composition
 *
 * <p>This exercise implements advanced monadic operations including custom monad implementations,
 * function composition patterns, Kleisli composition, and monadic utilities like sequence and traverse.
 * These concepts demonstrate deep understanding of functional programming patterns.
 *
 * <p>Based on CIS-194 Week 11: Advanced Functional Programming Patterns
 */
public class Exercise1 {

    /**
     * Maybe Monad implementation - represents a value that might be present (Just) or absent (Nothing)
     *
     * @param <T> the type of the value that might be present
     */
    public static abstract class Maybe<T> {

        /**
         * Creates a Maybe containing the given value
         *
         * @param <T> the type of the value
         * @param value the value to wrap
         * @return a Maybe containing the value
         */
        public static <T> Maybe<T> pure(T value) {
            return new Just<>(value);
        }

        /**
         * Creates an empty Maybe (Nothing)
         *
         * @param <T> the type parameter
         * @return an empty Maybe
         */
        public static <T> Maybe<T> nothing() {
            return new Nothing<>();
        }

        /**
         * Checks if this Maybe contains a value
         *
         * @return true if this is a Just, false if Nothing
         */
        public abstract boolean isJust();

        /**
         * Checks if this Maybe is empty
         *
         * @return true if this is Nothing, false if Just
         */
        public boolean isNothing() {
            return !isJust();
        }

        /**
         * Gets the value if present, throws exception if Nothing
         *
         * @return the contained value
         * @throws IllegalStateException if this is Nothing
         */
        public abstract T getValue();

        /**
         * Functor map operation - transforms the contained value if present
         *
         * @param <U> the target type
         * @param f the transformation function
         * @return a new Maybe with the transformed value, or Nothing if this is Nothing
         */
        public abstract <U> Maybe<U> fmap(Function<T, U> f);

        /**
         * Monadic bind operation - applies a function that returns a Maybe
         *
         * @param <U> the target type
         * @param f the monadic function
         * @return the result of applying f to the contained value, or Nothing if this is Nothing
         */
        public abstract <U> Maybe<U> bind(Function<T, Maybe<U>> f);
    }

    /**
     * Just case - represents a Maybe that contains a value
     */
    private static class Just<T> extends Maybe<T> {
        private final T value;

        public Just(T value) {
            this.value = Objects.requireNonNull(value);
        }

        @Override
        public boolean isJust() {
            return true;
        }

        @Override
        public T getValue() {
            return value;
        }

        @Override
        public <U> Maybe<U> fmap(Function<T, U> f) {
            return new Just<>(f.apply(value));
        }

        @Override
        public <U> Maybe<U> bind(Function<T, Maybe<U>> f) {
            return f.apply(value);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Just<?> just)) return false;
            return Objects.equals(value, just.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }
    }

    /**
     * Nothing case - represents an empty Maybe
     */
    private static class Nothing<T> extends Maybe<T> {
        @Override
        public boolean isJust() {
            return false;
        }

        @Override
        public T getValue() {
            throw new IllegalStateException("Nothing has no value");
        }

        @Override
        public <U> Maybe<U> fmap(Function<T, U> f) {
            return new Nothing<>();
        }

        @Override
        public <U> Maybe<U> bind(Function<T, Maybe<U>> f) {
            return new Nothing<>();
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Nothing<?>;
        }

        @Override
        public int hashCode() {
            return 0;
        }
    }

    /**
     * List Monad implementation - represents a list with monadic operations
     *
     * @param <T> the type of elements in the list
     */
    public static class ListMonad<T> {
        private final List<T> values;

        private ListMonad(List<T> values) {
            this.values = List.copyOf(values);
        }

        /**
         * Creates a ListMonad containing a single value
         *
         * @param <T> the type of the value
         * @param value the value to wrap
         * @return a ListMonad containing the single value
         */
        public static <T> ListMonad<T> pure(T value) {
            return new ListMonad<>(List.of(value));
        }

        /**
         * Creates a ListMonad from a list of values
         *
         * @param <T> the type of the values
         * @param values the values to wrap
         * @return a ListMonad containing the values
         */
        public static <T> ListMonad<T> of(List<T> values) {
            return new ListMonad<>(values);
        }

        /**
         * Gets the contained values
         *
         * @return the list of values
         */
        public List<T> getValues() {
            return values; // Already immutable from List.copyOf
        }

        /**
         * Functor map operation - transforms each value in the list
         *
         * @param <U> the target type
         * @param f the transformation function
         * @return a new ListMonad with transformed values
         */
        public <U> ListMonad<U> fmap(Function<T, U> f) {
            return new ListMonad<>(values.stream().map(f).toList());
        }

        /**
         * Monadic bind operation - applies a function that returns a ListMonad to each element
         * and flattens the results
         *
         * @param <U> the target type
         * @param f the monadic function
         * @return a new ListMonad with the flattened results
         */
        public <U> ListMonad<U> bind(Function<T, ListMonad<U>> f) {
            List<U> result = values.stream()
                .map(f)
                .flatMap(listMonad -> listMonad.getValues().stream())
                .toList();
            return new ListMonad<>(result);
        }
    }

    /**
     * Composes two functions: (g ∘ f)(x) = g(f(x))
     *
     * @param <A> the input type
     * @param <B> the intermediate type
     * @param <C> the output type
     * @param g the second function to apply
     * @param f the first function to apply
     * @return the composed function
     */
    public static <A, B, C> Function<A, C> compose(Function<B, C> g, Function<A, B> f) {
        return x -> g.apply(f.apply(x));
    }

    /**
     * Creates a pipeline of three functions applied in sequence
     *
     * @param <A> the input type
     * @param <B> the first intermediate type
     * @param <C> the second intermediate type
     * @param <D> the output type
     * @param f the first function
     * @param g the second function
     * @param h the third function
     * @return the composed pipeline function
     */
    public static <A, B, C, D> Function<A, D> pipe(Function<A, B> f, Function<B, C> g, Function<C, D> h) {
        return x -> h.apply(g.apply(f.apply(x)));
    }

    /**
     * Applies a function n times to a value
     *
     * @param <T> the type of the value
     * @param f the function to apply
     * @param n the number of times to apply the function
     * @param initial the initial value
     * @return the result after applying the function n times
     */
    public static <T> T applyN(Function<T, T> f, int n, T initial) {
        return Trampoline.execute(applyNTrampoline(f, n, initial));
    }

    /**
     * Trampoline interface for tail-recursive optimization
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

        static <T> Trampoline<T> complete(T value) {
            return new Trampoline<T>() {
                @Override
                public boolean isComplete() {
                    return true;
                }

                @Override
                public T result() {
                    return value;
                }

                @Override
                public Trampoline<T> apply() {
                    throw new UnsupportedOperationException("Already completed");
                }
            };
        }

        static <T> T execute(Trampoline<T> trampoline) {
            while (!trampoline.isComplete()) {
                trampoline = trampoline.apply();
            }
            return trampoline.result();
        }
    }

    /**
     * Tail-recursive helper for applyN using trampoline
     */
    private static <T> Trampoline<T> applyNTrampoline(Function<T, T> f, int n, T current) {
        return n <= 0
            ? Trampoline.complete(current)
            : () -> applyNTrampoline(f, n - 1, f.apply(current));
    }

    /**
     * Kleisli composition for Maybe monads: composes two functions that return Maybe values
     *
     * @param <A> the input type
     * @param <B> the intermediate type
     * @param <C> the output type
     * @param g the second monadic function
     * @param f the first monadic function
     * @return the composed monadic function
     */
    public static <A, B, C> Function<A, Maybe<C>> kleisliCompose(
            Function<B, Maybe<C>> g, Function<A, Maybe<B>> f) {
        return x -> f.apply(x).bind(g);
    }

    /**
     * Sequences a list of Maybe values into a Maybe containing a list
     * Returns Nothing if any of the input Maybe values is Nothing
     *
     * @param <T> the type of the values
     * @param maybes the list of Maybe values
     * @return Maybe containing a list of all values, or Nothing if any input is Nothing
     */
    public static <T> Maybe<List<T>> sequenceMaybe(List<Maybe<T>> maybes) {
        return maybes.stream()
            .filter(Maybe::isNothing)
            .findAny()
            .map(nothing -> Maybe.<List<T>>nothing())
            .orElseGet(() -> Maybe.pure(
                maybes.stream()
                    .map(Maybe::getValue)
                    .toList()
            ));
    }

    /**
     * Traverses a list with a function that returns Maybe values
     * Applies the function to each element and sequences the results
     *
     * @param <A> the input type
     * @param <B> the output type
     * @param list the input list
     * @param f the function that returns Maybe values
     * @return Maybe containing a list of results, or Nothing if any function application fails
     */
    public static <A, B> Maybe<List<B>> traverseMaybe(List<A> list, Function<A, Maybe<B>> f) {
        var maybes = list.stream().map(f).toList();
        return sequenceMaybe(maybes);
    }
}
