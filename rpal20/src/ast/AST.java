package ast;

import java.util.ArrayDeque;
import java.util.Stack;

import csem.Beta;
import csem.Delta;

public class AST{
  // The above code is declaring private instance variables in a Java class.
  private ASTNode root;
  private ArrayDeque<PendingDeltaBody> pendingDeltaBodyQueue;
  private boolean standardized;
  private Delta currentDelta;
  private Delta rootDelta;
  private int deltaIndex;

  public AST(ASTNode node){
    this.root = node;
  }
  public void print(){
    preOrderPrint(root,"");
  }

  /**
   * The function recursively prints the details of an ASTNode in pre-order traversal.
   * 
   * @param node The current node in the AST (Abstract Syntax Tree) that we want to print.
   * @param printPrefix The printPrefix parameter is a string that represents the prefix to be added to
   * the printed details of each ASTNode. It is used to create a hierarchical structure in the printed
   * output, indicating the level of nesting of each node in the tree.
   */
  private void preOrderPrint(ASTNode node, String printPrefix){
    if(node==null)
      return;

    printASTNodeDetails(node, printPrefix);
    preOrderPrint(node.getChild(),printPrefix+".");
    preOrderPrint(node.getSibling(),printPrefix);
  }

  /**
   * The function prints the details of an ASTNode, including its type and value if it is an
   * identifier, integer, or string.
   * 
   * @param node The `node` parameter is an object of type `ASTNode`, which represents a node in an
   * abstract syntax tree (AST). The ASTNode class likely has properties such as `type` and `value`
   * that store information about the node.
   * @param printPrefix The `printPrefix` parameter is a string that is used as a prefix when printing
   * the details of the ASTNode. It is added before the type and value of the node when printing.
   */
  private void printASTNodeDetails(ASTNode node, String printPrefix){
    if(node.getType() == ASTNodeType.IDENTIFIER || node.getType() == ASTNodeType.INTEGER || node.getType() == ASTNodeType.STRING){
      System.out.printf(printPrefix+node.getType().getPrintName()+"\n",node.getValue());
    }
    else
      System.out.println(printPrefix+node.getType().getPrintName());
  }


