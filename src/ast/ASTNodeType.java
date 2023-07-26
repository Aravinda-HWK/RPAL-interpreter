package ast;

// The code is defining an enumeration called `ASTNodeType` in Java. An enumeration is a special type
// in Java that represents a fixed set of constants.
public enum ASTNodeType {
    PAREN("<()>"),
    COMMA(","),
    WITHIN("within"),
    SIMULTDEF("and"),
    REC("rec"),
    EQUAL("="),
    FCNFORM("function_form"),
    BETA(""),
    DELTA(""),
    ETA(""),
    TUPLE(""),
    
    IDENTIFIER("<ID:%s>"),
    STRING("<STR:'%s'>"),
    INTEGER("<INT:%s>"),
  
    LET("let"),
    LAMBDA("lambda"),
    WHERE("where"),
  
    TAU("tau"),
    AUG("aug"),
    CONDITIONAL("->"),
  
    OR("or"),
    AND("&"),
    NOT("not"),
    GR("gr"),
    GE("ge"),
    LS("ls"),
    LE("le"),
    EQ("eq"),
    NE("ne"),
  
    PLUS("+"),
    MINUS("-"),
    NEG("neg"),
    MULT("*"),
    DIV("/"),
    EXP("**"),
    AT("@"),
  
    GAMMA("gamma"),
    TRUE("<true>"),
    FALSE("<false>"),
    NIL("<nil>"),
    DUMMY("<dummy>"),
  
    YSTAR("<Y*>");
  
    private String printName; 
  
    private ASTNodeType(String name){
        printName = name;
    }

    public String getPrintName(){
        return printName;
    }
}
