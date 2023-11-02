package com.grsoft.napoleon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.util.WorkTimeListener;

public class DocumentsEx extends DocumentsPrint {
	
	public static final int CONFIRM_STOP_TIMER = 50;
	WTLEx wtl;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		wtl = new WTLEx((NapoleonApp)getApplication(), org.getData().id, (ImageButton) findViewById(R.id.btnStart), btnNewDoc);
	}
	
	@Override
	protected int getContentViewID() {
		return R.layout.documentsex;
	}
		
	@Override
	public void onBackPressed() {
		if( wtl.isInWork() )
			return;
		super.onBackPressed();
	}
	
	@Override
	protected boolean canCreateDoc(DocType docType) {
		return wtl.isInWork() && super.canCreateDoc(docType);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if( id == CONFIRM_STOP_TIMER ) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("Потдверждение");
			b.setMessage("Завершить работу с точкой?");
			b.setNegativeButton(R.string.no, null);
			b.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					wtl.stopWork(findViewById(R.id.btnStart));
					arg0.dismiss();
					finish();
				}
			});
			return b.create();
		}
		return super.onCreateDialog(id);
	}
	
	class WTLEx extends WorkTimeListener {

		public WTLEx(NapoleonApp app, String id, ImageButton btnStart, View newDoc) {
			super(app, id, btnStart, newDoc);
		}
		
		public void stopWork(View v) {
			super.onClick(v);
		}
		
		@Override
		public void onClick(View v) {
			if( isInWork() ) {
				showDialog(CONFIRM_STOP_TIMER);
				return;
			}
			super.onClick(v);
		}
	}
}
