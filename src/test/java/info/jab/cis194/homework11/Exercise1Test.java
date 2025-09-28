package info.jab.cis194.homework11;

import java.util.List;
import java.util.function.Function;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for Exercise 1: Advanced Monadic Operations and Functional Composition
 *
 * This exercise focuses on implementing advanced monadic operations including
 * monad transformers, functional composition patterns, and higher-order functions
 * that demonstrate deep understanding of functional programming concepts.
 *
 * Based on CIS-194 Week 11: Advanced Functional Programming Patterns
 */
@DisplayName("Exercise 1: Advanced Monadic Operations")
class Exercise1Test {

    @Nested
    @DisplayName("Maybe Monad Implementation")
    class MaybeMonadImplementation {

        @Test
        @DisplayName("Should create Just value with pure")
        void shouldCreateJustValueWithPure() {
            // Given
            var value = 42;

            // When
            var maybe = Exercise1.Maybe.pure(value);

            // Then
            assertThat(maybe.isJust()).isTrue();
            assertThat(maybe.getValue()).isEqualTo(42);
        }

        @Test
        @DisplayName("Should create Nothing value")
        void shouldCreateNothingValue() {
            // When
            var maybe = Exercise1.Maybe.<Integer>nothing();

            // Then
            assertThat(maybe.isJust()).isFalse();
            assertThat(maybe.isNothing()).isTrue();
        }

        @Test
        @DisplayName("Should map over Just value")
        void shouldMapOverJustValue() {
            // Given
            var maybe = Exercise1.Maybe.pure(10);
            Function<Integer, String> f = x -> "Value: " + x;

            // When
            var result = maybe.fmap(f);

            // Then
            assertThat(result.isJust()).isTrue();
            assertThat(result.getValue()).isEqualTo("Value: 10");
        }

        @Test
        @DisplayName("Should map over Nothing value")
        void shouldMapOverNothingValue() {
            // Given
            var maybe = Exercise1.Maybe.<Integer>nothing();
            Function<Integer, String> f = x -> "Value: " + x;

            // When
            var result = maybe.fmap(f);

            // Then
            assertThat(result.isNothing()).isTrue();
        }

        @Test
        @DisplayName("Should bind Just value with function returning Just")
        void shouldBindJustValueWithFunctionReturningJust() {
            // Given
            var maybe = Exercise1.Maybe.pure(5);
            Function<Integer, Exercise1.Maybe<String>> f = x -> Exercise1.Maybe.pure("Number: " + x);

            // When
            var result = maybe.bind(f);

            // Then
            assertThat(result.isJust()).isTrue();
            assertThat(result.getValue()).isEqualTo("Number: 5");
        }

        @Test
        @DisplayName("Should bind Just value with function returning Nothing")
        void shouldBindJustValueWithFunctionReturningNothing() {
            // Given
            var maybe = Exercise1.Maybe.pure(5);
            Function<Integer, Exercise1.Maybe<String>> f = x -> Exercise1.Maybe.nothing();

            // When
            var result = maybe.bind(f);

            // Then
            assertThat(result.isNothing()).isTrue();
        }

        @Test
        @DisplayName("Should bind Nothing value")
        void shouldBindNothingValue() {
            // Given
            var maybe = Exercise1.Maybe.<Integer>nothing();
            Function<Integer, Exercise1.Maybe<String>> f = x -> Exercise1.Maybe.pure("Number: " + x);

            // When
            var result = maybe.bind(f);

            // Then
            assertThat(result.isNothing()).isTrue();
        }
    }

    @Nested
    @DisplayName("List Monad Implementation")
    class ListMonadImplementation {

        @Test
        @DisplayName("Should create list with pure")
        void shouldCreateListWithPure() {
            // Given
            var value = 42;

            // When
            var listMonad = Exercise1.ListMonad.pure(value);

            // Then
            assertThat(listMonad.getValues()).containsExactly(42);
        }

        @Test
        @DisplayName("Should map over list values")
        void shouldMapOverListValues() {
            // Given
            var listMonad = Exercise1.ListMonad.of(List.of(1, 2, 3));
            Function<Integer, String> f = x -> "Item: " + x;

            // When
            var result = listMonad.fmap(f);

            // Then
            assertThat(result.getValues()).containsExactly("Item: 1", "Item: 2", "Item: 3");
        }

        @Test
        @DisplayName("Should bind list with function returning lists")
        void shouldBindListWithFunctionReturningLists() {
            // Given
            var listMonad = Exercise1.ListMonad.of(List.of(1, 2, 3));
            Function<Integer, Exercise1.ListMonad<String>> f = x ->
                Exercise1.ListMonad.of(List.of("a" + x, "b" + x));

            // When
            var result = listMonad.bind(f);

            // Then
            assertThat(result.getValues()).containsExactly("a1", "b1", "a2", "b2", "a3", "b3");
        }

        @Test
        @DisplayName("Should handle empty list in bind")
        void shouldHandleEmptyListInBind() {
            // Given
            var listMonad = Exercise1.ListMonad.of(List.<Integer>of());
            Function<Integer, Exercise1.ListMonad<String>> f = x ->
                Exercise1.ListMonad.of(List.of("a" + x, "b" + x));

            // When
            var result = listMonad.bind(f);

            // Then
            assertThat(result.getValues()).isEmpty();
        }
    }

    @Nested
    @DisplayName("Function Composition")
    class FunctionComposition {

        @Test
        @DisplayName("Should compose two functions")
        void shouldComposeTwoFunctions() {
            // Given
            Function<Integer, Integer> f = x -> x * 2;
            Function<Integer, String> g = x -> "Result: " + x;

            // When
            var composed = Exercise1.compose(g, f);
            var result = composed.apply(5);

            // Then
            assertThat(result).isEqualTo("Result: 10");
        }

