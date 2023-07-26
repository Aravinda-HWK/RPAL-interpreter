package parser;

import java.util.Stack;

import ast.AST;
import ast.ASTNode;
import ast.ASTNodeType;
import scanner.Scanner;
import scanner.Token;
import scanner.TokenType;

public class Parser{
  // The above code is declaring a private Scanner object named "s" and a private Token object named
  // "currentToken". It also declares a Stack object named "stack" that will store ASTNode objects.
  private Scanner s;
  private Token currentToken;
  Stack<ASTNode> stack;

  // The above code is defining a constructor for a class called Parser. The constructor takes a
  // parameter of type Scanner and assigns it to a variable called s. It also initializes a new Stack
  // object called stack.
  public Parser(Scanner s){
    this.s = s;
    stack = new Stack<ASTNode>();
  }
  
  /**
   * The function builds an Abstract Syntax Tree (AST) by parsing a given input and returning the root
   * node of the tree.
   * 
   * @return The method is returning an instance of the AST (Abstract Syntax Tree) class.
   */
  public AST buildAST(){
    startParse();
    return new AST(stack.pop());
  }

 /**
  * The startParse function reads and processes tokens until the end of the file is reached.
  */
  public void startParse(){
    readNT();
    procE();
    if(currentToken!=null)
      throw new ParseException("Expected EOF.");
  }

  private void readNT() {
    // Continue reading tokens until a non-DELETE token is encountered
    for (currentToken = s.readNextToken(); isCurrentTokenType(TokenType.DELETE); currentToken = s.readNextToken()) {
        // Skip DELETE tokens
    }

    // Check if the currentToken is not null
    if (null != currentToken) {
        if (currentToken.getType() == TokenType.IDENTIFIER) {
            // Create a terminal AST node for an identifier
            createTerminalASTNode(ASTNodeType.IDENTIFIER, currentToken.getValue());
        } else if (currentToken.getType() == TokenType.INTEGER) {
            // Create a terminal AST node for an integer
            createTerminalASTNode(ASTNodeType.INTEGER, currentToken.getValue());
        } else if (currentToken.getType() == TokenType.STRING) {
            // Create a terminal AST node for a string
            createTerminalASTNode(ASTNodeType.STRING, currentToken.getValue());
        }
    }
}

  
  /**
 * Checks if the current token matches the given TokenType and value.
 * @param type The expected TokenType to match.
 * @param value The expected value to match.
 * @return True if the current token matches the given TokenType and value, false otherwise.
 */
private boolean isCurrentToken(TokenType type, String value) {
    // Check if the currentToken is null, indicating no valid token is available.
    if (currentToken == null) {
        return false;
    }

    // Compare the TokenType and value of the currentToken with the expected values.
    if (currentToken.getType() != type || !currentToken.getValue().equals(value)) {
        return false; // If TokenType or value does not match, return false.
    }

    return true; // The currentToken matches the expected TokenType and value.
}

  
  /**
 * Checks if the current token's TokenType matches the given TokenType.
 * @param type The expected TokenType to match.
 * @return True if the current token's TokenType matches the given TokenType, false otherwise.
 */
private boolean isCurrentTokenType(TokenType type) {
    // Check if the currentToken is null, indicating no valid token is available.
    if (currentToken == null) {
        return false;
    }

    // Compare the TokenType of the currentToken with the expected TokenType.
    if (currentToken.getType() == type) {
        return true; // If TokenType matches, return true.
    }

    return false; // The TokenType of the currentToken does not match the expected TokenType.
}

  
 /**
 * Builds an N-ary AST node with the given type and specified arity.
 *
 * @param type    The type of the N-ary AST node to build.
 * @param ariness The arity or number of children the N-ary node should have.
 */
private void buildNAryASTNode(ASTNodeType type, int ariness) {
    // Create a new ASTNode to represent the N-ary node.
    ASTNode node = new ASTNode();
    node.setType(type);

    for (int i = 0; i < ariness; i++) {
        // Pop the topmost 'ariness' number of nodes from the stack as children of the current node.
        ASTNode child = stack.pop();

        // Link the child to the current node by setting its sibling pointer.
        // This allows us to create a linked list of children with the same parent.
        if (node.getChild() != null) {
            child.setSibling(node.getChild());
        }

        // Set the current child as the new first child of the N-ary node.
        node.setChild(child);

        // Set the source line number of the N-ary node to that of its first child.
        // This ensures consistency in the source line information for the entire N-ary node.
        node.setSourceLineNumber(child.getSourceLineNumber());
    }

    // Push the newly built N-ary node back onto the stack to be used as a parent in further processing.
    stack.push(node);
}


