package dockerregistry.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

public class RegistryTest {
    
    private Registry registry;    
    private HttpInterface mockHttpInterface;
    private Mapper mockMapper;
    private RegistryCacheInMemory mockRegistryCache;
    
    public RegistryTest() {
    }
    
    @Before
    public void setUp(){
        registry = new Registry();
        
        mockHttpInterface = mock(HttpInterface.class);
        mockMapper = mock(Mapper.class);
        mockRegistryCache = mock(RegistryCacheInMemory.class);
        
        registry.setHttpInterface(mockHttpInterface);
        registry.setMapper(mockMapper);
        registry.setRegistryCache(mockRegistryCache);
    }
    
    @Test 
    public void testGetRepositoryNames() throws IOException{        
        when(mockHttpInterface.getRepositoryNames()).thenReturn("mock-my-life");
        when(mockMapper.mapRepositoryNames("mock-my-life")).thenReturn(new String[]{"ubuntu","hello-world","whalesay"});        
                
        String[] repoNames = registry.getRepositoryNames();
        
        assertTrue(repoNames[0].equals("ubuntu"));
        assertTrue(repoNames[1].equals("hello-world"));
        assertTrue(repoNames[2].equals("whalesay"));
    }
    
    @Test
    public void testGetTagNames() throws IOException{
        when(mockHttpInterface.getTagNames("repo")).thenReturn("mock-my-life");
        when(mockMapper.mapTagNames("mock-my-life")).thenReturn(new String[]{"latest","0.1.0","0.1.1"});
        
        String[] tagNames = registry.getTagNames("repo");
        
        assertTrue(tagNames[0].equals("latest"));
        assertTrue(tagNames[1].equals("0.1.0"));
        assertTrue(tagNames[2].equals("0.1.1"));
    }

    @Test
    public void testGetImageByName() throws IOException {
        Map<String, Image> imageCache = new HashMap<>();
        Image image11 = new Image("repo1:tag1");
        Image image21 = new Image("repo2:tag1");
        imageCache.put("repo1:tag1", image11);
        imageCache.put("repo2:tag1", image21);
                        
        when(mockRegistryCache.imageCacheContains("repo1:tag1")).thenReturn(true);
        when(mockRegistryCache.imageCacheContains("repo2:tag1")).thenReturn(true);
        when(mockRegistryCache.getImageCache()).thenReturn(imageCache);

        
        String[] manifestHashAndContentOfImage12 = new String[]{"mock-my-life", "mock-my-life"};
        Manifest manifestObjectOfImage12 = new Manifest();
        
        when(mockRegistryCache.imageCacheContains("repo1:tag2")).thenReturn(false);
        when(mockHttpInterface.getManifestHashAndManifestContent("repo1", "tag2")).thenReturn(manifestHashAndContentOfImage12);
        when(mockMapper.mapManifestHashAndManifestContentToManifestObject(manifestHashAndContentOfImage12)).thenReturn(manifestObjectOfImage12);
        
        
        
        Image returnImage11 = registry.getImageByName("repo1:tag1");
        assertSame(image11,returnImage11);
        Image returnImage21 = registry.getImageByName("repo2:tag1");
        assertSame(image21, returnImage21);
        
        // LIMIT OF MY MOCKING ABILITIES REACHED ...
        // Mockito.doCallRealMethod().when(mockRegistryCache).addImage(Matchers.any(Image.class));
        // Image returnImage12 = registry.getImageByName("repo1:tag2");
        // assertTrue("repo1:tag2".equals(returnImage12.getName()));        
    }          
    
    
}
