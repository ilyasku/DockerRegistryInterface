package cli;

import cli.parser.ConnectionOptionType;
import java.util.List;
import java.util.Map;

public class Command {
    private String commandString;
    private Map<ConnectionOptionType, String> connectionOptions;    
    private List<String> commandArgs;

    public Map<ConnectionOptionType, String> getConnectionOptions() {
        return connectionOptions;
    }

    public void setConnectionOptions(Map<ConnectionOptionType, String> connectionOptions) {
        this.connectionOptions = connectionOptions;
    }

    public String getCommandString() {
        return commandString;
    }

    public void setCommandString(String commandString) {
        this.commandString = commandString;
    }

    public List<String> getCommandArgs() {
        return commandArgs;
    }

    public void setCommandArgs(List<String> commandArgs) {
        this.commandArgs = commandArgs;
    }
}
