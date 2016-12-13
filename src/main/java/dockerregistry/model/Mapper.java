package dockerregistry.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Mapper {
    
    public String[] mapRepositoryNames(String httpResponseBody) throws IOException{
        ObjectMapper mapper = new ObjectMapper(); 
        JsonNode repositoriesOuterJsonNode = mapper.readTree(httpResponseBody);        
        JsonNode repositoriesJsonNode = repositoriesOuterJsonNode.get("repositories");
        
        int numberOfRepositories = repositoriesJsonNode.size();        
        String[] arrayOfRepositories = new String[numberOfRepositories];        
        for (int i = 0; i < numberOfRepositories; i++) {
            arrayOfRepositories[i] = repositoriesJsonNode.get(i).textValue();
        }
        
        return arrayOfRepositories;
    }
    
    public String[] mapTagNames(String httpResponseBody) throws IOException{
        ObjectMapper mapper = new ObjectMapper();        
        JsonNode tagsOuterJsonNode = mapper.readTree(httpResponseBody);
        
        JsonNode tagsJsonNode = tagsOuterJsonNode.get("tags");
        
        int numberOfTags = tagsJsonNode.size();        
        String[] arrayOfTags = new String[numberOfTags];        
        for (int i = 0; i < numberOfTags; i++) {
            arrayOfTags[i] = tagsJsonNode.get(i).textValue();
        }
        
        return arrayOfTags;
    }
    
    public Manifest mapManifestHashAndManifestContentToManifestObject(
            String[] manifestHashAndManifestContent) throws IOException{        
         
        Manifest manifestObject = new Manifest();
        
        String manifestHash = manifestHashAndManifestContent[0];
        String manifestAsJsonString = manifestHashAndManifestContent[1];
        
        ObjectMapper mapper = new ObjectMapper();        
        JsonNode manifestAsJsonNode = mapper.readTree(manifestAsJsonString);
        
        Blob manifestBlob = new Blob(manifestHash, 0);
        Blob configBlob = mapBlobNode(manifestAsJsonNode.get("config"));
        List<Blob> layerBlobs = mapListOfBlobNodes(manifestAsJsonNode.get("layers"));
        
        manifestObject.setManifestBlob(manifestBlob);
        manifestObject.setConfigBlob(configBlob);
        manifestObject.setLayerBlobs(layerBlobs);        
        
        return manifestObject;        
    }
    
    private Blob mapBlobNode(JsonNode blobNode) {
        String hash = blobNode.get("digest").asText();
        int size = blobNode.get("size").asInt();        
        return new Blob(hash, size);
    }
    
    private List<Blob> mapListOfBlobNodes(JsonNode listOfBlobNodes){
        int length = listOfBlobNodes.size();
        List<Blob> blobs = new ArrayList<>();
        for (int i=0; i < length; i++) {
            blobs.add(mapBlobNode(listOfBlobNodes.get(i)));
        }        
        return blobs;
    }    
}
