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
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.Assert;

/**
 * SpelHelper provides additional functionalities to work with
 * [Spring Expression Language (SpEL)][1].
 *
 * The addition functionalities provided are:
 *
 * 1. Implicit methods
 * 2. Implicit properties
 * 3. Simplified extension functions
 * 4. Simplified constructors
 *
 * **Implicit Methods**
 *
 * Implicit methods allow one to registers methods with SpelHelper and attach
 * them to particular classes. After that, when that method is called on an
 * object of that particular class inside a SpEL expression, SpelHelper
 * redirects the method call to the registered method.
 *
 * Example: {@link ImplicitMethods#sorted(List)} method is automatically
 * registered by SpelHelper. The class that the method should be invoked for
 * is the type of the first parameter of the method. In this case, the class is
 * {@link List}.
 *
 * So when an expression like `"#list(1,4,2).sorted()"` is evaluated, the
 * {@link ImplicitMethods#sorted(List)} method is invoked with the list as its
 * first parameter and its return value is used in further evaluation of the
 * expression.
 *
 * See {@link SpelHelper#registerImplicitMethodsFromClass(Class)}.
 *
 * **Implicit Properties**
 *
 * Implicit properties allow one to treat no argument methods of an object
 * as properties of the object. SpelHelper intercepts the property resolution
 * of SpEL and if the property name is same as some no-arg method of the target
 * object then it invokes the method on the object and provides its return value
 * as the property value for further evaluation of the expression.
 *
 * Example: Using implicit properties, the example of implicit methods can be
 * written as: `"#list(1,4,2).sorted"` - dropping the parens - and it will return
 * the same value as the last example.
 *
 * Implicit property resolution considers both the actual methods of the object
 * and the implicit methods registered on the object's class.
 *
 * **Simplified extension functions**
 *
 * SpEL [allows][2] to register extension function on the context by providing a
 * name and a {@link Method} object. SpelHelper simplifies this by taking a class
 * and registering all the `public static` methods of the class which do not
 * have a `void` return type. The methods are registered by their simple name.
 *
 * Example: All the methods of {@link ExtensionFunctions} class are automatically
 * registered by SpelHelper. Hence the method {@link ExtensionFunctions#list(Object...)}
 * can be called from inside a SpEL expression using the function call syntax:
 * `"#list(1,2,3)`".
 *
 * See {@link SpelHelper#registerFunctionsFromClass(Class)}.
 *
 * **Simplified constructors**
 *
 * SpEL [allows][3] calling constructors from inside a SpEL expression using the
 * `new` operator. But they have to be called with their full name like:
 * `"new org.example.Foo('bar')"`. SpelHelper simplifies this by taking a class
 * and registering all its public constructors to the SpEL context by their
 * simple name.
 *
 * Example: After registering the `org.example.Foo` class with SpelHelper, its
 * constructor can be called from inside a SpEL expression by: `"new Foo('bar')"`.
 *
 * See {@link SpelHelper#registerConstructorsFromClass(Class)}.
 *
 * In addition to all the above functionalities, SpelHelper automatically registers
 * some extension functions and implicit methods which are always available in
 * the SpEL expressions evaluated through SpelHelper. See {@link ExtensionFunctions}
 * and {@link ImplicitMethods} for further details.
 *
 * [1]: http://static.springsource.org/spring/docs/3.0.x/spring-framework-reference/html/expressions.html
 * [2]: http://static.springsource.org/spring/docs/3.0.x/spring-framework-reference/html/expressions.html#expressions-ref-functions
 * [3]: http://static.springsource.org/spring/docs/3.0.x/spring-framework-reference/html/expressions.html#d0e11927
 *
 * @author Abhinav Sarkar _abhinav@abhinavsarkar.net_
 */
public final class SpelHelper {

    static final String CONTEXT_LOOKUP_KEY = SpelHelper.class.getName();

