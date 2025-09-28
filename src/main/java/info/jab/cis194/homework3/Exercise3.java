package info.jab.cis194.homework3;

import java.util.List;

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

        // Count occurrences of each digit 0-9
        int[] counts = new int[10];
        for (Integer digit : input) {
            if (digit >= 0 && digit <= 9) {
                counts[digit]++;
            }
        }

        // Find the maximum count to determine height of histogram
        int maxCount = 0;
        for (int count : counts) {
            maxCount = Math.max(maxCount, count);
        }

        StringBuilder result = new StringBuilder();

        // Build histogram from top to bottom
        for (int row = maxCount; row >= 1; row--) {
            StringBuilder line = new StringBuilder();
            for (int col = 0; col < 10; col++) {
                if (counts[col] >= row) {
                    line.append("*");
                } else {
                    line.append(" ");
                }
            }
            // Trim trailing spaces from the line but keep internal spaces
            String lineStr = line.toString().replaceAll("\\s+$", "");
            result.append(lineStr).append("\n");
        }

        // Add the separator line
        result.append("==========\n");

        // Add the digit labels
        result.append("0123456789\n");

        return result.toString();
    }
}
