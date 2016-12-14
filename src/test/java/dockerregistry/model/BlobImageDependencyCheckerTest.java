package dockerregistry.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

public class BlobImageDependencyCheckerTest {                
        
    Map<String, Blob> blobMap;
    Map<String, Image> imageMap;
    BlobImageDependencyChecker dependencyChecker;
    
    @Before
    public void setUp(){
        Blob layerBlob1 = new Blob("layerBlob1", 10);
        Blob layerBlob2 = new Blob("layerBlob2", 20);
        Blob layerBlob3 = new Blob("layerBlob3", 30);
        Blob layerBlob4 = new Blob("layerBlob4", 40);
        
        Blob manifestBlob1 = new Blob("manifestBlob1", 0);
        Blob manifestBlob2 = new Blob("manifestBlob2", 0);
        Blob manifestBlob3 = new Blob("manifestBlob3", 0);
        
        Blob configBlob1 = new Blob("configBlob1",1);
        Blob configBlob2 = new Blob("configBlob2",1);
        Blob configBlob3 = new Blob("configBlob3",1);
        
        Manifest manifest1 = new Manifest();
        manifest1.setManifestBlob(manifestBlob1);
        manifest1.setConfigBlob(configBlob1);
        manifest1.setLayerBlobs(new ArrayList<Blob>(Arrays.asList(layerBlob1, layerBlob2)));
        
        Manifest manifest2 = new Manifest();
        manifest2.setManifestBlob(manifestBlob2);
        manifest2.setConfigBlob(configBlob2);
        manifest2.setLayerBlobs(new ArrayList<Blob>(Arrays.asList(layerBlob2, layerBlob3)));
        
        Manifest manifest3 = new Manifest();
        manifest3.setManifestBlob(manifestBlob3);
        manifest3.setConfigBlob(configBlob3);
        manifest3.setLayerBlobs(new ArrayList<Blob>(Arrays.asList(layerBlob4)));
        
        Image image1 = new Image("first:image");
        image1.setManifest(manifest1);
        Image image2 = new Image("second:image");
        image2.setManifest(manifest2);
        Image image3 = new Image("third:image");
        image3.setManifest(manifest3);
        
        imageMap = new HashMap<>();
        imageMap.put(image1.getName(), image1);
        imageMap.put(image2.getName(), image2);
        imageMap.put(image3.getName(), image3);
        
        blobMap = new HashMap<>();
        blobMap.put(layerBlob1.getHash(), layerBlob1);
        blobMap.put(layerBlob2.getHash(), layerBlob2);
        blobMap.put(layerBlob3.getHash(), layerBlob3);
        blobMap.put(layerBlob4.getHash(), layerBlob4);
        blobMap.put(manifestBlob1.getHash(), manifestBlob1);
        blobMap.put(manifestBlob2.getHash(), manifestBlob2);
        blobMap.put(manifestBlob3.getHash(), manifestBlob3);
        blobMap.put(configBlob1.getHash(), configBlob1);
        blobMap.put(configBlob2.getHash(), configBlob2);
        blobMap.put(configBlob3.getHash(), configBlob3);        
    }
    
