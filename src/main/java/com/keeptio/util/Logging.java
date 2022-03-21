package com.keeptio.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import javafx.beans.property.Property;

public class Logging {

	public static void addPropertyLogging(String name, Property<? extends Object> pt) {
		pt.addListener((obs, oldValue, newValue) -> {
			debug(String.format("CHANGED '%s': '%s'", name, newValue == null ? "null" : newValue.toString()));
		});
	}

	public static void debug(String message) {
		writeMessage(System.out, "DEBUG - " + message);
	}

	public static void warning(String message) {
		writeMessage(System.err, "WARNING - " + message);
	}

	private static void writeMessage(OutputStream out, String message) {
		try {
			Date now = new Date();
			out.write(now.toString().getBytes());
			out.write(" - ".getBytes());
			out.write(message.getBytes());
			out.write('\n');
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
