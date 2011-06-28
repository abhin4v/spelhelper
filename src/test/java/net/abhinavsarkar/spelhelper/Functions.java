package net.abhinavsarkar.spelhelper;

public final class Functions {

    public static String test(final String str) {
        return str;
    }

    static String testNonPublic(final String str) {
        return str;
    }

    public String testNonStatic(final String str) {
        return str;
    }

    public static void testVoid(final String str) {
        return;
    }

    public static String testNoArg() {
        return "a";
    }

    public static String testContext(final String str) {
        if (SpelHelper.getCurrentContext() == null) {
            throw new AssertionError();
        }
        return str;
    }

    @Override
    public boolean equals(final Object o) {
        return o instanceof Functions;
    }
}
