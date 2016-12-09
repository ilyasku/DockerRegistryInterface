package dockerregistry.cli;

import java.util.Arrays;

public class CLIMain {

    private final static CommandLineParser parser = new CommandLineParser();
    
    public static void main(String[] args) {
        String url = args[0];
        Request request = parser.parseRequest(Arrays.copyOfRange(args, 1, args.length));        
    }
    
}
