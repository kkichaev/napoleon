package com.grsoft.napoleon;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import com.grsoft.napoleon.util.CfgNpl;


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
