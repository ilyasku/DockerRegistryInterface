package dockerregistry.model;

import dockerregistry.exceptions.StringIsNotInRepoPlusTagNameFormatException;

public class Image {
    
    private final String repoName;
    private final String tagName;
    
    private Manifest manifest;

    public Image(String repoPlusTagName){
        String[] splittedNames = repoPlusTagName.split(":");
        if (splittedNames.length != 2){
            throw new StringIsNotInRepoPlusTagNameFormatException(repoPlusTagName);
        }
        repoName = splittedNames[0];
        tagName = splittedNames[1];
    }

    public void setManifest(Manifest manifest) {
        this.manifest = manifest;
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

    public Manifest getManifest() {
        return manifest;
    }           
}
