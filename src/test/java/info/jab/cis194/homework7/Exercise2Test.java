package info.jab.cis194.homework7;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Exercise 2: Monoids and Monoid Laws
 *
 * This exercise focuses on implementing monoids and verifying that they
 * satisfy the monoid laws: associativity and identity.
 * A monoid is an algebraic structure with an associative binary operation
 * and an identity element.
 */
public class Exercise2Test {

    @Test
    public void testSumMonoidIdentity() {
        Exercise2.SumMonoid sumMonoid = new Exercise2.SumMonoid();

        // Test left identity: mempty <> x = x
        assertEquals(Integer.valueOf(42), sumMonoid.mappend(sumMonoid.mempty(), 42));

        // Test right identity: x <> mempty = x
        assertEquals(Integer.valueOf(42), sumMonoid.mappend(42, sumMonoid.mempty()));
    }

    @Test
    public void testSumMonoidAssociativity() {
        Exercise2.SumMonoid sumMonoid = new Exercise2.SumMonoid();

        // Test associativity: (x <> y) <> z = x <> (y <> z)
        Integer x = 10, y = 20, z = 30;

        Integer leftAssoc = sumMonoid.mappend(sumMonoid.mappend(x, y), z);
        Integer rightAssoc = sumMonoid.mappend(x, sumMonoid.mappend(y, z));

        assertEquals(leftAssoc, rightAssoc);
        assertEquals(Integer.valueOf(60), leftAssoc);
    }

    @Test
    public void testProductMonoidIdentity() {
        Exercise2.ProductMonoid productMonoid = new Exercise2.ProductMonoid();

        // Test left identity: mempty <> x = x
        assertEquals(Integer.valueOf(42), productMonoid.mappend(productMonoid.mempty(), 42));

        // Test right identity: x <> mempty = x
        assertEquals(Integer.valueOf(42), productMonoid.mappend(42, productMonoid.mempty()));
    }

    @Test
    public void testProductMonoidAssociativity() {
        Exercise2.ProductMonoid productMonoid = new Exercise2.ProductMonoid();

        // Test associativity: (x <> y) <> z = x <> (y <> z)
        Integer x = 2, y = 3, z = 4;

        Integer leftAssoc = productMonoid.mappend(productMonoid.mappend(x, y), z);
        Integer rightAssoc = productMonoid.mappend(x, productMonoid.mappend(y, z));

        assertEquals(leftAssoc, rightAssoc);
        assertEquals(Integer.valueOf(24), leftAssoc);
    }

    @Test
    public void testBooleanAndMonoidIdentity() {
        Exercise2.BooleanAndMonoid andMonoid = new Exercise2.BooleanAndMonoid();

        // Test left identity: mempty <> x = x
        assertEquals(Boolean.TRUE, andMonoid.mappend(andMonoid.mempty(), true));
        assertEquals(Boolean.FALSE, andMonoid.mappend(andMonoid.mempty(), false));

        // Test right identity: x <> mempty = x
        assertEquals(Boolean.TRUE, andMonoid.mappend(true, andMonoid.mempty()));
        assertEquals(Boolean.FALSE, andMonoid.mappend(false, andMonoid.mempty()));
    }

    @Test
    public void testBooleanAndMonoidAssociativity() {
        Exercise2.BooleanAndMonoid andMonoid = new Exercise2.BooleanAndMonoid();

        // Test associativity with various combinations
        Boolean[] values = {true, false, true};

        Boolean leftAssoc = andMonoid.mappend(andMonoid.mappend(values[0], values[1]), values[2]);
        Boolean rightAssoc = andMonoid.mappend(values[0], andMonoid.mappend(values[1], values[2]));

        assertEquals(leftAssoc, rightAssoc);
        assertEquals(Boolean.FALSE, leftAssoc); // true && false && true = false
    }

