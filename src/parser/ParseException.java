package parser;

//ParseException is a custom runtime exception class used for handling parsing errors in the application.
//It extends the RuntimeException class to signify that it is an unchecked exception.
//The class holds a single constructor that accepts a custom error message to be displayed when the exception is thrown.
//The serialVersionUID field is used for serialization purposes.

public class ParseException extends RuntimeException{
  private static final long serialVersionUID = 1L;
  
  public ParseException(String message){
    super(message);
  }

}
