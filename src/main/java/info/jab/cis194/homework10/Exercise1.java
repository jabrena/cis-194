package info.jab.cis194.homework10;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
            return input -> funcParser.parse(input)
                .flatMap(funcResult -> this.parse(funcResult.remaining())
                    .map(valueResult -> new ParseResult<>(
                        funcResult.value().apply(valueResult.value()),
                        valueResult.remaining())));
        }

        /**
         * Alternative operation - try this parser, if it fails try the other
         *
         * @param other the alternative parser to try
         * @return a parser that tries this parser first, then other if this fails
         */
        default Parser<T> or(Parser<T> other) {
            return input -> this.parse(input).or(() -> other.parse(input));
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
        return input -> first.parse(input)
            .flatMap(firstResult -> second.parse(firstResult.remaining())
                .map(secondResult -> new ParseResult<>(
                    "" + firstResult.value() + secondResult.value(),
                    secondResult.remaining())));
    }

    /**
     * Parser that matches exactly two digits and converts them to an integer
     *
     * @return a parser that succeeds if it can parse exactly two consecutive digits
     */
    public static Parser<Integer> twoDigitNumber() {
        return input -> digit().parse(input)
            .flatMap(firstDigit -> digit().parse(firstDigit.remaining())
                .map(secondDigit -> {
                    var tens = Character.getNumericValue(firstDigit.value());
                    var ones = Character.getNumericValue(secondDigit.value());
                    return new ParseResult<>(tens * 10 + ones, secondDigit.remaining());
                }));
    }

    /**
     * Parser that matches a letter or digit character
     *
     * @return a parser that succeeds if the first character is a letter or digit
     */
    public static Parser<Character> letterOrDigit() {
        return input -> {
            if (input.isEmpty() || !Character.isLetterOrDigit(input.charAt(0))) {
                return Optional.empty();
            }
            var character = input.charAt(0);
            return Optional.of(new ParseResult<>(character, input.substring(1)));
        };
    }

    /**
     * Parser that matches an identifier (letter followed by zero or more letters or digits)
     *
     * @return a parser that succeeds if it can parse a valid identifier
     */
    public static Parser<String> identifier() {
        return input -> letter().parse(input)
            .flatMap(firstResult -> many(letterOrDigit()).parse(firstResult.remaining())
                .map(restResult -> {
                    var identifierChars = List.<Character>of(firstResult.value());
                    var allChars = Stream.concat(identifierChars.stream(), restResult.value().stream())
                        .map(String::valueOf)
                        .collect(Collectors.joining());
                    return new ParseResult<>(allChars, restResult.remaining());
                }));
    }

    /**
     * Parser that matches a value surrounded by parentheses
     *
     * @param <T> the type of the inner value
     * @param inner the parser for the inner value
     * @return a parser that matches (inner)
     */
    public static <T> Parser<T> parenthesized(Parser<T> inner) {
        return input -> char_('(').parse(input)
            .flatMap(openParen -> inner.parse(openParen.remaining())
                .flatMap(innerResult -> char_(')').parse(innerResult.remaining())
                    .map(closeParen -> new ParseResult<>(innerResult.value(), closeParen.remaining()))));
    }

    /**
     * Trampoline interface for tail-recursive optimization
     */
    @FunctionalInterface
    public interface Trampoline<T> {
        Trampoline<T> apply();

        default boolean isComplete() {
            return false;
        }

        default T result() {
            throw new UnsupportedOperationException("Not completed yet");
        }

        static <T> Trampoline<T> complete(T value) {
            return new Trampoline<T>() {
                @Override
                public boolean isComplete() {
                    return true;
                }

                @Override
                public T result() {
                    return value;
                }

                @Override
                public Trampoline<T> apply() {
                    throw new UnsupportedOperationException("Already completed");
                }
            };
        }

        static <T> T execute(Trampoline<T> trampoline) {
            while (!trampoline.isComplete()) {
                trampoline = trampoline.apply();
            }
            return trampoline.result();
        }
    }

    /**
     * Helper method for many parser using trampoline
     */
    private static <T> Trampoline<ParseResult<List<T>>> manyTrampoline(
            Parser<T> parser, String input, List<T> accumulated) {
        return parser.parse(input)
            .map(result -> (Trampoline<ParseResult<List<T>>>) () ->
                manyTrampoline(parser, result.remaining(),
                    Stream.concat(accumulated.stream(), Stream.of(result.value())).toList()))
            .orElse(Trampoline.complete(new ParseResult<>(List.copyOf(accumulated), input)));
    }

    /**
     * Parser that applies the given parser zero or more times, collecting results in a list
     *
     * @param <T> the type of values produced by the parser
     * @param parser the parser to apply repeatedly
     * @return a parser that always succeeds, returning a list of parsed values
     */
    public static <T> Parser<List<T>> many(Parser<T> parser) {
        return input -> Optional.of(Trampoline.execute(manyTrampoline(parser, input, List.of())));
    }

    /**
     * Parser that applies the given parser one or more times, collecting results in a list
     *
     * @param <T> the type of values produced by the parser
     * @param parser the parser to apply repeatedly
     * @return a parser that succeeds only if it can parse at least one value
     */
    public static <T> Parser<List<T>> some1(Parser<T> parser) {
        return input -> parser.parse(input)
            .flatMap(firstResult -> many(parser).parse(firstResult.remaining())
                .map(manyResult -> {
                    var allResults = Stream.concat(
                        Stream.of(firstResult.value()),
                        manyResult.value().stream()
                    ).toList();
                    return new ParseResult<>(allResults, manyResult.remaining());
                }));
    }
}
