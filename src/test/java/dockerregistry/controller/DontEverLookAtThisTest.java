package dockerregistry.controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dockerregistry.model.Tag;
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

public class DontEverLookAtThisTest {
    
    public DontEverLookAtThisTest() {
    }

    @Test
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
 
    
    @Test
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
    
    
    @Test
    public void testGetArrayOfRepositories() throws IOException{
        System.out.println("--------------------------------------------------");
        System.out.println("RequesterTest: method testGetArrayOfRepositories");
        
        HttpInterface http = new HttpInterface("http://localhost:5000/");
        
        String[] arrayOfRepositories = http.getRepositoryNames();
        
        for (int i = 0; i < arrayOfRepositories.length; i++) {
            System.out.println(arrayOfRepositories[i]);
        }
        
        assertEquals("ubuntu", arrayOfRepositories[1]);
        assertEquals("nimmis-ubuntu", arrayOfRepositories[0]);        
    }
    
    @Test
    public void testGetTagNames() throws IOException {
        System.out.println("--------------------------------------------------");
        System.out.println("RequesterTest: method testGetTagNames");
        
        HttpInterface http = new HttpInterface("http://localhost:5000/");
        
        String[] tagNames = http.getTagNames("ubuntu");
        
        for (int i = 0; i < tagNames.length; i++) {
            System.out.println(tagNames[i]);
        }
        
        assertEquals("latest", tagNames[0]);
    }
    
    @Test
    public void testGetHashOfTag() throws IOException {
        System.out.println("--------------------------------------------------");
        System.out.println("RequesterTest: method testGetHashOfTag");
        
        HttpInterface http = new HttpInterface("http://localhost:5000/");
        
        String hash = http.getHashOfManifest("ubuntu", "latest");
        System.out.println(hash);
    }
    
    @Test
    public void testCreateTagObject() throws IOException {
    }
    
    @Test
    public void someListProperties() {
        System.out.println("--------------------------------------------------");
        System.out.println("RequesterTest: method someListProperties");
        List<Integer> l = new ArrayList<>();
        
        l.add(0);
        l.add(12);
        
        System.out.println(l.indexOf(0));
        System.out.println(l.indexOf(12));
        
        System.out.println(l.indexOf(7));
        
    }
}

