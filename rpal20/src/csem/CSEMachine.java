package csem;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import ast.AST;
import ast.ASTNode;
import ast.ASTNodeType;

public class CSEMachine{

  private Stack<ASTNode> valueStack;
  private Delta rootDelta;

  // The above code is defining a constructor for a class called CSEMachine. The constructor takes an
  // AST (Abstract Syntax Tree) as a parameter.
  public CSEMachine(AST ast){
    if(!ast.isStandardized())
      throw new RuntimeException("AST has NOT been standardized!"); 
    rootDelta = ast.createDeltas();
    rootDelta.setLinkedEnv(new Environment()); 
    valueStack = new Stack<ASTNode>();
  }

  /**
   * The evaluateProgram function processes the control stack starting from the rootDelta and its
   * linked environment.
   */
  public void evaluateProgram(){
    processControlStack(rootDelta, rootDelta.getLinkedEnv());
  }

  /**
   * The function processes the nodes in the control stack until it is empty.
   * 
   * @param currentDelta The currentDelta parameter is an object of type Delta. It represents a delta
   * or a change in the program's state. It contains information about the changes that need to be
   * applied to the program's environment or other data structures.
   * @param currentEnv The current environment, which is a data structure that stores variables and
   * their values. It is used to keep track of the state of the program during execution.
   */
  private void processControlStack(Delta currentDelta, Environment currentEnv){
    Stack<ASTNode> controlStack = new Stack<ASTNode>();
    controlStack.addAll(currentDelta.getBody());
    
    while(!controlStack.isEmpty())
      processCurrentNode(currentDelta, currentEnv, controlStack);
  }

  /**
   * The function processes the current node in a control stack by applying binary and unary
   * operations, handling identifiers, creating tuples, handling beta nodes, applying gamma nodes, and
   * setting linked environments for delta nodes.
   * 
   * @param currentDelta The currentDelta parameter is an object of type Delta, which represents a
   * delta node in an abstract syntax tree.
   * @param currentEnv The current environment in which the code is being executed. It contains
   * information about the variables and their values.
   * @param currentControlStack The `currentControlStack` is a stack data structure that stores the
   * ASTNodes that need to be processed. It is used to keep track of the control flow of the program.
   */
  private void processCurrentNode(Delta currentDelta, Environment currentEnv, Stack<ASTNode> currentControlStack) {
    ASTNode node = currentControlStack.pop();
    
    if (applyBinaryOperation(node) || applyUnaryOperation(node)) {
        return;
    } else {
        switch (node.getType()) {
            case IDENTIFIER:
                handleIdentifiers(node, currentEnv);
                break;
            case NIL:
            case TAU:
                createTuple(node);
                break;
            case BETA:
                handleBeta((Beta) node, currentControlStack);
                break;
            case GAMMA:
                applyGamma(currentDelta, node, currentEnv, currentControlStack);
                break;
            case DELTA:
                ((Delta) node).setLinkedEnv(currentEnv); // RULE 2
                // Fallthrough to default case
            default:
                valueStack.push(node);
                break;
        }
    }
}


  // RULE 6
  /**
   * The function applies a binary operation based on the type of the given ASTNode and returns true if
   * the operation was successfully applied, otherwise it returns false.
   * 
   * @param rator The parameter "rator" is an ASTNode object representing the operator in a binary
   * operation.
   * @return The method is returning a boolean value.
   */
  private boolean applyBinaryOperation(ASTNode rator) {
    switch (rator.getType()) {
        case PLUS:
        case MINUS:
        case MULT:
        case DIV:
        case EXP:
        case LS:
        case LE:
        case GR:
        case GE:
            binaryArithmeticOp(rator.getType());
            break;
        case EQ:
        case NE:
            binaryLogicalEqNeOp(rator.getType());
            break;
        case OR:
        case AND:
            binaryLogicalOrAndOp(rator.getType());
            break;
        case AUG:
            augTuples();
            break;
        default:
            return false;
    }
    return true;
}