  /**
 * Creates a terminal AST node with the given type and value from the current token.
 *
 * @param type  The type of the terminal AST node to create.
 * @param value The value of the terminal AST node.
 */
private void createTerminalASTNode(ASTNodeType type, String value) {
    // Create a new ASTNode to represent the terminal node.
    ASTNode node = new ASTNode();
    node.setType(type);
    node.setValue(value);

    // Set the source line number of the terminal node from the current token.
    node.setSourceLineNumber(currentToken.getSourceLineNumber());

    // Push the newly created terminal node onto the stack for further processing.
    stack.push(node);
}

  
  private void procE() {
    // Check if the current token is 'let'
    if (isCurrentToken(TokenType.RESERVED, "let")) {
        readNT(); // Read the 'let' token
        procD(); // Process the declarations
        if (!isCurrentToken(TokenType.RESERVED, "in"))
            throw new ParseException("E: 'in' expected");
        readNT(); // Read the 'in' token
        procE(); // Process the expression
        buildNAryASTNode(ASTNodeType.LET, 2); // Build the AST node for 'let' expression with 2 children
    }
    // Check if the current token is 'fn'
    else if (isCurrentToken(TokenType.RESERVED, "fn")) {
        int treesToPop = 0;
        readNT(); // Read the 'fn' token

        // Process variable bindings
        for (; isCurrentTokenType(TokenType.IDENTIFIER) || isCurrentTokenType(TokenType.L_PAREN); treesToPop++) {
            procVB();
        }

        // Check if at least one 'Vb' is expected
        if (treesToPop == 0)
            throw new ParseException("E: at least one 'Vb' expected");

        // Check if the next token is '.'
        if (!isCurrentToken(TokenType.OPERATOR, "."))
            throw new ParseException("E: '.' expected");

        readNT(); // Read the '.' token
        procE(); // Process the expression
        buildNAryASTNode(ASTNodeType.LAMBDA, treesToPop + 1); // Build the AST node for lambda expression with appropriate children
    }
    // If neither 'let' nor 'fn', then process the expression with postfix operators and other expressions
    else
        procEW();
}
  
  /**
   * The function procEW processes a token and checks if it is a reserved word "where", and if so, it
   * reads the next token and processes it as a DR (data retrieval) token, and then builds an n-ary AST
   * (abstract syntax tree) node with the type WHERE and 2 children.
   */
  private void procEW(){
    procT();
    if(isCurrentToken(TokenType.RESERVED, "where")){ 
      readNT();
      procDR(); 
      buildNAryASTNode(ASTNodeType.WHERE, 2);
    }
  }
   
 
  private void procT() {
    // Process the first term in the expression
    procTA();

    int treesToPop = 0; // Counter to keep track of the number of terms

    // Iterate over each ',' separated term in the expression
    for (; isCurrentToken(TokenType.OPERATOR, ","); treesToPop++) {
        readNT(); // Read the ',' token
        procTA(); // Process the next term
    }

    // If there are multiple terms, build the NAry AST node for the 'tau' expression
    if (treesToPop > 0) {
        buildNAryASTNode(ASTNodeType.TAU, treesToPop + 1);
    }
}
 
private void procTA() {
    procTC();

    // Iterate over each 'aug' separated term in the expression
    for (; isCurrentToken(TokenType.RESERVED, "aug"); ) {
        readNT(); // Read the 'aug' token
        procTC(); // Process the next term
        buildNAryASTNode(ASTNodeType.AUG, 2); // Build the AST node for 'aug' expression with 2 children
    }
}

