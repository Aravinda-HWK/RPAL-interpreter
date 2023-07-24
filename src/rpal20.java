import ast.AST;
import csem.*;
import scanner.*;
import parser.*;

import java.io.IOException;

public class rpal20 {
    public static void main(String[] args) throws Exception { 
      if (args.length==2){
        String fileName = args[0];
        AST ast = buildAST(fileName, true);
        if (args[1].equals("-ast")){
          ast.print();
          ast.standardize();          
        }
        else if (args[1].equals("-st")){
          ast.standardize();
          ast.print();
        }
        evaluateST(ast); 
      }
      else{
        String fileName = args[0];
        AST ast = buildAST(fileName, true);
        ast.standardize();
        evaluateST(ast); 
      }       
            
    }
    private static AST buildAST(String fileName, boolean printOutput){
        AST ast = null;
        try{
          Scanner scanner = new Scanner(fileName);
          Parser parser = new Parser(scanner);
          ast = parser.buildAST();
        }catch(IOException e){
          throw new ParseException("ERROR: Could not read from file: " + fileName);
        }
        return ast;
      }
    
      private static void evaluateST(AST ast){
        CSEMachine csem = new CSEMachine(ast);
        csem.evaluateProgram();
        System.out.println();
      }

    
}
