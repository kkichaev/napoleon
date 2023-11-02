package com.grsoft.napoleon.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;

public class Descr {
	private static final String FOLDER = "descr";

	public static String read(Context ctx, String id) {
		StringBuilder result = new StringBuilder();

		try {
			CfgNpl config = (CfgNpl) ConfigManager.getConfig();
			String srcPath = config.presentpath;
			File file = new File(new File(srcPath, FOLDER), id);
			if (file.exists()) {
				InputStream is = new FileInputStream(file);
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = null;
				try {
					br = new BufferedReader(isr);
					String s = br.readLine();

					while (s != null) {
						result.append(s);
						s = br.readLine();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (br != null)
						br.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result.toString();
	}
}
