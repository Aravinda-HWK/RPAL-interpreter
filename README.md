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

For C/C++:
$ ./rpal20 file_name

For Java:
$ java rpal20 file_name

Replace `file_name` with the name of the file that contains the RPAL program as the input.

Input Format:
The input file should contain the RPAL program to be evaluated. The program should follow the syntax and lexical rules specified in RPAL_Lex.pdf and RPAL_Grammar.pdf, respectively.

Example Input:
Let Sum(A) = Psum (A, Order A)
where rec Psum (T, N) = N eq 0 -> 0
| Psum(T, N-1) + T N
in Print (Sum(1, 2, 3, 4, 5))

Output Format:
The output of the program will be the result of evaluating the RPAL program using the CSE machine.

Example Output:
15

Implementation Details:

- The lexical analyzer tokenizes the input RPAL program into individual tokens.
- The parser constructs the Abstract Syntax Tree (AST) based on the grammar rules.
- The AST is then converted to a Standardized Tree (ST) by applying standardization rules.
- The CSE machine evaluates the Standardized Tree and produces the final output.

Dependencies:
The project is implemented in Java/C/C++ and does not have any external dependencies.

Contributing:
This project is for educational purposes, and contributions are welcome. If you find any issues or have suggestions for improvements, feel free to open an issue or create a pull request.

License:
This project is licensed under the MIT License. See the LICENSE file for details.

Acknowledgments:
The RPAL language and related documentation are for educational use and based on academic work.

Contact:
For any queries or feedback, you can reach out to [Your Name] at [Your Email Address].

Happy coding!
