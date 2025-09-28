package info.jab.cis194.homework7;

import java.util.ArrayList;
import java.util.List;

/**
 * Exercise 2: Monoids and Monoid Laws
 *
 * A monoid is an algebraic structure consisting of:
 * 1. A set of elements
 * 2. An associative binary operation (mappend)
 * 3. An identity element (mempty)
 *
 * Monoid laws:
 * 1. Associativity: (a <> b) <> c = a <> (b <> c)
 * 2. Left identity: mempty <> a = a
 * 3. Right identity: a <> mempty = a
 */
public class Exercise2 {

    /**
     * Monoid interface defining the operations every monoid must have
     */
    public interface Monoid<T> {
        /**
         * The identity element of the monoid
         */
        T mempty();

        /**
         * The associative binary operation
         */
        T mappend(T a, T b);
    }

    /**
     * Sum monoid for integers - addition with 0 as identity
     */
    public static class SumMonoid implements Monoid<Integer> {
        @Override
        public Integer mempty() {
            return 0;
        }

        @Override
        public Integer mappend(Integer a, Integer b) {
            return a + b;
        }
    }

    /**
     * Product monoid for integers - multiplication with 1 as identity
     */
    public static class ProductMonoid implements Monoid<Integer> {
        @Override
        public Integer mempty() {
            return 1;
        }

        @Override
        public Integer mappend(Integer a, Integer b) {
            return a * b;
        }
    }

    /**
     * Boolean AND monoid - logical AND with true as identity
     */
    public static class BooleanAndMonoid implements Monoid<Boolean> {
        @Override
        public Boolean mempty() {
            return true;
        }

        @Override
        public Boolean mappend(Boolean a, Boolean b) {
            return a && b;
        }
    }

    /**
     * Boolean OR monoid - logical OR with false as identity
     */
    public static class BooleanOrMonoid implements Monoid<Boolean> {
        @Override
        public Boolean mempty() {
            return false;
        }

        @Override
        public Boolean mappend(Boolean a, Boolean b) {
            return a || b;
        }
    }

    /**
     * List monoid - list concatenation with empty list as identity
     */
    public static class ListMonoid<T> implements Monoid<List<T>> {
        @Override
        public List<T> mempty() {
            return new ArrayList<>();
        }

        @Override
        public List<T> mappend(List<T> a, List<T> b) {
            List<T> result = new ArrayList<>(a);
            result.addAll(b);
            return result;
        }
    }

    /**
     * Concatenate a list of monoid values using the monoid operation
     * This is the generalized fold operation for monoids
     */
    public static <T> T mconcat(Monoid<T> monoid, List<T> values) {
        T result = monoid.mempty();
        for (T value : values) {
            result = monoid.mappend(result, value);
        }
        return result;
    }

    /**
     * Alternative implementation of mconcat using reduce pattern
     */
    public static <T> T mconcatReduce(Monoid<T> monoid, List<T> values) {
        return values.stream()
                    .reduce(monoid.mempty(), monoid::mappend);
    }

    /**
     * Fold a list using a monoid - same as mconcat but more explicit name
     */
    public static <T> T foldMonoid(Monoid<T> monoid, List<T> values) {
        return mconcat(monoid, values);
    }
}
