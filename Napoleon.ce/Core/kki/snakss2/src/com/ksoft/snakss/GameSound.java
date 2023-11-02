package com.ksoft.snakss;

import android.content.Context;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;

public class GameSound {
	public enum Sounds { Eat, GameOver }
	private SoundPool soundPool;
	private int[] idx = new int[3];
	
	public GameSound(Context context) {
		soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
		AssetManager am = context.getAssets();
		try {
			idx[0] = soundPool.load(am.openFd("sounds/fx20.wav"), 1);
			idx[1] = soundPool.load(am.openFd("sounds/fx01.wav"), 1);
			idx[2] = soundPool.load(am.openFd("sounds/fx12.wav"), 1);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void play(Sounds s) {
		if (s == Sounds.Eat)
			soundPool.play(idx[1], 1, 1, 1, 0, 1.0f);
		else if (s == Sounds.GameOver)
			soundPool.play(idx[2], 1, 1, 1, 0, 1.0f);
	}
}
