package dockerregistry.model;

import com.google.common.collect.HashBiMap;
import dockerregistry.exceptions.ImageByThatNameAlreadyInImageCacheException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class RegistryCacheInMemory {
       
    private final Map<String,  Blob> blobCache = new HashMap<>();    
    private final Map<String, Image> imageCache = new HashMap<>();                
           
    public RegistryCacheInMemory(){}
    
    public void addImage(Image image){
        String imageName = image.getName();
        if (imageCacheContains(imageName)){
            throw new ImageByThatNameAlreadyInImageCacheException(imageName);
        }
        imageCache.put(image.getName(), image);        
        updateBlobCacheAndGivenManifest(image.getManifest());        
    }
    
    public Map<String, List<String>> getMapBlobsReferencdedByImages(){
        
        Map<String, List<String>> mapBlobsReferencdedByImages = new HashMap<>();
        
        for (Entry<String, Image> entry: imageCache.entrySet()){
            updateMapBlobsReferencedByImages(mapBlobsReferencdedByImages, entry.getKey(), entry.getValue().getManifest());
        }
        
        return mapBlobsReferencdedByImages;
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

    private void updateMapBlobsReferencedByImages(Map<String, List<String>> mapBlobsReferencdedByImages, String imageName, Manifest manifestOfImage) {
        updateMapBlobsReferencedByImages_BySingleBlob(mapBlobsReferencdedByImages, imageName, manifestOfImage.getManifestBlob());
        updateMapBlobsReferencedByImages_BySingleBlob(mapBlobsReferencdedByImages, imageName, manifestOfImage.getConfigBlob());
        for (Blob layerBlob: manifestOfImage.getLayerBlobs()){
            updateMapBlobsReferencedByImages_BySingleBlob(mapBlobsReferencdedByImages, imageName, layerBlob);
        }
    }

    private void updateMapBlobsReferencedByImages_BySingleBlob(Map<String, List<String>> mapBlobsReferencdedByImages, String imageName, Blob blob) {
        String blobHash = blob.getHash();
        if (!mapBlobsReferencdedByImages.containsKey(blobHash)){
            List<String> newListOfImages = new ArrayList<>();
            newListOfImages.add(imageName);
            mapBlobsReferencdedByImages.put(blobHash, newListOfImages);
        }
        else{
            List<String> listOfImagesReferencingThisBlob = mapBlobsReferencdedByImages.get(blobHash);
            if (!listOfImagesReferencingThisBlob.contains(imageName)){
                listOfImagesReferencingThisBlob.add(imageName);
            }
        }
    }
}
