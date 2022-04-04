package com.biit.plugins;

import com.biit.plugins.interfaces.IPlugin;
import com.biit.plugins.interfaces.ISpringPlugin;
import com.biit.plugins.interfaces.exceptions.InvalidMethodParametersException;
import com.biit.plugins.interfaces.exceptions.MethodInvocationException;
import com.biit.plugins.interfaces.exceptions.NoMethodFoundException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * The plugins that will extend this class
 */
public abstract class SpringBasePlugin implements ISpringPlugin {
    public final static String METHODS_PREFIX_TO_SELECT = "method";
    private Map<String, Method> methodsMap;

    public SpringBasePlugin() {
        methodsMap = new HashMap<>();
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
                parameters.add("Parameter " + parameterNumber + " - Any number of '"
                        + translateParameterTypeName(componentType.getSimpleName()) + "'");
            } else {
                parameters.add("Parameter " + parameterNumber + " - '"
                        + translateParameterTypeName(parameter.getSimpleName()) + "'");
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
     * @param parameters
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
                return false;
            } else {
                if (!methodParameter.equals(parameters[parameterNumber].getClass())) {
                    return false;
                }
            }
            parameterNumber++;
        }
        return true;
    }

    @Override
    public Object executeMethod(String methodName, Object... parameters) throws MethodInvocationException {
        Method methodFound = getMethod(methodName);
        if (methodFound == null) {
            throw new NoMethodFoundException("The method '" + methodName + "' was not found");
        } else {
            if (!areParametersMatching(methodFound, parameters)) {
                throw new InvalidMethodParametersException(
                        "Invalid parameters '" + Arrays.toString(parameters) + "' for the method '" + methodName + "'");
            } else {
                try {
                    return methodFound.invoke(this, parameters);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    throw new MethodInvocationException(
                            "Exception invoking the method '" + methodName + "' with parameters '" + Arrays.toString(parameters) + "'.",
                            e);
                }
            }
        }
    }

    @Override
    public int compareTo(IPlugin plugin) {
        return getPluginName().compareTo(plugin.getPluginName());
    }
}
