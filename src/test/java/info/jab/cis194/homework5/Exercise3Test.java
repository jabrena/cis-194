package info.jab.cis194.homework5;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Exercise 3: Type Class Constraints and Generic Programming")
class Exercise3Test {

    @Nested
    @DisplayName("Functor Type Class Tests")
    class FunctorTypeClassTests {

        @Test
        @DisplayName("Should map over Maybe correctly")
        void shouldMapOverMaybeCorrectly() {
            // Given
            Exercise3.Maybe<Integer> just = Exercise3.just(42);
            Exercise3.Maybe<Integer> nothing = Exercise3.nothing();
            Function<Integer, String> toString = Object::toString;

            // When
            Exercise3.Maybe<String> result1 = Exercise3.fmap(toString, just);
            Exercise3.Maybe<String> result2 = Exercise3.fmap(toString, nothing);

            // Then
            assertThat(Exercise3.fromMaybe("default", result1)).isEqualTo("42");
            assertThat(Exercise3.fromMaybe("default", result2)).isEqualTo("default");
        }

        @Test
        @DisplayName("Should map over List correctly")
        void shouldMapOverListCorrectly() {
            // Given
            List<Integer> numbers = List.of(1, 2, 3, 4, 5);
            Function<Integer, Integer> square = x -> x * x;

            // When
            List<Integer> result = Exercise3.fmap(square, numbers);

            // Then
            assertThat(result).containsExactly(1, 4, 9, 16, 25);
        }

        @Test
        @DisplayName("Should map over empty List correctly")
        void shouldMapOverEmptyListCorrectly() {
            // Given
            List<Integer> emptyList = List.of();
            Function<Integer, String> toString = Object::toString;

            // When
            List<String> result = Exercise3.fmap(toString, emptyList);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should satisfy functor laws - identity")
        void shouldSatisfyFunctorLawsIdentity() {
            // Given
            Exercise3.Maybe<Integer> maybe = Exercise3.just(42);
            List<Integer> list = List.of(1, 2, 3);

            // When
            Exercise3.Maybe<Integer> maybeResult = Exercise3.fmap(Function.identity(), maybe);
            List<Integer> listResult = Exercise3.fmap(Function.identity(), list);

            // Then
            assertThat(Exercise3.fromMaybe(0, maybeResult)).isEqualTo(Exercise3.fromMaybe(0, maybe));
            assertThat(listResult).isEqualTo(list);
        }

        @Test
        @DisplayName("Should satisfy functor laws - composition")
        void shouldSatisfyFunctorLawsComposition() {
            // Given
            Exercise3.Maybe<Integer> maybe = Exercise3.just(5);
            Function<Integer, Integer> f = x -> x * 2;
            Function<Integer, String> g = Object::toString;

            // When
            Exercise3.Maybe<String> result1 = Exercise3.fmap(g, Exercise3.fmap(f, maybe));
            Exercise3.Maybe<String> result2 = Exercise3.fmap(g.compose(f), maybe);

            // Then
            assertThat(Exercise3.fromMaybe("", result1)).isEqualTo(Exercise3.fromMaybe("", result2));
        }
    }

    @Nested
    @DisplayName("Applicative Type Class Tests")
    class ApplicativeTypeClassTests {

        @Test
        @DisplayName("Should apply pure function correctly")
        void shouldApplyPureFunctionCorrectly() {
            // Given
            Integer value = 42;

            // When
            Exercise3.Maybe<Integer> result = Exercise3.pure(value);

            // Then
            assertThat(Exercise3.fromMaybe(0, result)).isEqualTo(42);
        }

        @Test
        @DisplayName("Should apply function in context correctly")
        void shouldApplyFunctionInContextCorrectly() {
            // Given
            Exercise3.Maybe<Function<Integer, Integer>> maybeFunc = Exercise3.pure(x -> x * 2);
            Exercise3.Maybe<Integer> maybeValue = Exercise3.just(21);

            // When
            Exercise3.Maybe<Integer> result = Exercise3.apply(maybeFunc, maybeValue);

            // Then
            assertThat(Exercise3.fromMaybe(0, result)).isEqualTo(42);
        }

