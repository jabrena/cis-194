package info.jab.cis194.homework12;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import static info.jab.cis194.homework12.Exercise1.BattleResult.ATTACKER_WINS;
import static info.jab.cis194.homework12.Exercise1.BattleResult.DEFENDER_WINS;
import static info.jab.cis194.homework12.Exercise1.DieValue.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test class for Exercise 1 - Risk Battle Simulation Basics
 * Based on CIS-194 Homework 12 - Risk Simulation
 *
 * This test suite covers the basic components of Risk battle simulation:
 * - DieValue type and validation
 * - Die rolling mechanics
 * - Basic battle outcome calculation
 * - Random number generation handling
 */
@DisplayName("Exercise 1: Risk Battle Simulation Basics Tests")
class Exercise1Test {

    private Exercise1 exercise;
    private Random random;

    @BeforeEach
    void setUp() {
        exercise = new Exercise1();
        random = new Random(42); // Fixed seed for reproducible tests
    }

    @Nested
    @DisplayName("DieValue Tests")
    class DieValueTests {

        @ParameterizedTest
        @DisplayName("Should create valid die values for all valid inputs")
        @ValueSource(ints = {1, 2, 3, 4, 5, 6})
        void should_CreateValidDieValue_When_GivenValidInput(int value) {
            // Given
            // Valid die value provided via @ValueSource

            // When
            var dieValue = of(value);

            // Then
            assertThat(dieValue.getValue()).isEqualTo(value);
        }

