package com.grsoft.napoleon.test;

import java.io.File;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.database.Hitching;
import com.grsoft.dataobjects.OrgSum;
import com.grsoft.napoleon.UpdateDB;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.network.LoginData;
import com.grsoft.network.UpdateProcess;
import com.grsoft.network.UpdateProcess.Params;
import com.grsoft.util.Util;


public class SyncTest extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("SyncTest", "!!!!!!!   SyncTest   START !!!!!!");
		
		Path.clearDataDir();
		DataBaseManager.clearBase();
		DbWriter.checkDBTable(OrgSum.class);
		
		Params p = new Params();
		
		UpdateDB a = null;
		
		try{
			a = (UpdateDB) UpdateDB.activity.newInstance();
			for(Hitching h : a.getSyncData()) {
				p.indata.add(h);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		setUserInfo(context, p);
//		final Object w = new Object();
		
		UpdateProcess updater = new UpdateProcess(context){
			@Override
			protected void onPostExecute(Boolean result) {
				try{
					Log.d("SyncTest", "Copy base....");

					File src = new File(Path.getDataBasePath());
					File sdcard = Environment.getExternalStorageDirectory();
					File dist = new File(new File(sdcard, Path.SHARED_FOLDER), Path.BASE_NAME);
					Util.copy(src,dist);
		
					Log.d("SyncTest", "!!!!!!!   SyncTest   FINISHED !!!!!!");
					
//					synchronized (w) {
//						w.notify();
//					}
					
				}catch(Exception e){
					Log.d("SyncTest", "!!!!!!!   SyncTest   FAILED !!!!!!");
					
				}
		    }
		};
		updater.execute(p);
		
//		synchronized (w) {
//			try {
//				w.wait();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
				
	}

	protected void setUserInfo(Context ctx, Params p) {
		Config c = ConfigManager.getConfig();
		p.ip1 = c.address;
		p.ip2 = c.address2;
		p.port1 = c.port;
		p.port2 = c.port2;
		p.login = c.login;
		p.pass = c.passw;
		p.duration = LoginData.readDuration(ctx);
	}

}