        @Test
        @DisplayName("Should handle Nothing in apply correctly")
        void shouldHandleNothingInApplyCorrectly() {
            // Given
            Exercise3.Maybe<Function<Integer, Integer>> maybeFunc = Exercise3.nothing();
            Exercise3.Maybe<Integer> maybeValue = Exercise3.just(21);

            // When
            Exercise3.Maybe<Integer> result = Exercise3.apply(maybeFunc, maybeValue);

            // Then
            assertThat(Exercise3.fromMaybe(0, result)).isEqualTo(0);
        }

        @Test
        @DisplayName("Should lift binary function correctly")
        void shouldLiftBinaryFunctionCorrectly() {
            // Given
            Exercise3.Maybe<Integer> maybe1 = Exercise3.just(10);
            Exercise3.Maybe<Integer> maybe2 = Exercise3.just(20);

            // When
            Exercise3.Maybe<Integer> result = Exercise3.liftA2(a -> b -> a + b, maybe1, maybe2);

            // Then
            assertThat(Exercise3.fromMaybe(0, result)).isEqualTo(30);
        }

        @Test
        @DisplayName("Should handle Nothing in liftA2 correctly")
        void shouldHandleNothingInLiftA2Correctly() {
            // Given
            Exercise3.Maybe<Integer> maybe1 = Exercise3.just(10);
            Exercise3.Maybe<Integer> maybe2 = Exercise3.nothing();

            // When
            Exercise3.Maybe<Integer> result = Exercise3.liftA2(a -> b -> a + b, maybe1, maybe2);

            // Then
            assertThat(Exercise3.fromMaybe(-1, result)).isEqualTo(-1);
        }
    }

    @Nested
    @DisplayName("Monad Type Class Tests")
    class MonadTypeClassTests {

        @Test
        @DisplayName("Should bind Maybe correctly")
        void shouldBindMaybeCorrectly() {
            // Given
            Exercise3.Maybe<Integer> maybe = Exercise3.just(42);
            Function<Integer, Exercise3.Maybe<String>> f = x -> Exercise3.just(x.toString());

            // When
            Exercise3.Maybe<String> result = Exercise3.bind(maybe, f);

            // Then
            assertThat(Exercise3.fromMaybe("", result)).isEqualTo("42");
        }

        @Test
        @DisplayName("Should bind Nothing correctly")
        void shouldBindNothingCorrectly() {
            // Given
            Exercise3.Maybe<Integer> maybe = Exercise3.nothing();
            Function<Integer, Exercise3.Maybe<String>> f = x -> Exercise3.just(x.toString());

            // When
            Exercise3.Maybe<String> result = Exercise3.bind(maybe, f);

            // Then
            assertThat(Exercise3.fromMaybe("default", result)).isEqualTo("default");
        }

        @Test
        @DisplayName("Should chain monadic operations correctly")
        void shouldChainMonadicOperationsCorrectly() {
            // Given
            Exercise3.Maybe<Integer> maybe = Exercise3.just(10);

            // When
            Exercise3.Maybe<String> result = Exercise3.bind(maybe, x ->
                Exercise3.bind(Exercise3.just(x * 2), y ->
                    Exercise3.just("Result: " + y)
                )
            );

            // Then
            assertThat(Exercise3.fromMaybe("", result)).isEqualTo("Result: 20");
        }

        @Test
        @DisplayName("Should satisfy monad laws - left identity")
        void shouldSatisfyMonadLawsLeftIdentity() {
            // Given
            Integer value = 42;
            Function<Integer, Exercise3.Maybe<String>> f = x -> Exercise3.just(x.toString());

            // When
            Exercise3.Maybe<String> result1 = Exercise3.bind(Exercise3.pure(value), f);
            Exercise3.Maybe<String> result2 = f.apply(value);

            // Then
            assertThat(Exercise3.fromMaybe("", result1)).isEqualTo(Exercise3.fromMaybe("", result2));
        }

