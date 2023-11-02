package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrgDisablePhoto;
import com.grsoft.dataobjects.Visit;
import com.grsoft.dataobjects.impl.OrgDisablePhotoImpl;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

public class VisitEditEx extends VisitEditNew {
	boolean noNeedPhoto;
	
	@Override
	protected void init(Bundle savedInstanceState) {
		super.init(savedInstanceState);
	
		OrgDisablePhotoImpl odi = new OrgDisablePhotoImpl();
		OrgDisablePhoto od = odi.getData();
		od.id = visit.getId();
		noNeedPhoto = odi.read();
		odi.close();
		
		findViewById(R.id.tvPhotoInfo).setVisibility(noNeedPhoto ? View.VISIBLE : View.GONE);
	}
	
	@Override protected int getContentView() { return R.layout.visiteditex; }
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK){			
			Visit v = visit.getData();
			if((v.items == null || v.items.size() == 0) && noNeedPhoto ) {
				if (!saveVisit())
					visit.delete();
				
				finish();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
}
