package com.grsoft.napoleon;

import java.util.HashSet;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgSum;
import com.grsoft.dataobjects.impl.OrgImpl;

public class NapoleonEx extends Napoleon {

	private int STOP_DLG = 1000;
	private String alertMessage = "";
	OrgClick orgClick = new OrgClick();
	
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
				public void onClick(DialogInterface dialog, int which) { orgClick.resumeClick(); }
			});
			
			builder.setNegativeButton("Отменить", null);
			return builder.create();
		}
		return super.onCreateDialog(id);
	}
	
	protected String getOrgReadingFields() { return "name,id,address,color,flags,stopMsg"; }
	
	@Override protected OnItemClickListener getItemOnClickListner() { return orgClick; }

	class OrgClick extends OrglListOnClickListener {
		@Override
		protected void openOrg(OrgImpl oi) {
			clickedOrg = oi;
			alertMessage = ((OrgEx)oi.getData()).stopMsg;
			if( alertMessage.length() > 0 ) {
				showDialog(STOP_DLG);
			} else
				super.openOrg(oi);
		}
	}
	
	@Override
	protected void drawOrg(OrgImpl oi, View view) {
		super.drawOrg(oi, view);

		((TextView)view.findViewById(R.id.tvOrgName)).setBackgroundColor(avansOrgs.contains(oi.getData().id) ? Color.LTGRAY : Color.TRANSPARENT);
	}
	
	@Override
	protected int getStopBkg() { return R.drawable.list_stop_selector;	}
}
