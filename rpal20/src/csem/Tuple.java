package csem;

import ast.ASTNode;
import ast.ASTNodeType;


// The Tuple class represents a tuple in an abstract syntax tree (AST) and provides methods for
// printing its value and accepting a NodeCopier.

public class Tuple extends ASTNode{
  
  public Tuple(){
    setType(ASTNodeType.TUPLE);
  }
  
// The `getValue()` function is a method in the `Tuple` class that returns a string representation of a
// linked list of `ASTNodes`. The method checks if there are any child nodes. If there are no child
// nodes, it returns the string "nil". If there are child nodes, it iterates through the linked list,
// concatenating their values with commas and wrapping them in parentheses. The resulting string is then returned.

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
  
  public Tuple accept(NodeCopier nodeCopier){
    return nodeCopier.copy(this);
  }
  
}
