package dockerregistry.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dockerregistry.model.Manifest;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import dockerregistry.model.Tag;

public class HttpInterface {
    
    private final String urlPrefix;
    
    public HttpInterface(String url){
        this.urlPrefix = url;
    }
        
    public String[] getRepositoryNames() throws MalformedURLException, IOException{
        String query = "v2/_catalog";        
        HttpURLConnection connection = (HttpURLConnection) new URL(urlPrefix + query).openConnection();
        connection.setRequestProperty("Accept", "application/vnd.docker.distribution.manifest.v2+json");   
        
        InputStream response = connection.getInputStream();        
        Scanner scanner = new Scanner(response);        
        String responseBody = scanner.useDelimiter("\\A").next();        
        ObjectMapper mapper = new ObjectMapper();        
        JsonNode repositoriesOuterJsonNode = mapper.readTree(responseBody);        
        JsonNode repositoriesJsonNode = repositoriesOuterJsonNode.get("repositories");
        
        int numberOfRepositories = repositoriesJsonNode.size();        
        String[] arrayOfRepositories = new String[numberOfRepositories];        
        for (int i = 0; i < numberOfRepositories; i++) {
            arrayOfRepositories[i] = repositoriesJsonNode.get(i).textValue();
        }
        
        return arrayOfRepositories;        
    }
    
    public String[] getTagNames(String repositoryName) throws MalformedURLException, IOException {
        String query = "v2/"+ repositoryName + "/tags/list";        
        HttpURLConnection connection = (HttpURLConnection) new URL(urlPrefix + query).openConnection();
        connection.setRequestProperty("Accept", "application/vnd.docker.distribution.manifest.v2+json");   
        
        InputStream response = connection.getInputStream();        
        Scanner scanner = new Scanner(response);        
        String responseBody = scanner.useDelimiter("\\A").next();        
        ObjectMapper mapper = new ObjectMapper();        
        JsonNode tagsOuterJsonNode = mapper.readTree(responseBody);
        
        JsonNode tagsJsonNode = tagsOuterJsonNode.get("tags");
        
        int numberOfTags = tagsJsonNode.size();        
        String[] arrayOfTags = new String[numberOfTags];        
        for (int i = 0; i < numberOfTags; i++) {
            arrayOfTags[i] = tagsJsonNode.get(i).textValue();
        }
        
        return arrayOfTags;
        
    }
    
    public String getHashOfManifest(String repositoryName, String tagName) throws MalformedURLException, IOException {        
        String query = String.format("/v2/%s/manifests/%s", repositoryName, tagName);        
        HttpURLConnection connection = (HttpURLConnection) new URL(urlPrefix + query).openConnection();
        connection.setRequestProperty("Accept", "application/vnd.docker.distribution.manifest.v2+json");
        
        return connection.getHeaderField("Docker-Content-Digest");                        
    }        
 
    public Manifest createManifestObject(String repositoryName, String tagName) throws MalformedURLException, IOException{                
        String query = String.format("/v2/%s/manifests/%s", repositoryName, tagName);        
        HttpURLConnection connection = (HttpURLConnection) new URL(urlPrefix + query).openConnection();
        connection.setRequestProperty("Accept", "application/vnd.docker.distribution.manifest.v2+json");        
        
        String manifestHash = connection.getHeaderField("Docker-Content-Digest");        
        
        JsonNode manifestAsJsonNode = getManifestAsJsonNode(connection);        
                
        return new Manifest(manifestHash, manifestAsJsonNode);
    }

    private JsonNode getManifestAsJsonNode(HttpURLConnection connection) throws IOException {
        InputStream response = connection.getInputStream();                
        Scanner scanner = new Scanner(response);        
        String manifestAsPlainText = scanner.useDelimiter("\\A").next();        
        
        ObjectMapper mapper  = new ObjectMapper();
        
        return mapper.readTree(manifestAsPlainText);
    }
    
}
