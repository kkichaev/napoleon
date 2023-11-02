package com.grsoft.napoleon.dostavka;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.DShipment;
import com.grsoft.dataobjects.DWaybillDocument;
import com.grsoft.dataobjects.DWaybillDocumentItem;
import com.grsoft.dataobjects.Dispatch;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.InputNumberDlg;
import com.grsoft.network.ObjectListener;
import com.grsoft.util.Consts;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;
import com.grsoft.util.gps.GPSUtilNew;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;
import com.grsoft.view.KeypadHelper;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class DShipmentEditEx extends DShipmentEdit implements IncompleteAction {
	
	@Override protected int getLayoutID() { return R.layout.dshipmenteditex;}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		findViewById(R.id.btnIncompletely).setOnClickListener(this);
	}
	
	@Override
	protected void changeItemQty(final DWaybillDocumentItem item) {
		final QtyDecorator decorator = new QtyDecorator(item);
		
		InputNumberDlg.open(DShipmentEditEx.this, new InputNumber() {
			
			@Override
			public boolean isValid(int value, Object... params) {
				if(value > item.inqty) {
					Toast.makeText(DShipmentEditEx.this, getString(R.string.qty_more_than_delivery), Toast.LENGTH_LONG).show();
					return true;
				}
				if(value< item.inqty && decorator.selectedValue().length() == 0) {
					Toast.makeText(DShipmentEditEx.this, getString(R.string.select_cause), Toast.LENGTH_LONG).show();
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
			public int getValue() {				
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
	
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnIncompletely)
			promptIncomplete();
		else
			super.onClick(v);
	}
	
	private void promptIncomplete() {
		new IncompleteDialog().show(getFragmentManager(), IncompleteDialog.class.toString());
		
	}

	@Override
	public void doIncomplete(String remark) {
		doc.getData().params |= Dispatch.USER_STATUS;
		doc.getData().remark = remark;
		onAccept();
	}
}
