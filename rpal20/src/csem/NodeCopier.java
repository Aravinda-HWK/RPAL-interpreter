package csem;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import ast.ASTNode;


public class NodeCopier{
  

//The function "copy" creates a deep copy of an ASTNode object, including its child and sibling nodes.
//astNode The `astNode` parameter is an instance of the `ASTNode` class, which represents a node in an abstract syntax tree.
//The method is returning a copy of the given ASTNode object.

public ASTNode copy(ASTNode astNode) {
  ASTNode copy = new ASTNode();
  if (astNode.getChild() != null) {
      copy.setChild(astNode.getChild().accept(this));
  }
  if (astNode.getSibling() != null) {
      copy.setSibling(astNode.getSibling().accept(this));
  }
  copy.setType(astNode.getType());
  copy.setValue(astNode.getValue());
  copy.setSourceLineNumber(astNode.getSourceLineNumber());
  return copy;
}

  

  // The function `copy` creates a deep copy of a `Beta` object, including its child, sibling, type,
  // value, source line number, then body, and else bod
  // beta The parameter "beta" is an object of type Beta.
  // The method is returning a copy of the Beta object.

  public Beta copy(Beta beta) {
    Beta copy = new Beta();
    if (beta.getChild() != null) {
        copy.setChild(beta.getChild().accept(this));
    }
    if (beta.getSibling() != null) {
        copy.setSibling(beta.getSibling().accept(this));
    }
    copy.setType(beta.getType());
    copy.setValue(beta.getValue());
    copy.setSourceLineNumber(beta.getSourceLineNumber());

    Stack<ASTNode> thenBodyCopy = new Stack<>();
    for (ASTNode thenBodyElement : beta.getThenBody()) {
        thenBodyCopy.add(thenBodyElement.accept(this));
    }
    copy.setThenBody(thenBodyCopy);

    Stack<ASTNode> elseBodyCopy = new Stack<>();
    for (ASTNode elseBodyElement : beta.getElseBody()) {
        elseBodyCopy.add(elseBodyElement.accept(this));
    }
    copy.setElseBody(elseBodyCopy);

    return copy;
}

  

  // The function "copy" creates a deep copy of an object of type Eta, including its child, sibling,
  // type, value, source line number, and delta.
  // eta The parameter "eta" is an object of type Eta.
  // The method is returning a copy of the Eta object.

  public Eta copy(Eta eta) {
    Eta copy = new Eta();
    if (eta.getChild() != null) {
        copy.setChild(eta.getChild().accept(this));
    }
    if (eta.getSibling() != null) {
        copy.setSibling(eta.getSibling().accept(this));
    }
    copy.setType(eta.getType());
    copy.setValue(eta.getValue());
    copy.setSourceLineNumber(eta.getSourceLineNumber());

    copy.setDelta(eta.getDelta().accept(this));

    return copy;
}

  

  // The function `copy` creates a deep copy of a `Delta` object, including its child, sibling, type,
  // value, index, source line number, body, bound variables, and linked environment.
  // delta The parameter "delta" is an object of type "Delta".
  // The method is returning a copy of the Delta object.

  public Delta copy(Delta delta) {
    Delta copy = new Delta();
    if (delta.getChild() != null) {
        copy.setChild(delta.getChild().accept(this));
    }
    if (delta.getSibling() != null) {
        copy.setSibling(delta.getSibling().accept(this));
    }
    copy.setType(delta.getType());
    copy.setValue(delta.getValue());
    copy.setIndex(delta.getIndex());
    copy.setSourceLineNumber(delta.getSourceLineNumber());

    Stack<ASTNode> bodyCopy = new Stack<>();
    for (ASTNode bodyElement : delta.getBody()) {
        bodyCopy.add(bodyElement.accept(this));
    }
    copy.setBody(bodyCopy);

    List<String> boundVarsCopy = new ArrayList<>(delta.getBoundVars());
    copy.setBoundVars(boundVarsCopy);

    copy.setLinkedEnv(delta.getLinkedEnv());

    return copy;
}

  

  // The function "copy" creates a deep copy of a Tuple object, including its child and sibling references.
  // tuple The parameter "tuple" is an object of type "Tuple".
  // The method is returning a copy of the input Tuple object.

  public Tuple copy(Tuple tuple) {
    Tuple copy = new Tuple();
    if (tuple.getChild() != null) {
        copy.setChild(tuple.getChild().accept(this));
    }
    if (tuple.getSibling() != null) {
        copy.setSibling(tuple.getSibling().accept(this));
    }
    copy.setType(tuple.getType());
    copy.setValue(tuple.getValue());
    copy.setSourceLineNumber(tuple.getSourceLineNumber());
    return copy;
}

}