 private void procTC() {
    // Process the first expression
    procB();

    // Check if the current token is '->', indicating a conditional expression
    if (isCurrentToken(TokenType.OPERATOR, "->")) {
        readNT(); // Read the '->' token

        // Process the expression on the right-hand side of '->'
        procTC();

        // Check if the current token is '|', which separates the two branches of the conditional expression
        if (!isCurrentToken(TokenType.OPERATOR, "|"))
            throw new ParseException("TC: '|' expected");
        
        readNT(); // Read the '|' token

        // Process the expression on the right-hand side of '|'
        procTC();

        // Build the AST node for the 'conditional' expression with 3 children
        buildNAryASTNode(ASTNodeType.CONDITIONAL, 3);
    }
}

  
  
 private void procB() {
    procBT();

    // Iterate over each 'or' separated term in the expression
    for (; isCurrentToken(TokenType.RESERVED, "or"); ) {
        readNT(); // Read the 'or' token
        procBT(); // Process the next term
        buildNAryASTNode(ASTNodeType.OR, 2); // Build the AST node for 'or' expression with 2 children
    }
}

private void procBT() {
    procBS();

    // Iterate over each '&' separated factor in the expression
    for (; isCurrentToken(TokenType.OPERATOR, "&"); ) {
        readNT(); // Read the '&' token
        procBS(); // Process the next factor
        buildNAryASTNode(ASTNodeType.AND, 2); // Build the AST node for 'and' expression with 2 children
    }
}

private void procBS() {
    // Check if the current token is 'not'
    if (isCurrentToken(TokenType.RESERVED, "not")) {
        readNT(); // Read the 'not' token
        procBP(); // Process the next term
        buildNAryASTNode(ASTNodeType.NOT, 1); // Build the AST node for 'not' expression with 1 child
    } else {
        procBP(); // Process the term
    }
}