    @Test
    public void testBuildDependencyMap() {
        dependencyChecker = new BlobImageDependencyChecker();
        dependencyChecker.buildDependencyMap(imageMap);
        
        Map<String, List<String>> dependencyMap = dependencyChecker.getDependencyMap();
        
        // 10 keys (blob hashes) in total: 4 layer blobs, 3 manifest and 3 config blobs.
        assertEquals(10, dependencyMap.size());
        // all the manifest and config blobs should only be referenced by 1 image each
        assertEquals(1, dependencyMap.get("manifestBlob1").size());
        assertEquals(1, dependencyMap.get("manifestBlob2").size());
        assertEquals(1, dependencyMap.get("manifestBlob3").size());
        assertEquals(1, dependencyMap.get("configBlob1").size());
        assertEquals(1, dependencyMap.get("configBlob2").size());
        assertEquals(1, dependencyMap.get("configBlob3").size());
        // ... and they should be referenced by the image with matching number.
        assertTrue("first:image".equals(dependencyMap.get("manifestBlob1").get(0)));
        assertTrue("second:image".equals(dependencyMap.get("manifestBlob2").get(0)));
        assertTrue("third:image".equals(dependencyMap.get("configBlob3").get(0)));
        // layerBlob1 should only be referenced by first image, not by the other two
        assertTrue(dependencyMap.get("layerBlob1").contains("first:image"));
        assertFalse(dependencyMap.get("layerBlob1").contains("second:image"));
        assertFalse(dependencyMap.get("layerBlob1").contains("third:image"));
        // layerBlob2 should be referenced by first and second iamge
        assertTrue(dependencyMap.get("layerBlob2").contains("first:image"));
        assertTrue(dependencyMap.get("layerBlob2").contains("second:image"));
        assertFalse(dependencyMap.get("layerBlob2").contains("third:image"));        
    }
    
    @Test
    public void testGetDependencyMapWithImagesDeleted(){
        dependencyChecker = new BlobImageDependencyChecker();
        dependencyChecker.buildDependencyMap(imageMap);
        
        Map<String, List<String>> dependencyMapWithImagesDeleted = dependencyChecker.getDependencyMapWithImagesDeleted(new String[]{"first:image"});
        // With first image deleted, there should be 3 blobs that are not referenced by images anymore:
        // layerBlob1, manifestBlob1, configBlob1.
        List<String> listOfBlobsThatAreNotReferenced = getBlobsThatAreNotReferenced(dependencyMapWithImagesDeleted);                
        assertEquals(3, listOfBlobsThatAreNotReferenced.size());
        assertTrue(listOfBlobsThatAreNotReferenced.contains("layerBlob1"));
        assertTrue(listOfBlobsThatAreNotReferenced.contains("manifestBlob1"));
        assertTrue(listOfBlobsThatAreNotReferenced.contains("configBlob1"));
        
        dependencyMapWithImagesDeleted = dependencyChecker.getDependencyMapWithImagesDeleted(new String[]{"first:image", "second:image"});
        // With first and second image deleted, there should be 7 blobs that are not referenced by images anymore:
        // layerBlob1, layerBlob2, layerBlob3, manifestBlob1, manifestBlob2, configBlob1, configBlob2
        listOfBlobsThatAreNotReferenced = getBlobsThatAreNotReferenced(dependencyMapWithImagesDeleted);
        assertEquals(7, listOfBlobsThatAreNotReferenced.size());
        assertTrue(listOfBlobsThatAreNotReferenced.contains("layerBlob1"));
        assertTrue(listOfBlobsThatAreNotReferenced.contains("layerBlob2"));
        assertTrue(listOfBlobsThatAreNotReferenced.contains("layerBlob3"));
        assertTrue(listOfBlobsThatAreNotReferenced.contains("manifestBlob1"));
        assertTrue(listOfBlobsThatAreNotReferenced.contains("manifestBlob2"));
        assertTrue(listOfBlobsThatAreNotReferenced.contains("configBlob1"));
        assertTrue(listOfBlobsThatAreNotReferenced.contains("configBlob2"));
        
    }
    
    private List<String> getBlobsThatAreNotReferenced(Map<String, List<String>> dependencyMap){
        List<String> listOfBlobsThatAreNotReferenced = new ArrayList<>();
        
        for (Entry<String, List<String>> entry : dependencyMap.entrySet()){
            String blobHash = entry.getKey();
            List<String> listOfImagesReferencingThatBlob = entry.getValue();
            if (listOfImagesReferencingThatBlob.size() == 0){
                listOfBlobsThatAreNotReferenced.add(blobHash);
            }
        }
        
        return listOfBlobsThatAreNotReferenced;
    }
}
