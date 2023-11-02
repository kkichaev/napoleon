package com.grsoft.napoleon;

import android.widget.Spinner;

import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.util.Util;


public class PhotoSettingEx extends PhotoSetting {
	Spinner spMaxPkgLimit;
	
	@Override
	protected int getContentViewID() { return R.layout.photo_settingex ;}
	
	@Override
	protected void postCameraViewInit(CfgNplW config) {
		long pkg_len = config.max_packet_len;
		int sel_item = -1;
		
		for(int i = 0; i < spMaxPkgLimit.getCount(); i++){
			String s = (String)spMaxPkgLimit.getItemAtPosition(i);
			long sz = getPkgLen(s);
			
			if(sz == pkg_len){
				sel_item = i;
				break;
			}
				
		}
		
		spMaxPkgLimit.setSelection(sel_item, true);
	}

	protected long getPkgLen(String s) {
		return (long)(Util.StrToScale(s, 10) / 10.0 * 1000000L);
	}
	
	@Override
	protected void initChildView() {
		spMaxPkgLimit = (Spinner) findViewById(R.id.spMaxPkgLimit);
	}
	
	@Override
	protected void postSave(CfgNplW config) {
		try{
			String s = (String)spMaxPkgLimit.getSelectedItem();
			config.max_packet_len = getPkgLen(s);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
