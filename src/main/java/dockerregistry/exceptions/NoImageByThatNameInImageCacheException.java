package dockerregistry.exceptions;

public class NoImageByThatNameInImageCacheException extends RuntimeException {

    public NoImageByThatNameInImageCacheException(String message) {
        super(message);
    }    
}
