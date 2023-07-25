package scanner;

public enum TokenType {
    // The code snippet is defining an enumeration called `TokenType`. Each value in the enumeration
    // represents a different type of token that can be encountered in a scanner.
    IDENTIFIER,
    INTEGER,
    STRING,
    OPERATOR,
    DELETE,   
    LEFT_PARENTHESES,
    RIGHT_PARENTHESES,
    SEMICOLON,
    COMMA,
    RESERVED;

    /**
     * Checks if the token type is a keyword (a reserved word).
     *
     * @return true if the token type is a keyword, false otherwise.
     */
    public boolean isKeyword() {
        return this == DELETE || this == RESERVED;
    }

    /**
     * Checks if the token type is a symbol (an operator or punctuation symbol).
     *
     * @return true if the token type is a symbol, false otherwise.
     */
    public boolean isSymbol() {
        return this == OPERATOR || this == LEFT_PARENTHESES || this == RIGHT_PARENTHESES || this == SEMICOLON || this == COMMA;
    }
}