  /**
   * The function performs binary arithmetic operations on two integers and handles different types of
   * operations.
   * 
   * @param type The parameter `type` is of type `ASTNodeType`, which is an enumeration representing
   * different types of AST nodes. It is used to determine the specific binary arithmetic operation to
   * perform.
   */
  private void binaryArithmeticOp(ASTNodeType type) {
    ASTNode rand1 = valueStack.pop();
    ASTNode rand2 = valueStack.pop();
    if (rand1.getType() != ASTNodeType.INTEGER || rand2.getType() != ASTNodeType.INTEGER) {
        EvaluationError.printError(rand1.getSourceLineNumber(),
                "Expected two integers; was given \"" + rand1.getValue() + "\", \"" + rand2.getValue() + "\"");
    }

    int resultValue;
    switch (type) {
        case PLUS:
            resultValue = Integer.parseInt(rand1.getValue()) + Integer.parseInt(rand2.getValue());
            break;
        case MINUS:
            resultValue = Integer.parseInt(rand1.getValue()) - Integer.parseInt(rand2.getValue());
            break;
        case MULT:
            resultValue = Integer.parseInt(rand1.getValue()) * Integer.parseInt(rand2.getValue());
            break;
        case DIV:
            resultValue = Integer.parseInt(rand1.getValue()) / Integer.parseInt(rand2.getValue());
            break;
        case EXP:
            resultValue = (int) Math.pow(Integer.parseInt(rand1.getValue()), Integer.parseInt(rand2.getValue()));
            break;
        case LS:
            valueStack.push(compareIntegers(rand1, rand2) < 0 ? getTrueNode() : getFalseNode());
            return;
        case LE:
            valueStack.push(compareIntegers(rand1, rand2) <= 0 ? getTrueNode() : getFalseNode());
            return;
        case GR:
            valueStack.push(compareIntegers(rand1, rand2) > 0 ? getTrueNode() : getFalseNode());
            return;
        case GE:
            valueStack.push(compareIntegers(rand1, rand2) >= 0 ? getTrueNode() : getFalseNode());
            return;
        default:
            return;
    }
    ASTNode result = new ASTNode();
    result.setType(ASTNodeType.INTEGER);
    result.setValue(Integer.toString(resultValue));
    valueStack.push(result);
}

private int compareIntegers(ASTNode rand1, ASTNode rand2) {
    return Integer.compare(Integer.parseInt(rand1.getValue()), Integer.parseInt(rand2.getValue()));
}

private ASTNode getTrueNode() {
    ASTNode trueNode = new ASTNode();
    trueNode.setType(ASTNodeType.TRUE);
    return trueNode;
}

private ASTNode getFalseNode() {
    ASTNode falseNode = new ASTNode();
    falseNode.setType(ASTNodeType.FALSE);
    return falseNode;
}


  /**
   * The function performs binary logical equality and inequality operations on two operands of various
   * types.
   * 
   * @param type The parameter "type" is of type ASTNodeType, which is an enumeration representing
   * different types of AST nodes. It is used to determine the type of binary logical equality or
   * inequality operation being performed.
   */
  private void binaryLogicalEqNeOp(ASTNodeType type) {
    ASTNode rand1 = valueStack.pop();
    ASTNode rand2 = valueStack.pop();

    if (rand1.getType() == ASTNodeType.TRUE || rand1.getType() == ASTNodeType.FALSE) {
        if (rand2.getType() != ASTNodeType.TRUE && rand2.getType() != ASTNodeType.FALSE) {
            EvaluationError.printError(rand1.getSourceLineNumber(),
                    "Cannot compare dissimilar types; was given \"" + rand1.getValue() + "\", \"" + rand2.getValue() + "\"");
        }
        compareTruthValues(rand1, rand2, type);
        return;
    }

    if (rand1.getType() != rand2.getType()) {
        EvaluationError.printError(rand1.getSourceLineNumber(),
                "Cannot compare dissimilar types; was given \"" + rand1.getValue() + "\", \"" + rand2.getValue() + "\"");
    }

    switch (rand1.getType()) {
        case STRING:
            compareStrings(rand1, rand2, type);
            break;
        case INTEGER:
            compareIntegers(rand1, rand2, type);
            break;
        default:
            EvaluationError.printError(rand1.getSourceLineNumber(),
                    "Don't know how to " + type + " \"" + rand1.getValue() + "\", \"" + rand2.getValue() + "\"");
            break;
    }
}


