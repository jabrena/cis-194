package info.jab.cis194.homework10;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for Exercise 1: Applicative Functors - Parser Combinators
 *
 * This exercise focuses on implementing basic parser combinators using
 * Applicative Functors concepts. We'll build a simple parsing library
 * that can parse structured data using functional composition.
 *
 * Based on CIS-194 Week 10: Applicative Functors
 */
@DisplayName("Exercise 1: Applicative Parser Combinators")
class Exercise1Test {

    @Nested
    @DisplayName("Basic Parser Operations")
    class BasicParserOperations {

        @Test
        @DisplayName("Should parse single character successfully")
        void shouldParseSingleCharacter() {
            // Given
            Exercise1.Parser<Character> charParser = Exercise1.char_('a');
            String input = "abc";

            // When
            Optional<Exercise1.ParseResult<Character>> result = charParser.parse(input);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().value()).isEqualTo('a');
            assertThat(result.get().remaining()).isEqualTo("bc");
        }

        @Test
        @DisplayName("Should fail to parse wrong character")
        void shouldFailToParseWrongCharacter() {
            // Given
            Exercise1.Parser<Character> charParser = Exercise1.char_('a');
            String input = "xyz";

            // When
            Optional<Exercise1.ParseResult<Character>> result = charParser.parse(input);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should fail to parse empty string")
        void shouldFailToParseEmptyString() {
            // Given
            Exercise1.Parser<Character> charParser = Exercise1.char_('a');
            String input = "";

            // When
            Optional<Exercise1.ParseResult<Character>> result = charParser.parse(input);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should parse any digit successfully")
        void shouldParseAnyDigit() {
            // Given
            Exercise1.Parser<Character> digitParser = Exercise1.digit();
            String input = "5abc";

            // When
            Optional<Exercise1.ParseResult<Character>> result = digitParser.parse(input);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().value()).isEqualTo('5');
            assertThat(result.get().remaining()).isEqualTo("abc");
        }

        @Test
        @DisplayName("Should fail to parse non-digit")
        void shouldFailToParseNonDigit() {
            // Given
            Exercise1.Parser<Character> digitParser = Exercise1.digit();
            String input = "abc";

            // When
            Optional<Exercise1.ParseResult<Character>> result = digitParser.parse(input);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should parse any letter successfully")
        void shouldParseAnyLetter() {
            // Given
            Exercise1.Parser<Character> letterParser = Exercise1.letter();
            String input = "x123";

            // When
            Optional<Exercise1.ParseResult<Character>> result = letterParser.parse(input);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().value()).isEqualTo('x');
            assertThat(result.get().remaining()).isEqualTo("123");
        }
    }

    @Nested
    @DisplayName("Applicative Parser Combinators")
    class ApplicativeParserCombinators {

        @Test
        @DisplayName("Should map parser result using fmap")
        void shouldMapParserResult() {
            // Given
            Exercise1.Parser<Character> digitParser = Exercise1.digit();
            Exercise1.Parser<Integer> intParser = digitParser.fmap(c -> Character.getNumericValue(c));
            String input = "7abc";

            // When
            Optional<Exercise1.ParseResult<Integer>> result = intParser.parse(input);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().value()).isEqualTo(7);
            assertThat(result.get().remaining()).isEqualTo("abc");
        }

