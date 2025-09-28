package info.jab.cis194.homework10;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Exercise 2: Advanced Applicative Patterns - Employee Data Parser
 *
 * <p>This exercise builds on Exercise 1 to create a more complex parser that can handle
 * structured employee data using applicative functors. It demonstrates how to combine
 * multiple parsers to parse complex data formats.
 *
 * <p>Based on CIS-194 Week 10: Applicative Functors
 */
public class Exercise2 {

    // Reuse the Parser interface and ParseResult from Exercise 1
    public record ParseResult<T>(T value, String remaining) {}

    @FunctionalInterface
    public interface Parser<T> {
        Optional<ParseResult<T>> parse(String input);

        default <U> Parser<U> fmap(Function<T, U> f) {
            return input -> this.parse(input).map(result ->
                new ParseResult<>(f.apply(result.value()), result.remaining()));
        }

        default <U> Parser<U> apply(Parser<Function<T, U>> funcParser) {
            return input -> funcParser.parse(input)
                .flatMap(funcResult -> this.parse(funcResult.remaining())
                    .map(valueResult -> new ParseResult<>(
                        funcResult.value().apply(valueResult.value()),
                        valueResult.remaining())));
        }

        default Parser<T> or(Parser<T> other) {
            return input -> this.parse(input).or(() -> other.parse(input));
        }
    }

    public static <T> Parser<T> pure(T value) {
        return input -> Optional.of(new ParseResult<>(value, input));
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
     */
    public static <T> Parser<List<T>> many(Parser<T> parser) {
        return input -> Optional.of(Trampoline.execute(manyTrampoline(parser, input, List.of())));
    }

    /**
     * Parser that applies the given parser one or more times, collecting results in a list
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

    // Employee data classes
    public record Employee(String name, int age, String position) {}

    public record EmployeeWithId(Optional<Integer> id, String name, int age, String position) {}

    public record EmployeeWithDept(String name, int age, String position, String department) {}

    public record EmployeeWithSalary(String name, int age, String position, int salary) {}

    /**
     * Parser for CSV fields (text until comma or end of line)
     */
    public static Parser<String> csvField() {
        return input -> {
            if (input.isEmpty()) {
                return Optional.empty();
            }

            StringBuilder field = new StringBuilder();
            int i = 0;
            while (i < input.length() && input.charAt(i) != ',' && input.charAt(i) != '\n') {
                field.append(input.charAt(i));
                i++;
            }

            if (field.length() == 0) {
                return Optional.empty();
            }

            return Optional.of(new ParseResult<>(field.toString(), input.substring(i)));
        };
    }

    /**
     * Parser for comma character
     */
    public static Parser<Character> comma() {
        return input -> {
            if (input.isEmpty() || input.charAt(0) != ',') {
                return Optional.empty();
            }
            return Optional.of(new ParseResult<>(',', input.substring(1)));
        };
    }

    /**
     * Parser for digit character
     */
    public static Parser<Character> digit() {
        return input -> {
            if (input.isEmpty() || !Character.isDigit(input.charAt(0))) {
                return Optional.empty();
            }
            return Optional.of(new ParseResult<>(input.charAt(0), input.substring(1)));
        };
    }

    /**
     * Parser for integer numbers using functional approach
     */
    public static Parser<Integer> integerParser() {
        return input -> some1(digit()).parse(input)
            .flatMap(result -> {
                var digitString = result.value().stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining());
                try {
                    var value = Integer.parseInt(digitString);
                    return Optional.of(new ParseResult<>(value, result.remaining()));
                } catch (NumberFormatException e) {
                    return Optional.empty();
                }
            });
    }

    /**
     * Parser for single whitespace character
     */
    public static Parser<Character> whitespaceChar() {
        return input -> {
            if (input.isEmpty() || !Character.isWhitespace(input.charAt(0))) {
                return Optional.empty();
            }
            return Optional.of(new ParseResult<>(input.charAt(0), input.substring(1)));
        };
    }

