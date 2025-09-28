package info.jab.cis194.homework5;

import info.jab.cis194.homework5.Exercise1.Expr;
import java.math.BigInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Exercise 1: Type Classes and Instances - Expr Calculator")
class Exercise1Test {

    @Nested
    @DisplayName("Literal Expression Tests")
    class LiteralExpressionTests {

        @Test
        @DisplayName("Should evaluate integer literals correctly")
        void shouldEvaluateIntegerLiteralsCorrectly() {
            // Given
            Expr expr = Exercise1.lit(42);

            // When
            Integer result = Exercise1.eval(expr);

            // Then
            assertThat(result).isEqualTo(42);
        }

        @Test
        @DisplayName("Should evaluate negative integer literals correctly")
        void shouldEvaluateNegativeIntegerLiteralsCorrectly() {
            // Given
            Expr expr = Exercise1.lit(-15);

            // When
            Integer result = Exercise1.eval(expr);

            // Then
            assertThat(result).isEqualTo(-15);
        }

        @Test
        @DisplayName("Should evaluate zero literal correctly")
        void shouldEvaluateZeroLiteralCorrectly() {
            // Given
            Expr expr = Exercise1.lit(0);

            // When
            Integer result = Exercise1.eval(expr);

            // Then
            assertThat(result).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("Addition Expression Tests")
    class AdditionExpressionTests {

        @Test
        @DisplayName("Should evaluate simple addition correctly")
        void shouldEvaluateSimpleAdditionCorrectly() {
            // Given
            Expr expr = Exercise1.add(Exercise1.lit(3), Exercise1.lit(5));

            // When
            Integer result = Exercise1.eval(expr);

            // Then
            assertThat(result).isEqualTo(8);
        }

        @Test
        @DisplayName("Should evaluate addition with negative numbers correctly")
        void shouldEvaluateAdditionWithNegativeNumbersCorrectly() {
            // Given
            Expr expr = Exercise1.add(Exercise1.lit(-10), Exercise1.lit(7));

            // When
            Integer result = Exercise1.eval(expr);

            // Then
            assertThat(result).isEqualTo(-3);
        }

        @Test
        @DisplayName("Should evaluate addition with zero correctly")
        void shouldEvaluateAdditionWithZeroCorrectly() {
            // Given
            Expr expr = Exercise1.add(Exercise1.lit(0), Exercise1.lit(42));

            // When
            Integer result = Exercise1.eval(expr);

            // Then
            assertThat(result).isEqualTo(42);
        }
    }

    @Nested
    @DisplayName("Multiplication Expression Tests")
    class MultiplicationExpressionTests {

        @Test
        @DisplayName("Should evaluate simple multiplication correctly")
        void shouldEvaluateSimpleMultiplicationCorrectly() {
            // Given
            Expr expr = Exercise1.mul(Exercise1.lit(4), Exercise1.lit(6));

            // When
            Integer result = Exercise1.eval(expr);

            // Then
            assertThat(result).isEqualTo(24);
        }

        @Test
        @DisplayName("Should evaluate multiplication with negative numbers correctly")
        void shouldEvaluateMultiplicationWithNegativeNumbersCorrectly() {
            // Given
            Expr expr = Exercise1.mul(Exercise1.lit(-3), Exercise1.lit(4));

            // When
            Integer result = Exercise1.eval(expr);

            // Then
            assertThat(result).isEqualTo(-12);
        }

        @Test
        @DisplayName("Should evaluate multiplication with zero correctly")
        void shouldEvaluateMultiplicationWithZeroCorrectly() {
            // Given
            Expr expr = Exercise1.mul(Exercise1.lit(0), Exercise1.lit(42));

            // When
            Integer result = Exercise1.eval(expr);

            // Then
            assertThat(result).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("Complex Expression Tests")
    class ComplexExpressionTests {

        @Test
        @DisplayName("Should evaluate nested expressions correctly")
        void shouldEvaluateNestedExpressionsCorrectly() {
            // Given: (2 + 3) * 4 = 20
            Expr expr = Exercise1.mul(
                Exercise1.add(Exercise1.lit(2), Exercise1.lit(3)),
                Exercise1.lit(4)
            );

            // When
            Integer result = Exercise1.eval(expr);

            // Then
            assertThat(result).isEqualTo(20);
        }

        @Test
        @DisplayName("Should evaluate deeply nested expressions correctly")
        void shouldEvaluateDeeplyNestedExpressionsCorrectly() {
            // Given: ((1 + 2) * 3) + ((4 * 5) + 6) = 9 + 26 = 35
            Expr expr = Exercise1.add(
                Exercise1.mul(
                    Exercise1.add(Exercise1.lit(1), Exercise1.lit(2)),
                    Exercise1.lit(3)
                ),
                Exercise1.add(
                    Exercise1.mul(Exercise1.lit(4), Exercise1.lit(5)),
                    Exercise1.lit(6)
                )
            );

            // When
            Integer result = Exercise1.eval(expr);

            // Then
            assertThat(result).isEqualTo(35);
        }

        @Test
        @DisplayName("Should evaluate complex expression with mixed operations")
        void shouldEvaluateComplexExpressionWithMixedOperations() {
            // Given: (5 * (2 + 3)) + (10 * (4 + 1)) = 25 + 50 = 75
            Expr expr = Exercise1.add(
                Exercise1.mul(
                    Exercise1.lit(5),
                    Exercise1.add(Exercise1.lit(2), Exercise1.lit(3))
                ),
                Exercise1.mul(
                    Exercise1.lit(10),
                    Exercise1.add(Exercise1.lit(4), Exercise1.lit(1))
                )
            );

            // When
            Integer result = Exercise1.eval(expr);

            // Then
            assertThat(result).isEqualTo(75);
        }
    }

    @Nested
    @DisplayName("String Representation Tests")
    class StringRepresentationTests {

        @Test
        @DisplayName("Should format literal expressions correctly")
        void shouldFormatLiteralExpressionsCorrectly() {
            // Given
            Expr expr = Exercise1.lit(42);

            // When
            String result = Exercise1.format(expr);

            // Then
            assertThat(result).isEqualTo("42");
        }

        @Test
        @DisplayName("Should format addition expressions correctly")
        void shouldFormatAdditionExpressionsCorrectly() {
            // Given
            Expr expr = Exercise1.add(Exercise1.lit(2), Exercise1.lit(3));

            // When
            String result = Exercise1.format(expr);

            // Then
            assertThat(result).isEqualTo("(2 + 3)");
        }

        @Test
        @DisplayName("Should format multiplication expressions correctly")
        void shouldFormatMultiplicationExpressionsCorrectly() {
            // Given
            Expr expr = Exercise1.mul(Exercise1.lit(4), Exercise1.lit(5));

            // When
            String result = Exercise1.format(expr);

            // Then
            assertThat(result).isEqualTo("(4 * 5)");
        }

        @Test
        @DisplayName("Should format complex expressions correctly")
        void shouldFormatComplexExpressionsCorrectly() {
            // Given: (2 + 3) * 4
            Expr expr = Exercise1.mul(
                Exercise1.add(Exercise1.lit(2), Exercise1.lit(3)),
                Exercise1.lit(4)
            );

            // When
            String result = Exercise1.format(expr);

            // Then
            assertThat(result).isEqualTo("((2 + 3) * 4)");
        }
    }

    @Nested
    @DisplayName("Type Class Instance Tests")
    class TypeClassInstanceTests {

        @Test
        @DisplayName("Should work with different numeric types - BigInteger")
        void shouldWorkWithDifferentNumericTypesBigInteger() {
            // Given
            Expr expr = Exercise1.add(Exercise1.lit(5), Exercise1.lit(10));

            // When
            BigInteger result = Exercise1.evalBigInteger(expr);

            // Then
            assertThat(result).isEqualTo(BigInteger.valueOf(15));
        }

        @Test
        @DisplayName("Should work with different numeric types - Boolean (XOR)")
        void shouldWorkWithDifferentNumericTypesBooleanXOR() {
            // Given
            Expr expr = Exercise1.add(Exercise1.lit(1), Exercise1.lit(1)); // true XOR true = false

            // When
            Boolean result = Exercise1.evalBoolean(expr);

            // Then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Should work with Boolean AND operations")
        void shouldWorkWithBooleanANDOperations() {
            // Given
            Expr expr = Exercise1.mul(Exercise1.lit(1), Exercise1.lit(1)); // true AND true = true

            // When
            Boolean result = Exercise1.evalBoolean(expr);

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Should work with Boolean mixed operations")
        void shouldWorkWithBooleanMixedOperations() {
            // Given: (true XOR false) AND (true XOR true) = true AND false = false
            Expr expr = Exercise1.mul(
                Exercise1.add(Exercise1.lit(1), Exercise1.lit(0)), // true XOR false = true
                Exercise1.add(Exercise1.lit(1), Exercise1.lit(1))  // true XOR true = false
            );

            // When
            Boolean result = Exercise1.evalBoolean(expr);

            // Then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should handle null expressions gracefully")
        void shouldHandleNullExpressionsGracefully() {
            // Given
            Expr nullExpr = null;

            // When
            var throwableAssert = assertThatThrownBy(() -> Exercise1.eval(nullExpr));

            // Then
            throwableAssert
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Expression cannot be null");
        }

        @Test
        @DisplayName("Should handle null in format gracefully")
        void shouldHandleNullInFormatGracefully() {
            // Given
            Expr nullExpr = null;

            // When
            var throwableAssert = assertThatThrownBy(() -> Exercise1.format(nullExpr));

            // Then
            throwableAssert
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Expression cannot be null");
        }
    }
}
