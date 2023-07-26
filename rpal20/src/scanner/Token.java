package scanner;

public class Token {
    // The code snippet is defining the instance variables of the `Token` class.
    private TokenType type;          // The type of the token, which represents its category (e.g., identifier, operator symbol).
    private String value;            // The actual value of the token (e.g., the name of an identifier, the symbol of an operator).
    private int sourceLineNumber;    // The line number in the source code where the token was found.

    /**
     * Get the type of the token.
     *
     * @return The TokenType representing the type of the token.
     */
    public TokenType getType() {
        return type;
    }
    
    /**
     * The function sets the type of a token.
     * 
     * @param type The "type" parameter is of type TokenType.
     */
    public void setType(TokenType type) {
        this.type = type;
    }

    /**
     * Get the value of the token.
     *
     * @return The value of the token as a String.
     */
    public String getValue() {
        return value;
    }

    /**
     * Set the value of the token.
     *
     * @param value The value of the token as a String.
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Get the line number in the source code where the token was found.
     *
     * @return The source line number of the token.
     */
    public int getSourceLineNumber() {
        return sourceLineNumber;
    }

    /**
     * Set the line number in the source code where the token was found.
     *
     * @param sourceLineNumber The source line number of the token.
     */
    public void setSourceLineNumber(int sourceLineNumber) {
        this.sourceLineNumber = sourceLineNumber;
    }

    /**
     * Returns a String representation of the Token object.
     * The String includes the type, value, and source line number of the token.
     *
     * @return A formatted String representing the Token.
     */
    @Override
    public String toString() {
        return "Token{" +
                "type=" + type +
                ", value='" + value + '\'' +
                ", sourceLineNumber=" + sourceLineNumber +
                '}';
    }

    /**
     * Checks if the token represents an identifier.
     *
     * @return true if the token is an identifier, false otherwise.
     */
    public boolean isIdentifier() {
        return type == TokenType.IDENTIFIER;
    }

    /**
     * Checks if the token represents an operator symbol.
     *
     * @return true if the token is an operator symbol, false otherwise.
     */
    public boolean isOperator() {
        return type == TokenType.OPERATOR;
    }
}