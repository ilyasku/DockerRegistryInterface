package dockerregistry.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

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
    
    public Manifest mapManifestResponseToObject(String[] manifestHashAndResponseBody) throws IOException{        
         
        String manifestHash = manifestHashAndResponseBody[0];
        String manifestResponseBody = manifestHashAndResponseBody[1];
        
        ObjectMapper mapper = new ObjectMapper();        
        JsonNode manifestAsJsonNode = mapper.readTree(manifestResponseBody);        
                
        return new Manifest(manifestHash, manifestAsJsonNode);
        
    }
    
}
