package dockerregistry.model;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Registry {

    private HttpInterface httpInterface;        
    private Mapper mapper;
    private RegistryCacheInMemory registryCache;
    private BlobImageDependencyChecker dependencyChecker;
    
    public void setHttpInterface(HttpInterface httpInterface) {
        this.httpInterface = httpInterface;
    }

    public void setMapper(Mapper mapper) {
        this.mapper = mapper;
    }
    
    public void setRegistryCache(RegistryCacheInMemory registryCache) {
        this.registryCache = registryCache;
    }
    
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

    public Map<String, Image> getMapOfImages() {
        return registryCache.getImageCache();
    }
    
    public Map<String, Blob> getMapOfBlobs() {
        return registryCache.getBlobCache();
    }

    public Map<String, List<String>> getDependencyMapOfBlobsAndImages() {
        return dependencyChecker.getDependencyMap();
    }
    
    public Map<String, List<String>> getDependencyMapOfBlobsAndImagesIfImagesWouldBeDeleted(String[] namesOfImagesConsideredDeleted){
        return dependencyChecker.getDependencyMapWithImagesDeleted(namesOfImagesConsideredDeleted);
    }
    
    public void deleteImageFromRegistry(String repositoryName, String manifestHash) throws IOException{
        httpInterface.deleteImage(repositoryName, manifestHash);        
    }
    
    public void deleteBlobFromRegistry(String repositoryName, String blobHash) throws IOException {
        httpInterface.deleteBlob(repositoryName, blobHash);
    }
    
    public Image getImageByName(String imageName) throws IOException {
        if (!registryCache.imageCacheContains(imageName)){        
            buildImageViaHttpRequestAndAddToRegistryCache(imageName);
        }                
        return getImageFromRegistryCache(imageName);
    }

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
