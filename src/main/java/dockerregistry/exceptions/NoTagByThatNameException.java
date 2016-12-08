package dockerregistry.exceptions;

public class NoTagByThatNameException extends RuntimeException{
    
    public NoTagByThatNameException(String message){
        super(message);
    }
}
