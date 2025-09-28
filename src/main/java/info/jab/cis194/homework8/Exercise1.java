package info.jab.cis194.homework8;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Exercise 1: IO and File Processing
 *
 * <p>This exercise demonstrates IO operations, file processing, and functional programming
 * concepts applied to reading and parsing data from files.
 *
 * <p>Based on CIS-194 Week 8: IO and file operations
 */
public class Exercise1 {

    // Regular expression to parse employee lines: Emp <id> "<name>"
    private static final Pattern EMPLOYEE_PATTERN = Pattern.compile("^Emp\\s+(\\d+)\\s+\"([^\"]+)\"$");

    /**
     * Employee data class representing an employee with ID and name
     */
    public static class Employee {
        private final int id;
        private final String name;

        public Employee(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Employee employee = (Employee) obj;
            return id == employee.id && name.equals(employee.name);
        }

        @Override
        public int hashCode() {
            return 31 * id + name.hashCode();
        }

        @Override
        public String toString() {
            return "Employee{id=" + id + ", name='" + name + "'}";
        }
    }

    /**
     * Employee statistics record for aggregating employee data
     * Immutable data class following functional programming principles
     */
    public record EmployeeStats(
            int totalCount,
            int minId,
            int maxId,
            double averageId,
            List<String> employeeNames
    ) {
        public EmployeeStats {
            // Defensive copy to ensure immutability
            employeeNames = List.copyOf(employeeNames);
        }

        /**
         * Creates empty statistics for when no employees are found
         */
        public static EmployeeStats empty() {
            return new EmployeeStats(0, 0, 0, 0.0, List.of());
        }

        /**
         * Creates statistics from a list of employees using functional approach
         */
        public static EmployeeStats fromEmployees(List<Employee> employees) {
            if (employees.isEmpty()) {
                return empty();
            }

            var names = employees.stream().map(Employee::getName).toList();
            var minId = employees.stream().mapToInt(Employee::getId).min().orElse(0);
            var maxId = employees.stream().mapToInt(Employee::getId).max().orElse(0);
            var averageId = employees.stream().mapToInt(Employee::getId).average().orElse(0.0);

            return new EmployeeStats(
                    employees.size(),
                    minId,
                    maxId,
                    averageId,
                    names
            );
        }
    }

    /**
     * Parses a single employee line from the format: Emp <id> "<name>"
     *
     * @param line the line to parse
     * @return Optional containing the parsed Employee, or empty if parsing fails
     */
    public static Optional<Employee> parseEmployee(String line) {
        if (line == null || line.trim().isEmpty()) {
            return Optional.empty();
        }

        Matcher matcher = EMPLOYEE_PATTERN.matcher(line.trim());
        if (!matcher.matches()) {
            return Optional.empty();
        }

        try {
            int id = Integer.parseInt(matcher.group(1));
            String name = matcher.group(2);
            return Optional.of(new Employee(id, name));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /**
     * Reads employees from a file, parsing each line
     *
     * @param filename the path to the file to read
     * @return List of successfully parsed employees
     * @throws IOException if file cannot be read
     */
    public static List<Employee> readEmployeesFromFile(String filename) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filename));

        return lines.stream()
                .map(Exercise1::parseEmployee)
                .flatMap(Optional::stream)
                .toList();
    }

    /**
     * Filters employees by ID range (inclusive)
     *
     * @param employees the list of employees to filter
     * @param minId minimum ID (inclusive)
     * @param maxId maximum ID (inclusive)
     * @return filtered list of employees
     */
    public static List<Employee> filterEmployeesByIdRange(List<Employee> employees, int minId, int maxId) {
        return employees.stream()
                .filter(emp -> emp.getId() >= minId && emp.getId() <= maxId)
                .toList();
    }

    /**
     * Maps employee names using the provided function
     *
     * @param employees the list of employees
     * @param mapper function to transform employee names
     * @return list of transformed names
     */
    public static List<String> mapEmployeeNames(List<Employee> employees, Function<String, String> mapper) {
        return employees.stream()
                .map(Employee::getName)
                .map(mapper)
                .toList();
    }

    /**
     * Finds an employee by ID
     *
     * @param employees the list of employees to search
     * @param id the ID to search for
     * @return Optional containing the employee if found, empty otherwise
     */
    public static Optional<Employee> findEmployeeById(List<Employee> employees, int id) {
        return employees.stream()
                .filter(emp -> emp.getId() == id)
                .findFirst();
    }

    /**
     * Counts employees whose names contain the given substring (case-sensitive)
     *
     * @param employees the list of employees
     * @param substring the substring to search for
     * @return count of employees with names containing the substring
     */
    public static long countEmployeesWithNameContaining(List<Employee> employees, String substring) {
        return employees.stream()
                .map(Employee::getName)
                .filter(name -> name.contains(substring))
                .count();
    }

    /**
     * Reads employees from a file with fallback to another file if the first fails
     * Uses functional approach with supplier chain for error recovery
     *
     * @param primaryFile the primary file to try reading
     * @param fallbackFile the fallback file if primary fails
     * @return list of employees from successful file read
     */
    public static List<Employee> readEmployeesWithFallback(String primaryFile, String fallbackFile) {
        return tryReadFile(primaryFile)
                .or(() -> tryReadFile(fallbackFile))
                .orElse(List.of());
    }

    /**
     * Attempts to read employees from a file, returning Optional for functional composition
     *
     * @param filename the file to read
     * @return Optional containing employees list, or empty if reading fails
     */
    private static Optional<List<Employee>> tryReadFile(String filename) {
        try {
            return Optional.of(readEmployeesFromFile(filename));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    /**
     * Calculates comprehensive statistics for employees from a file using functional approach
     *
     * @param filename the file to read employees from
     * @return EmployeeStats containing various statistics
     * @throws IOException if file cannot be read
     */
    public static EmployeeStats calculateEmployeeStats(String filename) throws IOException {
        return EmployeeStats.fromEmployees(readEmployeesFromFile(filename));
    }
}
