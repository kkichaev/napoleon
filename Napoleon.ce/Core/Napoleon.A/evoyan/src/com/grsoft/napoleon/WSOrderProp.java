package com.grsoft.napoleon;

import com.grsoft.dataobjects.FirmEx;
import com.grsoft.dataobjects.WSOrder;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.WSOrderImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.WSOrderDoc;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Spinner;

public class WSOrderProp extends Activity {
	private WSOrderImpl order = new WSOrderImpl();
	private boolean editMode = false;

	public static void open(Context context, OrderImplBase<?> order, boolean editOldOrder) {
		Intent i = new Intent(context, WSOrderProp.class);
		
		i.putExtra(ExtrasConst.EDIT_MODE_STR, editOldOrder);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, order.getRowid());

		context.startActivity(i);		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wsorder_prop);
		init();
	}
	
	private void init() {
		editMode = getIntent().getBooleanExtra(ExtrasConst.EDIT_MODE_STR, true);
		long orderRowId = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
				
		order.read(orderRowId);
		final WSOrder o = (WSOrder) order.getData();
		
		Spinner sp;
		sp = (Spinner)findViewById(R.id.spFirma);
		DialogHelper.loadSpinnerFromDataObject(sp, FirmEx.class, new DialogHelper.Selected<FirmEx>() {
			@Override
			public boolean isSelected(FirmEx object) {
				return o.firmCode.equals(object.id);
			}
		}, false, "name");
		
		View btnOK = findViewById(R.id.btnOK);
		btnOK.setEnabled(order.isEditable());
		btnOK.setOnClickListener(new OKClickListener());

        findViewById(R.id.btnCancel).setOnClickListener(new CancelClickListener());
	}
	
	
	@Override
	protected void onStop() {
		order.close();
		super.onStop();
	}

	class CancelClickListener extends OnClickListenerToNotify {
		@Override
		public void onClick(View v) {
			super.onClick(v);
			deleteEmptyOrder();			
			finish();
		}
	}
	
	private void deleteEmptyOrder() {
		if(!editMode) {
			if( order.getData().items == null || order.getData().items.size() == 0 )
				order.delete();
		}
	}
	
	class OKClickListener extends OnClickListenerToNotify {
		@Override
		public void onClick(View v) {
			super.onClick(v);
			
			okDone(false);
		}
		
		private void okDone(boolean updateSumType) {
			WSOrder o = (WSOrder) order.getData();
			
			Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
			FirmEx sel = (FirmEx) spFirma.getSelectedItem();
			if(sel != null) {
				o.firmCode = sel.id;
			}
			order.write();
			
			if(!editMode) {
				DocType.setCurDoc(WSOrderDoc.instance());
				Warehouse.open(WSOrderProp.this, order, false);
			}
			
			finish();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_BACK){
				deleteEmptyOrder();
				finish();
				return true;
			}else
				return super.onKeyDown(keyCode, event);
	}}
