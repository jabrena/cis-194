package info.jab.cis194.homework5;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Exercise 2: Polymorphic Functions and Type Classes
 *
 * This exercise demonstrates type classes in Java through interfaces and generic methods.
 * It implements common type classes like Eq, Ord, and Show, along with polymorphic
 * functions that work across different types.
 */
public class Exercise2 {

    // Type class interfaces

    /**
     * Eq type class - defines equality operations
     */
    public interface Eq<T> {
        boolean eq(T a, T b);

        default boolean neq(T a, T b) {
            return !eq(a, b);
        }
    }

    /**
     * Ord type class - defines ordering operations
     */
    public interface Ord<T> extends Eq<T> {
        int compare(T a, T b);

        default boolean lt(T a, T b) {
            return compare(a, b) < 0;
        }

        default boolean lte(T a, T b) {
            return compare(a, b) <= 0;
        }

        default boolean gt(T a, T b) {
            return compare(a, b) > 0;
        }

        default boolean gte(T a, T b) {
            return compare(a, b) >= 0;
        }

        @Override
        default boolean eq(T a, T b) {
            return compare(a, b) == 0;
        }
    }

    /**
     * Show type class - defines string representation
     */
    public interface Show<T> {
        String show(T value);
    }

    // Type class instances

    /**
     * Eq instance for Integer
     */
    private static final Eq<Integer> INTEGER_EQ = new Eq<Integer>() {
        @Override
        public boolean eq(Integer a, Integer b) {
            if (a == null || b == null) {
                throw new IllegalArgumentException("Arguments cannot be null");
            }
            return a.equals(b);
        }
    };

    /**
     * Ord instance for Integer
     */
    private static final Ord<Integer> INTEGER_ORD = new Ord<Integer>() {
        @Override
        public int compare(Integer a, Integer b) {
            if (a == null || b == null) {
                throw new IllegalArgumentException("Arguments cannot be null");
            }
            return a.compareTo(b);
        }
    };

    /**
     * Show instance for Integer
     */
    private static final Show<Integer> INTEGER_SHOW = new Show<Integer>() {
        @Override
        public String show(Integer value) {
            if (value == null) {
                throw new IllegalArgumentException("Value cannot be null");
            }
            return value.toString();
        }
    };

    /**
     * Eq instance for String
     */
    private static final Eq<String> STRING_EQ = new Eq<String>() {
        @Override
        public boolean eq(String a, String b) {
            if (a == null || b == null) {
                throw new IllegalArgumentException("Arguments cannot be null");
            }
            return a.equals(b);
        }
    };

    /**
     * Ord instance for String
     */
    private static final Ord<String> STRING_ORD = new Ord<String>() {
        @Override
        public int compare(String a, String b) {
            if (a == null || b == null) {
                throw new IllegalArgumentException("Arguments cannot be null");
            }
            return a.compareTo(b);
        }
    };

    /**
     * Show instance for String
     */
    private static final Show<String> STRING_SHOW = new Show<String>() {
        @Override
        public String show(String value) {
            if (value == null) {
                throw new IllegalArgumentException("Value cannot be null");
            }
            return "\"" + value + "\"";
        }
    };

    // Point class and its type class instances

    /**
     * Point record representing a 2D point
     */
    public static record Point(int x, int y) {
        /**
         * Calculate Euclidean distance between two points
         */
        public double distanceTo(Point other) {
            if (other == null) {
                throw new IllegalArgumentException("Other point cannot be null");
            }
            int dx = this.x - other.x;
            int dy = this.y - other.y;
            return Math.sqrt(dx * dx + dy * dy);
        }
    }

    /**
     * Eq instance for Point
     */
    private static final Eq<Point> POINT_EQ = new Eq<Point>() {
        @Override
        public boolean eq(Point a, Point b) {
            if (a == null || b == null) {
                throw new IllegalArgumentException("Arguments cannot be null");
            }
            return a.equals(b);
        }
    };

    /**
     * Ord instance for Point (lexicographic ordering)
     */
    private static final Ord<Point> POINT_ORD = new Ord<Point>() {
        @Override
        public int compare(Point a, Point b) {
            if (a == null || b == null) {
                throw new IllegalArgumentException("Arguments cannot be null");
            }
            int xCompare = Integer.compare(a.x(), b.x());
            if (xCompare != 0) {
                return xCompare;
            }
            return Integer.compare(a.y(), b.y());
        }
    };

