package info.jab.cis194.homework5;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Exercise 3: Type Class Constraints and Generic Programming
 *
 * This exercise demonstrates advanced type classes including Functor, Applicative,
 * Monad, Foldable, and Traversable. It shows how these abstractions can be used
 * to write highly generic and reusable code.
 */
public class Exercise3 {

    // Maybe type - represents optional values

    /**
     * Maybe type representing optional values
     */
    public static abstract class Maybe<T> {

        private Maybe() {} // Prevent external subclassing

        /**
         * Check if this Maybe contains a value
         */
        public abstract boolean isJust();

        /**
         * Check if this Maybe is empty
         */
        public abstract boolean isNothing();

        /**
         * Get the value if present, throw exception otherwise
         */
        public abstract T get();

        /**
         * Apply a visitor pattern
         */
        public abstract <R> R accept(MaybeVisitor<T, R> visitor);
    }

    /**
     * Just case - contains a value
     */
    private static class Just<T> extends Maybe<T> {
        private final T value;

        public Just(T value) {
            this.value = Objects.requireNonNull(value, "Just value cannot be null");
        }

        @Override
        public boolean isJust() {
            return true;
        }

        @Override
        public boolean isNothing() {
            return false;
        }

        @Override
        public T get() {
            return value;
        }

        @Override
        public <R> R accept(MaybeVisitor<T, R> visitor) {
            return visitor.visitJust(value);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Just<?> just = (Just<?>) obj;
            return Objects.equals(value, just.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public String toString() {
            return "Just(" + value + ")";
        }
    }

    /**
     * Nothing case - empty value
     */
    private static class Nothing<T> extends Maybe<T> {

        @Override
        public boolean isJust() {
            return false;
        }

        @Override
        public boolean isNothing() {
            return true;
        }

        @Override
        public T get() {
            throw new IllegalStateException("Cannot get value from Nothing");
        }

        @Override
        public <R> R accept(MaybeVisitor<T, R> visitor) {
            return visitor.visitNothing();
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Nothing;
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public String toString() {
            return "Nothing";
        }
    }

    /**
     * Visitor interface for Maybe
     */
    public interface MaybeVisitor<T, R> {
        R visitJust(T value);
        R visitNothing();
    }

    // Maybe constructors

    /**
     * Create a Just value
     */
    public static <T> Maybe<T> just(T value) {
        return new Just<>(value);
    }

    /**
     * Create a Nothing value
     */
    @SuppressWarnings("unchecked")
    public static <T> Maybe<T> nothing() {
        return (Maybe<T>) NOTHING_INSTANCE;
    }

    private static final Maybe<?> NOTHING_INSTANCE = new Nothing<>();

    /**
     * Get value from Maybe with default
     */
    public static <T> T fromMaybe(T defaultValue, Maybe<T> maybe) {
        if (maybe == null) {
            throw new IllegalArgumentException("Maybe cannot be null");
        }
        return maybe.accept(new MaybeVisitor<T, T>() {
            @Override
            public T visitJust(T value) {
                return value;
            }

            @Override
            public T visitNothing() {
                return defaultValue;
            }
        });
    }

    // Functor type class

    /**
     * Functor interface
     */
    public interface Functor<F> {
        <A, B> F fmap(Function<A, B> f, F fa);
    }

    /**
     * Functor instance for Maybe
     */
    private static final Functor<Maybe<?>> MAYBE_FUNCTOR = new Functor<Maybe<?>>() {
        @Override
        @SuppressWarnings("unchecked")
        public <A, B> Maybe<?> fmap(Function<A, B> f, Maybe<?> fa) {
            if (f == null) {
                throw new IllegalArgumentException("Function cannot be null");
            }
            if (fa == null) {
                throw new IllegalArgumentException("Maybe cannot be null");
            }

            Maybe<A> maybe = (Maybe<A>) fa;
            return maybe.accept(new MaybeVisitor<A, Maybe<B>>() {
                @Override
                public Maybe<B> visitJust(A value) {
                    return just(f.apply(value));
                }

                @Override
                public Maybe<B> visitNothing() {
                    return nothing();
                }
            });
        }
    };

    /**
     * Functor instance for List
     */
    private static final Functor<List<?>> LIST_FUNCTOR = new Functor<List<?>>() {
        @Override
        @SuppressWarnings("unchecked")
        public <A, B> List<?> fmap(Function<A, B> f, List<?> fa) {
            if (f == null) {
                throw new IllegalArgumentException("Function cannot be null");
            }
            if (fa == null) {
                throw new IllegalArgumentException("List cannot be null");
            }

            List<A> list = (List<A>) fa;
            List<B> result = new ArrayList<>();
            for (A item : list) {
                result.add(f.apply(item));
            }
            return result;
        }
    };

    // Generic fmap methods

    /**
     * Map over Maybe
     */
    public static <A, B> Maybe<B> fmap(Function<A, B> f, Maybe<A> maybe) {
        if (f == null) {
            throw new IllegalArgumentException("Function cannot be null");
        }
        if (maybe == null) {
            throw new IllegalArgumentException("Maybe cannot be null");
        }

        return maybe.accept(new MaybeVisitor<A, Maybe<B>>() {
            @Override
            public Maybe<B> visitJust(A value) {
                return just(f.apply(value));
            }

            @Override
            public Maybe<B> visitNothing() {
                return nothing();
            }
        });
    }

    /**
     * Map over List
     */
    @SuppressWarnings("unchecked")
    public static <A, B> List<B> fmap(Function<A, B> f, List<A> list) {
        return (List<B>) LIST_FUNCTOR.fmap(f, list);
    }

    // Applicative type class

    /**
     * Applicative interface
     */
    public interface Applicative<F> extends Functor<F> {
        <A> F pure(A value);
        <A, B> F apply(F fab, F fa);
    }

    /**
     * Applicative instance for Maybe
     */
    private static final Applicative<Maybe<?>> MAYBE_APPLICATIVE = new Applicative<Maybe<?>>() {
        @Override
        @SuppressWarnings("unchecked")
        public <A> Maybe<?> pure(A value) {
            return just(value);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <A, B> Maybe<?> apply(Maybe<?> fab, Maybe<?> fa) {
            if (fab == null || fa == null) {
                throw new IllegalArgumentException("Arguments cannot be null");
            }

            Maybe<Function<A, B>> maybeF = (Maybe<Function<A, B>>) fab;
            Maybe<A> maybeA = (Maybe<A>) fa;

            return maybeF.accept(new MaybeVisitor<Function<A, B>, Maybe<B>>() {
                @Override
                public Maybe<B> visitJust(Function<A, B> f) {
                    return maybeA.accept(new MaybeVisitor<A, Maybe<B>>() {
                        @Override
                        public Maybe<B> visitJust(A value) {
                            return just(f.apply(value));
                        }

                        @Override
                        public Maybe<B> visitNothing() {
                            return nothing();
                        }
                    });
                }

                @Override
                public Maybe<B> visitNothing() {
                    return nothing();
                }
            });
        }

        @Override
        @SuppressWarnings("unchecked")
        public <A, B> Maybe<?> fmap(Function<A, B> f, Maybe<?> fa) {
            return MAYBE_FUNCTOR.fmap(f, fa);
        }
    };

    // Generic applicative methods

    /**
     * Pure for Maybe
     */
    @SuppressWarnings("unchecked")
    public static <A> Maybe<A> pure(A value) {
        return (Maybe<A>) MAYBE_APPLICATIVE.pure(value);
    }

    /**
     * Apply for Maybe
     */
    public static <A, B> Maybe<B> apply(Maybe<Function<A, B>> maybeF, Maybe<A> maybeA) {
        if (maybeF == null || maybeA == null) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }

        return maybeF.accept(new MaybeVisitor<Function<A, B>, Maybe<B>>() {
            @Override
            public Maybe<B> visitJust(Function<A, B> f) {
                return fmap(f, maybeA);
            }

            @Override
            public Maybe<B> visitNothing() {
                return nothing();
            }
        });
    }

    /**
     * Lift a binary function into Maybe context
     */
    public static <A, B, C> Maybe<C> liftA2(Function<A, Function<B, C>> op, Maybe<A> ma, Maybe<B> mb) {
        if (op == null) {
            throw new IllegalArgumentException("Operation cannot be null");
        }

        Maybe<Function<B, C>> maybeF = fmap(op, ma);
        return apply(maybeF, mb);
    }

    // Monad type class

    /**
     * Monad interface
     */
    public interface Monad<M> extends Applicative<M> {
        <A, B> M bind(M ma, Function<A, M> f);
    }

    /**
     * Monad instance for Maybe
     */
    private static final Monad<Maybe<?>> MAYBE_MONAD = new Monad<Maybe<?>>() {
        @Override
        @SuppressWarnings("unchecked")
        public <A, B> Maybe<?> bind(Maybe<?> ma, Function<A, Maybe<?>> f) {
            if (ma == null) {
                throw new IllegalArgumentException("Maybe cannot be null");
            }
            if (f == null) {
                throw new IllegalArgumentException("Function cannot be null");
            }

            Maybe<A> maybe = (Maybe<A>) ma;
            return maybe.accept(new MaybeVisitor<A, Maybe<B>>() {
                @Override
                @SuppressWarnings("unchecked")
                public Maybe<B> visitJust(A value) {
                    return (Maybe<B>) f.apply(value);
                }

                @Override
                public Maybe<B> visitNothing() {
                    return nothing();
                }
            });
        }

        @Override
        @SuppressWarnings("unchecked")
        public <A> Maybe<?> pure(A value) {
            return MAYBE_APPLICATIVE.pure(value);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <A, B> Maybe<?> apply(Maybe<?> fab, Maybe<?> fa) {
            return MAYBE_APPLICATIVE.apply(fab, fa);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <A, B> Maybe<?> fmap(Function<A, B> f, Maybe<?> fa) {
            return MAYBE_APPLICATIVE.fmap(f, fa);
        }
    };

    // Generic monad methods

    /**
     * Bind for Maybe
     */
    public static <A, B> Maybe<B> bind(Maybe<A> ma, Function<A, Maybe<B>> f) {
        if (ma == null) {
            throw new IllegalArgumentException("Maybe cannot be null");
        }
        if (f == null) {
            throw new IllegalArgumentException("Function cannot be null");
        }

        return ma.accept(new MaybeVisitor<A, Maybe<B>>() {
            @Override
            public Maybe<B> visitJust(A value) {
                return f.apply(value);
            }

            @Override
            public Maybe<B> visitNothing() {
                return nothing();
            }
        });
    }

    // Foldable type class

    /**
     * Foldable interface
     */
    public interface Foldable<F> {
        <A, B> B foldr(BinaryOperator<B> f, B z, F fa);
    }

    /**
     * Foldable instance for Maybe
     */
    private static final Foldable<Maybe<?>> MAYBE_FOLDABLE = new Foldable<Maybe<?>>() {
        @Override
        @SuppressWarnings("unchecked")
        public <A, B> B foldr(BinaryOperator<B> f, B z, Maybe<?> fa) {
            if (f == null) {
                throw new IllegalArgumentException("Function cannot be null");
            }
            if (fa == null) {
                throw new IllegalArgumentException("Foldable cannot be null");
            }

            Maybe<A> maybe = (Maybe<A>) fa;
            return maybe.accept(new MaybeVisitor<A, B>() {
                @Override
                public B visitJust(A value) {
                    // For Maybe, we need to convert A to B somehow
                    // This is a simplified implementation
                    @SuppressWarnings("unchecked")
                    B bValue = (B) value;
                    return f.apply(bValue, z);
                }

                @Override
                public B visitNothing() {
                    return z;
                }
            });
        }
    };

    /**
     * Foldable instance for List
     */
    private static final Foldable<List<?>> LIST_FOLDABLE = new Foldable<List<?>>() {
        @Override
        @SuppressWarnings("unchecked")
        public <A, B> B foldr(BinaryOperator<B> f, B z, List<?> fa) {
            if (f == null) {
                throw new IllegalArgumentException("Function cannot be null");
            }
            if (fa == null) {
                throw new IllegalArgumentException("Foldable cannot be null");
            }

            List<A> list = (List<A>) fa;
            B result = z;
            for (int i = list.size() - 1; i >= 0; i--) {
                @SuppressWarnings("unchecked")
                B item = (B) list.get(i);
                result = f.apply(item, result);
            }
            return result;
        }
    };

    // Generic foldable methods

    /**
     * Fold over Maybe
     */
    @SuppressWarnings("unchecked")
    public static <A> A foldr(BinaryOperator<A> f, A z, Maybe<A> maybe) {
        return MAYBE_FOLDABLE.foldr(f, z, maybe);
    }

    /**
     * Fold over List
     */
    @SuppressWarnings("unchecked")
    public static <A> A foldr(BinaryOperator<A> f, A z, List<A> list) {
        return LIST_FOLDABLE.foldr(f, z, list);
    }

    /**
     * Calculate length of foldable structure
     */
    public static <T> Integer length(Maybe<T> maybe) {
        return foldr((a, b) -> b + 1, 0,
            fmap(x -> 1, maybe));
    }

    /**
     * Calculate length of list
     */
    public static <T> Integer length(List<T> list) {
        return foldr((a, b) -> a + b, 0,
            fmap(x -> 1, list));
    }

    /**
     * Convert Maybe to List
     */
    public static <T> List<T> toList(Maybe<T> maybe) {
        if (maybe == null) {
            throw new IllegalArgumentException("Maybe cannot be null");
        }

        return maybe.accept(new MaybeVisitor<T, List<T>>() {
            @Override
            public List<T> visitJust(T value) {
                List<T> result = new ArrayList<>();
                result.add(value);
                return result;
            }

            @Override
            public List<T> visitNothing() {
                return new ArrayList<>();
            }
        });
    }

    // Traversable type class

    /**
     * Traversable interface
     */
    public interface Traversable<T> extends Functor<T>, Foldable<T> {
        <F, A, B> F traverse(Function<A, F> f, T ta);
    }

    // Generic traversable methods

    /**
     * Traverse List with Maybe using functional approach
     */
    public static <A, B> Maybe<List<B>> traverse(Function<A, Maybe<B>> f, List<A> list) {
        if (f == null) {
            throw new IllegalArgumentException("Function cannot be null");
        }
        if (list == null) {
            throw new IllegalArgumentException("List cannot be null");
        }

        return list.stream()
            .map(f)
            .reduce(
                pure(new ArrayList<B>()),
                (accMaybe, itemMaybe) -> liftA2(
                    acc -> item -> {
                        List<B> newList = new ArrayList<>(acc);
                        newList.add(item);
                        return newList;
                    },
                    accMaybe,
                    itemMaybe
                ),
                (maybe1, maybe2) -> liftA2(
                    list1 -> list2 -> {
                        List<B> combined = new ArrayList<>(list1);
                        combined.addAll(list2);
                        return combined;
                    },
                    maybe1,
                    maybe2
                )
            );
    }

    /**
     * Sequence a list of Maybe values
     */
    public static <A> Maybe<List<A>> sequence(List<Maybe<A>> maybes) {
        if (maybes == null) {
            throw new IllegalArgumentException("List cannot be null");
        }

        return traverse(Function.identity(), maybes);
    }

    // Advanced generic programming utilities

    /**
     * Pair utility class
     */
    public static record Pair<A, B>(A first, B second) {}

    /**
     * Generic filter function using functional approach
     */
    public static <A> List<A> filter(Predicate<A> predicate, List<A> list) {
        if (predicate == null) {
            throw new IllegalArgumentException("Predicate cannot be null");
        }
        if (list == null) {
            throw new IllegalArgumentException("List cannot be null");
        }

        return list.stream()
            .filter(predicate)
            .toList();
    }

    /**
     * Generic partition function using functional approach
     */
    public static <A> Pair<List<A>, List<A>> partition(Predicate<A> predicate, List<A> list) {
        if (predicate == null) {
            throw new IllegalArgumentException("Predicate cannot be null");
        }
        if (list == null) {
            throw new IllegalArgumentException("List cannot be null");
        }

        List<A> trues = list.stream()
            .filter(predicate)
            .toList();

        List<A> falses = list.stream()
            .filter(predicate.negate())
            .toList();

        return new Pair<>(trues, falses);
    }

    /**
     * MapMaybe - apply function that may fail, collect successes using functional approach
     */
    public static <A, B> List<B> mapMaybe(Function<A, Maybe<B>> f, List<A> list) {
        if (f == null) {
            throw new IllegalArgumentException("Function cannot be null");
        }
        if (list == null) {
            throw new IllegalArgumentException("List cannot be null");
        }

        return list.stream()
            .map(f)
            .filter(Maybe::isJust)
            .map(Maybe::get)
            .toList();
    }

    // Utility methods for demonstrating type class laws

    /**
     * Compose two functions
     */
    public static <A, B, C> Function<A, C> compose(Function<B, C> g, Function<A, B> f) {
        return a -> g.apply(f.apply(a));
    }

    /**
     * Identity function
     */
    public static <A> Function<A, A> identity() {
        return Function.identity();
    }

    /**
     * Constant function
     */
    public static <A, B> Function<A, B> constant(B value) {
        return a -> value;
    }
}
