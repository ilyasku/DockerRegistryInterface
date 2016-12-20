package springshell.commands;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.support.DefaultPromptProvider;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CustomPromptProvider extends DefaultPromptProvider{
    @Override
    public String getPrompt() {
        return "docker-registry-shell> ";
    }

	
    @Override
    public String getProviderName() {
        return "DockerRegistryInterface prompt provider";
    }
}
