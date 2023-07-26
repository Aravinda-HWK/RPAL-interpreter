import ast.AST;
import csem.*;
import scanner.*;
import parser.*;

import java.io.IOException;

public class rpal20 {
    public static void main(String[] args) throws Exception {

        boolean stFlag = false;
        boolean astFlag = false;
        String fileName = null;

        // Parse command-line arguments
        for (int i = 0; i < args.length; i++) {
            String cmdOption = args[i];

            if (cmdOption.equals("-st")) {
                stFlag = true;
            }else if(cmdOption.equals("-ast")){
                astFlag =true;
            } else if (i == args.length - 1) {
                fileName = args[i];
            } else {
                System.out.println("Invalid command-line argument: " + cmdOption);
                return;
            }
        }

        if (fileName == null) {
            System.out.println("Please specify a file name.");
            return;
        }

        AST ast = buildAST(fileName, true);
        if (astFlag) {
            ast.print();
            ast.standardize();
        } else if(stFlag) {
          ast.standardize();
          ast.print();
        }else{
          ast.standardize();
        }
        evaluateST(ast);
    }

    private static AST buildAST(String fileName, boolean printOutput) {
        AST ast = null;
        try {
            Scanner scanner = new Scanner(fileName);
            Parser parser = new Parser(scanner);
            ast = parser.buildAST();
        } catch (IOException e) {
            throw new ParseException("ERROR: Could not read from file: " + fileName);
        }
        return ast;
    }

    private static void evaluateST(AST ast) {
        CSEMachine csem = new CSEMachine(ast);
        csem.evaluateProgram();
        System.out.println();
    }
}

