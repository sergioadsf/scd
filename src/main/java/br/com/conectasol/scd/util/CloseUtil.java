package br.com.conectasol.scd.util;

import java.io.Closeable;
import java.io.IOException;
import java.util.logging.Logger;

public class CloseUtil {

	private CloseUtil() {
	}

	public static final void close(Closeable... cs) {
		for (Closeable c : cs) {
			if (c != null)
				try {
					c.close();
				} catch (IOException e) {
					Logger.getGlobal().info(e.getMessage());
				}
		}
	}
}
