package info.jab.cis194.homework3;

import java.util.ArrayList;
import java.util.List;

/**
 * Exercise 1: Skips Function
 *
 * Write a function skips :: [a] -> [[a]] which returns a list of lists.
 * The first list in the output should be the same as the input list.
 * The second list in the output should contain every second element from the input list.
 * The third list in the output should contain every third element from the input list.
 * And so on...
 *
 * For example:
 * skips("ABCD") == [["A","B","C","D"], ["B","D"], ["C"], ["D"]]
 * skips("hello!") == [["h","e","l","l","o","!"], ["e","l","o"], ["l","!"], ["l"], ["o"], ["!"]]
 * skips([1]) == [[1]]
 * skips([]) == []
 */
public class Exercise1 {

    /**
     * Returns a list of lists where the nth list contains every nth element
     * from the input list, starting from the nth position (1-indexed).
     *
     * @param <T> the type of elements in the list
     * @param input the input list
     * @return a list of lists containing the skipped elements
     * @throws IllegalArgumentException if input is null
     */
    public static <T> List<List<T>> skips(List<T> input) {
        if (input == null) {
            throw new IllegalArgumentException("Input list cannot be null");
        }

        List<List<T>> result = new ArrayList<>();

        // If input is empty, return empty list
        if (input.isEmpty()) {
            return result;
        }

        // For each position i (0-indexed), create a list that starts at position i
        // and takes every (i+1)th element
        for (int i = 0; i < input.size(); i++) {
            List<T> sublist = new ArrayList<>();

            // Start at position i, then skip (i+1) elements each time
            for (int j = i; j < input.size(); j += (i + 1)) {
                sublist.add(input.get(j));
            }

            result.add(sublist);
        }

        return result;
    }
}