        @Test
        @DisplayName("Should satisfy monad laws - right identity")
        void shouldSatisfyMonadLawsRightIdentity() {
            // Given
            Exercise3.Maybe<Integer> maybe = Exercise3.just(42);

            // When
            Exercise3.Maybe<Integer> result = Exercise3.bind(maybe, Exercise3::pure);

            // Then
            assertThat(Exercise3.fromMaybe(0, result)).isEqualTo(Exercise3.fromMaybe(0, maybe));
        }
    }

    @Nested
    @DisplayName("Foldable Type Class Tests")
    class FoldableTypeClassTests {

        @Test
        @DisplayName("Should fold List correctly")
        void shouldFoldListCorrectly() {
            // Given
            List<Integer> numbers = List.of(1, 2, 3, 4, 5);

            // When
            Integer sum = Exercise3.foldr(Integer::sum, 0, numbers);
            Integer product = Exercise3.foldr((x, y) -> x * y, 1, numbers);

            // Then
            assertThat(sum).isEqualTo(15);
            assertThat(product).isEqualTo(120);
        }

        @Test
        @DisplayName("Should fold Maybe correctly")
        void shouldFoldMaybeCorrectly() {
            // Given
            Exercise3.Maybe<Integer> just = Exercise3.just(42);
            Exercise3.Maybe<Integer> nothing = Exercise3.nothing();

            // When
            Integer result1 = Exercise3.foldr(Integer::sum, 0, just);
            Integer result2 = Exercise3.foldr(Integer::sum, 0, nothing);

            // Then
            assertThat(result1).isEqualTo(42);
            assertThat(result2).isEqualTo(0);
        }

        @Test
        @DisplayName("Should calculate length correctly")
        void shouldCalculateLengthCorrectly() {
            // Given
            List<String> words = List.of("hello", "world", "foo", "bar");
            Exercise3.Maybe<Integer> just = Exercise3.just(42);
            Exercise3.Maybe<Integer> nothing = Exercise3.nothing();

            // When
            Integer listLength = Exercise3.length(words);
            Integer justLength = Exercise3.length(just);
            Integer nothingLength = Exercise3.length(nothing);

            // Then
            assertThat(listLength).isEqualTo(4);
            assertThat(justLength).isEqualTo(1);
            assertThat(nothingLength).isEqualTo(0);
        }

        @Test
        @DisplayName("Should convert to list correctly")
        void shouldConvertToListCorrectly() {
            // Given
            Exercise3.Maybe<String> just = Exercise3.just("hello");
            Exercise3.Maybe<String> nothing = Exercise3.nothing();

            // When
            List<String> justList = Exercise3.toList(just);
            List<String> nothingList = Exercise3.toList(nothing);

            // Then
            assertThat(justList).containsExactly("hello");
            assertThat(nothingList).isEmpty();
        }
    }

    @Nested
    @DisplayName("Traversable Type Class Tests")
    class TraversableTypeClassTests {

        @Test
        @DisplayName("Should traverse List with Maybe correctly")
        void shouldTraverseListWithMaybeCorrectly() {
            // Given
            List<Integer> numbers = List.of(1, 2, 3);
            Function<Integer, Exercise3.Maybe<String>> f = x -> Exercise3.just(x.toString());

            // When
            Exercise3.Maybe<List<String>> result = Exercise3.traverse(f, numbers);

            // Then
            List<String> resultList = Exercise3.fromMaybe(List.of(), result);
            assertThat(resultList).containsExactly("1", "2", "3");
        }

