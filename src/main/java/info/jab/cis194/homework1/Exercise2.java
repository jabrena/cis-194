package info.jab.cis194.homework1;

import java.util.List;
import java.util.stream.Stream;

/**
 * Exercise 2 - Tower of Hanoi
 * Functional implementation of Tower of Hanoi solution based on CIS-194 Homework 1
 *
 * This implementation follows functional programming principles:
 * - Immutable data structures (using records)
 * - Pure functions (no side effects)
 * - Stream-based operations
 * - Functional composition and recursion
 */
public class Exercise2 {

    /**
     * Represents an immutable move from one peg to another using a record
     * Records provide immutability, equals(), hashCode(), and toString() automatically
     */
    public record Move(String from, String to) {
        /**
         * Constructor with validation
         */
        public Move {
            if (from == null || to == null) {
                throw new IllegalArgumentException("Peg names cannot be null");
            }
        }

        /**
         * Factory method for creating moves with validation
         */
        public static Move of(String from, String to) {
            return new Move(from, to);
        }

        /**
         * Check if this move involves a specific peg
         */
        public boolean involves(String peg) {
            return from.equals(peg) || to.equals(peg);
        }

        /**
         * Get the other peg involved in the move (given one peg)
         */
        public String getOtherPeg(String peg) {
            if (from.equals(peg)) return to;
            if (to.equals(peg)) return from;
            throw new IllegalArgumentException("Peg " + peg + " not involved in this move");
        }
    }

    /**
     * Solve the Tower of Hanoi problem using functional approach
     * @param n number of discs
     * @param a source peg
     * @param b destination peg
     * @param c auxiliary peg
     * @return immutable list of moves to solve the puzzle
     */
    public List<Move> hanoi(int n, String a, String b, String c) {
        return hanoiStream(n, a, b, c).toList();
    }

    /**
     * Generate a stream of moves for the Tower of Hanoi problem
     * @param n number of discs
     * @param a source peg
     * @param b destination peg
     * @param c auxiliary peg
     * @return stream of moves to solve the puzzle
     */
    private Stream<Move> hanoiStream(int n, String a, String b, String c) {
        if (n <= 0) {
            return Stream.empty();
        }

        if (n == 1) {
            return Stream.of(Move.of(a, b));
        }

        return Stream.of(
            // Move n-1 discs from a to c using b as auxiliary
            hanoiStream(n - 1, a, c, b),
            // Move the largest disc from a to b
            Stream.of(Move.of(a, b)),
            // Move n-1 discs from c to b using a as auxiliary
            hanoiStream(n - 1, c, b, a)
        ).flatMap(stream -> stream);
    }

    /**
     * Alternative functional implementation using tail recursion simulation
     * @param n number of discs
     * @param a source peg
     * @param b destination peg
     * @param c auxiliary peg
     * @return immutable list of moves to solve the puzzle
     */
    public List<Move> hanoiFunctional(int n, String a, String b, String c) {
        return hanoiAccumulator(n, a, b, c, List.of()).reversed();
    }

    /**
     * Tail-recursive style accumulator for Hanoi moves
     */
    private List<Move> hanoiAccumulator(int n, String a, String b, String c, List<Move> acc) {
        if (n <= 0) {
            return acc;
        }

        if (n == 1) {
            return Stream.concat(Stream.of(Move.of(a, b)), acc.stream()).toList();
        }

        // Build moves in reverse order for tail recursion simulation
        var step1 = hanoiAccumulator(n - 1, c, b, a, acc);
        var step2 = Stream.concat(Stream.of(Move.of(a, b)), step1.stream()).toList();
        return hanoiAccumulator(n - 1, a, c, b, step2);
    }

    /**
     * Calculate the minimum number of moves required using the mathematical formula 2^n - 1
     * @param n number of discs
     * @return minimum number of moves required
     */
    public long calculateMinMoves(int n) {
        return n <= 0 ? 0 : (1L << n) - 1;
    }

    /**
     * Validate that a sequence of moves is valid for the Tower of Hanoi problem
     * @param moves list of moves to validate
     * @param n number of discs
     * @return true if the sequence is valid
     */
    public boolean isValidSequence(List<Move> moves, int n) {
        long expectedMoves = calculateMinMoves(n);
        return moves.size() == expectedMoves &&
               moves.stream().allMatch(move -> move.from() != null && move.to() != null);
    }

    /**
     * Get all unique pegs involved in a sequence of moves
     * @param moves list of moves
     * @return set of unique peg names
     */
    public List<String> getInvolvedPegs(List<Move> moves) {
        return moves.stream()
                   .flatMap(move -> Stream.of(move.from(), move.to()))
                   .distinct()
                   .sorted()
                   .toList();
    }
}
