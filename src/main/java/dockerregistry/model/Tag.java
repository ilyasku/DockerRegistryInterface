package dockerregistry.model;

public class Tag {
    private final String name;
    
    private Blob[] layer;
    private Blob manifest;
    private Blob config;

    public Tag(String name){
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

    public Blob[] getLayer() {
        return layer;
    }

    public void setLayer(Blob[] layer) {
        this.layer = layer;
    }

    public Blob getManifest() {
        return manifest;
    }

    public void setManifest(Blob manifest) {
        this.manifest = manifest;
    }

    public Blob getConfig() {
        return config;
    }

    public void setConfig(Blob config) {
        this.config = config;
    }

       
}
