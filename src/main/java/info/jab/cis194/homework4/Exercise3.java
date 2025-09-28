package info.jab.cis194.homework4;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Exercise 3: More Folds
 *
 * This exercise implements more functions using folds, specifically:
 * 1. xor - implements exclusive OR over a list of Boolean values
 * 2. map - implements map using foldr
 *
 * The goal is to demonstrate how fundamental operations can be implemented
 * using folds, which are a powerful abstraction in functional programming.
 */
public class Exercise3 {

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
     * Implements exclusive OR (XOR) over a list of Boolean values using a fold.
     *
     * XOR returns true if and only if an odd number of the inputs are true.
     * This is equivalent to checking if the number of true values is odd.
     *
     * Examples:
     * xor([]) = false
     * xor([false]) = false
     * xor([true]) = true
     * xor([true, false]) = true
     * xor([true, true]) = false
     * xor([true, true, true]) = true
     *
     * @param values the list of Boolean values to XOR
     * @return true if an odd number of values are true, false otherwise
     * @throws IllegalArgumentException if the input list is null
     */
    public static Boolean xor(List<Boolean> values) {
        if (values == null) {
            throw new IllegalArgumentException("Input list cannot be null");
        }

        return values.stream()
            .reduce(false, (acc, value) -> acc ^ value);
    }

    /**
     * Implements map using a fold (reduce) operation.
     *
     * Map applies a function to each element of a list and returns a new list
     * containing the results. This implementation uses foldr (right fold)
     * to build the result list.
     *
     * @param <T> the type of elements in the input list
     * @param <R> the type of elements in the output list
     * @param function the function to apply to each element
     * @param values the input list
     * @return a new list with the function applied to each element
     * @throws IllegalArgumentException if the function or input list is null
     */
    public static <T, R> List<R> map(Function<T, R> function, List<T> values) {
        if (function == null) {
            throw new IllegalArgumentException("Function cannot be null");
        }
        if (values == null) {
            throw new IllegalArgumentException("Input list cannot be null");
        }

        // Use foldr (right fold) to build the result list
        // We need to process from right to left to maintain order
        return foldr(
            (element, acc) -> prepend(function.apply(element), acc),
            List.of(),
            values
        );
    }

    /**
     * Implements foldr (right fold) - processes elements from right to left using trampoline.
     *
     * @param <T> the type of elements in the list
     * @param <R> the type of the result
     * @param function the binary function to apply
     * @param initial the initial value
     * @param values the list to fold
     * @return the result of folding the list
     */
    private static <T, R> R foldr(BiFunction<T, R, R> function, R initial, List<T> values) {
        return foldrTrampoline(function, initial, values).evaluate();
    }

    /**
     * Stack-safe foldr implementation using trampoline
     * For simplicity, we'll use the iterative approach which is inherently stack-safe
     */
    private static <T, R> Trampoline<R> foldrTrampoline(BiFunction<T, R, R> function, R initial, List<T> values) {
        return Trampoline.complete(foldrIterative(function, initial, values));
    }

    /**
     * Alternative functional implementation using iterative approach with streams
     */
    public static <T, R> R foldrIterative(BiFunction<T, R, R> function, R initial, List<T> values) {
        return values.reversed().stream()
            .reduce(initial, (acc, element) -> function.apply(element, acc), (a, b) -> b);
    }

    /**
     * Prepends an element to the front of a list.
     *
     * @param <T> the type of elements in the list
     * @param element the element to prepend
     * @param list the list to prepend to
     * @return a new list with the element prepended
     */
    private static <T> List<T> prepend(T element, List<T> list) {
        return java.util.stream.Stream.concat(
            java.util.stream.Stream.of(element),
            list.stream()
        ).toList();
    }

    /**
     * Higher-order function that creates an XOR function for any type with a predicate
     */
    public static <T> Function<List<T>, Boolean> createXorFunction(Function<T, Boolean> predicate) {
        return list -> list.stream()
            .map(predicate)
            .reduce(false, (acc, value) -> acc ^ value);
    }

    /**
     * Curried map function for partial application
     */
    public static <T, R> Function<List<T>, List<R>> curriedMap(Function<T, R> function) {
        return values -> map(function, values);
    }

    /**
     * Function composition helper for chaining transformations
     */
    public static <T, R, S> Function<List<T>, List<S>> composeTransformations(
            Function<T, R> first,
            Function<R, S> second) {
        return list -> map(first.andThen(second), list);
    }

    /**
     * Alternative map implementation using parallel streams for large datasets
     */
    public static <T, R> List<R> mapParallel(Function<T, R> function, List<T> values) {
        if (function == null) {
            throw new IllegalArgumentException("Function cannot be null");
        }
        if (values == null) {
            throw new IllegalArgumentException("Input list cannot be null");
        }

        return values.parallelStream()
            .map(function)
            .toList();
    }
}