  private void procBP(){
    procA(); // Process the left-hand side expression

    // Check for greater than (>) or 'gr' reserved token
    if(isCurrentToken(TokenType.RESERVED,"gr")||isCurrentToken(TokenType.OPERATOR,">")){ 
        readNT(); // Consume the 'gr' or '>' token
        procA(); // Process the right-hand side expression
        buildNAryASTNode(ASTNodeType.GR, 2); // Build AST node for 'greater than' operation
    }
    // Check for greater than or equal (>=) or 'ge' reserved token
    else if(isCurrentToken(TokenType.RESERVED,"ge")||isCurrentToken(TokenType.OPERATOR,">=")){ 
        readNT(); // Consume the 'ge' or '>=' token
        procA(); // Process the right-hand side expression
        buildNAryASTNode(ASTNodeType.GE, 2); // Build AST node for 'greater than or equal' operation
    }
    // Check for less than (<) or 'ls' reserved token
    else if(isCurrentToken(TokenType.RESERVED,"ls")||isCurrentToken(TokenType.OPERATOR,"<")){ 
        readNT(); // Consume the 'ls' or '<' token
        procA(); // Process the right-hand side expression
        buildNAryASTNode(ASTNodeType.LS, 2); // Build AST node for 'less than' operation
    }
    // Check for less than or equal (<=) or 'le' reserved token
    else if(isCurrentToken(TokenType.RESERVED,"le")||isCurrentToken(TokenType.OPERATOR,"<=")){ 
        readNT(); // Consume the 'le' or '<=' token
        procA(); // Process the right-hand side expression
        buildNAryASTNode(ASTNodeType.LE, 2); // Build AST node for 'less than or equal' operation
    }
    // Check for equal (eq) reserved token
    else if(isCurrentToken(TokenType.RESERVED,"eq")){ 
        readNT(); // Consume the 'eq' token
        procA(); // Process the right-hand side expression
        buildNAryASTNode(ASTNodeType.EQ, 2); // Build AST node for 'equal' operation
    }
    // Check for not equal (ne) reserved token
    else if(isCurrentToken(TokenType.RESERVED,"ne")){
        readNT(); // Consume the 'ne' token
        procA(); // Process the right-hand side expression
        buildNAryASTNode(ASTNodeType.NE, 2); // Build AST node for 'not equal' operation
    }
}

  
private void procA() {
  // Check if the current token is '+'
  if (isCurrentToken(TokenType.OPERATOR, "+")) {
    readNT(); // Consume the '+' token
    procAT(); // Process the next term
  } 
  // Check if the current token is '-'
  else if (isCurrentToken(TokenType.OPERATOR, "-")) {
    readNT(); // Consume the '-' token
    procAT(); // Process the next term
    buildNAryASTNode(ASTNodeType.NEG, 1); // Build AST node for unary minus
  } 
  // If the current token is neither '+' nor '-', process the term
  else {
    procAT(); // Process the term
  }

  // A flag to keep track of whether the last operator encountered was a '+' or '-'
  boolean plus = true;

  // Loop to handle consecutive '+' or '-' operators
  while (isCurrentToken(TokenType.OPERATOR, "+") || isCurrentToken(TokenType.OPERATOR, "-")) {
    // Check the current operator and set the flag accordingly
    if (currentToken.getValue().equals("+"))
      plus = true; // Set flag for '+' operator
    else if (currentToken.getValue().equals("-"))
      plus = false; // Set flag for '-' operator

    readNT(); // Consume the '+' or '-' token
    procAT(); // Process the next term

    // Build the appropriate AST node based on whether it is a plus or minus operator
    if (plus)
      buildNAryASTNode(ASTNodeType.PLUS, 2); // Build AST node for '+' operation
    else
      buildNAryASTNode(ASTNodeType.MINUS, 2); // Build AST node for '-' operation
  }
}

  
  
 private void procAT() {
    // Process the left-hand side factor.
    procAF();

    // A flag to keep track of whether the last operator encountered was '*' or '/'.
    boolean mult = true;

    // Iterate over the consecutive '*' or '/' operators.
    while (isCurrentToken(TokenType.OPERATOR, "*") || isCurrentToken(TokenType.OPERATOR, "/")) {
        // Check the current operator and set the flag accordingly.
        if (currentToken.getValue().equals("*")) {
            mult = true;
        } else if (currentToken.getValue().equals("/")) {
            mult = false;
        }

        // Consume the current token.
        readNT();

        // Process the next factor.
        procAF();

        // Build the appropriate AST node based on the operator.
        if (mult) {
            buildNAryASTNode(ASTNodeType.MULT, 2);
        } else {
            buildNAryASTNode(ASTNodeType.DIV, 2);
        }
    }
}

// Comments:
// The `procAF()` method is used to process the left-hand side factor.
// The `isCurrentToken()` method is used to check if the current token is of the specified type.
// The `readNT()` method is used to consume the current token.
// The `buildNAryASTNode()` method is used to build the appropriate AST node based on the operator.

  
 /**
  * The function procAF checks if the current token is an operator "**" and if so, it reads the next
  * token and recursively calls itself to build an n-ary AST node of type EXP with 2 children.
  */
  private void procAF(){
    procAP();
    if(isCurrentToken(TokenType.OPERATOR, "**")){
      readNT();
      procAF();
      buildNAryASTNode(ASTNodeType.EXP, 2);
    }
  }
  
