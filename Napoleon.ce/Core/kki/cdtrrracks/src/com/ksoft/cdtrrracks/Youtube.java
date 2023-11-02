package com.ksoft.cdtrrracks;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;

public class Youtube extends YouTubeBaseActivity implements
		YouTubePlayer.OnInitializedListener {
	private static final String API_KEY = "AIzaSyAC-JtICElW_h8w6Dd9jI9IU-2ntQ7tiKw";
	private static final String LINK = "link";
	private String urlVideo = "";

	public static void open(Context context, String link) {
		Intent intent = new Intent(context, Youtube.class);
		intent.putExtra(LINK, link);
		context.startActivity(intent);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.youtube);
		YouTubePlayerView player = (YouTubePlayerView) findViewById(R.id.player);
		urlVideo = getIntent().getExtras().getString(LINK);
		player.initialize(API_KEY, this);
	}

	public void onInitializationFailure(Provider arg0,
			YouTubeInitializationResult error) {
		Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
	}

	public void onInitializationSuccess(Provider arg0, YouTubePlayer arg1,
			boolean wasRestored) {
		if (!wasRestored) {
			arg1.loadVideo(urlVideo);
		}

	}
}
