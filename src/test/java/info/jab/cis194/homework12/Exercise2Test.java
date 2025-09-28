package info.jab.cis194.homework12;

import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static info.jab.cis194.homework12.Exercise2.Army.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


/**
 * Test class for Exercise 2 - Advanced Risk Battle Simulation
 * Based on CIS-194 Homework 12 - Risk Simulation
 *
 * This test suite covers advanced Risk battle simulation features:
 * - Army management and validation
 * - Complete battle simulation with army sizes
 * - Risk battle rules implementation
 * - Probability estimation through simulation
 */
@DisplayName("Exercise 2: Advanced Risk Battle Simulation Tests")
class Exercise2Test {

    private Exercise2 exercise;
    private Random random;

    @BeforeEach
    void setUp() {
        exercise = new Exercise2();
        random = new Random(42); // Fixed seed for reproducible tests
    }

    @Nested
    @DisplayName("Army Management Tests")
    class ArmyManagementTests {

        @ParameterizedTest
        @DisplayName("Should create valid armies")
        @ValueSource(ints = {1, 5, 10, 100})
        void shouldCreateValidArmies(int size) {
            // When
            var army = of(size);

            // Then
            assertThat(army.getSize()).isEqualTo(size);
        }

        @ParameterizedTest
        @DisplayName("Should reject invalid army sizes")
        @ValueSource(ints = {-1, -10})
        void shouldRejectInvalidArmySizes(int size) {
            // When & Then
            assertThatThrownBy(() -> of(size))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Army size cannot be negative");
        }

        @Test
        @DisplayName("Should allow army size of 0 (eliminated army)")
        void shouldAllowArmySizeOfZero() {
            // When
            var army = of(0);

            // Then
            assertThat(army.getSize()).isEqualTo(0);
        }

        @Test
        @DisplayName("Should implement equals and hashCode correctly")
        void shouldImplementEqualsAndHashCodeCorrectly() {
            // Given
            var army1 = of(5);
            var army2 = of(5);
            var army3 = of(3);

            // Then
            assertThat(army1).isEqualTo(army2);
            assertThat(army1).isNotEqualTo(army3);
            assertThat(army1.hashCode()).isEqualTo(army2.hashCode());
        }

        @Test
        @DisplayName("Should reduce army size after losses")
        void shouldReduceArmySizeAfterLosses() {
            // Given
            var army = of(10);

            // When
            var reducedArmy = army.withLosses(3);

            // Then
            assertThat(reducedArmy.getSize()).isEqualTo(7);
        }

        @ParameterizedTest
        @DisplayName("Should reject excessive losses")
        @CsvSource({
            "5, 6",
            "3, 4",
            "1, 2"
        })
        void shouldRejectExcessiveLosses(int armySize, int losses) {
            // Given
            var army = of(armySize);

            // When & Then
            assertThatThrownBy(() -> army.withLosses(losses))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cannot lose more units than available");
        }

        @Test
        @DisplayName("Should handle zero losses")
        void shouldHandleZeroLosses() {
            // Given
            var army = of(5);

            // When
            var sameArmy = army.withLosses(0);

            // Then
            assertThat(sameArmy).isEqualTo(army);
        }
    }

    @Nested
    @DisplayName("Battle Configuration Tests")
    class BattleConfigurationTests {

        @ParameterizedTest
        @DisplayName("Should determine correct number of attacker dice")
        @CsvSource({
            "1, 0",  // 1 army can't attack (needs 1 to stay)
            "2, 1",  // 2 armies can roll 1 die
            "3, 2",  // 3 armies can roll 2 dice
            "4, 3",  // 4+ armies can roll 3 dice (max)
            "10, 3"
        })
        void shouldDetermineCorrectNumberOfAttackerDice(int armySize, int expectedDice) {
            // Given
            var army = of(armySize);

            // When
            int dice = exercise.getAttackerDice(army);

            // Then
            assertThat(dice).isEqualTo(expectedDice);
        }

