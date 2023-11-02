package com.grsoft.napoleon.dostavka;

import com.grsoft.dataobjects.DWaybillDocumentItem;
import com.grsoft.dataobjects.Dispatch;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.DWaybillDocumentImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.InputNumberDlg;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;
import com.grsoft.view.KeypadHelper;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class DShipmentEdit extends DWaybillEdit implements OnClickListener, RejectAction {
	
	public static Class<? extends DShipmentEdit> activity = DShipmentEdit.class;
	
	public static void open(Context context, DWaybillDocumentImpl<?> doc){
		Intent intent = new Intent(context, activity);
		intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		intent.putExtra(DWaybillEdit.DOCTYPE, doc.getClass());
		context.startActivity(intent);
	}
	
	@Override protected int getLayoutID() { return R.layout.dshipmentedit;}

	@Override protected DialogFragment createItemEditDialog() { return new DWaybillItemEdit(); }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		View v = findViewById(R.id.btnOK);

		if (v != null)
			v.setOnClickListener(this);

		v = findViewById(R.id.btnReject);

		if (v != null)
			v.setOnClickListener(this);

		v = findViewById(R.id.btnIncompletely);

		if (v  != null)
			v.setOnClickListener(this);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		updateDocOnBack();
	}

	public void updateDocOnBack() {
		if(!doc.isDirty()) {
			doc.delete();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		doc.close();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnOK) {
			onAccept();
		}else if (v.getId() == R.id.btnReject) { 
			onReject();
		} else if (v.getId() == R.id.btnIncompletely)
			incompletely();
	}

	private void incompletely() {
		doc.getData().params |= Dispatch.INCOMPLEETE;
		onAccept();
	}
	protected void onAccept() {
		doc.setReadyToSend();
		doc.write();
		finish();
	}
	
	protected void onReject() {
		new RejectDialog().show(getFragmentManager(), RejectDialog.class.toString());
	}

	
	@Override
	public void doReject(String remark) {
		doc.getData().remark = remark;
		doc.setRejected();
		doc.write();
	}

	@Override
	protected void changeItemQty(final DWaybillDocumentItem item) {
		final QtyDecorator decorator = new QtyDecorator(item);

		InputNumberDlg.open(DShipmentEdit.this, new InputNumber() {

			@Override
			public boolean isValid(int value, Object... params) {
				if(value > item.inqty) {
					Toast.makeText(DShipmentEdit.this, getString(R.string.qty_more_than_delivery), Toast.LENGTH_LONG).show();
					return true;
				}
				if(value< item.inqty && decorator.selectedValue().length() == 0) {
					Toast.makeText(DShipmentEdit.this, getString(R.string.select_cause), Toast.LENGTH_LONG).show();
					return false;
				}
				return super.isValid(value, params);
			}

			@Override
			public void applayInput(int value, Object... params) {
				if(value >= item.inqty) {
					item.outqty = value;
					adapter.notifyDataSetChanged();
					return;
				}
				if(value < item.inqty) {
					item.outqty = value;
					item.cause = item.outqty == item.inqty ? "" : decorator.selectedValue();
					adapter.notifyDataSetChanged();
				}
			}

			@Override
			public long getValue() {
				return item == null ? 0 : item.outqty;
			}
		}, Consts.QTY_SCALE, true, "Ввести количество принятого товара", false, decorator);
	}

	class QtyDecorator implements InputNumberDlg.Decorator {
		DWaybillDocumentItem item;
		Spinner causeSp;

		public QtyDecorator(DWaybillDocumentItem item) {
			this.item = item;
		}


		public String selectedValue() {
			KeyValue kv = (KeyValue) causeSp.getSelectedItem();
			return kv == null ? "" : kv.key.toString();
		}

		@Override
		public void adjustView(AlertDialog dialog, View view, KeypadHelper nh) {
			causeSp = (Spinner)view.findViewById(R.id.spRetCause);
			ConfigImpl ci = new ConfigImpl();
			DialogHelper.loadSpinnerWithKeyW(ci, "ПричиныВозвратовДоставка", new ArrayList<KeyValue>(), causeSp, item.cause, true);
			ci.close();

			PriceImpl price = new PriceImpl();

			price.read("id", item.id);
			TextView tv = (TextView) view.findViewById(R.id.tvDiscountInfo);
			tv.setVisibility(View.VISIBLE);
			tv.setText(price.getData().name);

			tv = (TextView) view.findViewById(R.id.tvBeforeQty);
			tv.setText(Util.IntToScaleStr(item.inqty, Consts.QTY_SCALE));
		}


		@Override public int getContentView() { return R.layout.input_wb_qty; }
	}
}
