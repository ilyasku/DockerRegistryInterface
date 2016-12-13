package dockerregistry.model;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class HttpInterface {
    
    private final String urlPrefix;
    
    public HttpInterface(String url){
        this.urlPrefix = url;
    }   
        
    public String getRepositoryNames() throws MalformedURLException, IOException{
        String query = "v2/_catalog";        
        HttpURLConnection connection = (HttpURLConnection) new URL(urlPrefix + query).openConnection();
        connection.setRequestProperty("Accept", "application/vnd.docker.distribution.manifest.v2+json");   
        
        InputStream response = connection.getInputStream();        
        Scanner scanner = new Scanner(response);        
        String responseBody = scanner.useDelimiter("\\A").next();        
        
        return responseBody;                
    }
    
    public String getTagNames(String repositoryName) throws MalformedURLException, IOException {
        String query = "v2/"+ repositoryName + "/tags/list";        
        HttpURLConnection connection = (HttpURLConnection) new URL(urlPrefix + query).openConnection();
        connection.setRequestProperty("Accept", "application/vnd.docker.distribution.manifest.v2+json");   
        
        InputStream response = connection.getInputStream();        
        Scanner scanner = new Scanner(response);        
        String responseBody = scanner.useDelimiter("\\A").next();        
        
        return responseBody;
    }                
 
    public String[] getManifestHashAndManifestContent(String repositoryName, String tagName) throws MalformedURLException, IOException{                
        String query = String.format("/v2/%s/manifests/%s", repositoryName, tagName);        
        HttpURLConnection connection = (HttpURLConnection) new URL(urlPrefix + query).openConnection();
        connection.setRequestProperty("Accept", "application/vnd.docker.distribution.manifest.v2+json");        
        
        String[] manifestHashAndManifestContent = new String[2];        
        String manifestHash = connection.getHeaderField("Docker-Content-Digest");        
        manifestHashAndManifestContent[0] = manifestHash;
        
        InputStream response = connection.getInputStream();                
        Scanner scanner = new Scanner(response);        
        String manifestResponseBody = scanner.useDelimiter("\\A").next();        
        
        manifestHashAndManifestContent[1] = manifestResponseBody;
        
        return manifestHashAndManifestContent;                                      
    }

    
}
