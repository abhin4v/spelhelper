package net.abhinavsarkar.spelhelper;

import static java.util.Arrays.asList;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.expression.ConstructorResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.Assert;

public final class SpelHelper {

	public static final String CONTEXT_LOOKUP_KEY = SpelHelper.class.getName();

	private static final ExpressionParser PARSER = new SpelExpressionParser();
	private static final ThreadLocal<EvaluationContext> currentContext =
		new ThreadLocal<EvaluationContext>();

	private volatile EvaluationContext context;
	private final Set<Method> registeredFunctions = new HashSet<Method>();
	private final Map<String,Method> registeredMethods =
		new ConcurrentHashMap<String, Method>();
    private final Map<String,Constructor<?>> registeredConstructors =
    	new ConcurrentHashMap<String, Constructor<?>>();

    {
        registerFunctionsFromClass(ExtensionFunctions.class);
        registerImplicitMethodsFromClass(ImplicitMethods.class);
    }

	public SpelHelper registerImplicitMethodsFromClass(final Class<?> clazz) {
		for (Method method : filterMethods(clazz)) {
			registeredMethods.put(String.format(
					"%s.%s", method.getParameterTypes()[0].getName(), method.getName()),
					method);
		}
		return this;
	}

    public SpelHelper registerFunctionsFromClass(final Class<?> clazz) {
        registeredFunctions.addAll(filterMethods(clazz));
		context = null;
		return this;
	}

    public SpelHelper registerImplicitConstructorsFromClass(final Class<?> clazz) {
    	for (Constructor<?> constructor : asList(clazz.getConstructors())) {
    		registeredConstructors.put(
    				constructor.getDeclaringClass().getSimpleName()
                    + Arrays.toString(constructor.getParameterTypes()),
                    constructor);
    	}
        return this;
	}

    public <T> T evalExpression(final String expressionString,
			final Object rootElement, final Class<T> desiredType) {
    	EvaluationContext evaluationContext = getEvaluationContext(rootElement);
    	currentContext.set(evaluationContext);
		T value = evalExpression(expressionString, evaluationContext, desiredType);
		currentContext.set(null);

		return value;
	}

	public <T> T evalExpression(final String expressionString,
            final EvaluationContext evaluationContext, final Class<T> desiredType) {
	    return PARSER.parseExpression(expressionString)
                	.getValue(evaluationContext, desiredType);
    }

    public <T> T evalExpressions(final String[] expressionStrings,
			final Object rootElement, final Class<T> desiredType) {
        int length = expressionStrings.length;
        Assert.isTrue(length > 0,
                "expressionStrings should have length more than 0");
        for (int i = 0; i < length - 1; i++) {
            evalExpression(expressionStrings[i], rootElement, Object.class);
        }
		return evalExpression(expressionStrings[length - 1],
                rootElement, desiredType);
	}

	private EvaluationContext getEvaluationContext(final Object rootObject) {
		if (context == null) {
			synchronized (PARSER) {
				if (context == null) {
					StandardEvaluationContext newContext = new StandardEvaluationContext(rootObject);
					newContext.getMethodResolvers().add(new ImplicitMethodResolver());
					newContext.getPropertyAccessors().add(new ImplicitPropertyAccessor());
					newContext.setConstructorResolvers(
							asList((ConstructorResolver) new ImplicitConstructorResolver()));
                    for (Method method : registeredFunctions) {
                    	newContext.setVariable(method.getName(), method);
					}
                    newContext.setVariable(CONTEXT_LOOKUP_KEY, this);
                    context = newContext;
				}
			}
		}
		return context;
	}

	public Method lookupImplicitMethod(final String lookup) {
		Assert.notNull(lookup);
    	return registeredMethods.get(lookup);
    }

	public Constructor<?> lookupImplicitConstructor(final String lookup) {
		Assert.notNull(lookup);
		return registeredConstructors.get(lookup);
    }

	public static EvaluationContext getCurrentContext() {
		return currentContext.get();
	}

    private static List<Method> filterMethods(final Class<?> clazz) {
        List<Method> allowedMethods = new ArrayList<Method>();
        for (Method method : clazz.getMethods()) {
            int modifiers = method.getModifiers();
            if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers)
                    && !method.getReturnType().equals(Void.TYPE)
                    && method.getParameterTypes().length > 0) {
                allowedMethods.add(method);
            }
        }
        return allowedMethods;
    }

}
