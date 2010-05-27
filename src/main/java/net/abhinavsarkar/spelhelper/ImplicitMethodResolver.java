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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.MethodExecutor;
import org.springframework.expression.MethodResolver;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.support.ReflectiveMethodResolver;

final class ImplicitMethodResolver implements MethodResolver {

    private static final ConcurrentHashMap<String, MethodExecutor> cache =
        new ConcurrentHashMap<String, MethodExecutor>();

    private static final MethodExecutor NULL_ME = new MethodExecutor() {
        public TypedValue execute(final EvaluationContext context, final Object target,
                final Object... arguments) throws AccessException {
            return null;
        }
    };

    private static final class ImplicitMethodExecutor implements
            MethodExecutor {
        private final MethodExecutor executor;

        private ImplicitMethodExecutor(final MethodExecutor executor) {
            this.executor = executor;
        }

        public TypedValue execute(final EvaluationContext context, final Object target,
                final Object... arguments) throws AccessException {
            Object[] modifiedArguments = new Object[arguments.length + 1];
            modifiedArguments[0] = target;
            System.arraycopy(arguments, 0, modifiedArguments, 1, arguments.length);
            return executor.execute(context, null, modifiedArguments);
        }
    }

    public MethodExecutor resolve(final EvaluationContext context,
            final Object targetObject, final String name, final Class<?>[] argumentTypes)
            throws AccessException {
        if (targetObject == null) {
            return null;
        }
        Class<?> type = targetObject.getClass();
        String cacheKey = type.getName() + "." + name;
        if (cache.containsKey(cacheKey)) {
            MethodExecutor executor = cache.get(cacheKey);
            return executor == NULL_ME ? null : executor;
        }

        Method method = lookupMethod(context, type, name);
        if (method != null) {
            int modifiers = method.getModifiers();
            if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers)) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                Class<?> firstParameterType = parameterTypes[0];
                if (parameterTypes.length > 0
                        && firstParameterType.isAssignableFrom(type)) {

                    Class<?>[] modifiedArgumentTypes = new Class[argumentTypes.length + 1];
                    modifiedArgumentTypes[0] = firstParameterType;
                    System.arraycopy(argumentTypes, 0, modifiedArgumentTypes,
                            1, argumentTypes.length);
                    MethodExecutor executor = new ReflectiveMethodResolver()
                            .resolve(context, method.getDeclaringClass(), name,
                                    modifiedArgumentTypes);
                    MethodExecutor wrappedExecutor = executor == null ? null
                            : new ImplicitMethodExecutor(executor);
                    cache.putIfAbsent(cacheKey, wrappedExecutor);
                    return wrappedExecutor;
                }
            }
        }
        cache.putIfAbsent(cacheKey, NULL_ME);
        return null;
    }

    private static Method lookupMethod(final EvaluationContext context,
            final Class<?> type, final String name) {
        for (Class<?> clazz : InheritenceUtil.getInheritance(type)) {
            Object variable = ((SpelHelper) context.lookupVariable(SpelHelper.CONTEXT_LOOKUP_KEY))
                .lookupImplicitMethod(clazz.getName() + "." + name);
            if (variable instanceof Method) {
                return (Method) variable;
            }
        }
        return null;
    }

}