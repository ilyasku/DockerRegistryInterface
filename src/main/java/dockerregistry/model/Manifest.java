package dockerregistry.model;

import com.fasterxml.jackson.databind.JsonNode;

public class Manifest {
    private final String manifestHash;
    private final JsonNode manifestAsJsonNode;
    
    public Manifest(String manifestHash, JsonNode manifestAsJsonNode) {
        this.manifestAsJsonNode = manifestAsJsonNode;
        this.manifestHash = manifestHash;        
    }

    public JsonNode getConfigNode() {
        return manifestAsJsonNode.get("config");
    }
    
    public JsonNode getLayersNode() {
        return manifestAsJsonNode.get("layers");
    }
    
    public String getManifestHash() {
        return manifestHash;
    }

    public JsonNode getManifestAsJsonNode() {
        return manifestAsJsonNode;
    }
}
