package csem;

import ast.ASTNode;
import ast.ASTNodeType;

//The Eta class represents an eta closure in a Java program, which is used for partial application.

public class Eta extends ASTNode{
  private Delta delta;
  
  public Eta(){
    setType(ASTNodeType.ETA);
  }
  
  //used if the program evaluation results in a partial application
  @Override
  public String getValue(){
    return "[eta closure: "+delta.getBoundVars().get(0)+": "+delta.getIndex()+"]";
  }
  
  // The function accepts a NodeCopier object and returns a copy of the current object.
  // The parameter "nodeCopier" is an object of type "NodeCopier".
  // The method is returning an object of type Eta.
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
