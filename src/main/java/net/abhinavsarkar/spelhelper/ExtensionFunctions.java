/* Copyright 2010 Abhinav Sarkar <abhinav@abhinavsarkar.net>
 *
 * This file is a part of SpelHelper library.
 *
 * SpelHelper library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License (GNU LGPL) as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * SpelHelper library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with SpelHelper library.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.abhinavsarkar.spelhelper;

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.util.Assert;

/**
 * Provides some extension functions to create some basic collection types
 * inline in SpEL expressions.
 * These functions are automatically registered with {@link SpelHelper}.
 *
 * **See Also:**
 * [Spring Docs on extension functions](http://static.springsource.org/spring/docs/3.0.x/spring-framework-reference/html/expressions.html#expressions-ref-functions)
 * @author Abhinav Sarkar _abhinav@abhinavsarkar.net_
 */
public final class ExtensionFunctions {

    private ExtensionFunctions() {
    }

    /**
     * Creates an unmodifiable {@link List} of the arguments provided.
     *
     * Example use: `"#list('one', 'two', 'three')"`
     * @param <T>   Type of the arguments provided.
     * @param args  Arguments to create list of.
     * @return  An unmodifiable list of the arguments provided.
     */
    public static <T> List<T> list(final T... args) {
        return unmodifiableList(Arrays.asList(args));
    }

    /**
     * Creates an unmodifiable {@link Set} of the arguments provided.
     *
     * Example use: `"#set('one', 'two', 'three')"`
     * @param <T>   Type of the arguments provided.
     * @param args  Arguments to create set of.
     * @return  An unmodifiable set of the arguments provided.
     */
    public static <T> Set<T> set(final T... args) {
        return unmodifiableSet(new HashSet<T>(list(args)));
    }

    /**
     * Creates an unmodifiable {@link Map} using the {@link List} of keys
     * provided as the first argument and the {@link List} of values provided
     * as the second argument.
     *
     * Example use: `"#map(#list('one', 'two', 'three'), #list(1, 2, 3))"`
     * @param <K>   Type of the keys of the map.
     * @param <V>   Type of the values of map.
     * @param keys  List of the keys.
     * @param values    List of the values.
     * @return  A unmodifiable map created from the key and value lists.
     * @throws  IllegalArgumentException if the number of keys and the number of
     * values is not equal.
     */
    public static <K,V> Map<K,V> map(final List<? extends K> keys,
            final List<? extends V> values) {
        Assert.isTrue(keys.size() == values.size(),
                "There should equal number of keys and values");
        Map<K,V> map = new HashMap<K,V>();
        int length = keys.size();
        for (int i = 0; i < length; i++) {
            map.put(keys.get(i), values.get(i));
        }
        return unmodifiableMap(map);
    }

}
