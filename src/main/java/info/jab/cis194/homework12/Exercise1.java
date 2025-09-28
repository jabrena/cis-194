package info.jab.cis194.homework12;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * Exercise 1 - Risk Battle Simulation Basics
 * Functional implementation of Risk battle mechanics based on CIS-194 Homework 12
 *
 * This implementation follows functional programming principles:
 * - Immutable data structures
 * - Pure functions (no side effects except for random generation)
 * - Value objects with proper equality
 * - Comprehensive battle resolution logic
 */
public class Exercise1 {

    /**
     * DieValue represents the result of rolling a six-sided die.
     * This is a value object that ensures only valid die values (1-6) can be created.
     */
    public static final class DieValue implements Comparable<DieValue> {
        private final int value;

        private DieValue(int value) {
            if (value < 1 || value > 6) {
                throw new IllegalArgumentException("Die value must be between 1 and 6, got: " + value);
            }
            this.value = value;
        }

        /**
         * Create a DieValue with the specified value
         * @param value the die value (must be 1-6)
         * @return new DieValue instance
         * @throws IllegalArgumentException if value is not between 1 and 6
         */
        public static DieValue of(int value) {
            return new DieValue(value);
        }

        /**
         * Get the numeric value of this die
         * @return the die value (1-6)
         */
        public int getValue() {
            return value;
        }

        @Override
        public int compareTo(DieValue other) {
            return Integer.compare(this.value, other.value);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            DieValue dieValue = (DieValue) obj;
            return value == dieValue.value;
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public String toString() {
            return "DieValue{" + value + "}";
        }
    }

    /**
     * BattleResult represents the outcome of comparing two dice
     */
    public enum BattleResult {
        ATTACKER_WINS,
        DEFENDER_WINS
    }

    /**
     * BattleOutcome represents the result of a complete battle with losses for both sides
     */
    public static final class BattleOutcome {
        private final int attackerLosses;
        private final int defenderLosses;

        public BattleOutcome(int attackerLosses, int defenderLosses) {
            if (attackerLosses < 0 || defenderLosses < 0) {
                throw new IllegalArgumentException("Losses cannot be negative");
            }
            this.attackerLosses = attackerLosses;
            this.defenderLosses = defenderLosses;
        }

        public int getAttackerLosses() {
            return attackerLosses;
        }

        public int getDefenderLosses() {
            return defenderLosses;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            BattleOutcome that = (BattleOutcome) obj;
            return attackerLosses == that.attackerLosses && defenderLosses == that.defenderLosses;
        }

        @Override
        public int hashCode() {
            return Objects.hash(attackerLosses, defenderLosses);
        }

        @Override
        public String toString() {
            return "BattleOutcome{attackerLosses=" + attackerLosses + ", defenderLosses=" + defenderLosses + "}";
        }
    }

    /**
     * Roll a single six-sided die
     * @param random the random number generator to use
     * @return a DieValue representing the roll result
     */
    public DieValue rollDie(Random random) {
        Objects.requireNonNull(random, "Random generator cannot be null");
        int roll = random.nextInt(6) + 1; // Generate 1-6
        return DieValue.of(roll);
    }

    /**
     * Roll multiple dice
     * @param numberOfDice the number of dice to roll
     * @param random the random number generator to use
     * @return list of DieValue results
     * @throws IllegalArgumentException if numberOfDice is negative
     */
    public List<DieValue> rollDice(int numberOfDice, Random random) {
        if (numberOfDice < 0) {
            throw new IllegalArgumentException("Number of dice cannot be negative: " + numberOfDice);
        }
        Objects.requireNonNull(random, "Random generator cannot be null");

        return IntStream.range(0, numberOfDice)
                .mapToObj(i -> rollDie(random))
                .toList();
    }

    /**
     * Sort dice in descending order (highest first)
     * @param dice the list of dice to sort
     * @return new sorted list in descending order
     */
    public List<DieValue> sortDiceDescending(List<DieValue> dice) {
        Objects.requireNonNull(dice, "Dice list cannot be null");
        return dice.stream()
                .sorted(Collections.reverseOrder())
                .toList();
    }

    /**
     * Compare two dice according to Risk rules
     * In Risk, the defender wins ties
     * @param attackerDie the attacker's die
     * @param defenderDie the defender's die
     * @return BattleResult indicating who wins this comparison
     */
    public BattleResult compareDice(DieValue attackerDie, DieValue defenderDie) {
        Objects.requireNonNull(attackerDie, "Attacker die cannot be null");
        Objects.requireNonNull(defenderDie, "Defender die cannot be null");

        // Defender wins ties in Risk
        return attackerDie.getValue() > defenderDie.getValue()
            ? BattleResult.ATTACKER_WINS
            : BattleResult.DEFENDER_WINS;
    }

    /**
     * Resolve a complete battle between attacker and defender dice
     * @param attackerRolls the attacker's dice rolls
     * @param defenderRolls the defender's dice rolls
     * @return BattleOutcome with losses for each side
     */
    public BattleOutcome resolveBattle(List<DieValue> attackerRolls, List<DieValue> defenderRolls) {
        Objects.requireNonNull(attackerRolls, "Attacker rolls cannot be null");
        Objects.requireNonNull(defenderRolls, "Defender rolls cannot be null");

        // Sort both lists in descending order
        List<DieValue> sortedAttackerRolls = sortDiceDescending(attackerRolls);
        List<DieValue> sortedDefenderRolls = sortDiceDescending(defenderRolls);

        int attackerLosses = 0;
        int defenderLosses = 0;

        // Compare dice pairwise, starting with the highest
        int comparisons = Math.min(sortedAttackerRolls.size(), sortedDefenderRolls.size());

        for (int i = 0; i < comparisons; i++) {
            BattleResult result = compareDice(sortedAttackerRolls.get(i), sortedDefenderRolls.get(i));

            if (result == BattleResult.ATTACKER_WINS) {
                defenderLosses++;
            } else {
                attackerLosses++;
            }
        }

        return new BattleOutcome(attackerLosses, defenderLosses);
    }
}
