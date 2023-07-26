package scanner;

import java.util.regex.Pattern;

public class LexicalRegexPatterns {
    // Regular expression strings for different token types
    private static final String letterRegexString = "a-zA-Z";
    private static final String digitRegexString = "\\d";
    private static final String spaceRegexString = "[\\s\\t\\n]";
    private static final String punctuationRegexString = "();,";
    private static final String opSymbolRegexString = "+-/~:=|!#%_{}\"*<>.&$^\\[\\]?@";
    private static final String opSymbolToEscapeString = "([*<>.&$^?])";

    // Compiled patterns for each token type
    public static final Pattern LetterPattern = Pattern.compile("[" + letterRegexString + "]");
    public static final Pattern IdentifierPattern = Pattern.compile("[" + letterRegexString + digitRegexString + "_]");
    public static final Pattern DigitPattern = Pattern.compile(digitRegexString);
    public static final Pattern PunctuationPattern = Pattern.compile("[" + punctuationRegexString + "]");
    public static final String opSymbolRegex = "[" + escapeMetaChars(opSymbolRegexString, opSymbolToEscapeString) + "]";
    public static final Pattern OpSymbolPattern = Pattern.compile(opSymbolRegex);
    public static final Pattern StringPattern = Pattern.compile("[ \\t\\n\\\\" + punctuationRegexString + letterRegexString + digitRegexString + escapeMetaChars(opSymbolRegexString, opSymbolToEscapeString) + "]");
    public static final Pattern SpacePattern = Pattern.compile(spaceRegexString);
    public static final Pattern CommentPattern = Pattern.compile("[ \\t\\'\\\\ \\r" + punctuationRegexString + letterRegexString + digitRegexString + escapeMetaChars(opSymbolRegexString, opSymbolToEscapeString) + "]");

    /**
     * Escape special meta characters in the input string.
     *
     * @param inputString     The input string to escape characters from.
     * @param charsToEscape   The special characters that need to be escaped.
     * @return                The input string with escaped characters.
     */
    private static String escapeMetaChars(String inputString, String charsToEscape) {
        return inputString.replaceAll(charsToEscape, "\\\\\\\\$1");
    }

    /**
     * Add a new pattern for a specific token type.
     *
     * @param patternRegex   The regular expression for the new token type.
     * @return               The compiled pattern for the new token type.
     */
    public static Pattern addPattern(String patternRegex) {
        return Pattern.compile(patternRegex);
    }
}
