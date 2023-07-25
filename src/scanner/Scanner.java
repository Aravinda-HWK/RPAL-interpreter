package scanner;

// These import statements are importing classes and interfaces from the `java.io` and `java.util`
// packages.
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class Scanner{
  private BufferedReader buffer;
  private String extraCharRead;
  private final List<String> reservedIdentifiers = Arrays.asList("let", "in", "within", "fn", "where", "aug", "or",
          "not", "gr", "ge", "ls", "le", "eq", "ne", "true", "false", "nil", "dummy", "rec", "and");
  private int sourceLineNumber;

  // The `public Scanner(String inputFile) throws IOException` is a constructor for the `Scanner`
  // class. It takes a `String` parameter `inputFile` which represents the path to the input file.
  public Scanner(String inputFile) throws IOException {
      sourceLineNumber = 1;
      File file=new File(inputFile);
      FileInputStream fileInputStream=new FileInputStream(file);
      InputStreamReader inputStreamReader=new InputStreamReader(fileInputStream);
      buffer = new BufferedReader(inputStreamReader);
  }

  /**
   * The function reads the next character and returns it as a token, or returns a previously read
   * character if available.
   * 
   * @return The method is returning a Token object.
   */
  public Token readNextToken() {
    if (extraCharRead != null) {
        String nextChar = extraCharRead;
        extraCharRead = null;
        return buildToken(nextChar);
    } else {
        return buildToken(readNextChar());
    }
  }

  /**
   * The function reads the next character from a buffer and returns it as a string, while also
   * incrementing a source line number if the character is a newline.
   * 
   * @return The method is returning a String value.
   */
  private String readNextChar() {
    try {
        int c = buffer.read();
        if (c != -1) {
            String nextChar = Character.toString((char) c);
            if (nextChar.equals("\n")) {
                sourceLineNumber++;
            }
            return nextChar;
        } else {
            buffer.close();
            return null;
        }
    } catch (IOException e) {
        // Handle the exception gracefully (optional)
        e.printStackTrace();
        return null;
    }
}

/**
 * The function builds a token based on the current character, using different patterns and conditions.
 * 
 * @param currentChar The `currentChar` parameter is a string that represents the current character
 * being processed in the code. It is used to determine the type of token to be built based on its
 * value.
 * @return The method is returning the nextToken, which is an instance of the Token class.
 */
private Token buildToken(String currentChar) {
  Token nextToken = null;

  switch (currentChar) {
      case "\'":
          nextToken = buildStringToken(currentChar);
          break;
      case "(":
      case ")":
      case ";":
      case ",":
          nextToken = buildPunctuationPattern(currentChar);
          break;
      default:
          if (LexicalRegexPatterns.LetterPattern.matcher(currentChar).matches()) {
              nextToken = buildIdentifierToken(currentChar);
          } else if (LexicalRegexPatterns.DigitPattern.matcher(currentChar).matches()) {
              nextToken = buildIntegerToken(currentChar);
          } else if (LexicalRegexPatterns.OpSymbolPattern.matcher(currentChar).matches()) {
              nextToken = buildOperatorToken(currentChar);
          } else if (LexicalRegexPatterns.SpacePattern.matcher(currentChar).matches()) {
              nextToken = buildSpaceToken(currentChar);
          }
          break;
  }

  return nextToken;
}
 
// The `buildIdentifierToken` method is responsible for constructing a token of type `IDENTIFIER` from
// the current character and subsequent characters that match the identifier pattern.
private Token buildIdentifierToken(String currentChar) {
  Token identifierToken = new Token();
  identifierToken.setType(TokenType.IDENTIFIER);
  identifierToken.setSourceLineNumber(sourceLineNumber);

  StringBuilder sBuilder = new StringBuilder(currentChar);

  for (String nextChar = readNextChar(); nextChar != null; nextChar = readNextChar()) {
      if (LexicalRegexPatterns.IdentifierPattern.matcher(nextChar).matches()) {
          sBuilder.append(nextChar);
      } else {
          extraCharRead = nextChar;
          break;
      }
  }

  String value = sBuilder.toString();
  if (reservedIdentifiers.contains(value)) {
      identifierToken.setType(TokenType.RESERVED);
  }

  identifierToken.setValue(value);
  return identifierToken;
}

/**
 * The function builds an integer token by reading consecutive digits from the input string.
 * 
 * @param currentChar The `currentChar` parameter in the `buildIntegerToken` method is a string that
 * represents the current character being processed.
 * @return The method is returning an instance of the Token class, specifically a Token representing an
 * integer value.
 */
private Token buildIntegerToken(String currentChar) {
  Token integerToken = new Token();
  integerToken.setType(TokenType.INTEGER);
  integerToken.setSourceLineNumber(sourceLineNumber);

  StringBuilder sBuilder = new StringBuilder(currentChar);

  for (String nextChar = readNextChar(); nextChar != null; nextChar = readNextChar()) {
      if (LexicalRegexPatterns.DigitPattern.matcher(nextChar).matches()) {
          sBuilder.append(nextChar);
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
 * @param currentChar The `currentChar` parameter in the `buildOperatorToken` method is a String that
 * represents the current character being processed while building an operator token.
 * @return The method is returning a Token object.
 */
private Token buildOperatorToken(String currentChar) {
  Token opSymbolToken = new Token();
  opSymbolToken.setType(TokenType.OPERATOR);
  opSymbolToken.setSourceLineNumber(sourceLineNumber);

  StringBuilder sBuilder = new StringBuilder(currentChar);

  String nextChar;
  for (nextChar = readNextChar(); nextChar != null; nextChar = readNextChar()) {
      if (currentChar.equals("/") && nextChar.equals("/")) {
          return buildCommentToken(currentChar + nextChar);
      }

      if (LexicalRegexPatterns.OpSymbolPattern.matcher(nextChar).matches()) {
          sBuilder.append(nextChar);
      } else {
          extraCharRead = nextChar;
          break;
      }
  }

  opSymbolToken.setValue(sBuilder.toString());
  return opSymbolToken;
}
/**
 * The function builds a string token by reading characters until it encounters a closing single quote,
 * and returns the token with the collected characters as its value.
 * 
 * @param currentChar The `currentChar` parameter is a string that represents the current character
 * being processed in the code.
 * @return The method is returning a Token object.
 */
private Token buildStringToken(String currentChar) {
  Token stringToken = new Token();
  stringToken.setType(TokenType.STRING);
  stringToken.setSourceLineNumber(sourceLineNumber);
  StringBuilder sBuilder = new StringBuilder("");

  for (String nextChar = readNextChar(); nextChar != null; nextChar = readNextChar()) {
      if (nextChar.equals("\'")) {
          stringToken.setValue(sBuilder.toString());
          return stringToken;
      } else if (LexicalRegexPatterns.StringPattern.matcher(nextChar).matches()) {
          sBuilder.append(nextChar);
      }
  }

  return null;
}

  
/**
 * The function builds a space token by reading consecutive space characters and creating a token with
 * the value of those characters.
 * 
 * @param currentChar The parameter `currentChar` is a string that represents the current character
 * being processed in the code.
 * @return The method is returning a Token object.
 */
private Token buildSpaceToken(String currentChar) {
  Token deleteToken = new Token();
  deleteToken.setType(TokenType.DELETE);
  deleteToken.setSourceLineNumber(sourceLineNumber);
  StringBuilder sBuilder = new StringBuilder(currentChar);

  for (String nextChar = readNextChar(); nextChar != null; nextChar = readNextChar()) {
      if (LexicalRegexPatterns.SpacePattern.matcher(nextChar).matches()) {
          sBuilder.append(nextChar);
      } else {
          extraCharRead = nextChar;
          break;
      }
  }

  deleteToken.setValue(sBuilder.toString());
  return deleteToken;
}

  
 /**
  * The function builds a comment token by reading characters until a newline character is encountered
  * or the end of the input is reached.
  * 
  * @param currentChar The parameter `currentChar` is a String that represents the current character
  * being processed in the code.
  * @return The method is returning a Token object.
  */
 private Token buildCommentToken(String currentChar) {
    Token commentToken = new Token();
    commentToken.setType(TokenType.DELETE);
    commentToken.setSourceLineNumber(sourceLineNumber);
    StringBuilder sBuilder = new StringBuilder(currentChar);

    for (String nextChar = readNextChar(); nextChar != null; nextChar = readNextChar()) {
        if (LexicalRegexPatterns.CommentPattern.matcher(nextChar).matches()) {
            sBuilder.append(nextChar);
        } else if (nextChar.equals("\n")) {
            break;
        }
    }

    commentToken.setValue(sBuilder.toString());
    return commentToken;
}


/**
 * The function builds a punctuation token based on the current character and assigns the appropriate
 * token type.
 * 
 * @param currentChar The parameter `currentChar` represents the current character being processed in
 * the code.
 * @return The method is returning a Token object.
 */
private Token buildPunctuationPattern(String currentChar) {
  Token punctuationToken = new Token();
  punctuationToken.setSourceLineNumber(sourceLineNumber);
  punctuationToken.setValue(currentChar);

  String[] punctuationSymbols = { "(", ")", ";", "," };
  TokenType[] tokenTypes = {
      TokenType.LEFT_PARENTHESES,
      TokenType.RIGHT_PARENTHESES,
      TokenType.SEMICOLON,
      TokenType.COMMA
  };

  for (int i = 0; i < punctuationSymbols.length; i++) {
      if (currentChar.equals(punctuationSymbols[i])) {
          punctuationToken.setType(tokenTypes[i]);
          break;
      }
  }

  return punctuationToken;
}

}

