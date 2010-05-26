/**
 *
 */
package net.abhinavsarkar.spelhelper;

import java.text.MessageFormat;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.MethodExecutor;
import org.springframework.expression.MethodResolver;
import org.springframework.expression.TypedValue;
import org.springframework.util.Assert;

final class ImplicitPropertyAccessor extends ReadOnlyGenericPropertyAccessor {

    private static final ConcurrentHashMap<String, MethodExecutor> cache =
        new ConcurrentHashMap<String, MethodExecutor>();

    public boolean canRead(final EvaluationContext context,
            final Object target, final String name)
            throws AccessException {
        Assert.notNull(target, "target is null");
        String cacheKey = target.getClass().getName() + "." + name;
        if (cache.containsKey(cacheKey)) {
            return cache.get(cacheKey) != null;
        }

        for (MethodResolver mr : context.getMethodResolvers()) {
            MethodExecutor me = mr.resolve(context, target, name, new Class[0]);
            if (me != null) {
                cache.putIfAbsent(cacheKey, me);
                return true;
            }
        }

        cache.putIfAbsent(cacheKey, null);
        return false;
    }

    public TypedValue read(final EvaluationContext context,
            final Object target, final String name)
            throws AccessException {
        if (canRead(context, target, name)) {
            String cacheKey = target.getClass().getName() + "." + name;
            return cache.get(cacheKey).execute(context, target, new Object[0]);
        }
        throw new AccessException(MessageFormat.format(
                "Cannot read property: {0} of target: {1}", name, target));
    }

}