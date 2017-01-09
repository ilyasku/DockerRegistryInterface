package exceptions;

public class WrongNumberOfArgumentsForThisCommandException extends RuntimeException{
    
    public WrongNumberOfArgumentsForThisCommandException(int numberOfExpectedArguments){        
        super(Integer.toString(numberOfExpectedArguments));
    }
            
    public WrongNumberOfArgumentsForThisCommandException(String numberOfExpectedArguments){
        super(numberOfExpectedArguments);
    }
}
