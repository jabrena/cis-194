package info.jab.cis194.homework11;

import info.jab.cis194.homework11.Exercise1.ListMonad;
import info.jab.cis194.homework11.Exercise1.Maybe;
import java.util.List;
import java.util.function.Function;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for Exercise 1: Advanced Monadic Operations and Functional Composition
 *
 * <p>This test class verifies the implementation of advanced monadic operations including
 * monad transformers, functional composition patterns, and higher-order functions
 * that demonstrate deep understanding of functional programming concepts.
 *
 * <p>Test structure follows the Arrange-Act-Assert (AAA) pattern with clear
 * Given-When-Then sections for maximum readability and maintainability.
 *
 * <p>Based on CIS-194 Week 11: Advanced Functional Programming Patterns
 */
@DisplayName("Exercise 1: Advanced Monadic Operations and Functional Composition")
class Exercise1Test {

    @Nested
    @DisplayName("Maybe Monad Implementation")
    class MaybeMonadImplementation {

        @Test
        @DisplayName("Should create Just value when using pure constructor")
        void shouldCreateJustValueWhenUsingPureConstructor() {
            // Given
            Integer inputValue = 42;

            // When
            Maybe<Integer> actualMaybe = Maybe.pure(inputValue);

            // Then
            assertThat(actualMaybe.isJust())
                .as("Maybe created with pure should be Just")
                .isTrue();
            assertThat(actualMaybe.getValue())
                .as("Just value should contain the original input")
                .isEqualTo(42);
        }

        @Test
        @DisplayName("Should create Nothing value when using nothing constructor")
        void shouldCreateNothingValueWhenUsingNothingConstructor() {
            // Given - no input needed for Nothing

            // When
            Maybe<Integer> actualMaybe = Maybe.<Integer>nothing();

            // Then
            assertThat(actualMaybe.isJust())
                .as("Maybe created with nothing should not be Just")
                .isFalse();
            assertThat(actualMaybe.isNothing())
                .as("Maybe created with nothing should be Nothing")
                .isTrue();
        }

        @Test
        @DisplayName("Should apply function transformation when mapping over Just value")
        void shouldApplyFunctionTransformationWhenMappingOverJustValue() {
            // Given
            Maybe<Integer> inputMaybe = Maybe.pure(10);
            Function<Integer, String> transformationFunction = x -> "Value: " + x;

            // When
            Maybe<String> actualResult = inputMaybe.fmap(transformationFunction);

            // Then
            assertThat(actualResult.isJust())
                .as("Mapping over Just should result in Just")
                .isTrue();
            assertThat(actualResult.getValue())
                .as("Mapped value should be transformed by the function")
                .isEqualTo("Value: 10");
        }

        @Test
        @DisplayName("Should preserve Nothing when mapping over Nothing value")
        void shouldPreserveNothingWhenMappingOverNothingValue() {
            // Given
            Maybe<Integer> inputMaybe = Maybe.<Integer>nothing();
            Function<Integer, String> transformationFunction = x -> "Value: " + x;

            // When
            Maybe<String> actualResult = inputMaybe.fmap(transformationFunction);

            // Then
            assertThat(actualResult.isNothing())
                .as("Mapping over Nothing should preserve Nothing")
                .isTrue();
        }

        @Test
        @DisplayName("Should chain computations when binding Just value with function returning Just")
        void shouldChainComputationsWhenBindingJustValueWithFunctionReturningJust() {
            // Given
            Maybe<Integer> inputMaybe = Maybe.pure(5);
            Function<Integer, Maybe<String>> monadicFunction =
                x -> Maybe.pure("Number: " + x);

            // When
            Maybe<String> actualResult = inputMaybe.bind(monadicFunction);

            // Then
            assertThat(actualResult.isJust())
                .as("Binding Just with function returning Just should result in Just")
                .isTrue();
            assertThat(actualResult.getValue())
                .as("Bound value should be the result of the monadic function")
                .isEqualTo("Number: 5");
        }