        @ParameterizedTest
        @DisplayName("Should reject invalid die values with appropriate error message")
        @ValueSource(ints = {0, 7, -1, 10})
        void should_ThrowIllegalArgumentException_When_GivenInvalidDieValue(int value) {
            // Given
            // Invalid die value provided via @ValueSource

            // When & Then
            assertThatThrownBy(() -> of(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Die value must be between 1 and 6");
        }

        @Test
        @DisplayName("Should implement equals and hashCode contract correctly")
        void should_ImplementEqualsAndHashCodeContract_When_ComparingDieValues() {
            // Given
            var die1 = of(3);
            var die2 = of(3);
            var die3 = of(4);

            // When
            // Comparison operations are performed

            // Then
            assertThat(die1)
                .isEqualTo(die2)
                .isNotEqualTo(die3)
                .hasSameHashCodeAs(die2);
        }

        @Test
        @DisplayName("Should implement Comparable interface correctly for ordering")
        void should_ImplementComparableInterface_When_ComparingDieValues() {
            // Given
            var lowerDie = of(2);
            var higherDie = of(5);
            var equalDie = of(2);

            // When
            int lowerToHigherComparison = lowerDie.compareTo(higherDie);
            int higherToLowerComparison = higherDie.compareTo(lowerDie);
            int equalComparison = lowerDie.compareTo(equalDie);

            // Then
            assertThat(lowerToHigherComparison).isNegative();
            assertThat(higherToLowerComparison).isPositive();
            assertThat(equalComparison).isZero();
        }

        @Test
        @DisplayName("Should have proper toString representation for debugging")
        void should_ReturnFormattedString_When_ToStringIsCalled() {
            // Given
            var die = of(4);

            // When
            String stringRepresentation = die.toString();

            // Then
            assertThat(stringRepresentation).isEqualTo("DieValue{4}");
        }
    }

    @Nested
    @DisplayName("Die Rolling Tests")
    class DieRollingTests {

        @Test
        @DisplayName("Should roll die values within valid range for multiple rolls")
        void should_RollValuesWithinValidRange_When_RollingDieMultipleTimes() {
            // Given
            int numberOfRolls = 100;

            // When
            List<Exercise1.DieValue> rolls = IntStream.range(0, numberOfRolls)
                .mapToObj(i -> exercise.rollDie(random))
                .toList();

            // Then
            assertThat(rolls)
                .hasSize(numberOfRolls)
                .allSatisfy(die -> assertThat(die.getValue()).isBetween(1, 6));
        }

        @Test
        @DisplayName("Should produce different values with sufficient rolls demonstrating randomness")
        void should_ProduceDifferentValues_When_RollingDieManyTimes() {
            // Given
            int numberOfRolls = 1000;
            int minimumExpectedUniqueValues = 5;

            // When
            List<Integer> uniqueRolls = IntStream.range(0, numberOfRolls)
                .mapToObj(i -> exercise.rollDie(random))
                .map(Exercise1.DieValue::getValue)
                .distinct()
                .toList();

            // Then
            assertThat(uniqueRolls).hasSizeGreaterThanOrEqualTo(minimumExpectedUniqueValues);
        }

        @Test
        @DisplayName("Should roll multiple dice with correct count and valid values")
        void should_RollMultipleDiceWithValidValues_When_GivenNumberOfDice() {
            // Given
            int numberOfDice = 3;

            // When
            List<Exercise1.DieValue> rolls = exercise.rollDice(numberOfDice, random);

            // Then
            assertThat(rolls)
                .hasSize(numberOfDice)
                .allSatisfy(die -> assertThat(die.getValue()).isBetween(1, 6));
        }

        @Test
        @DisplayName("Should handle rolling zero dice returning empty list")
        void should_ReturnEmptyList_When_RollingZeroDice() {
            // Given
            int numberOfDice = 0;

            // When
            List<Exercise1.DieValue> rolls = exercise.rollDice(numberOfDice, random);

            // Then
            assertThat(rolls).isEmpty();
        }

        @ParameterizedTest
        @DisplayName("Should reject negative number of dice with appropriate error")
        @ValueSource(ints = {-1, -5, -10})
        void should_ThrowIllegalArgumentException_When_GivenNegativeNumberOfDice(int numberOfDice) {
            // Given
            // Negative number of dice provided via @ValueSource

            // When & Then
            assertThatThrownBy(() -> exercise.rollDice(numberOfDice, random))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Number of dice cannot be negative");
        }
    }

    @Nested
    @DisplayName("Battle Mechanics Tests")
    class BattleMechanicsTests {

        @Test
        @DisplayName("Should sort dice in descending order for battle comparison")
        void should_SortDiceInDescendingOrder_When_GivenUnsortedDice() {
            // Given
            List<Exercise1.DieValue> unsortedDice = List.of(
                of(2),
                of(6),
                of(1),
                of(4)
            );

            // When
            List<Exercise1.DieValue> sortedDice = exercise.sortDiceDescending(unsortedDice);

            // Then
            assertThat(sortedDice).containsExactly(
                of(6),
                of(4),
                of(2),
                of(1)
            );
        }

        @Test
        @DisplayName("Should handle empty dice list gracefully")
        void should_ReturnEmptyList_When_GivenEmptyDiceList() {
            // Given
            List<Exercise1.DieValue> emptyDiceList = List.of();

            // When
            List<Exercise1.DieValue> sortedDice = exercise.sortDiceDescending(emptyDiceList);

            // Then
            assertThat(sortedDice).isEmpty();
        }

        @ParameterizedTest
        @DisplayName("Should determine battle outcome correctly according to Risk rules")
        @MethodSource("battleOutcomeTestCases")
        void should_DetermineBattleOutcome_When_ComparingAttackerAndDefenderDice(
            Exercise1.DieValue attackerDie,
            Exercise1.DieValue defenderDie,
            Exercise1.BattleResult expected
        ) {
            // Given
            // Dice values provided via @MethodSource

            // When
            Exercise1.BattleResult actualResult = exercise.compareDice(attackerDie, defenderDie);

            // Then
            assertThat(actualResult).isEqualTo(expected);
        }

        static Stream<Arguments> battleOutcomeTestCases() {
            return Stream.of(
                // Attacker wins (higher roll)
                Arguments.of(of(6), of(5), ATTACKER_WINS),
                Arguments.of(of(4), of(2), ATTACKER_WINS),
                Arguments.of(of(2), of(1), ATTACKER_WINS),

                // Defender wins (equal or higher roll)
                Arguments.of(of(3), of(3), DEFENDER_WINS),
                Arguments.of(of(2), of(4), DEFENDER_WINS),
                Arguments.of(of(1), of(6), DEFENDER_WINS)
            );
        }

        @Test
        @DisplayName("Should calculate battle losses correctly for single die combat")
        void should_CalculateLossesCorrectly_When_ResolvingSingleDieBattle() {
            // Given
            List<Exercise1.DieValue> attackerRolls = List.of(of(5));
            List<Exercise1.DieValue> defenderRolls = List.of(of(3));

            // When
            Exercise1.BattleOutcome outcome = exercise.resolveBattle(attackerRolls, defenderRolls);

            // Then
            assertThat(outcome.getAttackerLosses()).isZero();
            assertThat(outcome.getDefenderLosses()).isEqualTo(1);
        }

        @Test
        @DisplayName("Should calculate battle losses correctly for multiple dice combat")
        void should_CalculateLossesCorrectly_When_ResolvingMultipleDiceBattle() {
            // Given
            List<Exercise1.DieValue> attackerRolls = List.of(
                of(6), of(3)
            );
            List<Exercise1.DieValue> defenderRolls = List.of(
                of(5), of(4)
            );

            // When
            Exercise1.BattleOutcome outcome = exercise.resolveBattle(attackerRolls, defenderRolls);

            // Then
            // 6 vs 5: Attacker wins (defender loses 1)
            // 3 vs 4: Defender wins (attacker loses 1)
            assertThat(outcome.getAttackerLosses()).isEqualTo(1);
            assertThat(outcome.getDefenderLosses()).isEqualTo(1);
        }

        @Test
        @DisplayName("Should handle unequal number of dice by comparing only matching pairs")
        void should_CompareOnlyMatchingPairs_When_GivenUnequalNumberOfDice() {
            // Given
            List<Exercise1.DieValue> attackerRolls = List.of(
                of(6), of(4), of(2)
            );
            List<Exercise1.DieValue> defenderRolls = List.of(
                of(5)
            );

            // When
            Exercise1.BattleOutcome outcome = exercise.resolveBattle(attackerRolls, defenderRolls);

            // Then
            // Only compare highest dice: 6 vs 5, attacker wins
            assertThat(outcome.getAttackerLosses()).isZero();
            assertThat(outcome.getDefenderLosses()).isEqualTo(1);
        }
    }
}
