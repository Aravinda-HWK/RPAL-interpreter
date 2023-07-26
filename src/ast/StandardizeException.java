package ast;

/**
 * The StandardizeException class is a custom exception that extends the RuntimeException class and
 * provides a constructor to set the exception message.
 */
public class StandardizeException extends RuntimeException{
  private static final long serialVersionUID = 1L;
  
  public StandardizeException(String message){
    super(message);
  }
}
