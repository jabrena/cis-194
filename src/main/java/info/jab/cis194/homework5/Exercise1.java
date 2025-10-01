package info.jab.cis194.homework5;

import java.math.BigInteger;
import java.util.Objects;
import java.util.function.Function;

/**
 * Exercise 1: Type Classes and Instances - Expression Calculator
 *
 * This exercise demonstrates type classes and polymorphism by implementing
 * an expression language that can be evaluated in different contexts.
 *
 * The expression language supports:
 * - Literal values
 * - Addition
 * - Multiplication
 *
 * And can be evaluated as:
 * - Integers
 * - BigIntegers
 * - Booleans (with XOR as addition and AND as multiplication)
 * - String representations
 */
public class Exercise1 {

    /**
     * Expression interface representing the abstract syntax tree
     * Sealed to restrict implementations to LitExpr, AddExpr, and MulExpr
     */
    public sealed interface Expr permits LitExpr, AddExpr, MulExpr {
        <T> T accept(ExprVisitor<T> visitor);
    }

    /**
     * Visitor pattern interface for type-safe expression evaluation
     */
    public interface ExprVisitor<T> {
        T visitLit(int value);
        T visitAdd(Expr left, Expr right);
        T visitMul(Expr left, Expr right);
    }

    /**
     * Literal expression implementation
     */
    public static final class LitExpr implements Expr {
        private final int value;

