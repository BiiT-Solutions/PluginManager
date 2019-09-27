package com.biit.plugins.exceptions;

public class MethodInvocationException extends Exception {
	private static final long serialVersionUID = 2781594922476539377L;

	public MethodInvocationException(String message) {
		super(message);
	}

	public MethodInvocationException(String message, Throwable e) {
		super(message, e);
	}
}
