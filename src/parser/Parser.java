package parser;

import java.util.Stack;

import ast.AST;
import ast.ASTNode;
import ast.ASTNodeType;
import scanner.Scanner;
import scanner.Token;
import scanner.TokenType;

/**
 * Recursive descent parser implementing RPAL's phrase structure grammar.
 *
 * This class serves as the core component, handling the following tasks:
 * - Obtaining input from the scanner for each clause in the phrase structure grammar.
 * - Constructing the abstract syntax tree (AST) based on the input.
 */

public class Parser{
  private Scanner s;
  private Token currentToken;
  Stack<ASTNode> stack;

  public Parser(Scanner s){
    this.s = s;
    stack = new Stack<ASTNode>();
  }
  
  public AST buildAST(){
    startParse();
    return new AST(stack.pop());
  }

  public void startParse(){
    readNT();
    procE(); //extra readNT in procE()
    if(currentToken!=null)
      throw new ParseException("Expected EOF.");
  }


  //The function reads tokens from a stream until it encounters a token of type DELETE, and then creates
  //a terminal AST node based on the type of the current token.

  private void readNT(){
    do{
      currentToken = s.readNextToken(); //load next token
    }while(isCurrentTokenType(TokenType.DELETE));
    if(null != currentToken){
      if(currentToken.getType()==TokenType.IDENTIFIER){
        createTerminalASTNode(ASTNodeType.IDENTIFIER, currentToken.getValue());
      }
      else if(currentToken.getType()==TokenType.INTEGER){
        createTerminalASTNode(ASTNodeType.INTEGER, currentToken.getValue());
      } 
      else if(currentToken.getType()==TokenType.STRING){
        createTerminalASTNode(ASTNodeType.STRING, currentToken.getValue());
      }
    }
  }
  
  //The function checks if the current token has a specific type and value.
  //he type of the token that we want to check against the current token. It is of type TokenType.
  //The value parameter is a String that represents the expected value of the current token.
  //The method is returning a boolean value.

  private boolean isCurrentToken(TokenType type, String value){
    if(currentToken==null)
      return false;
    if(currentToken.getType()!=type || !currentToken.getValue().equals(value))
      return false;
    return true;
  }
  
  private boolean isCurrentTokenType(TokenType type){
    if(currentToken==null)
      return false;
    if(currentToken.getType()==type)
      return true;
    return false;
  }
  
  /**
   * The function builds an N-ary AST (Abstract Syntax Tree) node by popping the required number of
   * child nodes from a stack and setting them as children of the new node.
   * 
   * @param type The type of the AST node being built. It is of type ASTNodeType.
   * @param ariness The parameter "ariness" represents the number of children that the current node
   * should have in the N-ary AST (Abstract Syntax Tree).
   */
  private void buildNAryASTNode(ASTNodeType type, int ariness){
    ASTNode node = new ASTNode();
    node.setType(type);
    while(ariness>0){
      ASTNode child = stack.pop();
      if(node.getChild()!=null)
        child.setSibling(node.getChild());
      node.setChild(child);
      node.setSourceLineNumber(child.getSourceLineNumber());
      ariness--;
    }
    stack.push(node);
  }

 /**
  * The function creates a terminal AST node with a given type, value, and source line number, and
  * pushes it onto a stack.
  * 
  * @param type The type parameter is an enumeration value that represents the type of the AST node. It
  * specifies the category or role of the node in the abstract syntax tree.
  * @param value The "value" parameter is a string that represents the value of the AST node. It can be
  * any string value that is relevant to the specific AST node being created.
  */
  private void createTerminalASTNode(ASTNodeType type, String value){
    ASTNode node = new ASTNode();
    node.setType(type);
    node.setValue(value);
    node.setSourceLineNumber(currentToken.getSourceLineNumber());
    stack.push(node);
  }
  
 
 