  /**
   * The function compares the truth values of two ASTNodes based on their types and pushes a true or
   * false node onto the stack accordingly.
   * 
   * @param rand1 The first random ASTNode to compare.
   * @param rand2 The parameter "rand2" is an ASTNode, which is an abstract syntax tree node. It
   * represents a node in the abstract syntax tree, which is a data structure used to represent the
   * structure of a program or code snippet.
   * @param type The "type" parameter is of type ASTNodeType, which is an enumeration representing
   * different types of AST nodes.
   */
  private void compareTruthValues(ASTNode rand1, ASTNode rand2, ASTNodeType type){
    if(rand1.getType()==rand2.getType())
      if(type==ASTNodeType.EQ)
        pushTrueNode();
      else
        pushFalseNode();
    else
      if(type==ASTNodeType.EQ)
        pushFalseNode();
      else
        pushTrueNode();
  }

  /**
   * The function compares the values of two ASTNodes and pushes a true or false node onto the stack
   * based on the comparison result and the given ASTNodeType.
   * 
   * @param rand1 The first random ASTNode object to compare.
   * @param rand2 ASTNode rand2 is an object of type ASTNode.
   * @param type The "type" parameter is of type ASTNodeType, which is an enumeration representing
   * different types of AST nodes.
   */
  private void compareStrings(ASTNode rand1, ASTNode rand2, ASTNodeType type){
    if(rand1.getValue().equals(rand2.getValue()))
      if(type==ASTNodeType.EQ)
        pushTrueNode();
      else
        pushFalseNode();
    else
      if(type==ASTNodeType.EQ)
        pushFalseNode();
      else
        pushTrueNode();
  }

  /**
   * The function compares two integers and pushes a true or false node onto the stack based on the
   * comparison result and the given ASTNodeType.
   * 
   * @param rand1 The first random integer value to compare.
   * @param rand2 ASTNode representing the second random integer value to compare.
   * @param type The "type" parameter is of type ASTNodeType, which is an enumeration representing
   * different types of AST nodes.
   */
  private void compareIntegers(ASTNode rand1, ASTNode rand2, ASTNodeType type){
    if(Integer.parseInt(rand1.getValue())==Integer.parseInt(rand2.getValue()))
      if(type==ASTNodeType.EQ)
        pushTrueNode();
      else
        pushFalseNode();
    else
      if(type==ASTNodeType.EQ)
        pushFalseNode();
      else
        pushTrueNode();
  }

  /**
   * The function performs binary logical OR and AND operations on two operands.
   * 
   * @param type The parameter "type" is of type ASTNodeType, which is an enumeration representing
   * different types of AST nodes.
   */
  private void binaryLogicalOrAndOp(ASTNodeType type) {
    ASTNode rand1 = valueStack.pop();
    ASTNode rand2 = valueStack.pop();

    if ((rand1.getType() == ASTNodeType.TRUE || rand1.getType() == ASTNodeType.FALSE) &&
        (rand2.getType() == ASTNodeType.TRUE || rand2.getType() == ASTNodeType.FALSE)) {
        orAndTruthValues(rand1, rand2, type);
    } else {
        EvaluationError.printError(rand1.getSourceLineNumber(),
                "Don't know how to " + type + " \"" + rand1.getValue() + "\", \"" + rand2.getValue() + "\"");
    }
}


  /**
   * The function takes two ASTNodes and an ASTNodeType as input and evaluates the logical OR or AND
   * operation between the two nodes based on the given type, pushing a true or false node onto the
   * stack accordingly.
   * 
   * @param rand1 The first random node to evaluate.
   * @param rand2 The parameter "rand2" is a variable of type ASTNode.
   * @param type The "type" parameter represents the type of logical operation to be performed. It can
   * be either "OR" or "AND".
   */
  private void orAndTruthValues(ASTNode rand1, ASTNode rand2, ASTNodeType type) {
    if (type == ASTNodeType.OR) {
        valueStack.push((rand1.getType() == ASTNodeType.TRUE || rand2.getType() == ASTNodeType.TRUE) ? getTrueNode() : getFalseNode());
    } else {
        valueStack.push((rand1.getType() == ASTNodeType.TRUE && rand2.getType() == ASTNodeType.TRUE) ? getTrueNode() : getFalseNode());
    }
}


