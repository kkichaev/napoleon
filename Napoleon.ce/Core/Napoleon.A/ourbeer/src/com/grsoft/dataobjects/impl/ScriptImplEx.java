package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.OrgEx;
import com.grsoft.script.dataobjects.ScriptDef;
import com.grsoft.script.dataobjects.ScriptDefItem;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.util.GpsCoord;

import android.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.text.Html;


public class ScriptImplEx extends ScriptImpl {

	@Override
	protected boolean initInternal(Context c, String orgId, GpsCoord gpsCoord, ScriptDef srciptDef) {
		if (hasOrder(srciptDef)) {
			OrgImpl org = new OrgImpl();
			org.read("id", orgId);
			
			if(((OrgEx)org.getData()).blockupd.length() > 0) {
				showBlockUpdDlg(c, ((OrgEx)org.getData()).blockupd);
				return false;
			}else if (((OrgEx)org.getData()).stopupd.length() > 0) {
				showStopUpdDlg(c, ((OrgEx)org.getData()).stopupd, orgId, gpsCoord, srciptDef);
				return false;
			}
		}
		
		return super.initInternal(c, orgId, gpsCoord, srciptDef);
	}

	private void showStopUpdDlg(final Context context, String stopupd, final String orgId, final GpsCoord gpsCoord, final ScriptDef srciptDef) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Блок УПД");
		StringBuilder sb = new StringBuilder();
		sb.append("По данному клиенту есть неподписанные документы в системе ЕГАИС:<br>");
		sb.append(splitByComma(stopupd));
		builder.setMessage(Html.fromHtml(sb.toString()));
		builder.setPositiveButton(R.string.ok, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				superInitInternal(context, orgId, gpsCoord, srciptDef);
			}
		});
		builder.create().show();
	}

	protected void superInitInternal(Context context, String orgId, GpsCoord gpsCoord, ScriptDef srciptDef) {
		super.initInternal(context, orgId, gpsCoord, srciptDef);
	}

	private void showBlockUpdDlg(Context context, String blockupd) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Блок УПД");
		StringBuilder sb = new StringBuilder();
		sb.append("По данному клиенту есть неподписанные документы в системе ЕГАИС, Заказ создать невозможно:<br>");
		sb.append(splitByComma(blockupd));
		builder.setMessage(Html.fromHtml(sb.toString()));
		builder.setPositiveButton(R.string.ok, null);
		builder.create().show();
	}

	private String splitByComma(String blockupd) {
		String arr[] = blockupd.split(",");
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i < arr.length; i++)
			sb.append(arr[i]).append("<br>");
		
		return sb.toString();
	}

	private boolean hasOrder(ScriptDef srciptDef) {
		for(ScriptDefItem i : srciptDef.items)
			if (i.curType.equals("Order"))
				return true;
		
		return false;
	}

}

