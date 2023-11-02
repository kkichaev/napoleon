package com.grsoft.prch_order;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.prch_order.dataobjects.ConfigHelper;
import com.grsoft.prch_order.dataobjects.Gate;

import android.app.Application;

public class App extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		
		Path.init(this);
		DataBaseManager.init();
		
		ConfigHelper.init();
		
		DbWriter.checkDBTable(Gate.class);
		
		File f = getOrderFile();
		if(f.exists() == false) {
			InputStream in = null;
			FileOutputStream out = null;
			try {
				in = getResources().openRawResource(R.raw.order);
				out = new FileOutputStream(f);
				
				copyFile(in, out);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static File getOrderFile() { return new File(Path.getFilesDir(), "order.xls"); }
	
	/**
	 * Copy and Close streams
	 * @param in
	 * @param out
	 */
	public static void copyFile(InputStream in, OutputStream out) {
		try {
			byte[] buff = new byte[10240];
			int read = 0;
			
			while ((read = in.read(buff)) > 0) {
			      out.write(buff, 0, read);
			   }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(in != null)
					in.close();
				if(out != null)
					out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
