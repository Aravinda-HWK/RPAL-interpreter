package csem;

//The EvaluationError class provides a method to print an error message along with the source line
//number and exit the program.
public class EvaluationError{
  
  public static void printError(int sourceLineNumber, String message){
    System.out.println(":"+sourceLineNumber+": "+message);
    System.exit(1);
  }

}
