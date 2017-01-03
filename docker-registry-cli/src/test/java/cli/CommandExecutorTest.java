package cli;

import cli.parser.ConnectionOptionType;
import dockerregistry.model.LocalConfigHandler;
import exceptions.WrongNumberOfArgumentsForThisCommandException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.Matchers;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;


public class CommandExecutorTest {
    
    public CommandExecutorTest() {
    }

    @Test
    public void testExecute_setUrlCommand() throws IOException {
                
        Command command = new Command();
        command.setCommandString("set-url");
        command.setCommandArgs(Arrays.asList("http://localhost:5000"));
        command.setConnectionOptions(new HashMap<>());
        
        LocalConfigHandler localConfigHandler = mock(LocalConfigHandler.class);
        Mockito.doNothing().when(localConfigHandler).writeUrl(Matchers.anyString());
        
        CommandExecutor commandExecutor = new CommandExecutor();
        commandExecutor.setLocalConfigHandler(localConfigHandler);
        
        String response = commandExecutor.execute(command);
        assertTrue(response.equals("URL written to file."));           
    }
    
    @Test(expected = WrongNumberOfArgumentsForThisCommandException.class)
    public void testExecute_setUrlCommand_wrongNumberOfArguments() throws IOException {
                
        Command command = new Command();
        command.setCommandString("set-url");
        command.setCommandArgs(Arrays.asList());
        command.setConnectionOptions(new HashMap<>());
        
        LocalConfigHandler localConfigHandler = mock(LocalConfigHandler.class);
        
        CommandExecutor commandExecutor = new CommandExecutor();
        commandExecutor.setLocalConfigHandler(localConfigHandler);

        String response = commandExecutor.execute(command);                
    }
    
    
    
}
