package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.BarcodeImpl;
import com.grsoft.napoleon.R;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

public class BarcodeDoc extends DocType {

	static protected BarcodeDoc instance = null;
	
	static public DocType instance() {
		if( instance == null )
			instance = new BarcodeDoc();
		
		return instance;
	}
	
	protected BarcodeDoc() {
		super("Barcode", "Barcode", BarcodeImpl.class);
	}

	public void updateTotalSum(Activity activity, long sum, int weight, int count, int textViewId){
		TextView tvTotalSum = (TextView) activity.findViewById(textViewId);		
		if (tvTotalSum != null)
		{
			tvTotalSum.setVisibility(View.INVISIBLE);
		}
	}
	
	@Override
	public boolean outOfScript() {
		return true;
	}
	
	@Override
	public int getResurceId() {
		return R.drawable.barcode_doc;
	}
}
