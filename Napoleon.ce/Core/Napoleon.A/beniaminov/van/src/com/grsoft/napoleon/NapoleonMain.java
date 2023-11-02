package com.grsoft.napoleon;

import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.napoleon.util.ConfigManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.IntentCompat;

public class NapoleonMain extends NapoleonEx {
	public static void open(Context context){
		Intent intent = new Intent(context, NapoleonMain.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK); 
		context.startActivity(intent);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		CfgNplEx cfg = (CfgNplEx) ConfigManager.getConfig();
		
		if(cfg.simpleMode){
			SimpleMode.open(this);
			DocType.setCurDoc(SalesDoc.instance());
		}
	}
}
