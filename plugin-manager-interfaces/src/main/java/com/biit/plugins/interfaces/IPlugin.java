package com.biit.plugins.interfaces;

/*-
 * #%L
 * Plugin Manager (Interfaces)
 * %%
 * Copyright (C) 2014 - 2025 BiiT Sourcing Solutions S.L.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.biit.plugins.interfaces.exceptions.MethodInvocationException;
import org.pf4j.ExtensionPoint;

import java.lang.reflect.Method;
import java.util.List;

public interface IPlugin extends ExtensionPoint, Comparable<IPlugin> {

    String getPluginName();

    List<Method> getPluginMethods();

    List<String> getPluginMethodParametersString(Method method);

    Method getPluginMethod(String methodName, Class<?>... parameterTypes) throws NoSuchMethodException;

    Object executeMethod(String methodName, Object... parameters) throws MethodInvocationException;

}
