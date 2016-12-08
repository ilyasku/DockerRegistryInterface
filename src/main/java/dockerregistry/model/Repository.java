package dockerregistry.model;

import dockerregistry.exceptions.NoTagByThatNameException;

public class Repository {
    private Tag[] tags;
    private String name;
    
    public Tag getTagByName(String tagName) {        
        Tag[] tags = getTags();
        int numberOfTags = tags.length;
        
        for (int i = 0; i < numberOfTags; i++){
            if (tags[i].getName().equals(tagName)) {
                return tags[i];
            }
        }        
        throw new NoTagByThatNameException("No tag by name '"+ tagName + "' found.");
    }
    
    public Repository(String name){
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

    public Tag[] getTags() {
        return tags;
    }

    public void setTags(Tag[] tags) {
        this.tags = tags;
    }

    public void setName(String name) {
        this.name = name;
    }
}
