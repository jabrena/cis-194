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

import static org.junit.jupiter.api.Assertions.*;

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
            String validLine = "Emp 42 \"John Doe\"";

            Optional<Exercise1.Employee> result = Exercise1.parseEmployee(validLine);

            assertTrue(result.isPresent());
            Exercise1.Employee employee = result.get();
            assertEquals(42, employee.getId());
            assertEquals("John Doe", employee.getName());
        }

        @Test
        @DisplayName("Should handle employee names with special characters")
        void shouldHandleSpecialCharacters() {
            String specialLine = "Emp 123 \"Mary O'Connor-Smith\"";

            Optional<Exercise1.Employee> result = Exercise1.parseEmployee(specialLine);

            assertTrue(result.isPresent());
            Exercise1.Employee employee = result.get();
            assertEquals(123, employee.getId());
            assertEquals("Mary O'Connor-Smith", employee.getName());
        }

        @Test
        @DisplayName("Should return empty for invalid employee line")
        void shouldReturnEmptyForInvalidLine() {
            String invalidLine = "Invalid employee data";

            Optional<Exercise1.Employee> result = Exercise1.parseEmployee(invalidLine);

            assertFalse(result.isPresent());
        }

        @Test
        @DisplayName("Should handle malformed employee ID")
        void shouldHandleMalformedId() {
            String malformedLine = "Emp abc \"John Doe\"";

            Optional<Exercise1.Employee> result = Exercise1.parseEmployee(malformedLine);

            assertFalse(result.isPresent());
        }

        @Test
        @DisplayName("Should handle missing quotes in name")
        void shouldHandleMissingQuotes() {
            String noQuotesLine = "Emp 42 John Doe";

            Optional<Exercise1.Employee> result = Exercise1.parseEmployee(noQuotesLine);

            assertFalse(result.isPresent());
        }
    }

    @Nested
    @DisplayName("File Reading Operations")
    class FileReadingOperations {

        @Test
        @DisplayName("Should read and parse employees from file")
        void shouldReadEmployeesFromFile() throws IOException {
            List<Exercise1.Employee> employees = Exercise1.readEmployeesFromFile(testFile.toString());

            assertEquals(5, employees.size());

            // Check first employee
            Exercise1.Employee first = employees.get(0);
            assertEquals(1, first.getId());
            assertEquals("John Doe", first.getName());

            // Check last employee
            Exercise1.Employee last = employees.get(4);
            assertEquals(5, last.getId());
            assertEquals("Charlie Davis", last.getName());
        }

        @Test
        @DisplayName("Should handle empty file gracefully")
        void shouldHandleEmptyFile() throws IOException {
            List<Exercise1.Employee> employees = Exercise1.readEmployeesFromFile(emptyFile.toString());

            assertTrue(employees.isEmpty());
        }

        @Test
        @DisplayName("Should skip malformed lines and parse valid ones")
        void shouldSkipMalformedLines() throws IOException {
            List<Exercise1.Employee> employees = Exercise1.readEmployeesFromFile(malformedFile.toString());

            assertEquals(2, employees.size());
            assertEquals(1, employees.get(0).getId());
            assertEquals("John Doe", employees.get(0).getName());
            assertEquals(3, employees.get(1).getId());
            assertEquals("Bob Johnson", employees.get(1).getName());
        }

        @Test
        @DisplayName("Should throw IOException for non-existent file")
        void shouldThrowIOExceptionForNonExistentFile() {
            assertThrows(IOException.class, () -> {
                Exercise1.readEmployeesFromFile("non-existent-file.txt");
            });
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
            List<Exercise1.Employee> filtered = Exercise1.filterEmployeesByIdRange(sampleEmployees, 2, 4);

            assertEquals(3, filtered.size());
            assertEquals(2, filtered.get(0).getId());
            assertEquals(3, filtered.get(1).getId());
            assertEquals(4, filtered.get(2).getId());
        }

        @Test
        @DisplayName("Should map employee names to uppercase")
        void shouldMapEmployeeNamesToUppercase() {
            List<String> upperNames = Exercise1.mapEmployeeNames(sampleEmployees, String::toUpperCase);

            assertEquals(5, upperNames.size());
            assertEquals("JOHN DOE", upperNames.get(0));
            assertEquals("JANE SMITH", upperNames.get(1));
            assertEquals("BOB JOHNSON", upperNames.get(2));
        }

        @Test
        @DisplayName("Should find employee by ID")
        void shouldFindEmployeeById() {
            Optional<Exercise1.Employee> found = Exercise1.findEmployeeById(sampleEmployees, 3);

            assertTrue(found.isPresent());
            assertEquals(3, found.get().getId());
            assertEquals("Bob Johnson", found.get().getName());
        }

        @Test
        @DisplayName("Should return empty when employee not found")
        void shouldReturnEmptyWhenEmployeeNotFound() {
            Optional<Exercise1.Employee> found = Exercise1.findEmployeeById(sampleEmployees, 999);

            assertFalse(found.isPresent());
        }

        @Test
        @DisplayName("Should count employees with name containing substring")
        void shouldCountEmployeesWithNameContaining() {
            long count = Exercise1.countEmployeesWithNameContaining(sampleEmployees, "Jo");

            assertEquals(2, count); // John Doe and Bob Johnson
        }
    }

    @Nested
    @DisplayName("IO Monadic Operations")
    class IOMonadicOperations {

        @Test
        @DisplayName("Should compose IO operations functionally")
        void shouldComposeIOOperations() throws IOException {
            // Test composition of reading file and processing data
            Function<String, List<Exercise1.Employee>> readAndFilter =
                filename -> {
                    try {
                        List<Exercise1.Employee> employees = Exercise1.readEmployeesFromFile(filename);
                        return Exercise1.filterEmployeesByIdRange(employees, 1, 3);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                };

            List<Exercise1.Employee> result = readAndFilter.apply(testFile.toString());

            assertEquals(3, result.size());
            assertEquals("John Doe", result.get(0).getName());
            assertEquals("Jane Smith", result.get(1).getName());
            assertEquals("Bob Johnson", result.get(2).getName());
        }

        @Test
        @DisplayName("Should handle IO operations with error recovery")
        void shouldHandleIOOperationsWithErrorRecovery() {
            List<Exercise1.Employee> result = Exercise1.readEmployeesWithFallback(
                "non-existent-file.txt",
                testFile.toString()
            );

            assertEquals(5, result.size()); // Should fall back to test file
        }
    }

    @Nested
    @DisplayName("Employee Statistics")
    class EmployeeStatistics {

        @Test
        @DisplayName("Should calculate employee statistics from file")
        void shouldCalculateEmployeeStatistics() throws IOException {
            Exercise1.EmployeeStats stats = Exercise1.calculateEmployeeStats(testFile.toString());

            assertEquals(5, stats.getTotalCount());
            assertEquals(1, stats.getMinId());
            assertEquals(5, stats.getMaxId());
            assertEquals(3.0, stats.getAverageId(), 0.01);
            assertTrue(stats.getEmployeeNames().contains("John Doe"));
            assertTrue(stats.getEmployeeNames().contains("Charlie Davis"));
        }

        @Test
        @DisplayName("Should handle empty file for statistics")
        void shouldHandleEmptyFileForStatistics() throws IOException {
            Exercise1.EmployeeStats stats = Exercise1.calculateEmployeeStats(emptyFile.toString());

            assertEquals(0, stats.getTotalCount());
            assertEquals(0, stats.getMinId());
            assertEquals(0, stats.getMaxId());
            assertEquals(0.0, stats.getAverageId());
            assertTrue(stats.getEmployeeNames().isEmpty());
        }
    }
}