        @Test
        @DisplayName("Should short-circuit to Nothing when binding Just value with function returning Nothing")
        void shouldShortCircuitToNothingWhenBindingJustValueWithFunctionReturningNothing() {
            // Given
            Maybe<Integer> inputMaybe = Maybe.pure(5);
            Function<Integer, Maybe<String>> monadicFunction = x -> Maybe.nothing();

            // When
            Maybe<String> actualResult = inputMaybe.bind(monadicFunction);

            // Then
            assertThat(actualResult.isNothing())
                .as("Binding Just with function returning Nothing should result in Nothing")
                .isTrue();
        }

        @Test
        @DisplayName("Should preserve Nothing when binding Nothing value with any function")
        void shouldPreserveNothingWhenBindingNothingValueWithAnyFunction() {
            // Given
            Maybe<Integer> inputMaybe = Maybe.<Integer>nothing();
            Function<Integer, Maybe<String>> monadicFunction =
                x -> Maybe.pure("Number: " + x);

            // When
            Maybe<String> actualResult = inputMaybe.bind(monadicFunction);

            // Then
            assertThat(actualResult.isNothing())
                .as("Binding Nothing with any function should preserve Nothing")
                .isTrue();
        }
    }

    @Nested
    @DisplayName("List Monad Implementation")
    class ListMonadImplementation {

        @Test
        @DisplayName("Should create singleton list when using pure constructor")
        void shouldCreateSingletonListWhenUsingPureConstructor() {
            // Given
            Integer inputValue = 42;

            // When
            ListMonad<Integer> actualListMonad = ListMonad.pure(inputValue);

            // Then
            assertThat(actualListMonad.getValues())
                .as("Pure should create a singleton list containing the input value")
                .containsExactly(42);
        }

        @Test
        @DisplayName("Should transform all elements when mapping over list values")
        void shouldTransformAllElementsWhenMappingOverListValues() {
            // Given
            ListMonad<Integer> inputListMonad = ListMonad.of(List.of(1, 2, 3));
            Function<Integer, String> transformationFunction = x -> "Item: " + x;

            // When
            ListMonad<String> actualResult = inputListMonad.fmap(transformationFunction);

            // Then
            assertThat(actualResult.getValues())
                .as("Mapping should transform each element in order")
                .containsExactly("Item: 1", "Item: 2", "Item: 3");
        }

        @Test
        @DisplayName("Should flatten nested lists when binding list with function returning lists")
        void shouldFlattenNestedListsWhenBindingListWithFunctionReturningLists() {
            // Given
            ListMonad<Integer> inputListMonad = ListMonad.of(List.of(1, 2, 3));
            Function<Integer, ListMonad<String>> monadicFunction = x ->
                ListMonad.of(List.of("a" + x, "b" + x));

            // When
            ListMonad<String> actualResult = inputListMonad.bind(monadicFunction);

            // Then
            assertThat(actualResult.getValues())
                .as("Binding should apply function to each element and flatten the results")
                .containsExactly("a1", "b1", "a2", "b2", "a3", "b3");
        }

        @Test
        @DisplayName("Should return empty list when binding empty list with any function")
        void shouldReturnEmptyListWhenBindingEmptyListWithAnyFunction() {
            // Given
            ListMonad<Integer> inputListMonad = ListMonad.of(List.<Integer>of());
            Function<Integer, ListMonad<String>> monadicFunction = x ->
                ListMonad.of(List.of("a" + x, "b" + x));

            // When
            ListMonad<String> actualResult = inputListMonad.bind(monadicFunction);

            // Then
            assertThat(actualResult.getValues())
                .as("Binding empty list should result in empty list")
                .isEmpty();
        }
    }

    @Nested
    @DisplayName("Function Composition")
    class FunctionComposition {

        @Test
        @DisplayName("Should compose two functions in mathematical order (g ∘ f)")
        void shouldComposeTwoFunctionsInMathematicalOrder() {
            // Given
            Function<Integer, Integer> firstFunction = x -> x * 2;
            Function<Integer, String> secondFunction = x -> "Result: " + x;

            // When
            Function<Integer, String> actualComposedFunction = Exercise1.compose(secondFunction, firstFunction);
            String actualResult = actualComposedFunction.apply(5);

            // Then
            assertThat(actualResult)
                .as("Compose should apply first function then second function")
                .isEqualTo("Result: 10");
        }

