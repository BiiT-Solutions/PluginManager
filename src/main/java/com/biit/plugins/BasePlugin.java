package com.biit.plugins;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.biit.plugins.exceptions.InvalidMethodParametersException;
import com.biit.plugins.exceptions.MethodInvocationException;
import com.biit.plugins.exceptions.NoMethodFoundException;
import com.biit.plugins.interfaces.IPlugin;

/**
 * The plugins that will extend this class
 * 
 */
public abstract class BasePlugin implements IPlugin {
	private final static String METHODS_PREFIX_TO_SELECT = "method";
	private Map<String, Method> methodsMap;

	public BasePlugin() {
		methodsMap = new HashMap<String, Method>();
		List<Method> pluginMethods = getPluginMethods();
		for (Method pluginMethod : pluginMethods) {
			methodsMap.put(pluginMethod.getName(), pluginMethod);
		}
	}

	/**
	 * Return the methods that we want to represent in the GUI
	 */
	@Override
	public List<Method> getPluginMethods() {
		List<Method> methods = new ArrayList<Method>();
		for (Method method : this.getClass().getMethods()) {
			if (method.getName().startsWith(METHODS_PREFIX_TO_SELECT)) {
				methods.add(method);
			}
		}
		return methods;
	}

	/**
	 * Return the method that we want to invoke
	 */
	@Override
	public Method getPluginMethod(String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
		return this.getClass().getMethod(methodName, parameterTypes);
	}

	/**
	 * Returns a String representation of the parameters needed by the method
	 * 
	 * @param method
	 * @return
	 */
	@Override
	public List<String> getPluginMethodParametersString(Method method) {
		List<String> parameters = new ArrayList<String>();
		int parameterNumber = 0;
		for (Class<?> parameter : method.getParameterTypes()) {
			parameterNumber++;
			Class<?> componentType = parameter.getComponentType();
			if (componentType != null) {
				// The parameter is an array
				// We want the internal class of the array
				parameters.add("Parameter " + parameterNumber + " - Any number of '" + translateParameterTypeName(componentType.getSimpleName()) + "'");
			} else {
				parameters.add("Parameter " + parameterNumber + " - '" + translateParameterTypeName(parameter.getSimpleName()) + "'");
			}
		}
		if (parameterNumber == 0) {
			parameters.add("No parameters needed");
		}
		return parameters;
	}

	/**
	 * Returns a 'user friendly' representation of the class names
	 * 
	 * @param className
	 * @return
	 */
	private String translateParameterTypeName(String className) {
		switch (className) {
		case "Integer":
		case "Double":
		case "Float":
			return "Number";

		case "String":
			return "Text";
		}
		return className;
	}

	protected Method getMethod(String methodName) {
		return methodsMap.get(methodName);
	}

	/**
	 * Return true if the parameters passed match the parameters that the method
	 * requires
	 * 
	 * @param method
	 * @param pameters
	 * @return
	 */
	protected boolean areParametersMatching(Method method, Object... parameters) {
		int parameterNumber = 0;
		if (method.getParameterTypes().length != parameters.length) {
			return false;
		}
		for (Class<?> methodParameter : method.getParameterTypes()) {
			Class<?> methodComponentType = methodParameter.getComponentType();
			if (methodComponentType != null) {
				// The parameter is an array
				Class<?> componentType = parameters[parameterNumber].getClass().getComponentType();
				if (componentType != null) {
					// The parameters passed is not an array
					return false;
				} else {
					if (!methodComponentType.equals(componentType)) {
						// The classes are not the same
						return false;
					}
				}
			} else {
				if (!methodParameter.equals(parameters[parameterNumber].getClass())) {
					// The classes are not the same
					return false;
				}
			}
			parameterNumber++;
		}
		return true;
	}

	@Override
	public Object executeMethod(String methodName, Object... parameters) throws NoMethodFoundException, InvalidMethodParametersException,
			MethodInvocationException {
		Method methodFound = getMethod(methodName);
		if (methodFound == null) {
			throw new NoMethodFoundException("The method '" + methodName + "' was not found", methodName);
		} else {
			if (!areParametersMatching(methodFound, parameters)) {
				throw new InvalidMethodParametersException("Invalid parameters for the method '" + methodName + "'", methodName, parameters);
			} else {
				try {
					return methodFound.invoke(this, parameters);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					throw new MethodInvocationException("Exception invoking the method '" + methodName + "'", methodName, parameters);
				}
			}
		}
	}

	@Override
	public int compareTo(IPlugin plugin) {
		return getPluginName().compareTo(plugin.getPluginName());
	}
}
