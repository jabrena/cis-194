package info.jab.cis194.homework3;

import java.util.List;
import java.util.stream.IntStream;

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

        return input.isEmpty()
            ? List.of()
            : IntStream.range(0, input.size())
                .mapToObj(i -> takeEveryNth(input, i + 1, i))
                .toList();
    }

    /**
     * Takes every nth element from the input list starting at the given offset.
     *
     * @param <T> the type of elements in the list
     * @param input the input list
     * @param step the step size (how many elements to skip)
     * @param startIndex the starting index
     * @return a list containing every nth element
     */
    private static <T> List<T> takeEveryNth(List<T> input, int step, int startIndex) {
        return IntStream.iterate(startIndex, i -> i < input.size(), i -> i + step)
            .mapToObj(input::get)
            .toList();
    }
}
