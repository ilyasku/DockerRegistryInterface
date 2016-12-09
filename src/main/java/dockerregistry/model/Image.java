package dockerregistry.model;

import dockerregistry.exceptions.StringIsNotInRepoPlusTagNameFormatException;

public class Image {
    
    private final String repoName;
    private final String tagName;
    
    private Blob[] layerBlobs;
    private Blob manifestBlob;
    private Blob configBlob;

    public Image(String repoPlusTagName){
        String[] splittedNames = repoPlusTagName.split(":");
        if (splittedNames.length != 2){
            throw new StringIsNotInRepoPlusTagNameFormatException(repoPlusTagName);
        }
        repoName = splittedNames[0];
        tagName = splittedNames[1];
    }

    public String getName() {
        return repoName + ":" + tagName;
    }
    
    public String getRepoName() {
        return repoName;
    }

    public String getTagName() {
        return tagName;
    }

    public Blob[] getLayerBlobs() {
        return layerBlobs;
    }

    public void setLayerBlobs(Blob[] layerBlobs) {
        this.layerBlobs = layerBlobs;
    }

    public Blob getManifestBlob() {
        return manifestBlob;
    }

    public void setManifestBlob(Blob manifestBlob) {
        this.manifestBlob = manifestBlob;
    }

    public Blob getConfigBlob() {
        return configBlob;
    }

    public void setConfigBlob(Blob configBlob) {
        this.configBlob = configBlob;
    }
           
}
