package com.ksoft.ardalarm;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;

public class SoundPlay extends Service {

	private MediaPlayer player;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		SharedPreferences pref = getSharedPreferences(
				Setting.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
		String message_snd = pref.getString(Setting.MESSAGE_SND, "");
		player = new MediaPlayer();
		player.setAudioStreamType(AudioManager.STREAM_MUSIC);
		player.setLooping(true);
		try {
			player.setDataSource(this, Uri.parse(message_snd));
			player.prepare();
			player.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Intent i = new Intent(this, ArdruinoExcahge.class);
		startService(i);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		if(player != null)
			player.stop();
	}
}