    private final ExpressionParser PARSER = new SpelExpressionParser();
    private static final ThreadLocal<EvaluationContext> CURRENT_CONTEXT =
        new ThreadLocal<EvaluationContext>();

    private final Set<Method> registeredFunctions = new HashSet<Method>();
    private final Map<String,Method> registeredMethods =
        new ConcurrentHashMap<String, Method>();
    private final Map<String,Constructor<?>> registeredConstructors =
        new ConcurrentHashMap<String, Constructor<?>>();

    /**
     * Creates an instance of SpelHelper.
     */
    public SpelHelper() {
        registerFunctionsFromClass(ExtensionFunctions.class);
        registerImplicitMethodsFromClass(ImplicitMethods.class);
    }

    /**
     * Registers the public static methods in the class `clazz` as implicit
     * methods for the class of the first parameter of the methods.
     *
     * Only registers the public static methods with non void return type and at
     * least one argument.
     * @see ImplicitMethods
     * @param clazz The class to register the methods from.
     * @return      The current instance of SpelHelper. This is for chaining
     * the methods calls.
     */
    public SpelHelper registerImplicitMethodsFromClass(final Class<?> clazz) {
        for (Method method : filterMethods(clazz)) {
            registeredMethods.put(String.format(
                    "%s.%s", method.getParameterTypes()[0].getName(), method.getName()),
                    method);
        }
        return this;
    }

    /**
     * Registers the public static methods in the class `clazz` as functions
     * which can be called from SpEL expressions.
     * The functions are registered with the simple name of the methods.
     *
     * Only registers the public static methods with non void return type.
     * @see ExtensionFunctions
     * @param clazz The class to register the functions from.
     * @return      The current instance of SpelHelper. This is for chaining
     * the methods calls.
     */
    public SpelHelper registerFunctionsFromClass(final Class<?> clazz) {
        registeredFunctions.addAll(filterFunctions(clazz));
        return this;
    }

    /**
     * Registers the public constructors of the class `clazz` so that they
     * can be called by their simple name from SpEL expressions.
     * @param clazz The class to register the constructors from.
     * @return      The current instance of SpelHelper. This is for chaining
     * the methods calls.
     */
    public SpelHelper registerConstructorsFromClass(final Class<?> clazz) {
        for (Constructor<?> constructor : asList(clazz.getConstructors())) {
            registeredConstructors.put(
                    constructor.getDeclaringClass().getSimpleName()
                        + Arrays.toString(constructor.getParameterTypes()),
                    constructor);
        }
        return this;
    }

    /**
     * Evaluates a SpEL expression `expressionString` in the context
     * of root element `rootElement` and gives back a result of type
     * `desiredType`.
     * @param <T>   The type of the result desired.
     * @param expressionString  The SpEL expression to evaluate.
     * @param rootElement   The root element in context of which the expression
     * is to be evaluated.
     * @param desiredType   The class of the result desired.
     * @return  The result of the evaluation of the expression.
     * @see ExpressionParser#parseExpression(String)
     * @see Expression#getValue(EvaluationContext, Class)
     */
    public <T> T evalExpression(final String expressionString,
            final Object rootElement, final Class<T> desiredType) {
        EvaluationContext evaluationContext = getEvaluationContext(rootElement);
        CURRENT_CONTEXT.set(evaluationContext);
        T value = evalExpression(expressionString, evaluationContext, desiredType);
        CURRENT_CONTEXT.set(null);
        return value;
    }

    /**
     * Evaluates a SpEL expression `expressionString` in the provided
     * context `evaluationContext` and gives back a result of type
     * `desiredType`.
     * @param <T>   The type of the result desired.
     * @param expressionString  The SpEL expression to evaluate.
     * @param evaluationContext The context in which the expression is to be evaluated.
     * @param desiredType   The class of the result desired.
     * @return  The result of the evaluation of the expression.
     * @see ExpressionParser#parseExpression(String)
     * @see Expression#getValue(EvaluationContext, Class)
     */
    public <T> T evalExpression(final String expressionString,
            final EvaluationContext evaluationContext, final Class<T> desiredType) {
        return PARSER.parseExpression(expressionString)
                    .getValue(evaluationContext, desiredType);
    }

