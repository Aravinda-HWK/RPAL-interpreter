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

Example Output:
15

Implementation Details:

- The lexical analyzer tokenizes the input RPAL program into individual tokens.
- The parser constructs the Abstract Syntax Tree (AST) based on the grammar rules.
- The AST is then converted to a Standardized Tree (ST) by applying standardization rules.
- The CSE machine evaluates the Standardized Tree and produces the final output.

///This will output the abstract syntax tree
For Java:
$ java rpal20 file_name -ast

Output:

let
.within
..rec
...function_form
....<ID:Rev>
....<ID:S>
....->
.....eq
......<ID:S>
......<STR:''>
.....<STR:''>
.....@
......gamma
.......<ID:Rev>
.......gamma
........<ID:Stern>
........<ID:S>
......<ID:Conc>
......gamma
.......<ID:Stem>
.......<ID:S>
..function_form
...<ID:Pairs>
...,
....<ID:S1>
....<ID:S2>
...where
....->
.....not
......&
.......gamma
........<ID:Isstring>
........<ID:S1>
.......gamma
........<ID:Isstring>
........<ID:S2>
.....<STR:'both args not strings'>
.....gamma
......<ID:P>
......tau
.......gamma
........<ID:Rev>
........<ID:S1>
.......gamma
........<ID:Rev>
........<ID:S2>
....rec
.....function_form
......<ID:P>
......,
.......<ID:S1>
.......<ID:S2>
......->
.......&
........eq
.........<ID:S1>
.........<STR:''>
........eq
.........<ID:S2>
.........<STR:''>
.......<nil>
.......->
........or
.........&
..........eq
...........gamma
............<ID:Stern>
............<ID:S1>
...........<STR:''>
..........ne
...........gamma
............<ID:Stern>
............<ID:S2>
...........<STR:''>
.........&
..........ne
...........gamma
............<ID:Stern>
............<ID:S1>
...........<STR:''>
..........eq
...........gamma
............<ID:Stern>
............<ID:S2>
...........<STR:''>
........<STR:'unequal length strings'>
........aug
.........gamma
..........<ID:P>
..........tau
...........gamma
............<ID:Stern>
............<ID:S1>
...........gamma
............<ID:Stern>
............<ID:S2>
.........@
..........gamma
...........<ID:Stem>
...........<ID:S1>
..........<ID:Conc>
..........gamma
...........<ID:Stem>
...........<ID:S2>
.gamma
..<ID:Print>
..gamma
...<ID:Pairs>
...tau
....<STR:'abe'>
....<STR:'def'>
(ad, be, ef)

///This will output the standadize tree
For Java:
$ java rpal20 file_name -st

Output:

gamma
.lambda
..<ID:Pairs>
..gamma
...<ID:Print>
...gamma
....<ID:Pairs>
....tau
.....<STR:'abe'>
.....<STR:'def'>
.gamma
..lambda
...<ID:Rev>
...lambda
....,
.....<ID:S1>
.....<ID:S2>
....gamma
.....lambda
......<ID:P>
......->
.......not
........&
.........gamma
..........<ID:Isstring>
..........<ID:S1>
.........gamma
..........<ID:Isstring>
..........<ID:S2>
.......<STR:'both args not strings'>
.......gamma
........<ID:P>
........tau
.........gamma
..........<ID:Rev>
..........<ID:S1>
.........gamma
..........<ID:Rev>
..........<ID:S2>
.....gamma
......<Y*>
......lambda
.......<ID:P>
.......lambda
........,
.........<ID:S1>
.........<ID:S2>
........->
.........&
..........eq
...........<ID:S1>
...........<STR:''>
..........eq
...........<ID:S2>
...........<STR:''>
.........<nil>
.........->
..........or
...........&
............eq
.............gamma
..............<ID:Stern>
..............<ID:S1>
.............<STR:''>
............ne
.............gamma
..............<ID:Stern>
..............<ID:S2>
.............<STR:''>
...........&
............ne
.............gamma
..............<ID:Stern>
..............<ID:S1>
.............<STR:''>
............eq
.............gamma
..............<ID:Stern>
..............<ID:S2>
.............<STR:''>
..........<STR:'unequal length strings'>
..........aug
...........gamma
............<ID:P>
............tau
.............gamma
..............<ID:Stern>
..............<ID:S1>
.............gamma
..............<ID:Stern>
..............<ID:S2>
...........gamma
............gamma
.............<ID:Conc>
.............gamma
..............<ID:Stem>
..............<ID:S1>
............gamma
.............<ID:Stem>
.............<ID:S2>
..gamma
...<Y*>
...lambda
....<ID:Rev>
....lambda
.....<ID:S>
.....->
......eq
.......<ID:S>
.......<STR:''>
......<STR:''>
......gamma
.......gamma
........<ID:Conc>
........gamma
.........<ID:Rev>
.........gamma
..........<ID:Stern>
..........<ID:S>
.......gamma
........<ID:Stem>
........<ID:S>
(ad, be, ef)
