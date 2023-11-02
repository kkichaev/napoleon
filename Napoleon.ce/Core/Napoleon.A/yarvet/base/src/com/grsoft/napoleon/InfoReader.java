package com.grsoft.napoleon;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.PresentSdcard;

public class InfoReader {
	public static final String FILE_NAME = "info";
	public String getInfo(String id) {
		String result = null;
		String path = ((CfgNplW)ConfigManager.getConfig()).presentpath;
		if (path.length() > 0) {
			File txt = new File(path, FILE_NAME);
			
			try {
				InputStream is = new FileInputStream(txt);
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);

				String s = br.readLine();
				final String DELIMITER = ";";
				
				while (s != null && result == null) {
					String[] data = s.split(DELIMITER);
					
					if(data.length == 2) {
						if (data[0].equals(id)) {
							result = path + "//" + data[1];
						}
					}
					s = br.readLine();
				}

				br.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}
}
