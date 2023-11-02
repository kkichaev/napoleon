package com.grsoft.napoleon;

import com.grsoft.database.Hitching;
import com.grsoft.database.ReportHitching;
import com.grsoft.dataobjects.ClientCard;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.impl.OrgDataImpl;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.UpdateProcess;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class DocumentsEx extends Documents {
	private View btnNote;
	private View btnClientCard;
	private View btnOrgData;
	private OrgDataImpl orgData = new OrgDataImpl();
	
	@Override protected void onlyVisitInit() {}
	@Override protected int getContentViewID() { return R.layout.documentsex; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		btnNote = findViewById(R.id.btnNote);
		btnClientCard = findViewById(R.id.btnClientCard);
		btnOrgData = findViewById(R.id.btnOrgData);
		
		btnNote.setOnClickListener(noteClick);
		btnClientCard.setOnClickListener(clientCardClick);
		btnOrgData.setOnClickListener(orgDataClick);
		
		orgData.read("id", org.getData().id);
	}
	
	private OnClickListener noteClick = new OnClickListener() {
		@Override public void onClick(View v) { showDialog(NOTES_DLG_ID);}};
		
		private OnClickListener orgDataClick = new OnClickListener() {
			@Override public void onClick(View v) { showDialog(R.id.org_data_dlg);}};	
		
	private OnClickListener clientCardClick = new OnClickListener() {
		@Override public void onClick(View v) {
			UpdateProcess.Params params = new UpdateProcess.Params();
			Config config = ConfigManager.getConfig();
			params.ip1 = config.address;
			params.ip2 = config.address2;
			params.port1 = config.port;
			params.port2 = config.port2;
			params.login = config.login;
			params.pass = config.passw;
			
			Param p = new Param();
			p.id = org.getData().id;
			ReportHitching rp = new ReportHitching("client_card_html", p, new Hitching(ClientCard.class, "Result"));
			params.indata.add(rp);
			
			UpdateProcess updater = new UpdateProcess(v.getContext()){
				protected void onPreExecute() {
					showDialog(R.id.wait_dlg);
				};
				
				@Override protected void onPostExecute(Boolean result) { 
					dismissDialog(R.id.wait_dlg);
					ClientCardView.open(DocumentsEx.this, org.getData().id);
				}
			};
			
			updater.execute(params);
		}};
		
	private DialogInterface.OnClickListener applyOrgData = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			Dialog v = (Dialog) dialog;
			
			EditText ed = (EditText) v.findViewById(R.id.edBefvisit);
			orgData.getData().befvisit = ed.getText().toString().trim();
			
			ed = (EditText) v.findViewById(R.id.edConcurents);
			orgData.getData().concurents = ed.getText().toString().trim();
			
			orgData.write();
			orgData.close();
		}
	};
		
	static class Param extends DataObject{
		public String id = ""; 
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == R.id.wait_dlg)
			return createWaitDlg();
		else if (id == R.id.org_data_dlg)
			return createOrgDataDlg();
		else
			return super.onCreateDialog(id);
	}
	
	private Dialog createOrgDataDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(View.inflate(this, R.layout.org_data_dlg, null));
		builder.setPositiveButton(R.string.ok, applyOrgData );
		builder.setNegativeButton(R.string.cancel, null);
		return builder.create();
	}
	
	private Dialog createWaitDlg() {
		ProgressDialog dlg = new ProgressDialog(this);
		dlg.setMessage(getString(R.string.please_wait));
		return dlg;
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if(id == R.id.org_data_dlg){
			prepareOrgDataDlg(dialog);
		}else
			super.onPrepareDialog(id, dialog);
	}
	
	private void prepareOrgDataDlg(Dialog dialog) {
		EditText ed = (EditText) dialog.findViewById(R.id.edBefvisit);
		ed.setText(orgData.getData().befvisit);
		
		ed = (EditText) dialog.findViewById(R.id.edConcurents);
		ed.setText(orgData.getData().concurents);
	}
}
