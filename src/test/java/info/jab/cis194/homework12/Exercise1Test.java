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
        @DisplayName("Should create valid die values")
        @ValueSource(ints = {1, 2, 3, 4, 5, 6})
        void shouldCreateValidDieValues(int value) {
            // When
            var dieValue = of(value);

            // Then
            assertThat(dieValue.getValue()).isEqualTo(value);
        }

        @ParameterizedTest
        @DisplayName("Should reject invalid die values")
        @ValueSource(ints = {0, 7, -1, 10})
        void shouldRejectInvalidDieValues(int value) {
            // When & Then
            assertThatThrownBy(() -> of(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Die value must be between 1 and 6");
        }

        @Test
        @DisplayName("Should implement equals and hashCode correctly")
        void shouldImplementEqualsAndHashCodeCorrectly() {
            // Given
            var die1 = of(3);
            var die2 = of(3);
            var die3 = of(4);

            // Then
            assertThat(die1).isEqualTo(die2);
            assertThat(die1).isNotEqualTo(die3);
            assertThat(die1.hashCode()).isEqualTo(die2.hashCode());
        }

        @Test
        @DisplayName("Should implement Comparable correctly")
        void shouldImplementComparableCorrectly() {
            // Given
            var die1 = of(2);
            var die2 = of(5);
            var die3 = of(2);

            // Then
            assertThat(die1.compareTo(die2)).isLessThan(0);
            assertThat(die2.compareTo(die1)).isGreaterThan(0);
            assertThat(die1.compareTo(die3)).isEqualTo(0);
        }

        @Test
        @DisplayName("Should have proper toString representation")
        void shouldHaveProperToStringRepresentation() {
            // Given
            var die = of(4);

            // Then
            assertThat(die.toString()).isEqualTo("DieValue{4}");
        }
    }

    @Nested
    @DisplayName("Die Rolling Tests")
    class DieRollingTests {

        @Test
        @DisplayName("Should roll die values within valid range")
        void shouldRollDieValuesWithinValidRange() {
            // When
            List<Exercise1.DieValue> rolls = IntStream.range(0, 100)
                .mapToObj(i -> exercise.rollDie(random))
                .toList();

            // Then
            assertThat(rolls).allSatisfy(die -> {
                assertThat(die.getValue()).isBetween(1, 6);
            });
        }

        @Test
        @DisplayName("Should produce different values with sufficient rolls")
        void shouldProduceDifferentValuesWithSufficientRolls() {
            // When
            List<Integer> rolls = IntStream.range(0, 1000)
                .mapToObj(i -> exercise.rollDie(random))
                .map(Exercise1.DieValue::getValue)
                .distinct()
                .toList();

            // Then
            assertThat(rolls).hasSizeGreaterThanOrEqualTo(5); // Should get most values
        }

        @Test
        @DisplayName("Should roll multiple dice")
        void shouldRollMultipleDice() {
            // Given
            int numberOfDice = 3;

            // When
            List<Exercise1.DieValue> rolls = exercise.rollDice(numberOfDice, random);

            // Then
            assertThat(rolls).hasSize(numberOfDice);
            assertThat(rolls).allSatisfy(die -> {
                assertThat(die.getValue()).isBetween(1, 6);
            });
        }

        @Test
        @DisplayName("Should handle rolling zero dice")
        void shouldHandleRollingZeroDice() {
            // When
            List<Exercise1.DieValue> rolls = exercise.rollDice(0, random);

            // Then
            assertThat(rolls).isEmpty();
        }

        @ParameterizedTest
        @DisplayName("Should reject negative number of dice")
        @ValueSource(ints = {-1, -5, -10})
        void shouldRejectNegativeNumberOfDice(int numberOfDice) {
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
        @DisplayName("Should sort dice in descending order")
        void shouldSortDiceInDescendingOrder() {
            // Given
            List<Exercise1.DieValue> dice = List.of(
                of(2),
                of(6),
                of(1),
                of(4)
            );

            // When
            List<Exercise1.DieValue> sorted = exercise.sortDiceDescending(dice);

            // Then
            assertThat(sorted).containsExactly(
                of(6),
                of(4),
                of(2),
                of(1)
            );
        }

        @Test
        @DisplayName("Should handle empty dice list")
        void shouldHandleEmptyDiceList() {
            // Given
            List<Exercise1.DieValue> dice = List.of();

            // When
            List<Exercise1.DieValue> sorted = exercise.sortDiceDescending(dice);

            // Then
            assertThat(sorted).isEmpty();
        }

        @ParameterizedTest
        @DisplayName("Should determine battle outcome correctly")
        @MethodSource("battleOutcomeTestCases")
        void shouldDetermineBattleOutcomeCorrectly(
            Exercise1.DieValue attackerDie,
            Exercise1.DieValue defenderDie,
            Exercise1.BattleResult expected
        ) {
            // When
            Exercise1.BattleResult result = exercise.compareDice(attackerDie, defenderDie);

            // Then
            assertThat(result).isEqualTo(expected);
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
        @DisplayName("Should calculate battle losses for single combat")
        void shouldCalculateBattleLossesForSingleCombat() {
            // Given
            List<Exercise1.DieValue> attackerRolls = List.of(of(5));
            List<Exercise1.DieValue> defenderRolls = List.of(of(3));

            // When
            Exercise1.BattleOutcome outcome = exercise.resolveBattle(attackerRolls, defenderRolls);

            // Then
            assertThat(outcome.getAttackerLosses()).isEqualTo(0);
            assertThat(outcome.getDefenderLosses()).isEqualTo(1);
        }

        @Test
        @DisplayName("Should calculate battle losses for multiple dice")
        void shouldCalculateBattleLossesForMultipleDice() {
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
        @DisplayName("Should handle unequal number of dice")
        void shouldHandleUnequalNumberOfDice() {
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
            assertThat(outcome.getAttackerLosses()).isEqualTo(0);
            assertThat(outcome.getDefenderLosses()).isEqualTo(1);
        }
    }
}
