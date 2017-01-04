package cli.parser;

import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;

public class ConnectionOptionParserTest {
    
    public ConnectionOptionParserTest() {
    }   

    @Test
    public void testParse() {
        
        // use all three options with abbreviations
        String[] mockConnectionOptions = new String[]{"-r", "http://localhost:5000",
            "-u","firlefranz"};
        
        Map<ConnectionOptionType, String> parsedOptions = ConnectionOptionParser.parse(mockConnectionOptions);
        
        
        assertTrue(parsedOptions.containsKey(ConnectionOptionType.REGISTRY_URL));
        assertTrue(parsedOptions.containsKey(ConnectionOptionType.USER_NAME));
        
        
        assertTrue(parsedOptions.get(ConnectionOptionType.REGISTRY_URL).equals("http://localhost:5000"));
        assertTrue(parsedOptions.get(ConnectionOptionType.USER_NAME).equals("firlefranz"));
        
        // use all three options, some abbreviated, some with long name
        mockConnectionOptions = new String[]{"-r", "http://localhost:5000",
            "--username","firlefranz"};
        
        parsedOptions = ConnectionOptionParser.parse(mockConnectionOptions);
        
        
        assertTrue(parsedOptions.containsKey(ConnectionOptionType.REGISTRY_URL));
        assertTrue(parsedOptions.containsKey(ConnectionOptionType.USER_NAME));
        
        
        assertTrue(parsedOptions.get(ConnectionOptionType.REGISTRY_URL).equals("http://localhost:5000"));
        assertTrue(parsedOptions.get(ConnectionOptionType.USER_NAME).equals("firlefranz"));
        
        // options can occur more than once, but only the last occurence should be used
        mockConnectionOptions = new String[]{"-r", "http://localhost:5000",            
            "--username","firlefranz",
            "--username", "this should overwrite firlefranz"};
        
        parsedOptions = ConnectionOptionParser.parse(mockConnectionOptions);       
        
        
        assertTrue(parsedOptions.containsKey(ConnectionOptionType.REGISTRY_URL));
        assertTrue(parsedOptions.containsKey(ConnectionOptionType.USER_NAME));
        
        
        assertTrue(parsedOptions.get(ConnectionOptionType.REGISTRY_URL).equals("http://localhost:5000"));
        assertFalse(parsedOptions.get(ConnectionOptionType.USER_NAME).equals("firlefranz"));
        assertTrue(parsedOptions.get(ConnectionOptionType.USER_NAME).equals("this should overwrite firlefranz"));
        
        // test single
        mockConnectionOptions = new String[]{"--registry", "http://localhost:5000"};
        parsedOptions = ConnectionOptionParser.parse(mockConnectionOptions);
        assertTrue(parsedOptions.get(ConnectionOptionType.REGISTRY_URL).equals("http://localhost:5000"));
        
        mockConnectionOptions = new String[]{"--user-name", "ich"};
        parsedOptions = ConnectionOptionParser.parse(mockConnectionOptions);
        assertTrue(parsedOptions.get(ConnectionOptionType.USER_NAME).equals("ich"));
    }
    
}
