package com.grsoft.napoleon;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;

import androidx.core.content.FileProvider;

import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.network.encrypt.EncodableConnection;
import com.grsoft.network.encrypt.Encryptor;
import com.grsoft.util.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class BehaviorSetting extends BehaviorSettingW {
	private Spinner spClientsAndGoods;
	private CheckBox cbKeepAway;
	private CheckBox cbOverlay;
	
	@Override protected int getContentViewID() { return R.layout.behavior_setting_new; }
	
	@Override
	protected void init() {
		super.init();
		spClientsAndGoods = (Spinner) findViewById(R.id.spClientsAndGoods);
		cbKeepAway = (CheckBox) findViewById(R.id.cbKeepAway);
		cbOverlay = findViewById(R.id.cbOverlay);
		
		CfgNpl cfex = (CfgNpl) config;
		
		if(spClientsAndGoods != null){
			if(cfex.onlyNewstItems < spClientsAndGoods.getCount())
				spClientsAndGoods.setSelection(cfex.onlyNewstItems, true);
		}
		
		if(cbKeepAway != null)
			cbKeepAway.setChecked(cfex.keepAwayInOrder);

		CheckBox cb = findViewById(R.id.cbWiFiPrezentation);
		if(cb != null)
			cb.setChecked(cfex.loadPresentationByWiFi);

		if(cbOverlay != null) {
			cbOverlay.setVisibility(Build.VERSION.SDK_INT >= 23 ? View.VISIBLE : View.GONE);

			if (Build.VERSION.SDK_INT >= 23) {
				final boolean canDrawOverlays = Settings.canDrawOverlays(this);
				cbOverlay.setChecked(cfex.overlay && canDrawOverlays);
				cbOverlay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if (isChecked) {
							if (!canDrawOverlays) {
								Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
								if (intent.resolveActivity(getPackageManager()) != null) {
									startActivity(intent);
								}
							}
						}
					}
				});
			}
		}

		View v = findViewById(R.id.btnShareDB);
		if(v != null)
			v.setOnClickListener(view -> shareDB());
	}

	void shareDB() {
		try {
			File src = new File(Path.getDataBasePath());
			File db = Util.encodeFile(this, src);

			if(db == null) {
				return;
			}

			Uri uri = null;

			if (Build.VERSION.SDK_INT >= 24) {
				uri = FileProvider.getUriForFile(this,getString(R.string.fileprovider_authorities), db);
			}else
				uri = Uri.fromFile(db);

			String type = getContentResolver().getType(uri);

			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
			sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			sendIntent.setType(type);

			Intent shareIntent = Intent.createChooser(sendIntent, null);
			startActivity(shareIntent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void save() {
		CfgNpl cfex = (CfgNpl) config;
		
		if(spClientsAndGoods != null)
			cfex.onlyNewstItems = spClientsAndGoods.getSelectedItemPosition();
		
		if(cbKeepAway != null)
			cfex.keepAwayInOrder = cbKeepAway.isChecked();

		if(cbOverlay != null) {
			cfex.overlay = cbOverlay.isChecked();
		}

		CheckBox cb = findViewById(R.id.cbWiFiPrezentation);
		if(cb != null)
			cfex.loadPresentationByWiFi = cb.isChecked();
		super.save();
	}
}
