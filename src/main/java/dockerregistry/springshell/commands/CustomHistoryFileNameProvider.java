package dockerregistry.springshell.commands;

import dockerregistry.model.LocalConfigHandler;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.support.DefaultHistoryFileNameProvider;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CustomHistoryFileNameProvider extends DefaultHistoryFileNameProvider {
    
    @Override
    public String getHistoryFileName() {
        
        return LocalConfigHandler.getHomePath() + "/"
                + LocalConfigHandler.getConfigDirectoryPath() + "/"
                + LocalConfigHandler.getShellHistoryFileName();
        //return "shell.log";
    }

    @Override
    public String getProviderName() {
        return "docker-registry-shell history file name provider";
    }
}
