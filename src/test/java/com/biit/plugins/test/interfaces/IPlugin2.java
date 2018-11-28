package com.biit.plugins.test.interfaces;

import java.lang.reflect.Method;
import java.util.List;

import org.pf4j.ExtensionPoint;

import com.biit.plugins.exceptions.InvalidMethodParametersException;
import com.biit.plugins.exceptions.MethodInvocationException;
import com.biit.plugins.exceptions.NoMethodFoundException;

public interface IPlugin2 extends ExtensionPoint {

	String getPluginName();

	List<Method> getPluginMethods();

	List<String> getPluginMethodParametersString(Method method);

	Method getPluginMethod(String methodName, Class<?>... parameterTypes) throws NoSuchMethodException;

	Object executeMethod(String methodName, Object... parameters) throws NoMethodFoundException, InvalidMethodParametersException, MethodInvocationException;
}