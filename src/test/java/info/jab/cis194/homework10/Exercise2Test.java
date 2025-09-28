package info.jab.cis194.homework10;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for Exercise 2: Advanced Applicative Patterns - Employee Data Parser
 *
 * This exercise builds on Exercise 1 to create a more complex parser that can handle
 * structured employee data using applicative functors. It demonstrates how to combine
 * multiple parsers to parse complex data formats.
 *
 * Based on CIS-194 Week 10: Applicative Functors
 */
@DisplayName("Exercise 2: Advanced Employee Data Parser")
class Exercise2Test {

    @Nested
    @DisplayName("Employee Record Parsing")
    class EmployeeRecordParsing {

        @Test
        @DisplayName("Should parse simple employee record")
        void shouldParseSimpleEmployeeRecord() {
            // Given
            var employeeParser = Exercise2.employeeParser();
            var input = "John,25,Engineer";

            // When
            var result = employeeParser.parse(input);

            // Then
            assertThat(result)
                .isPresent()
                .hasValueSatisfying(parseResult -> {
                    assertThat(parseResult.remaining()).isEmpty();
                    var employee = parseResult.value();
                    assertThat(employee.name()).isEqualTo("John");
                    assertThat(employee.age()).isEqualTo(25);
                    assertThat(employee.position()).isEqualTo("Engineer");
                });
        }

        @Test
        @DisplayName("Should parse employee with complex name")
        void shouldParseEmployeeWithComplexName() {
            // Given
            var employeeParser = Exercise2.employeeParser();
            var input = "Mary-Jane O'Connor,30,Senior Developer";

            // When
            var result = employeeParser.parse(input);

            // Then
            assertThat(result)
                .isPresent()
                .hasValueSatisfying(parseResult -> {
                    assertThat(parseResult.remaining()).isEmpty();
                    var employee = parseResult.value();
                    assertThat(employee.name()).isEqualTo("Mary-Jane O'Connor");
                    assertThat(employee.age()).isEqualTo(30);
                    assertThat(employee.position()).isEqualTo("Senior Developer");
                });
        }

