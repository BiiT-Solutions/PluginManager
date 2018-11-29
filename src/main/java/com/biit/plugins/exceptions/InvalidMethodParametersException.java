package com.biit.plugins.exceptions;

public class InvalidMethodParametersException extends MethodInvocationException {
	private static final long serialVersionUID = 2781594922476539377L;

	public InvalidMethodParametersException(String message) {
		super(message);
	}
}
