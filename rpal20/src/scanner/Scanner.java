package scanner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Scanner {
    private BufferedReader buffer;
    private String extraCharRead;
   // The line `private final List<String> reservedIdentifiers = Arrays.asList("let", "in", "within",
   // "fn", "where", "aug", "or", "not", "gr", "ge", "ls", "le", "eq", "ne", "true", "false", "nil",
   // "dummy", "rec", "and");` is declaring and initializing a list of reserved identifiers. These
   // reserved identifiers are keywords or special words in the programming language that have a
   // specific meaning and cannot be used as regular identifiers (variable names, function names,
   // etc.). The list is created using the `Arrays.asList()` method, which takes a variable number of
   // arguments and returns a fixed-size list containing those elements.
    private final List<String> reservedIdentifiers = Arrays.asList("let", "in", "within", "fn", "where", "aug", "or",
                                                                    "not", "gr", "ge", "ls", "le", "eq", "ne", "true",
                                                                    "false", "nil", "dummy", "rec", "and");
    private int sourceLineNumber;

    public Scanner(String inputFile) throws IOException {
        sourceLineNumber = 1;
        buffer = new BufferedReader(new InputStreamReader(new FileInputStream(new File(inputFile))));
    }

    public Token readNextToken() {
        Token nextToken = null;
        String nextChar;
        if (extraCharRead != null) {
            nextChar = extraCharRead;
            extraCharRead = null;
        } else
            nextChar = readNextChar();
        if (nextChar != null)
            nextToken = buildToken(nextChar);
        return nextToken;
    }

    private String readNextChar() {
        String nextChar = null;
        try {
            int c = buffer.read();
            if (c != -1) {
                nextChar = Character.toString((char) c);
                if (nextChar.equals("\n")) sourceLineNumber++;
            } else
                buffer.close();
        } catch (IOException e) {
        }
        return nextChar;
    }

/**
 * The function `buildToken` takes a character as input and returns a token based on the type of
 * character.
 * 
 * @param currentChar The `currentChar` parameter is a string that represents the current character
 * being processed. It is used to determine the type of token to build based on the character's
 * properties.
 * @return The method `buildToken` returns the `nextToken` object.
 */
    private Token buildToken(String currentChar) {
        Token nextToken = null;
        if (LexicalRegexPatterns.LetterPattern.matcher(currentChar).matches()) {
            nextToken = buildIdentifierToken(currentChar);
        } else if (LexicalRegexPatterns.DigitPattern.matcher(currentChar).matches()) {
            nextToken = buildIntegerToken(currentChar);
        } else if (LexicalRegexPatterns.OpSymbolPattern.matcher(currentChar).matches()) {
            nextToken = buildOperatorToken(currentChar);
        } else if (currentChar.equals("\'")) {
            nextToken = buildStringToken(currentChar);
        } else if (LexicalRegexPatterns.SpacePattern.matcher(currentChar).matches()) {
            nextToken = buildSpaceToken(currentChar);
        } else if (LexicalRegexPatterns.PunctuationPattern.matcher(currentChar).matches()) {
            nextToken = buildPunctuationToken(currentChar);
        }
        return nextToken;
    }

    private Token buildIdentifierToken(String currentChar) {
        Token identifierToken = new Token();
        identifierToken.setType(TokenType.IDENTIFIER);
        identifierToken.setSourceLineNumber(sourceLineNumber);
        StringBuilder sBuilder = new StringBuilder(currentChar);

        String nextChar = readNextChar();
        while (nextChar != null) {
            if (LexicalRegexPatterns.IdentifierPattern.matcher(nextChar).matches()) {
                sBuilder.append(nextChar);
                nextChar = readNextChar();
            } else {
                extraCharRead = nextChar;
                break;
            }
        }

        String value = sBuilder.toString();
        if (reservedIdentifiers.contains(value))
            identifierToken.setType(TokenType.RESERVED);

        identifierToken.setValue(value);
        return identifierToken;
    }

    /**
     * The function builds an integer token by reading consecutive digits from the input string.
     * 
     * @param currentChar The `currentChar` parameter is a string that represents the current character
     * being processed by the `buildIntegerToken` method.
     * @return The method is returning an instance of the Token class, specifically a Token
     * representing an integer value.
     */
    private Token buildIntegerToken(String currentChar) {
        Token integerToken = new Token();
        integerToken.setType(TokenType.INTEGER);
        integerToken.setSourceLineNumber(sourceLineNumber);
        StringBuilder sBuilder = new StringBuilder(currentChar);

        String nextChar = readNextChar();
        while (nextChar != null) {
            if (LexicalRegexPatterns.DigitPattern.matcher(nextChar).matches()) {
                sBuilder.append(nextChar);
                nextChar = readNextChar();
            } else {
                extraCharRead = nextChar;
                break;
            }
        }

        integerToken.setValue(sBuilder.toString());
        return integerToken;
    }

/**
 * The function builds an operator token by reading characters and appending them to a StringBuilder
 * until a non-operator character is encountered.
 * 
 * @param currentChar The `currentChar` parameter is a string representing the current character being
 * processed by the `buildOperatorToken` method.
 * @return The method is returning a Token object with the type set to TokenType.OPERATOR, the source
 * line number set to the current source line number, and the value set to the concatenated string of
 * operator symbols.
 */
    private Token buildOperatorToken(String currentChar) {
        Token opSymbolToken = new Token();
        opSymbolToken.setType(TokenType.OPERATOR);
        opSymbolToken.setSourceLineNumber(sourceLineNumber);
        StringBuilder sBuilder = new StringBuilder(currentChar);

        String nextChar = readNextChar();

        if (currentChar.equals("/") && nextChar.equals("/"))
            return buildCommentToken(currentChar + nextChar);

        while (nextChar != null) {
            if (LexicalRegexPatterns.OpSymbolPattern.matcher(nextChar).matches()) {
                sBuilder.append(nextChar);
                nextChar = readNextChar();
            } else {
                extraCharRead = nextChar;
                break;
            }
        }

        opSymbolToken.setValue(sBuilder.toString());
        return opSymbolToken;
    }

/**
 * The function `buildStringToken` reads characters from a source and builds a string token until it
 * encounters a closing single quote character.
 * 
 * @param currentChar The `currentChar` parameter is a string that represents the current character
 * being processed in the code.
 * @return The method is returning a Token object.
 */
    private Token buildStringToken(String currentChar) {
        Token stringToken = new Token();
        stringToken.setType(TokenType.STRING);
        stringToken.setSourceLineNumber(sourceLineNumber);
        StringBuilder sBuilder = new StringBuilder();

        String nextChar = readNextChar();
        while (nextChar != null) {
            if (nextChar.equals("\'")) {
                stringToken.setValue(sBuilder.toString());
                return stringToken;
            } else if (LexicalRegexPatterns.StringPattern.matcher(nextChar).matches()) {
                sBuilder.append(nextChar);
                nextChar = readNextChar();
            }
        }

        return null;
    }

/**
 * The function builds a space token by reading consecutive space characters from the input.
 * 
 * @param currentChar The `currentChar` parameter is a string that represents the current character
 * being processed in the code.
 * @return The method is returning a Token object.
 */
    private Token buildSpaceToken(String currentChar) {
        Token deleteToken = new Token();
        deleteToken.setType(TokenType.DELETE);
        deleteToken.setSourceLineNumber(sourceLineNumber);
        StringBuilder sBuilder = new StringBuilder(currentChar);

        String nextChar = readNextChar();
        while (nextChar != null) {
            if (LexicalRegexPatterns.SpacePattern.matcher(nextChar).matches()) {
                sBuilder.append(nextChar);
                nextChar = readNextChar();
            } else {
                extraCharRead = nextChar;
                break;
            }
        }

        deleteToken.setValue(sBuilder.toString());
        return deleteToken;
    }
/**
 * The function builds a comment token by reading characters until it encounters a newline character or
 * a character that does not match the comment pattern.
 * 
 * @param currentChar The `currentChar` parameter is a string that represents the current character
 * being processed in the code.
 * @return The method is returning a Token object.
 */

    private Token buildCommentToken(String currentChar) {
        Token commentToken = new Token();
        commentToken.setType(TokenType.DELETE);
        commentToken.setSourceLineNumber(sourceLineNumber);
        StringBuilder sBuilder = new StringBuilder(currentChar);

        String nextChar = readNextChar();
        while (nextChar != null) {
            if (LexicalRegexPatterns.CommentPattern.matcher(nextChar).matches()) {
                sBuilder.append(nextChar);
                nextChar = readNextChar();
            } else if (nextChar.equals("\n"))
                break;
        }

        commentToken.setValue(sBuilder.toString());
        return commentToken;
    }

  /**
   * The function builds a punctuation token based on the current character and sets its type
   * accordingly.
   * 
   * @param currentChar The `currentChar` parameter is a string that represents the current character
   * being processed.
   * @return The method is returning a Token object.
   */
    private Token buildPunctuationToken(String currentChar) {
        Token punctuationToken = new Token();
        punctuationToken.setSourceLineNumber(sourceLineNumber);
        punctuationToken.setValue(currentChar);
        if (currentChar.equals("("))
            punctuationToken.setType(TokenType.L_PAREN);
        else if (currentChar.equals(")"))
            punctuationToken.setType(TokenType.R_PAREN);
        else if (currentChar.equals(";"))
            punctuationToken.setType(TokenType.SEMICOLON);
        else if (currentChar.equals(","))
            punctuationToken.setType(TokenType.COMMA);

        return punctuationToken;
    }

    /**
     * The function checks if a given token is a reserved identifier.
     * 
     * @param token The parameter "token" is of type Token. It represents a token in a programming
     * language, which typically consists of a type and a value. In this case, the token is being
     * checked to see if it is a reserved identifier.
     * @return The method isReservedIdentifier is returning a boolean value.
     */
    private boolean isReservedIdentifier(Token token) {
        // New function: To check if a given token is a reserved identifier
        return token.getType() == TokenType.IDENTIFIER && reservedIdentifiers.contains(token.getValue());
    }
}
