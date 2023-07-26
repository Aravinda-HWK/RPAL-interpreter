package csem;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import ast.ASTNode;
import ast.ASTNodeType;


  //The Delta class represents a node in an abstract syntax tree with bound variables, 
  //a linked environment, a body, and an index.

public class Delta extends ASTNode{
  private List<String> boundVars;
  private Environment linkedEnv;
  private Stack<ASTNode> body;
  private int index;
  
  // The `public Delta()` method is a constructor for the `Delta` class. 
  // It initializes the `boundVars` list as an empty `ArrayList<String>`. 
  // It also sets the type of the `Delta` object to`ASTNodeType.DELTA`.
  public Delta(){
    setType(ASTNodeType.DELTA);
    boundVars = new ArrayList<String>();
  }
  
  public Delta accept(NodeCopier nodeCopier){
    return nodeCopier.copy(this);
  }
  

  // The code you provided is a class called `Delta` that extends `ASTNode`. It represents a node in an
  // abstract syntax tree with bound variables, a linked environment, a body, and an index.
  @Override
  public String getValue(){
    return "[lambda closure: "+boundVars.get(0)+": "+index+"]";
  }

  public List<String> getBoundVars(){
    return boundVars;
  }
  
  public void addBoundVars(String boundVar){
    boundVars.add(boundVar);
  }
  
  public void setBoundVars(List<String> boundVars){
    this.boundVars = boundVars;
  }
  
  public Stack<ASTNode> getBody(){
    return body;
  }
  
  public void setBody(Stack<ASTNode> body){
    this.body = body;
  }
  
  public int getIndex(){
    return index;
  }

  public void setIndex(int index){
    this.index = index;
  }

  public Environment getLinkedEnv(){
    return linkedEnv;
  }

  public void setLinkedEnv(Environment linkedEnv){
    this.linkedEnv = linkedEnv;
  }
}
