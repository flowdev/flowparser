package org.flowdev.flowparser.util;

import java.util.List;

import static java.util.Arrays.asList;

public abstract class FormatUtil {
    private static final String[] ALLOWED_FORMATS = {"adoc", "java", "wiki", "go"};

    /**
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
     * @return the allowed output formats as a nicely formatted string.
     */
    public static String formatsAsString() {
        return String.join(", ", ALLOWED_FORMATS);
    }

    public static List<String> formatsAsList() {
        return asList(ALLOWED_FORMATS);
    }

    /**
     * @return the length of the ALLOWED_FORMATS array.
     */
    public static int formatCount() {
        return ALLOWED_FORMATS.length;
    }
}
