package cli.parser;

import cli.Command;
import exceptions.InvalidCommandException;
import exceptions.NoCommandFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Parser {    
    private static final List<String> COMMANDS = Arrays.asList("set-url", "set-registry", 
        "set-credentials", 
        "delete-images", 
        "list-repositories",
        "list-tags");
    
    public static Command parseCompleteCommand(String[] completeCommandArguments){
        Command command = new Command();
        
        int commandIndex = findCommandIndex(completeCommandArguments);        
        

        String commandString = completeCommandArguments[commandIndex];
        if (!COMMANDS.contains(commandString)){
            throw new InvalidCommandException(commandString);
        }
        Map<ConnectionOptionType, String> connectionOptions = ConnectionOptionParser.parse(Arrays.copyOfRange(completeCommandArguments, 0, commandIndex));
        String[] arrayOfCommandArguments;
        if (commandIndex + 1 < completeCommandArguments.length){
            arrayOfCommandArguments = Arrays.copyOfRange(completeCommandArguments, commandIndex + 1, completeCommandArguments.length);
        }
        else{
            arrayOfCommandArguments = new String[]{};
        }
        List<String> commandArguments = Arrays.asList(arrayOfCommandArguments);
                        
        command.setConnectionOptions(connectionOptions);
        command.setCommandString(commandString);
        command.setCommandArgs(commandArguments);        
        
        return command;
    }
    
    /**
     * Finds the index of the Command-argument.
     * The "Command"-argument is the first argument that is not preceded by 
     * an argument starting with a '-' character.
     */
    private static int findCommandIndex(String[] completeCommandArguments) {
        String currentArgument;
        int i = 0;
        while (i < completeCommandArguments.length){
            currentArgument = completeCommandArguments[i];
            if (currentArgument.startsWith("-")){
                i += 2;
            }
            else{
                return i;
            }
        }        
        throw new NoCommandFoundException();
    }    

}
