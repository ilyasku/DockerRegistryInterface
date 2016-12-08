package dockerregistry.exceptions;

public class NoRepositoryByThatNameException extends RuntimeException{
    
    public NoRepositoryByThatNameException(String message){
        super(message);
    }
}
