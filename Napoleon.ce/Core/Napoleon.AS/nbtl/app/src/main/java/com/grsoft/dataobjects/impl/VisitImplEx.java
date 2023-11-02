package com.grsoft.dataobjects.impl;

import java.util.Date;
import com.grsoft.dataobjects.VisitEx;
import com.grsoft.napoleon.NapoleonAppBase;
import com.grsoft.napoleon.NapoleonAppNbtlBase;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class VisitImplEx extends VisitImpl{
	@Override
	public void addPhoto(byte[] photo) {
		if(data.items.size() == 0){
			Context context = NapoleonAppNbtlBase.context;;
			SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
			long sr = p.getLong(ScriptImplEx.CURRENT_SCRIPT_ROW_ID, ExtrasConst.INVALID_ROWID);
			
			if(sr != ExtrasConst.INVALID_ROWID){
				Date ph = ((VisitEx)data).photoDate; 
				
				if (ph == null || ph.getTime() == 0){
					((VisitEx)data).photoDate = Util.getDateTime();
					write();
					close();
				}
			}
		}
		
		super.addPhoto(photo);
	}
}