        @Test
        @DisplayName("Should fail to parse malformed employee record")
        void shouldFailToParseInvalidRecord() {
            // Given
            var employeeParser = Exercise2.employeeParser();
            var input = "John,invalid_age,Engineer";

            // When
            var result = employeeParser.parse(input);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should fail to parse incomplete employee record")
        void shouldFailToParseIncompleteRecord() {
            // Given
            var employeeParser = Exercise2.employeeParser();
            var input = "John,25";

            // When
            var result = employeeParser.parse(input);

            // Then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Multi-Employee Parsing")
    class MultiEmployeeParsing {

        @Test
        @DisplayName("Should parse multiple employees separated by newlines")
        void shouldParseMultipleEmployees() {
            // Given
            var multiParser = Exercise2.multiEmployeeParser();
            var input = """
                John,25,Engineer
                Jane,28,Designer
                Bob,35,Manager""";

            // When
            var result = multiParser.parse(input);

            // Then
            assertThat(result)
                .isPresent()
                .hasValueSatisfying(parseResult -> {
                    var employees = parseResult.value();
                    assertThat(employees)
                        .hasSize(3)
                        .satisfiesExactly(
                            emp1 -> {
                                assertThat(emp1.name()).isEqualTo("John");
                                assertThat(emp1.age()).isEqualTo(25);
                                assertThat(emp1.position()).isEqualTo("Engineer");
                            },
                            emp2 -> {
                                assertThat(emp2.name()).isEqualTo("Jane");
                                assertThat(emp2.age()).isEqualTo(28);
                                assertThat(emp2.position()).isEqualTo("Designer");
                            },
                            emp3 -> {
                                assertThat(emp3.name()).isEqualTo("Bob");
                                assertThat(emp3.age()).isEqualTo(35);
                                assertThat(emp3.position()).isEqualTo("Manager");
                            }
                        );
                });
        }

        @Test
        @DisplayName("Should handle empty employee list")
        void shouldHandleEmptyEmployeeList() {
            // Given
            var multiParser = Exercise2.multiEmployeeParser();
            var input = "";

            // When
            var result = multiParser.parse(input);

            // Then
            assertThat(result)
                .isPresent()
                .hasValueSatisfying(parseResult -> {
                    assertThat(parseResult.value()).isEmpty();
                    assertThat(parseResult.remaining()).isEmpty();
                });
        }

        @Test
        @DisplayName("Should skip invalid lines and parse valid ones")
        void shouldSkipInvalidLines() {
            // Given
            var multiParser = Exercise2.multiEmployeeParser();
            var input = """
                John,25,Engineer
                Invalid line here
                Jane,28,Designer
                Another,invalid,line,format
                Bob,35,Manager""";

            // When
            var result = multiParser.parse(input);

            // Then
            assertThat(result)
                .isPresent()
                .hasValueSatisfying(parseResult -> {
                    var employees = parseResult.value();
                    assertThat(employees)
                        .hasSize(3)
                        .extracting(Exercise2.Employee::name)
                        .containsExactly("John", "Jane", "Bob");
                });
        }
    }

    @Nested
    @DisplayName("Advanced Applicative Patterns")
    class AdvancedApplicativePatterns {

        @Test
        @DisplayName("Should parse optional employee ID")
        void shouldParseOptionalEmployeeId() {
            // Given
            var parserWithId = Exercise2.employeeWithOptionalIdParser();
            var inputWithId = "123,John,25,Engineer";
            var inputWithoutId = "John,25,Engineer";

            // When
            var resultWithId = parserWithId.parse(inputWithId);
            var resultWithoutId = parserWithId.parse(inputWithoutId);

            // Then
            assertThat(resultWithId)
                .isPresent()
                .hasValueSatisfying(parseResult -> {
                    var employee = parseResult.value();
                    assertThat(employee.id()).hasValue(123);
                    assertThat(employee.name()).isEqualTo("John");
                });

            assertThat(resultWithoutId)
                .isPresent()
                .hasValueSatisfying(parseResult -> {
                    var employee = parseResult.value();
                    assertThat(employee.id()).isEmpty();
                    assertThat(employee.name()).isEqualTo("John");
                });
        }

        @Test
        @DisplayName("Should parse employee with department using lift3")
        void shouldParseEmployeeWithDepartment() {
            // Given
            var deptParser = Exercise2.employeeWithDepartmentParser();
            var input = "John,25,Engineer,IT";

            // When
            var result = deptParser.parse(input);

            // Then
            assertThat(result)
                .isPresent()
                .hasValueSatisfying(parseResult -> {
                    var employee = parseResult.value();
                    assertThat(employee.name()).isEqualTo("John");
                    assertThat(employee.age()).isEqualTo(25);
                    assertThat(employee.position()).isEqualTo("Engineer");
                    assertThat(employee.department()).isEqualTo("IT");
                });
        }

        @Test
        @DisplayName("Should validate employee age range")
        void shouldValidateEmployeeAgeRange() {
            // Given
            var validatingParser = Exercise2.validatingEmployeeParser();
            var validInput = "John,25,Engineer";
            var invalidInput = "John,15,Engineer"; // Too young

            // When
            var validResult = validatingParser.parse(validInput);
            var invalidResult = validatingParser.parse(invalidInput);

            // Then
            assertThat(validResult).isPresent();
            assertThat(invalidResult).isEmpty();
        }

        @Test
        @DisplayName("Should parse employee with salary using applicative composition")
        void shouldParseEmployeeWithSalary() {
            // Given
            var salaryParser = Exercise2.employeeWithSalaryParser();
            var input = "John,25,Engineer,$50000";

            // When
            var result = salaryParser.parse(input);

            // Then
            assertThat(result)
                .isPresent()
                .hasValueSatisfying(parseResult -> {
                    var employee = parseResult.value();
                    assertThat(employee.name()).isEqualTo("John");
                    assertThat(employee.age()).isEqualTo(25);
                    assertThat(employee.position()).isEqualTo("Engineer");
                    assertThat(employee.salary()).isEqualTo(50000);
                });
        }
    }

    @Nested
    @DisplayName("Parser Utilities")
    class ParserUtilities {

        @Test
        @DisplayName("Should parse comma-separated values")
        void shouldParseCommaSeparatedValues() {
            // Given
            var csvParser = Exercise2.csvField();
            var input = "Hello,World";

            // When
            var result = csvParser.parse(input);

            // Then
            assertThat(result)
                .isPresent()
                .hasValueSatisfying(parseResult -> {
                    assertThat(parseResult.value()).isEqualTo("Hello");
                    assertThat(parseResult.remaining()).isEqualTo(",World");
                });
        }

        @Test
        @DisplayName("Should parse integer numbers")
        void shouldParseIntegerNumbers() {
            // Given
            var intParser = Exercise2.integerParser();
            var input = "12345abc";

            // When
            var result = intParser.parse(input);

            // Then
            assertThat(result)
                .isPresent()
                .hasValueSatisfying(parseResult -> {
                    assertThat(parseResult.value()).isEqualTo(12345);
                    assertThat(parseResult.remaining()).isEqualTo("abc");
                });
        }

        @Test
        @DisplayName("Should fail to parse non-integer")
        void shouldFailToParseNonInteger() {
            // Given
            var intParser = Exercise2.integerParser();
            var input = "abc123";

            // When
            var result = intParser.parse(input);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should parse whitespace")
        void shouldParseWhitespace() {
            // Given
            var wsParser = Exercise2.whitespace();
            var input = "   \t\n  hello";

            // When
            var result = wsParser.parse(input);

            // Then
            assertThat(result)
                .isPresent()
                .hasValueSatisfying(parseResult -> {
                    assertThat(parseResult.value()).isEqualTo("   \t\n  ");
                    assertThat(parseResult.remaining()).isEqualTo("hello");
                });
        }
    }
}
