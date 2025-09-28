package info.jab.cis194.homework6;

import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * Exercise 1: Fibonacci numbers using lazy evaluation
 *
 * This exercise implements fibonacci numbers using lazy evaluation concepts.
 * We create an infinite stream of fibonacci numbers where each number
 * is computed only when needed (lazy evaluation).
 *
 * Follows functional programming principles:
 * - Immutability
 * - Pure functions
 * - Higher-order functions
 * - Function composition
 * - Lazy evaluation
 */
public class Exercise1 {

    /**
     * Immutable pair to represent fibonacci state
     */
    public record FibPair(long current, long next) {
        public FibPair advance() {
            return new FibPair(next, current + next);
        }
    }

    /**
     * Returns the nth fibonacci number (0-indexed) using functional approach
     * F(0) = 0, F(1) = 1, F(n) = F(n-1) + F(n-2)
     *
     * @param n the index of the fibonacci number to compute
     * @return the nth fibonacci number
     */
    public long fibonacci(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Index must be non-negative");
        }

        return fibonacciStream()
                .skip(n)
                .findFirst()
                .orElse(0);
    }

    /**
     * Pure functional fibonacci using fold/reduce
     */
    public long fibonacciFunctional(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Index must be non-negative");
        }

        return Stream.iterate(new FibPair(0, 1), FibPair::advance)
                .limit(n + 1)
                .reduce((first, second) -> second)
                .map(FibPair::current)
                .orElse(0L);
    }

    /**
     * Returns an infinite stream of fibonacci numbers using lazy evaluation.
     * The stream generates fibonacci numbers on-demand, demonstrating
     * the concept of lazy evaluation where values are computed only when needed.
     *
     * @return an infinite stream of fibonacci numbers
     */
    public LongStream fibonacciStream() {
        return Stream.iterate(new FibPair(0, 1), FibPair::advance)
                .mapToLong(FibPair::current);
    }

    /**
     * Alternative implementation using Stream<Long> instead of LongStream
     * This demonstrates another way to create lazy fibonacci sequences.
     *
     * @return an infinite stream of fibonacci numbers as Long objects
     */
    public Stream<Long> fibonacciStreamBoxed() {
        return Stream.iterate(new FibPair(0, 1), FibPair::advance)
                .map(FibPair::current);
    }

    /**
     * Higher-order function that creates a fibonacci generator
     */
    public static Function<Integer, Long> createFibonacciFunction() {
        return n -> new Exercise1().fibonacci(n);
    }

    /**
     * Curried fibonacci function
     */
    public static Function<Integer, Function<Integer, Stream<Long>>> fibonacciRange() {
        return start -> end -> new Exercise1().fibonacciStreamBoxed()
                .skip(start)
                .limit(end - start + 1);
    }

    /**
     * Composition of fibonacci functions
     */
    public long fibonacciComposed(int n) {
        UnaryOperator<FibPair> advance = FibPair::advance;
        Function<FibPair, Long> extractCurrent = FibPair::current;

        return Stream.iterate(new FibPair(0, 1), advance)
                .skip(n)
                .map(extractCurrent)
                .findFirst()
                .orElse(0L);
    }
}
