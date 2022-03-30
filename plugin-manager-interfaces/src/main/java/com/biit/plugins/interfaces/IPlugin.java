package com.biit.plugins.interfaces;

import com.biit.plugins.interfaces.exceptions.MethodInvocationException;
import org.pf4j.ExtensionPoint;

import java.lang.reflect.Method;
import java.util.List;

public interface IPlugin extends ExtensionPoint, Comparable<IPlugin> {

	String getPluginName();

	List<Method> getPluginMethods();

	List<String> getPluginMethodParametersString(Method method);

	Method getPluginMethod(String methodName, Class<?>... parameterTypes) throws NoSuchMethodException;

	Object executeMethod(String methodName, Object... parameters) throws  MethodInvocationException;
}