        @Test
        @DisplayName("Should create pipeline when composing multiple functions with pipe")
        void shouldCreatePipelineWhenComposingMultipleFunctionsWithPipe() {
            // Given
            Function<Integer, Integer> addTenFunction = x -> x + 10;
            Function<Integer, Integer> multiplyByTwoFunction = x -> x * 2;
            Function<Integer, String> toStringFunction = x -> "Value: " + x;

            // When
            Function<Integer, String> actualPipeline = Exercise1.pipe(addTenFunction, multiplyByTwoFunction, toStringFunction);
            String actualResult = actualPipeline.apply(5);

            // Then
            assertThat(actualResult)
                .as("Pipeline should apply functions in sequence: (5 + 10) * 2 = 30, then 'Value: 30'")
                .isEqualTo("Value: 30");
        }

        @Test
        @DisplayName("Should apply function specified number of times when count is positive")
        void shouldApplyFunctionSpecifiedNumberOfTimesWhenCountIsPositive() {
            // Given
            Function<Integer, Integer> incrementFunction = x -> x + 1;
            int applicationCount = 3;
            Integer initialValue = 10;

            // When
            Integer actualResult = Exercise1.applyN(incrementFunction, applicationCount, initialValue);

            // Then
            assertThat(actualResult)
                .as("Function applied 3 times should increment by 3: 10 + 1 + 1 + 1 = 13")
                .isEqualTo(13);
        }

        @Test
        @DisplayName("Should return original value when applying function zero times")
        void shouldReturnOriginalValueWhenApplyingFunctionZeroTimes() {
            // Given
            Function<Integer, Integer> incrementFunction = x -> x + 1;
            int applicationCount = 0;
            Integer initialValue = 10;

            // When
            Integer actualResult = Exercise1.applyN(incrementFunction, applicationCount, initialValue);

            // Then
            assertThat(actualResult)
                .as("Function applied 0 times should return original value")
                .isEqualTo(10);
        }
    }

    @Nested
    @DisplayName("Kleisli Composition")
    class KleisliComposition {

        @Test
        @DisplayName("Should compose monadic functions successfully when both succeed")
        void shouldComposeMonadicFunctionsSuccessfullyWhenBothSucceed() {
            // Given
            Function<Integer, Maybe<Integer>> firstMonadicFunction = x ->
                x > 0 ? Maybe.pure(x * 2) : Maybe.nothing();
            Function<Integer, Maybe<String>> secondMonadicFunction = x ->
                x < 100 ? Maybe.pure("Result: " + x) : Maybe.nothing();
            Integer inputValue = 10;

            // When
            Function<Integer, Maybe<String>> actualComposedFunction =
                Exercise1.kleisliCompose(secondMonadicFunction, firstMonadicFunction);
            Maybe<String> actualResult = actualComposedFunction.apply(inputValue);

            // Then
            assertThat(actualResult.isJust())
                .as("Kleisli composition should succeed when both functions succeed")
                .isTrue();
            assertThat(actualResult.getValue())
                .as("Result should be from applying both functions: 10 * 2 = 20, then 'Result: 20'")
                .isEqualTo("Result: 20");
        }

        @Test
        @DisplayName("Should short-circuit to Nothing when first monadic function fails")
        void shouldShortCircuitToNothingWhenFirstMonadicFunctionFails() {
            // Given
            Function<Integer, Maybe<Integer>> firstMonadicFunction = x ->
                x > 0 ? Maybe.pure(x * 2) : Maybe.nothing();
            Function<Integer, Maybe<String>> secondMonadicFunction = x ->
                x < 100 ? Maybe.pure("Result: " + x) : Maybe.nothing();
            Integer inputValue = -5;

            // When
            Function<Integer, Maybe<String>> actualComposedFunction =
                Exercise1.kleisliCompose(secondMonadicFunction, firstMonadicFunction);
            Maybe<String> actualResult = actualComposedFunction.apply(inputValue);

            // Then
            assertThat(actualResult.isNothing())
                .as("Kleisli composition should fail when first function returns Nothing")
                .isTrue();
        }

