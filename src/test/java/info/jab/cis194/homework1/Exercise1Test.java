package info.jab.cis194.homework1;

import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Exercise 1 - Credit Card Validation
 * Based on CIS-194 Homework 1
 */
public class Exercise1Test {

    @Test
    public void testToDigits() {
        Exercise1 exercise = new Exercise1();

        // Test positive numbers
        assertEquals(List.of(1, 2, 3, 4), exercise.toDigits(1234));
        assertEquals(List.of(0), exercise.toDigits(0));
        assertEquals(List.of(5), exercise.toDigits(5));

        // Test negative numbers should return empty list
        assertEquals(List.of(), exercise.toDigits(-17));
        assertEquals(List.of(), exercise.toDigits(-1));
    }

    @Test
    public void testToDigitsRev() {
        Exercise1 exercise = new Exercise1();

        // Test reverse order
        assertEquals(List.of(4, 3, 2, 1), exercise.toDigitsRev(1234));
        assertEquals(List.of(0), exercise.toDigitsRev(0));
        assertEquals(List.of(5), exercise.toDigitsRev(5));

        // Test negative numbers should return empty list
        assertEquals(List.of(), exercise.toDigitsRev(-17));
    }

    @Test
    public void testDoubleEveryOther() {
        Exercise1 exercise = new Exercise1();

        // Test doubling every other element from the right
        // The algorithm works on digits in reverse order (from toDigitsRev)
        assertEquals(List.of(1, 4, 3, 8), exercise.doubleEveryOther(List.of(1, 2, 3, 4)));
        assertEquals(List.of(2), exercise.doubleEveryOther(List.of(2)));
        assertEquals(List.of(1, 4), exercise.doubleEveryOther(List.of(1, 2)));
        assertEquals(List.of(2, 2, 6), exercise.doubleEveryOther(List.of(1, 2, 3)));
    }

    @Test
    public void testSumDigits() {
        Exercise1 exercise = new Exercise1();

        // Test summing all digits
        // 16 -> 1+6=7, 7 -> 7, 12 -> 1+2=3, 5 -> 5, total = 7+7+3+5 = 22
        assertEquals(22, exercise.sumDigits(List.of(16, 7, 12, 5)));
        assertEquals(18, exercise.sumDigits(List.of(1, 5, 8, 4)));
        assertEquals(0, exercise.sumDigits(List.of()));
        assertEquals(5, exercise.sumDigits(List.of(5)));
    }

    @Test
    public void testValidate() {
        Exercise1 exercise = new Exercise1();

        // Test valid credit card numbers
        assertTrue(exercise.validate(4012888888881881L));
        assertTrue(exercise.validate(4111111111111111L));

        // Test invalid credit card numbers
        assertFalse(exercise.validate(4012888888881882L));
        assertFalse(exercise.validate(1234567890123456L));
    }
}
