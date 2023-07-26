package csem;

import java.util.HashMap;
import java.util.Map;

import ast.ASTNode;


//The Environment class represents a scope in a programming language and stores the mapping of
//variable names to their corresponding values.

public class Environment{
  private Environment parent;
  private Map<String, ASTNode> nameValueMap;
  
  public Environment(){
    nameValueMap = new HashMap<String, ASTNode>();
  }

  public Environment getParent(){
    return parent;
  }

  public void setParent(Environment parent){
    this.parent = parent;
  }
  

// The `lookup` method in the `Environment` class is used to search for a variable name in the current
// scope and its parent scopes. It takes a `key` parameter, which is the variable name to be looked up.
  public ASTNode lookup(String key){
    ASTNode retValue = null;
    Map<String, ASTNode> map = nameValueMap;
    
    retValue = map.get(key);
    
    if(retValue!=null)
      return retValue.accept(new NodeCopier());
    
    if(parent!=null)
      return parent.lookup(key);
    else
      return null;
  }
  
  public void addMapping(String key, ASTNode value){
    nameValueMap.put(key, value);
  }
}
