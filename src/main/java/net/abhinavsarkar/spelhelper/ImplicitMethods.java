package net.abhinavsarkar.spelhelper;

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

final class ImplicitMethods {

    public static <T> Set<T> distinct(final List<? extends T> list) {
    	return unmodifiableSet(new HashSet<T>(list));
    }

    public static <T extends Comparable<? super T>> List<T> sorted(
    		final List<? extends T> list) {
    	List<T> temp = new ArrayList<T>(list);
    	Collections.sort(temp);
    	return unmodifiableList(temp);
    }

    public static <T> List<T> reversed(final List<? extends T> list) {
    	List<T> temp = new ArrayList<T>(list);
    	Collections.reverse(temp);
    	return unmodifiableList(temp);
    }

    public static <T> List<T> take(final List<T> list, final int n) {
    	return unmodifiableList(list.subList(0, n));
    }

    public static <T> List<T> drop(final List<T> list, final int n) {
    	return unmodifiableList(list.subList(n, list.size()));
    }

}
