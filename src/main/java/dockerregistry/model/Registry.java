package dockerregistry.model;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * This is the facade that provides access to the model of DockerRegistryInterface.
 * Implement this in unser interfaces to make use of DockerRegistryInterface's 
 * functionalities. 
 */
public class Registry {

    private HttpInterface httpInterface;        
    private Mapper mapper;
    private RegistryCacheInMemory registryCache;
    private BlobImageDependencyChecker dependencyChecker;
    
    /**
     * Needs to be set before a registry instance ca be used (or at least before
     * any communication with a registry's HTTP API can take place).
     * @param httpInterface
     */
    public void setHttpInterface(HttpInterface httpInterface) {
        this.httpInterface = httpInterface;
    }

    /**
     * Needs to be set before a registry instance ca be used (or at least before
     * any JSON response from a registy's HTTP API can be mapped to Java objects).
     * @param mapper
     */
    public void setMapper(Mapper mapper) {
        this.mapper = mapper;
    }
    
    /**
     * Needs to be set before a registry instance ca be used (or at least before
     * you want to use any information on blobs of images).
     * @param registryCache
     */
    public void setRegistryCache(RegistryCacheInMemory registryCache) {
        this.registryCache = registryCache;
    }
    
    /**
     * Set this before you want to compute a dependency graph which image
     * depends on which blobs.
     * @param dependencyChecker 
     */
    public void setDependencyChecker(BlobImageDependencyChecker dependencyChecker) {
        this.dependencyChecker = dependencyChecker;
    }
    
    public String[] getRepositoryNames() throws IOException {
        String repositoryNamesAsJsonString = httpInterface.getRepositoryNames();
        return mapper.mapRepositoryNames(repositoryNamesAsJsonString);
    }

    public String[] getTagNames(String repositoryName) throws IOException {
        String tagNamesAsJsonString = httpInterface.getTagNames(repositoryName);
        return mapper.mapTagNames(tagNamesAsJsonString);
    }

    /**
     * @return Map assigning Image objects to image names.
     */
    public Map<String, Image> getMapOfImages() {
        return registryCache.getImageCache();
    }
    
    /**
     * @return Map assigning Blob objects to blob hashes.
     */
    public Map<String, Blob> getMapOfBlobs() {
        return registryCache.getBlobCache();
    }

    /** 
     * You need to run loadAllImagesToRegistryCache and buildDependencyMapOfBlobsAndImages
     * for this function to return the complete dependency map.
     * @return Map assigning lists of image names to hashes of blobs the images
     * depend on.
     */
    public Map<String, List<String>> getDependencyMapOfBlobsAndImages() {
        return dependencyChecker.getDependencyMap();
    }
    
    /**
     * Get a map assigning lists of image names to hashes of blobs the images
     * depend on, with some images considered deleted. This allows you to 
     * identify blobs that could safely be deleted, similar to what the 
     * garbage collector might do to identify blobs for deletion.
     * You need to run loadAllImagesToRegistryCache and buildDependencyMapOfBlobsAndImages
     * for this function to return the complete dependency map.
     */
    public Map<String, List<String>> getDependencyMapOfBlobsAndImagesIfImagesWouldBeDeleted(String[] namesOfImagesConsideredDeleted){
        return dependencyChecker.getDependencyMapWithImagesDeleted(namesOfImagesConsideredDeleted);
    }
    
    /**
     * @param repositoryName
     * @param manifestHash
     * @return HTTP ReponseCode of DELETE request.
     * @throws java.io.IOException
     */
    public int deleteImageFromRegistry(String repositoryName, String manifestHash) throws IOException{
        return httpInterface.deleteImage(repositoryName, manifestHash);        
    }
    
    /**
     * @return HTTP ResponseCode of DELETE request.
     */
    public int deleteBlobFromRegistry(String repositoryName, String blobHash) throws IOException {
        return httpInterface.deleteBlob(repositoryName, blobHash);
    }
    
    /**
     * Get an `Image` object for the image identified by given name.
     * An `Image` object contains information on what blobs this image depends.
     */
    public Image getImageByName(String imageName) throws IOException {
        if (!registryCache.imageCacheContains(imageName)){        
            buildImageViaHttpRequestAndAddToRegistryCache(imageName);
        }                
        return getImageFromRegistryCache(imageName);
    }

    /**
     * Call this function before you want to build a graph which images depend
     * on which blobs.
     * Automatically reads all repository names and their tag names and builds
     * `Image` instances for each image.
     */
    public void loadAllImagesToRegistryCache() throws IOException {
        for (String repository : getRepositoryNames()){
            for (String tag : getTagNames(repository)){
                String imageName = repository + ":" + tag;
                if (!registryCache.imageCacheContains(imageName)) {
                    buildImageViaHttpRequestAndAddToRegistryCache(imageName);
                }
            }
        }        
    }    
    
    /**
     * Builds a graph of which images depend on which blobs. 
     */
    public void buildDependencyMapOfBlobsAndImages(){
        dependencyChecker.buildDependencyMap(getMapOfImages());
    }
    
    private Image getImageFromRegistryCache(String imageName) {
        return registryCache.getImageCache().get(imageName);        
    }

    private void buildImageViaHttpRequestAndAddToRegistryCache(String imageName) throws IOException {
        Image newImage = new Image(imageName);
        String[] manifestHashAndContent = httpInterface.getManifestHashAndManifestContent(
                newImage.getRepoName(),
                newImage.getTagName());
        
        Manifest manifestOfImage = mapper.mapManifestHashAndManifestContentToManifestObject(manifestHashAndContent);
        newImage.setManifest(manifestOfImage);
        
        registryCache.addImage(newImage);        
    }        
    
}
