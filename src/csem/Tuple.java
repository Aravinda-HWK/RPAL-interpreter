package csem;

import ast.ASTNode;
import ast.ASTNodeType;


//The Tuple class represents a tuple in an abstract syntax tree (AST) and provides methods for
//printing its value and accepting a NodeCopier.

public class Tuple extends ASTNode{
  
  public Tuple(){
    setType(ASTNodeType.TUPLE);
  }
  

// The getValue() function beautifully handles string representation of a linked list of ASTNodes. 
// It elegantly returns the values of child nodes, gracefully handling cases with or without child nodes.

  @Override
  public String getValue(){
    ASTNode childNode = getChild();
    if(childNode==null)
      return "nil";
    
    String printValue = "(";
    while(childNode.getSibling()!=null){
      printValue += childNode.getValue() + ", ";
      childNode = childNode.getSibling();
    }
    printValue += childNode.getValue() + ")";
    return printValue;
  }
  
  // The function accepts a NodeCopier object and returns a Tuple object.
  // The parameter "nodeCopier" is an object of type "NodeCopier".
  // The method is returning a Tuple.
  public Tuple accept(NodeCopier nodeCopier){
    return nodeCopier.copy(this);
  }
  
}
