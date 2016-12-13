/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dockerregistry.model;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ilyas
 */
public class RegistryCacheInMemoryTest {
    
    public RegistryCacheInMemoryTest() {
    }

    @Test
    public void testAddImage() {
        RegistryCacheInMemory registryCache = new RegistryCacheInMemory();                
        
        Manifest manifestRepo1Tag1 = new Manifest();
        manifestRepo1Tag1.setManifestBlob(new Blob("m11",0));
        manifestRepo1Tag1.setConfigBlob(new Blob("c11",10));
        List<Blob> layerBlobsRepo1Tag1 = new ArrayList<>();
        layerBlobsRepo1Tag1.add(new Blob("a",1200));
        layerBlobsRepo1Tag1.add(new Blob("b",1203));
        layerBlobsRepo1Tag1.add(new Blob("c",13532));
        manifestRepo1Tag1.setLayerBlobs(layerBlobsRepo1Tag1);                        
        
        Image image11 = new Image("repo1:tag1");
        image11.setManifest(manifestRepo1Tag1);
        
        registryCache.addImage(image11);
        
        assertEquals(5, registryCache.getBlobCache().size());
        
        Manifest manifestRepo1Tag2 = new Manifest();
        manifestRepo1Tag2.setManifestBlob(new Blob("m12",0));
        manifestRepo1Tag2.setConfigBlob(new Blob("c12",1234));
        List<Blob> layerBlobsRepo1Tag2 = new ArrayList<>();
        layerBlobsRepo1Tag2.add(new Blob("c",13532));
        layerBlobsRepo1Tag2.add(new Blob("d",7821));
        layerBlobsRepo1Tag2.add(new Blob("e",4221));
        manifestRepo1Tag2.setLayerBlobs(layerBlobsRepo1Tag2);                        
        
        Image image12 = new Image("repo1:tag2");
        image12.setManifest(manifestRepo1Tag2);
        
        registryCache.addImage(image12);
        
        // image11 and image12 have one layer in common, thus the blobCashe should
        // now hold 5 + 5 - 1 = 9 items
        assertEquals(9, registryCache.getBlobCache().size());
        
        int indexOfBlob_c_ofImage11 = image11.getManifest().getLayerBlobs().indexOf(new Blob("c", 0));
        Blob blob_c_ofImage11 = image11.getManifest().getLayerBlobs().get(indexOfBlob_c_ofImage11);
        assertSame(registryCache.getBlobCache().get("c"), blob_c_ofImage11);
        
        int indexOfBlob_c_ofImage12 = image12.getManifest().getLayerBlobs().indexOf(new Blob("c", 0));
        Blob blob_c_ofImage12 = image12.getManifest().getLayerBlobs().get(indexOfBlob_c_ofImage12);
        assertSame(registryCache.getBlobCache().get("c"), blob_c_ofImage12);        
    }    
}
