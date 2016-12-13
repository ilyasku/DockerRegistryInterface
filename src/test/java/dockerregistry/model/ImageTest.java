package dockerregistry.model;

import org.junit.Test;
import static org.junit.Assert.*;

public class ImageTest {
    
    @Test
    public void testGetName() {
        Image image = new Image("ubuntu:latest");
        
        assertTrue(image.getRepoName().equals("ubuntu"));
        assertTrue(image.getTagName().equals("latest"));
        assertTrue(image.getName().equals("ubuntu:latest"));
    }    
}
