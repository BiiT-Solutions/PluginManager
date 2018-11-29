package com.biit.plugins.another;

import org.pf4j.Extension;

import com.biit.plugins.BasePlugin;

@Extension
public class AnotherPluginsExtension extends BasePlugin implements AnotherDefintions {

	public String getGreeting() {
		return "Welcome";
	}

	public String getPluginName() {
		return "methods-plugin";
	}

	/**
	 * Methods that starts with "method" are selectables.
	 */
	public void methodOne() {
		return;
	}

	/**
	 * Methods that starts with "method" are selectables.
	 */
	public void methodTwo() {
		return;
	}
}