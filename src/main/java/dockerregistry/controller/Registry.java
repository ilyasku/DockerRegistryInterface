package dockerregistry.controller;

import com.fasterxml.jackson.databind.JsonNode;
import dockerregistry.exceptions.NoRepositoryByThatNameException;
import dockerregistry.model.Blob;
import dockerregistry.model.Manifest;
import dockerregistry.model.Repository;
import dockerregistry.model.Tag;
import java.util.ArrayList;
import java.util.List;

public class Registry {
    
    
    private Repository[] repositories;
    
    private List<Blob> blobs = new ArrayList<>();
    
    public Registry(){
        
    }

    String[] getNamesOfRepositories() {        
        int numberOfRepositories = getNumberOfRepositories();
        String[] arrayOfNames = new String[numberOfRepositories];
        
        for (int i = 0; i < numberOfRepositories; i++){
            arrayOfNames[i] = repositories[i].getName();
        }
        return arrayOfNames;        
    }        

    public Repository[] getRepositories() {
        return repositories;
    }

    public void setRepositories(Repository[] repositories) {
        this.repositories = repositories;
    }

    public int getNumberOfRepositories() {
        return repositories.length;
    }

    public Repository getRepositoryByName(String nameOfRepository) {
        int numberOfRepositories = getNumberOfRepositories();
        
        for (int i = 0; i < numberOfRepositories; i++) {
            if (repositories[i].getName().equals(nameOfRepository)) {
                return repositories[i];
            }
        }
        
        throw new NoRepositoryByThatNameException("No repository by name '" + nameOfRepository + "' found.");
    }
    
    public Tag buildTagAndBlobsFromManifest(Manifest manifest, String tagName) {
        Tag tag = new Tag(tagName);
        
        Blob manifestBlob = addNewBlobToListOrFetchIdenticalBlobFromList(new Blob(manifest.getManifestHash(), 0));
        tag.setManifest(manifestBlob);
                        
        JsonNode configNode = manifest.getConfigNode();
        String configHash = configNode.get("digest").asText();        
        int configSize = configNode.get("size").asInt();
        Blob configBlob = addNewBlobToListOrFetchIdenticalBlobFromList(new Blob(configHash, configSize));        
        tag.setConfig(configBlob);        
                        
        JsonNode layersNode = manifest.getLayersNode();
        int numberOfLayers = layersNode.size();
        
        Blob[] layerBlobs = new Blob[numberOfLayers];
        for (int i = 0; i < numberOfLayers; i++){
            JsonNode singleLayerNode = layersNode.get(i);
            
            String layerHash = singleLayerNode.get("digest").asText();
            int layerSize = singleLayerNode.get("size").asInt();
            
            Blob layerBlob = addNewBlobToListOrFetchIdenticalBlobFromList(new Blob(layerHash, layerSize));
            
            layerBlobs[i] = layerBlob;
        }
        
        tag.setLayer(layerBlobs);
        
        return tag;
    }

    private Blob addNewBlobToListOrFetchIdenticalBlobFromList(Blob blob) {
        
        int indexOfBlobInList = blobs.indexOf(blob);
        
        if (identicalBlobAlreadyInList(indexOfBlobInList)) {
            return blobs.get(indexOfBlobInList);
        }
        else {
            blobs.add(blob);
            return blob;
        }        
    }
    
    private boolean identicalBlobAlreadyInList(int indexOfBlobInList) {
        return (indexOfBlobInList != -1);
    }
    
}
