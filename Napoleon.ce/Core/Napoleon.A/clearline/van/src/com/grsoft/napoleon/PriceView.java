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
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;

public class PriceView extends PricePresentationFolder implements CountTextResolver, OnClickListener{
	protected final int DOC_SUM_DLG = 3;
	
	private static final String FOLDERID = "folderid";
	public static SalesImpl document;
	
	private View btnOK;
	private View btnCancel;
	private int fid = -1;
	
	public static void open(Context context, int folderid){
		Intent intent = new Intent(context, PriceView.class);
		intent.putExtra(FOLDERID, folderid);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		btnOK = findViewById(R.id.btnOK);
		btnCancel = findViewById(R.id.btnCancel);
		
		btnOK.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
	}
	
	protected void makeSale() {
		if(document.getId().length() == 0)
			OrgList.open(this, document.getRowid());
		else{
			SalesDetailSM.open(this, document.getRowid());
		}
	}

	@Override protected Fragment createFragment() {	return new PriceViewFragment();}
	
	@Override protected int getLayoutID() { return R.layout.priceview; }
	
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
			PriceViewFragment f = ((PriceViewFragment)adapter.instantiateItem(pager, pidx));
			
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
			PriceViewFragment f = (PriceViewFragment)adapter.instantiateItem(pager, i);
			
			if (f != null)
			 f.refresh();
		}
		
		updateTotalSum(0, 0, 0);
	}
	
	@Override
	protected void initPresentList() {
		PresentationFolderW.items.fill(false);
		fid = getIntent().getIntExtra(FOLDERID, -1);
		PresentationList result = new PresentationList();
		for(PresentationData d : PresentationFolderW.items )
			if(fid == -1 || d.folder == fid)
				result.add(d);
		
		list = result;
	}
}
