package com.source.Layout;

/**
 *
 * @author SAMUN
 */
public class LayoutUtils {

    private static LayoutUtils instance;                                              // Static LayoutUtils instance, so that it never expires

    private final String THREE_CHAR_MATH_TERMS[] = {"sin", "cos", "tan", "log"};      // All available Math Functions that is 3 char long.
    private final String TWO_CHAR_MATH_TERMS[] = {"ln"};                              // All available Math Functions that us 2 char long.

    /**
     * Private Constructor so no external class can create new instance.
     */
    private LayoutUtils() {

    }


    /**
     * Returns the existing instance. If no instance has created before, then
     * creates the instance and returns. Ensures no other instance is created
     * but one on whole run-time. The created instance is static so it will not
     * expire.
     *
     * @return self-class instance.
     */
    public static LayoutUtils getInstance() {
        if (instance == null) {
            instance = new LayoutUtils();
        }

        return instance;
    }


    /**
     * Returns a String Array of math functions based on the length. If the
     * length is 3 returns <code>THREE_CHAR_MATH_TERMS</code>. If the length is
     * 2 returns <code>TWO_CHAR_MATH_TERMS</code>.
     *
     * @param length length of the needed math functions
     * @return String Array of math functions
     */
    public String[] getMathFunctions(int length) {
        switch (length) {
            case 3:
                return THREE_CHAR_MATH_TERMS;
            case 2:
                return TWO_CHAR_MATH_TERMS;
            default:
                return new String[0];
        }
    }


    /**
     * Checks and returns length of Math Functions. If the given expression
     * doesn't match to any, then returns 0.
     *
     * @param exp expression that need to be checked
     * @return length
     */
    public int checkMathTermLength(String exp) {
        for (String str : THREE_CHAR_MATH_TERMS) {
            if (str.equals(exp)) {
                return 3;
            }
        }

        for (String str : TWO_CHAR_MATH_TERMS) {
            if (str.equals(exp)) {
                return 2;
            }
        }

        return 0;
    }


}
