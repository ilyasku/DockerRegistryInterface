package dockerregistry.model;

import dockerregistry.exceptions.ImageByThatNameAlreadyInImageCacheException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegistryCacheInMemory {
       
    private final Map<String, Blob> blobCache = new HashMap<>();    
    private final Map<String,Image> imageCache = new HashMap<>();                
           
    public RegistryCacheInMemory(){}
    
    public void addImage(Image image){
        String imageName = image.getName();
        if (imageCacheContains(imageName)){
            throw new ImageByThatNameAlreadyInImageCacheException(imageName);
        }
        imageCache.put(image.getName(), image);        
        updateBlobCacheAndGivenManifest(image.getManifest());        
    }
    
    private void updateBlobCacheAndGivenManifest(Manifest manifest){
        manifest.setManifestBlob(checkForBlobInBlobCacheAndPutIfNotPresent(manifest.getManifestBlob()));
        manifest.setConfigBlob(checkForBlobInBlobCacheAndPutIfNotPresent(manifest.getConfigBlob()));
        List<Blob> layersOfManifest = manifest.getLayerBlobs();
        for (int i = 0; i < layersOfManifest.size(); i++){
            layersOfManifest.set(i, checkForBlobInBlobCacheAndPutIfNotPresent(layersOfManifest.get(i)));
        }                
    }

    private Blob checkForBlobInBlobCacheAndPutIfNotPresent(Blob configBlob) {
        if (getBlobCache().containsKey(configBlob.getHash())){
            return getBlobCache().get(configBlob.getHash());
        }
        getBlobCache().put(configBlob.getHash(), configBlob);
        return configBlob;
    }

    public Map<String, Blob> getBlobCache() {
        return blobCache;
    }

    public Map<String,Image> getImageCache() {
        return imageCache;
    }
    
    public boolean imageCacheContains(String imageName){
        return imageCache.containsKey(imageName);
    }
}
