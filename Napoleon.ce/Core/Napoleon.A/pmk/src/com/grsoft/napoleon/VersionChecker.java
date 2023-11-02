package com.grsoft.napoleon;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.grsoft.util.Updater;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.widget.Toast;

public class VersionChecker {
	public static final int SLEEP_TIMER = 10 * 60 * 1000; // 10 минут
	static final int WAIT_ANSWER = 5000;
	
	static boolean isSameVersion = true;
	static VersionCheckerThread checker = null;
	static Object waitToken = new Object();
	
	public static boolean isLastVersion(Context context, boolean forceCheck) {
		Resources res = context.getResources();
		String project = res.getString(R.string.project);
		String version = Updater.getVersion(res);
		String link = res.getString(R.string.link);

		if(forceCheck) {
			if(checker != null) {
				checker.interrupt();
				checker = null;
			}

			checker = new VersionCheckerThread(project, version, link, waitToken);
			checker.start();
			try {
				waitToken.wait(WAIT_ANSWER);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			if(checker == null) {
				checker = new VersionCheckerThread(project, version, link, waitToken);
				checker.start();
			}
		}
		return isSameVersion;
	}
	
	public static synchronized Boolean checkLastVersion(String project, String version, String link) {
		Boolean isSame = null;
		try {
			StringBuilder url = new StringBuilder();
			url.append(link);
			url.append("/upgrade/proj_list.php");
		
			HttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet(url.toString());
			HttpResponse response = null;
	
			response = client.execute(get);
			HttpEntity entity = response.getEntity();
			String retSrc = EntityUtils.toString(entity, "UTF-8");
			String[] prj = retSrc.split("</tr>");
			for(String prjStr : prj) {
				String[] tds = prjStr.split("</td>");
				if(tds.length > 2 && tds[0].contains(project)) {
					isSame = compareVersions(tds[1], version);
					break;
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return isSame;
	}
	
	
	public static synchronized void setIsSameVersion(boolean newVal) { isSameVersion = newVal; }
	
	public static boolean compareVersions(String siteVersion, String prgVersion) {
		boolean res = true;
		int idx = siteVersion.indexOf("<td>");
		if(idx >= 0)
			siteVersion = siteVersion.substring(idx + 4).trim();
		siteVersion = Updater.getVersion(siteVersion);
		String[] sv = siteVersion.split("\\.");
		String[] pv = prgVersion.split("\\.");
		for(int i=0; i<sv.length; i++){
			if(i >= pv.length) {
				res = false;
				break;
			}
			
			try {
				if( Integer.parseInt(sv[i]) > Integer.parseInt(pv[i]) ) {
					res = false;
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return res;
	}

	public static Dialog createAlertDialog(final Context context) {
		AlertDialog.Builder b = new AlertDialog.Builder(context);
		b.setTitle("Ошибка");
		b.setMessage("Обновитесь до последней версии");
		b.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				new Updater(){
					protected void onPreExecute() { 
						Toast.makeText(context, R.string.check_updating, Toast.LENGTH_SHORT).show();
					};
					
					protected void onPostExecute(Boolean result) {
						if(!result)
							Toast.makeText(context, R.string.update_not_found, Toast.LENGTH_SHORT).show();
					};
				}.execute(context);
			}
		});
		return b.create();
	}
}

class VersionCheckerThread extends Thread {
	String project;
	String version;
	String link;
	Object waitToken;
	
	public VersionCheckerThread(String project, String version, String link, Object waitToken) {
		this.project = project;
		this.link = link;
		this.version = version;
		this.waitToken = waitToken;
	}
	
	@Override
	public void run() {
		Thread thisThread = Thread.currentThread();
		while(true) {
			try {
				if(thisThread.isInterrupted())
					break;
				
				Boolean isSame = VersionChecker.checkLastVersion(project, version, link);
				if(isSame != null)
					VersionChecker.setIsSameVersion(isSame);
				
				synchronized (waitToken) {
					waitToken.notify();
				}
				
				Thread.sleep(VersionChecker.SLEEP_TIMER);
			} catch(Exception e) {
				e.printStackTrace();
				break;
			}
		}
	}
}
