package info.jab.cis194.homework12;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.IntStream;

/**
 * Exercise 2 - Advanced Risk Battle Simulation
 * Complete Risk battle simulation with army management based on CIS-194 Homework 12
 *
 * This implementation follows functional programming principles:
 * - Immutable data structures
 * - Pure functions (except for random generation)
 * - Value objects with proper equality
 * - Complete battle simulation with probability estimation
 */
public class Exercise2 {

    private final Exercise1 basicBattleLogic;

    public Exercise2() {
        this.basicBattleLogic = new Exercise1();
    }

    /**
     * Trampoline utility for tail-recursive optimization
     * Prevents stack overflow in recursive battle simulations
     */
    private static abstract class Trampoline<T> {
        public abstract T get();
        public abstract Trampoline<T> jump();
        public abstract boolean isComplete();

        public static <T> Trampoline<T> done(final T result) {
            return new Trampoline<T>() {
                @Override
                public T get() { return result; }
                @Override
                public Trampoline<T> jump() { return this; }
                @Override
                public boolean isComplete() { return true; }
            };
        }

        public static <T> Trampoline<T> more(final Supplier<Trampoline<T>> supplier) {
            return new Trampoline<T>() {
                @Override
                public T get() { throw new RuntimeException("Not done yet"); }
                @Override
                public Trampoline<T> jump() { return supplier.get(); }
                @Override
                public boolean isComplete() { return false; }
            };
        }

        public T execute() {
            Trampoline<T> trampoline = this;
            while (!trampoline.isComplete()) {
                trampoline = trampoline.jump();
            }
            return trampoline.get();
        }
    }

    /**
     * Army represents a collection of military units in Risk
     * This is an immutable value object that ensures only valid army sizes
     */
    public static final class Army {
        private final int size;

        private Army(int size) {
            if (size < 0) {
                throw new IllegalArgumentException("Army size cannot be negative, got: " + size);
            }
            this.size = size;
        }

        /**
         * Create an Army with the specified size
         * @param size the army size (must be non-negative)
         * @return new Army instance
         * @throws IllegalArgumentException if size is negative
         */
        public static Army of(int size) {
            return new Army(size);
        }

        /**
         * Get the size of this army
         * @return the number of units in the army
         */
        public int getSize() {
            return size;
        }

        /**
         * Create a new Army with losses applied
         * @param losses the number of units lost
         * @return new Army with reduced size (can be 0 for eliminated army)
         * @throws IllegalArgumentException if losses exceed army size
         */
        public Army withLosses(int losses) {
            if (losses < 0) {
                throw new IllegalArgumentException("Losses cannot be negative: " + losses);
            }
            if (losses > size) {
                throw new IllegalArgumentException(
                    "Cannot lose more units than available. Army: " + size + ", Losses: " + losses
                );
            }
            if (losses == 0) {
                return this; // No change
            }
            return new Army(size - losses);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Army army = (Army) obj;
            return size == army.size;
        }

        @Override
        public int hashCode() {
            return Objects.hash(size);
        }

        @Override
        public String toString() {
            return "Army{size=" + size + "}";
        }
    }

    /**
     * BattleResult represents the outcome of a battle with updated army sizes
     */
    public static final class BattleResult {
        private final Army attackerArmy;
        private final Army defenderArmy;

        public BattleResult(Army attackerArmy, Army defenderArmy) {
            this.attackerArmy = Objects.requireNonNull(attackerArmy, "Attacker army cannot be null");
            this.defenderArmy = Objects.requireNonNull(defenderArmy, "Defender army cannot be null");
        }

        public Army getAttackerArmy() {
            return attackerArmy;
        }

        public Army getDefenderArmy() {
            return defenderArmy;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            BattleResult that = (BattleResult) obj;
            return Objects.equals(attackerArmy, that.attackerArmy) &&
                   Objects.equals(defenderArmy, that.defenderArmy);
        }

        @Override
        public int hashCode() {
            return Objects.hash(attackerArmy, defenderArmy);
        }

        @Override
        public String toString() {
            return "BattleResult{attackerArmy=" + attackerArmy + ", defenderArmy=" + defenderArmy + "}";
        }
    }

    /**
     * Determine the number of dice an attacker can roll based on army size
     * Risk rules: Attacker can roll up to 3 dice, but must leave 1 army behind
     * @param army the attacking army
     * @return number of dice the attacker can roll (0-3)
     */
    public int getAttackerDice(Army army) {
        Objects.requireNonNull(army, "Army cannot be null");

        int availableForAttack = army.getSize() - 1; // Must leave 1 army behind
        return Math.max(0, Math.min(3, availableForAttack));
    }

    /**
     * Determine the number of dice a defender can roll based on army size
     * Risk rules: Defender can roll up to 2 dice
     * @param army the defending army
     * @return number of dice the defender can roll (1-2)
     */
    public int getDefenderDice(Army army) {
        Objects.requireNonNull(army, "Army cannot be null");

        return Math.min(2, army.getSize());
    }