    @Test
    public void testBooleanOrMonoidIdentity() {
        Exercise2.BooleanOrMonoid orMonoid = new Exercise2.BooleanOrMonoid();

        // Test left identity: mempty <> x = x
        assertEquals(Boolean.TRUE, orMonoid.mappend(orMonoid.mempty(), true));
        assertEquals(Boolean.FALSE, orMonoid.mappend(orMonoid.mempty(), false));

        // Test right identity: x <> mempty = x
        assertEquals(Boolean.TRUE, orMonoid.mappend(true, orMonoid.mempty()));
        assertEquals(Boolean.FALSE, orMonoid.mappend(false, orMonoid.mempty()));
    }

    @Test
    public void testBooleanOrMonoidAssociativity() {
        Exercise2.BooleanOrMonoid orMonoid = new Exercise2.BooleanOrMonoid();

        // Test associativity with various combinations
        Boolean[] values = {false, false, true};

        Boolean leftAssoc = orMonoid.mappend(orMonoid.mappend(values[0], values[1]), values[2]);
        Boolean rightAssoc = orMonoid.mappend(values[0], orMonoid.mappend(values[1], values[2]));

        assertEquals(leftAssoc, rightAssoc);
        assertEquals(Boolean.TRUE, leftAssoc); // false || false || true = true
    }

    @Test
    public void testListMonoidIdentity() {
        Exercise2.ListMonoid<String> listMonoid = new Exercise2.ListMonoid<>();

        List<String> testList = Arrays.asList("hello", "world");

        // Test left identity: mempty <> x = x
        assertEquals(testList, listMonoid.mappend(listMonoid.mempty(), testList));

        // Test right identity: x <> mempty = x
        assertEquals(testList, listMonoid.mappend(testList, listMonoid.mempty()));
    }

    @Test
    public void testListMonoidAssociativity() {
        Exercise2.ListMonoid<String> listMonoid = new Exercise2.ListMonoid<>();

        List<String> x = Arrays.asList("a", "b");
        List<String> y = Arrays.asList("c", "d");
        List<String> z = Arrays.asList("e", "f");

        List<String> leftAssoc = listMonoid.mappend(listMonoid.mappend(x, y), z);
        List<String> rightAssoc = listMonoid.mappend(x, listMonoid.mappend(y, z));

        assertEquals(leftAssoc, rightAssoc);
        assertEquals(Arrays.asList("a", "b", "c", "d", "e", "f"), leftAssoc);
    }

    @Test
    public void testMconcatFunction() {
        Exercise2.SumMonoid sumMonoid = new Exercise2.SumMonoid();

        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        Integer result = Exercise2.mconcat(sumMonoid, numbers);

        assertEquals(Integer.valueOf(15), result);
    }

    @Test
    public void testMconcatWithEmptyList() {
        Exercise2.SumMonoid sumMonoid = new Exercise2.SumMonoid();

        List<Integer> empty = Arrays.asList();
        Integer result = Exercise2.mconcat(sumMonoid, empty);

        assertEquals(sumMonoid.mempty(), result);
    }

    @Test
    public void testMconcatWithSingleElement() {
        Exercise2.ProductMonoid productMonoid = new Exercise2.ProductMonoid();

        List<Integer> single = Arrays.asList(42);
        Integer result = Exercise2.mconcat(productMonoid, single);

        assertEquals(Integer.valueOf(42), result);
    }

    @Test
    public void testMconcatWithBooleans() {
        Exercise2.BooleanAndMonoid andMonoid = new Exercise2.BooleanAndMonoid();

        List<Boolean> booleans = Arrays.asList(true, true, false, true);
        Boolean result = Exercise2.mconcat(andMonoid, booleans);

        assertEquals(Boolean.FALSE, result);
    }

    @Test
    public void testMconcatWithLists() {
        Exercise2.ListMonoid<String> listMonoid = new Exercise2.ListMonoid<>();

        List<List<String>> lists = Arrays.asList(
            Arrays.asList("a", "b"),
            Arrays.asList("c", "d"),
            Arrays.asList("e")
        );

        List<String> result = Exercise2.mconcat(listMonoid, lists);
        assertEquals(Arrays.asList("a", "b", "c", "d", "e"), result);
    }
}
