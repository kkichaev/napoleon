package com.grsoft.napmobile.base;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.os.Environment;

import com.grsoft.aceteam.R;
import com.grsoft.dataobjects.Log;
import com.grsoft.dataobjects.impl.LogImpl;
import com.grsoft.napoleon.CrashReport;
import com.grsoft.util.Util;

@SuppressLint("SimpleDateFormat")
public class MainExceptionHandler implements UncaughtExceptionHandler {
	private Context context;
	private String folder;
	
	public MainExceptionHandler(Context context, String folder){
		this.context = context;
		this.folder = folder;
	}
	
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		ex.printStackTrace();
		Throwable cause = ex.getCause() == null ? ex : ex.getCause();
		
		if (cause != null){
			StringBuilder sb = new StringBuilder();
			Resources res = context.getResources(); 
			SimpleDateFormat dateFormat =  new SimpleDateFormat("dd.MM.yy HH:mm");
			sb.append("data: ").append(dateFormat.format(Util.getDateTime())).append('\n');
			sb.append("project=").append(res.getString(R.string.project)).append('\n');
			sb.append("version=").append(res.getString(R.string.version)).append('\n');
			sb.append("build date=").append(context.getString(R.string.buildTime)).append('\n');;
			sb.append("model=").append(android.os.Build.MODEL).append('\n');
			sb.append("android=").append(android.os.Build.VERSION.RELEASE).append('\n');
			sb.append("stack:\n");
		    final Writer result = new StringWriter();
		    final PrintWriter printWriter = new PrintWriter(result);
		    cause.printStackTrace(printWriter);
			sb.append(result.toString());
			
			LogImpl.logd(Log.PROGRAMM_CRASHED, sb.toString());
			
			if (Environment.getExternalStorageState()
					.equals(Environment.MEDIA_MOUNTED)){

				try {
					File logFile = new File(Environment.getExternalStorageDirectory(), folder + "/crash.log");
					
					if (!logFile.exists()) 
						logFile.createNewFile();
					
					FileOutputStream fos = new FileOutputStream(logFile);
					fos.write(sb.toString().getBytes());
					fos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			CrashReport.open(context, folder, sb.toString());
		}

		System.exit(10);
	}
}
