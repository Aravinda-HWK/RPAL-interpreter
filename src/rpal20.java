import ast.AST;
import csem.*;
import scanner.*;
import parser.*;

import java.io.IOException;

public class rpal20 {
    public static void main(String[] args) throws Exception {
        // Check if the correct number of arguments is provided (2)
        if (args.length == 2) {
            // First argument is the test file name
            String test_file_name = args[0];
            
            // Build the Abstract Syntax Tree (AST) from the test file
            AST abstract_syntax_tree = Ast_Build(test_file_name);
            
            // Check the second argument to determine the action (-ast or -st)
            if (args[1].equals("-ast")) {
                // Print the AST and standardize it
                abstract_syntax_tree.print();
                abstract_syntax_tree.standardize();
            } else if (args[1].equals("-st")) {
                // Standardize the AST and then print it
                abstract_syntax_tree.standardize();
                abstract_syntax_tree.print();
            }

            // Evaluate the AST using the CSEMachine
            Evaluate_ST(abstract_syntax_tree);
        } else {
            // If only one argument is provided, assume it's the test file name
            String test_file_name = args[0];
            
            // Build the Abstract Syntax Tree (AST) from the test file
            AST abstract_syntax_tree = Ast_Build(test_file_name);
            
            // Standardize the AST
            abstract_syntax_tree.standardize();

            // Evaluate the AST using the CSEMachine
            Evaluate_ST(abstract_syntax_tree);
        }
    }

    // Build the Abstract Syntax Tree (AST) from the input file
    private static AST Ast_Build(String test_file_name) {
        AST abstract_syntax_tree = null;
        try {
            // Create a Scanner and Parser to process the input file
            Scanner scanner = new Scanner(test_file_name);
            Parser parser = new Parser(scanner);
            // Build the AST
            abstract_syntax_tree = parser.buildAST();
        } catch (IOException e) {
            // If an error occurs while processing the file, throw a ParseException
            throw new ParseException("There is no " + test_file_name + " in the bin folder");
        }
        // Return the built AST
        return abstract_syntax_tree;
    }

    // Evaluate the AST using the CSEMachine
    private static void Evaluate_ST(AST abstract_syntax_tree) {
        // Create a CSEMachine and pass the AST to it for evaluation
        CSEMachine csem = new CSEMachine(abstract_syntax_tree);
        csem.evaluateProgram();
    }
}