  /**
   * The function `augTuples` augments two tuples by adding the second tuple as a sibling to the first
   * tuple.
   */
  private void augTuples() {
    ASTNode rand1 = valueStack.pop();
    ASTNode rand2 = valueStack.pop();

    if (rand1.getType() != ASTNodeType.TUPLE) {
        EvaluationError.printError(rand1.getSourceLineNumber(),
                "Cannot augment a non-tuple \"" + rand1.getValue() + "\"");
    }

    ASTNode childNode = rand1.getChild();
    if (childNode == null) {
        rand1.setChild(rand2);
    } else {
        while (childNode.getSibling() != null) {
            childNode = childNode.getSibling();
        }
        childNode.setSibling(rand2);
    }
    rand2.setSibling(null);

    valueStack.push(rand1);
}


  // RULE 7
  /**
   * The function applies a unary operation to an ASTNode and returns true if the operation was
   * successfully applied, otherwise it returns false.
   * 
   * @param rator The parameter "rator" is an ASTNode object representing the operator of a unary
   * operation.
   * @return The method is returning a boolean value. If the switch statement matches the type of the
   * rator with either NOT or NEG, it will perform the corresponding operation (not() or neg()) and
   * return true. If the type does not match any of the cases, it will return false.
   */
  private boolean applyUnaryOperation(ASTNode rator){
    switch(rator.getType()){
      case NOT:
        not();
        return true;
      case NEG:
        neg();
        return true;
      default:
        return false;
    }
  }

  private void not(){
    ASTNode rand = valueStack.pop();
    if(rand.getType()!=ASTNodeType.TRUE && rand.getType()!=ASTNodeType.FALSE)
      EvaluationError.printError(rand.getSourceLineNumber(), "Expecting a truthvalue; was given \""+rand.getValue()+"\"");

    if(rand.getType()==ASTNodeType.TRUE)
      pushFalseNode();
    else
      pushTrueNode();
  }

  /**
   * The neg() function takes a value from the valueStack, checks if it is an integer, and if so,
   * negates it by multiplying it by -1 and pushes the result back onto the valueStack.
   */
  private void neg() {
    ASTNode rand = valueStack.pop();
    if (rand.getType() != ASTNodeType.INTEGER) {
        EvaluationError.printError(rand.getSourceLineNumber(),
                "Expecting an integer; was given \"" + rand.getValue() + "\"");
    }

    int negValue = -1 * Integer.parseInt(rand.getValue());

    ASTNode result = new ASTNode();
    result.setType(ASTNodeType.INTEGER);
    result.setValue(Integer.toString(negValue));
    valueStack.push(result);
}


  //RULE 3
  /**
   * The function applies different rules based on the type of the rator (operator) and performs
   * corresponding actions.
   * 
   * @param currentDelta The currentDelta parameter is of type Delta and represents the current delta
   * being evaluated in the program.
   * @param node The `node` parameter represents the current AST node that is being evaluated. It is of
   * type `ASTNode`, which is a class representing an abstract syntax tree node in the code. The `node`
   * parameter is used to keep track of the current position in the code during evaluation.
   * @param currentEnv The `currentEnv` parameter is an object of type `Environment` which represents
   * the current environment or scope in which the code is being executed. It contains mappings of
   * variable names to their corresponding values.
   * @param currentControlStack The `currentControlStack` parameter is a `Stack` that keeps track of
   * the control flow in the program. It is used to store `ASTNode` objects representing the current
   * state of the program execution.
   */
  private void applyGamma(Delta currentDelta, ASTNode node, Environment currentEnv, Stack<ASTNode> currentControlStack){
    ASTNode rator = valueStack.pop();
    ASTNode rand = valueStack.pop();

    if(rator.getType()==ASTNodeType.DELTA){
      Delta nextDelta = (Delta) rator;
      Environment newEnv = new Environment();
      newEnv.setParent(nextDelta.getLinkedEnv());
      
      //RULE 4
      if(nextDelta.getBoundVars().size()==1){
        newEnv.addMapping(nextDelta.getBoundVars().get(0), rand);
      }
      //RULE 11
      else{
        if(rand.getType()!=ASTNodeType.TUPLE)
          EvaluationError.printError(rand.getSourceLineNumber(), "Expected a tuple; was given \""+rand.getValue()+"\"");
        
        for(int i = 0; i < nextDelta.getBoundVars().size(); i++){
          newEnv.addMapping(nextDelta.getBoundVars().get(i), getNthTupleChild((Tuple)rand, i+1)); 
        }
      }
      
      processControlStack(nextDelta, newEnv);
      return;
    }
    else if(rator.getType()==ASTNodeType.YSTAR){
      //RULE 12
      if(rand.getType()!=ASTNodeType.DELTA)
        EvaluationError.printError(rand.getSourceLineNumber(), "Expected a Delta; was given \""+rand.getValue()+"\"");
      
      Eta etaNode = new Eta();
      etaNode.setDelta((Delta)rand);
      valueStack.push(etaNode);
      return;
    }
    else if(rator.getType()==ASTNodeType.ETA){
      //RULE 13
      //push back the rand, the eta and then the delta it contains
      valueStack.push(rand);
      valueStack.push(rator);
      valueStack.push(((Eta)rator).getDelta());
      currentControlStack.push(node);
      currentControlStack.push(node);
      return;
    }
    else if(rator.getType()==ASTNodeType.TUPLE){
      tupleSelection((Tuple)rator, rand);
      return;
    }
    else if(evaluateReservedIdentifiers(rator, rand, currentControlStack))
      return;
    else
      EvaluationError.printError(rator.getSourceLineNumber(), "Don't know how to evaluate \""+rator.getValue()+"\"");
  }

