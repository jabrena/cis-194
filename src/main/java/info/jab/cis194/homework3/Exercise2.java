package info.jab.cis194.homework3;

import java.util.ArrayList;
import java.util.List;

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

        List<T> result = new ArrayList<>();

        // Need at least 3 elements to have a local maximum
        if (input.size() < 3) {
            return result;
        }

        // Check each element from index 1 to size-2 (elements that have both neighbors)
        for (int i = 1; i < input.size() - 1; i++) {
            T current = input.get(i);
            T previous = input.get(i - 1);
            T next = input.get(i + 1);

            // Check if current is strictly greater than both neighbors
            if (current.compareTo(previous) > 0 && current.compareTo(next) > 0) {
                result.add(current);
            }
        }

        return result;
    }
}
