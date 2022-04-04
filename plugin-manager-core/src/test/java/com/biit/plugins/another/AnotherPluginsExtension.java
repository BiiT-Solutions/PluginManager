package com.biit.plugins.another;

import org.pf4j.Extension;

import com.biit.plugins.BasePlugin;

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