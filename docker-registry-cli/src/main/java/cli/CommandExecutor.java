package cli;

import cli.parser.ConnectionOptionType;
import dockerregistry.model.HttpInterface;
import dockerregistry.model.Image;
import dockerregistry.model.LocalConfigHandler;
import dockerregistry.model.Registry;
import java.io.BufferedReader;
import java.io.Console;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        System.out.print("  password: ");
        
        Console console = System.console();
        String password = new String(console.readPassword());        
        
        String usernamePassword = username + ":" + password;
        String base64encodedCredentials = Base64.getEncoder().encodeToString(usernamePassword.getBytes());                
        
        localConfigHandler.writeCredentials(base64encodedCredentials);                
    }
    
    public String[] executeListRepositories() throws IOException {                
        return registry.getRepositoryNames();        
    }    

    public Map<String, String[]> executeListTags(List<String> repositoryNames) {
        Map<String, String[]> tagNames = new HashMap<>();
        for (String repositoryName : repositoryNames){
            try {
                String[] tagNamesForThisRepository = registry.getTagNames(repositoryName);
                tagNames.put(repositoryName, tagNamesForThisRepository);
            } catch (IOException ex) {
                Logger.getLogger(CommandExecutor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return tagNames;
    }

    public void executeDeleteImages(List<String> commandArgs) throws IOException {
        for (String imageName : commandArgs){
            System.out.println("deleting " + imageName);
            Image image = registry.getImageByName(imageName);
            // the registry's DELETE for images requires the repository name 
            // and the hash of the image's manifest
            String repositoryName = image.getRepoName();
            String manifestHash = image.getManifest().getManifestBlob().getHash();
            registry.deleteImageFromRegistry(repositoryName, manifestHash);
        }        
    }
    





    
    
}
