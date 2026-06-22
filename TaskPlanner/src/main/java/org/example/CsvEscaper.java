package org.example;

/**
 * Quotes CSV fields per RFC 4180 so that values containing the separator,
 * a double quote, or a line break survive a write/read round-trip through
 * the commons-csv reader. Fields without special characters are unchanged.
 */
public final class CsvEscaper {

    private CsvEscaper() {
    }

    public static String escape(String field) {
        if (field == null) {
            return "";
        }
        boolean needsQuoting = field.indexOf(CsvConstants.SEPARATOR) >= 0
                || field.indexOf('"') >= 0
                || field.indexOf('\n') >= 0
                || field.indexOf('\r') >= 0;
        if (!needsQuoting) {
            return field;
        }
        return '"' + field.replace("\"", "\"\"") + '"';
    }
}
