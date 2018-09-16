package br.com.conectasol.scd.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;

public class UTF8ToAnsiUtils {

	public static final String UTF8_BOM = "\uFEFF";

	public static void convert(String... filepath) {
		FileInputStream fis = null;
		BufferedReader r = null;
		FileOutputStream fos = null;
		Writer w = null;
		try {
			BufferedInputStream in = new BufferedInputStream(new URL(filepath[0]).openStream());
//			URL url = new URL(filepath[0]);
			boolean firstLine = true;
//			fis = new FileInputStream(url.getFile());
			r = new BufferedReader(new InputStreamReader(in, "UTF8"));
			fos = new FileOutputStream(filepath[1]);
			w = new BufferedWriter(new OutputStreamWriter(fos, "UTF8"));
			for (String s = ""; (s = r.readLine()) != null;) {
				if (firstLine) {
					s = UTF8ToAnsiUtils.removeUTF8BOM(s);
					firstLine = false;
				}
				w.write(s + System.getProperty("line.separator"));
				w.flush();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CloseUtil.close(fis, r, fos, w);
		}
	}

	private static String removeUTF8BOM(String s) {
		if (s.startsWith(UTF8_BOM)) {
			s = s.substring(1);
		}
		return s;
	}
}
