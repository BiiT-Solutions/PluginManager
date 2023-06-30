package com.biit.plugins.exceptions;

import com.biit.plugins.interfaces.exceptions.PluginException;

public class DuplicatedPluginFoundException extends PluginException {
    private static final long serialVersionUID = 6156145755688864651L;

    public DuplicatedPluginFoundException(String message) {
        super(message);
    }
}
