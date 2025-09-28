package info.jab.cis194.homework9;

import java.util.List;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;

/**
 * Test class for Exercise 2 - Applicative Functor Implementation
 * Based on CIS-194 Homework 9 - Functors and Applicative Functors
 *
 * This test suite covers the Applicative Functor typeclass implementation including:
 * - Pure function for wrapping values
 * - Apply operation for applying wrapped functions to wrapped values
 * - Applicative laws verification
 * - Practical applications like form validation
 */
@DisplayName("Exercise 2: Applicative Functor Implementation Tests")
class Exercise2Test {

    private Exercise2 exercise;

    @BeforeEach
    void setUp() {
        exercise = new Exercise2();
    }

    @Nested
    @DisplayName("Maybe Applicative Tests")
    class MaybeApplicativeTests {

        @Test
        @DisplayName("Should apply function in Just to value in Just")
        void shouldApplyFunctionInJustToValueInJust() {
            // Given
            var maybeFunction = Exercise1.Maybe.just((Integer x) -> x * 2);
            var maybeValue = Exercise1.Maybe.just(5);

            // When
            var result = exercise.applyMaybe(maybeFunction, maybeValue);

            // Then
            assertThat(result.isJust()).isTrue();
            assertThat(result.getValue()).isEqualTo(10);
        }

        @Test
        @DisplayName("Should return Nothing when function is Nothing")
        void shouldReturnNothingWhenFunctionIsNothing() {
            // Given
            Exercise1.Maybe<Function<Integer, Integer>> maybeFunction = Exercise1.Maybe.nothing();
            var maybeValue = Exercise1.Maybe.just(5);

            // When
            var result = exercise.applyMaybe(maybeFunction, maybeValue);

            // Then
            assertThat(result.isNothing()).isTrue();
        }

        @Test
        @DisplayName("Should return Nothing when value is Nothing")
        void shouldReturnNothingWhenValueIsNothing() {
            // Given
            var maybeFunction = Exercise1.Maybe.just((Integer x) -> x * 2);
            Exercise1.Maybe<Integer> maybeValue = Exercise1.Maybe.nothing();

            // When
            var result = exercise.applyMaybe(maybeFunction, maybeValue);

            // Then
            assertThat(result.isNothing()).isTrue();
        }

        @Test
        @DisplayName("Should return Nothing when both function and value are Nothing")
        void shouldReturnNothingWhenBothFunctionAndValueAreNothing() {
            // Given
            Exercise1.Maybe<Function<Integer, Integer>> maybeFunction = Exercise1.Maybe.nothing();
            Exercise1.Maybe<Integer> maybeValue = Exercise1.Maybe.nothing();

            // When
            var result = exercise.applyMaybe(maybeFunction, maybeValue);

            // Then
            assertThat(result.isNothing()).isTrue();
        }

        @Test
        @DisplayName("Should work with curried functions")
        void shouldWorkWithCurriedFunctions() {
            // Given
            var add = (Integer x) -> (Integer y) -> x + y;
            var maybeAdd = Exercise1.Maybe.just(add);
            var maybeX = Exercise1.Maybe.just(3);
            var maybeY = Exercise1.Maybe.just(4);

            // When
            var partialResult = exercise.applyMaybe(maybeAdd, maybeX);
            var finalResult = exercise.applyMaybe(partialResult, maybeY);

            // Then
            assertThat(finalResult.isJust()).isTrue();
            assertThat(finalResult.getValue()).isEqualTo(7);
        }
    }

    @Nested
    @DisplayName("List Applicative Tests")
    class ListApplicativeTests {

        @Test
        @DisplayName("Should apply functions to values in cartesian product style")
        void shouldApplyFunctionsToValuesInCartesianProductStyle() {
            // Given
            List<Function<Integer, Integer>> functions = List.of(
                x -> x + 1,
                x -> x * 2
            );
            List<Integer> values = List.of(10, 20);

            // When
            List<Integer> result = exercise.applyList(functions, values);

            // Then
            // (x+1) applied to 10,20 = [11, 21]
            // (x*2) applied to 10,20 = [20, 40]
            // Combined: [11, 21, 20, 40]
            assertThat(result).containsExactly(11, 21, 20, 40);
        }

