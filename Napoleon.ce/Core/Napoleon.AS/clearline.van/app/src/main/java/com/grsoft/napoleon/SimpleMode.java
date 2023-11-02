package com.grsoft.napoleon;

import java.util.Date;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.impl.SalesFake;
import com.grsoft.napoleon.FoldersViewAdapter.FData;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocType.CountTextResolver;
import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.ExtrasConst;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;


public class SimpleMode extends Activity implements OnItemClickListener, OnClickListener, CountTextResolver{
	private GridView grid;
	private SimpleModeHelper smHelper = new SimpleModeHelper(this);
	private View btnOK;
	private View btnCancel;
	private FoldersViewAdapter adapter;
	
	public static void open(Context context){
		Intent i = new Intent(context, SimpleMode.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simplemode);
		
		grid = (GridView) findViewById(R.id.grid);
		btnOK = findViewById(R.id.btnOK);
		btnCancel = findViewById(R.id.btnCancel);
		
		btnOK.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		adapter = new FoldersViewAdapter(this, getImageWidth());
		grid.setAdapter(adapter);
		
		grid.setOnItemClickListener(this);
		CfgNplEx cfg = (CfgNplEx) ConfigManager.getConfig();
		grid.setNumColumns(cfg.simpleModeColumns);
		
		registerReceiver(reloadRcv, new IntentFilter(UpdateDBEx.RELOAD_ACTION));
	}
	
	int getImageWidth() {
		CfgNplEx cfg = (CfgNplEx) ConfigManager.getConfig();
		return getWindowManager().getDefaultDisplay().getWidth() / cfg.simpleModeColumns - 4 * (cfg.simpleModeColumns - 1); 
	}
	
	void updateLayout() {
		CfgNplEx cfg = (CfgNplEx) ConfigManager.getConfig();
		grid.setNumColumns(cfg.simpleModeColumns);
		adapter.reload(getImageWidth());
		adapter.notifyDataSetChanged();		
	}
	
	public void onConfigurationChanged(android.content.res.Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		updateLayout();
	}

	BroadcastReceiver reloadRcv = new BroadcastReceiver() {
		@Override public void onReceive(Context context, Intent intent) { updateLayout(); }
	};
	
	@Override
	public void onItemClick(AdapterView<?> adapter, View arg1, int pos, long arg3) {
		FoldersViewAdapter.FData f = (FData) adapter.getItemAtPosition(pos);
		PriceView.open(this, f.id);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean result = smHelper.onKeyDown(keyCode, event);
		
		if (!result)
			result = super.onKeyDown(keyCode, event);
		
		return result;
	}
	
	@Override public boolean onCreateOptionsMenu(Menu menu) { return smHelper.onCreateOptionsMenu(menu); }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return smHelper.onOptionsItemSelected(item);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		return smHelper.onCreateDialog(id);
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		smHelper.onPrepareDialog(id, dialog);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.btnOK)
			makeSale();
		else if (id == R.id.btnCancel)
			cancelSale();
	}
	
	protected void makeSale() {
		if(PriceView.document.getId().length() == 0)
			OrgList.open(this, PriceView.document.getRowid());
		else{
			SalesDetailSM.open(this, PriceView.document.getRowid());
		}
	}

	@Override public String getCountText() { return getString(R.string.pack_lbl); }
	
	protected void cancelSale() {
		PriceView.document.delete();
		PriceView.document.close();
		PriceView.document = SalesFake.getInstance(this, true);
		
		btnCancel.setEnabled(false);
		btnOK.setEnabled(false);
		updateTotalSum(0, 0, 0);
	}
	
	public void updateTotalSum(long sum, int weight, int count) {
		DocType.getCurDoc().updateTotalSum(this, sum, weight, count);
	}
	
	protected void onResume() {
		super.onResume();
		
		CfgNplEx cfg = (CfgNplEx) ConfigManager.getConfig();
		
		if(!cfg.simpleMode)
			resumeToExtendedMode();
		else
			resumeToSimpleMode();
	}
	
	private void resumeToExtendedMode() { MainEx.open(this); }
	
	private void resumeToSimpleMode() { updateDocument(); }
	
	public void updateDocument(){
		PriceView.document = SalesFake.getInstance(this);
		
		if(PriceView.document.getRowid() != ExtrasConst.INVALID_ROWID){
			Date buddy = ((SalesEx)PriceView.document.getData()).buddy;
			
			if(buddy != null && buddy.getTime() > 0)
				PriceView.document.read(buddy.getTime(), false);
			
			PriceView.document.close();
			
			int qty = PriceView.document.qty();
			
			btnOK.setEnabled(qty > 0);
			btnCancel.setEnabled(qty > 0);
		}
		
		updateTotalSum(PriceView.document.sum(), 0, PriceView.document.countPack());
	}
}
