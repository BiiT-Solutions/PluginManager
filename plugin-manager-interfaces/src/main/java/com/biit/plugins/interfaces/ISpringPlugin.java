package com.biit.plugins.interfaces;

import java.util.List;

public interface ISpringPlugin extends IPlugin<ISpringPlugin> {

    List<Object> restControllers();
}
