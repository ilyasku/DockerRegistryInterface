package cli;

import cli.parser.ConnectionOptionType;
import exceptions.InvalidCommandException;
import exceptions.WrongNumberOfArgumentsForThisCommandException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

public class CommandLineInterface {
    
    public static void main(String[] args){
        System.out.println("Running docker-registry-cli!");
    }
    
    public String[] execute(Command command) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        
        updateHttpInterfaceByConnectionOptions(command.getConnectionOptions());
        
        CommandExecutor commandExecutor = new CommandExecutor();
        
        final String commandString = command.getCommandString();
        if (commandString.equals("set-url")){
            if (command.getCommandArgs().size() != 1){
                throw new WrongNumberOfArgumentsForThisCommandException(1);
            }
            return commandExecutor.executeSetUrl(command.getCommandArgs().get(0));
        }
        else if (commandString.equals("set-credentials")){
            if (!command.getCommandArgs().isEmpty()) {
                throw new WrongNumberOfArgumentsForThisCommandException(0);
            }
            return commandExecutor.executeSetCredentials();
        }
        else if (commandString.equals("list-repositories")){
            if (!command.getCommandArgs().isEmpty()){
                throw new WrongNumberOfArgumentsForThisCommandException(0);
            }
            return commandExecutor.executeListRepositories(command.getConnectionOptions());
        }
        throw new InvalidCommandException(commandString);
    }

    private void updateHttpInterfaceByConnectionOptions(Map<ConnectionOptionType, String> connectionOptions) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
