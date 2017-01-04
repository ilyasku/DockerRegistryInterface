package cli;

import cli.parser.ConnectionOptionType;
import dockerregistry.model.HttpInterface;
import dockerregistry.model.LocalConfigHandler;
import dockerregistry.model.Registry;
import exceptions.WrongNumberOfArgumentsForThisCommandException;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.mockito.Matchers;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class CommandExecutorTest {
    
    public CommandExecutorTest() {
    }

    @Test
    public void testExecute_setUrlCommand() throws IOException {
                
        
        // mocking dependencies
        LocalConfigHandler localConfigHandler = mock(LocalConfigHandler.class);
        Mockito.doNothing().when(localConfigHandler).writeUrl(Matchers.anyString());
        
        CommandExecutor commandExecutor = new CommandExecutor();
        commandExecutor.setLocalConfigHandler(localConfigHandler);
        
        commandExecutor.executeSetUrl("http://localhost:5000");        
    }    
    
    @Test
    public void testExecute_setCredentialsCommand() throws FileNotFoundException, UnsupportedEncodingException, IOException {
        Command command = new Command();
        command.setCommandString("set-credentials");
        command.setCommandArgs(Arrays.asList());
        command.setConnectionOptions(new HashMap<>());
        
        // mocking dependencies
        LocalConfigHandler localConfigHandler = mock(LocalConfigHandler.class);
        Mockito.doNothing().when(localConfigHandler).writeCredentials(Matchers.anyString());
        
        CommandExecutor commandExecutor = new CommandExecutor();
        commandExecutor.setLocalConfigHandler(localConfigHandler);
        
        
        ByteArrayInputStream in = new ByteArrayInputStream("testuser\ntestpassword".getBytes());
        System.setIn(in);        
        commandExecutor.executeSetCredentials();
        System.setIn(System.in);        
    }
        
    @Test
    public void testExecute_listRepositoriesCommand() throws UnsupportedEncodingException, IOException {
        
        Map<ConnectionOptionType, String> connectionOptions = new HashMap<>();
        
        // mocking dependencies
        String[] mockRepositoryNames = new String[]{"ubuntu", "whalesay", "busybox"};        
        Registry mockRegistry = mock(Registry.class);
        when(mockRegistry.getRepositoryNames()).thenReturn(mockRepositoryNames);
        HttpInterface mockHttpInterface = mock(HttpInterface.class); 
        Mockito.doNothing().when(mockHttpInterface).setEncodedUsernamePassword(Matchers.anyString());
        LocalConfigHandler mockLocalConfigHandler = mock(LocalConfigHandler.class);
        when(mockLocalConfigHandler.getUrl()).thenReturn("mock my life");
        when(mockLocalConfigHandler.getCredentials()).thenReturn("mock my life");
        
        CommandExecutor commandExecutor = new CommandExecutor();        
        commandExecutor.setRegistry(mockRegistry);
        commandExecutor.setHttpInterface(mockHttpInterface);
        commandExecutor.setLocalConfigHandler(mockLocalConfigHandler);
        
        String[] response = commandExecutor.executeListRepositories(connectionOptions);
        
        assertTrue(response.length == 3);
        assertTrue(response[0].equals("ubuntu"));
        assertTrue(response[1].equals("whalesay"));
        assertTrue(response[2].equals("busybox"));
    }
    
    @Test
    public void testExecute_listRepositoriesCommand_withOptionRegistry() throws UnsupportedEncodingException, IOException {
        System.out.println("------------------------------------------------------------");
        System.out.println("test: method testExecute_listRepositoriesCommand_withOptionRegistry");
        
        Map<ConnectionOptionType, String> connectionOptions = new HashMap<>();
        connectionOptions.put(ConnectionOptionType.REGISTRY_URL, "http://localhost:5000");
        
        // mocking dependencies
        String[] mockRepositoryNames = new String[]{"ubuntu", "whalesay", "busybox"};        
        Registry mockRegistry = mock(Registry.class);
        when(mockRegistry.getRepositoryNames()).thenReturn(mockRepositoryNames);
        HttpInterface mockHttpInterface = mock(HttpInterface.class); 
        Mockito.doNothing().when(mockHttpInterface).setEncodedUsernamePassword(Matchers.anyString());
        LocalConfigHandler mockLocalConfigHandler = mock(LocalConfigHandler.class);
        when(mockLocalConfigHandler.getUrl()).thenReturn("mock my life");
        when(mockLocalConfigHandler.getCredentials()).thenReturn("mock my life");
        
        CommandExecutor commandExecutor = new CommandExecutor();        
        commandExecutor.setRegistry(mockRegistry);
        commandExecutor.setHttpInterface(mockHttpInterface);
        commandExecutor.setLocalConfigHandler(mockLocalConfigHandler);
        
        String[] response = commandExecutor.executeListRepositories(connectionOptions);
        
        assertTrue(response.length == 3);
        assertTrue(response[0].equals("ubuntu"));
        assertTrue(response[1].equals("whalesay"));
        assertTrue(response[2].equals("busybox"));
    }

    @Ignore
    @Test
    public void testExecute_listRepositoriesCommand_withOptionUsername() throws UnsupportedEncodingException, IOException {
        System.out.println("------------------------------------------------------------");
        System.out.println("test: method testExecute_listRepositoriesCommand_withOptionUsername");
        
        Map<ConnectionOptionType, String> connectionOptions = new HashMap<>();
        connectionOptions.put(ConnectionOptionType.USER_NAME, "testuser");
        
        // mocking dependencies
        String[] mockRepositoryNames = new String[]{"ubuntu", "whalesay", "busybox"};        
        Registry mockRegistry = mock(Registry.class);
        when(mockRegistry.getRepositoryNames()).thenReturn(mockRepositoryNames);
        HttpInterface mockHttpInterface = mock(HttpInterface.class); 
        Mockito.doNothing().when(mockHttpInterface).setEncodedUsernamePassword(Matchers.anyString());
        LocalConfigHandler mockLocalConfigHandler = mock(LocalConfigHandler.class);
        when(mockLocalConfigHandler.getUrl()).thenReturn("mock my life");
        when(mockLocalConfigHandler.getCredentials()).thenReturn("mock my life");
        
        ByteArrayInputStream in = new ByteArrayInputStream("testpassword".getBytes());
        System.setIn(in);                
        
        CommandExecutor commandExecutor = new CommandExecutor();        
        commandExecutor.setRegistry(mockRegistry);
        commandExecutor.setHttpInterface(mockHttpInterface);
        commandExecutor.setLocalConfigHandler(mockLocalConfigHandler);                
        
        String[] response = commandExecutor.executeListRepositories(connectionOptions);
        System.setIn(System.in);
         
        assertTrue(response.length == 3);
        assertTrue(response[0].equals("ubuntu"));
        assertTrue(response[1].equals("whalesay"));
        assertTrue(response[2].equals("busybox"));
    }
    
    @Test
    public void testExecute_listTagsCommand() throws IOException {
        System.out.println("------------------------------------------------------------");
        System.out.println("test: method testExecute_listTagsCommand");
        
        List<String> repositories = Arrays.asList("ubuntu", "whalesay");
                
        String[] mockTagNamesUbuntu = new String[]{"latest", "1"};
        String[] mockTagNamesWhalesay = new String[]{"latest", "5", "7"};
        
        // mocking dependencies
        Registry mockRegistry = mock(Registry.class);
        when(mockRegistry.getTagNames("ubuntu")).thenReturn(mockTagNamesUbuntu);
        when(mockRegistry.getTagNames("whalesay")).thenReturn(mockTagNamesWhalesay);
        HttpInterface mockHttpInterface = mock(HttpInterface.class); 
        Mockito.doNothing().when(mockHttpInterface).setEncodedUsernamePassword(Matchers.anyString());
        LocalConfigHandler mockLocalConfigHandler = mock(LocalConfigHandler.class);
        when(mockLocalConfigHandler.getUrl()).thenReturn("mock my life");
        when(mockLocalConfigHandler.getCredentials()).thenReturn("mock my life");
     
        CommandExecutor commandExecutor = new CommandExecutor();        
        commandExecutor.setRegistry(mockRegistry);
        commandExecutor.setHttpInterface(mockHttpInterface);
        commandExecutor.setLocalConfigHandler(mockLocalConfigHandler);
        
        Map<String, List<String>> response = commandExecutor.executeListTags();                
        
    }
}