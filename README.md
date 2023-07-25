RPAL Language Parser and CSE Machine

Overview:
This project is an implementation of a lexical analyzer, parser, Abstract Syntax Tree (AST), Standardized Tree (ST), and CSE machine for the RPAL language. The RPAL language is a programming language designed for teaching purposes. The project aims to read an RPAL program from an input file, parse it, build the AST, convert it to ST, and finally evaluate it using the CSE machine.

Features:

- Lexical Analyzer: Tokenizes the input RPAL program based on the provided lexical rules.
- Parser: Generates an Abstract Syntax Tree (AST) by applying the grammar rules specified for RPAL.
- Standardization: Converts the AST into a Standardized Tree (ST) by applying standardization rules.
- CSE Machine: Evaluates the Standardized Tree and produces the final output.

Usage:
To run the program, use the following commands:

For Java:
$ java rpal20 file_name

Replace `file_name` with the name of the file that contains the RPAL program as the input.

Input Format:
The input file should contain the RPAL program to be evaluated. The program should follow the syntax and lexical rules specified in RPAL_Lex.pdf and RPAL_Grammar.pdf, respectively.

Example Input:
let rec Rev S =
S eq '' -> '' | (Rev(Stern S)) @Conc(Stem S)
within
Pairs (S1, S2) =
not (Isstring S1 & Isstring S2)
-> 'both args not strings' | P (Rev S1, Rev S2)
where rec P (S1, S2) =
S1 eq '' & S2 eq '' -> nil | (Stern S1 eq '' & Stern S2 ne '') or
(Stern S1 ne '' & Stern S2 eq '')
-> 'unequal length strings'
| (P (Stern S1, Stern S2) aug ((Stem S1) @Conc(Stem S2)))
in Print(Pairs('abe','def'))

Output Format:
The output of the program will be the result of evaluating the RPAL program using the CSE machine.
