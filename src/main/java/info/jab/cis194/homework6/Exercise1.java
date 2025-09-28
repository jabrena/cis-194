package info.jab.cis194.homework6;

import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * Exercise 1: Fibonacci numbers using lazy evaluation
 *
 * This exercise implements fibonacci numbers using lazy evaluation concepts.
 * We create an infinite stream of fibonacci numbers where each number
 * is computed only when needed (lazy evaluation).
 */
public class Exercise1 {

    /**
     * Returns the nth fibonacci number (0-indexed)
     * F(0) = 0, F(1) = 1, F(n) = F(n-1) + F(n-2)
     *
     * @param n the index of the fibonacci number to compute
     * @return the nth fibonacci number
     */
    public long fibonacci(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Index must be non-negative");
        }
        if (n == 0) return 0;
        if (n == 1) return 1;

        // Use iterative approach for efficiency
        long prev = 0;
        long curr = 1;

        for (int i = 2; i <= n; i++) {
            long next = prev + curr;
            prev = curr;
            curr = next;
        }

        return curr;
    }

    /**
     * Returns an infinite stream of fibonacci numbers using lazy evaluation.
     * The stream generates fibonacci numbers on-demand, demonstrating
     * the concept of lazy evaluation where values are computed only when needed.
     *
     * @return an infinite stream of fibonacci numbers
     */
    public LongStream fibonacciStream() {
        return Stream.iterate(
            // Seed: start with F(0) = 0 and F(1) = 1 encoded as a pair
            new long[]{0, 1},
            // Function to generate next fibonacci pair [F(n), F(n+1)]
            pair -> new long[]{pair[1], pair[0] + pair[1]}
        )
        // Extract just the first element of each pair (the current fibonacci number)
        .mapToLong(pair -> pair[0]);
    }

    /**
     * Alternative implementation using Stream<Long> instead of LongStream
     * This demonstrates another way to create lazy fibonacci sequences.
     *
     * @return an infinite stream of fibonacci numbers as Long objects
     */
    public Stream<Long> fibonacciStreamBoxed() {
        return Stream.iterate(
            new long[]{0, 1},
            pair -> new long[]{pair[1], pair[0] + pair[1]}
        )
        .map(pair -> pair[0]);
    }
}
