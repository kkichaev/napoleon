package com.grsoft.napoleon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.grsoft.dataobjects.CreateDocDataObject;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgSum;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.PKO1cDoc;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.DocExportListener;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.Util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class MainEx extends Main {

	protected int STOP_DLG = 1000;
	protected String alertMessage = "";
	int orgClickPos = -1;
	Org clickOrg = null;

	HashSet<String> avansOrgs = new HashSet<String>();
	
	private PricePrintHelper pph;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		pph = new PricePrintHelper(this); 
	}
	
	public static void open(Context context){
		Intent intent = new Intent(context, MainEx.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		context.startActivity(intent);
	}
	
	@Override
	protected ArrayList<MenuHandler> createMainMenuList() {
		ArrayList<MenuHandler> ret = super.createMainMenuList();
		int pos = ret.size() - 3;
		if(pos < 0)
			pos = 0;
		
		ret.add(pos, pph.getMenuHandler());
		return ret;
	}
		
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if (id == R.id.chooseorgdlg)
			pph.updateOrgList(dialog);
		else if( id == STOP_DLG ) {
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

		CfgNplEx cfg = (CfgNplEx) ConfigManager.getConfig();
		
		if(cfg.simpleMode){
			SimpleMode.open(this);
			DocType.setCurDoc(SalesDoc.instance());
		}

		List<DocExportListener> docs =  DocType.getDocuments(true, true);

		for(DocExportListener lis : docs){
			for(Document d : lis.getDocuments()){
				if (d instanceof CreatableDocument){
					CreatableDocument cd = (CreatableDocument)d;

					if (((CreateDocDataObject)cd.getData()).created.getTime() < Util.resetTime(new Date()).getTime())
						showDialog(R.id.ask_to_send_old_docs_dlg);
				}
			}
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == R.id.ask_to_send_old_docs_dlg)
			return createAskToSendOldOdcsDlg();
		else if(id == R.id.chooseorgdlg)
			return pph.createrOrgSelector();
		else if( id == R.id.wait_for_print_dlg)
			return SelectPrinFormDlg.createWaitDlg(this);
		else if( id == STOP_DLG ) {
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

	private Dialog createAskToSendOldOdcsDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.warning);
		builder.setMessage(R.string.send_old_docs);
		builder.setPositiveButton(R.string.ok, null);
		builder.setNegativeButton(R.string.send, (d,w)->UpdateDB.open(this));
		return builder.create();
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