        @Test
        @DisplayName("Should handle failure in traverse correctly")
        void shouldHandleFailureInTraverseCorrectly() {
            // Given
            List<Integer> numbers = List.of(1, 2, 3);
            Function<Integer, Exercise3.Maybe<String>> f = x ->
                x == 2 ? Exercise3.nothing() : Exercise3.just(x.toString());

            // When
            Exercise3.Maybe<List<String>> result = Exercise3.traverse(f, numbers);

            // Then
            assertThat(Exercise3.fromMaybe(List.of("default"), result))
                .containsExactly("default");
        }

        @Test
        @DisplayName("Should sequence Maybe list correctly")
        void shouldSequenceMaybeListCorrectly() {
            // Given
            List<Exercise3.Maybe<Integer>> maybes = List.of(
                Exercise3.just(1),
                Exercise3.just(2),
                Exercise3.just(3)
            );

            // When
            Exercise3.Maybe<List<Integer>> result = Exercise3.sequence(maybes);

            // Then
            List<Integer> resultList = Exercise3.fromMaybe(List.of(), result);
            assertThat(resultList).containsExactly(1, 2, 3);
        }

        @Test
        @DisplayName("Should handle Nothing in sequence correctly")
        void shouldHandleNothingInSequenceCorrectly() {
            // Given
            List<Exercise3.Maybe<Integer>> maybes = List.of(
                Exercise3.just(1),
                Exercise3.nothing(),
                Exercise3.just(3)
            );

            // When
            Exercise3.Maybe<List<Integer>> result = Exercise3.sequence(maybes);

            // Then
            assertThat(Exercise3.fromMaybe(List.of(-1), result))
                .containsExactly(-1);
        }
    }

    @Nested
    @DisplayName("Advanced Generic Programming Tests")
    class AdvancedGenericProgrammingTests {

        @Test
        @DisplayName("Should implement generic filter correctly")
        void shouldImplementGenericFilterCorrectly() {
            // Given
            List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
            Predicate<Integer> isEven = x -> x % 2 == 0;

            // When
            List<Integer> result = Exercise3.filter(isEven, numbers);

            // Then
            assertThat(result).containsExactly(2, 4, 6, 8, 10);
        }

        @Test
        @DisplayName("Should implement generic partition correctly")
        void shouldImplementGenericPartitionCorrectly() {
            // Given
            List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
            Predicate<Integer> isEven = x -> x % 2 == 0;

            // When
            Exercise3.Pair<List<Integer>, List<Integer>> result = Exercise3.partition(isEven, numbers);

            // Then
            assertThat(result.first()).containsExactly(2, 4, 6, 8, 10);
            assertThat(result.second()).containsExactly(1, 3, 5, 7, 9);
        }

        @Test
        @DisplayName("Should implement mapMaybe correctly")
        void shouldImplementMapMaybeCorrectly() {
            // Given
            List<String> strings = List.of("1", "not a number", "3", "4", "invalid");
            Function<String, Exercise3.Maybe<Integer>> parseInteger = s -> {
                try {
                    return Exercise3.just(Integer.parseInt(s));
                } catch (NumberFormatException e) {
                    return Exercise3.nothing();
                }
            };

            // When
            List<Integer> result = Exercise3.mapMaybe(parseInteger, strings);

            // Then
            assertThat(result).containsExactly(1, 3, 4);
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should handle null in fmap correctly")
        void shouldHandleNullInFmapCorrectly() {
            // Given
            Function<Integer, String> nullFunction = null;
            List<Integer> list = List.of(1, 2, 3);

            // When & Then
            assertThatThrownBy(() -> Exercise3.fmap(nullFunction, list))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Function cannot be null");
        }

        @Test
        @DisplayName("Should handle null list in foldr correctly")
        void shouldHandleNullListInFoldrCorrectly() {
            // Given
            List<Integer> nullList = null;

            // When & Then
            assertThatThrownBy(() -> Exercise3.foldr(Integer::sum, 0, nullList))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Foldable cannot be null");
        }
    }
}
