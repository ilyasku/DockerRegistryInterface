package dockerregistry.model;

import dockerregistry.exceptions.NoTagByThatNameException;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

public class RepositoryTest {
    
    public RepositoryTest() {
    }    
    
    @Test
    public void testGetTagByName() {
        Repository repository = new Repository("test repo");
        String[] mockNames = {"tag one", "tag two", "tag three"
                , "tag four", "tag five", "tag six"};
        
        Tag[] tags = new Tag[6];
        for (int i = 0; i < 6; i++) {
            tags[i] = mock(Tag.class);
            when(tags[i].getName()).thenReturn(mockNames[i]);
        }
                
        repository.setTags(tags);
        
        assertEquals(tags[1], repository.getTagByName("tag two"));
        assertEquals(tags[5], repository.getTagByName("tag six"));
        assertEquals(tags[3], repository.getTagByName("tag four"));
        
        try {
            Tag tagThatDoesNotExist = repository.getTagByName("tag eins zwo");
            fail();
        }
        catch (NoTagByThatNameException ex) {
            assertTrue(ex.getMessage().equals("No tag by name 'tag eins zwo' found."));
        }
    }    
}