        @Test
        @DisplayName("Should handle empty function list")
        void shouldHandleEmptyFunctionList() {
            // Given
            List<Function<Integer, Integer>> functions = List.of();
            List<Integer> values = List.of(10, 20);

            // When
            List<Integer> result = exercise.applyList(functions, values);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should handle empty value list")
        void shouldHandleEmptyValueList() {
            // Given
            List<Function<Integer, Integer>> functions = List.of(x -> x + 1);
            List<Integer> values = List.of();

            // When
            List<Integer> result = exercise.applyList(functions, values);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should work with curried functions for lists")
        void shouldWorkWithCurriedFunctionsForLists() {
            // Given
            var add = (Integer x) -> (Integer y) -> x + y;
            List<Function<Integer, Function<Integer, Integer>>> functions = List.of(add);
            List<Integer> firstValues = List.of(1, 2);
            List<Integer> secondValues = List.of(10, 20);

            // When
            var partialResult = exercise.applyList(functions, firstValues);
            var finalResult = exercise.applyList(partialResult, secondValues);

            // Then
            // add(1) applied to [10,20] = [11, 21]
            // add(2) applied to [10,20] = [12, 22]
            // Combined: [11, 21, 12, 22]
            assertThat(finalResult).containsExactly(11, 21, 12, 22);
        }
    }

    @Nested
    @DisplayName("Practical Applications")
    class PracticalApplicationTests {

        @Test
        @DisplayName("Should validate person creation with all valid fields")
        void shouldValidatePersonCreationWithAllValidFields() {
            // Given
            String name = "John Doe";
            String email = "john@example.com";
            int age = 25;

            // When
            var result = exercise.createPerson(name, email, age);

            // Then
            assertThat(result.isJust()).isTrue();
            var person = result.getValue();
            assertThat(person.name()).isEqualTo(name);
            assertThat(person.email()).isEqualTo(email);
            assertThat(person.age()).isEqualTo(age);
        }

        @Test
        @DisplayName("Should fail validation with invalid name")
        void shouldFailValidationWithInvalidName() {
            // Given
            String name = ""; // invalid
            String email = "john@example.com";
            int age = 25;

            // When
            var result = exercise.createPerson(name, email, age);

            // Then
            assertThat(result.isNothing()).isTrue();
        }

        @Test
        @DisplayName("Should fail validation with invalid email")
        void shouldFailValidationWithInvalidEmail() {
            // Given
            String name = "John Doe";
            String email = "invalid-email"; // invalid
            int age = 25;

            // When
            var result = exercise.createPerson(name, email, age);

            // Then
            assertThat(result.isNothing()).isTrue();
        }

        @Test
        @DisplayName("Should fail validation with invalid age")
        void shouldFailValidationWithInvalidAge() {
            // Given
            String name = "John Doe";
            String email = "john@example.com";
            int age = -5; // invalid

            // When
            var result = exercise.createPerson(name, email, age);

            // Then
            assertThat(result.isNothing()).isTrue();
        }

        @Test
        @DisplayName("Should fail validation with multiple invalid fields")
        void shouldFailValidationWithMultipleInvalidFields() {
            // Given
            String name = ""; // invalid
            String email = "invalid"; // invalid
            int age = -1; // invalid

            // When
            var result = exercise.createPerson(name, email, age);

            // Then
            assertThat(result.isNothing()).isTrue();
        }
    }

    @Nested
    @DisplayName("Applicative Laws")
    class ApplicativeLawsTests {

        @Test
        @DisplayName("Should satisfy identity law for Maybe")
        void shouldSatisfyIdentityLawForMaybe() {
            // Given
            var value = Exercise1.Maybe.just(42);
            var identity = Exercise1.Maybe.just(Function.<Integer>identity());

            // When
            var result = exercise.applyMaybe(identity, value);

            // Then
            assertThat(result).isEqualTo(value);
        }

        @Test
        @DisplayName("Should satisfy composition law for Maybe")
        void shouldSatisfyCompositionLawForMaybe() {
            // Given
            Function<Integer, String> f = x -> "f(" + x + ")";
            Function<String, String> g = x -> "g(" + x + ")";
            var value = Exercise1.Maybe.just(42);

            var maybeF = Exercise1.Maybe.just(f);
            var maybeG = Exercise1.Maybe.just(g);
            var maybeCompose = Exercise1.Maybe.just((Function<Integer, String> func) -> g.compose(func));

            // When
            var result1 = exercise.applyMaybe(exercise.applyMaybe(maybeCompose, maybeF), value);
            var result2 = exercise.applyMaybe(maybeG, exercise.applyMaybe(maybeF, value));

            // Then
            assertThat(result1).isEqualTo(result2);
        }

        @Test
        @DisplayName("Should satisfy identity law for List")
        void shouldSatisfyIdentityLawForList() {
            // Given
            var values = List.of(1, 2, 3);
            var identities = List.of(Function.<Integer>identity());

            // When
            var result = exercise.applyList(identities, values);

            // Then
            assertThat(result).isEqualTo(values);
        }
    }
}