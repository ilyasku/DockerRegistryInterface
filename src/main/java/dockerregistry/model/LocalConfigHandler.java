package dockerregistry.model;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LocalConfigHandler {
    
    private static String homePath = System.getProperty("user.home");
    private static String configDirectoryPath = ".DockerRegistryInterface";
    private static String urlFileName = "url.txt";    
    private static String shellHistoryFileName = "shell.log";
    private static String credentialsFileName = ".credentials.txt";

    public static String getHomePath() {
        return homePath;
    }

    public static String getConfigDirectoryPath() {
        return configDirectoryPath;
    }

    public static String getUrlFileName() {
        return urlFileName;
    }

    public static String getShellHistoryFileName() {
        return shellHistoryFileName;
    }

    public static String getCredentialsFileName() {
        return credentialsFileName;
    }
    
    private String url = null;
    
    public LocalConfigHandler() throws IOException{
        checkForConfigDirectory();
    }
    
    public String getCredentials() throws IOException, FileNotFoundException{
        return readCredentials();
    }
    
    public String getUrl() throws FileNotFoundException, IOException{
        loadUrl();
        return url;
    }
    
    private void loadUrl() throws FileNotFoundException, IOException{        
        InputStream fileInputStream = new FileInputStream(homePath + "/" 
                + configDirectoryPath + "/" + urlFileName);
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, Charset.forName("UTF-8"));
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        
        url = bufferedReader.readLine();
    }
    
    public void writeUrl(String url) throws FileNotFoundException, UnsupportedEncodingException {        
        PrintWriter writer = new PrintWriter(homePath + "/" 
                + configDirectoryPath + "/" + urlFileName, "UTF-8");
        this.url = url;
        writer.print(url);
        writer.close();        
    }
    
    public void writeCredentials(String encodedCredentials) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter(homePath + "/" 
                + configDirectoryPath + "/" + credentialsFileName, "UTF-8");
        writer.print(encodedCredentials);
        writer.close();        
    }
            
    private void checkForConfigDirectory() throws IOException {
        Path path = Paths.get(getHomePath() + "/" + getConfigDirectoryPath());
        if (Files.notExists(path)){            
            Files.createDirectory(path);
        }
    }
    


    private String readCredentials() throws FileNotFoundException, IOException {
        InputStream fileInputStream = new FileInputStream(homePath + "/" 
                + configDirectoryPath + "/" + credentialsFileName);
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, Charset.forName("UTF-8"));
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        
        return bufferedReader.readLine();
    }    
}
