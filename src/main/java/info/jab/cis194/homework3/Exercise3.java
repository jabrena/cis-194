package info.jab.cis194.homework3;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Exercise 3: Histogram Function
 *
 * Write a function histogram :: [Integer] -> String which takes as input a list of Integers
 * between 0 and 9 (inclusive), and outputs a vertical histogram showing how many of each
 * number were in the input list.
 *
 * For example:
 * histogram([1,1,1,5]) should produce:
 *  *  *
 *  *  *
 *  *  *
 * ==========
 * 0123456789
 *
 * histogram([1,4,5,4,6,6,3,4,2,4,9]) should produce:
 *     *
 *     *   *
 *     **  *
 *  *  ***  *
 * ==========
 * 0123456789
 */
public class Exercise3 {

    /**
     * Creates a vertical histogram showing the frequency of digits 0-9 in the input list.
     * The histogram shows asterisks (*) stacked vertically to represent the count of each digit.
     * Digits outside the range 0-9 are ignored.
     *
     * @param input the list of integers to create a histogram for
     * @return a string representation of the histogram
     * @throws IllegalArgumentException if input is null
     */
    public static String histogram(List<Integer> input) {
        if (input == null) {
            throw new IllegalArgumentException("Input list cannot be null");
        }

        // Count occurrences of each digit 0-9 using functional approach
        List<Long> counts = IntStream.range(0, 10)
            .mapToObj(digit -> input.stream()
                .filter(n -> n != null && n == digit)
                .count())
            .toList();

        // Find the maximum count to determine height of histogram
        long maxCount = counts.stream()
            .mapToLong(Long::longValue)
            .max()
            .orElse(0);

        // Build histogram from top to bottom using functional approach
        String histogramLines = IntStream.rangeClosed(1, (int) maxCount)
            .map(row -> (int) maxCount - row + 1) // Reverse to go from top to bottom
            .mapToObj(row -> buildHistogramLine(counts, row))
            .collect(Collectors.joining("\n"));

        return histogramLines.isEmpty()
            ? "==========\n0123456789\n"
            : histogramLines + "\n==========\n0123456789\n";
    }

    /**
     * Builds a single line of the histogram for the given row.
     *
     * @param counts the counts for each digit 0-9
     * @param row the row number (1-indexed from bottom)
     * @return the histogram line as a string
     */
    private static String buildHistogramLine(List<Long> counts, int row) {
        return counts.stream()
            .map(count -> count >= row ? "*" : " ")
            .collect(Collectors.joining())
            .replaceAll("\\s+$", ""); // Trim trailing spaces
    }
}
