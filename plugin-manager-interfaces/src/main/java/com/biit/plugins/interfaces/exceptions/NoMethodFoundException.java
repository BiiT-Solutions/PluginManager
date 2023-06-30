package com.biit.plugins.interfaces.exceptions;

public class NoMethodFoundException extends MethodInvocationException {
    private static final long serialVersionUID = 2781594922476539377L;

    public NoMethodFoundException(String message) {
        super(message);
    }
}