  private void procAP() {
    // Process the argument predicate.
    procR();

    // Iterate over the consecutive '@' operators.
    while (isCurrentToken(TokenType.OPERATOR, "@")) {
        // Consume the current token.
        readNT();

        // Check if the next token is an identifier.
        if (!isCurrentTokenType(TokenType.IDENTIFIER)) {
            throw new ParseException("AP: expected Identifier");
        }

        // Consume the identifier token.
        readNT();

        // Process the next predicate.
        procR();

        // Build the appropriate AST node based on the operator.
        buildNAryASTNode(ASTNodeType.AT, 3);
    }
}

private void procR() {
    // Process the right operand.
    procRN();

    // Consume the next token.
    readNT();

    // Iterate over the consecutive gamma operators.
    while (isCurrentTokenType(TokenType.INTEGER) ||
           isCurrentTokenType(TokenType.STRING) ||
           isCurrentTokenType(TokenType.IDENTIFIER) ||
           isCurrentToken(TokenType.RESERVED, "true") ||
           isCurrentToken(TokenType.RESERVED, "false") ||
           isCurrentToken(TokenType.RESERVED, "nil") ||
           isCurrentToken(TokenType.RESERVED, "dummy") ||
           isCurrentTokenType(TokenType.L_PAREN)) {
        // Process the next operand.
        procRN();

        // Build the appropriate AST node based on the operator.
        buildNAryASTNode(ASTNodeType.GAMMA, 2);

        // Consume the next token.
        readNT();
    }
}

private void procRN() {
    // Check if the current token is an identifier, integer, or string.
    if (isCurrentTokenType(TokenType.IDENTIFIER) ||
        isCurrentTokenType(TokenType.INTEGER) ||
        isCurrentTokenType(TokenType.STRING)) {
        // Do nothing.
    }
    // Check if the current token is the keyword "true".
    else if (isCurrentToken(TokenType.RESERVED, "true")) {
        // Create a terminal AST node with the value "true".
        createTerminalASTNode(ASTNodeType.TRUE, "true");
    }
    // Check if the current token is the keyword "false".
    else if (isCurrentToken(TokenType.RESERVED, "false")) {
        // Create a terminal AST node with the value "false".
        createTerminalASTNode(ASTNodeType.FALSE, "false");
    }
    // Check if the current token is the keyword "nil".
    else if (isCurrentToken(TokenType.RESERVED, "nil")) {
        // Create a terminal AST node with the value "nil".
        createTerminalASTNode(ASTNodeType.NIL, "nil");
    }
    // Check if the current token is a left parenthesis.
    else if (isCurrentTokenType(TokenType.L_PAREN)) {
        // Consume the current token.
        readNT();

        // Process the expression.
        procE();

        // Check if the current token is a right parenthesis.
        if (!isCurrentTokenType(TokenType.R_PAREN)) {
            throw new ParseException("RN: ')' expected");
        }
    }
    // Check if the current token is the keyword "dummy".
    else if (isCurrentToken(TokenType.RESERVED, "dummy")) {
        // Create a terminal AST node with the value "dummy".
        createTerminalASTNode(ASTNodeType.DUMMY, "dummy");
    }
}


/**
 * The function `procD` processes a specific syntax rule and builds an AST node if a certain condition
 * is met.
 */
private void procD() {
    procDA();
    if (isCurrentToken(TokenType.RESERVED, "within")) {
        readNT();
        procD();
        buildNAryASTNode(ASTNodeType.WITHIN, 2);
    }
}

/**
 * The function procDA processes a series of "and" statements by calling procDR and building an n-ary
 * AST node.
 */
private void procDA() {
    procDR();
    int treesToPop = 0;
    while (isCurrentToken(TokenType.RESERVED, "and")) {
        readNT();
        procDR();
        treesToPop++;
    }
    if (treesToPop > 0) buildNAryASTNode(ASTNodeType.SIMULTDEF, treesToPop + 1);
}

  
 /**
  * The function procDR checks if the current token is "rec" and if so, reads the next token and
  * processes the database, otherwise it just processes the database.
  */
  private void procDR(){
    if(isCurrentToken(TokenType.RESERVED, "rec")){
      readNT();
      procDB();
      buildNAryASTNode(ASTNodeType.REC, 1);
    }
    else{
      procDB();
    }
  }