  /**
   * The function evaluates reserved identifiers and performs specific actions based on the
   * identifier's value.
   * 
   * @param rator The parameter "rator" is of type ASTNode, which represents the operator node in an
   * abstract syntax tree.
   * @param rand The parameter "rand" is an ASTNode object representing the operand of the operator
   * being evaluated.
   * @param currentControlStack The `currentControlStack` parameter is a stack of `ASTNode` objects. It
   * is used to keep track of the control flow in the program.
   * @return The method returns a boolean value.
   */
  private boolean evaluateReservedIdentifiers(ASTNode rator, ASTNode rand, Stack<ASTNode> currentControlStack) {
    String ratorValue = rator.getValue();
    switch (ratorValue) {
        case "Isinteger":
            checkTypeAndPushTrueOrFalse(rand, ASTNodeType.INTEGER);
            return true;
        case "Isstring":
            checkTypeAndPushTrueOrFalse(rand, ASTNodeType.STRING);
            return true;
        case "Isdummy":
            checkTypeAndPushTrueOrFalse(rand, ASTNodeType.DUMMY);
            return true;
        case "Isfunction":
            checkTypeAndPushTrueOrFalse(rand, ASTNodeType.DELTA);
            return true;
        case "Istuple":
            checkTypeAndPushTrueOrFalse(rand, ASTNodeType.TUPLE);
            return true;
        case "Istruthvalue":
            if (rand.getType() == ASTNodeType.TRUE || rand.getType() == ASTNodeType.FALSE) {
                pushTrueNode();
            } else {
                pushFalseNode();
            }
            return true;
        case "Stem":
            stem(rand);
            return true;
        case "Stern":
            stern(rand);
            return true;
        case "Conc":
        case "conc": // Typing errors
            conc(rand, currentControlStack);
            return true;
        case "Print":
        case "print": // Typing errors
            printNodeValue(rand);
            pushDummyNode();
            return true;
        case "ItoS":
            itos(rand);
            return true;
        case "Order":
            order(rand);
            return true;
        case "Null":
            isNullTuple(rand);
            return true;
        default:
            return false;
    }
}


  /**
   * The function checks the type of a given ASTNode and pushes a true or false node based on the
   * comparison result.
   * 
   * @param rand The "rand" parameter is an ASTNode object, which represents a node in an abstract
   * syntax tree.
   * @param type The "type" parameter is the expected ASTNodeType that we want to check against the
   * type of the "rand" ASTNode.
   */
  private void checkTypeAndPushTrueOrFalse(ASTNode rand, ASTNodeType type){
    if(rand.getType()==type)
      pushTrueNode();
    else
      pushFalseNode();
  }

  private void pushTrueNode(){
    ASTNode trueNode = new ASTNode();
    trueNode.setType(ASTNodeType.TRUE);
    trueNode.setValue("true");
    valueStack.push(trueNode);
  }
  
