package org.flowdev.flowparser.util;

public abstract class FormatUtil {
    private static final String[] ALLOWED_FORMATS = {"adoc", "java", "wiki"};

    /**
     * This method is intentionally public static so it can be used by the main class.
     *
     * @param format the format to look for in the ALLOWED_FORMATS array.
     * @return the index of the given format in the ALLOWED_FORMATS array.
     */
    public static int formatIndex(String format) {
        for (int i = 0; i < ALLOWED_FORMATS.length; i++) {
            if (ALLOWED_FORMATS[i].equals(format)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * This method is intentionally public static so it can be used by the main class.
     *
     * @return the allowed output formats as a nicely formatted string.
     */
    public static String allowedFormats() {
        return String.join(", ", ALLOWED_FORMATS);
    }

    public static int formatCount() {
        return ALLOWED_FORMATS.length;
    }
}
