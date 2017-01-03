package exceptions;

public class InvalidConnectionOptionException extends RuntimeException{

    public InvalidConnectionOptionException(String option) {
        super(option);        
    }        
}
