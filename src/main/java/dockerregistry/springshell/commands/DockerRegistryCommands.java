package dockerregistry.springshell.commands;

import dockerregistry.model.Blob;
import dockerregistry.model.HttpInterface;
import dockerregistry.model.Image;
import dockerregistry.model.Mapper;
import dockerregistry.model.Registry;
import dockerregistry.model.RegistryCacheInMemory;
import dockerregistry.springshell.ObjectToCliStringFormatter;
import java.io.IOException;
import java.util.Map;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

@Component
public class DockerRegistryCommands implements CommandMarker{
    
    private String url = "http://localhost:5000/";
    private HttpInterface httpInterface = new HttpInterface(url);
    private final Mapper mapper = new Mapper();
    private RegistryCacheInMemory registryCache = new RegistryCacheInMemory();
    private final Registry registry;
    
    private final ObjectToCliStringFormatter stringFormatter = new ObjectToCliStringFormatter();
    
    public DockerRegistryCommands() {
        registry = new Registry();
        registry.setHttpInterface(httpInterface);
        registry.setMapper(mapper);
        registry.setRegistryCache(registryCache);
    }
    
    @CliCommand(value = "set url", help = "Set URL of the repository you want to connect to.")
    public String setUrl(
    @CliOption(key = {"","url"}, mandatory = true, help = "URL of repository you want to connect to") String url){
        httpInterface = new HttpInterface(url);
        registry.setHttpInterface(httpInterface);
        return "set HttpInterface to url \"" + url + "\"";
    }
    
    @CliCommand(value = "list repository names", help = "List names of all repositories")
    public String listRepositoryNames(){
        String returnString = "Names of all repositories:\n";
        try {
            String[] repositoryNames = registry.getRepositoryNames();
            for (int i = 0; i < repositoryNames.length; i++) {
                returnString = returnString + "  * " + repositoryNames[i] + "\n";
            }
        
            return returnString;
        }
        catch (IOException ex){
            return "Unable to read repository Names!";
        }                
    }    
    
    @CliCommand(value = "list tag names of", help = "List tag names of given repositories.")
    public String listTagNames(
    @CliOption(key = {"", "repository-names"}, mandatory = true, 
            help = "name of repository for which you want to see tags") String[] repositoryNames) throws IOException{
        String returnString = "";
        
        for (String repositoryName: repositoryNames){
        
            returnString = returnString + "Tags of repository " + repositoryName + ":\n";
            String[] tags = registry.getTagNames(repositoryName);
            
            for (String tagName: tags){
                returnString = returnString + "  * " + tagName + "\n";
            }        
        }
        return returnString;        
    }
    
    @CliCommand(value = "list blobs of", help = "List blobs of given images.")
    public String listBlobs(
            @CliOption(key = {"", "image-names"}, mandatory = true,
                    help = "Names of images you want to get the blobs listed of.\n"
                            +"Note that an image name needs to have the format \"repoName:tagName\".\n"
                            +"Image names need to be comma-separated and without any spaces!")
                    String[] imageNames) throws IOException{
        String returnString = "";
        for (String imageName: imageNames){
            returnString = returnString + "Blobs of image " + imageName + "\n";
            Image image = registry.getImageByName(imageName);
            returnString = returnString + stringFormatter.imageToString(image) + "\n";
        }
        return returnString;
    }
    
    @CliCommand(value = "print cache info", help = "Print some information on the local RegistryCache.")
    public String printCacheInfo(){
        String returnString = "Info on local registry cache.\n"
                + "(i.e. information on how many items have already been loaded via HTTP interface from remote registry)\n";
        
        returnString = returnString + getRegistryCacheInfo();
        
        return returnString;
    }

    private String getRegistryCacheInfo() {
        String returnString = "";
        Map<String, Image> imageMap = registry.getMapOfImages();
        Map<String, Blob> blobMap = registry.getMapOfBlobs();
        returnString = returnString + "  * number of images in cache: " + imageMap.size() + "\n";
        returnString = returnString + "  * number of blobs  in cache: " + blobMap.size() + "\n";
        return returnString;
    }
    
    @CliCommand(value = "load complete image information",
            help = "Reads information on all images (what blobs are required for an image) from registry at specified URL." )
    public String loadCompleteInformation() throws IOException{
        registry.loadAllImagesToRegistryCache();
        String returnString = "Done.\n";
        returnString = returnString + getRegistryCacheInfo();
        return returnString;
    }
    
    private boolean simpleCommandExecuted = false;
    
    @CliAvailabilityIndicator({"hw simple"})
    public boolean isSimpleAvailable() {
        //always available
        return true;
    }
    
    @CliCommand(value = "hw simple", help = "Print a simple hello world message")
    public String simple(
            @CliOption(key = { "message" }, mandatory = true, help = "The hello world message") final String message,
            @CliOption(key = { "location" }, mandatory = false, help = "Where you are saying hello", specifiedDefaultValue="At work") final String location) {		
        simpleCommandExecuted = true;
        return "Message = [" + message + "] Location = [" + location + "]";
    }
}