 /**
  * The function "standardize" recursively standardizes a binary tree starting from the root node.
  */
  public void standardize(){
    standardize(root);
    standardized = true;
  }

 
  private void standardize(ASTNode node){
    // The above code is checking if the current node has a child. If it does, it assigns the child
    // node to a variable called childNode. Then, it enters a while loop that iterates as long as
    // childNode is not null. Inside the loop, it calls a method called standardize on the childNode.
    // After that, it updates the childNode variable to the sibling of the current childNode. This
    // process continues until there are no more siblings of the current childNode.
    if(node.getChild()!=null){
      for (ASTNode childNode = node.getChild(); childNode != null; childNode = childNode.getSibling()) {
        standardize(childNode);
    }    
    }
  
    switch(node.getType()){
      // The above code is a case statement in a Java switch statement. It is handling the case where
      // the node type is LET.
      case LET:
        ASTNode getNode = node.getChild();
        if(getNode.getType()!=ASTNodeType.EQUAL)
          throw new StandardizeException("LET/WHERE: left child is not EQUAL"); 
        ASTNode e = getNode.getChild().getSibling();
        getNode.getChild().setSibling(getNode.getSibling());
        getNode.setSibling(e);
        getNode.setType(ASTNodeType.LAMBDA);
        node.setType(ASTNodeType.GAMMA);
        break;
      // The above code is a case statement in Java. It is handling the case "WHERE".
      case WHERE:        
        getNode = node.getChild().getSibling();
        node.getChild().setSibling(null);
        getNode.setSibling(node.getChild());
        node.setChild(getNode);
        node.setType(ASTNodeType.LET);
        standardize(node);
        break;
     // The above code is a case statement in a Java switch statement. It is handling the case where
     // the node type is FCNFORM.
      case FCNFORM:       
        ASTNode childSibling = node.getChild().getSibling();
        node.getChild().setSibling(constructLambdaChain(childSibling));
        node.setType(ASTNodeType.EQUAL);
        break;
      // The above code is a case statement in Java. It is handling the case when the node type is
      // "AT".
      case AT:
        ASTNode e1 = node.getChild();
        ASTNode n = e1.getSibling();
        ASTNode e2 = n.getSibling();
        ASTNode gammaNode = new ASTNode();
        gammaNode.setType(ASTNodeType.GAMMA);
        gammaNode.setChild(n);
        n.setSibling(e1);
        e1.setSibling(null);
        gammaNode.setSibling(e2);
        node.setChild(gammaNode);
        node.setType(ASTNodeType.GAMMA);
        break;
      // The above code is handling a specific case called "WITHIN" in an abstract syntax tree (AST)
      // for a programming language.
      case WITHIN:
        if(node.getChild().getType()!=ASTNodeType.EQUAL || node.getChild().getSibling().getType()!=ASTNodeType.EQUAL)
          throw new StandardizeException("WITHIN: one of the children is not EQUAL"); //safety
        ASTNode x1 = node.getChild().getChild();
        e1 = x1.getSibling();
        ASTNode x2 = node.getChild().getSibling().getChild();
        e2 = x2.getSibling();
        ASTNode lambdaNode = new ASTNode();
        lambdaNode.setType(ASTNodeType.LAMBDA);
        x1.setSibling(e2);
        lambdaNode.setChild(x1);
        lambdaNode.setSibling(e1);
        gammaNode = new ASTNode();
        gammaNode.setType(ASTNodeType.GAMMA);
        gammaNode.setChild(lambdaNode);
        x2.setSibling(gammaNode);
        node.setChild(x2);
        node.setType(ASTNodeType.EQUAL);
        break;
    // The code is handling a case called "SIMULTDEF" in an abstract syntax tree (AST) for a
    // programming language.
      case SIMULTDEF:       
        ASTNode commaNode = new ASTNode();
        commaNode.setType(ASTNodeType.COMMA);
        ASTNode tauNode = new ASTNode();
        tauNode.setType(ASTNodeType.TAU);
        ASTNode childNode = node.getChild();
        while(childNode!=null){
          populateCommaAndTauNode(childNode, commaNode, tauNode);
          childNode = childNode.getSibling();
        }
        commaNode.setSibling(tauNode);
        node.setChild(commaNode);
        node.setType(ASTNodeType.EQUAL);
        break;
     // The code is handling a case where the node type is REC (recursive). It first checks if the
     // child node is of type EQUAL, and if not, it throws a StandardizeException.
      case REC:
        childNode = node.getChild();
        if(childNode.getType()!=ASTNodeType.EQUAL)
          throw new StandardizeException("REC: child is not EQUAL"); //safety
        ASTNode x = childNode.getChild();
        lambdaNode = new ASTNode();
        lambdaNode.setType(ASTNodeType.LAMBDA);
        lambdaNode.setChild(x); //x is already attached to e
        ASTNode yStarNode = new ASTNode();
        yStarNode.setType(ASTNodeType.YSTAR);
        yStarNode.setSibling(lambdaNode);
        gammaNode = new ASTNode();
        gammaNode.setType(ASTNodeType.GAMMA);
        gammaNode.setChild(yStarNode);
        ASTNode xWithSiblingGamma = new ASTNode(); //same as x except the sibling is not e but gamma
        xWithSiblingGamma.setChild(x.getChild());
        xWithSiblingGamma.setSibling(gammaNode);
        xWithSiblingGamma.setType(x.getType());
        xWithSiblingGamma.setValue(x.getValue());
        node.setChild(xWithSiblingGamma);
        node.setType(ASTNodeType.EQUAL);
        break;
      // The above code is a case statement in a switch statement. It is checking if the case is
      // LAMBDA. If it is, it assigns the sibling of the child node to the variable childSibling. Then,
      // it sets the sibling of the child node to the result of the method constructLambdaChain,
      // passing in the childSibling as an argument.
      case LAMBDA:
        childSibling = node.getChild().getSibling();
        node.getChild().setSibling(constructLambdaChain(childSibling));
        break;
      default:       
        break;
    }
  }

  /**
   * The function populates a comma node and a tau node with the child nodes of an equal node.
   * 
   * @param equalNode The equalNode parameter is an ASTNode representing an EQUAL node in an abstract
   * syntax tree.
   * @param commaNode The `commaNode` parameter is an ASTNode representing a comma symbol in the
   * abstract syntax tree.
   * @param tauNode The tauNode parameter is an ASTNode object that represents the node where the "tau"
   * value will be populated.
   */
  private void populateCommaAndTauNode(ASTNode equalNode, ASTNode commaNode, ASTNode tauNode){
    if(equalNode.getType()!=ASTNodeType.EQUAL)
      throw new StandardizeException("SIMULTDEF: one of the children is not EQUAL"); //safety
    ASTNode x = equalNode.getChild();
    ASTNode e = x.getSibling();
    setChild(commaNode, x);
    setChild(tauNode, e);
  }

  /**
   * The function sets a child node for a given parent node, ensuring that the child node is added as a
   * sibling to any existing child nodes.
   * 
   * @param parentNode The parent node is the node to which the child node will be added as a child or
   * sibling. It is an instance of the ASTNode class.
   * @param childNode The childNode parameter is an ASTNode object that represents the node that you
   * want to set as a child of the parentNode.
   */
  private void setChild(ASTNode parentNode, ASTNode childNode){
    if(parentNode.getChild()==null)
      parentNode.setChild(childNode);
    else{
      ASTNode lastSibling = parentNode.getChild();
      while(lastSibling.getSibling()!=null)
        lastSibling = lastSibling.getSibling();
      lastSibling.setSibling(childNode);
    }
    childNode.setSibling(null);
  }

