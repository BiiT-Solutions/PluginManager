package com.biit.plugins.exceptions;

public class DuplicatedPluginFoundException extends PluginException {
	private static final long serialVersionUID = 6156145755688864651L;

	public DuplicatedPluginFoundException(String message) {
		super(message);
	}
}
