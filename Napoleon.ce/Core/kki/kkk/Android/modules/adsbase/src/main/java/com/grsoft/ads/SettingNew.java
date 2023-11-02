package com.grsoft.ads;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceActivity;
import android.widget.Toast;

import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.util.Util;

public class SettingNew extends PreferenceActivity {
	public static final String LOGIN = "login";
	public static final String PASSWORD = "password";
	public static final String IP1 = "ip1";
	public static final String IP2 = "ip2";
	public static final String PORT = "port";
	public static final String FULL_SETTING = "full_setting";
	private static List<String> fragments = new ArrayList<String>();

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
	}

	@Override
	public void onHeaderClick(Header header, int position) {
		super.onHeaderClick(header, position);

		if (header.id == R.id.hdExport)
			exportDataBase();
	}

	private void exportDataBase() {
		new AsyncTask<Void, Void, Void>(){
			@Override
			protected Void doInBackground(Void... voids) {
				try {
					File src = new File(Path.getDataBasePath());
					File sdcard = Environment.getExternalStorageDirectory();
					File adsDir = new File(sdcard, "ADS");

					if (!adsDir.exists())
						adsDir.createNewFile();

					File dist = new File(adsDir, Path.BASE_NAME);

					Util.copy(src, dist);
				}catch (Exception e){
					e.printStackTrace();
				}

				return null;
			}

			@Override
			protected void onPostExecute(Void aVoid) {
				Toast.makeText(SettingNew.this, "Ёкспорт завершен", Toast.LENGTH_SHORT).show();
			}
		}.execute();
	}

	@Override
	public void onBuildHeaders(List<Header> target) {
		loadHeadersFromResource(R.xml.preference_headers, target);

		boolean full = getIntent().getBooleanExtra(FULL_SETTING, false);

		if (!full)
			filterHeader(target);

		updateFragments(target);
	}

	private void filterHeader(List<Header> target) {
		List<Header> copy = new ArrayList<>();

		for (Header h : target) {
			if (h.id == R.id.hdNetwork ||
					h.id == R.id.hdSyncBg)
				continue;

			copy.add(h);
		}

		target.clear();
		target.addAll(copy);
	}

	protected void updateFragments(List<Header> target) {
		fragments.clear();
        for (Header header : target) {
            fragments.add(header.fragment);
        }
	}
	
	protected boolean isValidFragment(String fragmentName) { return fragments.contains(fragmentName); }
}
