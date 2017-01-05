package cli;

import cli.parser.ConnectionOptionType;
import cli.parser.Parser;
import dockerregistry.model.HttpInterface;
import dockerregistry.model.LocalConfigHandler;
import dockerregistry.model.Mapper;
import dockerregistry.model.Registry;
import exceptions.InvalidCommandException;
import exceptions.WrongNumberOfArgumentsForThisCommandException;
import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommandLineInterface {
    
    private static LocalConfigHandler localConfigHandler;
    static {

        try {
            localConfigHandler = new LocalConfigHandler();
        } catch (IOException ex) {            
            Logger.getLogger(CommandLineInterface.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }

    }
    
    public CommandLineInterface(){        
    }
    
    public static void main(String[] args){
        String stringToPrint = "";
        
        Command command = Parser.parseCompleteCommand(args);
        try{
            stringToPrint += execute(command);
        }
        catch (WrongNumberOfArgumentsForThisCommandException ex){
            
        } catch (IOException ex) {
            Logger.getLogger(CommandLineInterface.class.getName()).log(Level.SEVERE, null, ex);
        }                                    
                
        System.out.print(stringToPrint);
    }
    
    private static String execute(Command command) throws IOException{
        
        String returnString = "";
        
        // set-up dependencies
        HttpInterface httpInterface = initializeHttpInterface(command.getConnectionOptions());
        Mapper mapper = new Mapper();
        Registry registry = new Registry();
        registry.setMapper(mapper);
        CommandExecutor commandExecutor = new CommandExecutor();
        commandExecutor.setRegistry(registry);
        commandExecutor.setHttpInterface(httpInterface);
        commandExecutor.setLocalConfigHandler(localConfigHandler);
                        
        final String commandString = command.getCommandString();
        // execute command --> fill returnString with response
        if (commandString.equals("set-url")){
            if (command.getCommandArgs().size() != 1){
                throw new WrongNumberOfArgumentsForThisCommandException(1);
            }
            commandExecutor.executeSetUrl(command.getCommandArgs().get(0));
        }
        else if (commandString.equals("set-credentials")){
            if (!command.getCommandArgs().isEmpty()) {
                throw new WrongNumberOfArgumentsForThisCommandException(0);
            }
            commandExecutor.executeSetCredentials();
        }
        else if (commandString.equals("list-repositories")){
            if (!command.getCommandArgs().isEmpty()){
                throw new WrongNumberOfArgumentsForThisCommandException(0);
            }
            String[] arrayOfRepositories = commandExecutor.executeListRepositories();
            for (String repositoryName : arrayOfRepositories){
                returnString += repositoryName + "\n";
            }
        }
        else if (commandString.equals("list-tags")) {
            if (command.getCommandArgs().isEmpty()){
                throw new WrongNumberOfArgumentsForThisCommandException(0);
            }
            Map<String, String[]> tagNames = commandExecutor.executeListTags(command.getCommandArgs());
            for (String repositoryName : tagNames.keySet()){
                if (tagNames.keySet().size() > 1){
                    returnString += "[" + repositoryName  + "]" + "\n";
                }
                for (String tagName : tagNames.get(repositoryName)) {
                    returnString += tagName + "\n";
                }
            }            
        }        
        else{
            throw new InvalidCommandException(commandString);
        }
        return returnString;
    }    
    
    private static String getUrl(Map<ConnectionOptionType, String> connectionOptions) throws IOException {
        if (connectionOptions.containsKey(ConnectionOptionType.REGISTRY_URL)){            
            return connectionOptions.get(ConnectionOptionType.REGISTRY_URL);
        }
        else {
            try{
                return localConfigHandler.getUrl();
            }
            catch (IOException ex){
                // no local url file found -> create a new one
                System.out.println("No local url file found (docker-registry looks for a default url there). Create one now!");
                System.out.print("Type default url: ");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
                String url = bufferedReader.readLine();
                localConfigHandler.writeUrl(url);
                return url;
            }
        }
    }


    private static HttpInterface initializeHttpInterface(Map<ConnectionOptionType, String> connectionOptions) throws IOException{
        HttpInterface httpInterface = new HttpInterface(getUrl(connectionOptions));        
        if (connectionOptions.containsKey(ConnectionOptionType.USER_NAME)){
            String username = connectionOptions.get(ConnectionOptionType.USER_NAME);
            String password = readPasswordFromStdin();
        
            String usernamePassword = username + ":" + password;
            String base64encodedCredentials = Base64.getEncoder().encodeToString(usernamePassword.getBytes());
            
            httpInterface.setEncodedUsernamePassword(base64encodedCredentials);   
        }        
        else {
            try {
                String base64encodedCredentials = localConfigHandler.getCredentials();
                httpInterface.setEncodedUsernamePassword(base64encodedCredentials);
            } catch (IOException ex) {
                 // this means no credentials file exists 
                 // => do nothing (try connection without credentials)
            }
        }
        return httpInterface;            
    }
    
    private static String readPasswordFromStdin() {
        System.out.print("password: ");
        Console console = System.console();
        return new String(console.readPassword());
    }
       
}
