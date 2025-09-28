package info.jab.cis194.homework1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Exercise 1 - Credit Card Validation
 * Implementation of credit card validation algorithm based on CIS-194 Homework 1
 */
public class Exercise1 {

    /**
     * Convert a positive integer to a list of its digits
     * @param n the number to convert
     * @return list of digits, or empty list if n is negative
     */
    public List<Integer> toDigits(long n) {
        if (n < 0) {
            return new ArrayList<>();
        }

        if (n == 0) {
            return List.of(0);
        }

        List<Integer> digits = new ArrayList<>();
        while (n > 0) {
            digits.add((int) (n % 10));
            n /= 10;
        }

        Collections.reverse(digits);
        return digits;
    }

    /**
     * Convert a positive integer to a list of its digits in reverse order
     * @param n the number to convert
     * @return list of digits in reverse order, or empty list if n is negative
     */
    public List<Integer> toDigitsRev(long n) {
        if (n < 0) {
            return new ArrayList<>();
        }

        if (n == 0) {
            return List.of(0);
        }

        List<Integer> digits = new ArrayList<>();
        while (n > 0) {
            digits.add((int) (n % 10));
            n /= 10;
        }

        return digits;
    }

    /**
     * Double every other element starting from the right (second-to-last, fourth-to-last, etc.)
     * @param digits list of digits
     * @return list with every other element doubled from the right
     */
    public List<Integer> doubleEveryOther(List<Integer> digits) {
        List<Integer> result = new ArrayList<>(digits);

        // Special case: single element list should not be doubled
        if (result.size() <= 1) {
            return result;
        }

        // The pattern for multi-element lists:
        // Even length: start doubling from index 1 (second element)
        // Odd length: start doubling from index 0 (first element)
        int start = (result.size() % 2 == 0) ? 1 : 0;

        for (int i = start; i < result.size(); i += 2) {
            result.set(i, result.get(i) * 2);
        }

        return result;
    }

    /**
     * Sum all digits in a list, treating multi-digit numbers by summing their digits
     * @param digits list of integers
     * @return sum of all digits
     */
    public int sumDigits(List<Integer> digits) {
        int sum = 0;
        for (int num : digits) {
            while (num > 0) {
                sum += num % 10;
                num /= 10;
            }
        }
        return sum;
    }

    /**
     * Validate a credit card number using Luhn algorithm
     * @param cardNumber the credit card number to validate
     * @return true if valid, false otherwise
     */
    public boolean validate(long cardNumber) {
        List<Integer> digits = toDigitsRev(cardNumber);
        List<Integer> doubled = doubleEveryOther(digits);
        int sum = sumDigits(doubled);
        return sum % 10 == 0;
    }
}
