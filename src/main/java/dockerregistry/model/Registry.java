package dockerregistry.model;

import dockerregistry.exceptions.NoImageByThatNameInImageCacheException;
import java.io.IOException;
import java.util.Map;
import sun.awt.image.ImageCache;

public class Registry {

    private HttpInterface httpInterface;        
    private Mapper mapper;
    private RegistryCacheInMemory registryCache;
    
    public void setHttpInterface(HttpInterface httpInterface) {
        this.httpInterface = httpInterface;
    }

    public void setMapper(Mapper mapper) {
        this.mapper = mapper;
    }
    
    String[] getRepositoryNames() throws IOException {
        String repositoryNamesAsJsonString = httpInterface.getRepositoryNames();
        return mapper.mapRepositoryNames(repositoryNamesAsJsonString);
    }

    String[] getTagNames(String repositoryName) throws IOException {
        String tagNamesAsJsonString = httpInterface.getTagNames(repositoryName);
        return mapper.mapTagNames(tagNamesAsJsonString);
    }

    public Map<String, Image> getMapOfImages() {
        return registryCache.getImageCache();
    }
    
    public Map<String, Blob> getMapOfBlobs() {
        return registryCache.getBlobCache();
    }
    
    public void setRegistryCache(RegistryCacheInMemory registryCache) {
        this.registryCache = registryCache;
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
