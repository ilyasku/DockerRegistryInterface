package dockerregistry.model;


import dockerregistry.model.HttpInterface;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dockerregistry.model.Image;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

public class DontEverLookAtThisTest {
    
    public DontEverLookAtThisTest() {
    }

    public void testConnectingViaHTTP() throws MalformedURLException, IOException{
        System.out.println("--------------------------------------------------");
        System.out.println("RequesterTest: method testConnectingViaHTTP");
        String url = "http://localhost:5000/";
        
        String query = "/v2/ubuntu/manifests/latest";
        
        URLConnection connection = new URL(url + query).openConnection();
        
        InputStream response = connection.getInputStream();
        
        
        Scanner scanner = new Scanner(response);
        
        System.out.println(scanner.useDelimiter("\\A").next());
        
        /*
        try (Scanner scanner = new Scanner(response)) {
            String responseBody = scanner.useDelimiter("\\A").next();
            System.out.println(responseBody);
        } */         
    }
     
    public void testReadHeader() throws MalformedURLException, IOException{
        System.out.println("--------------------------------------------------");
        System.out.println("RequesterTest: method testReadHeader");
        String url = "http://localhost:5000/";
        
        String query = "/v2/ubuntu/manifests/latest";
        
        //URLConnection connection = new URL(url + query).openConnection();
        HttpURLConnection connection = (HttpURLConnection) new URL(url + query).openConnection();
        connection.setRequestProperty("Accept", "application/vnd.docker.distribution.manifest.v2+json");
        
        InputStream response = connection.getInputStream();
        
        try (Scanner scanner = new Scanner(response)) {
            String responseBody = scanner.useDelimiter("\\A").next();
            System.out.println(responseBody);
        }
        
        for (Entry<String, List<String>> header : connection.getHeaderFields().entrySet()) {
            System.out.println(header.getKey() + "=" + header.getValue());
        }        
    }
    
    @Ignore
    @Test
    public void testGetArrayOfRepositories() throws IOException{
        System.out.println("--------------------------------------------------");
        System.out.println("RequesterTest: method testGetArrayOfRepositories");
        
        HttpInterface http = new HttpInterface("http://localhost:5000/");
        Mapper mapper = new Mapper();
        
        String repositoryNamesAsJsonString = http.getRepositoryNames();
        
        System.out.println(repositoryNamesAsJsonString);
        
        String[] arrayOfRepositories = mapper.mapRepositoryNames(repositoryNamesAsJsonString);
        
        for (int i = 0; i < arrayOfRepositories.length; i++) {
            System.out.println(arrayOfRepositories[i]);
        }
        
        assertEquals("ubuntu", arrayOfRepositories[1]);
        assertEquals("nimmis-ubuntu", arrayOfRepositories[0]);        
    }
    
    public void testGetTagNames() throws IOException {
        System.out.println("--------------------------------------------------");
        System.out.println("RequesterTest: method testGetTagNames");
        
        HttpInterface http = new HttpInterface("http://localhost:5000/");
        Mapper mapper = new Mapper();
        
         String tagNamesAsJsonString = http.getTagNames("ubuntu");
         String[] tagNames = mapper.mapTagNames(tagNamesAsJsonString);
        
        for (int i = 0; i < tagNames.length; i++) {
            System.out.println(tagNames[i]);
        }
        
        assertEquals("latest", tagNames[0]);
    }
    
    
    public void testGetHashOfManifest() throws IOException {
        System.out.println("--------------------------------------------------");
        System.out.println("RequesterTest: method testGetHashOfTag");
        
        HttpInterface http = new HttpInterface("http://localhost:5000/");
        
        String hash = http.getManifestHashAndManifestContent("ubuntu", "latest")[0];
        System.out.println(hash);
    }

    @Ignore
    @Test
    public void testLoadAllImagesToCache() throws IOException {
        System.out.println("--------------------------------------------------");
        System.out.println("DontEverLookAtThisTest: method testLoadAllImagesToCache");
        HttpInterface http = new HttpInterface("http://localhost:5000/");
        Mapper mapper = new Mapper();
        RegistryCacheInMemory registryCache = new RegistryCacheInMemory();
        
        Registry registry = new Registry();
        registry.setHttpInterface(http);
        registry.setMapper(mapper);
        registry.setRegistryCache(registryCache);                
        
        registry.loadAllImagesToRegistryCache();
        Map<String, Image> imageCache = registry.getMapOfImages();
        assertEquals(1, imageCache.size());
        assertTrue(imageCache.containsKey("ubuntu:latest"));
    }
    
    @Test
    public void testWhatDoesUserHomeReturn(){
        System.out.println("--------------------------------------------------");
        System.out.println("DontEverLookAtThisTest: method testWhatDoesUserHomeReturn");
        System.err.println(System.getProperty("user.home"));
    }
        
}