 /**
  * The function `procE` is a recursive function that parses a specific grammar rule in a programming
  * language, building an abstract syntax tree (AST) based on the parsed tokens.
  */
  private void procE(){
    if(isCurrentToken(TokenType.RESERVED, "let")){ //E -> 'let' D 'in' E => 'let'
      readNT();
      procD();
      if(!isCurrentToken(TokenType.RESERVED, "in"))
        throw new ParseException("E:  'in' expected");
      readNT();
      procE(); //extra readNT in procE()
      buildNAryASTNode(ASTNodeType.LET, 2);
    }
    else if(isCurrentToken(TokenType.RESERVED, "fn")){ //E -> 'fn' Vb+ '.' E => 'lambda'
      int treesToPop = 0;
      
      readNT();
      while(isCurrentTokenType(TokenType.IDENTIFIER) || isCurrentTokenType(TokenType.LEFT_PARENTHESES)){
        procVB(); //extra readNT in procVB()
        treesToPop++;
      }
      
      if(treesToPop==0)
        throw new ParseException("E: at least one 'Vb' expected");
      
      if(!isCurrentToken(TokenType.OPERATOR, "."))
        throw new ParseException("E: '.' expected");
      
      readNT();
      procE(); //extra readNT in procE()
      
      buildNAryASTNode(ASTNodeType.LAMBDA, treesToPop+1); //+1 for the last E 
    }
    else //E -> Ew
      procEW();
  }

 
  /**
   * The function `procEW` processes an expression by calling `procT` and then checking if the current
   * token is a reserved word "where", in which case it calls `procDR` and builds an n-ary AST node
   * with the type "WHERE".
   */
  private void procEW(){
    procT(); 
    if(isCurrentToken(TokenType.RESERVED, "where")){ //Ew -> T 'where' Dr => 'where'
      readNT();
      procDR(); //extra readToken() in procDR()
      buildNAryASTNode(ASTNodeType.WHERE, 2);
    }
  }
  
  
  /**
   * The function procT processes a series of tokens and builds an n-ary abstract syntax tree if there
   * are multiple trees to be popped.
   */
  private void procT(){
    procTA(); 
    int treesToPop = 0;
    while(isCurrentToken(TokenType.OPERATOR, ",")){ 
      readNT();
      procTA();
      treesToPop++;
    }
    if(treesToPop > 0) buildNAryASTNode(ASTNodeType.TAU, treesToPop+1);
  }


/**
 * The function `procTA` processes tokens and builds an n-ary abstract syntax tree node for each
 * occurrence of the "aug" reserved token.
 */
  private void procTA(){
    procTC();
    while(isCurrentToken(TokenType.RESERVED, "aug")){ 
      readNT();
      procTC(); 
      buildNAryASTNode(ASTNodeType.AUG, 2);
    }
  }

  /**
   * The function `procTC` processes a conditional expression and builds an n-ary AST node.
   */
  private void procTC(){
    procB();
    if(isCurrentToken(TokenType.OPERATOR, "->")){ 
      readNT();
      procTC(); 
      if(!isCurrentToken(TokenType.OPERATOR, "|"))
        throw new ParseException("TC: '|' expected");
      readNT();
      procTC(); 
      buildNAryASTNode(ASTNodeType.CONDITIONAL, 3);
    }
  }
  
 /**
  * The function procB processes a series of tokens and builds an n-ary AST node for each "or" token
  * encountered.
  */
  private void procB(){
    procBT();
    while(isCurrentToken(TokenType.RESERVED, "or")){ 
      readNT();
      procBT();
      buildNAryASTNode(ASTNodeType.OR, 2);
    }
  }
  
 
  /**
   * The function procBT processes a binary tree by repeatedly calling procBS and building an AST node
   * for each occurrence of the "&" operator.
   */
  private void procBT(){
    procBS(); 
    while(isCurrentToken(TokenType.OPERATOR, "&")){ 
      readNT();
      procBS();
      buildNAryASTNode(ASTNodeType.AND, 2);
    }
  }
  
 /**
  * The function procBS checks if the current token is "not" and if so, it reads the next token and
  * processes the next production, otherwise it processes the next production directly.
  */
  private void procBS(){
    if(isCurrentToken(TokenType.RESERVED, "not")){ 
      readNT();
      procBP(); 
      buildNAryASTNode(ASTNodeType.NOT, 1);
    }
    else
      procBP(); 
  }
  
 
  /**
   * The function `procBP()` processes different comparison operators and builds an AST node based on
   * the operator type.
   */
  private void procBP(){
    procA(); 
    if(isCurrentToken(TokenType.RESERVED,"gr")||isCurrentToken(TokenType.OPERATOR,">")){
      procA(); 
      buildNAryASTNode(ASTNodeType.GR, 2);
    }
    else if(isCurrentToken(TokenType.RESERVED,"ge")||isCurrentToken(TokenType.OPERATOR,">=")){ 
      procA(); 
      buildNAryASTNode(ASTNodeType.GE, 2);
    }
    else if(isCurrentToken(TokenType.RESERVED,"ls")||isCurrentToken(TokenType.OPERATOR,"<")){ 
      readNT();
      procA(); 
      buildNAryASTNode(ASTNodeType.LS, 2);
    }
    else if(isCurrentToken(TokenType.RESERVED,"le")||isCurrentToken(TokenType.OPERATOR,"<=")){ 
      readNT();
      procA(); 
      buildNAryASTNode(ASTNodeType.LE, 2);
    }
    else if(isCurrentToken(TokenType.RESERVED,"eq")){ 
      readNT();
      procA();
      buildNAryASTNode(ASTNodeType.EQ, 2);
    }
    else if(isCurrentToken(TokenType.RESERVED,"ne")){ 
      readNT();
      procA(); 
      buildNAryASTNode(ASTNodeType.NE, 2);
    }
  }
  
