package info.jab.cis194.homework7;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
        // Given
        Exercise2.SumMonoid sumMonoid = new Exercise2.SumMonoid();
        Integer testValue = 42;

        // When & Then - Test left identity: mempty <> x = x
        Integer leftIdentityResult = sumMonoid.mappend(sumMonoid.mempty(), testValue);
        assertThat(leftIdentityResult).isEqualTo(testValue);

        // When & Then - Test right identity: x <> mempty = x
        Integer rightIdentityResult = sumMonoid.mappend(testValue, sumMonoid.mempty());
        assertThat(rightIdentityResult).isEqualTo(testValue);
    }

    @Test
    public void testSumMonoidAssociativity() {
        // Given
        Exercise2.SumMonoid sumMonoid = new Exercise2.SumMonoid();
        Integer x = 10, y = 20, z = 30;

        // When
        Integer leftAssoc = sumMonoid.mappend(sumMonoid.mappend(x, y), z);
        Integer rightAssoc = sumMonoid.mappend(x, sumMonoid.mappend(y, z));

        // Then - Test associativity: (x <> y) <> z = x <> (y <> z)
        assertThat(leftAssoc).isEqualTo(rightAssoc);
        assertThat(leftAssoc).isEqualTo(60);
    }

    @Test
    public void testProductMonoidIdentity() {
        // Given
        Exercise2.ProductMonoid productMonoid = new Exercise2.ProductMonoid();
        Integer testValue = 42;

        // When & Then - Test left identity: mempty <> x = x
        Integer leftIdentityResult = productMonoid.mappend(productMonoid.mempty(), testValue);
        assertThat(leftIdentityResult).isEqualTo(testValue);

        // When & Then - Test right identity: x <> mempty = x
        Integer rightIdentityResult = productMonoid.mappend(testValue, productMonoid.mempty());
        assertThat(rightIdentityResult).isEqualTo(testValue);
    }

    @Test
    public void testProductMonoidAssociativity() {
        // Given
        Exercise2.ProductMonoid productMonoid = new Exercise2.ProductMonoid();
        Integer x = 2, y = 3, z = 4;

        // When
        Integer leftAssoc = productMonoid.mappend(productMonoid.mappend(x, y), z);
        Integer rightAssoc = productMonoid.mappend(x, productMonoid.mappend(y, z));

        // Then - Test associativity: (x <> y) <> z = x <> (y <> z)
        assertThat(leftAssoc).isEqualTo(rightAssoc);
        assertThat(leftAssoc).isEqualTo(24);
    }

    @Test
    public void testBooleanAndMonoidIdentity() {
        // Given
        Exercise2.BooleanAndMonoid andMonoid = new Exercise2.BooleanAndMonoid();

        // When & Then - Test left identity: mempty <> x = x
        assertThat(andMonoid.mappend(andMonoid.mempty(), true)).isTrue();
        assertThat(andMonoid.mappend(andMonoid.mempty(), false)).isFalse();

        // When & Then - Test right identity: x <> mempty = x
        assertThat(andMonoid.mappend(true, andMonoid.mempty())).isTrue();
        assertThat(andMonoid.mappend(false, andMonoid.mempty())).isFalse();
    }

    @Test
    public void testBooleanAndMonoidAssociativity() {
        // Given
        Exercise2.BooleanAndMonoid andMonoid = new Exercise2.BooleanAndMonoid();
        Boolean[] values = {true, false, true};

        // When
        Boolean leftAssoc = andMonoid.mappend(andMonoid.mappend(values[0], values[1]), values[2]);
        Boolean rightAssoc = andMonoid.mappend(values[0], andMonoid.mappend(values[1], values[2]));

        // Then - Test associativity: (x <> y) <> z = x <> (y <> z)
        assertThat(leftAssoc).isEqualTo(rightAssoc);
        assertThat(leftAssoc).isFalse(); // true && false && true = false
    }

    @Test
    public void testBooleanOrMonoidIdentity() {
        // Given
        Exercise2.BooleanOrMonoid orMonoid = new Exercise2.BooleanOrMonoid();

        // When & Then - Test left identity: mempty <> x = x
        assertThat(orMonoid.mappend(orMonoid.mempty(), true)).isTrue();
        assertThat(orMonoid.mappend(orMonoid.mempty(), false)).isFalse();

        // When & Then - Test right identity: x <> mempty = x
        assertThat(orMonoid.mappend(true, orMonoid.mempty())).isTrue();
        assertThat(orMonoid.mappend(false, orMonoid.mempty())).isFalse();
    }

    @Test
    public void testBooleanOrMonoidAssociativity() {
        // Given
        Exercise2.BooleanOrMonoid orMonoid = new Exercise2.BooleanOrMonoid();
        Boolean[] values = {false, false, true};

        // When
        Boolean leftAssoc = orMonoid.mappend(orMonoid.mappend(values[0], values[1]), values[2]);
        Boolean rightAssoc = orMonoid.mappend(values[0], orMonoid.mappend(values[1], values[2]));

        // Then - Test associativity: (x <> y) <> z = x <> (y <> z)
        assertThat(leftAssoc).isEqualTo(rightAssoc);
        assertThat(leftAssoc).isTrue(); // false || false || true = true
    }

    @Test
    public void testListMonoidIdentity() {
        // Given
        Exercise2.ListMonoid<String> listMonoid = new Exercise2.ListMonoid<>();
        List<String> testList = Arrays.asList("hello", "world");

        // When & Then - Test left identity: mempty <> x = x
        List<String> leftIdentityResult = listMonoid.mappend(listMonoid.mempty(), testList);
        assertThat(leftIdentityResult).isEqualTo(testList);

        // When & Then - Test right identity: x <> mempty = x
        List<String> rightIdentityResult = listMonoid.mappend(testList, listMonoid.mempty());
        assertThat(rightIdentityResult).isEqualTo(testList);
    }

    @Test
    public void testListMonoidAssociativity() {
        // Given
        Exercise2.ListMonoid<String> listMonoid = new Exercise2.ListMonoid<>();
        List<String> x = Arrays.asList("a", "b");
        List<String> y = Arrays.asList("c", "d");
        List<String> z = Arrays.asList("e", "f");

        // When
        List<String> leftAssoc = listMonoid.mappend(listMonoid.mappend(x, y), z);
        List<String> rightAssoc = listMonoid.mappend(x, listMonoid.mappend(y, z));

        // Then - Test associativity: (x <> y) <> z = x <> (y <> z)
        assertThat(leftAssoc).isEqualTo(rightAssoc);
        assertThat(leftAssoc).isEqualTo(Arrays.asList("a", "b", "c", "d", "e", "f"));
    }

    @Test
    public void testMconcatFunction() {
        // Given
        Exercise2.SumMonoid sumMonoid = new Exercise2.SumMonoid();
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

        // When
        Integer result = Exercise2.mconcat(sumMonoid, numbers);

        // Then
        assertThat(result).isEqualTo(15);
    }

    @Test
    public void testMconcatWithEmptyList() {
        // Given
        Exercise2.SumMonoid sumMonoid = new Exercise2.SumMonoid();
        List<Integer> empty = Arrays.asList();

        // When
        Integer result = Exercise2.mconcat(sumMonoid, empty);

        // Then
        assertThat(result).isEqualTo(sumMonoid.mempty());
    }

    @Test
    public void testMconcatWithSingleElement() {
        // Given
        Exercise2.ProductMonoid productMonoid = new Exercise2.ProductMonoid();
        List<Integer> single = Arrays.asList(42);

        // When
        Integer result = Exercise2.mconcat(productMonoid, single);

        // Then
        assertThat(result).isEqualTo(42);
    }

    @Test
    public void testMconcatWithBooleans() {
        // Given
        Exercise2.BooleanAndMonoid andMonoid = new Exercise2.BooleanAndMonoid();
        List<Boolean> booleans = Arrays.asList(true, true, false, true);

        // When
        Boolean result = Exercise2.mconcat(andMonoid, booleans);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    public void testMconcatWithLists() {
        // Given
        Exercise2.ListMonoid<String> listMonoid = new Exercise2.ListMonoid<>();
        List<List<String>> lists = Arrays.asList(
            Arrays.asList("a", "b"),
            Arrays.asList("c", "d"),
            Arrays.asList("e")
        );

        // When
        List<String> result = Exercise2.mconcat(listMonoid, lists);

        // Then
        assertThat(result).isEqualTo(Arrays.asList("a", "b", "c", "d", "e"));
    }
}
