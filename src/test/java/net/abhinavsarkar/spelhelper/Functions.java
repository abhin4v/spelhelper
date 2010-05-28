package net.abhinavsarkar.spelhelper;

public final class Functions {

    public static String test(String str) {
        return str;
    }

    static String testNonPublic(String str) {
        return str;
    }

    public String testNonStatic(String str) {
        return str;
    }

    public static void testVoid(String str) {
        return;
    }

    public static String testNoArg() {
        return "a";
    }

    public static String testContext(String str) {
        if (SpelHelper.getCurrentContext() == null)
            throw new AssertionError();
        return str;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Functions;
    }
}
