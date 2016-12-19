package dockerregistry.springshell.commands;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.support.DefaultBannerProvider;
import org.springframework.shell.support.util.OsUtils;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CustomBannerProvider extends DefaultBannerProvider  {

        @Override
	public String getBanner() {
		StringBuilder buf = new StringBuilder();
		buf.append("=======================================").append(OsUtils.LINE_SEPARATOR);
		buf.append("*                                     *").append(OsUtils.LINE_SEPARATOR);
		buf.append("*       docker-registry-shell         *").append(OsUtils.LINE_SEPARATOR);
		buf.append("*                                     *").append(OsUtils.LINE_SEPARATOR);
		buf.append("=======================================").append(OsUtils.LINE_SEPARATOR);
		buf.append("Version:").append(this.getVersion());
		return buf.toString();
	}

        @Override
	public String getVersion() {
		return "0.0.1";
	}

        @Override
	public String getWelcomeMessage() {
		return "Welcome to docker-registry-shell";
	}
	
	@Override
	public String getProviderName() {
		return "docker-registry-shell Banner";
	}
}