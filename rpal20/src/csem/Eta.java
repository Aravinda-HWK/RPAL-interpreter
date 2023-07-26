package csem;

import ast.ASTNode;
import ast.ASTNodeType;

// The Eta class is a subclass of ASTNode and represents a node in an abstract syntax tree with a type of ETA.

public class Eta extends ASTNode{
  private Delta delta;
  
  public Eta(){
    setType(ASTNodeType.ETA);
  }
  

 //The getValue() function returns a string representation of an eta closure.
 //The method is returning a string that represents an eta closure. The string includes the
 //bound variable and the index of the delta.
 
  @Override
  public String getValue(){
    return "[eta closure: "+delta.getBoundVars().get(0)+": "+delta.getIndex()+"]";
  }
  
  public Eta accept(NodeCopier nodeCopier){
    return nodeCopier.copy(this);
  }

  public Delta getDelta(){
    return delta;
  }

  public void setDelta(Delta delta){
    this.delta = delta;
  }
  
}
