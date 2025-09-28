package info.jab.cis194.homework4;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Exercise 1: Wholemeal Programming
 *
 * The goal of this exercise is to rewrite the following function in a more idiomatic,
 * "wholemeal" way using higher-order functions.
 *
 * Original function (in Haskell-like pseudocode):
 * fun1 [] = []
 * fun1 (x:xs)
 *   | even x    = (x - 1) : fun1 xs
 *   | otherwise = (3 * x + 1) : fun1 xs
 *
 * This function takes a list of integers and applies a transformation to each element:
 * - If the number is even, subtract 1
 * - If the number is odd, multiply by 3 and add 1
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
     * fun1 - Stack-safe recursive version using trampoline
     *
     * Takes a list of integers and transforms each element:
     * - If even: subtract 1
     * - If odd: multiply by 3 and add 1
     *
     * @param input the input list of integers
     * @return a new list with transformed elements
     * @throws IllegalArgumentException if input is null
     */
    public static List<Integer> fun1(List<Integer> input) {
        if (input == null) {
            throw new IllegalArgumentException("Input list cannot be null");
        }

        return transformWithTrampoline(input, 0, List.of()).evaluate();
    }

    /**
     * fun2 - Wholemeal/functional version using higher-order functions
     *
     * Same functionality as fun1 but implemented using functional programming
     * concepts and higher-order functions (map).
     *
     * @param input the input list of integers
     * @return a new list with transformed elements
     * @throws IllegalArgumentException if input is null
     */
    public static List<Integer> fun2(List<Integer> input) {
        if (input == null) {
            throw new IllegalArgumentException("Input list cannot be null");
        }

        return input.stream()
            .map(Exercise1::transformElement)
            .toList();
    }

    /**
     * Stack-safe recursive transformation using trampoline
     */
    private static Trampoline<List<Integer>> transformWithTrampoline(List<Integer> input, int index, List<Integer> acc) {
        if (index >= input.size()) {
            return Trampoline.complete(acc);
        }

        Integer transformed = transformElement(input.get(index));
        List<Integer> newAcc = java.util.stream.Stream.concat(
            acc.stream(),
            java.util.stream.Stream.of(transformed)
        ).toList();

        return Trampoline.more(() -> transformWithTrampoline(input, index + 1, newAcc));
    }

    /**
     * Alternative functional approach using fold
     */
    public static List<Integer> fun1Fold(List<Integer> input) {
        if (input == null) {
            throw new IllegalArgumentException("Input list cannot be null");
        }

        return input.stream()
            .reduce(
                List.<Integer>of(),
                (acc, element) -> java.util.stream.Stream.concat(
                    acc.stream(),
                    java.util.stream.Stream.of(transformElement(element))
                ).toList(),
                (list1, list2) -> java.util.stream.Stream.concat(
                    list1.stream(),
                    list2.stream()
                ).toList()
            );
    }

    /**
     * Transform a single element according to the rule:
     * - If even: subtract 1
     * - If odd: multiply by 3 and add 1
     */
    private static Integer transformElement(Integer x) {
        return (x % 2 == 0) ? x - 1 : 3 * x + 1;
    }

    /**
     * Higher-order function approach - returns a transformation function
     */
    public static Function<Integer, Integer> getTransformFunction() {
        return x -> (x % 2 == 0) ? x - 1 : 3 * x + 1;
    }

    /**
     * Functional composition approach using method references and function composition
     */
    public static List<Integer> fun2Compose(List<Integer> input) {
        if (input == null) {
            throw new IllegalArgumentException("Input list cannot be null");
        }

        Function<Integer, Integer> transform = getTransformFunction();
        return input.stream()
            .map(transform)
            .toList();
    }

    /**
     * Point-free style implementation
     */
    public static Function<List<Integer>, List<Integer>> createTransformer() {
        return list -> list.stream()
            .map(getTransformFunction())
            .toList();
    }

    /**
     * Curried function approach
     */
    public static Function<List<Integer>, Function<Function<Integer, Integer>, List<Integer>>> curriedTransform() {
        return list -> transformer -> list.stream()
            .map(transformer)
            .toList();
    }
}