        @ParameterizedTest
        @DisplayName("Should determine correct number of defender dice")
        @CsvSource({
            "1, 1",  // 1 army rolls 1 die
            "2, 2",  // 2+ armies can roll 2 dice (max)
            "5, 2",
            "10, 2"
        })
        void shouldDetermineCorrectNumberOfDefenderDice(int armySize, int expectedDice) {
            // Given
            var army = of(armySize);

            // When
            int dice = exercise.getDefenderDice(army);

            // Then
            assertThat(dice).isEqualTo(expectedDice);
        }

        @Test
        @DisplayName("Should validate battle preconditions")
        void shouldValidateBattlePreconditions() {
            // Given
            var validAttacker = of(2);
            var invalidAttacker = of(1);
            var validDefender = of(1);

            // When & Then
            assertThat(exercise.canAttack(validAttacker)).isTrue();
            assertThat(exercise.canAttack(invalidAttacker)).isFalse();
            assertThat(exercise.canDefend(validDefender)).isTrue();
        }
    }

    @Nested
    @DisplayName("Single Battle Tests")
    class SingleBattleTests {

        @Test
        @DisplayName("Should simulate single battle correctly")
        void shouldSimulateSingleBattleCorrectly() {
            // Given
            var attacker = of(4); // Can roll 3 dice
            var defender = of(3);  // Can roll 2 dice

            // When
            Exercise2.BattleResult result = exercise.simulateSingleBattle(attacker, defender, random);

            // Then
            assertThat(result.getAttackerArmy().getSize()).isLessThanOrEqualTo(4);
            assertThat(result.getDefenderArmy().getSize()).isLessThanOrEqualTo(3);

            // Total losses should be between 1 and 2 (min of dice counts)
            int totalLosses = (4 - result.getAttackerArmy().getSize()) + (3 - result.getDefenderArmy().getSize());
            assertThat(totalLosses).isBetween(1, 2);
        }

        @Test
        @DisplayName("Should handle minimum army sizes")
        void shouldHandleMinimumArmySizes() {
            // Given
            var attacker = of(2); // Can roll 1 die
            var defender = of(1);  // Can roll 1 die

            // When
            Exercise2.BattleResult result = exercise.simulateSingleBattle(attacker, defender, random);

            // Then
            // Exactly one army should lose exactly one unit
            int attackerLosses = 2 - result.getAttackerArmy().getSize();
            int defenderLosses = 1 - result.getDefenderArmy().getSize();

            assertThat(attackerLosses + defenderLosses).isEqualTo(1);
            assertThat(attackerLosses).isBetween(0, 1);
            assertThat(defenderLosses).isBetween(0, 1);
        }

        @Test
        @DisplayName("Should reject invalid battle configurations")
        void shouldRejectInvalidBattleConfigurations() {
            // Given
            var invalidAttacker = of(1); // Can't attack
            var validDefender = of(1);

            // When & Then
            assertThatThrownBy(() -> exercise.simulateSingleBattle(invalidAttacker, validDefender, random))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Attacker must have at least 2 armies to attack");
        }
    }

    @Nested
    @DisplayName("Complete Battle Simulation Tests")
    class CompleteBattleSimulationTests {

        @Test
        @DisplayName("Should simulate complete battle until one army is eliminated")
        void shouldSimulateCompleteBattleUntilOneArmyIsEliminated() {
            // Given
            var attacker = of(5);
            var defender = of(3);

            // When
            Exercise2.BattleResult result = exercise.simulateCompleteBattle(attacker, defender, random);

            // Then
            // Either attacker has 1 army left (can't attack) or defender is eliminated
            boolean attackerCantContinue = result.getAttackerArmy().getSize() == 1;
            boolean defenderEliminated = result.getDefenderArmy().getSize() == 0;

            assertThat(attackerCantContinue || defenderEliminated).isTrue();
        }

