package net.abhinavsarkar.spelhelper;

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Provides some implicit methods which can be invoked on the instances of
 * class of the first parameter of the method inside a SpEL expression.
 * @author Abhinav Sarkar _abhinav@abhinavsarkar.net_
 */
public final class ImplicitMethods {

    /**
     * Provides implicit method `distinct` on the {@link List} class.
     *
     * Example: `"#list('a','b','a').distinct()" //should return List('a','b')`
     *
     * With implicit property support provided by {@link SpelHelper} this can
     * also be written as:
     *
     * `"#list('a','b','a').distinct" //same output as earlier`
     * @param <T>   Type of the list's elements.
     * @param list  The list to call this method upon.
     * @return  An unmodifiable {@link Set} containing the distinct items of the list.
     */
    public static <T> Set<T> distinct(final List<? extends T> list) {
        return unmodifiableSet(new HashSet<T>(list));
    }

    /**
     * Provides implicit method `sorted` on the {@link List} class.
     *
     * Example: `"#list('c','b','a').sorted()" //should return List('a','b','c')`
     *
     * With implicit property support provided by {@link SpelHelper} this can
     * also be written as:
     *
     * `"#list('c','b','a').sorted" //same output as earlier`
     * @param <T>   Type of the list's elements.
     * @param list  The list to call this method upon.
     * @return      An unmodifiable {@link List} containing the sorted items
     * of the list.
     * @see Collections#sort(List)
     */
    public static <T extends Comparable<? super T>> List<T> sorted(
            final List<? extends T> list) {
        List<T> temp = new ArrayList<T>(list);
        Collections.sort(temp);
        return unmodifiableList(temp);
    }

    /**
     * Provides implicit method `reversed` on the {@link List} class.
     *
     * Example: `"#list('c','b','a').reversed()" //should return List('a','b','c')`
     *
     * With implicit property support provided by {@link SpelHelper} this can
     * also be written as:
     *
     * `"#list('c','b','a').reversed" //same output as earlier`
     * @param <T>   Type of the list's elements.
     * @param list  The list to call this method upon.
     * @return      An unmodifiable {@link List} containing the items of the
     * list in reverse order.
     * @see Collections#reverse(List)
     */
    public static <T> List<T> reversed(final List<? extends T> list) {
        List<T> temp = new ArrayList<T>(list);
        Collections.reverse(temp);
        return unmodifiableList(temp);
    }

    /**
     * Provides implicit method `take` on the {@link List} class.
     *
     * Example: `"#list('c','b','a').take(2)" //should return List('a','b')`
     *
     * @param <T>   Type of the list's elements.
     * @param list  The list to call this method upon.
     * @param n     Number of items to _take_ from the list.
     * @return      An unmodifiable {@link List} containing the first `n` items
     * of the list.
     */
    public static <T> List<T> take(final List<T> list, final int n) {
        return unmodifiableList(list.subList(0, n));
    }

    /**
     * Provides implicit method `drop` on the {@link List} class.
     *
     * Example: `"#list('c','b','a').drop(2)" //should return List('a')`
     *
     * @param <T>   Type of the list's elements.
     * @param list  The list to call this method upon.
     * @param n     Number of items to _drop_ from the list.
     * @return      An unmodifiable {@link List} containing the items after the
     * first `n` items of the list.
     */
    public static <T> List<T> drop(final List<T> list, final int n) {
        return unmodifiableList(list.subList(n, list.size()));
    }

}
