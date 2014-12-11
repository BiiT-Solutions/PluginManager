package com.biit.plugins.interfaces;

import java.lang.reflect.Method;
import java.util.List;

import com.biit.plugins.exceptions.InvalidMethodParametersException;
import com.biit.plugins.exceptions.MethodInvocationException;
import com.biit.plugins.exceptions.NoMethodFoundException;

public interface IPlugin extends net.xeoh.plugins.base.Plugin {

	public String getPluginName();

	public List<Method> getPluginMethods();

	public List<String> getPluginMethodParametersString(Method method);

	public Method getPluginMethod(String methodName, Class<?>... parameterTypes) throws NoSuchMethodException;

	public Object executeMethod(String methodName, Object... parameters) throws NoMethodFoundException,
			InvalidMethodParametersException, MethodInvocationException;
}