  private void procDB() {
    // Process the database.
    if (isCurrentTokenType(TokenType.L_PAREN)) {
        // Consume the left parenthesis.
        readNT();

        // Process the definition.
        procD();

        // Consume the right parenthesis.
        if (!isCurrentTokenType(TokenType.R_PAREN)) {
            throw new ParseException("DB: ')' expected");
        }

        readNT();
    } else if (isCurrentTokenType(TokenType.IDENTIFIER)) {
        // Consume the identifier.
        readNT();

        // Check if the next token is a comma.
        if (isCurrentToken(TokenType.OPERATOR, ",")) {
            // Consume the comma.
            readNT();

            // Process the value list.
            procVL();

            // Check if the next token is an equal sign.
            if (!isCurrentToken(TokenType.OPERATOR, "=")) {
                throw new ParseException("DB: = expected.");
            }

            // Consume the equal sign.
            readNT();

            // Process the expression.
            procE();

            // Build the appropriate AST node based on the operators.
            buildNAryASTNode(ASTNodeType.EQUAL, 2);
        } else {
            // Check if the next token is an equal sign.
            if (isCurrentToken(TokenType.OPERATOR, "=")) {
                // Consume the equal sign.
                readNT();

                // Process the expression.
                procE();

                // Build the appropriate AST node based on the operators.
                buildNAryASTNode(ASTNodeType.EQUAL, 2);
            } else {
                // Process the value list.
                int treesToPop = 0;

                while (isCurrentTokenType(TokenType.IDENTIFIER) || isCurrentTokenType(TokenType.L_PAREN)) {
                    procVB();
                    treesToPop++;
                }

                if (treesToPop == 0) {
                    throw new ParseException("E: at least one 'Vb' expected");
                }

                if (!isCurrentToken(TokenType.OPERATOR, "=")) {
                    throw new ParseException("DB: = expected.");
                }

                readNT();
                procE();

                buildNAryASTNode(ASTNodeType.FCNFORM, treesToPop + 2);
            }
        }
    }
}

private void procVB() {
    // Process the value.
    if (isCurrentTokenType(TokenType.IDENTIFIER)) {
        // Consume the identifier.
        readNT();
    } else if (isCurrentTokenType(TokenType.L_PAREN)) {
        // Consume the left parenthesis.
        readNT();

        if (isCurrentTokenType(TokenType.R_PAREN)) {
            // Create a terminal AST node with the value "".
            createTerminalASTNode(ASTNodeType.PAREN, "");
            readNT();
        } else {
            // Process the value list.
            procVL();

            if (!isCurrentTokenType(TokenType.R_PAREN)) {
                throw new ParseException("VB: ')' expected");
            }

            readNT();
        }
    }
}


 private void procVL() {
    // Process the value list.
    if (!isCurrentTokenType(TokenType.IDENTIFIER)) {
        // Throw an exception if the next token is not an identifier.
        throw new ParseException("VL: Identifier expected");
    }

    // Consume the identifier.
    readNT();

    // Initialize a variable to keep track of the number of commas.
    int treesToPop = 0;

    // Iterate over the consecutive commas.
    while (isCurrentToken(TokenType.OPERATOR, ",")) {
        // Consume the comma.
        readNT();

        // Check if the next token is an identifier.
        if (!isCurrentTokenType(TokenType.IDENTIFIER)) {
            // Throw an exception if the next token is not an identifier.
            throw new ParseException("VL: Identifier expected");
        }

        // Consume the identifier.
        readNT();

        // Increment the number of commas.
        treesToPop++;
    }

    // Build the appropriate AST node based on the operators.
    if (treesToPop > 0) {
        buildNAryASTNode(ASTNodeType.COMMA, treesToPop + 1);
    }
}

// Comments:
// The `procVL()` method processes the value list.
// The `isCurrentTokenType()` method is used to check if the current token is of the specified type.
// The `readNT()` method is used to consume the current token.
// The `buildNAryASTNode()` method is used to build the appropriate AST node based on the operators.

}
