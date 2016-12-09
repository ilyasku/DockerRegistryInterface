package dockerregistry.model;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegistryInMemoryStorage {
       
    private Map<String, Blob> blobCache = new HashMap<>();
    
    private List<Image> images = new ArrayList<>();    
    private List<Blob> blobs = new ArrayList<>();    
    
    Map<String, Blob> blobMap = new HashMap<>();
    
    public List<Blob> getListOfBlobs(){       
        return blobs;
    }    
    
    public Image buildTagAndBlobsFromManifest(Manifest manifest, String tagName) {
        
        Image tag = new Image(tagName);
        
        Blob manifestBlob = addNewBlobToListOrFetchIdenticalBlobFromList(new Blob(manifest.getManifestHash(), 0));
        tag.setManifestBlob(manifestBlob);
                        
        JsonNode configNode = manifest.getConfigNode();
        String configHash = configNode.get("digest").asText();        
        int configSize = configNode.get("size").asInt();
        Blob configBlob = addNewBlobToListOrFetchIdenticalBlobFromList(new Blob(configHash, configSize));        
        tag.setConfigBlob(configBlob);        
                        
        JsonNode layersNode = manifest.getLayersNode();
        int numberOfLayers = layersNode.size();
        
        Blob[] layerBlobs = new Blob[numberOfLayers];
        for (int i = 0; i < numberOfLayers; i++){
            JsonNode singleLayerNode = layersNode.get(i+1);
            
            String layerHash = singleLayerNode.get("digest").asText();
            int layerSize = singleLayerNode.get("size").asInt();
            
            Blob layerBlob = addNewBlobToListOrFetchIdenticalBlobFromList(new Blob(layerHash, layerSize));
            
            layerBlobs[i] = layerBlob;
        }
        
        tag.setLayerBlobs(layerBlobs);
        
        return tag;
    }

    private Blob addNewBlobToListOrFetchIdenticalBlobFromList(Blob blob) {                                
        if (blobByThatHashExistsInRegistry(blob.getHash())) {
            int indexOfBlobInList = blobs.indexOf(blob);
            return blobs.get(indexOfBlobInList);
        }
        else {
            blobs.add(blob);
            return blob;
        }        
    }
    
    public boolean blobByThatHashExistsInRegistry(String hash) {
        Blob temporaryBlobWithRequestedHash = new Blob(hash, 0);
        int indexOfBlobInList = blobs.indexOf(temporaryBlobWithRequestedHash);
        return (indexOfBlobInList != -1);
    }
    
    public Blob[] getBlobsThatWouldBeUnreferencedAfterDeletionOfTheGivenRepoTags(String[] repoTags) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
