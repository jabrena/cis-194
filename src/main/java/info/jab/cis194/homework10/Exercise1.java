package info.jab.cis194.homework10;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Exercise 1: Applicative Functors - Parser Combinators
 *
 * <p>This exercise implements basic parser combinators using Applicative Functors concepts.
 * We build a simple parsing library that can parse structured data using functional composition.
 *
 * <p>A Parser is a function that takes a String and returns either a successful parse result
 * (containing the parsed value and remaining input) or a failure (empty Optional).
 *
 * <p>Based on CIS-194 Week 10: Applicative Functors
 */
public class Exercise1 {

    /**
     * Parse result containing the parsed value and remaining input
     *
     * @param <T> the type of the parsed value
     */
    public record ParseResult<T>(T value, String remaining) {}

    /**
     * Parser interface representing a parser that can parse input of type T
     *
     * @param <T> the type of value this parser produces
     */
    @FunctionalInterface
    public interface Parser<T> {
        /**
         * Parse the input string, returning a result if successful
         *
         * @param input the input string to parse
         * @return Optional containing ParseResult if successful, empty otherwise
         */
        Optional<ParseResult<T>> parse(String input);

        /**
         * Functor map operation - transform the parsed value using the given function
         *
         * @param <U> the target type
         * @param f the transformation function
         * @return a new parser that applies the transformation
         */
        default <U> Parser<U> fmap(Function<T, U> f) {
            return input -> this.parse(input).map(result ->
                new ParseResult<>(f.apply(result.value()), result.remaining()));
        }

        /**
         * Applicative apply operation - apply a parser containing a function to this parser
         *
         * @param <U> the target type
         * @param funcParser parser containing a function
         * @return a new parser that applies the function from funcParser to the value from this parser
         */
        default <U> Parser<U> apply(Parser<Function<T, U>> funcParser) {
            return input -> {
                Optional<ParseResult<Function<T, U>>> funcResult = funcParser.parse(input);
                if (funcResult.isEmpty()) {
                    return Optional.empty();
                }

                Optional<ParseResult<T>> valueResult = this.parse(funcResult.get().remaining());
                if (valueResult.isEmpty()) {
                    return Optional.empty();
                }

                Function<T, U> func = funcResult.get().value();
                T value = valueResult.get().value();
                String remaining = valueResult.get().remaining();

                return Optional.of(new ParseResult<>(func.apply(value), remaining));
            };
        }

        /**
         * Alternative operation - try this parser, if it fails try the other
         *
         * @param other the alternative parser to try
         * @return a parser that tries this parser first, then other if this fails
         */
        default Parser<T> or(Parser<T> other) {
            return input -> {
                Optional<ParseResult<T>> result = this.parse(input);
                if (result.isPresent()) {
                    return result;
                }
                return other.parse(input);
            };
        }
    }

    /**
     * Pure operation - creates a parser that always succeeds with the given value
     *
     * @param <T> the type of the value
     * @param value the value to wrap in a parser
     * @return a parser that always succeeds with the given value
     */
    public static <T> Parser<T> pure(T value) {
        return input -> Optional.of(new ParseResult<>(value, input));
    }

    /**
     * Parser that matches a specific character
     *
     * @param c the character to match
     * @return a parser that succeeds if the first character matches c
     */
    public static Parser<Character> char_(char c) {
        return input -> {
            if (input.isEmpty() || input.charAt(0) != c) {
                return Optional.empty();
            }
            return Optional.of(new ParseResult<>(c, input.substring(1)));
        };
    }

    /**
     * Parser that matches any digit character (0-9)
     *
     * @return a parser that succeeds if the first character is a digit
     */
    public static Parser<Character> digit() {
        return input -> {
            if (input.isEmpty() || !Character.isDigit(input.charAt(0))) {
                return Optional.empty();
            }
            char digit = input.charAt(0);
            return Optional.of(new ParseResult<>(digit, input.substring(1)));
        };
    }

    /**
     * Parser that matches any letter character (a-z, A-Z)
     *
     * @return a parser that succeeds if the first character is a letter
     */
    public static Parser<Character> letter() {
        return input -> {
            if (input.isEmpty() || !Character.isLetter(input.charAt(0))) {
                return Optional.empty();
            }
            char letter = input.charAt(0);
            return Optional.of(new ParseResult<>(letter, input.substring(1)));
        };
    }