    /**
     * Evaluates multiple SpEL expressions and returns the result of the last
     * expression.
     * @param <T>   The type of the result desired.
     * @param expressionStrings  The SpEL expressions to evaluate.
     * @param rootElement   The root element in context of which the expressions
     * are to be evaluated.
     * @param desiredType   The class of the result desired.
     * @return  The result of the evaluation of the last expression.
     * @see SpelHelper#evalExpression(String, EvaluationContext, Class)
     * @see SpelHelper#evalExpression(String, Object, Class)
     */
    public <T> T evalExpressions(final String[] expressionStrings,
            final Object rootElement, final Class<T> desiredType) {
        return evalExpressions(
                expressionStrings, getEvaluationContext(rootElement), desiredType);
    }

    /**
     * Evaluates multiple SpEL expressions and returns the result of the last
     * expression.
     * @param <T>   The type of the result desired.
     * @param expressionStrings  The SpEL expressions to evaluate.
     * @param evaluationContext The context in which the expression is to be evaluated.
     * @param desiredType   The class of the result desired.
     * @return  The result of the evaluation of the last expression.
     * @see SpelHelper#evalExpression(String, EvaluationContext, Class)
     * @see SpelHelper#evalExpression(String, Object, Class)
     */
    public <T> T evalExpressions(final String[] expressionStrings,
            final EvaluationContext evaluationContext, final Class<T> desiredType) {
        int length = expressionStrings.length;
        Assert.isTrue(length > 0,
                "expressionStrings should have length more than 0");
        for (int i = 0; i < length - 1; i++) {
            evalExpression(expressionStrings[i], evaluationContext, Object.class);
        }
        return evalExpression(expressionStrings[length - 1],
                evaluationContext, desiredType);
    }

    private EvaluationContext getEvaluationContext(final Object rootObject) {
        StandardEvaluationContext newContext = new StandardEvaluationContext(rootObject);
        newContext.getMethodResolvers().add(new ImplicitMethodResolver());
        newContext.getPropertyAccessors().add(new ImplicitPropertyAccessor());
        newContext.setConstructorResolvers(
                asList((ConstructorResolver) new ImplicitConstructorResolver()));
        for (Method method : registeredFunctions) {
            newContext.setVariable(method.getName(), method);
        }
        newContext.setVariable(CONTEXT_LOOKUP_KEY, this);
        return newContext;
    }

    /**
     * Looks up an implicit method registered with this instance.
     * @param lookup    key to lookup which should be of form:
     * `method.getParameterTypes()[0].getName() + "." + method.getName()`
     * @return  The registered method if found, else null.
     */
    public Method lookupImplicitMethod(final String lookup) {
        Assert.notNull(lookup);
        return registeredMethods.get(lookup);
    }

    /**
     * Looks up an implicit constructor registered with this instance.
     * @param lookup    key to lookup which should be of form:
     * `constructor.getDeclaringClass().getSimpleName()`
     * `+ Arrays.toString(constructor.getParameterTypes())`
     * @return  The registered constructor if found, else null.
     */
    public Constructor<?> lookupImplicitConstructor(final String lookup) {
        Assert.notNull(lookup);
        return registeredConstructors.get(lookup);
    }

    /**
     * Returns the current evaluation context. Null if there is no context.
     * @return  The current evaluation context.
     */
    public static EvaluationContext getCurrentContext() {
        return CURRENT_CONTEXT.get();
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

    private static List<Method> filterFunctions(final Class<?> clazz) {
        List<Method> allowedMethods = new ArrayList<Method>();
        for (Method method : clazz.getMethods()) {
            int modifiers = method.getModifiers();
            if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers)
                    && !method.getReturnType().equals(Void.TYPE)) {
                allowedMethods.add(method);
            }
        }
        return allowedMethods;
    }

}
