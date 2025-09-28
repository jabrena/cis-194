package info.jab.cis194.homework1;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Exercise 2 - Tower of Hanoi
 * Implementation of Tower of Hanoi solution based on CIS-194 Homework 1
 */
public class Exercise2 {

    /**
     * Represents a move from one peg to another
     */
    public static class Move {
        private final String from;
        private final String to;

        public Move(String from, String to) {
            this.from = from;
            this.to = to;
        }

        public String getFrom() {
            return from;
        }

        public String getTo() {
            return to;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Move move = (Move) obj;
            return Objects.equals(from, move.from) && Objects.equals(to, move.to);
        }

        @Override
        public int hashCode() {
            return Objects.hash(from, to);
        }

        @Override
        public String toString() {
            return "Move{" + "from='" + from + '\'' + ", to='" + to + '\'' + '}';
        }
    }

    /**
     * Solve the Tower of Hanoi problem
     * @param n number of discs
     * @param a source peg
     * @param b destination peg
     * @param c auxiliary peg
     * @return list of moves to solve the puzzle
     */
    public List<Move> hanoi(int n, String a, String b, String c) {
        List<Move> moves = new ArrayList<>();

        if (n <= 0) {
            return moves;
        }

        if (n == 1) {
            moves.add(new Move(a, b));
            return moves;
        }

        // Move n-1 discs from a to c using b as auxiliary
        moves.addAll(hanoi(n - 1, a, c, b));

        // Move the largest disc from a to b
        moves.add(new Move(a, b));

        // Move n-1 discs from c to b using a as auxiliary
        moves.addAll(hanoi(n - 1, c, b, a));

        return moves;
    }
}
