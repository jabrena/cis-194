package info.jab.cis194.homework1;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Exercise 1 - Credit Card Validation
 * Functional implementation of credit card validation algorithm based on CIS-194 Homework 1
 *
 * This implementation follows functional programming principles:
 * - Immutable data structures
 * - Pure functions (no side effects)
 * - Stream-based operations
 * - Function composition
 */
public class Exercise1 {

    /**
     * Convert a positive integer to a list of its digits using functional approach
     * @param n the number to convert
     * @return immutable list of digits, or empty list if n is negative
     */
    public List<Integer> toDigits(long n) {
        return n < 0 ? List.of() :
               n == 0 ? List.of(0) :
               toDigitsRev(n).stream()
                   .collect(Collectors.collectingAndThen(
                       Collectors.toList(),
                       list -> {
                           var reversed = List.copyOf(list);
                           return IntStream.range(0, reversed.size())
                                          .mapToObj(i -> reversed.get(reversed.size() - 1 - i))
                                          .collect(Collectors.toUnmodifiableList());
                       }
                   ));
    }

    /**
     * Convert a positive integer to a list of its digits in reverse order using streams
     * @param n the number to convert
     * @return immutable list of digits in reverse order, or empty list if n is negative
     */
    public List<Integer> toDigitsRev(long n) {
        return n < 0 ? List.of() :
               n == 0 ? List.of(0) :
               generateDigitsStream(n)
                   .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Generate a stream of digits from a number
     * @param n the number to extract digits from
     * @return stream of digits in reverse order
     */
    private Stream<Integer> generateDigitsStream(long n) {
        return Stream.iterate(n, num -> num > 0, num -> num / 10)
                     .map(num -> (int) (num % 10));
    }

    /**
     * Double every other element starting from the right using functional approach
     * @param digits immutable list of digits
     * @return new immutable list with every other element doubled from the right
     */
    public List<Integer> doubleEveryOther(List<Integer> digits) {
        if (digits.size() <= 1) {
            return List.copyOf(digits);
        }

        final int startIndex = digits.size() % 2 == 0 ? 1 : 0;

        return IntStream.range(0, digits.size())
                       .mapToObj(i -> shouldDouble(i, startIndex) ?
                                     digits.get(i) * 2 :
                                     digits.get(i))
                       .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Determine if an element at given index should be doubled
     * @param index current index
     * @param startIndex starting index for doubling
     * @return true if element should be doubled
     */
    private boolean shouldDouble(int index, int startIndex) {
        return index >= startIndex && (index - startIndex) % 2 == 0;
    }

    /**
     * Sum all digits in a list using streams and functional composition
     * @param digits list of integers
     * @return sum of all digits
     */
    public int sumDigits(List<Integer> digits) {
        return digits.stream()
                    .flatMapToInt(this::digitStream)
                    .sum();
    }

    /**
     * Convert a number to a stream of its individual digits
     * @param number the number to break into digits
     * @return IntStream of individual digits
     */
    private IntStream digitStream(int number) {
        return generateDigitsStream(number)
                .mapToInt(Integer::intValue);
    }

    /**
     * Validate a credit card number using Luhn algorithm with functional composition
     * @param cardNumber the credit card number to validate
     * @return true if valid, false otherwise
     */
    public boolean validate(long cardNumber) {
        return Function.<Long>identity()
                .andThen(this::toDigitsRev)
                .andThen(this::doubleEveryOther)
                .andThen(this::sumDigits)
                .andThen(sum -> sum % 10 == 0)
                .apply(cardNumber);
    }

    /**
     * Alternative functional validation using method chaining
     * @param cardNumber the credit card number to validate
     * @return true if valid, false otherwise
     */
    public boolean validateFunctional(long cardNumber) {
        return Stream.of(cardNumber)
                    .map(this::toDigitsRev)
                    .map(this::doubleEveryOther)
                    .mapToInt(this::sumDigits)
                    .anyMatch(sum -> sum % 10 == 0);
    }
}
