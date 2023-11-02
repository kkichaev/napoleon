package com.grsoft.napoleon;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import com.google.gson.Gson;
import com.grsoft.napoleon.util.debug.Path;

import android.content.Context;

public class MapHelper {
	
	public String createMap(Context context, Object data, String name){
		String result = inflateMap(context, name);
		final String DATASECTION = "DATASECTION";
		Gson gson = new Gson();
		String ds = gson.toJson(data);
		result = result.replace(DATASECTION, ds);
		
		saveHtml(context, result);
		
		return result;
	}
	
	private String inflateMap(Context context, String name) {
		StringBuilder sb = new StringBuilder();
		int resid = context.getResources().getIdentifier(name, "raw", context.getPackageName());
        
    	InputStream is = null; 
    	try{
    		is = context.getResources().openRawResource(resid);
	    	InputStreamReader r = new InputStreamReader(is, Charset.forName("utf-8"));
	    	BufferedReader br = new BufferedReader(r);
	        
	        String line;
        
	        while ((line = br.readLine()) != null) {
	             sb.append(line).append("\n");
	        }
    	} catch(Exception e){
        	e.printStackTrace();
        } finally{
    		if (is != null)
    			try{
    				is.close();
    			}catch(Exception e){
    				e.printStackTrace();
    			}
    	}
    	
    	return sb.toString();
	}
	
	public File saveHtml(Context context, String output) {
		File cacheDir = Path.getCacheDir(context);
		File result = new File(cacheDir, "map.html");

		OutputStreamWriter osw = null;
		try {
			osw = new OutputStreamWriter(new FileOutputStream(result), "utf-8");
			osw.write(output);
			osw.flush();
			osw.close();
			osw = null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (osw != null)
				try {
					osw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return result;
	}
}