    /**
     * Check if an army can attack (has at least 2 units)
     * @param army the army to check
     * @return true if the army can attack
     */
    public boolean canAttack(Army army) {
        Objects.requireNonNull(army, "Army cannot be null");
        return army.getSize() >= 2;
    }

    /**
     * Check if an army can defend (has at least 1 unit)
     * @param army the army to check
     * @return true if the army can defend
     */
    public boolean canDefend(Army army) {
        Objects.requireNonNull(army, "Army cannot be null");
        return army.getSize() >= 1;
    }

    /**
     * Simulate a single battle between two armies
     * @param attackerArmy the attacking army
     * @param defenderArmy the defending army
     * @param random the random number generator
     * @return BattleResult with updated army sizes
     * @throws IllegalArgumentException if attacker cannot attack
     */
    public BattleResult simulateSingleBattle(Army attackerArmy, Army defenderArmy, Random random) {
        Objects.requireNonNull(attackerArmy, "Attacker army cannot be null");
        Objects.requireNonNull(defenderArmy, "Defender army cannot be null");
        Objects.requireNonNull(random, "Random generator cannot be null");

        if (!canAttack(attackerArmy)) {
            throw new IllegalArgumentException("Attacker must have at least 2 armies to attack");
        }
        if (!canDefend(defenderArmy)) {
            throw new IllegalArgumentException("Defender must have at least 1 army to defend");
        }

        // Determine number of dice for each side
        int attackerDiceCount = getAttackerDice(attackerArmy);
        int defenderDiceCount = getDefenderDice(defenderArmy);

        // Roll dice for both sides
        List<Exercise1.DieValue> attackerRolls = basicBattleLogic.rollDice(attackerDiceCount, random);
        List<Exercise1.DieValue> defenderRolls = basicBattleLogic.rollDice(defenderDiceCount, random);

        // Resolve the battle
        Exercise1.BattleOutcome outcome = basicBattleLogic.resolveBattle(attackerRolls, defenderRolls);

        // Apply losses to armies
        Army newAttackerArmy = attackerArmy.withLosses(outcome.getAttackerLosses());
        Army newDefenderArmy = defenderArmy.withLosses(outcome.getDefenderLosses());

        return new BattleResult(newAttackerArmy, newDefenderArmy);
    }

    /**
     * Simulate a complete battle until one side cannot continue using functional recursion with trampoline
     * @param attackerArmy the attacking army
     * @param defenderArmy the defending army
     * @param random the random number generator
     * @return final BattleResult when battle ends
     */
    public BattleResult simulateCompleteBattle(Army attackerArmy, Army defenderArmy, Random random) {
        Objects.requireNonNull(attackerArmy, "Attacker army cannot be null");
        Objects.requireNonNull(defenderArmy, "Defender army cannot be null");
        Objects.requireNonNull(random, "Random generator cannot be null");

        return simulateCompleteBattleTrampoline(attackerArmy, defenderArmy, random).execute();
    }

    /**
     * Tail-recursive implementation of complete battle simulation using trampoline pattern
     */
    private Trampoline<BattleResult> simulateCompleteBattleTrampoline(Army attackerArmy, Army defenderArmy, Random random) {
        // Base case: battle is over
        if (!canAttack(attackerArmy) || !canDefend(defenderArmy)) {
            return Trampoline.done(new BattleResult(attackerArmy, defenderArmy));
        }

        // Recursive case: continue battle
        return Trampoline.more(() -> {
            BattleResult singleBattleResult = simulateSingleBattle(attackerArmy, defenderArmy, random);
            return simulateCompleteBattleTrampoline(
                singleBattleResult.getAttackerArmy(),
                singleBattleResult.getDefenderArmy(),
                random
            );
        });
    }

    /**
     * Estimate the probability that the attacker wins through Monte Carlo simulation using functional approach
     * @param attackerArmy the attacking army
     * @param defenderArmy the defending army
     * @param simulations the number of simulations to run
     * @param random the random number generator
     * @return probability between 0.0 and 1.0 that attacker wins
     * @throws IllegalArgumentException if simulations is not positive
     */
    public double estimateAttackerWinProbability(
        Army attackerArmy,
        Army defenderArmy,
        int simulations,
        Random random
    ) {
        if (simulations <= 0) {
            throw new IllegalArgumentException("Number of simulations must be positive: " + simulations);
        }
        Objects.requireNonNull(attackerArmy, "Attacker army cannot be null");
        Objects.requireNonNull(defenderArmy, "Defender army cannot be null");
        Objects.requireNonNull(random, "Random generator cannot be null");

        // Functional Monte Carlo simulation using streams and higher-order functions
        var attackerWins = IntStream.range(0, simulations)
            .mapToObj(i -> simulateCompleteBattle(attackerArmy, defenderArmy, random))
            .map(BattleResult::getDefenderArmy)
            .map(Army::getSize)
            .filter(size -> size == 0) // Attacker wins when defender is eliminated
            .count();

        return (double) attackerWins / simulations;
    }
}