 /**
  * The function procA processes arithmetic expressions by reading tokens and building an abstract
  * syntax tree.
  */
  private void procA(){
    if(isCurrentToken(TokenType.OPERATOR, "+")){ 
      readNT();
      procAT(); 
    }
    else if(isCurrentToken(TokenType.OPERATOR, "-")){ 
      readNT();
      procAT(); 
      buildNAryASTNode(ASTNodeType.NEG, 1);
    }
    else
      procAT(); 
    
    boolean plus = true;
    while(isCurrentToken(TokenType.OPERATOR, "+")||isCurrentToken(TokenType.OPERATOR, "-")){
      if(currentToken.getValue().equals("+"))
        plus = true;
      else if(currentToken.getValue().equals("-"))
        plus = false;
      readNT();
      procAT();
      if(plus) 
        buildNAryASTNode(ASTNodeType.PLUS, 2);
      else
        buildNAryASTNode(ASTNodeType.MINUS, 2);
    }
  }
  
 
 /**
  * The function procAT processes arithmetic expressions involving multiplication and division
  * operators.
  */
  private void procAT(){
    procAF(); 
   boolean mult = true;
    while(isCurrentToken(TokenType.OPERATOR, "*")||isCurrentToken(TokenType.OPERATOR, "/")){
      if(currentToken.getValue().equals("*"))
        mult = true;
      else if(currentToken.getValue().equals("/"))
        mult = false;
      readNT();
      procAF(); 
      if(mult)
        buildNAryASTNode(ASTNodeType.MULT, 2);
      else
        buildNAryASTNode(ASTNodeType.DIV, 2);
    }
  }
  
 
 /**
  * The function procAF checks if the current token is an operator "**" and if so, it reads the next
  * token, recursively calls procAF, and builds an AST node of type EXP with two children.
  */
  private void procAF(){
    procAP(); 
    if(isCurrentToken(TokenType.OPERATOR, "**")){ 
      readNT();
      procAF();
      buildNAryASTNode(ASTNodeType.EXP, 2);
    }
  }
  
  
  /**
   * The function `procAP` processes a sequence of tokens and builds an n-ary AST node for each
   * occurrence of the '@' operator followed by an identifier and another expression.
   */
  private void procAP(){
    procR();
    while(isCurrentToken(TokenType.OPERATOR, "@")){ 
      readNT();
      if(!isCurrentTokenType(TokenType.IDENTIFIER))
        throw new ParseException("AP: expected Identifier");
      readNT();
      procR(); 
      buildNAryASTNode(ASTNodeType.AT, 3);
    }
  }
  

 /**
  * The function `procR` processes a series of tokens and builds an n-ary abstract syntax tree (AST)
  * node.
  */
  private void procR(){
    procRN(); 
    readNT();
    while(isCurrentTokenType(TokenType.INTEGER)||
        isCurrentTokenType(TokenType.STRING)|| 
        isCurrentTokenType(TokenType.IDENTIFIER)||
        isCurrentToken(TokenType.RESERVED, "true")||
        isCurrentToken(TokenType.RESERVED, "false")||
        isCurrentToken(TokenType.RESERVED, "nil")||
        isCurrentToken(TokenType.RESERVED, "dummy")||
        isCurrentTokenType(TokenType.LEFT_PARENTHESES)){
      procRN(); 
      buildNAryASTNode(ASTNodeType.GAMMA, 2);
      readNT();
    }
  }

 
  // The above code is defining a private method called `procRN()`. This method is used to process a
  // specific type of token in a programming language.
  private void procRN(){
    if(isCurrentTokenType(TokenType.IDENTIFIER)|| 
       isCurrentTokenType(TokenType.INTEGER)|| 
       isCurrentTokenType(TokenType.STRING)){ 
    }
    else if(isCurrentToken(TokenType.RESERVED, "true")){ 
      createTerminalASTNode(ASTNodeType.TRUE, "true");
    }
    else if(isCurrentToken(TokenType.RESERVED, "false")){
      createTerminalASTNode(ASTNodeType.FALSE, "false");
    } 
    else if(isCurrentToken(TokenType.RESERVED, "nil")){
      createTerminalASTNode(ASTNodeType.NIL, "nil");
    }
    else if(isCurrentTokenType(TokenType.LEFT_PARENTHESES)){
      readNT();
      procE();
      if(!isCurrentTokenType(TokenType.RIGHT_PARENTHESES))
        throw new ParseException("RN: ')' expected");
    }
    else if(isCurrentToken(TokenType.RESERVED, "dummy")){
      createTerminalASTNode(ASTNodeType.DUMMY, "dummy");
    }
  }

 
  private void procD(){
    procDA(); 
    if(isCurrentToken(TokenType.RESERVED, "within")){ 
      readNT();
      procD();
      buildNAryASTNode(ASTNodeType.WITHIN, 2);
    }
  }
  
