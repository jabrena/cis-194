package info.jab.cis194.homework3;

import java.util.List;
import java.util.stream.IntStream;

/**
 * Exercise 2: Local Maxima Function
 *
 * A local maximum is an element that is strictly greater than both its neighbors.
 * Write a function localMaxima :: [a] -> [a] which finds all the local maxima in the input list.
 *
 * For example:
 * localMaxima([2,9,5,6,1]) == [9,6]
 * localMaxima([2,3,4,1,5]) == [4]
 * localMaxima([1,2,3,4,5]) == []
 */
public class Exercise2 {

    /**
     * Finds all local maxima in the input list.
     * A local maximum is an element that is strictly greater than both its neighbors.
     * Elements at the beginning or end of the list cannot be local maxima since they
     * don't have two neighbors.
     *
     * @param <T> the type of elements in the list, must be Comparable
     * @param input the input list
     * @return a list containing all local maxima in the same order as they appear in the input
     * @throws IllegalArgumentException if input is null
     */
    public static <T extends Comparable<T>> List<T> localMaxima(List<T> input) {
        if (input == null) {
            throw new IllegalArgumentException("Input list cannot be null");
        }

        return input.size() < 3
            ? List.of()
            : IntStream.range(1, input.size() - 1)
                .filter(i -> isLocalMaximum(input, i))
                .mapToObj(input::get)
                .toList();
    }

    /**
     * Checks if the element at the given index is a local maximum.
     *
     * @param <T> the type of elements in the list
     * @param input the input list
     * @param index the index to check
     * @return true if the element is a local maximum, false otherwise
     */
    private static <T extends Comparable<T>> boolean isLocalMaximum(List<T> input, int index) {
        T current = input.get(index);
        T previous = input.get(index - 1);
        T next = input.get(index + 1);

        return current.compareTo(previous) > 0 && current.compareTo(next) > 0;
    }
}
