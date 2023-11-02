package com.grsoft.napoleon;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase.UpdateQtyHandler;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;

public class PriceCountEx extends PriceCount {
	protected static final int MIN_QTY_WARNING_DLG = R.id.min_qty_warning_dlg;
	protected OnClickListener baseClickListener;
	private ArrayList<KeyValue> priceType = new ArrayList<KeyValue>();
	private int sumType;
	private Spinner spPrices;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		spPrices = (Spinner) findViewById(R.id.spPrices);

		btnOK.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PriceEx p = (PriceEx) price.getData();

				if (p.qty > 0 && qtyItems > 0 && p.minqty > qtyItems)
					showDialog(MIN_QTY_WARNING_DLG);
				else
					new BtnOkR().run();
			}
		});

		if (document instanceof OrderImpl) {
			OrderImpl orderImpl = ((OrderImpl) document);
			orderImpl.setUpdateQtyHandler(new UpdateQtyHandler() {

				@Override
				public void itemUpdated(OrderItem item, Order order,
						boolean isNewItem) {
					KeyValue kv = (KeyValue) spPrices.getSelectedItem();
					
					if(kv != null)
						((OrderItemEx)item).priceType = kv.key.toString();
				}
			});
			
			OrderEx o = (OrderEx) orderImpl.getData();
			OrderItemEx item = (OrderItemEx)orderImpl.findItem(price.getData().id);
			String pt = o.priceType;
			
			if(item != null)
				pt = item.priceType;
			
			spPrices.setVisibility(View.VISIBLE);
			ConfigImpl config = new ConfigImpl();
			
			
			DialogHelper.loadSpinnerWithKey(config, "“ип÷ены", priceType,
					spPrices, pt);
			spPrices.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					sumType = position;
					Price p = price.getData();
					priceVal = (p.cost != null && p.cost.size() > sumType && sumType >= 0) ? p.cost
							.get(sumType).cost : 0;
					updateCost();
					updateSumTextView();
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
				}
			});
		}
	}

	@Override
	protected int getContentViewId() {
		return R.layout.pricecountex;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case MIN_QTY_WARNING_DLG:
			return createMinQtyWarningDlg();
		default:
			return super.onCreateDialog(id);
		}
	}

	private Dialog createMinQtyWarningDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.alert);
		builder.setMessage("");
		builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						new BtnOkR().run();
						finish();
					}
				});
		builder.setNegativeButton(R.string.cancel, null);
		return builder.create();
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case MIN_QTY_WARNING_DLG:
			prepareMinQtyWarningDlg(dialog);
			break;
		default:
			super.onPrepareDialog(id, dialog);
		}
	}

	private void prepareMinQtyWarningDlg(Dialog dialog) {
		((AlertDialog) dialog).setMessage(getResources().getString(
				R.string.min_qty_warning,
				Util.IntToScaleStr(((PriceEx) price.getData()).minqty,
						Consts.QTY_SCALE)));
	}
}