    /**
     * Sequence two parsers, combining their results into a string
     *
     * @param first the first parser
     * @param second the second parser
     * @return a parser that applies both parsers in sequence and combines results
     */
    public static Parser<String> sequence2(Parser<Character> first, Parser<Character> second) {
        return input -> {
            Optional<ParseResult<Character>> firstResult = first.parse(input);
            if (firstResult.isEmpty()) {
                return Optional.empty();
            }

            Optional<ParseResult<Character>> secondResult = second.parse(firstResult.get().remaining());
            if (secondResult.isEmpty()) {
                return Optional.empty();
            }

            String combined = "" + firstResult.get().value() + secondResult.get().value();
            return Optional.of(new ParseResult<>(combined, secondResult.get().remaining()));
        };
    }

    /**
     * Parser that matches exactly two digits and converts them to an integer
     *
     * @return a parser that succeeds if it can parse exactly two consecutive digits
     */
    public static Parser<Integer> twoDigitNumber() {
        return input -> {
            Optional<ParseResult<Character>> firstDigit = digit().parse(input);
            if (firstDigit.isEmpty()) {
                return Optional.empty();
            }

            Optional<ParseResult<Character>> secondDigit = digit().parse(firstDigit.get().remaining());
            if (secondDigit.isEmpty()) {
                return Optional.empty();
            }

            int tens = Character.getNumericValue(firstDigit.get().value());
            int ones = Character.getNumericValue(secondDigit.get().value());
            int number = tens * 10 + ones;

            return Optional.of(new ParseResult<>(number, secondDigit.get().remaining()));
        };
    }

    /**
     * Parser that matches an identifier (letter followed by zero or more letters or digits)
     *
     * @return a parser that succeeds if it can parse a valid identifier
     */
    public static Parser<String> identifier() {
        return input -> {
            // Must start with a letter
            Optional<ParseResult<Character>> firstResult = letter().parse(input);
            if (firstResult.isEmpty()) {
                return Optional.empty();
            }

            StringBuilder identifier = new StringBuilder();
            identifier.append(firstResult.get().value());
            String remaining = firstResult.get().remaining();

            // Continue parsing letters and digits
            while (!remaining.isEmpty()) {
                char c = remaining.charAt(0);
                if (Character.isLetterOrDigit(c)) {
                    identifier.append(c);
                    remaining = remaining.substring(1);
                } else {
                    break;
                }
            }

            return Optional.of(new ParseResult<>(identifier.toString(), remaining));
        };
    }

    /**
     * Parser that matches a value surrounded by parentheses
     *
     * @param <T> the type of the inner value
     * @param inner the parser for the inner value
     * @return a parser that matches (inner)
     */
    public static <T> Parser<T> parenthesized(Parser<T> inner) {
        return input -> {
            Optional<ParseResult<Character>> openParen = char_('(').parse(input);
            if (openParen.isEmpty()) {
                return Optional.empty();
            }

            Optional<ParseResult<T>> innerResult = inner.parse(openParen.get().remaining());
            if (innerResult.isEmpty()) {
                return Optional.empty();
            }

            Optional<ParseResult<Character>> closeParen = char_(')').parse(innerResult.get().remaining());
            if (closeParen.isEmpty()) {
                return Optional.empty();
            }

            return Optional.of(new ParseResult<>(innerResult.get().value(), closeParen.get().remaining()));
        };
    }

    /**
     * Parser that applies the given parser zero or more times, collecting results in a list
     *
     * @param <T> the type of values produced by the parser
     * @param parser the parser to apply repeatedly
     * @return a parser that always succeeds, returning a list of parsed values
     */
    public static <T> Parser<List<T>> many(Parser<T> parser) {
        return input -> {
            List<T> results = new ArrayList<>();
            String remaining = input;

            while (true) {
                Optional<ParseResult<T>> result = parser.parse(remaining);
                if (result.isEmpty()) {
                    break;
                }
                results.add(result.get().value());
                remaining = result.get().remaining();
            }

            return Optional.of(new ParseResult<>(results, remaining));
        };
    }

    /**
     * Parser that applies the given parser one or more times, collecting results in a list
     *
     * @param <T> the type of values produced by the parser
     * @param parser the parser to apply repeatedly
     * @return a parser that succeeds only if it can parse at least one value
     */
    public static <T> Parser<List<T>> some1(Parser<T> parser) {
        return input -> {
            Optional<ParseResult<T>> firstResult = parser.parse(input);
            if (firstResult.isEmpty()) {
                return Optional.empty();
            }

            Optional<ParseResult<List<T>>> manyResult = many(parser).parse(firstResult.get().remaining());
            if (manyResult.isEmpty()) {
                return Optional.empty();
            }

            List<T> allResults = new ArrayList<>();
            allResults.add(firstResult.get().value());
            allResults.addAll(manyResult.get().value());

            return Optional.of(new ParseResult<>(allResults, manyResult.get().remaining()));
        };
    }
}
