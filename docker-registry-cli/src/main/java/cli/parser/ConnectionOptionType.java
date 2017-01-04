package cli.parser;

public enum ConnectionOptionType {
    
    REGISTRY_URL("registry-url"), USER_NAME("user-name");
        
    private String humanReadableName;
    
    ConnectionOptionType(String name) {
        humanReadableName = name;        
    }
}
