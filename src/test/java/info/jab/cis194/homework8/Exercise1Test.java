package info.jab.cis194.homework8;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests for Exercise 1: IO and File Processing
 *
 * This exercise focuses on IO operations, file processing, and functional programming
 * concepts applied to reading and parsing data from files.
 *
 * Based on CIS-194 Week 8: IO and file operations
 */
@DisplayName("Exercise 1: IO and File Processing")
class Exercise1Test {

    @TempDir
    Path tempDir;

    private Path testFile;
    private Path emptyFile;
    private Path malformedFile;

    @BeforeEach
    void setUp() throws IOException {
        // Create test files
        testFile = tempDir.resolve("employees.txt");
        emptyFile = tempDir.resolve("empty.txt");
        malformedFile = tempDir.resolve("malformed.txt");

        // Sample employee data
        String sampleData = """
            Emp 1 "John Doe"
            Emp 2 "Jane Smith"
            Emp 3 "Bob Johnson"
            Emp 4 "Alice Brown"
            Emp 5 "Charlie Davis"
            """;
        Files.writeString(testFile, sampleData);

        // Empty file
        Files.writeString(emptyFile, "");

        // Malformed data
        String malformedData = """
            Emp 1 "John Doe"
            Invalid line here
            Emp 3 "Bob Johnson"
            Emp "Invalid ID" "Alice Brown"
            """;
        Files.writeString(malformedFile, malformedData);
    }

    @Nested
    @DisplayName("Employee Data Parsing")
    class EmployeeDataParsing {

        @Test
        @DisplayName("Should parse valid employee line correctly")
        void shouldParseValidEmployeeLine() {
            // Given
            String validLine = "Emp 42 \"John Doe\"";

            // When
            Optional<Exercise1.Employee> result = Exercise1.parseEmployee(validLine);

            // Then
            assertThat(result).isPresent();
            Exercise1.Employee employee = result.get();
            assertThat(employee.getId()).isEqualTo(42);
            assertThat(employee.getName()).isEqualTo("John Doe");
        }

        @Test
        @DisplayName("Should handle employee names with special characters")
        void shouldHandleSpecialCharacters() {
            // Given
            String specialLine = "Emp 123 \"Mary O'Connor-Smith\"";

            // When
            Optional<Exercise1.Employee> result = Exercise1.parseEmployee(specialLine);

            // Then
            assertThat(result).isPresent();
            Exercise1.Employee employee = result.get();
            assertThat(employee.getId()).isEqualTo(123);
            assertThat(employee.getName()).isEqualTo("Mary O'Connor-Smith");
        }

