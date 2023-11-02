package com.grsoft.napoleon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.View;

import com.grsoft.dataobjects.PriceEx;
import com.grsoft.napoleon.R;

public class PriceCountEx extends PriceCount {
	
	@Override protected int getContentViewId() { return R.layout.pricecountex; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		findViewById(R.id.ivPriceInfo).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				if(((PriceEx)price.getData()).info.length() > 0)
					showDialog(R.id.price_info);
			}
		});
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if( id == R.id.price_info ) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("Описание товара");
			b.setMessage("");
			b.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				@Override public void onClick(DialogInterface arg0, int arg1) { arg0.dismiss(); }
			});
			return b.create();
		}
		
		return super.onCreateDialog(id);
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if( id == R.id.price_info) {
			((AlertDialog)dialog).setMessage(Html.fromHtml(((PriceEx)price.getData()).info));
		} else 
			super.onPrepareDialog(id, dialog);
	}
}
