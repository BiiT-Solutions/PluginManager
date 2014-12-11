package com.biit.plugins.exceptions;

public class NoMethodFoundException extends Exception {
	private static final long serialVersionUID = 2781594922476539377L;
	String methodName;

	public NoMethodFoundException(String message, String methodName) {
		super(message);
		this.methodName = methodName;
	}
	
	public String getMethodName(){
		return methodName;
	}
}