        @Test
        @DisplayName("Should apply parser function to parser value")
        void shouldApplyParserFunction() {
            // Given
            Exercise1.Parser<Character> charParser = Exercise1.char_('a');
            Exercise1.Parser<java.util.function.Function<Character, String>> funcParser =
                Exercise1.pure(c -> "Parsed: " + c);
            Exercise1.Parser<String> resultParser = charParser.apply(funcParser);
            String input = "abc";

            // When
            Optional<Exercise1.ParseResult<String>> result = resultParser.parse(input);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().value()).isEqualTo("Parsed: a");
            assertThat(result.get().remaining()).isEqualTo("bc");
        }

        @Test
        @DisplayName("Should sequence two parsers")
        void shouldSequenceTwoParsers() {
            // Given
            Exercise1.Parser<Character> firstParser = Exercise1.char_('a');
            Exercise1.Parser<Character> secondParser = Exercise1.char_('b');
            Exercise1.Parser<String> sequenceParser = Exercise1.sequence2(firstParser, secondParser);
            String input = "abcd";

            // When
            Optional<Exercise1.ParseResult<String>> result = sequenceParser.parse(input);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().value()).isEqualTo("ab");
            assertThat(result.get().remaining()).isEqualTo("cd");
        }

        @Test
        @DisplayName("Should fail sequence if first parser fails")
        void shouldFailSequenceIfFirstParserFails() {
            // Given
            Exercise1.Parser<Character> firstParser = Exercise1.char_('x');
            Exercise1.Parser<Character> secondParser = Exercise1.char_('b');
            Exercise1.Parser<String> sequenceParser = Exercise1.sequence2(firstParser, secondParser);
            String input = "abcd";

            // When
            Optional<Exercise1.ParseResult<String>> result = sequenceParser.parse(input);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should fail sequence if second parser fails")
        void shouldFailSequenceIfSecondParserFails() {
            // Given
            Exercise1.Parser<Character> firstParser = Exercise1.char_('a');
            Exercise1.Parser<Character> secondParser = Exercise1.char_('x');
            Exercise1.Parser<String> sequenceParser = Exercise1.sequence2(firstParser, secondParser);
            String input = "abcd";

            // When
            Optional<Exercise1.ParseResult<String>> result = sequenceParser.parse(input);

            // Then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Complex Parser Compositions")
    class ComplexParserCompositions {

        @Test
        @DisplayName("Should parse a two-digit number")
        void shouldParseTwoDigitNumber() {
            // Given
            Exercise1.Parser<Integer> twoDigitParser = Exercise1.twoDigitNumber();
            String input = "42abc";

            // When
            Optional<Exercise1.ParseResult<Integer>> result = twoDigitParser.parse(input);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().value()).isEqualTo(42);
            assertThat(result.get().remaining()).isEqualTo("abc");
        }

        @Test
        @DisplayName("Should fail to parse single digit as two-digit number")
        void shouldFailToParseSingleDigit() {
            // Given
            Exercise1.Parser<Integer> twoDigitParser = Exercise1.twoDigitNumber();
            String input = "5abc";

            // When
            Optional<Exercise1.ParseResult<Integer>> result = twoDigitParser.parse(input);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should parse a simple identifier")
        void shouldParseSimpleIdentifier() {
            // Given
            Exercise1.Parser<String> identifierParser = Exercise1.identifier();
            String input = "hello123 world";

            // When
            Optional<Exercise1.ParseResult<String>> result = identifierParser.parse(input);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().value()).isEqualTo("hello123");
            assertThat(result.get().remaining()).isEqualTo(" world");
        }

        @Test
        @DisplayName("Should parse identifier starting with letter only")
        void shouldParseIdentifierStartingWithLetter() {
            // Given
            Exercise1.Parser<String> identifierParser = Exercise1.identifier();
            String input = "123hello";

            // When
            Optional<Exercise1.ParseResult<String>> result = identifierParser.parse(input);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should parse parenthesized expression")
        void shouldParseParenthesizedExpression() {
            // Given
            Exercise1.Parser<Integer> parenParser = Exercise1.parenthesized(Exercise1.twoDigitNumber());
            String input = "(42)abc";

            // When
            Optional<Exercise1.ParseResult<Integer>> result = parenParser.parse(input);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().value()).isEqualTo(42);
            assertThat(result.get().remaining()).isEqualTo("abc");
        }

        @Test
        @DisplayName("Should fail parenthesized expression with missing closing paren")
        void shouldFailParenthesizedWithMissingClosingParen() {
            // Given
            Exercise1.Parser<Integer> parenParser = Exercise1.parenthesized(Exercise1.twoDigitNumber());
            String input = "(42abc";

            // When
            Optional<Exercise1.ParseResult<Integer>> result = parenParser.parse(input);

            // Then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Alternative Parser Operations")
    class AlternativeParserOperations {

        @Test
        @DisplayName("Should try first alternative successfully")
        void shouldTryFirstAlternative() {
            // Given
            Exercise1.Parser<Character> letterOrDigit = Exercise1.letter().or(Exercise1.digit());
            String input = "a123";

            // When
            Optional<Exercise1.ParseResult<Character>> result = letterOrDigit.parse(input);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().value()).isEqualTo('a');
            assertThat(result.get().remaining()).isEqualTo("123");
        }

        @Test
        @DisplayName("Should try second alternative when first fails")
        void shouldTrySecondAlternative() {
            // Given
            Exercise1.Parser<Character> letterOrDigit = Exercise1.letter().or(Exercise1.digit());
            String input = "5abc";

            // When
            Optional<Exercise1.ParseResult<Character>> result = letterOrDigit.parse(input);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().value()).isEqualTo('5');
            assertThat(result.get().remaining()).isEqualTo("abc");
        }

        @Test
        @DisplayName("Should fail when both alternatives fail")
        void shouldFailWhenBothAlternativesFail() {
            // Given
            Exercise1.Parser<Character> letterOrDigit = Exercise1.letter().or(Exercise1.digit());
            String input = "!@#";

            // When
            Optional<Exercise1.ParseResult<Character>> result = letterOrDigit.parse(input);

            // Then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Many Parser Operations")
    class ManyParserOperations {

        @Test
        @DisplayName("Should parse many digits")
        void shouldParseManyDigits() {
            // Given
            Exercise1.Parser<List<Character>> manyDigits = Exercise1.many(Exercise1.digit());
            String input = "12345abc";

            // When
            Optional<Exercise1.ParseResult<List<Character>>> result = manyDigits.parse(input);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().value()).containsExactly('1', '2', '3', '4', '5');
            assertThat(result.get().remaining()).isEqualTo("abc");
        }

        @Test
        @DisplayName("Should parse empty list when no matches")
        void shouldParseEmptyListWhenNoMatches() {
            // Given
            Exercise1.Parser<List<Character>> manyDigits = Exercise1.many(Exercise1.digit());
            String input = "abc123";

            // When
            Optional<Exercise1.ParseResult<List<Character>>> result = manyDigits.parse(input);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().value()).isEmpty();
            assertThat(result.get().remaining()).isEqualTo("abc123");
        }

        @Test
        @DisplayName("Should parse at least one digit with some1")
        void shouldParseAtLeastOneDigit() {
            // Given
            Exercise1.Parser<List<Character>> some1Digits = Exercise1.some1(Exercise1.digit());
            String input = "123abc";

            // When
            Optional<Exercise1.ParseResult<List<Character>>> result = some1Digits.parse(input);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().value()).containsExactly('1', '2', '3');
            assertThat(result.get().remaining()).isEqualTo("abc");
        }

        @Test
        @DisplayName("Should fail some1 when no matches")
        void shouldFailSome1WhenNoMatches() {
            // Given
            Exercise1.Parser<List<Character>> some1Digits = Exercise1.some1(Exercise1.digit());
            String input = "abc123";

            // When
            Optional<Exercise1.ParseResult<List<Character>>> result = some1Digits.parse(input);

            // Then
            assertThat(result).isEmpty();
        }
    }
}
