package info.jab.cis194.homework9;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for Exercise 1 - Functor Implementation
 * Based on CIS-194 Homework 9 - Functors and Applicative Functors
 *
 * This test suite covers the Functor typeclass implementation including:
 * - Custom Maybe type as a functor
 * - List as a functor
 * - Function composition laws
 * - Functor laws verification
 */
@DisplayName("Exercise 1: Functor Implementation Tests")
class Exercise1Test {

    private Exercise1 exercise;

    @BeforeEach
    void setUp() {
        exercise = new Exercise1();
    }

    @Nested
    @DisplayName("Maybe Functor Tests")
    class MaybeFunctorTests {

        @Test
        @DisplayName("Should map over Just values")
        void shouldMapOverJustValues() {
            // Given
            var maybe = Exercise1.Maybe.just(5);
            Function<Integer, String> f = x -> "Value: " + x;

            // When
            var result = exercise.mapMaybe(f, maybe);

            // Then
            assertThat(result.isJust()).isTrue();
            assertThat(result.getValue()).isEqualTo("Value: 5");
        }

        @Test
        @DisplayName("Should map over Nothing values")
        void shouldMapOverNothingValues() {
            // Given
            Exercise1.Maybe<Integer> maybe = Exercise1.Maybe.nothing();
            Function<Integer, String> f = x -> "Value: " + x;

            // When
            var result = exercise.mapMaybe(f, maybe);

            // Then
            assertThat(result.isNothing()).isTrue();
        }

        @ParameterizedTest
        @DisplayName("Should satisfy functor identity law")
        @MethodSource("maybeFunctorTestCases")
        void shouldSatisfyFunctorIdentityLaw(Exercise1.Maybe<Integer> maybe) {
            // Given
            Function<Integer, Integer> identity = Function.identity();

            // When
            var result = exercise.mapMaybe(identity, maybe);

            // Then
            assertThat(result).isEqualTo(maybe);
        }

        @ParameterizedTest
        @DisplayName("Should satisfy functor composition law")
        @MethodSource("maybeFunctorTestCases")
        void shouldSatisfyFunctorCompositionLaw(Exercise1.Maybe<Integer> maybe) {
            // Given
            Function<Integer, String> f = x -> "f(" + x + ")";
            Function<String, String> g = x -> "g(" + x + ")";

            // When
            var result1 = exercise.mapMaybe(g.compose(f), maybe);
            var result2 = exercise.mapMaybe(g, exercise.mapMaybe(f, maybe));

            // Then
            assertThat(result1).isEqualTo(result2);
        }

        static Stream<Arguments> maybeFunctorTestCases() {
            return Stream.of(
                Arguments.of(Exercise1.Maybe.just(42)),
                Arguments.of(Exercise1.Maybe.just(0)),
                Arguments.of(Exercise1.Maybe.just(-10)),
                Arguments.of(Exercise1.Maybe.nothing())
            );
        }
    }

    @Nested
    @DisplayName("List Functor Tests")
    class ListFunctorTests {

        @Test
        @DisplayName("Should map over non-empty lists")
        void shouldMapOverNonEmptyLists() {
            // Given
            List<Integer> list = List.of(1, 2, 3, 4);
            Function<Integer, Integer> f = x -> x * 2;

            // When
            List<Integer> result = exercise.mapList(f, list);

            // Then
            assertThat(result).containsExactly(2, 4, 6, 8);
        }

        @Test
        @DisplayName("Should map over empty lists")
        void shouldMapOverEmptyLists() {
            // Given
            List<Integer> list = List.of();
            Function<Integer, Integer> f = x -> x * 2;

            // When
            List<Integer> result = exercise.mapList(f, list);

            // Then
            assertThat(result).isEmpty();
        }

        @ParameterizedTest
        @DisplayName("Should satisfy functor identity law for lists")
        @MethodSource("listFunctorTestCases")
        void shouldSatisfyFunctorIdentityLawForLists(List<Integer> list) {
            // Given
            Function<Integer, Integer> identity = Function.identity();

            // When
            List<Integer> result = exercise.mapList(identity, list);

            // Then
            assertThat(result).isEqualTo(list);
        }

        @ParameterizedTest
        @DisplayName("Should satisfy functor composition law for lists")
        @MethodSource("listFunctorTestCases")
        void shouldSatisfyFunctorCompositionLawForLists(List<Integer> list) {
            // Given
            Function<Integer, String> f = x -> "f(" + x + ")";
            Function<String, String> g = x -> "g(" + x + ")";

            // When
            List<String> result1 = exercise.mapList(g.compose(f), list);
            List<String> result2 = exercise.mapList(g, exercise.mapList(f, list));

            // Then
            assertThat(result1).isEqualTo(result2);
        }

        static Stream<Arguments> listFunctorTestCases() {
            return Stream.of(
                Arguments.of(List.of(1, 2, 3)),
                Arguments.of(List.of(42)),
                Arguments.of(List.of()),
                Arguments.of(List.of(-1, 0, 1, 2))
            );
        }
    }

    @Nested
    @DisplayName("Higher-Order Function Tests")
    class HigherOrderFunctionTests {

        @Test
        @DisplayName("Should implement total function correctly")
        void shouldImplementTotalFunctionCorrectly() {
            // Given
            List<Exercise1.Maybe<Integer>> maybes = List.of(
                Exercise1.Maybe.just(1),
                Exercise1.Maybe.just(2),
                Exercise1.Maybe.just(3)
            );

            // When
            Exercise1.Maybe<Integer> result = exercise.total(maybes);

            // Then
            assertThat(result.isJust()).isTrue();
            assertThat(result.getValue()).isEqualTo(6);
        }

        @Test
        @DisplayName("Should handle Nothing in total function")
        void shouldHandleNothingInTotalFunction() {
            // Given
            List<Exercise1.Maybe<Integer>> maybes = List.of(
                Exercise1.Maybe.just(1),
                Exercise1.Maybe.nothing(),
                Exercise1.Maybe.just(3)
            );

            // When
            Exercise1.Maybe<Integer> result = exercise.total(maybes);

            // Then
            assertThat(result.isNothing()).isTrue();
        }

        @Test
        @DisplayName("Should handle empty list in total function")
        void shouldHandleEmptyListInTotalFunction() {
            // Given
            List<Exercise1.Maybe<Integer>> maybes = List.of();

            // When
            Exercise1.Maybe<Integer> result = exercise.total(maybes);

            // Then
            assertThat(result.isJust()).isTrue();
            assertThat(result.getValue()).isEqualTo(0);
        }
    }
}
