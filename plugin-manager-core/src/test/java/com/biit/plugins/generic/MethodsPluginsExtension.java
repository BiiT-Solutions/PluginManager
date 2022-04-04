package com.biit.plugins.generic;

import org.pf4j.Extension;

import com.biit.plugins.BaseCommonPlugin;

@Extension
public class MethodsPluginsExtension extends BaseCommonPlugin implements MethodsDefinitions {

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