        @Test
        @DisplayName("Should compose multiple functions with pipe")
        void shouldComposeMultipleFunctionsWithPipe() {
            // Given
            Function<Integer, Integer> addTen = x -> x + 10;
            Function<Integer, Integer> multiplyByTwo = x -> x * 2;
            Function<Integer, String> toString = x -> "Value: " + x;

            // When
            var pipeline = Exercise1.pipe(addTen, multiplyByTwo, toString);
            var result = pipeline.apply(5);

            // Then
            assertThat(result).isEqualTo("Value: 30");
        }

        @Test
        @DisplayName("Should apply function multiple times")
        void shouldApplyFunctionMultipleTimes() {
            // Given
            Function<Integer, Integer> addOne = x -> x + 1;

            // When
            var result = Exercise1.applyN(addOne, 3, 10);

            // Then
            assertThat(result).isEqualTo(13);
        }

        @Test
        @DisplayName("Should apply function zero times")
        void shouldApplyFunctionZeroTimes() {
            // Given
            Function<Integer, Integer> addOne = x -> x + 1;

            // When
            var result = Exercise1.applyN(addOne, 0, 10);

            // Then
            assertThat(result).isEqualTo(10);
        }
    }

    @Nested
    @DisplayName("Kleisli Composition")
    class KleisliComposition {

        @Test
        @DisplayName("Should compose Kleisli arrows for Maybe")
        void shouldComposeKleisliArrowsForMaybe() {
            // Given
            Function<Integer, Exercise1.Maybe<Integer>> f = x ->
                x > 0 ? Exercise1.Maybe.pure(x * 2) : Exercise1.Maybe.nothing();
            Function<Integer, Exercise1.Maybe<String>> g = x ->
                x < 100 ? Exercise1.Maybe.pure("Result: " + x) : Exercise1.Maybe.nothing();

            // When
            var composed = Exercise1.kleisliCompose(g, f);
            var result = composed.apply(10);

            // Then
            assertThat(result.isJust()).isTrue();
            assertThat(result.getValue()).isEqualTo("Result: 20");
        }

        @Test
        @DisplayName("Should handle failure in first Kleisli arrow")
        void shouldHandleFailureInFirstKleisliArrow() {
            // Given
            Function<Integer, Exercise1.Maybe<Integer>> f = x ->
                x > 0 ? Exercise1.Maybe.pure(x * 2) : Exercise1.Maybe.nothing();
            Function<Integer, Exercise1.Maybe<String>> g = x ->
                x < 100 ? Exercise1.Maybe.pure("Result: " + x) : Exercise1.Maybe.nothing();

            // When
            var composed = Exercise1.kleisliCompose(g, f);
            var result = composed.apply(-5);

            // Then
            assertThat(result.isNothing()).isTrue();
        }

        @Test
        @DisplayName("Should handle failure in second Kleisli arrow")
        void shouldHandleFailureInSecondKleisliArrow() {
            // Given
            Function<Integer, Exercise1.Maybe<Integer>> f = x ->
                x > 0 ? Exercise1.Maybe.pure(x * 200) : Exercise1.Maybe.nothing();
            Function<Integer, Exercise1.Maybe<String>> g = x ->
                x < 100 ? Exercise1.Maybe.pure("Result: " + x) : Exercise1.Maybe.nothing();

            // When
            var composed = Exercise1.kleisliCompose(g, f);
            var result = composed.apply(10);

            // Then
            assertThat(result.isNothing()).isTrue();
        }
    }

    @Nested
    @DisplayName("Monadic Utilities")
    class MonadicUtilities {

        @Test
        @DisplayName("Should sequence Maybe values with all Just")
        void shouldSequenceMaybeValuesWithAllJust() {
            // Given
            var maybes = List.of(
                Exercise1.Maybe.pure(1),
                Exercise1.Maybe.pure(2),
                Exercise1.Maybe.pure(3)
            );

            // When
            var result = Exercise1.sequenceMaybe(maybes);

            // Then
            assertThat(result.isJust()).isTrue();
            assertThat(result.getValue()).containsExactly(1, 2, 3);
        }

        @Test
        @DisplayName("Should sequence Maybe values with one Nothing")
        void shouldSequenceMaybeValuesWithOneNothing() {
            // Given
            var maybes = List.of(
                Exercise1.Maybe.pure(1),
                Exercise1.Maybe.<Integer>nothing(),
                Exercise1.Maybe.pure(3)
            );

            // When
            var result = Exercise1.sequenceMaybe(maybes);

            // Then
            assertThat(result.isNothing()).isTrue();
        }

        @Test
        @DisplayName("Should traverse list with function returning Maybe")
        void shouldTraverseListWithFunctionReturningMaybe() {
            // Given
            var numbers = List.of(1, 2, 3, 4);
            Function<Integer, Exercise1.Maybe<String>> f = x ->
                x <= 3 ? Exercise1.Maybe.pure("Item: " + x) : Exercise1.Maybe.nothing();

            // When
            var result = Exercise1.traverseMaybe(numbers, f);

            // Then
            assertThat(result.isNothing()).isTrue();
        }

        @Test
        @DisplayName("Should traverse list successfully with all valid values")
        void shouldTraverseListSuccessfullyWithAllValidValues() {
            // Given
            var numbers = List.of(1, 2, 3);
            Function<Integer, Exercise1.Maybe<String>> f = x ->
                x <= 3 ? Exercise1.Maybe.pure("Item: " + x) : Exercise1.Maybe.nothing();

            // When
            var result = Exercise1.traverseMaybe(numbers, f);

            // Then
            assertThat(result.isJust()).isTrue();
            assertThat(result.getValue()).containsExactly("Item: 1", "Item: 2", "Item: 3");
        }
    }
}
