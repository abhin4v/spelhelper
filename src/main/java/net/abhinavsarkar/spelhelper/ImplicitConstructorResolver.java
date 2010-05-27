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

import java.lang.reflect.Constructor;
import java.util.Arrays;

import org.springframework.expression.AccessException;
import org.springframework.expression.ConstructorExecutor;
import org.springframework.expression.ConstructorResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.ReflectiveConstructorResolver;

final class ImplicitConstructorResolver implements
        ConstructorResolver {

    private final ReflectiveConstructorResolver delegate = new ReflectiveConstructorResolver();

    public ConstructorExecutor resolve(final EvaluationContext context,
            final String typeName, final Class<?>[] argumentTypes) throws AccessException {
        try {
            return delegate.resolve(context, typeName, argumentTypes);
        } catch (AccessException ex) {
            Object variable = ((SpelHelper) context.lookupVariable(SpelHelper.CONTEXT_LOOKUP_KEY))
                .lookupImplicitConstructor(typeName + Arrays.toString(argumentTypes));
            if (variable instanceof Constructor<?>) {
                Constructor<?> constructor = (Constructor<?>) variable;
                return delegate.resolve(context, constructor.getDeclaringClass().getName(), argumentTypes);
            }
            return null;
        }
    }
}