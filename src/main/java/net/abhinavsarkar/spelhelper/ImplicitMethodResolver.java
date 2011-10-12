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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.MethodExecutor;
import org.springframework.expression.MethodResolver;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.support.ReflectiveMethodResolver;

final class ImplicitMethodResolver implements MethodResolver {

    private static final ConcurrentHashMap<String, MethodExecutor> CACHE =
        new ConcurrentHashMap<String, MethodExecutor>();

    private final ReflectiveMethodResolver delegate = new ReflectiveMethodResolver();

    private static final MethodExecutor NULL_ME = new MethodExecutor() {
        public TypedValue execute(final EvaluationContext context, final Object target,
                final Object... arguments) throws AccessException {
            return null;
        }
    };

    private static final class ImplicitMethodExecutor implements
            MethodExecutor {
        private final MethodExecutor executor;

        public ImplicitMethodExecutor(final MethodExecutor executor) {
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

    @Override
    public MethodExecutor resolve(
            final EvaluationContext context, final Object targetObject,
            final String name, final List<TypeDescriptor> argumentTypes)
    throws AccessException {
        if (targetObject == null) {
            return null;
        }
        Class<?> type = targetObject.getClass();
        String cacheKey = type.getName() + "." + name;
        if (CACHE.containsKey(cacheKey)) {
            MethodExecutor executor = CACHE.get(cacheKey);
            return executor == NULL_ME ? null : executor;
        }

        Method method = lookupMethod(context, type, name);
        if (method != null) {
            int modifiers = method.getModifiers();
            if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers)) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                Class<?> firstParamType = parameterTypes[0];
                if (parameterTypes.length > 0
                        && firstParamType.isAssignableFrom(type)) {
                    List<TypeDescriptor> newArgumentTypes = new ArrayList<TypeDescriptor>();
                    newArgumentTypes.add(TypeDescriptor.valueOf(firstParamType));
                    newArgumentTypes.addAll(argumentTypes);

                    MethodExecutor executor =
                        delegate.resolve(context, method.getDeclaringClass(),
                                name, newArgumentTypes);
                    MethodExecutor wrappedExecutor = executor == null ? null
                            : new ImplicitMethodExecutor(executor);
                    if (wrappedExecutor == null) {
                        CACHE.putIfAbsent(cacheKey, NULL_ME);
                    }
                    return wrappedExecutor;
                }
            }
        }
        CACHE.putIfAbsent(cacheKey, NULL_ME);
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