 /**
  * The function procDA processes a series of reserved tokens followed by a call to procDR, and then
  * builds an n-ary AST node if there were multiple reserved tokens.
  */
  private void procDA(){
    procDR(); 
    int treesToPop = 0;
    while(isCurrentToken(TokenType.RESERVED, "and")){ 
      readNT();
      procDR(); 
      treesToPop++;
    }
    if(treesToPop > 0) buildNAryASTNode(ASTNodeType.SIMULTDEF, treesToPop+1);
  }
  
  /**
   * The function `procDR` checks if the current token is "rec" and if so, it processes a database and
   * builds an n-ary AST node of type "REC", otherwise it just processes the database.
   */
  private void procDR(){
    if(isCurrentToken(TokenType.RESERVED, "rec")){ 
      procDB();
      buildNAryASTNode(ASTNodeType.REC, 1);
    }
    else{ 
      procDB(); 
    }
  }
  
  /**
   * The function `procDB` processes a database query by parsing the tokens and building an abstract
   * syntax tree (AST) based on the grammar rules.
   */
  private void procDB(){
    if(isCurrentTokenType(TokenType.LEFT_PARENTHESES)){ 
      procD();
      readNT();
      if(!isCurrentTokenType(TokenType.RIGHT_PARENTHESES))
        throw new ParseException("DB: ')' expected");
      readNT();
    }
    else if(isCurrentTokenType(TokenType.IDENTIFIER)){
      readNT();
      if(isCurrentToken(TokenType.OPERATOR, ",")){ 
        readNT();
        procVL();
        if(!isCurrentToken(TokenType.OPERATOR, "="))
          throw new ParseException("DB: = expected.");
        buildNAryASTNode(ASTNodeType.COMMA, 2);
        readNT();
        procE(); 
        buildNAryASTNode(ASTNodeType.EQUAL, 2);
      }
      else{ 
        if(isCurrentToken(TokenType.OPERATOR, "=")){ 
          readNT();
          procE(); 
          buildNAryASTNode(ASTNodeType.EQUAL, 2);
        }
        else{ 
          int treesToPop = 0;

          while(isCurrentTokenType(TokenType.IDENTIFIER) || isCurrentTokenType(TokenType.LEFT_PARENTHESES)){
            procVB(); 
            treesToPop++;
          }

          if(treesToPop==0)
            throw new ParseException("E: at least one 'Vb' expected");

          if(!isCurrentToken(TokenType.OPERATOR, "="))
            throw new ParseException("DB: = expected.");

          readNT();
          procE();

          buildNAryASTNode(ASTNodeType.FCNFORM, treesToPop+2);
        }
      }
    }
  }
  
  /**
   * The function `procVB` reads tokens and creates an abstract syntax tree (AST) node based on the
   * token type.
   */
  private void procVB(){
    if(isCurrentTokenType(TokenType.IDENTIFIER)){ 
      readNT();
    }
    else if(isCurrentTokenType(TokenType.LEFT_PARENTHESES)){
      readNT();
      if(isCurrentTokenType(TokenType.RIGHT_PARENTHESES)){ 
        createTerminalASTNode(ASTNodeType.PAREN, "");
        readNT();
      }
      else{ 
        procVL(); 
        if(!isCurrentTokenType(TokenType.RIGHT_PARENTHESES))
          throw new ParseException("VB: ')' expected");
        readNT();
      }
    }
  }


  /**
   * The function `procVL` checks if the current token is an identifier, and if so, reads the next
   * token and builds an n-ary AST node if there are multiple identifiers separated by commas.
   */
  private void procVL(){
    if(!isCurrentTokenType(TokenType.IDENTIFIER))
      throw new ParseException("VL: Identifier expected");
    else{
      readNT();
      int treesToPop = 0;
      while(isCurrentToken(TokenType.OPERATOR, ",")){ 
        readNT();
        if(!isCurrentTokenType(TokenType.IDENTIFIER))
          throw new ParseException("VL: Identifier expected");
        readNT();
        treesToPop++;
      }
      if(treesToPop > 0) buildNAryASTNode(ASTNodeType.COMMA, treesToPop+1); 
    }
  }

}

