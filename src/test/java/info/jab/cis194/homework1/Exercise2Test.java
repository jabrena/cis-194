package info.jab.cis194.homework1;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Exercise 2 - Tower of Hanoi
 * Based on CIS-194 Homework 1
 *
 * This test suite covers the Tower of Hanoi algorithm implementation including:
 * - Move generation for different disc counts
 * - Verification of the 2^n - 1 move count formula
 * - Move object equality and hash code behavior
 * - Edge cases and specific move sequences
 */
@DisplayName("Exercise 2: Tower of Hanoi Tests")
class Exercise2Test {

    private Exercise2 exercise;

    @BeforeEach
    void setUp() {
        exercise = new Exercise2();
    }

    @Nested
    @DisplayName("Hanoi Algorithm Move Generation")
    class HanoiAlgorithmTests {

        @Test
        @DisplayName("Should handle base case: moving 1 disc")
        void shouldHandleBaseCaseMovingOneDisc() {
            List<Exercise2.Move> moves = exercise.hanoi(1, "a", "b", "c");

            assertEquals(1, moves.size());
            assertEquals(new Exercise2.Move("a", "b"), moves.get(0));
        }

        @Test
        @DisplayName("Should generate correct sequence for 2 discs")
        void shouldGenerateCorrectSequenceForTwoDiscs() {
            List<Exercise2.Move> moves = exercise.hanoi(2, "a", "b", "c");

            assertEquals(3, moves.size());
            assertEquals(new Exercise2.Move("a", "c"), moves.get(0));
            assertEquals(new Exercise2.Move("a", "b"), moves.get(1));
            assertEquals(new Exercise2.Move("c", "b"), moves.get(2));
        }

        @Test
        @DisplayName("Should generate correct sequence for 3 discs")
        void shouldGenerateCorrectSequenceForThreeDiscs() {
            List<Exercise2.Move> moves = exercise.hanoi(3, "a", "b", "c");

            assertEquals(7, moves.size());

            // Verify the complete sequence for 3 discs
            List<Exercise2.Move> expectedMoves = List.of(
                new Exercise2.Move("a", "b"),
                new Exercise2.Move("a", "c"),
                new Exercise2.Move("b", "c"),
                new Exercise2.Move("a", "b"),
                new Exercise2.Move("c", "a"),
                new Exercise2.Move("c", "b"),
                new Exercise2.Move("a", "b")
            );

            assertEquals(expectedMoves, moves);
        }

        @Test
        @DisplayName("Should return empty list for 0 discs")
        void shouldReturnEmptyListForZeroDiscs() {
            List<Exercise2.Move> moves = exercise.hanoi(0, "a", "b", "c");
            assertEquals(0, moves.size());
        }

        @Test
        @DisplayName("Should handle different peg names")
        void shouldHandleDifferentPegNames() {
            List<Exercise2.Move> moves = exercise.hanoi(1, "source", "destination", "auxiliary");

            assertEquals(1, moves.size());
            assertEquals(new Exercise2.Move("source", "destination"), moves.get(0));
        }
    }

    @Nested
    @DisplayName("Move Count Verification")
    class MoveCountTests {

        @ParameterizedTest
        @DisplayName("Should follow 2^n - 1 formula for move count")
        @CsvSource({
            "1, 1",   // 2^1 - 1 = 1
            "2, 3",   // 2^2 - 1 = 3
            "3, 7",   // 2^3 - 1 = 7
            "4, 15",  // 2^4 - 1 = 15
            "5, 31"   // 2^5 - 1 = 31
        })
        void shouldFollowExponentialMoveCountFormula(int discs, int expectedMoveCount) {
            List<Exercise2.Move> moves = exercise.hanoi(discs, "a", "b", "c");
            assertEquals(expectedMoveCount, moves.size(),
                () -> String.format("For %d discs, expected %d moves but got %d",
                    discs, expectedMoveCount, moves.size()));
        }

