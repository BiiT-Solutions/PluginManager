package com.biit.plugins.springboot.plugin;

import com.biit.plugins.interfaces.ISpringPlugin;
import org.pf4j.ExtensionPoint;

import java.util.List;

public interface SpringGreetingPlugin extends ISpringPlugin {
	String methodGetGreeting();
	List<Object> restControllers();
}