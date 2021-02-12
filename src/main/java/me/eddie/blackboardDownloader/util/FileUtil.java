package me.eddie.blackboardDownloader.util;

public class FileUtil {
    /*from ww w  .j  av a 2s.  co  m
            * list of characters not allowed in filenames
     */
    public static final char INVALID_CHARS[] = { '\\', '/', ':', '*', '?', '"', '<', '>', '|', '[', ']', '\'', ';',
            '=', ',', '\'', '\n', '\r' };
    private static final char SANITIZED_CHAR = '_';

    /**
     * Given an input, return a sanitized form of the input suitable for use as
     * a file/directory name
     *
     * @param filename the filename to sanitize.
     * @return a sanitized version of the input
     */
    public static String sanitizeFileName(String filename) {
        return sanitizeFileName(filename, SANITIZED_CHAR);
    }

    public static String sanitizeFileName(String filename, char substitute) {

        for (int i = 0; i < INVALID_CHARS.length; i++) {
            if (-1 != filename.indexOf(INVALID_CHARS[i])) {
                filename = filename.replace(INVALID_CHARS[i], substitute);
            }
        }

        return filename;
    }
}