        @ParameterizedTest
        @DisplayName("Should verify mathematical formula 2^n - 1")
        @MethodSource("discCountTestCases")
        void shouldVerifyMathematicalFormula(int discs) {
            List<Exercise2.Move> moves = exercise.hanoi(discs, "a", "b", "c");
            int expectedMoveCount = (int) Math.pow(2, discs) - 1;

            assertEquals(expectedMoveCount, moves.size(),
                () -> String.format("Mathematical formula 2^%d - 1 = %d should match actual move count",
                    discs, expectedMoveCount));
        }

        static Stream<Arguments> discCountTestCases() {
            return Stream.of(
                Arguments.of(1),
                Arguments.of(2),
                Arguments.of(3),
                Arguments.of(4),
                Arguments.of(5),
                Arguments.of(6)
            );
        }
    }

    @Nested
    @DisplayName("Move Object Behavior")
    class MoveObjectTests {

        @Test
        @DisplayName("Should implement equality correctly")
        void shouldImplementEqualityCorrectly() {
            Exercise2.Move move1 = new Exercise2.Move("a", "b");
            Exercise2.Move move2 = new Exercise2.Move("a", "b");
            Exercise2.Move move3 = new Exercise2.Move("b", "a");

            assertEquals(move1, move2, "Moves with same from and to should be equal");
            assertNotEquals(move1, move3, "Moves with different from/to should not be equal");
        }

        @Test
        @DisplayName("Should implement hash code correctly")
        void shouldImplementHashCodeCorrectly() {
            Exercise2.Move move1 = new Exercise2.Move("a", "b");
            Exercise2.Move move2 = new Exercise2.Move("a", "b");
            Exercise2.Move move3 = new Exercise2.Move("b", "a");

            assertEquals(move1.hashCode(), move2.hashCode(),
                "Equal moves should have equal hash codes");

            // Note: Different objects may have same hash code, but we expect them to be different
            // This is not a strict requirement but helps with hash table performance
        }

        @Test
        @DisplayName("Should handle null values in equality")
        void shouldHandleNullValuesInEquality() {
            Exercise2.Move move = new Exercise2.Move("a", "b");

            assertNotEquals(move, null, "Move should not equal null");
            assertNotEquals(move, "not a move", "Move should not equal objects of different type");
        }

        @Test
        @DisplayName("Should implement reflexive equality")
        void shouldImplementReflexiveEquality() {
            Exercise2.Move move = new Exercise2.Move("a", "b");
            assertEquals(move, move, "Move should equal itself");
        }

        @Test
        @DisplayName("Should provide meaningful string representation")
        void shouldProvideMeaningfulStringRepresentation() {
            Exercise2.Move move = new Exercise2.Move("source", "destination");
            String stringRepresentation = move.toString();

            assertNotNull(stringRepresentation);
            assertTrue(stringRepresentation.contains("source"),
                "String representation should contain source peg");
            assertTrue(stringRepresentation.contains("destination"),
                "String representation should contain destination peg");
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Conditions")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle negative disc count gracefully")
        void shouldHandleNegativeDiscCountGracefully() {
            // Depending on implementation, this might throw an exception or return empty list
            // For now, we expect it to return an empty list based on current implementation
            List<Exercise2.Move> moves = exercise.hanoi(-1, "a", "b", "c");
            assertEquals(0, moves.size(), "Negative disc count should return empty list");
        }

        @Test
        @DisplayName("Should work with single character peg names")
        void shouldWorkWithSingleCharacterPegNames() {
            List<Exercise2.Move> moves = exercise.hanoi(2, "x", "y", "z");
            assertEquals(3, moves.size());

            // Verify all moves use the correct peg names
            moves.forEach(move -> {
                assertTrue(List.of("x", "y", "z").contains(move.getFrom()),
                    "From peg should be one of the provided pegs");
                assertTrue(List.of("x", "y", "z").contains(move.getTo()),
                    "To peg should be one of the provided pegs");
            });
        }
    }
}