        @Test
        @DisplayName("Should short-circuit to Nothing when second monadic function fails")
        void shouldShortCircuitToNothingWhenSecondMonadicFunctionFails() {
            // Given
            Function<Integer, Maybe<Integer>> firstMonadicFunction = x ->
                x > 0 ? Maybe.pure(x * 200) : Maybe.nothing();
            Function<Integer, Maybe<String>> secondMonadicFunction = x ->
                x < 100 ? Maybe.pure("Result: " + x) : Maybe.nothing();
            Integer inputValue = 10;

            // When
            Function<Integer, Maybe<String>> actualComposedFunction =
                Exercise1.kleisliCompose(secondMonadicFunction, firstMonadicFunction);
            Maybe<String> actualResult = actualComposedFunction.apply(inputValue);

            // Then
            assertThat(actualResult.isNothing())
                .as("Kleisli composition should fail when second function returns Nothing (10 * 200 = 2000 > 100)")
                .isTrue();
        }
    }

    @Nested
    @DisplayName("Monadic Utilities")
    class MonadicUtilities {

        @Test
        @DisplayName("Should collect all values when sequencing list of all Just values")
        void shouldCollectAllValuesWhenSequencingListOfAllJustValues() {
            // Given
            List<Maybe<Integer>> inputMaybes = List.of(
                Maybe.pure(1),
                Maybe.pure(2),
                Maybe.pure(3)
            );

            // When
            Maybe<List<Integer>> actualResult = Exercise1.sequenceMaybe(inputMaybes);

            // Then
            assertThat(actualResult.isJust())
                .as("Sequencing all Just values should result in Just")
                .isTrue();
            assertThat(actualResult.getValue())
                .as("Sequenced result should contain all values in order")
                .containsExactly(1, 2, 3);
        }

        @Test
        @DisplayName("Should return Nothing when sequencing list containing any Nothing value")
        void shouldReturnNothingWhenSequencingListContainingAnyNothingValue() {
            // Given
            List<Maybe<Integer>> inputMaybes = List.of(
                Maybe.pure(1),
                Maybe.<Integer>nothing(),
                Maybe.pure(3)
            );

            // When
            Maybe<List<Integer>> actualResult = Exercise1.sequenceMaybe(inputMaybes);

            // Then
            assertThat(actualResult.isNothing())
                .as("Sequencing list with any Nothing should result in Nothing")
                .isTrue();
        }

        @Test
        @DisplayName("Should return Nothing when traversing list with function that fails for any element")
        void shouldReturnNothingWhenTraversingListWithFunctionThatFailsForAnyElement() {
            // Given
            List<Integer> inputNumbers = List.of(1, 2, 3, 4);
            Function<Integer, Maybe<String>> monadicFunction = x ->
                x <= 3 ? Maybe.pure("Item: " + x) : Maybe.nothing();

            // When
            Maybe<List<String>> actualResult = Exercise1.traverseMaybe(inputNumbers, monadicFunction);

            // Then
            assertThat(actualResult.isNothing())
                .as("Traversing should fail when function returns Nothing for any element (4 > 3)")
                .isTrue();
        }

        @Test
        @DisplayName("Should collect transformed values when traversing list with function that succeeds for all elements")
        void shouldCollectTransformedValuesWhenTraversingListWithFunctionThatSucceedsForAllElements() {
            // Given
            List<Integer> inputNumbers = List.of(1, 2, 3);
            Function<Integer, Maybe<String>> monadicFunction = x ->
                x <= 3 ? Maybe.pure("Item: " + x) : Maybe.nothing();

            // When
            Maybe<List<String>> actualResult = Exercise1.traverseMaybe(inputNumbers, monadicFunction);

            // Then
            assertThat(actualResult.isJust())
                .as("Traversing should succeed when function succeeds for all elements")
                .isTrue();
            assertThat(actualResult.getValue())
                .as("Traversed result should contain all transformed values in order")
                .containsExactly("Item: 1", "Item: 2", "Item: 3");
        }
    }
}
