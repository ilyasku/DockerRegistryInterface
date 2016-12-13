package dockerregistry.model;

import java.io.IOException;
import org.junit.Test;
import static org.junit.Assert.*;

public class MapperTest {
    
    private Mapper mapper = new Mapper();
    
    public MapperTest() {
    }    

    @Test
    public void testMapRepositoryNames() throws IOException {
        String repositoryNamesAsJsonString = "{\"repositories\":[\"nimmis-ubuntu\", \"ubuntu\", \"hello-world\"]}";
        String[] repositoryNames = mapper.mapRepositoryNames(repositoryNamesAsJsonString);
        
        assertTrue(repositoryNames[0].equals("nimmis-ubuntu"));
        assertTrue(repositoryNames[1].equals("ubuntu"));
        assertTrue(repositoryNames[2].equals("hello-world"));
    }

    @Test
    public void testMapTagNames() throws IOException {
        String tagNamesAsJsonString = "{\"tags\":[\"latest\", \"0.1.0\", \"2\"]}";
        String[] tagNames = mapper.mapTagNames(tagNamesAsJsonString);
        
        assertTrue(tagNames[0].equals("latest"));
        assertTrue(tagNames[1].equals("0.1.0"));
        assertTrue(tagNames[2].equals("2"));
    }

    @Test
    public void testMapManifestHashAndManifestContentToManifestObject() throws IOException{
        String manifestHash = "sha256:3b64c309deae7ab0f7dbdd42b6b326261ccd6261da5d88396439353162703fb5";
        String manifestContent = "{"
                + "\"schemaVersion\": 2,"
                + "\"mediaType\": \"application/vnd.docker.distribution.manifest.v2+json\","
                + "\"config\": {"
                + "\"mediaType\": \"application/vnd.docker.container.image.v1+json\","
                + "\"size\": 3621,"
                + "\"digest\": \"sha256:4ca3a192ff2a5b7e225e81dc006b6379c10776ed3619757a65608cb72de0a7f6\""
                + "},"
                + "\"layers\": ["
                + "{"
                + "\"mediaType\": \"application/vnd.docker.image.rootfs.diff.tar.gzip\","
                + "\"size\": 50096701,"
                + "\"digest\": \"sha256:af49a5ceb2a56a8232402f5868cdb13dfdae5d66a62955a73e647e16e9f30a63\""
                + "},"
                + "{"
                + "\"mediaType\": \"application/vnd.docker.image.rootfs.diff.tar.gzip\","
                + "\"size\": 824,"
                + "\"digest\": \"sha256:8f9757b472e7962a4304d4af61630e2cde66129218135b4093a43b9db8942c34\""
                + "},"
                + "{"
                + "\"mediaType\": \"application/vnd.docker.image.rootfs.diff.tar.gzip\","
                + "\"size\": 518,"
                + "\"digest\": \"sha256:e931b117db38a05b9d0bbd28ca99a0abe5236a0026d88b3db804f520e59977ec\""
                + "},"
                + "{"
                + "\"mediaType\": \"application/vnd.docker.image.rootfs.diff.tar.gzip\","
                + "\"size\": 682,"
                + "\"digest\": \"sha256:47b5e16c0811b08c1cf3198fa5ac0b920946ac538a0a0030627d19763e2fa212\""
                + "},"
                + "{"
                + "\"mediaType\": \"application/vnd.docker.image.rootfs.diff.tar.gzip\","
                + "\"size\": 162,"
                + "\"digest\": \"sha256:9332eaf1a55b72fb779d2f249b65855c623c8ce7be83c822b7d80115ef5a3af3\""
                + "}"
                + "]"
                + "}";        
        
        String[] manifestHashAndManifestContent = new String[]{manifestHash, manifestContent};
        
        Manifest manifest = mapper.mapManifestHashAndManifestContentToManifestObject(manifestHashAndManifestContent);
        
        assertTrue(manifest.getManifestBlob().getHash().equals("sha256:3b64c309deae7ab0f7dbdd42b6b326261ccd6261da5d88396439353162703fb5"));
        assertTrue(manifest.getConfigBlob().getHash().equals("sha256:4ca3a192ff2a5b7e225e81dc006b6379c10776ed3619757a65608cb72de0a7f6"));
        assertEquals(5, manifest.getLayerBlobs().size());
        Blob layerBlob_3 = manifest.getLayerBlobs().get(3);
        assertTrue(layerBlob_3.getHash().equals("sha256:47b5e16c0811b08c1cf3198fa5ac0b920946ac538a0a0030627d19763e2fa212"));
        assertEquals(682, layerBlob_3.getSize());
    }
    
}
