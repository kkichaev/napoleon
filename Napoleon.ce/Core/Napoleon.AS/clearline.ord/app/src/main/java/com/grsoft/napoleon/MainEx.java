package com.grsoft.napoleon;

import java.util.HashSet;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgFolders;
import com.grsoft.dataobjects.OrgSum;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.PKO1cDoc;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.util.gps.GPSUtilNew;

public class MainEx extends Main {

	protected int STOP_DLG = 1000;
	protected String alertMessage = "";
	int orgClickPos = -1;
	Org clickOrg = null;

	HashSet<String> avansOrgs = new HashSet<String>();
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if( id == STOP_DLG ) {
			((AlertDialog)dialog).setMessage(alertMessage);
		} else
			super.onPrepareDialog(id, dialog);
	}
	
	@Override
	protected void onResume() {
		if(DocType.getCurDoc() == PKO1cDoc.instance()) {
			DocType ct = SalesDoc.instance();
			if( DocType.getDocType(ct.getObjectName()) == null )
				ct = OrderDoc.instance();
			DocType.setCurDoc(ct);
		}
		
		super.onResume();
		
		avansOrgs.clear();
		DataTraveler.travel(OrgSum.class, new DataTraveler.Travel<OrgSum>() {

			@Override
			public boolean travel(DataTraveler<OrgSum> item) {
				avansOrgs.add(item.data.id);
				return true;
			}
		}, "\"type\"='Долги' and sum < 0");
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if( id == STOP_DLG ) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Внимание");
			builder.setMessage("");
			builder.setPositiveButton("ОК", new DialogInterface.OnClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					MainEx.super.openOrg(clickOrg, orgClickPos);
					}
			});
			
			builder.setNegativeButton("Отменить", null);
			return builder.create();
		}
		return super.onCreateDialog(id);
	}

	@Override
	public void openOrg(Org org, int pos) {
		alertMessage = ((OrgEx)org).stopMsg;
		if( alertMessage.length() > 0 ) {
			clickOrg = org;
			orgClickPos = pos;
			showDialog(STOP_DLG);
		} else
			super.openOrg(org, pos);
	}

	@Override
	protected void drawOrg(Org oi, View view) {
		super.drawOrg(oi, view);
		((TextView)view.findViewById(R.id.tvOrgName)).setBackgroundColor(avansOrgs.contains(oi.id) ? Color.LTGRAY : Color.TRANSPARENT);
	}
	
	@Override
	protected int getStopBkg() { return R.drawable.list_stop_selector;	}
}
