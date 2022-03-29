package com.biit.plugins.helloworld;

import org.pf4j.ExtensionPoint;

public interface Greeting extends ExtensionPoint {
	String getGreeting();
}