package cli.parser;

import cli.Command;
import exceptions.InvalidCommandException;
import exceptions.InvalidConnectionOptionException;
import org.junit.Test;
import static org.junit.Assert.*;

public class ParserTest {
    
    public ParserTest() {
    }   
        
    @Test
    public void testParseCompleteCommand_ratherCompleteCLIArguments() {
        
        System.out.println("------------------------------------------------------------");
        System.out.println("test: method testParseCompleteCommand_ratherCompleteCLIArguments");
        
        String[] arrayOfCLIArguments = new String[] {"-u", "testuser", // 1 set of connection options
            "delete-images", // command
            "ubuntu:2", "whalesay:6"}; // 2 command arguments
            
        System.out.println("created mock arguments ...");
                
        Command command = Parser.parseCompleteCommand(arrayOfCLIArguments);
        System.out.println("parsed complete commands");
        
        assertEquals(command.getConnectionOptions().size(), 1);
        assertTrue(command.getCommandString().equals("delete-images"));
        assertEquals(command.getCommandArgs().size(), 2);
        
        assertTrue(command.getConnectionOptions().containsKey(ConnectionOptionType.USER_NAME));
        assertTrue(command.getConnectionOptions().get(ConnectionOptionType.USER_NAME).equals("testuser"));
        assertTrue(command.getCommandArgs().contains("ubuntu:2"));
        assertTrue(command.getCommandArgs().contains("whalesay:6"));
    }
    
    @Test
    public void testParseCompleteCommand_noConnectionOptions() {
        
        System.out.println("------------------------------------------------------------");
        System.out.println("test: method testParseCompleteCommand_noConnectionOptions");
                
        String[] arrayOfCLIArguments = new String[] {"set-url", // command
            "http://localhost:5000"}; // 1 command arguments
            
        System.out.println("created mock arguments ...");
                
        Command command = Parser.parseCompleteCommand(arrayOfCLIArguments);
        System.out.println("parsed complete commands");
        
        assertEquals(command.getConnectionOptions().size(), 0);
        assertTrue(command.getCommandString().equals("set-url"));
        assertEquals(command.getCommandArgs().size(), 1);        
        assertTrue(command.getCommandArgs().contains("http://localhost:5000"));
    }
    
    @Test
    public void testParseCompleteCommand_commandOnly() {        
        System.out.println("------------------------------------------------------------");
        System.out.println("test: method testParseCompleteCommand_commandOnly");
                
        String[] arrayOfCLIArguments = new String[] {"set-credentials"};
            
        System.out.println("created mock arguments ...");
                
        Command command = Parser.parseCompleteCommand(arrayOfCLIArguments);
        System.out.println("parsed complete commands");
        
        assertEquals(command.getConnectionOptions().size(), 0);
        assertTrue(command.getCommandString().equals("set-credentials"));
        assertEquals(command.getCommandArgs().size(), 0);        
    }    
    
    @Test(expected = InvalidCommandException.class)
    public void testParseCompleteCommand_invalidCommand() {
        System.out.println("------------------------------------------------------------");
        System.out.println("test: method testParseCompleteCommand_invalidCommand");
                
        String[] arrayOfCLIArguments = new String[] {"set-something"};            
        System.out.println("created mock arguments ...");
                
        Command command = Parser.parseCompleteCommand(arrayOfCLIArguments);
        System.out.println("parsed complete commands");
        
        fail("expected exception was not thrown!");
    }
     
    @Test(expected = InvalidConnectionOptionException.class)
    public void testParseCompleteCommand_invalidOption() {
        System.out.println("------------------------------------------------------------");
        System.out.println("test: method testParseCompleteCommand_invalidNumberOfOptionArguments");
                
        String[] arrayOfCLIArguments = new String[] {"-c","dings","--yo-sicher","bums","list-tags"};            
        System.out.println("created mock arguments ...");
                
        Command command = Parser.parseCompleteCommand(arrayOfCLIArguments);
        System.out.println("parsed complete commands");
        
        fail("expected exception was not thrown!");
    }
}
