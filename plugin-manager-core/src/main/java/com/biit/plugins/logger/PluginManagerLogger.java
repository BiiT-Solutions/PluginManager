package com.biit.plugins.logger;

/*-
 * #%L
 * Plugin Manager (Core)
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Defines basic log behavior. Uses log4j.properties.
 */
public class PluginManagerLogger {
	private static final Logger logger = LoggerFactory.getLogger(PluginManagerLogger.class);

	private PluginManagerLogger() {
	}

	/**
	 * Events that have business meaning (i.e. creating category, deleting form,
	 * ...). To follow user actions.
	 *
	 * @param message
	 *            message to be shown.
	 */
	private static void info(String message) {
		logger.info(message);
	}

	/**
	 * Events that have business meaning (i.e. creating category, deleting form,
	 * ...). To follow user actions.
	 *
	 * @param className
	 *            class to be logged.
	 *
	 * @param message
	 *            message to be shown.
	 */
	public static void info(String className, String message) {
		info(className + ": " + message);
	}

	/**
	 * Shows not critical errors. I.e. Email address not found, permissions not
	 * allowed for this user, ...
	 *
	 * @param message
	 *            message to be shown.
	 */
	private static void warning(String message) {
		logger.warn(message);
	}

	/**
	 * Shows not critical errors. I.e. Email address not found, permissions not
	 * allowed for this user, ...
	 *
	 * @param className
	 *            class to be logged.
	 *
	 * @param message
	 *            message to be shown.
	 */
	public static void warning(String className, String message) {
		warning(className + ": " + message);
	}

	/**
	 * For following the trace of the execution. I.e. Knowing if the application
	 * access to a method, opening database connection, etc.
	 *
	 * @param message
	 *            message to be shown.
	 */
	private static void debug(String message) {
		if (isDebugEnabled()) {
			logger.debug(message);
		}
	}

	/**
	 * For following the trace of the execution. I.e. Knowing if the application
	 * access to a method, opening database connection, etc.
	 *
	 * @param className
	 *            class to be logged.
	 *
	 * @param message
	 *            message logged.
	 */
	public static void debug(String className, String message) {
		debug(className + ": " + message);
	}

	/**
	 * To log any not expected error that can cause application malfuncionality.
	 * I.e. couldn't open database connection, etc..
	 *
	 * @param message
	 *            message to be shown.
	 */
	private static void severe(String message) {
		logger.error(message);
	}

	/**
	 * To log any not expected error that can cause application malfuncionality.
	 *
	 * @param className
	 *            class to be logged.
	 *
	 * @param message
	 *            message logged.
	 *
	 */
	public static void severe(String className, String message) {
		severe(className + ": " + message);
	}

	/**
	 * Used for debugging when accessing to a method.
	 *
	 * @param className
	 *            class to be logged.
	 * @param method
	 *            method logged.
	 */
	public static void entering(String className, String method) {
		debug(className, "ENTRY (" + method + ")");
	}

	/**
	 * Used for debugging when exiting from a method.
	 *
	 * @param className
	 *            class to be logged.
	 * @param method
	 *            method logged.
	 */
	public static void exiting(String className, String method) {
		debug(className, "RETURN (" + method + ")");
	}

	/**
	 * To log java exceptions and log also the stack trace.
	 *
	 * @param className
	 *            class to be logged.
	 * @param throwable
	 *            exception to be logged.
	 */
	public static void errorMessage(String className, Throwable throwable) {
		String error = stackTraceToString(throwable);
		severe(className, error);
	}

	public static String stackTraceToString(Throwable e) {
		StringBuilder sb = new StringBuilder();
		for (StackTraceElement element : e.getStackTrace()) {
			sb.append(element.toString());
			sb.append("\n");
		}
		return sb.toString();
	}

	public static boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	public static void debug(Class<?> clazz, String message) {
		debug(clazz.getName(), message);
	}

	public static void info(Class<?> clazz, String message) {
		info(clazz.getName(), message);
	}

	public static void warning(Class<?> clazz, String message) {
		warning(clazz.getName(), message);
	}
}
