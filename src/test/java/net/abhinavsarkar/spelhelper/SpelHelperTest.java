package net.abhinavsarkar.spelhelper;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class SpelHelperTest {

    @Test
    public void testRegisteredFunction() {
        Assert.assertEquals(
                Arrays.asList("abhinav", "mini", "dan"),
                new SpelHelper().evalExpression(
                        "#list('abhinav','mini','dan')", new Object(), List.class));
    }

    @Test
    public void testImplicitMethod() {
        Assert.assertEquals(
                Arrays.asList("abhinav", "dan", "mini"),
                new SpelHelper().evalExpression(
                        "#list('abhinav','mini','dan').sorted", new Object(), List.class));
    }

    public static final class ConstructorTest {
        @Override
        public boolean equals(final Object o) {
            return o instanceof ConstructorTest;
        }
    }

    @Test
    public void testImplicitConstructor() {
        Assert.assertEquals(
            new ConstructorTest(),
            new SpelHelper()
                .registerImplicitConstructorsFromClass(ConstructorTest.class)
                .evalExpression("new ConstructorTest()", new Object(), ConstructorTest.class));
    }

}
