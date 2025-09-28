package info.jab.cis194.homework4;

import java.util.List;

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
     * fun1 - Imperative/recursive version
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

        return input.isEmpty()
            ? List.of()
            : transformRecursively(input, 0);
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
     * Helper method for recursive implementation
     */
    private static List<Integer> transformRecursively(List<Integer> input, int index) {
        if (index >= input.size()) {
            return List.of();
        }

        Integer transformed = transformElement(input.get(index));
        List<Integer> rest = transformRecursively(input, index + 1);

        return java.util.stream.Stream.concat(
            java.util.stream.Stream.of(transformed),
            rest.stream()
        ).toList();
    }

    /**
     * Transform a single element according to the rule:
     * - If even: subtract 1
     * - If odd: multiply by 3 and add 1
     */
    private static Integer transformElement(Integer x) {
        return (x % 2 == 0) ? x - 1 : 3 * x + 1;
    }
}
