package springshell.commands;

import dockerregistry.model.Blob;
import dockerregistry.model.BlobImageDependencyChecker;
import dockerregistry.model.HttpInterface;
import dockerregistry.model.Image;
import dockerregistry.model.LocalConfigHandler;
import dockerregistry.model.Mapper;
import dockerregistry.model.Registry;
import dockerregistry.model.RegistryCacheInMemory;
import springshell.ObjectToCliStringFormatter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import jline.console.ConsoleReader;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

@Component
public class DockerRegistryCommands implements CommandMarker{
    
    private boolean completeImageInfoLoaded = false;
    private boolean dependencyMapBuild = false;
    
    
    private LocalConfigHandler localConfigHandler = new LocalConfigHandler();
    private String url;
    private HttpInterface httpInterface;
    private final Mapper mapper  = new Mapper();;
    private RegistryCacheInMemory registryCache;
    private BlobImageDependencyChecker dependencyChecker;
    private final Registry registry = new Registry();
    
    private final ObjectToCliStringFormatter stringFormatter = new ObjectToCliStringFormatter();
    
    private String encodedUsernamePassword = null;
    
    public DockerRegistryCommands() throws IOException {
        
        url = constructInitialUrl();        
        initializeRegistry();
    }    
    
    @CliAvailabilityIndicator({"load dependencies between blobs-images"})
    public boolean isLoadDependenciesAvailable(){
        return completeImageInfoLoaded;
    }
    
    @CliAvailabilityIndicator({"list blobs unreferenced after deletion of"})
    public boolean isListBlobsUnreferencedAvailable(){
        return completeImageInfoLoaded && dependencyMapBuild;
    }
    
    
    
