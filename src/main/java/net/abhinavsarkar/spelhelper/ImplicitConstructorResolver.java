/**
 *
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