    /**
     * Parser for whitespace characters using functional approach
     */
    public static Parser<String> whitespace() {
        return input -> many(whitespaceChar()).parse(input)
            .map(result -> new ParseResult<>(
                result.value().stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining()),
                result.remaining()));
    }

    /**
     * Parser for newline character
     */
    public static Parser<Character> newline() {
        return input -> {
            if (input.isEmpty() || input.charAt(0) != '\n') {
                return Optional.empty();
            }
            return Optional.of(new ParseResult<>('\n', input.substring(1)));
        };
    }

    /**
     * Parser for dollar sign (for salary parsing)
     */
    public static Parser<Character> dollar() {
        return input -> {
            if (input.isEmpty() || input.charAt(0) != '$') {
                return Optional.empty();
            }
            return Optional.of(new ParseResult<>('$', input.substring(1)));
        };
    }

    /**
     * Lift2 - apply a binary function to two parser results
     */
    public static <A, B, C> Parser<C> lift2(Function<A, Function<B, C>> f, Parser<A> pa, Parser<B> pb) {
        return pb.apply(pa.fmap(f));
    }

    /**
     * Lift3 - apply a ternary function to three parser results
     */
    public static <A, B, C, D> Parser<D> lift3(
            Function<A, Function<B, Function<C, D>>> f,
            Parser<A> pa,
            Parser<B> pb,
            Parser<C> pc) {
        return pc.apply(lift2(f, pa, pb));
    }

    /**
     * Lift4 - apply a quaternary function to four parser results
     */
    public static <A, B, C, D, E> Parser<E> lift4(
            Function<A, Function<B, Function<C, Function<D, E>>>> f,
            Parser<A> pa,
            Parser<B> pb,
            Parser<C> pc,
            Parser<D> pd) {
        return pd.apply(lift3(f, pa, pb, pc));
    }

    /**
     * Basic employee parser: Name,Age,Position using functional composition
     */
    public static Parser<Employee> employeeParser() {
        return input -> csvField().parse(input)
            .flatMap(nameResult -> comma().parse(nameResult.remaining())
                .flatMap(comma1 -> integerParser().parse(comma1.remaining())
                    .flatMap(ageResult -> comma().parse(ageResult.remaining())
                        .flatMap(comma2 -> csvField().parse(comma2.remaining())
                            .map(positionResult -> new ParseResult<>(
                                new Employee(nameResult.value(), ageResult.value(), positionResult.value()),
                                positionResult.remaining()))))));
    }

    /**
     * Multi-employee parser that handles multiple lines using functional approach
     */
    public static Parser<List<Employee>> multiEmployeeParser() {
        return input -> {
            if (input.isEmpty()) {
                return Optional.of(new ParseResult<>(List.of(), input));
            }

            var employees = Stream.of(input.split("\n"))
                .filter(line -> !line.trim().isEmpty())
                .map(line -> employeeParser().parse(line.trim()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(ParseResult::value)
                .toList();

            return Optional.of(new ParseResult<>(employees, ""));
        };
    }

    /**
     * Employee parser with optional ID using applicative patterns
     */
    public static Parser<EmployeeWithId> employeeWithOptionalIdParser() {
        return input -> {
            // Try to parse ID first
            Optional<ParseResult<Integer>> idResult = integerParser().parse(input);
            Optional<Integer> id = Optional.empty();
            String remaining = input;

            if (idResult.isPresent()) {
                // Check if followed by comma
                Optional<ParseResult<Character>> commaResult = comma().parse(idResult.get().remaining());
                if (commaResult.isPresent()) {
                    id = Optional.of(idResult.get().value());
                    remaining = commaResult.get().remaining();
                }
            }

            // Parse regular employee data
            Optional<ParseResult<Employee>> employeeResult = employeeParser().parse(remaining);
            if (employeeResult.isEmpty()) {
                return Optional.empty();
            }

            Employee emp = employeeResult.get().value();
            EmployeeWithId result = new EmployeeWithId(id, emp.name(), emp.age(), emp.position());

            return Optional.of(new ParseResult<>(result, employeeResult.get().remaining()));
        };
    }

    /**
     * Employee parser with department using lift4
     */
    public static Parser<EmployeeWithDept> employeeWithDepartmentParser() {
        // Simplified approach - parse manually with applicative style
        return input -> {
            Optional<ParseResult<String>> nameResult = csvField().parse(input);
            if (nameResult.isEmpty()) return Optional.empty();

            Optional<ParseResult<Character>> comma1 = comma().parse(nameResult.get().remaining());
            if (comma1.isEmpty()) return Optional.empty();

            Optional<ParseResult<Integer>> ageResult = integerParser().parse(comma1.get().remaining());
            if (ageResult.isEmpty()) return Optional.empty();

            Optional<ParseResult<Character>> comma2 = comma().parse(ageResult.get().remaining());
            if (comma2.isEmpty()) return Optional.empty();

            Optional<ParseResult<String>> posResult = csvField().parse(comma2.get().remaining());
            if (posResult.isEmpty()) return Optional.empty();

            Optional<ParseResult<Character>> comma3 = comma().parse(posResult.get().remaining());
            if (comma3.isEmpty()) return Optional.empty();

            Optional<ParseResult<String>> deptResult = csvField().parse(comma3.get().remaining());
            if (deptResult.isEmpty()) return Optional.empty();

            EmployeeWithDept employee = new EmployeeWithDept(
                nameResult.get().value(),
                ageResult.get().value(),
                posResult.get().value(),
                deptResult.get().value());

            return Optional.of(new ParseResult<>(employee, deptResult.get().remaining()));
        };
    }

    /**
     * Validating employee parser that checks age range
     */
    public static Parser<Employee> validatingEmployeeParser() {
        return input -> {
            Optional<ParseResult<Employee>> result = employeeParser().parse(input);
            if (result.isEmpty()) {
                return Optional.empty();
            }

            Employee employee = result.get().value();
            if (employee.age() < 18 || employee.age() > 65) {
                return Optional.empty(); // Invalid age range
            }

            return result;
        };
    }

    /**
     * Employee parser with salary using applicative composition
     */
    public static Parser<EmployeeWithSalary> employeeWithSalaryParser() {
        return input -> {
            // Parse basic employee
            Optional<ParseResult<Employee>> empResult = employeeParser().parse(input);
            if (empResult.isEmpty()) {
                return Optional.empty();
            }

            // Parse comma
            Optional<ParseResult<Character>> commaResult = comma().parse(empResult.get().remaining());
            if (commaResult.isEmpty()) {
                return Optional.empty();
            }

            // Parse dollar sign
            Optional<ParseResult<Character>> dollarResult = dollar().parse(commaResult.get().remaining());
            if (dollarResult.isEmpty()) {
                return Optional.empty();
            }

            // Parse salary
            Optional<ParseResult<Integer>> salaryResult = integerParser().parse(dollarResult.get().remaining());
            if (salaryResult.isEmpty()) {
                return Optional.empty();
            }

            Employee emp = empResult.get().value();
            EmployeeWithSalary result = new EmployeeWithSalary(
                emp.name(),
                emp.age(),
                emp.position(),
                salaryResult.get().value());

            return Optional.of(new ParseResult<>(result, salaryResult.get().remaining()));
        };
    }
}