    /**
     * Show instance for Point
     */
    private static final Show<Point> POINT_SHOW = new Show<Point>() {
        @Override
        public String show(Point value) {
            if (value == null) {
                throw new IllegalArgumentException("Value cannot be null");
            }
            return "Point(" + value.x() + ", " + value.y() + ")";
        }
    };

    // Generic type class methods

    /**
     * Generic equality check
     */
    @SuppressWarnings("unchecked")
    public static <T> boolean eq(T a, T b) {
        if (a == null || b == null) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }

        Eq<T> eq = getEqInstance((Class<T>) a.getClass());
        return eq.eq(a, b);
    }

    /**
     * Generic inequality check
     */
    public static <T> boolean neq(T a, T b) {
        return !eq(a, b);
    }

    /**
     * Generic less than comparison
     */
    @SuppressWarnings("unchecked")
    public static <T> boolean lt(T a, T b) {
        if (a == null || b == null) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }

        Ord<T> ord = getOrdInstance((Class<T>) a.getClass());
        return ord.lt(a, b);
    }

    /**
     * Generic less than or equal comparison
     */
    @SuppressWarnings("unchecked")
    public static <T> boolean lte(T a, T b) {
        if (a == null || b == null) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }

        Ord<T> ord = getOrdInstance((Class<T>) a.getClass());
        return ord.lte(a, b);
    }

    /**
     * Generic greater than comparison
     */
    @SuppressWarnings("unchecked")
    public static <T> boolean gt(T a, T b) {
        if (a == null || b == null) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }

        Ord<T> ord = getOrdInstance((Class<T>) a.getClass());
        return ord.gt(a, b);
    }

    /**
     * Generic greater than or equal comparison
     */
    @SuppressWarnings("unchecked")
    public static <T> boolean gte(T a, T b) {
        if (a == null || b == null) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }

        Ord<T> ord = getOrdInstance((Class<T>) a.getClass());
        return ord.gte(a, b);
    }

    /**
     * Generic show method
     */
    @SuppressWarnings("unchecked")
    public static <T> String show(T value) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }

        Show<T> show = getShowInstance((Class<T>) value.getClass());
        return show.show(value);
    }

    // Helper methods to get type class instances

    @SuppressWarnings("unchecked")
    private static <T> Eq<T> getEqInstance(Class<T> clazz) {
        if (clazz == Integer.class) {
            return (Eq<T>) INTEGER_EQ;
        } else if (clazz == String.class) {
            return (Eq<T>) STRING_EQ;
        } else if (clazz == Point.class) {
            return (Eq<T>) POINT_EQ;
        } else {
            throw new UnsupportedOperationException("No Eq instance for " + clazz.getSimpleName());
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> Ord<T> getOrdInstance(Class<T> clazz) {
        if (clazz == Integer.class) {
            return (Ord<T>) INTEGER_ORD;
        } else if (clazz == String.class) {
            return (Ord<T>) STRING_ORD;
        } else if (clazz == Point.class) {
            return (Ord<T>) POINT_ORD;
        } else {
            throw new UnsupportedOperationException("No Ord instance for " + clazz.getSimpleName());
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> Show<T> getShowInstance(Class<T> clazz) {
        if (clazz == Integer.class) {
            return (Show<T>) INTEGER_SHOW;
        } else if (clazz == String.class) {
            return (Show<T>) STRING_SHOW;
        } else if (clazz == Point.class) {
            return (Show<T>) POINT_SHOW;
        } else {
            throw new UnsupportedOperationException("No Show instance for " + clazz.getSimpleName());
        }
    }

    // Polymorphic functions

    /**
     * Find the maximum element in a list using functional approach
     */
    @SuppressWarnings("unchecked")
    public static <T> Optional<T> maximum(List<T> list) {
        if (list == null) {
            throw new IllegalArgumentException("List cannot be null");
        }

        return list.stream()
            .reduce((a, b) -> {
                @SuppressWarnings("unchecked")
                Ord<T> ord = getOrdInstance((Class<T>) a.getClass());
                return ord.gt(a, b) ? a : b;
            });
    }

    /**
     * Sort a list in ascending order using functional approach
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> sort(List<T> list) {
        if (list == null) {
            throw new IllegalArgumentException("List cannot be null");
        }

        return list.stream()
            .sorted((a, b) -> {
                @SuppressWarnings("unchecked")
                Ord<T> ord = getOrdInstance((Class<T>) a.getClass());
                return ord.compare(a, b);
            })
            .toList();
    }

    /**
     * Check if an element is in a list using functional approach
     */
    @SuppressWarnings("unchecked")
    public static <T> boolean elem(T element, List<T> list) {
        if (element == null) {
            throw new IllegalArgumentException("Element cannot be null");
        }
        if (list == null) {
            throw new IllegalArgumentException("List cannot be null");
        }

        Eq<T> eq = getEqInstance((Class<T>) element.getClass());

        return list.stream()
            .anyMatch(item -> eq.eq(element, item));
    }

    /**
     * Remove duplicates from a list, preserving order using functional approach
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> nub(List<T> list) {
        if (list == null) {
            throw new IllegalArgumentException("List cannot be null");
        }

        return list.stream()
            .reduce(
                new ArrayList<T>(),
                (acc, element) -> {
                    @SuppressWarnings("unchecked")
                    Eq<T> eq = getEqInstance((Class<T>) element.getClass());

                    boolean alreadyExists = acc.stream()
                        .anyMatch(existing -> eq.eq(element, existing));

                    if (!alreadyExists) {
                        ArrayList<T> newAcc = new ArrayList<>(acc);
                        newAcc.add(element);
                        return newAcc;
                    }
                    return acc;
                },
                (list1, list2) -> {
                    ArrayList<T> combined = new ArrayList<>(list1);
                    combined.addAll(list2);
                    return combined;
                }
            );
    }

    /**
     * Show a list with proper formatting
     */
    public static <T> String showList(List<T> list) {
        if (list == null) {
            throw new IllegalArgumentException("List cannot be null");
        }

        if (list.isEmpty()) {
            return "[]";
        }

        String elements = list.stream()
            .map(Exercise2::show)
            .reduce("", (acc, element) -> acc.isEmpty() ? element : acc + ", " + element);

        return "[" + elements + "]";
    }

    // Utility methods

    /**
     * Calculate distance between two points
     */
    public static double distance(Point p1, Point p2) {
        if (p1 == null || p2 == null) {
            throw new IllegalArgumentException("Points cannot be null");
        }
        return p1.distanceTo(p2);
    }

    // Advanced polymorphic functions demonstrating type class constraints

    /**
     * Generic minimum function using functional approach
     */
    public static <T> Optional<T> minimum(List<T> list) {
        if (list == null) {
            throw new IllegalArgumentException("List cannot be null");
        }

        return list.stream()
            .reduce((a, b) -> {
                @SuppressWarnings("unchecked")
                Ord<T> ord = getOrdInstance((Class<T>) a.getClass());
                return ord.lt(a, b) ? a : b;
            });
    }

    /**
     * Generic function to check if a list is sorted using functional approach
     */
    @SuppressWarnings("unchecked")
    public static <T> boolean isSorted(List<T> list) {
        if (list == null) {
            throw new IllegalArgumentException("List cannot be null");
        }

        if (list.size() <= 1) {
            return true;
        }

        return IntStream.range(0, list.size() - 1)
            .allMatch(i -> {
                T current = list.get(i);
                T next = list.get(i + 1);
                @SuppressWarnings("unchecked")
                Ord<T> ord = getOrdInstance((Class<T>) current.getClass());
                return ord.lte(current, next);
            });
    }

    /**
     * Generic function to insert an element into a sorted list using functional approach
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> insert(T element, List<T> sortedList) {
        if (element == null) {
            throw new IllegalArgumentException("Element cannot be null");
        }
        if (sortedList == null) {
            throw new IllegalArgumentException("List cannot be null");
        }

        if (sortedList.isEmpty()) {
            return List.of(element);
        }

        OrdInstance<T> ord = getOrdInstance((Class<T>) element.getClass());

        // Find the insertion point
        int insertionIndex = IntStream.range(0, sortedList.size())
            .filter(i -> ord.lte(element, sortedList.get(i)))
            .findFirst()
            .orElse(sortedList.size());

        // Build result functionally using streams
        List<T> beforeInsertion = sortedList.stream()
            .limit(insertionIndex)
            .toList();

        List<T> afterInsertion = sortedList.stream()
            .skip(insertionIndex)
            .toList();

        return Stream.of(
                beforeInsertion.stream(),
                Stream.of(element),
                afterInsertion.stream()
            )
            .flatMap(Function.identity())
            .toList();
    }

    /**
     * Insertion sort implementation using functional approach
     */
    public static <T> List<T> insertionSort(List<T> list) {
        if (list == null) {
            throw new IllegalArgumentException("List cannot be null");
        }

        return list.stream()
            .reduce(
                List.<T>of(),
                (sortedAcc, element) -> insert(element, sortedAcc),
                (list1, list2) -> Stream.concat(list1.stream(), list2.stream()).toList()
            );
    }
}