        @Test
        @DisplayName("Should return empty for invalid employee line")
        void shouldReturnEmptyForInvalidLine() {
            // Given
            String invalidLine = "Invalid employee data";

            // When
            Optional<Exercise1.Employee> result = Exercise1.parseEmployee(invalidLine);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should handle malformed employee ID")
        void shouldHandleMalformedId() {
            // Given
            String malformedLine = "Emp abc \"John Doe\"";

            // When
            Optional<Exercise1.Employee> result = Exercise1.parseEmployee(malformedLine);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should handle missing quotes in name")
        void shouldHandleMissingQuotes() {
            // Given
            String noQuotesLine = "Emp 42 John Doe";

            // When
            Optional<Exercise1.Employee> result = Exercise1.parseEmployee(noQuotesLine);

            // Then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("File Reading Operations")
    class FileReadingOperations {

        @Test
        @DisplayName("Should read and parse employees from file")
        void shouldReadEmployeesFromFile() throws IOException {
            // Given
            // Test file is set up in @BeforeEach with sample employee data

            // When
            List<Exercise1.Employee> employees = Exercise1.readEmployeesFromFile(testFile.toString());

            // Then
            assertThat(employees).hasSize(5);

            // Check first employee
            Exercise1.Employee first = employees.get(0);
            assertThat(first.getId()).isEqualTo(1);
            assertThat(first.getName()).isEqualTo("John Doe");

            // Check last employee
            Exercise1.Employee last = employees.get(4);
            assertThat(last.getId()).isEqualTo(5);
            assertThat(last.getName()).isEqualTo("Charlie Davis");
        }

        @Test
        @DisplayName("Should handle empty file gracefully")
        void shouldHandleEmptyFile() throws IOException {
            // Given
            // Empty file is set up in @BeforeEach

            // When
            List<Exercise1.Employee> employees = Exercise1.readEmployeesFromFile(emptyFile.toString());

            // Then
            assertThat(employees).isEmpty();
        }

        @Test
        @DisplayName("Should skip malformed lines and parse valid ones")
        void shouldSkipMalformedLines() throws IOException {
            // Given
            // Malformed file is set up in @BeforeEach with some invalid lines

            // When
            List<Exercise1.Employee> employees = Exercise1.readEmployeesFromFile(malformedFile.toString());

            // Then
            assertThat(employees).hasSize(2);
            assertThat(employees.get(0).getId()).isEqualTo(1);
            assertThat(employees.get(0).getName()).isEqualTo("John Doe");
            assertThat(employees.get(1).getId()).isEqualTo(3);
            assertThat(employees.get(1).getName()).isEqualTo("Bob Johnson");
        }

        @Test
        @DisplayName("Should throw IOException for non-existent file")
        void shouldThrowIOExceptionForNonExistentFile() {
            // Given
            String nonExistentFile = "non-existent-file.txt";

            // When & Then
            assertThatThrownBy(() -> Exercise1.readEmployeesFromFile(nonExistentFile))
                    .isInstanceOf(IOException.class);
        }
    }

    @Nested
    @DisplayName("Functional Operations on Employees")
    class FunctionalOperations {

        private List<Exercise1.Employee> sampleEmployees;

        @BeforeEach
        void setUpEmployees() throws IOException {
            sampleEmployees = Exercise1.readEmployeesFromFile(testFile.toString());
        }

        @Test
        @DisplayName("Should filter employees by ID range")
        void shouldFilterEmployeesByIdRange() {
            // Given
            // Sample employees loaded in @BeforeEach

            // When
            List<Exercise1.Employee> filtered = Exercise1.filterEmployeesByIdRange(sampleEmployees, 2, 4);

            // Then
            assertThat(filtered).hasSize(3);
            assertThat(filtered.get(0).getId()).isEqualTo(2);
            assertThat(filtered.get(1).getId()).isEqualTo(3);
            assertThat(filtered.get(2).getId()).isEqualTo(4);
        }

        @Test
        @DisplayName("Should map employee names to uppercase")
        void shouldMapEmployeeNamesToUppercase() {
            // Given
            // Sample employees loaded in @BeforeEach

            // When
            List<String> upperNames = Exercise1.mapEmployeeNames(sampleEmployees, String::toUpperCase);

            // Then
            assertThat(upperNames).hasSize(5);
            assertThat(upperNames.get(0)).isEqualTo("JOHN DOE");
            assertThat(upperNames.get(1)).isEqualTo("JANE SMITH");
            assertThat(upperNames.get(2)).isEqualTo("BOB JOHNSON");
        }

        @Test
        @DisplayName("Should find employee by ID")
        void shouldFindEmployeeById() {
            // Given
            int targetId = 3;

            // When
            Optional<Exercise1.Employee> found = Exercise1.findEmployeeById(sampleEmployees, targetId);

            // Then
            assertThat(found).isPresent();
            assertThat(found.get().getId()).isEqualTo(3);
            assertThat(found.get().getName()).isEqualTo("Bob Johnson");
        }

        @Test
        @DisplayName("Should return empty when employee not found")
        void shouldReturnEmptyWhenEmployeeNotFound() {
            // Given
            int nonExistentId = 999;

            // When
            Optional<Exercise1.Employee> found = Exercise1.findEmployeeById(sampleEmployees, nonExistentId);

            // Then
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("Should count employees with name containing substring")
        void shouldCountEmployeesWithNameContaining() {
            // Given
            String substring = "Jo";

            // When
            long count = Exercise1.countEmployeesWithNameContaining(sampleEmployees, substring);

            // Then
            assertThat(count).isEqualTo(2); // John Doe and Bob Johnson
        }
    }

    @Nested
    @DisplayName("IO Monadic Operations")
    class IOMonadicOperations {

        @Test
        @DisplayName("Should compose IO operations functionally")
        void shouldComposeIOOperations() throws IOException {
            // Given
            Function<String, List<Exercise1.Employee>> readAndFilter =
                filename -> {
                    try {
                        List<Exercise1.Employee> employees = Exercise1.readEmployeesFromFile(filename);
                        return Exercise1.filterEmployeesByIdRange(employees, 1, 3);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                };

            // When
            List<Exercise1.Employee> result = readAndFilter.apply(testFile.toString());

            // Then
            assertThat(result).hasSize(3);
            assertThat(result.get(0).getName()).isEqualTo("John Doe");
            assertThat(result.get(1).getName()).isEqualTo("Jane Smith");
            assertThat(result.get(2).getName()).isEqualTo("Bob Johnson");
        }

        @Test
        @DisplayName("Should handle IO operations with error recovery")
        void shouldHandleIOOperationsWithErrorRecovery() {
            // Given
            String nonExistentFile = "non-existent-file.txt";
            String fallbackFile = testFile.toString();

            // When
            List<Exercise1.Employee> result = Exercise1.readEmployeesWithFallback(
                nonExistentFile,
                fallbackFile
            );

            // Then
            assertThat(result).hasSize(5); // Should fall back to test file
        }
    }

    @Nested
    @DisplayName("Employee Statistics")
    class EmployeeStatistics {

        @Test
        @DisplayName("Should calculate employee statistics from file")
        void shouldCalculateEmployeeStatistics() throws IOException {
            // Given
            // Test file with employee data set up in @BeforeEach

            // When
            Exercise1.EmployeeStats stats = Exercise1.calculateEmployeeStats(testFile.toString());

            // Then
            assertThat(stats.getTotalCount()).isEqualTo(5);
            assertThat(stats.getMinId()).isEqualTo(1);
            assertThat(stats.getMaxId()).isEqualTo(5);
            assertThat(stats.getAverageId()).isEqualTo(3.0, within(0.01));
            assertThat(stats.getEmployeeNames())
                    .contains("John Doe")
                    .contains("Charlie Davis");
        }

        @Test
        @DisplayName("Should handle empty file for statistics")
        void shouldHandleEmptyFileForStatistics() throws IOException {
            // Given
            // Empty file set up in @BeforeEach

            // When
            Exercise1.EmployeeStats stats = Exercise1.calculateEmployeeStats(emptyFile.toString());

            // Then
            assertThat(stats.getTotalCount()).isEqualTo(0);
            assertThat(stats.getMinId()).isEqualTo(0);
            assertThat(stats.getMaxId()).isEqualTo(0);
            assertThat(stats.getAverageId()).isEqualTo(0.0);
            assertThat(stats.getEmployeeNames()).isEmpty();
        }
    }
}