    @CliCommand(value = "set url", help = "Set URL of the repository you want to connect to.")
    public String setUrl(
            @CliOption(key = {"","url"}, mandatory = true, 
                    help = "URL of repository you want to connect to") String url,
            @CliOption(key = {"default", "set-as-default"}, mandatory = false,
                    help = "set this URL as default (this URL will be used on next start of the docker-registry-shell)",
                    specifiedDefaultValue = "true",
                    unspecifiedDefaultValue = "false") String makeDefault){
        this.url = url;
        httpInterface = new HttpInterface(this.url);
        if (encodedUsernamePassword != null){
            httpInterface.setEncodedUsernamePassword(encodedUsernamePassword);
        }
        registry.setHttpInterface(httpInterface);
        if (makeDefault.equals("true")){
            try {
                localConfigHandler.writeUrl(url);
            } catch (FileNotFoundException | UnsupportedEncodingException ex) {
                Logger.getLogger(DockerRegistryCommands.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return "set HttpInterface to url \"" + url + "\"";
    }
    
    @CliCommand(value = "set credentials", help = "Set username and password to autheticate at the registry.")
    public String setCredentials() throws IOException {
        ConsoleReader reader = new ConsoleReader();
        String userName = reader.readLine("Username: ");
        String password = reader.readLine("Password: ",Character.MIN_VALUE);
        String usernamePassword = userName + ":" + password;
        String base64encodedCredentials = Base64.getEncoder().encodeToString(usernamePassword.getBytes());
        encodedUsernamePassword = base64encodedCredentials;
        httpInterface.setEncodedUsernamePassword(encodedUsernamePassword);        
        return "Credentials set.";         
    }        
    
    @CliCommand(value = "list repositories", help = "List names of all repositories")
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
    
    @CliCommand(value = "list tags of", help = "List tag names of given repositories.")
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

    @CliCommand(value = "delete image", help = "Deletes an image from the registry via Docker Registry HTTP API v2")
    public String deleteImage(@CliOption(key = {"", "image-names"}, mandatory = true,
                    help = "Names of images you want to delete.\n"
                            +"Note that an image name needs to have the format \"repoName:tagName\".\n")
                    String[] imageNames) throws IOException{
        for (String imageName: imageNames){
            Image image = registry.getImageByName(imageName);
            // HTTP API requires image hash for deletion (not its tag)
            String imageHash = image.getManifest().getManifestBlob().getHash();
            registry.deleteImageFromRegistry(image.getRepoName(), imageHash);
        }
        return "Done.";
    }
    
    @CliCommand(value = "delete blob", help = "Deletes a blob. BE CAREFUL: multiple images might depend on a single blob.")
    public String deleteBlob(@CliOption(key = {"", "blob-hashes"}, 
            help = "Hashes of blobs you want to delete.")
            String[] blobHashes) {
        return "Not implemented yet";
    }
    
    private String getRegistryCacheInfo() {
        String returnString = "";
        Map<String, Image> imageMap = registry.getMapOfImages();
        Map<String, Blob> blobMap = registry.getMapOfBlobs();
        returnString = returnString + "  * number of images in cache: " + imageMap.size() + "\n";
        returnString = returnString + "  * number of blobs  in cache: " + blobMap.size() + "\n";
        return returnString;
    }
    
    @CliCommand(value = {"load complete image information"},
            help = "Reads information on all images (what blobs are required for an image) from registry at specified URL." )
    public String loadCompleteInformation() throws IOException{
        registry.loadAllImagesToRegistryCache();
        String returnString = "Done.\n";
        returnString = returnString + getRegistryCacheInfo();
        completeImageInfoLoaded = true;
        return returnString;
    }
    
    
    @CliCommand(value = "load dependencies between blobs-images", help ="Loads information for every blob which images depend on it.\n"
            + "This information will not be complete, unless you called \"load complete image information\" before!!")
    public String loadDependenciesWhichImagesDependOnWhichBlobs(){        
        registry.buildDependencyMapOfBlobsAndImages();
        dependencyMapBuild = true;
        return "Done. Note: The dependencies will not be complete if you did not call \"load complete image information\" before!!";
    }            
    
    @CliCommand(value = "list blobs unreferenced after deletion of", 
            help = "Prints a list of blobs that would be unreferenced if you would delete the images specified.")
    public String getUnreferencedBlobsIfImagesWouldBeDeleted(
            @CliOption(key = {"", "names-of-images-considered-deleted"}, mandatory = true, help = "list of image names considered deleted for this request")String[] imageNames){
        String returnString = "Blobs that would be unreferenced:\n";
        
        Map<String, List<String>> dependencyMapWithImagesDeleted = registry.getDependencyMapOfBlobsAndImagesIfImagesWouldBeDeleted(imageNames);
        List<String> listOfBlobsThatAreNotReferenced  = getListOfBlobsThatAreNotReferenced(dependencyMapWithImagesDeleted);
        
        for (String blobHash: listOfBlobsThatAreNotReferenced){
            returnString = returnString + "  * " + blobHash + "\n";
        }
        
        return returnString;
    }

    @CliCommand(value = "list images depending on", help = "List all images that depend on given blob.")
    public String getImagesDependingOnBlob(
    @CliOption(key = {"", "blob-name"}, mandatory = true, help = "name of the blob you want to see the dependencies of") String blobHash) {
        String returnString = "";
        
        Map<String, List<String>> dependencyMap = registry.getDependencyMapOfBlobsAndImages();
        
        if (dependencyMap.containsKey(blobHash)){
            returnString = returnString + "Images depending on Blob with hash \"" + blobHash + "\":";
            for (String nameOfImageDependingOnBlob: dependencyMap.get(blobHash)){
                returnString = returnString + "  * "+ nameOfImageDependingOnBlob + "\n";
            }
        }
        else {
            returnString = returnString + "Sorry! No Blob by that Hash in the dependency map.\n";
            returnString = returnString + "You need to run commands\n";
            returnString = returnString + "  > load complete image information\n";
            returnString = returnString + "  > load dependencies between blobs-images\n";
            returnString = returnString + "to build the complete map!";
        }                
        return returnString;
    }
    
    private List<String> getListOfBlobsThatAreNotReferenced(Map<String, List<String>> dependencyMapWithImagesDeleted) {
        List<String> listOfBlobsThatAreNotReferenced = new ArrayList<>();
        
        for (Entry<String, List<String>> entry : dependencyMapWithImagesDeleted.entrySet()){
            String blobHash = entry.getKey();
            List<String> listOfImagesReferencingThatBlob = entry.getValue();
            if (listOfImagesReferencingThatBlob.isEmpty()){
                listOfBlobsThatAreNotReferenced.add(blobHash);
            }
        }
        
        return listOfBlobsThatAreNotReferenced;
    }            

    private String constructInitialUrl() throws IOException {             
        
        String pathToUrlFile = LocalConfigHandler.getHomePath() + "/"
                + LocalConfigHandler.getConfigDirectoryPath() + "/"
                + LocalConfigHandler.getUrlFileName();
        
        
        
        System.out.println("reading default URL (from " + pathToUrlFile + ") ...");                        
        String defaultUrl;
        
        try {            
            defaultUrl =  localConfigHandler.getUrl();
            System.out.println("set URL to: " + defaultUrl);
        }        
        catch (FileNotFoundException ex){
            ConsoleReader reader = new ConsoleReader();
            reader.println("no default URL found (file does not exist)");            
            defaultUrl = reader.readLine("Set default URL now: ");
            localConfigHandler.writeUrl(defaultUrl);
        }                
        return defaultUrl;
    }
    
    private void initializeRegistry() {
        httpInterface  = new HttpInterface(url);
        registryCache = new RegistryCacheInMemory();
        dependencyChecker = new BlobImageDependencyChecker();
        
        registry.setHttpInterface(httpInterface);
        registry.setMapper(mapper);
        registry.setRegistryCache(registryCache);
        registry.setDependencyChecker(dependencyChecker);
    }
}