  /**
   * The function constructs a chain of lambda nodes from a given ASTNode.
   * 
   * @param node The "node" parameter is an ASTNode object representing a node in an abstract syntax
   * tree.
   * @return The method is returning an ASTNode object.
   */
  private ASTNode constructLambdaChain(ASTNode node){
    if(node.getSibling()==null)
      return node;
    
    ASTNode lambdaNode = new ASTNode();
    lambdaNode.setType(ASTNodeType.LAMBDA);
    lambdaNode.setChild(node);
    if(node.getSibling().getSibling()!=null)
      node.setSibling(constructLambdaChain(node.getSibling()));
    return lambdaNode;
  }

 
 /**
  * The function creates and processes delta objects for a given root object.
  * 
  * @return The method is returning a Delta object.
  */
  public Delta createDeltas(){
    pendingDeltaBodyQueue = new ArrayDeque<PendingDeltaBody>();
    deltaIndex = 0;
    currentDelta = createDelta(root);
    processPendingDeltaStack();
    return rootDelta;
  }

  /**
   * The function creates a new Delta object, sets its body and index, and assigns it as the
   * currentDelta.
   * 
   * @param startBodyNode The startBodyNode parameter is an ASTNode object that represents the starting
   * node of the body for which a Delta object is being created.
   * @return The method is returning a Delta object.
   */
  private Delta createDelta(ASTNode startBodyNode){
    PendingDeltaBody pendingDelta = new PendingDeltaBody();
    pendingDelta.startNode = startBodyNode;
    pendingDelta.body = new Stack<ASTNode>();
    pendingDeltaBodyQueue.add(pendingDelta);
    
    Delta d = new Delta();
    d.setBody(pendingDelta.body);
    d.setIndex(deltaIndex++);
    currentDelta = d;
    
    if(startBodyNode==root)
      rootDelta = currentDelta;
    
    return d;
  }

  /**
   * The function processes pending delta bodies by iterating through the queue and building delta
   * bodies.
   */
  private void processPendingDeltaStack(){
    while(!pendingDeltaBodyQueue.isEmpty()){
      PendingDeltaBody pendingDeltaBody = pendingDeltaBodyQueue.pop();
      buildDeltaBody(pendingDeltaBody.startNode, pendingDeltaBody.body);
    }
  }
  
  // The above code is a Java method called `buildDeltaBody` that takes an `ASTNode` object and a
  // `Stack<ASTNode>` object as parameters.
  private void buildDeltaBody(ASTNode node, Stack<ASTNode> body){
    if(node.getType()==ASTNodeType.LAMBDA){ 
      Delta d = createDelta(node.getChild().getSibling());
      if(node.getChild().getType()==ASTNodeType.COMMA){ 
        ASTNode commaNode = node.getChild();
        ASTNode childNode = commaNode.getChild();
        while(childNode!=null){
          d.addBoundVars(childNode.getValue());
          childNode = childNode.getSibling();
        }
      }
      else
        d.addBoundVars(node.getChild().getValue());
      body.push(d); 
      return;
    }
    else if(node.getType()==ASTNodeType.CONDITIONAL){
      ASTNode conditionNode = node.getChild();
      ASTNode thenNode = conditionNode.getSibling();
      ASTNode elseNode = thenNode.getSibling();
      
      //Add a Beta node.
      Beta betaNode = new Beta();
      
      buildDeltaBody(thenNode, betaNode.getThenBody());
      buildDeltaBody(elseNode, betaNode.getElseBody());
      
      body.push(betaNode);
      
      buildDeltaBody(conditionNode, body);
      
      return;
    }
    

    // The above code is a recursive function called "buildDeltaBody" that takes in an ASTNode object
    // and a List object called "body". It pushes the current node into the body list and then iterates
    // through all the child nodes of the current node. For each child node, it recursively calls the
    // buildDeltaBody function and passes in the child node and the body list. This process continues
    // until there are no more child nodes.
    body.push(node);
    ASTNode childNode = node.getChild();
    while(childNode!=null){
      buildDeltaBody(childNode, body);
      childNode = childNode.getSibling();
    }
  }

  /**
   * The class "PendingDeltaBody" is a private class that contains a stack of ASTNodes and a startNode,
   * and the class "isStandardized" is a public method that returns a boolean indicating whether the
   * object is standardized.
   */
  private class PendingDeltaBody{
    Stack<ASTNode> body;
    ASTNode startNode;
  }

  public boolean isStandardized(){
    return standardized;
  }
}