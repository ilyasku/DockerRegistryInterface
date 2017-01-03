package cli;

import dockerregistry.model.LocalConfigHandler;
import exceptions.WrongNumberOfArgumentsForThisCommandException;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

public class CommandExecutor {

    private LocalConfigHandler localConfigHandler;

    public void setLocalConfigHandler(LocalConfigHandler aLocalConfigHandler) {
        localConfigHandler = aLocalConfigHandler;
    }

    private String executeSetUrl(String url) throws FileNotFoundException, UnsupportedEncodingException{
        localConfigHandler.writeUrl(url);
        return "URL written to file.";
    }

    public String execute(Command command) throws FileNotFoundException, UnsupportedEncodingException {
        if (command.getCommandString().equals("set-url")){
            if (command.getCommandArgs().size() != 1){
                throw new WrongNumberOfArgumentsForThisCommandException(1);
            }
            return executeSetUrl(command.getCommandArgs().get(0));
        }
        return "";
    }
    
}
