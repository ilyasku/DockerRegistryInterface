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
    private static String pathToConfigDirectory = ".dockerRegistryInterface";
    private static String urlFileName = "url.txt";    

    public static String getHomePath() {
        return homePath;
    }

    public static String getPathToConfigDirectory() {
        return pathToConfigDirectory;
    }

    public static String getUrlFileName() {
        return urlFileName;
    }

    
    private String url = null;
    
    public LocalConfigHandler() throws IOException{
        checkForConfigDirectory();
    }
    
    public String getUrl() throws FileNotFoundException, IOException{
        loadUrl();
        return url;
    }
    
    private void loadUrl() throws FileNotFoundException, IOException{        
        InputStream fileInputStream = new FileInputStream(getHomePath() + "/" 
                + getPathToConfigDirectory() + "/" + getUrlFileName());
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, Charset.forName("UTF-8"));
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        
        url = bufferedReader.readLine();        
    }
    
    public void writeUrl(String url) throws FileNotFoundException, UnsupportedEncodingException {        
        PrintWriter writer = new PrintWriter(getHomePath() + "/" 
                + getPathToConfigDirectory() + "/" + getUrlFileName(), "UTF-8");
        this.url = url;
        writer.print(url);
        writer.close();        
    }

    private void checkForConfigDirectory() throws IOException {
        Path path = Paths.get(getHomePath() + "/" + getPathToConfigDirectory());
        if (Files.notExists(path)){            
            Files.createDirectory(path);
        }
    }
    
}
