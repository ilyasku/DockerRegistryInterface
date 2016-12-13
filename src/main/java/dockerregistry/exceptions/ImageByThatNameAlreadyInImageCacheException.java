package dockerregistry.exceptions;

public class ImageByThatNameAlreadyInImageCacheException extends RuntimeException{
    public ImageByThatNameAlreadyInImageCacheException(String message){
        super(message);
    }
}
