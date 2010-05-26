package net.abhinavsarkar.spelhelper;

import java.text.MessageFormat;

import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.PropertyAccessor;

public abstract class ReadOnlyGenericPropertyAccessor implements
        PropertyAccessor {

    public final boolean canWrite(final EvaluationContext context,
            final Object target, final String name) throws AccessException {
        return false;
    }

    @SuppressWarnings("unchecked")
    public final Class[] getSpecificTargetClasses() {
        return null;
    }

    public final void write(final EvaluationContext context, final Object target,
            final String name, final Object newValue) throws AccessException {
        throw new AccessException(MessageFormat.format(
                "Cannot write property: {0} of target: {1}", name, target));
    }

}