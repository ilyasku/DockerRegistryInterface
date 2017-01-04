package cli;

import cli.parser.ConnectionOptionType;
import dockerregistry.model.HttpInterface;
import dockerregistry.model.LocalConfigHandler;
import dockerregistry.model.Registry;
import exceptions.InvalidCommandException;
import exceptions.WrongNumberOfArgumentsForThisCommandException;
import java.io.BufferedReader;
import java.io.Console;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Map;

public class CommandExecutor {
    
    private Registry registry;
    private LocalConfigHandler localConfigHandler;
    private HttpInterface httpInterface;
    
    

    public void setHttpInterface(HttpInterface httpInterface) {
        this.httpInterface = httpInterface;
        registry.setHttpInterface(httpInterface);
    }
    
    public void setLocalConfigHandler(LocalConfigHandler aLocalConfigHandler) {
        localConfigHandler = aLocalConfigHandler;
    }

    public void setRegistry(Registry aRegistry) {
        registry = aRegistry;
    }
    
    public void executeSetUrl(String url) throws FileNotFoundException, UnsupportedEncodingException{
        localConfigHandler.writeUrl(url);        
    }
    
    public void executeSetCredentials() throws IOException {
        System.out.println("Set credentials:");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("  username: ");
        String username = bufferedReader.readLine();
        System.out.println();
        System.out.print("  password: ");
        String password = bufferedReader.readLine();
        
        System.err.println(username);
        System.err.println(password);
        
        String usernamePassword = username + ":" + password;
        String base64encodedCredentials = Base64.getEncoder().encodeToString(usernamePassword.getBytes());
        
        System.err.println(base64encodedCredentials);
        
        localConfigHandler.writeCredentials(base64encodedCredentials);                
    }
    
    public String[] executeListRepositories(Map<ConnectionOptionType, String> connectionOptions) throws IOException {
        String url = getUrl(connectionOptions);
        if (connectionOptions.containsKey(ConnectionOptionType.USER_NAME)){
            String username = connectionOptions.get(ConnectionOptionType.USER_NAME);
            String password = readPasswordFromStdin();
        
            String usernamePassword = username + ":" + password;
            String base64encodedCredentials = Base64.getEncoder().encodeToString(usernamePassword.getBytes());
            
            httpInterface.setEncodedUsernamePassword(base64encodedCredentials);   
        }        
        else {
            String base64encodedCredentials = localConfigHandler.getCredentials();
            httpInterface.setEncodedUsernamePassword(base64encodedCredentials);
        }
        
        return registry.getRepositoryNames();        
    }    

    private String getUrl(Map<ConnectionOptionType, String> connectionOptions) throws IOException {
        if (connectionOptions.containsKey(ConnectionOptionType.REGISTRY_URL)){
            return connectionOptions.get(ConnectionOptionType.REGISTRY_URL);
        }
        else {
            return localConfigHandler.getUrl();
        }
    }

    private String readPasswordFromStdin() {
        System.out.print("password: ");
        Console console = System.console();
        return new String(console.readPassword());
    }
    
    
}
