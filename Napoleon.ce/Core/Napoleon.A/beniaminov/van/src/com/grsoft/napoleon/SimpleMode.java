package com.grsoft.napoleon;

import java.util.Date;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.impl.SalesFake;
import com.grsoft.dataobjects.impl.SalesImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocType.CountTextResolver;
import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.ExtrasConst;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.IntentCompat;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;

public class SimpleMode extends PricePresentationFolder implements CountTextResolver, OnClickListener{
	protected final int DOC_SUM_DLG = 3;
	private SimpleModeHelper smHelper = new SimpleModeHelper(this);
	
	SalesImpl document;
	
	private View btnOK;
	private View btnCancel;
	
	public static void open(Context context){
		Intent intent = new Intent(context, SimpleMode.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK); 
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		btnOK = findViewById(R.id.btnOK);
		btnCancel = findViewById(R.id.btnCancel);
		
		btnOK.setOnClickListener(this);
		btnCancel.setOnClickListener(this);

		registerReceiver(reloadRcv, new IntentFilter(UpdateDBEx.RELOAD_ACTION));
	}
	
	BroadcastReceiver reloadRcv = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			PresentationFolderW.items.clear();
			PresentationFolderW.items.fill();
			adapter.notifyDataSetChanged();
		}
	};
	
	protected void makeSale() {
		if(document.getId().length() == 0)
			OrgList.open(this, document.getRowid());
		else{
			SalesDetailSM.open(this, document.getRowid());
		}
	}

	@Override protected Fragment createFragment() { return new SimpleModeFragment(); }
	
	@Override protected int getLayoutID() { return R.layout.simplemode; }
	
	@Override public boolean onCreateOptionsMenu(Menu menu) { return smHelper.onCreateOptionsMenu(menu); }
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean result = smHelper.onKeyDown(keyCode, event);
		
		if (!result)
			result = super.onKeyDown(keyCode, event);
		
		return result;
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		return smHelper.onCreateDialog(id);
	}
	
	public void updateTotalSum(long sum, int weight, int count) {
		DocType.getCurDoc().updateTotalSum(this, sum, weight, count);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		CfgNplEx cfg = (CfgNplEx) ConfigManager.getConfig();
		
		if(!cfg.simpleMode)
			resumeToExtendedMode();
		else
			resumeToSimpleMode();
	}

	private void resumeToExtendedMode() { NapoleonMain.open(this); }

	private void resumeToSimpleMode() {
		updateDocument();
		
		int pidx = pager.getCurrentItem();
		
		if(adapter.getCount() > pidx){
			SimpleModeFragment f = ((SimpleModeFragment)adapter.instantiateItem(pager, pidx));
			
			if(f != null)
				f.refresh();
		}
	}

	public void updateDocument(){
		document = SalesFake.getInstance(this);
		
		if(document.getRowid() != ExtrasConst.INVALID_ROWID){
			Date buddy = ((SalesEx)document.getData()).buddy;
			
			if(buddy != null && buddy.getTime() > 0)
				document.read(buddy.getTime(), false);
			
			document.close();
			
			int qty = document.qty();
			
			btnOK.setEnabled(qty > 0);
			btnCancel.setEnabled(qty > 0);
		}
		
		updateTotalSum(document.sum(), 0, document.countPack());
	}
	
	@Override public String getCountText() { return getString(R.string.pack_lbl); }

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.btnOK)
			makeSale();
		else if (id == R.id.btnCancel)
			cancelSale();
	}

	protected void cancelSale() {
		document.delete();
		document.close();
		document = SalesFake.getInstance(this, true);
		
		btnCancel.setEnabled(false);
		btnOK.setEnabled(false);
		updateViews();
	}

	protected void updateViews() {
		for(int i = 0; i < adapter.getCount(); i++){
			SimpleModeFragment f = (SimpleModeFragment)adapter.instantiateItem(pager, i);
			
			if (f != null)
			 f.refresh();
		}
		
		updateTotalSum(0, 0, 0);
	}
}
