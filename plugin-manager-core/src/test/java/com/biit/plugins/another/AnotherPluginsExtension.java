package com.biit.plugins.another;

import com.biit.plugins.BasePlugin;
import org.pf4j.Extension;

@Extension
public class AnotherPluginsExtension extends BasePlugin implements AnotherDefintions {

	@Override
	public String methodGetGreeting() {
		return "Another greeting";
	}

	@Override
	public String getPluginName() {
		return "another-plugin";
	}

	/**
	 * Methods that starts with "method" are selectables.
	 */
	public void methodOne() {
		return;
	}
}