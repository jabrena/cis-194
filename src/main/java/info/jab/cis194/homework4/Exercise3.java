package info.jab.cis194.homework4;

import java.util.List;
import java.util.function.Function;

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
     * Implements foldr (right fold) - processes elements from right to left.
     *
     * @param <T> the type of elements in the list
     * @param <R> the type of the result
     * @param function the binary function to apply
     * @param initial the initial value
     * @param values the list to fold
     * @return the result of folding the list
     */
    private static <T, R> R foldr(java.util.function.BiFunction<T, R, R> function, R initial, List<T> values) {
        if (values.isEmpty()) {
            return initial;
        }

        // For right fold, we need to process the tail first, then apply function to head
        T head = values.get(0);
        List<T> tail = values.subList(1, values.size());

        return function.apply(head, foldr(function, initial, tail));
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
}
