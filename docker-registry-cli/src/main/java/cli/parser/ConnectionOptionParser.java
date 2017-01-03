package cli.parser;


import exceptions.InvalidConnectionOptionException;
import java.util.HashMap;
import java.util.Map;

public class ConnectionOptionParser {
    private static final Map<String, ConnectionOptionType> CONNECTION_OPTIONS;
    static {
        CONNECTION_OPTIONS = new HashMap<>();
        CONNECTION_OPTIONS.put("-r", ConnectionOptionType.REGISTRY_URL);
        CONNECTION_OPTIONS.put("--registry", ConnectionOptionType.REGISTRY_URL);
        CONNECTION_OPTIONS.put("--registry-url", ConnectionOptionType.REGISTRY_URL);
        CONNECTION_OPTIONS.put("-u", ConnectionOptionType.USER_NAME);
        CONNECTION_OPTIONS.put("--user-name", ConnectionOptionType.USER_NAME);
        CONNECTION_OPTIONS.put("--username", ConnectionOptionType.USER_NAME);
        CONNECTION_OPTIONS.put("-c", ConnectionOptionType.CREDENTIALS_FILE);
        CONNECTION_OPTIONS.put("--credentials", ConnectionOptionType.CREDENTIALS_FILE);
        CONNECTION_OPTIONS.put("--credentials-file", ConnectionOptionType.CREDENTIALS_FILE);        
    }
    
    
    
    public static Map<ConnectionOptionType, String> parse(String[] connectionArguments) {
        Map<ConnectionOptionType, String> parsedMapOfConnectionOptions = new HashMap<>();
        int i = 0;
        while (i < connectionArguments.length - 1){
            String currentArgument = connectionArguments[i];
            if (!CONNECTION_OPTIONS.containsKey(currentArgument)){
                throw new InvalidConnectionOptionException(currentArgument);
            }
            else {
                parsedMapOfConnectionOptions.put(CONNECTION_OPTIONS.get(currentArgument), connectionArguments[i+1]);
                i += 2;
            }
        }
        return parsedMapOfConnectionOptions;
    }
}
