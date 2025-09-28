package info.jab.cis194.homework10;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static info.jab.cis194.homework10.Exercise1.char_;
import static info.jab.cis194.homework10.Exercise1.digit;
import static info.jab.cis194.homework10.Exercise1.identifier;
import static info.jab.cis194.homework10.Exercise1.letter;
import static info.jab.cis194.homework10.Exercise1.letterOrDigit;
import static info.jab.cis194.homework10.Exercise1.many;
import static info.jab.cis194.homework10.Exercise1.parenthesized;
import static info.jab.cis194.homework10.Exercise1.pure;
import static info.jab.cis194.homework10.Exercise1.sequence2;
import static info.jab.cis194.homework10.Exercise1.some1;
import static info.jab.cis194.homework10.Exercise1.twoDigitNumber;
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
            var charParser = char_('a');
            var input = "abc";

            // When
            var result = charParser.parse(input);

            // Then
            assertThat(result)
                .isPresent()
                .hasValueSatisfying(parseResult -> {
                    assertThat(parseResult.value()).isEqualTo('a');
                    assertThat(parseResult.remaining()).isEqualTo("bc");
                });
        }

        @Test
        @DisplayName("Should fail to parse wrong character")
        void shouldFailToParseWrongCharacter() {
            // Given
            var charParser = char_('a');
            var input = "xyz";

            // When
            var result = charParser.parse(input);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should fail to parse empty string")
        void shouldFailToParseEmptyString() {
            // Given
            var charParser = char_('a');
            var input = "";

            // When
            var result = charParser.parse(input);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should parse any digit successfully")
        void shouldParseAnyDigit() {
            // Given
            var digitParser = digit();
            var input = "5abc";

            // When
            var result = digitParser.parse(input);

            // Then
            assertThat(result)
                .isPresent()
                .hasValueSatisfying(parseResult -> {
                    assertThat(parseResult.value()).isEqualTo('5');
                    assertThat(parseResult.remaining()).isEqualTo("abc");
                });
        }

        @Test
        @DisplayName("Should fail to parse non-digit")
        void shouldFailToParseNonDigit() {
            // Given
            var digitParser = digit();
            var input = "abc";

            // When
            var result = digitParser.parse(input);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should parse any letter successfully")
        void shouldParseAnyLetter() {
            // Given
            var letterParser = letter();
            var input = "x123";

            // When
            var result = letterParser.parse(input);

            // Then
            assertThat(result)
                .isPresent()
                .hasValueSatisfying(parseResult -> {
                    assertThat(parseResult.value()).isEqualTo('x');
                    assertThat(parseResult.remaining()).isEqualTo("123");
                });
        }
    }

    @Nested
    @DisplayName("Applicative Parser Combinators")
    class ApplicativeParserCombinators {

        @Test
        @DisplayName("Should map parser result using fmap")
        void shouldMapParserResult() {
            // Given
            var digitParser = digit();
            var intParser = digitParser.fmap(Character::getNumericValue);
            var input = "7abc";

            // When
            var result = intParser.parse(input);

            // Then
            assertThat(result)
                .isPresent()
                .hasValueSatisfying(parseResult -> {
                    assertThat(parseResult.value()).isEqualTo(7);
                    assertThat(parseResult.remaining()).isEqualTo("abc");
                });
        }

        @Test
        @DisplayName("Should apply parser function to parser value")
        void shouldApplyParserFunction() {
            // Given
            var charParser = char_('a');
            var funcParser = pure((java.util.function.Function<Character, String>) c -> "Parsed: " + c);
            var resultParser = charParser.apply(funcParser);
            var input = "abc";

            // When
            var result = resultParser.parse(input);

            // Then
            assertThat(result)
                .isPresent()
                .hasValueSatisfying(parseResult -> {
                    assertThat(parseResult.value()).isEqualTo("Parsed: a");
                    assertThat(parseResult.remaining()).isEqualTo("bc");
                });
        }

        @Test
        @DisplayName("Should sequence two parsers")
        void shouldSequenceTwoParsers() {
            // Given
            var firstParser = char_('a');
            var secondParser = char_('b');
            var sequenceParser = sequence2(firstParser, secondParser);
            var input = "abcd";

            // When
            var result = sequenceParser.parse(input);

            // Then
            assertThat(result)
                .isPresent()
                .hasValueSatisfying(parseResult -> {
                    assertThat(parseResult.value()).isEqualTo("ab");
                    assertThat(parseResult.remaining()).isEqualTo("cd");
                });
        }

        @Test
        @DisplayName("Should fail sequence if first parser fails")
        void shouldFailSequenceIfFirstParserFails() {
            // Given
            var firstParser = char_('x');
            var secondParser = char_('b');
            var sequenceParser = sequence2(firstParser, secondParser);
            var input = "abcd";

            // When
            var result = sequenceParser.parse(input);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should fail sequence if second parser fails")
        void shouldFailSequenceIfSecondParserFails() {
            // Given
            var firstParser = char_('a');
            var secondParser = char_('x');
            var sequenceParser = sequence2(firstParser, secondParser);
            var input = "abcd";

            // When
            var result = sequenceParser.parse(input);

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
            var twoDigitParser = twoDigitNumber();
            var input = "42abc";

            // When
            var result = twoDigitParser.parse(input);

            // Then
            assertThat(result)
                .isPresent()
                .hasValueSatisfying(parseResult -> {
                    assertThat(parseResult.value()).isEqualTo(42);
                    assertThat(parseResult.remaining()).isEqualTo("abc");
                });
        }

        @Test
        @DisplayName("Should fail to parse single digit as two-digit number")
        void shouldFailToParseSingleDigit() {
            // Given
            var twoDigitParser = twoDigitNumber();
            var input = "5abc";

            // When
            var result = twoDigitParser.parse(input);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should parse a simple identifier")
        void shouldParseSimpleIdentifier() {
            // Given
            var identifierParser = identifier();
            var input = "hello123 world";

            // When
            var result = identifierParser.parse(input);

            // Then
            assertThat(result)
                .isPresent()
                .hasValueSatisfying(parseResult -> {
                    assertThat(parseResult.value()).isEqualTo("hello123");
                    assertThat(parseResult.remaining()).isEqualTo(" world");
                });
        }

        @Test
        @DisplayName("Should parse identifier starting with letter only")
        void shouldParseIdentifierStartingWithLetter() {
            // Given
            var identifierParser = identifier();
            var input = "123hello";

            // When
            var result = identifierParser.parse(input);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should parse parenthesized expression")
        void shouldParseParenthesizedExpression() {
            // Given
            var parenParser = parenthesized(twoDigitNumber());
            var input = "(42)abc";

            // When
            var result = parenParser.parse(input);

            // Then
            assertThat(result)
                .isPresent()
                .hasValueSatisfying(parseResult -> {
                    assertThat(parseResult.value()).isEqualTo(42);
                    assertThat(parseResult.remaining()).isEqualTo("abc");
                });
        }

        @Test
        @DisplayName("Should fail parenthesized expression with missing closing paren")
        void shouldFailParenthesizedWithMissingClosingParen() {
            // Given
            var parenParser = parenthesized(twoDigitNumber());
            var input = "(42abc";

            // When
            var result = parenParser.parse(input);

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
            var letterOrDigit = letter().or(digit());
            var input = "a123";

            // When
            var result = letterOrDigit.parse(input);

            // Then
            assertThat(result)
                .isPresent()
                .hasValueSatisfying(parseResult -> {
                    assertThat(parseResult.value()).isEqualTo('a');
                    assertThat(parseResult.remaining()).isEqualTo("123");
                });
        }

        @Test
        @DisplayName("Should try second alternative when first fails")
        void shouldTrySecondAlternative() {
            // Given
            var letterOrDigit = letter().or(digit());
            var input = "5abc";

            // When
            var result = letterOrDigit.parse(input);

            // Then
            assertThat(result)
                .isPresent()
                .hasValueSatisfying(parseResult -> {
                    assertThat(parseResult.value()).isEqualTo('5');
                    assertThat(parseResult.remaining()).isEqualTo("abc");
                });
        }

        @Test
        @DisplayName("Should fail when both alternatives fail")
        void shouldFailWhenBothAlternativesFail() {
            // Given
            var letterOrDigit = letter().or(digit());
            var input = "!@#";

            // When
            var result = letterOrDigit.parse(input);

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
            var manyDigits = many(digit());
            var input = "12345abc";

            // When
            var result = manyDigits.parse(input);

            // Then
            assertThat(result)
                .isPresent()
                .hasValueSatisfying(parseResult -> {
                    assertThat(parseResult.value()).containsExactly('1', '2', '3', '4', '5');
                    assertThat(parseResult.remaining()).isEqualTo("abc");
                });
        }

        @Test
        @DisplayName("Should parse empty list when no matches")
        void shouldParseEmptyListWhenNoMatches() {
            // Given
            var manyDigits = many(digit());
            var input = "abc123";

            // When
            var result = manyDigits.parse(input);

            // Then
            assertThat(result)
                .isPresent()
                .hasValueSatisfying(parseResult -> {
                    assertThat(parseResult.value()).isEmpty();
                    assertThat(parseResult.remaining()).isEqualTo("abc123");
                });
        }

        @Test
        @DisplayName("Should parse at least one digit with some1")
        void shouldParseAtLeastOneDigit() {
            // Given
            var some1Digits = some1(digit());
            var input = "123abc";

            // When
            var result = some1Digits.parse(input);

            // Then
            assertThat(result)
                .isPresent()
                .hasValueSatisfying(parseResult -> {
                    assertThat(parseResult.value()).containsExactly('1', '2', '3');
                    assertThat(parseResult.remaining()).isEqualTo("abc");
                });
        }

        @Test
        @DisplayName("Should fail some1 when no matches")
        void shouldFailSome1WhenNoMatches() {
            // Given
            var some1Digits = some1(digit());
            var input = "abc123";

            // When
            var result = some1Digits.parse(input);

            // Then
            assertThat(result).isEmpty();
        }
    }
}
