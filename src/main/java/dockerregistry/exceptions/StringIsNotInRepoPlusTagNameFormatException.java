package dockerregistry.exceptions;

public class StringIsNotInRepoPlusTagNameFormatException extends RuntimeException{
    public StringIsNotInRepoPlusTagNameFormatException(String message){
        super(message);
    }
}