  /**
   * The function creates a new ASTNode representing a boolean false value and pushes it onto a stack.
   */
  private void pushFalseNode(){
    ASTNode falseNode = new ASTNode();
    falseNode.setType(ASTNodeType.FALSE);
    falseNode.setValue("false");
    valueStack.push(falseNode);
  }

  /**
   * The function pushes a dummy node onto a stack.
   */
  private void pushDummyNode(){
    ASTNode falseNode = new ASTNode();
    falseNode.setType(ASTNodeType.DUMMY);
    valueStack.push(falseNode);
  }

  /**
   * The function takes an ASTNode as input, checks if it is a string, trims it to the first character
   * if it is not empty, and pushes it onto a value stack.
   * 
   * @param rand The parameter "rand" is an ASTNode object.
   */
  private void stem(ASTNode rand){
    if(rand.getType()!=ASTNodeType.STRING)
      EvaluationError.printError(rand.getSourceLineNumber(), "Expected a string; was given \""+rand.getValue()+"\"");
    
    if(rand.getValue().isEmpty())
      rand.setValue("");
    else
      rand.setValue(rand.getValue().substring(0,1));
    
    valueStack.push(rand);
  }

  private void stern(ASTNode rand){
    if(rand.getType()!=ASTNodeType.STRING)
      EvaluationError.printError(rand.getSourceLineNumber(), "Expected a string; was given \""+rand.getValue()+"\"");
    
    if(rand.getValue().isEmpty() || rand.getValue().length()==1)
      rand.setValue("");
    else
      rand.setValue(rand.getValue().substring(1));
    
    valueStack.push(rand);
  }

  /**
   * The conc function concatenates two strings and pushes the result onto the value stack.
   * 
   * @param rand1 The `rand1` parameter is an `ASTNode` object representing the first random value.
   * @param currentControlStack A stack that keeps track of the control flow of the program. It is used
   * to determine the current execution context and to navigate through the program's control
   * structures.
   */
  private void conc(ASTNode rand1, Stack<ASTNode> currentControlStack){
    currentControlStack.pop();
    ASTNode rand2 = valueStack.pop();
    if(rand1.getType()!=ASTNodeType.STRING || rand2.getType()!=ASTNodeType.STRING)
      EvaluationError.printError(rand1.getSourceLineNumber(), "Expected two strings; was given \""+rand1.getValue()+"\", \""+rand2.getValue()+"\"");

    ASTNode result = new ASTNode();
    result.setType(ASTNodeType.STRING);
    result.setValue(rand1.getValue()+rand2.getValue());
    
    valueStack.push(result);
  }

  private void itos(ASTNode rand){
    if(rand.getType()!=ASTNodeType.INTEGER)
      EvaluationError.printError(rand.getSourceLineNumber(), "Expected an integer; was given \""+rand.getValue()+"\"");
    
    rand.setType(ASTNodeType.STRING); 
    valueStack.push(rand);
  }

  private void order(ASTNode rand){
    if(rand.getType()!=ASTNodeType.TUPLE)
      EvaluationError.printError(rand.getSourceLineNumber(), "Expected a tuple; was given \""+rand.getValue()+"\"");

    ASTNode result = new ASTNode();
    result.setType(ASTNodeType.INTEGER);
    result.setValue(Integer.toString(getNumChildren(rand)));
    
    valueStack.push(result);
  }

  private void isNullTuple(ASTNode rand){
    if(rand.getType()!=ASTNodeType.TUPLE)
      EvaluationError.printError(rand.getSourceLineNumber(), "Expected a tuple; was given \""+rand.getValue()+"\"");

    if(getNumChildren(rand)==0)
      pushTrueNode();
    else
      pushFalseNode();
  }

  // RULE 10
  /**
   * The function `tupleSelection` selects a specific element from a tuple based on the given index.
   * 
   * @param rator The parameter "rator" is of type Tuple, which represents a tuple object. It is used
   * to perform operations on the tuple.
   * @param rand The parameter "rand" is an ASTNode object, which represents a node in an abstract
   * syntax tree. It is used to specify the index of the tuple element that needs to be selected.
   */
  private void tupleSelection(Tuple rator, ASTNode rand){
    if(rand.getType()!=ASTNodeType.INTEGER)
      EvaluationError.printError(rand.getSourceLineNumber(), "Non-integer tuple selection with \""+rand.getValue()+"\"");

    ASTNode result = getNthTupleChild(rator, Integer.parseInt(rand.getValue()));
    if(result==null)
      EvaluationError.printError(rand.getSourceLineNumber(), "Tuple selection index "+rand.getValue()+" out of bounds");

    valueStack.push(result);
  }