        public LitExpr(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        @Override
        public <T> T accept(ExprVisitor<T> visitor) {
            return visitor.visitLit(value);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            LitExpr litExpr = (LitExpr) obj;
            return value == litExpr.value;
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public String toString() {
            return "Lit(" + value + ")";
        }
    }

    /**
     * Addition expression implementation
     */
    public static final class AddExpr implements Expr {
        private final Expr left;
        private final Expr right;

        public AddExpr(Expr left, Expr right) {
            this.left = Objects.requireNonNull(left, "Left expression cannot be null");
            this.right = Objects.requireNonNull(right, "Right expression cannot be null");
        }

        public Expr getLeft() {
            return left;
        }

        public Expr getRight() {
            return right;
        }

        @Override
        public <T> T accept(ExprVisitor<T> visitor) {
            return visitor.visitAdd(left, right);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            AddExpr addExpr = (AddExpr) obj;
            return Objects.equals(left, addExpr.left) && Objects.equals(right, addExpr.right);
        }

        @Override
        public int hashCode() {
            return Objects.hash(left, right);
        }

        @Override
        public String toString() {
            return "Add(" + left + ", " + right + ")";
        }
    }

    /**
     * Multiplication expression implementation
     */
    public static final class MulExpr implements Expr {
        private final Expr left;
        private final Expr right;

        public MulExpr(Expr left, Expr right) {
            this.left = Objects.requireNonNull(left, "Left expression cannot be null");
            this.right = Objects.requireNonNull(right, "Right expression cannot be null");
        }

        public Expr getLeft() {
            return left;
        }

        public Expr getRight() {
            return right;
        }

        @Override
        public <T> T accept(ExprVisitor<T> visitor) {
            return visitor.visitMul(left, right);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            MulExpr mulExpr = (MulExpr) obj;
            return Objects.equals(left, mulExpr.left) && Objects.equals(right, mulExpr.right);
        }

        @Override
        public int hashCode() {
            return Objects.hash(left, right);
        }

        @Override
        public String toString() {
            return "Mul(" + left + ", " + right + ")";
        }
    }

    // Expression constructors (smart constructors)

    /**
     * Creates a literal expression
     * @param value the integer value
     * @return a literal expression
     */
    public static Expr lit(int value) {
        return new LitExpr(value);
    }

    /**
     * Creates an addition expression
     * @param left the left operand
     * @param right the right operand
     * @return an addition expression
     * @throws IllegalArgumentException if either operand is null
     */
    public static Expr add(Expr left, Expr right) {
        return new AddExpr(left, right);
    }

    /**
     * Creates a multiplication expression
     * @param left the left operand
     * @param right the right operand
     * @return a multiplication expression
     * @throws IllegalArgumentException if either operand is null
     */
    public static Expr mul(Expr left, Expr right) {
        return new MulExpr(left, right);
    }

    // Type class instances for evaluation

    /**
     * Integer evaluator - implements arithmetic operations
     */
    private static final ExprVisitor<Integer> INTEGER_EVALUATOR = new ExprVisitor<Integer>() {
        @Override
        public Integer visitLit(int value) {
            return value;
        }

        @Override
        public Integer visitAdd(Expr left, Expr right) {
            return left.accept(this) + right.accept(this);
        }

        @Override
        public Integer visitMul(Expr left, Expr right) {
            return left.accept(this) * right.accept(this);
        }
    };

    /**
     * BigInteger evaluator - implements arithmetic operations for big integers
     */
    private static final ExprVisitor<BigInteger> BIGINTEGER_EVALUATOR = new ExprVisitor<BigInteger>() {
        @Override
        public BigInteger visitLit(int value) {
            return BigInteger.valueOf(value);
        }

        @Override
        public BigInteger visitAdd(Expr left, Expr right) {
            return left.accept(this).add(right.accept(this));
        }

        @Override
        public BigInteger visitMul(Expr left, Expr right) {
            return left.accept(this).multiply(right.accept(this));
        }
    };

    /**
     * Boolean evaluator - implements boolean algebra
     * Addition = XOR, Multiplication = AND
     * Non-zero values are treated as true, zero as false
     */
    private static final ExprVisitor<Boolean> BOOLEAN_EVALUATOR = new ExprVisitor<Boolean>() {
        @Override
        public Boolean visitLit(int value) {
            return value != 0;
        }

        @Override
        public Boolean visitAdd(Expr left, Expr right) {
            // Addition as XOR
            return left.accept(this) ^ right.accept(this);
        }

        @Override
        public Boolean visitMul(Expr left, Expr right) {
            // Multiplication as AND
            return left.accept(this) && right.accept(this);
        }
    };

    /**
     * String formatter - creates string representations with parentheses
     */
    private static final ExprVisitor<String> STRING_FORMATTER = new ExprVisitor<String>() {
        @Override
        public String visitLit(int value) {
            return String.valueOf(value);
        }

        @Override
        public String visitAdd(Expr left, Expr right) {
            return "(" + left.accept(this) + " + " + right.accept(this) + ")";
        }

        @Override
        public String visitMul(Expr left, Expr right) {
            return "(" + left.accept(this) + " * " + right.accept(this) + ")";
        }
    };

    // Public evaluation methods

    /**
     * Evaluates an expression as an integer
     * @param expr the expression to evaluate
     * @return the integer result
     * @throws IllegalArgumentException if expr is null
     */
    public static Integer eval(Expr expr) {
        if (expr == null) {
            throw new IllegalArgumentException("Expression cannot be null");
        }
        return expr.accept(INTEGER_EVALUATOR);
    }

    /**
     * Evaluates an expression as a BigInteger
     * @param expr the expression to evaluate
     * @return the BigInteger result
     * @throws IllegalArgumentException if expr is null
     */
    public static BigInteger evalBigInteger(Expr expr) {
        if (expr == null) {
            throw new IllegalArgumentException("Expression cannot be null");
        }
        return expr.accept(BIGINTEGER_EVALUATOR);
    }

    /**
     * Evaluates an expression as a Boolean using boolean algebra
     * @param expr the expression to evaluate
     * @return the boolean result
     * @throws IllegalArgumentException if expr is null
     */
    public static Boolean evalBoolean(Expr expr) {
        if (expr == null) {
            throw new IllegalArgumentException("Expression cannot be null");
        }
        return expr.accept(BOOLEAN_EVALUATOR);
    }

    /**
     * Formats an expression as a string with parentheses
     * @param expr the expression to format
     * @return the formatted string
     * @throws IllegalArgumentException if expr is null
     */
    public static String format(Expr expr) {
        if (expr == null) {
            throw new IllegalArgumentException("Expression cannot be null");
        }
        return expr.accept(STRING_FORMATTER);
    }

    // Utility methods for demonstrating polymorphism

    /**
     * Generic evaluation method that can work with any evaluator
     * @param <T> the result type
     * @param expr the expression to evaluate
     * @param evaluator the evaluator to use
     * @return the evaluation result
     * @throws IllegalArgumentException if expr or evaluator is null
     */
    public static <T> T evaluate(Expr expr, ExprVisitor<T> evaluator) {
        if (expr == null) {
            throw new IllegalArgumentException("Expression cannot be null");
        }
        if (evaluator == null) {
            throw new IllegalArgumentException("Evaluator cannot be null");
        }
        return expr.accept(evaluator);
    }

    /**
     * Creates a custom evaluator that applies a transformation to integer results
     * @param <T> the target type
     * @param transformer the transformation function
     * @return a new evaluator
     * @throws IllegalArgumentException if transformer is null
     */
    public static <T> ExprVisitor<T> createCustomEvaluator(Function<Integer, T> transformer) {
        if (transformer == null) {
            throw new IllegalArgumentException("Transformer cannot be null");
        }

        return new ExprVisitor<T>() {
            @Override
            public T visitLit(int value) {
                return transformer.apply(value);
            }

            @Override
            public T visitAdd(Expr left, Expr right) {
                // First evaluate as integers, then transform
                Integer leftResult = left.accept(INTEGER_EVALUATOR);
                Integer rightResult = right.accept(INTEGER_EVALUATOR);
                return transformer.apply(leftResult + rightResult);
            }

            @Override
            public T visitMul(Expr left, Expr right) {
                // First evaluate as integers, then transform
                Integer leftResult = left.accept(INTEGER_EVALUATOR);
                Integer rightResult = right.accept(INTEGER_EVALUATOR);
                return transformer.apply(leftResult * rightResult);
            }
        };
    }

    // Example of type class constraints and higher-order functions

    /**
     * Applies a binary operation to two expressions and returns a new expression
     * @param op the binary operation constructor
     * @param left the left expression
     * @param right the right expression
     * @return a new expression
     * @throws IllegalArgumentException if any parameter is null
     */
    public static Expr applyBinaryOp(BinaryOpConstructor op, Expr left, Expr right) {
        if (op == null) {
            throw new IllegalArgumentException("Operation cannot be null");
        }
        return op.apply(left, right);
    }

    /**
     * Functional interface for binary operation constructors
     */
    @FunctionalInterface
    public interface BinaryOpConstructor {
        Expr apply(Expr left, Expr right);
    }

    /**
     * Predefined binary operation constructors
     */
    public static final BinaryOpConstructor ADD_OP = Exercise1::add;
    public static final BinaryOpConstructor MUL_OP = Exercise1::mul;

    /**
     * Demonstrates function composition with expressions
     * @param expr the base expression
     * @param ops the operations to apply in sequence
     * @return the composed expression
     * @throws IllegalArgumentException if expr is null or ops contains null
     */
    @SafeVarargs
    public static Expr compose(Expr expr, Function<Expr, Expr>... ops) {
        if (expr == null) {
            throw new IllegalArgumentException("Expression cannot be null");
        }

        Expr result = expr;
        for (Function<Expr, Expr> op : ops) {
            if (op == null) {
                throw new IllegalArgumentException("Operation cannot be null");
            }
            result = op.apply(result);
        }
        return result;
    }
}
