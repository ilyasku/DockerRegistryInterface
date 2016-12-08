package dockerregistry.controller;

import com.fasterxml.jackson.databind.JsonNode;
import dockerregistry.exceptions.NoRepositoryByThatNameException;
import dockerregistry.model.Manifest;
import dockerregistry.model.Repository;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RegistryTest {
    
    public RegistryTest() {
    }

    @Test
    public void testRegistryInit() {                      
        Registry registry = new Registry();
    }
    
    @Test
    public void testGetNamesOfRepositories() {                        
        Registry registry = new Registry();
        
        String[] mockNames = {"repo one", "repo two", "repo three"
                , "repo four", "repo five", "repo six"};
        
        Repository[] repositories = new Repository[6];
        for (int i = 0; i < 6; i++) {
            repositories[i] = mock(Repository.class);
            when(repositories[i].getName()).thenReturn(mockNames[i]);
        }
        
        registry.setRepositories(repositories);
        
        String[] names = registry.getNamesOfRepositories();        
    }
    
    @Test
    public void testGetRepositoryByName() {                      
        Registry registry = new Registry();
        
        String[] mockNames = {"repo one", "repo two", "repo three"
                , "repo four", "repo five", "repo six"};
        
        Repository[] repositories = new Repository[6];
        for (int i = 0; i < 6; i++) {
            repositories[i] = mock(Repository.class);
            when(repositories[i].getName()).thenReturn(mockNames[i]);
        }
        
        registry.setRepositories(repositories);
                
        assertEquals(repositories[1], registry.getRepositoryByName("repo two"));
        assertEquals(repositories[0], registry.getRepositoryByName("repo one"));
        assertEquals(repositories[4], registry.getRepositoryByName("repo five"));
        
        try {
            Repository repoThatDoesNotExist = registry.getRepositoryByName("repo eins zwo");
            fail();
        }
        catch (NoRepositoryByThatNameException ex){
            assertTrue(ex.getMessage().equals("No repository by name 'repo eins zwo' found."));
        }
    }
    
    @Test
    public void testGetNumberOfRepositories() {                      
        Registry registry = new Registry();        
        
        
        Repository[] repositories = new Repository[6];
        for (int i = 0; i < 6; i++) {
            repositories[i] = mock(Repository.class);
        }        
        registry.setRepositories(repositories);
                        
        assertEquals(6, registry.getNumberOfRepositories());
    }
 
    @Test
    public void testBuildTagAndBlobsFromManifest() {
        
        //JsonNode mockConfigDigest = mock(JsonNode.class);      
        
        JsonNode mockConfigNode = mock(JsonNode.class);
        when(mockConfigNode.get("digest").asText()).thenReturn("sha256:4ca3a192ff2a5b7e225e81dc006b6379c10776ed3619757a65608cb72de0a7f6");
        when(mockConfigNode.get("size").asInt()).thenReturn(3621);
        
        JsonNode mockLayersNode = mock(JsonNode.class);
        when(mockLayersNode.size()).thenReturn(2);
        
        JsonNode mockFirstLayerNode = mock(JsonNode.class);
        when(mockFirstLayerNode.get("digest").asText()).thenReturn("sha256:af49a5ceb2a56a8232402f5868cdb13dfdae5d66a62955a73e647e16e9f30a63");
        when(mockFirstLayerNode.get("size").asInt()).thenReturn(50096701);

        JsonNode mockSecondLayerNode = mock(JsonNode.class);
        when(mockSecondLayerNode.get("digest").asText()).thenReturn("sha256:8f9757b472e7962a4304d4af61630e2cde66129218135b4093a43b9db8942c34");
        when(mockSecondLayerNode.get("size").asInt()).thenReturn(824);
        
        when(mockLayersNode.get(1)).thenReturn(mockFirstLayerNode);
        when(mockLayersNode.get(2)).thenReturn(mockSecondLayerNode);
        
        Manifest mockManifest = mock(Manifest.class);
        when(mockManifest.getManifestHash()).thenReturn("sha256:3b64c309deae7ab0f7dbdd42b6b326261ccd6261da5d88396439353162703fb5");
        
        Registry registry = new Registry();
        
        registry.buildTagAndBlobsFromManifest(mockManifest, "testTag");
        
    }
    
}
