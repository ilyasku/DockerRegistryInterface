package dockerregistry.model;

import dockerregistry.model.RegistryInMemoryStorage;
import com.fasterxml.jackson.databind.JsonNode;
import dockerregistry.model.Manifest;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RegistryTest {
    
    public RegistryTest() {
    }

    @Test
    public void testRegistryInit() {                      
        RegistryInMemoryStorage registry = new RegistryInMemoryStorage();
    }
    
    @Test
    public void testGetNamesOfRepositories() {                        
                
    }
    
    @Test
    public void testGetRepositoryByName() {                      
        
    }
    
    @Test
    public void testGetNumberOfRepositories() {                      
        
    }
 
    @Test
    public void testBuildTagAndBlobsFromManifest() {                
        
        JsonNode mockConfigNode = 
                buildMockJsonNodeWithDigestAndSize(
                        "sha256:4ca3a192ff2a5b7e225e81dc006b6379c10776ed3619757a65608cb72de0a7f6",
                        3621);        
        
        JsonNode mockLayersNode = mock(JsonNode.class);
        when(mockLayersNode.size()).thenReturn(2);
        
        JsonNode mockFirstLayerNode = 
                buildMockJsonNodeWithDigestAndSize(
                        "sha256:af49a5ceb2a56a8232402f5868cdb13dfdae5d66a62955a73e647e16e9f30a63", 
                        50096701);        
        JsonNode mockSecondLayerNode = 
                buildMockJsonNodeWithDigestAndSize(
                        "sha256:8f9757b472e7962a4304d4af61630e2cde66129218135b4093a43b9db8942c34",
                        824);
        
        when(mockLayersNode.get(1)).thenReturn(mockFirstLayerNode);
        when(mockLayersNode.get(2)).thenReturn(mockSecondLayerNode);
        
        Manifest mockManifest = mock(Manifest.class);
        when(mockManifest.getManifestHash()).thenReturn("sha256:3b64c309deae7ab0f7dbdd42b6b326261ccd6261da5d88396439353162703fb5");
        when(mockManifest.getConfigNode()).thenReturn(mockConfigNode);
        when(mockManifest.getLayersNode()).thenReturn(mockLayersNode);
        
        RegistryInMemoryStorage registry = new RegistryInMemoryStorage();
                        
        registry.buildTagAndBlobsFromManifest(mockManifest, "test:tag");
        
        assertEquals(4, registry.getListOfBlobs().size());
        assertTrue(registry.blobByThatHashExistsInRegistry("sha256:3b64c309deae7ab0f7dbdd42b6b326261ccd6261da5d88396439353162703fb5"));
        assertTrue(registry.blobByThatHashExistsInRegistry("sha256:af49a5ceb2a56a8232402f5868cdb13dfdae5d66a62955a73e647e16e9f30a63"));
        assertFalse(registry.blobByThatHashExistsInRegistry("This is not the hash you are looking for."));
        
        // Use buildTagAndBlobsFromManifest with slightly different manifest.
        // Identical blobs should not be appended to the list of blobs a second time.
        
        mockFirstLayerNode = buildMockJsonNodeWithDigestAndSize("sha256:47b5e16c0811b08c1cf3198fa5ac0b920946ac538a0a0030627d19763e2fa212", 682);
        when(mockLayersNode.get(1)).thenReturn(mockFirstLayerNode);
        when(mockManifest.getLayersNode()).thenReturn(mockLayersNode);
        
        registry.buildTagAndBlobsFromManifest(mockManifest, "test:tag2");        
        
        assertEquals(5, registry.getListOfBlobs().size());
        assertTrue(registry.blobByThatHashExistsInRegistry("sha256:47b5e16c0811b08c1cf3198fa5ac0b920946ac538a0a0030627d19763e2fa212"));        
    }

    private JsonNode buildMockJsonNodeWithDigestAndSize(String digest, int size){
        JsonNode mockJsonNode = mock(JsonNode.class);
        JsonNode mockDigestNode = mock(JsonNode.class);
        JsonNode mockSizeNode = mock(JsonNode.class);
        
        when(mockJsonNode.get("digest")).thenReturn(mockDigestNode);
        when(mockJsonNode.get("size")).thenReturn(mockSizeNode);
        
        when(mockDigestNode.asText()).thenReturn(digest);
        when(mockSizeNode.asInt()).thenReturn(size);
        
        return mockJsonNode;
    }    
}
