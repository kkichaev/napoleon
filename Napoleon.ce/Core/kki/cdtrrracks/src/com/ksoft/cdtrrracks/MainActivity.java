package com.ksoft.cdtrrracks;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.Menu;
import android.view.View;

public class MainActivity extends FragmentActivity {

	private FragmentTabHost tabHost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		tabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
		tabHost.addTab(
				tabHost.newTabSpec(HomePage.class.toString()).setIndicator(
						View.inflate(this, R.layout.hometab, null)),
				HomePage.class, null);
		tabHost.addTab(
				tabHost.newTabSpec(ArtistsPage.class.toString()).setIndicator(
						View.inflate(this, R.layout.artiststab, null)),
				ArtistsPage.class, null);
		tabHost.addTab(
				tabHost.newTabSpec(AlbumsPage.class.toString()).setIndicator(
						View.inflate(this, R.layout.albumstab, null)),
				AlbumsPage.class, null);
		tabHost.addTab(
				tabHost.newTabSpec(SearchPage.class.toString()).setIndicator(
						View.inflate(this, R.layout.searchtab, null)),
				SearchPage.class, null);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
