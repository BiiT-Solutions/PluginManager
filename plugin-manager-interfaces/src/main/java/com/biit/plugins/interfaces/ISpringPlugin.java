package com.biit.plugins.interfaces;

import java.util.List;

public interface ISpringPlugin extends IPlugin, Comparable<ISpringPlugin> {

    List<Object> restControllers();
}
