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

final class ExtensionFunctions {

    public static <T> List<T> list(final T... args) {
        return unmodifiableList(Arrays.asList(args));
    }

    public static <T> Set<T> set(final T... args) {
        return unmodifiableSet(new HashSet<T>(list(args)));
    }

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
