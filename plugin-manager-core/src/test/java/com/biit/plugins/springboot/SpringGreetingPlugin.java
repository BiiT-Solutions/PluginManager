package com.biit.plugins.springboot;

import org.pf4j.ExtensionPoint;

import java.util.List;

public interface SpringGreetingPlugin extends ExtensionPoint {
	String getGreeting();
	List<Object> restControllers();
}