        @Test
        @DisplayName("Should preserve total army count during battle")
        void shouldPreserveTotalArmyCountDuringBattle() {
            // Given
            var attacker = of(10);
            var defender = of(5);
            int initialTotal = attacker.getSize() + defender.getSize();

            // When
            Exercise2.BattleResult result = exercise.simulateCompleteBattle(attacker, defender, random);

            // Then
            int finalTotal = result.getAttackerArmy().getSize() + result.getDefenderArmy().getSize();
            assertThat(finalTotal).isLessThan(initialTotal); // Some armies should be lost
            assertThat(finalTotal).isGreaterThan(0); // But not all
        }

        @Test
        @DisplayName("Should handle edge case where attacker barely wins")
        void shouldHandleEdgeCaseWhereAttackerBarelyWins() {
            // Given
            var attacker = of(2); // Minimum attacking force
            var defender = of(1);  // Minimum defending force

            // When
            Exercise2.BattleResult result = exercise.simulateCompleteBattle(attacker, defender, random);

            // Then
            // Either attacker wins (defender eliminated) or attacker loses and can't continue
            if (result.getDefenderArmy().getSize() == 0) {
                // Attacker won
                assertThat(result.getAttackerArmy().getSize()).isGreaterThanOrEqualTo(1);
            } else {
                // Attacker lost and can't continue
                assertThat(result.getAttackerArmy().getSize()).isEqualTo(1);
            }
        }
    }

    @Nested
    @DisplayName("Probability Estimation Tests")
    class ProbabilityEstimationTests {

        @Test
        @DisplayName("Should estimate battle probabilities through simulation")
        void shouldEstimateBattleProbabilitiesThroughSimulation() {
            // Given
            var attacker = of(5);
            var defender = of(3);
            int simulations = 1000;

            // When
            double attackerWinProbability = exercise.estimateAttackerWinProbability(
                attacker, defender, simulations, new Random(123)
            );

            // Then
            assertThat(attackerWinProbability).isBetween(0.0, 1.0);
            // With 5 vs 3, attacker should have decent chance but not guaranteed
            assertThat(attackerWinProbability).isBetween(0.3, 0.9);
        }

        @Test
        @DisplayName("Should handle extreme probability cases")
        void shouldHandleExtremeProbabilityCases() {
            // Given
            var strongAttacker = of(20);
            var weakDefender = of(1);
            var weakAttacker = of(2);
            var strongDefender = of(20);
            int simulations = 100;
            Random testRandom = new Random(456);

            // When
            double strongAttackerProb = exercise.estimateAttackerWinProbability(
                strongAttacker, weakDefender, simulations, testRandom
            );
            double weakAttackerProb = exercise.estimateAttackerWinProbability(
                weakAttacker, strongDefender, simulations, testRandom
            );

            // Then
            assertThat(strongAttackerProb).isGreaterThan(0.8); // Strong attacker should almost always win
            assertThat(weakAttackerProb).isLessThan(0.2);     // Weak attacker should rarely win
        }

        @Test
        @DisplayName("Should provide consistent results with same random seed")
        void shouldProvideConsistentResultsWithSameRandomSeed() {
            // Given
            var attacker = of(4);
            var defender = of(2);
            int simulations = 100;

            // When
            double result1 = exercise.estimateAttackerWinProbability(
                attacker, defender, simulations, new Random(789)
            );
            double result2 = exercise.estimateAttackerWinProbability(
                attacker, defender, simulations, new Random(789)
            );

            // Then
            assertThat(result1).isEqualTo(result2);
        }

        @ParameterizedTest
        @DisplayName("Should reject invalid simulation parameters")
        @ValueSource(ints = {0, -1, -10})
        void shouldRejectInvalidSimulationParameters(int simulations) {
            // Given
            var attacker = of(3);
            var defender = of(2);

            // When & Then
            assertThatThrownBy(() -> exercise.estimateAttackerWinProbability(
                attacker, defender, simulations, random
            ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Number of simulations must be positive");
        }
    }
}
