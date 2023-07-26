package parser;

/**
 * The ParseException class is a subclass of RuntimeException that represents an exception that occurs
 * during parsing.
 */
public class ParseException extends RuntimeException{
  private static final long serialVersionUID = 1L;
  
  public ParseException(String message){
    super(message);
  }
}