  private ASTNode getNthTupleChild(Tuple tupleNode, int n){
    ASTNode childNode = tupleNode.getChild();
    for(int i=1;i<n;++i){ 
      if(childNode==null)
        break;
      childNode = childNode.getSibling();
    }
    return childNode;
  }

  private void handleIdentifiers(ASTNode node, Environment currentEnv){
    if(currentEnv.lookup(node.getValue())!=null) // RULE 1
      valueStack.push(currentEnv.lookup(node.getValue()));
    else if(isReservedIdentifier(node.getValue()))
      valueStack.push(node);
    else
      EvaluationError.printError(node.getSourceLineNumber(), "Undeclared identifier \""+node.getValue()+"\"");
  }

  //RULE 9
  /**
   * The function creates a tuple node and populates it with child nodes from the value stack.
   * 
   * @param node The parameter "node" is an ASTNode object, which represents a node in an abstract
   * syntax tree.
   */
  private void createTuple(ASTNode node){
    int numChildren = getNumChildren(node);
    Tuple tupleNode = new Tuple();
    if(numChildren==0){
      valueStack.push(tupleNode);
      return;
    }

    ASTNode childNode = null, tempNode = null;
    for(int i=0;i<numChildren;++i){
      if(childNode==null)
        childNode = valueStack.pop();
      else if(tempNode==null){
        tempNode = valueStack.pop();
        childNode.setSibling(tempNode);
      }
      else{
        tempNode.setSibling(valueStack.pop());
        tempNode = tempNode.getSibling();
      }
    }
    tempNode.setSibling(null);
    tupleNode.setChild(childNode);
    valueStack.push(tupleNode);
  }

  // RULE 8
  /**
   * The function handles the execution of a Beta node by evaluating a condition and then adding the
   * appropriate body to the control stack based on the condition result.
   * 
   * @param node The parameter "node" is of type Beta, which is a specific type of ASTNode. It
   * represents a conditional statement with an if-else structure.
   * @param currentControlStack The `currentControlStack` parameter is a `Stack` data structure that
   * stores `ASTNode` objects. It is used to keep track of the control flow in the program.
   */
  private void handleBeta(Beta node, Stack<ASTNode> currentControlStack){
    ASTNode conditionResultNode = valueStack.pop();

    if(conditionResultNode.getType()!=ASTNodeType.TRUE && conditionResultNode.getType()!=ASTNodeType.FALSE)
      EvaluationError.printError(conditionResultNode.getSourceLineNumber(), "Expecting a truthvalue; found \""+conditionResultNode.getValue()+"\"");

    if(conditionResultNode.getType()==ASTNodeType.TRUE)
      currentControlStack.addAll(node.getThenBody());
    else
      currentControlStack.addAll(node.getElseBody());
  }

  private int getNumChildren(ASTNode node){
    int numChildren = 0;
    ASTNode childNode = node.getChild();
    while(childNode!=null){
      numChildren++;
      childNode = childNode.getSibling();
    }
    return numChildren;
  }
  
  private void printNodeValue(ASTNode rand){
    String evaluationResult = rand.getValue();
    evaluationResult = evaluationResult.replace("\\t", "\t");
    evaluationResult = evaluationResult.replace("\\n", "\n");
    System.out.print(evaluationResult);
  }

  // The above code is defining a private method called `isReservedIdentifier` in Java. This method
  // takes a string parameter called `value` and checks if it is a reserved identifier.
private boolean isReservedIdentifier(String value) {
    List<String> reservedIdentifiers = Arrays.asList(
        "Isinteger", "Isstring", "Istuple", "Isdummy", "Istruthvalue", "Isfunction",
        "ItoS", "Order", "Conc", "conc", "Stern", "Stem", "Null", "Print", "print", "neg"
    );

    return reservedIdentifiers.contains(value);
}


}
