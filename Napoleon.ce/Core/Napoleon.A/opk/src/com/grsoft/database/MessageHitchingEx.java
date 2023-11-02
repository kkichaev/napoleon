package com.grsoft.database;

import android.app.NotificationManager;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

import java.io.IOException;

import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.util.GlobalServiceContext;

public class MessageHitchingEx extends MessageHitching {

	boolean alreadyNotified = false;
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		
		if (!alreadyNotified){
			
			final Context context= GlobalServiceContext.service.getApplicationContext(); 
			final CfgNplEx cfg = (CfgNplEx) ConfigManager.getConfig();
			if(cfg.notifySound.length() > 0) {
				Thread t = new Thread(new Runnable() {
					
					@Override
					public void run() {
						MediaPlayer mp = new MediaPlayer();
						Uri notification = Uri.parse(cfg.notifySound);
						try {
							mp.setDataSource(context, notification);
							mp.prepare();
							mp.start();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
				t.start();
			}
      	    
       	    Vibrator v = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
       	    v.vibrate(500);
       	    
       	    alreadyNotified=true;

		}
		super.onRead(rawObject);
	}
	
}
