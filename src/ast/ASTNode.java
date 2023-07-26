package ast;

import csem.NodeCopier;

public class ASTNode{
 // These are instance variables of the `ASTNode` class.
  private ASTNodeType type;
  private String value;
  private ASTNode child;
  private ASTNode sibling;
  private int sourceLineNumber;


  /**
   * The function returns the line number in the source code where it is called.
   * 
   * @return The method is returning the value of the variable "sourceLineNumber".
   */
  public int getSourceLineNumber(){
    return sourceLineNumber;
  }

  /**
   * The function sets the source line number for a given object.
   * 
   * @param sourceLineNumber The sourceLineNumber parameter is an integer that represents the line
   * number in the source code where a particular action or event occurs.
   */
  public void setSourceLineNumber(int sourceLineNumber){
    this.sourceLineNumber = sourceLineNumber;
  }


  //Check if the node is a leaf (has no child and no sibling).
  public boolean isLeaf() {
    return child == null && sibling == null;
}

//Count the total number of nodes in the subtree rooted at this node.
public int countNodes() {
    int count = 1; // Count the current node.
    if (child != null) {
        count += child.countNodes(); // Count nodes in the child subtree recursively.
    }
    if (sibling != null) {
        count += sibling.countNodes(); // Count nodes in the sibling subtree recursively.
    }
    return count;
}

//Create a string representation of the subtree rooted at this node.
public String treeToString() {
    StringBuilder sb = new StringBuilder();
    treeToStringHelper(sb, 0);
    return sb.toString();
}

private void treeToStringHelper(StringBuilder sb, int indentLevel) {
    for (int i = 0; i < indentLevel; i++) {
        sb.append("  "); // Indent based on the level in the tree.
    }
    sb.append(getName());
    if (value != null) {
        sb.append(" (").append(value).append(")");
    }
    sb.append("\n");
    if (child != null) {
        child.treeToStringHelper(sb, indentLevel + 1); // Recurse to the child subtree.
    }
    if (sibling != null) {
        sibling.treeToStringHelper(sb, indentLevel); // Recurse to the sibling subtree.
    }
}
  
 

  /**
   * The function returns the child node of an ASTNode.
   * 
   * @return The method is returning an ASTNode object.
   */
  public ASTNode getChild(){
    return child;
  }

  /**
   * The function sets the child node of an ASTNode object.
   * 
   * @param child The "child" parameter is an object of type ASTNode.
   */
  public void setChild(ASTNode child){
    this.child = child;
  }

  /**
   * The function returns the sibling of a given ASTNode.
   * 
   * @return The method is returning the sibling of the current ASTNode.
   */
  public ASTNode getSibling(){
    return sibling;
  }

   /**
   * The getName() function returns the name of the type.
   * 
   * @return The method is returning the name of the type.
   */
  public String getName(){
    return type.name();
  }

  /**
   * The function returns the type of the AST node.
   * 
   * @return The method is returning the value of the variable "type", which is of type ASTNodeType.
   */
  public ASTNodeType getType(){
    return type;
  }

 /**
  * The function sets the type of a node in an abstract syntax tree.
  * 
  * @param type The "type" parameter is of type ASTNodeType, which is an enumeration representing
  * different types of AST (Abstract Syntax Tree) nodes.
  */
  public void setType(ASTNodeType type){
    this.type = type;
  }

 /**
  * The function sets the sibling of the current ASTNode object.
  * 
  * @param sibling The "sibling" parameter is an object of type ASTNode.
  */
  public void setSibling(ASTNode sibling){
    this.sibling = sibling;
  }

 /**
  * The getValue() function returns the value of a variable.
  * 
  * @return The method is returning the value of the variable "value".
  */
  public String getValue(){
    return value;
  }

  /**
   * The function sets the value of a variable.
   * 
   * @param value The parameter "value" is a String type parameter.
   */
  public void setValue(String value){
    this.value = value;
  }

 /**
  * The accept function is used to accept a NodeCopier and return a copied ASTNode.
  * 
  * @param nodeCopier The parameter "nodeCopier" is an object of type "NodeCopier".
  * @return The method is returning an ASTNode object.
  */
  public ASTNode accept(NodeCopier nodeCopier){
    return nodeCopier.copy(this);
  }  
}
