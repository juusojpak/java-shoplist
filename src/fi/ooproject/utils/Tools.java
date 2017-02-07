package fi.ooproject.utils;

/**
 * Utility methods.
 *
 * @author Juuso Pakarinen
 * @version 2016.1110
 * @since 1.8
 */
public class Tools {

    /**
     * Returns whether given string is empty.
     *
     * Returns whether given string is null, is shorter than 1 or contains
     * only tab and/or whitespace characters.
     *
     * @param s given string.
     * @return whether given string is null, is shorter than 1 or contains
     *         only tab and/or whitespace characters.
     */
    public static boolean isEmpty(String s) {

        boolean empty = true;

        if (s.length() > 0 && s != null) {
            for (int i = 0; i < s.length(); i++) {
                if (s.charAt(i) != ' ' && s.charAt(i) != '\t'
                        && s.charAt(i) != '\n') {
                    empty = false;
                }
            }
        }

        return empty;
    }
}
