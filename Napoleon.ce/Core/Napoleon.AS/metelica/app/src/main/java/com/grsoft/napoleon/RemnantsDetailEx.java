package com.grsoft.napoleon;

import com.grsoft.dataobjects.RemnantItem;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.RemnantsImpl;
import com.grsoft.napoleon.documents.RemnantsDoc;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.EditText;

public class RemnantsDetailEx extends RemnantsDetail implements ScannerHelper.DocUpdated {
	protected static final int EDIT_REMARK = 7;
	EditText edRemark;

	ScannerHelper helper;
	String barCode = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		helper = new ScannerHelper(remnantsImpl, this);
	
//		lvRemnantItems.setOnKeyListener(new View.OnKeyListener() {
//			@Override
//			public boolean onKey(View v, int keyCode, KeyEvent event) {
//				if( event.getKeyCode() == KeyEvent.KEYCODE_ENTER)
//					helper.onKeyDown(event);
//				return false;
//			}
//		});

		findViewById(R.id.btnRemark).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { showDialog(EDIT_REMARK); }
		});
	}
	
	@Override protected int getLayoutId() { return R.layout.remnants_detail_ex; }
	
	@Override
	protected void onResume() {
		super.onResume();
		lvRemnantItems.clearFocus();
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
	    try {
	        if(event.getAction()== KeyEvent.ACTION_UP) {
	        	helper.onKeyDown(event);
	        	if(event.getKeyCode() == KeyEvent.KEYCODE_ENTER)
	        		return true;
	        }
	    }
	    catch (Exception ex) {
	        ex.printStackTrace();
	    }
	    return super.dispatchKeyEvent(event);
	}
	
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//    	helper.onKeyDown(event);
//		return super.onKeyDown(keyCode, event);
//	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if( helper != null )
			helper.close();
	}	
	
	@Override
	public void updated(RemnantsImpl doc, PriceImpl p) {
		((BaseAdapter)lvRemnantItems.getAdapter()).notifyDataSetChanged();
		lvRemnantItems.clearFocus();

		int pos = 0;
		for(RemnantItem ri : remnantsImpl.getData().items) {
			if(ri.id.equals(p.getData().id)) {
				lvRemnantItems.requestFocusFromTouch();
				lvRemnantItems.setSelection(pos);
				break;
			}
			pos++;
		}
		
		RemnantsDoc.instance().refreshDocSum(doc.getId());
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == EDIT_REMARK) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("Введите комментарий по заявке");
			View v = View.inflate(this, R.layout.rest_out_remark, null);
			edRemark = (EditText) v.findViewById(R.id.edRemark);
			b.setView(v);
			
			b.setNegativeButton(android.R.string.cancel, null);
			b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					if(remnantsImpl.isEditable()) {
						remnantsImpl.getData().remark = edRemark.getText().toString();
						remnantsImpl.write();
					}
				}
			});
			return b.create();
		}

		if(id == R.id.no_bc_id) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("Но ШК");
			b.setMessage("Нет ШК");
			b.setPositiveButton(android.R.string.ok, null);
			return b.create();
		}
		return super.onCreateDialog(id);
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if(id == EDIT_REMARK) {
			edRemark.setText(remnantsImpl.getData().remark);
			return;
		}
		if(id == R.id.no_bc_id) {
			((AlertDialog)dialog).setMessage("Нет ШК :" + barCode);
			return;
		}
		super.onPrepareDialog(id, dialog);
	}
	
	@Override
	public void notUpdated(String barcode) {
		lvRemnantItems.clearFocus();
		barCode = barcode;
		showDialog(R.id.no_bc_id);
	}
}
