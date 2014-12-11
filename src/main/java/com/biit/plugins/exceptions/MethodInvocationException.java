package com.biit.plugins.exceptions;

public class MethodInvocationException extends Exception {
	private static final long serialVersionUID = 2781594922476539377L;
	String methodName;
	Object[] parameters;

	public MethodInvocationException(String message, String methodName, Object[] parameters) {
		super(message);
		this.methodName = methodName;
		this.parameters = parameters;
	}
	
	public String getMethodName(){
		return methodName;
	}
	
	public Object[] getParameters(){
		return parameters;
	}
}
