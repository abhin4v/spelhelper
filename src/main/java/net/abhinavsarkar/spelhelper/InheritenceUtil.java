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

import java.util.LinkedHashSet;
import java.util.Set;

final class InheritenceUtil {

    private InheritenceUtil() {
    }

    public static Set<Class<?>> getInheritance(final Class<?> clazz) {
        LinkedHashSet<Class<?>> result = new LinkedHashSet<Class<?>>();
        result.add(clazz);
        getInheritance(clazz, result);
        return result;
    }

    /**
     * Get inheritance of type.
     *
     * @param clazz
     * @param result
     */
    private static void getInheritance(final Class<?> clazz, final Set<Class<?>> result) {
        Class<?> superclass = getSuperclass(clazz);

        if (superclass != null) {
            result.add(superclass);
            getInheritance(superclass, result);
        }

        getInterfaceInheritance(clazz, result);
    }

    /**
     * Get interfaces that the type inherits from.
     *
     * @param clazz
     * @param result
     */
    private static void getInterfaceInheritance(final Class<?> clazz,
            final Set<Class<?>> result) {
        for (Class<?> c : clazz.getInterfaces()) {
            result.add(c);
            getInterfaceInheritance(c, result);
        }
    }

    /**
     * Get superclass of class.
     *
     * @param clazz
     * @return
     */
    private static Class<?> getSuperclass(final Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        if (clazz.isArray() && clazz != Object[].class) {
            Class<?> type = clazz.getComponentType();
            while (type.isArray()) {
                type = type.getComponentType();
            }
            return type;
        }
        return clazz.getSuperclass();
    }

}
