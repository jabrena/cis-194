package info.jab.cis194.homework10;

import java.util.List;
import java.util.Optional;
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
            Exercise2.Parser<Exercise2.Employee> employeeParser = Exercise2.employeeParser();
            String input = "John,25,Engineer";

            // When
            Optional<Exercise2.ParseResult<Exercise2.Employee>> result = employeeParser.parse(input);

            // Then
            assertThat(result).isPresent();
            Exercise2.Employee employee = result.get().value();
            assertThat(employee.name()).isEqualTo("John");
            assertThat(employee.age()).isEqualTo(25);
            assertThat(employee.position()).isEqualTo("Engineer");
            assertThat(result.get().remaining()).isEmpty();
        }

        @Test
        @DisplayName("Should parse employee with complex name")
        void shouldParseEmployeeWithComplexName() {
            // Given
            Exercise2.Parser<Exercise2.Employee> employeeParser = Exercise2.employeeParser();
            String input = "Mary-Jane O'Connor,30,Senior Developer";

            // When
            Optional<Exercise2.ParseResult<Exercise2.Employee>> result = employeeParser.parse(input);

            // Then
            assertThat(result).isPresent();
            Exercise2.Employee employee = result.get().value();
            assertThat(employee.name()).isEqualTo("Mary-Jane O'Connor");
            assertThat(employee.age()).isEqualTo(30);
            assertThat(employee.position()).isEqualTo("Senior Developer");
            assertThat(result.get().remaining()).isEmpty();
        }

        @Test
        @DisplayName("Should fail to parse malformed employee record")
        void shouldFailToParseInvalidRecord() {
            // Given
            Exercise2.Parser<Exercise2.Employee> employeeParser = Exercise2.employeeParser();
            String input = "John,invalid_age,Engineer";

            // When
            Optional<Exercise2.ParseResult<Exercise2.Employee>> result = employeeParser.parse(input);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should fail to parse incomplete employee record")
        void shouldFailToParseIncompleteRecord() {
            // Given
            Exercise2.Parser<Exercise2.Employee> employeeParser = Exercise2.employeeParser();
            String input = "John,25";

            // When
            Optional<Exercise2.ParseResult<Exercise2.Employee>> result = employeeParser.parse(input);

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
            Exercise2.Parser<List<Exercise2.Employee>> multiParser = Exercise2.multiEmployeeParser();
            String input = """
                John,25,Engineer
                Jane,28,Designer
                Bob,35,Manager""";

            // When
            Optional<Exercise2.ParseResult<List<Exercise2.Employee>>> result = multiParser.parse(input);

            // Then
            assertThat(result).isPresent();
            List<Exercise2.Employee> employees = result.get().value();
            assertThat(employees).hasSize(3);

            assertThat(employees.get(0).name()).isEqualTo("John");
            assertThat(employees.get(0).age()).isEqualTo(25);
            assertThat(employees.get(0).position()).isEqualTo("Engineer");

            assertThat(employees.get(1).name()).isEqualTo("Jane");
            assertThat(employees.get(1).age()).isEqualTo(28);
            assertThat(employees.get(1).position()).isEqualTo("Designer");

            assertThat(employees.get(2).name()).isEqualTo("Bob");
            assertThat(employees.get(2).age()).isEqualTo(35);
            assertThat(employees.get(2).position()).isEqualTo("Manager");
        }

        @Test
        @DisplayName("Should handle empty employee list")
        void shouldHandleEmptyEmployeeList() {
            // Given
            Exercise2.Parser<List<Exercise2.Employee>> multiParser = Exercise2.multiEmployeeParser();
            String input = "";

            // When
            Optional<Exercise2.ParseResult<List<Exercise2.Employee>>> result = multiParser.parse(input);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().value()).isEmpty();
            assertThat(result.get().remaining()).isEmpty();
        }

        @Test
        @DisplayName("Should skip invalid lines and parse valid ones")
        void shouldSkipInvalidLines() {
            // Given
            Exercise2.Parser<List<Exercise2.Employee>> multiParser = Exercise2.multiEmployeeParser();
            String input = """
                John,25,Engineer
                Invalid line here
                Jane,28,Designer
                Another,invalid,line,format
                Bob,35,Manager""";

            // When
            Optional<Exercise2.ParseResult<List<Exercise2.Employee>>> result = multiParser.parse(input);

            // Then
            assertThat(result).isPresent();
            List<Exercise2.Employee> employees = result.get().value();
            assertThat(employees).hasSize(3);
            assertThat(employees.get(0).name()).isEqualTo("John");
            assertThat(employees.get(1).name()).isEqualTo("Jane");
            assertThat(employees.get(2).name()).isEqualTo("Bob");
        }
    }

    @Nested
    @DisplayName("Advanced Applicative Patterns")
    class AdvancedApplicativePatterns {

        @Test
        @DisplayName("Should parse optional employee ID")
        void shouldParseOptionalEmployeeId() {
            // Given
            Exercise2.Parser<Exercise2.EmployeeWithId> parserWithId = Exercise2.employeeWithOptionalIdParser();
            String input1 = "123,John,25,Engineer";
            String input2 = "John,25,Engineer";

            // When
            Optional<Exercise2.ParseResult<Exercise2.EmployeeWithId>> result1 = parserWithId.parse(input1);
            Optional<Exercise2.ParseResult<Exercise2.EmployeeWithId>> result2 = parserWithId.parse(input2);

            // Then
            assertThat(result1).isPresent();
            Exercise2.EmployeeWithId emp1 = result1.get().value();
            assertThat(emp1.id()).hasValue(123);
            assertThat(emp1.name()).isEqualTo("John");

            assertThat(result2).isPresent();
            Exercise2.EmployeeWithId emp2 = result2.get().value();
            assertThat(emp2.id()).isEmpty();
            assertThat(emp2.name()).isEqualTo("John");
        }

        @Test
        @DisplayName("Should parse employee with department using lift3")
        void shouldParseEmployeeWithDepartment() {
            // Given
            Exercise2.Parser<Exercise2.EmployeeWithDept> deptParser = Exercise2.employeeWithDepartmentParser();
            String input = "John,25,Engineer,IT";

            // When
            Optional<Exercise2.ParseResult<Exercise2.EmployeeWithDept>> result = deptParser.parse(input);

            // Then
            assertThat(result).isPresent();
            Exercise2.EmployeeWithDept employee = result.get().value();
            assertThat(employee.name()).isEqualTo("John");
            assertThat(employee.age()).isEqualTo(25);
            assertThat(employee.position()).isEqualTo("Engineer");
            assertThat(employee.department()).isEqualTo("IT");
        }

        @Test
        @DisplayName("Should validate employee age range")
        void shouldValidateEmployeeAgeRange() {
            // Given
            Exercise2.Parser<Exercise2.Employee> validatingParser = Exercise2.validatingEmployeeParser();
            String validInput = "John,25,Engineer";
            String invalidInput = "John,15,Engineer"; // Too young

            // When
            Optional<Exercise2.ParseResult<Exercise2.Employee>> validResult = validatingParser.parse(validInput);
            Optional<Exercise2.ParseResult<Exercise2.Employee>> invalidResult = validatingParser.parse(invalidInput);

            // Then
            assertThat(validResult).isPresent();
            assertThat(invalidResult).isEmpty();
        }

        @Test
        @DisplayName("Should parse employee with salary using applicative composition")
        void shouldParseEmployeeWithSalary() {
            // Given
            Exercise2.Parser<Exercise2.EmployeeWithSalary> salaryParser = Exercise2.employeeWithSalaryParser();
            String input = "John,25,Engineer,$50000";

            // When
            Optional<Exercise2.ParseResult<Exercise2.EmployeeWithSalary>> result = salaryParser.parse(input);

            // Then
            assertThat(result).isPresent();
            Exercise2.EmployeeWithSalary employee = result.get().value();
            assertThat(employee.name()).isEqualTo("John");
            assertThat(employee.age()).isEqualTo(25);
            assertThat(employee.position()).isEqualTo("Engineer");
            assertThat(employee.salary()).isEqualTo(50000);
        }
    }

    @Nested
    @DisplayName("Parser Utilities")
    class ParserUtilities {

        @Test
        @DisplayName("Should parse comma-separated values")
        void shouldParseCommaSeparatedValues() {
            // Given
            Exercise2.Parser<String> csvParser = Exercise2.csvField();
            String input = "Hello,World";

            // When
            Optional<Exercise2.ParseResult<String>> result = csvParser.parse(input);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().value()).isEqualTo("Hello");
            assertThat(result.get().remaining()).isEqualTo(",World");
        }

        @Test
        @DisplayName("Should parse integer numbers")
        void shouldParseIntegerNumbers() {
            // Given
            Exercise2.Parser<Integer> intParser = Exercise2.integerParser();
            String input = "12345abc";

            // When
            Optional<Exercise2.ParseResult<Integer>> result = intParser.parse(input);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().value()).isEqualTo(12345);
            assertThat(result.get().remaining()).isEqualTo("abc");
        }

        @Test
        @DisplayName("Should fail to parse non-integer")
        void shouldFailToParseNonInteger() {
            // Given
            Exercise2.Parser<Integer> intParser = Exercise2.integerParser();
            String input = "abc123";

            // When
            Optional<Exercise2.ParseResult<Integer>> result = intParser.parse(input);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should parse whitespace")
        void shouldParseWhitespace() {
            // Given
            Exercise2.Parser<String> wsParser = Exercise2.whitespace();
            String input = "   \t\n  hello";

            // When
            Optional<Exercise2.ParseResult<String>> result = wsParser.parse(input);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().value()).isEqualTo("   \t\n  ");
            assertThat(result.get().remaining()).isEqualTo("hello");
        }
    }
}
