package scanner;

import java.util.regex.Pattern;

public class LexicalRegexPatterns {
    // Regular expression character classes for different types of tokens
    private static final String LETTER = "a-zA-Z";                                 // Letters (uppercase and lowercase)
    private static final String DIGIT = "\\d";                                     // Digits
    private static final String OPERATOR_SYMBOL = "+-*<>&.@/:=~|$!#%^_\\[\\]{}\"'?"; // Operator symbols
    private static final String PUNCTUATION = "();,";                              // Punctuation symbols
    private static final String SPACES = "\\s";                                    // Whitespace characters (spaces, tabs, newlines)

    // Patterns for different token types
    public static final String opSymbolRegex = "[" + escapeMetaChars(OPERATOR_SYMBOL) + "]";                         // Escapes special characters in operator symbols
    public static final Pattern OpSymbolPattern = Pattern.compile(opSymbolRegex);                                    // Matches operator symbols
    public static final Pattern LetterPattern = Pattern.compile("[" + LETTER + "]");                                 // Matches single letters
    public static final Pattern IdentifierPattern = Pattern.compile("[" + LETTER + DIGIT + "_]");                    // Matches identifiers (letters, digits, underscores)
    public static final Pattern StringPattern = Pattern.compile("[ \t\n\\" + PUNCTUATION + LETTER + DIGIT + escapeMetaChars(OPERATOR_SYMBOL) + "]");  // Matches strings (including escape characters)
    public static final Pattern SpacePattern = Pattern.compile(SPACES);                                                // Matches whitespace characters
    public static final Pattern DigitPattern = Pattern.compile(DIGIT);                                                // Matches single digits
    public static final Pattern PunctuationPattern = Pattern.compile("[" + PUNCTUATION + "]");                       // Matches punctuation symbols
    public static final Pattern CommentPattern = Pattern.compile("[ \t'\\\\ \r" + PUNCTUATION + LETTER + DIGIT + escapeMetaChars(OPERATOR_SYMBOL) + "]");  // Matches comments (including escape characters)
    
    // Helper method to escape special characters in the OPERATOR_SYMBOL
    private static String escapeMetaChars(String inputString) {
        return inputString.replaceAll("([*<>.&$^?])", "\\\\\\\\$1");
    }
}
