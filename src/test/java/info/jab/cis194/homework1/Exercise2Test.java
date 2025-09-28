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

import static org.assertj.core.api.Assertions.*;

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
            // Given
            int numberOfDiscs = 1;
            String sourcePeg = "a";
            String destinationPeg = "b";
            String auxiliaryPeg = "c";

            // When
            List<Exercise2.Move> moves = exercise.hanoi(numberOfDiscs, sourcePeg, destinationPeg, auxiliaryPeg);

            // Then
            assertThat(moves).hasSize(1);
            assertThat(moves.get(0)).isEqualTo(new Exercise2.Move("a", "b"));
        }

        @Test
        @DisplayName("Should generate correct sequence for 2 discs")
        void shouldGenerateCorrectSequenceForTwoDiscs() {
            // Given
            int numberOfDiscs = 2;
            String sourcePeg = "a";
            String destinationPeg = "b";
            String auxiliaryPeg = "c";

            // When
            List<Exercise2.Move> moves = exercise.hanoi(numberOfDiscs, sourcePeg, destinationPeg, auxiliaryPeg);

            // Then
            assertThat(moves).hasSize(3);
            assertThat(moves.get(0)).isEqualTo(new Exercise2.Move("a", "c"));
            assertThat(moves.get(1)).isEqualTo(new Exercise2.Move("a", "b"));
            assertThat(moves.get(2)).isEqualTo(new Exercise2.Move("c", "b"));
        }

        @Test
        @DisplayName("Should generate correct sequence for 3 discs")
        void shouldGenerateCorrectSequenceForThreeDiscs() {
            List<Exercise2.Move> moves = exercise.hanoi(3, "a", "b", "c");

            assertThat(moves).hasSize(7);

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

            assertThat(moves).isEqualTo(expectedMoves);
        }

        @Test
        @DisplayName("Should return empty list for 0 discs")
        void shouldReturnEmptyListForZeroDiscs() {
            List<Exercise2.Move> moves = exercise.hanoi(0, "a", "b", "c");
            assertThat(moves).isEmpty();
        }

        @Test
        @DisplayName("Should handle different peg names")
        void shouldHandleDifferentPegNames() {
            List<Exercise2.Move> moves = exercise.hanoi(1, "source", "destination", "auxiliary");

            assertThat(moves).hasSize(1);
            assertThat(moves.get(0)).isEqualTo(new Exercise2.Move("source", "destination"));
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
            assertThat(moves)
                .as("For %d discs, expected %d moves", discs, expectedMoveCount)
                .hasSize(expectedMoveCount);
        }

        @ParameterizedTest
        @DisplayName("Should verify mathematical formula 2^n - 1")
        @MethodSource("discCountTestCases")
        void shouldVerifyMathematicalFormula(int discs) {
            List<Exercise2.Move> moves = exercise.hanoi(discs, "a", "b", "c");
            int expectedMoveCount = (int) Math.pow(2, discs) - 1;

            assertThat(moves)
                .as("Mathematical formula 2^%d - 1 = %d should match actual move count",
                    discs, expectedMoveCount)
                .hasSize(expectedMoveCount);
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

            assertThat(move1)
                .as("Moves with same from and to should be equal")
                .isEqualTo(move2);
            assertThat(move1)
                .as("Moves with different from/to should not be equal")
                .isNotEqualTo(move3);
        }

        @Test
        @DisplayName("Should implement hash code correctly")
        void shouldImplementHashCodeCorrectly() {
            Exercise2.Move move1 = new Exercise2.Move("a", "b");
            Exercise2.Move move2 = new Exercise2.Move("a", "b");
            Exercise2.Move move3 = new Exercise2.Move("b", "a");

            assertThat(move1.hashCode())
                .as("Equal moves should have equal hash codes")
                .isEqualTo(move2.hashCode());

            // Note: Different objects may have same hash code, but we expect them to be different
            // This is not a strict requirement but helps with hash table performance
        }

        @Test
        @DisplayName("Should handle null values in equality")
        void shouldHandleNullValuesInEquality() {
            Exercise2.Move move = new Exercise2.Move("a", "b");

            assertThat(move)
                .as("Move should not equal null")
                .isNotEqualTo(null);
            assertThat(move)
                .as("Move should not equal objects of different type")
                .isNotEqualTo("not a move");
        }

        @Test
        @DisplayName("Should implement reflexive equality")
        void shouldImplementReflexiveEquality() {
            Exercise2.Move move = new Exercise2.Move("a", "b");
            assertThat(move)
                .as("Move should equal itself")
                .isEqualTo(move);
        }

        @Test
        @DisplayName("Should provide meaningful string representation")
        void shouldProvideMeaningfulStringRepresentation() {
            Exercise2.Move move = new Exercise2.Move("source", "destination");
            String stringRepresentation = move.toString();

            assertThat(stringRepresentation)
                .isNotNull()
                .as("String representation should contain source peg")
                .contains("source")
                .as("String representation should contain destination peg")
                .contains("destination");
        }

        @Test
        @DisplayName("Should support factory method creation")
        void shouldSupportFactoryMethodCreation() {
            Exercise2.Move move1 = new Exercise2.Move("a", "b");
            Exercise2.Move move2 = Exercise2.Move.of("a", "b");

            assertThat(move1).isEqualTo(move2);
        }

        @Test
        @DisplayName("Should check peg involvement correctly")
        void shouldCheckPegInvolvementCorrectly() {
            Exercise2.Move move = new Exercise2.Move("a", "b");

            assertThat(move.involves("a")).isTrue();
            assertThat(move.involves("b")).isTrue();
            assertThat(move.involves("c")).isFalse();
        }

        @Test
        @DisplayName("Should get other peg correctly")
        void shouldGetOtherPegCorrectly() {
            Exercise2.Move move = new Exercise2.Move("source", "destination");

            assertThat(move.getOtherPeg("source")).isEqualTo("destination");
            assertThat(move.getOtherPeg("destination")).isEqualTo("source");
        }
    }

    @Nested
    @DisplayName("Functional Programming Features")
    class FunctionalProgrammingTests {

        @Test
        @DisplayName("Should calculate minimum moves using bit shifting")
        void shouldCalculateMinMovesUsingBitShifting() {
            assertThat(exercise.calculateMinMoves(0)).isEqualTo(0);
            assertThat(exercise.calculateMinMoves(1)).isEqualTo(1);
            assertThat(exercise.calculateMinMoves(2)).isEqualTo(3);
            assertThat(exercise.calculateMinMoves(3)).isEqualTo(7);
            assertThat(exercise.calculateMinMoves(4)).isEqualTo(15);
        }

        @Test
        @DisplayName("Should validate move sequences")
        void shouldValidateMoveSequences() {
            List<Exercise2.Move> validSequence = exercise.hanoi(2, "a", "b", "c");
            assertThat(exercise.isValidSequence(validSequence, 2)).isTrue();

            List<Exercise2.Move> invalidSequence = List.of(
                new Exercise2.Move("a", "b")
            );
            assertThat(exercise.isValidSequence(invalidSequence, 2)).isFalse();
        }

        @Test
        @DisplayName("Should extract involved pegs from move sequence")
        void shouldExtractInvolvedPegsFromMoveSequence() {
            List<Exercise2.Move> moves = exercise.hanoi(2, "source", "dest", "aux");
            List<String> involvedPegs = exercise.getInvolvedPegs(moves);

            assertThat(involvedPegs)
                .hasSize(3)
                .containsExactly("aux", "dest", "source"); // sorted order
        }

        @Test
        @DisplayName("Should use alternative functional implementation")
        void shouldUseAlternativeFunctionalImplementation() {
            List<Exercise2.Move> standard = exercise.hanoi(3, "a", "b", "c");
            List<Exercise2.Move> functional = exercise.hanoiFunctional(3, "a", "b", "c");

            // Both should produce the same number of moves
            assertThat(functional).hasSize(standard.size());
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
            assertThat(moves)
                .as("Negative disc count should return empty list")
                .isEmpty();
        }

        @Test
        @DisplayName("Should work with single character peg names")
        void shouldWorkWithSingleCharacterPegNames() {
            List<Exercise2.Move> moves = exercise.hanoi(2, "x", "y", "z");
            assertThat(moves).hasSize(3);

            // Verify all moves use the correct peg names
            moves.forEach(move -> {
                assertThat(move.from())
                    .as("From peg should be one of the provided pegs")
                    .isIn("x", "y", "z");
                assertThat(move.to())
                    .as("To peg should be one of the provided pegs")
                    .isIn("x", "y", "z");
            });
        }
    }
}
