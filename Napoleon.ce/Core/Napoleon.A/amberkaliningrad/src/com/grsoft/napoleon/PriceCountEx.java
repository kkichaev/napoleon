package com.grsoft.napoleon;

import com.grsoft.dataobjects.AgentProcent;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;
import com.grsoft.view.KeypadHelper;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

public class PriceCountEx extends PriceCount {
	private View ivInfo;
//	private boolean integersInputValue = Features.INTEGER_INPUTS_QTY;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		CheckBox cb = (CheckBox)findViewById(R.id.cbKG);
		cb.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean checked) {
				updateCost();
				updateSumTextView();
			}
		});
		
		if( document instanceof OrderImplBase<?> )
			((OrderImplBase<?>)document).setUpdateQtyHandler(new OrderImplBase.UpdateQtyHandler() {

				@Override
				public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
					OrderItemEx oe = (OrderItemEx)item;
					EditText ed = (EditText)findViewById(R.id.edRemark);
					oe.remark = ed.getText().toString();
					ed = (EditText)findViewById(R.id.edWhRemark);
					oe.remarkWh = ed.getText().toString();
					oe.inKG = ((CheckBox)findViewById(R.id.cbKG)).isChecked() ? 1 : 0;
				}
			});
		
		ivInfo = findViewById(R.id.ivInfo);
		ivInfo.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { showDialog(R.id.price_descr_dlg); } });
	}

	void updatePrcText(long ordsum) {
		double prc = AgentProcent.getProcent() / (100.0 * Consts.SUM_SCALE);
		double sum = ordsum * prc;
		
		TextView tv = (TextView) findViewById(R.id.tvPrcInfo);
		if( sum == 0 )
			tv.setVisibility(View.GONE);
		else {
			tv.setVisibility(View.VISIBLE);
			String earn = getString(R.string.your_earn);
			String text = String.format(earn, Util.IntToScaleStr((long)(sum * Consts.SUM_SCALE), Consts.SUM_SCALE, Util.DEC_DELIM, false ));
			if( tv != null) {
				tv.setText(text);
				findViewById(R.id.llPrcInfo).setVisibility(View.VISIBLE);
			}
		}
	}
	@Override
	protected long getSum(int count) {
		long sum = super.getSum(count); 
		updatePrcText(sum);
		return sum;
	}
	
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == R.id.price_descr_dlg)
			return priceDescrDlg();
		else
			return super.onCreateDialog(id);
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if (id == R.id.price_descr_dlg)
			preparePriceDescrDlg(dialog);
		else
			super.onPrepareDialog(id, dialog);
	}
	private void preparePriceDescrDlg(Dialog dialog) {
		AlertDialog ad = (AlertDialog) dialog;
		ad.setMessage(((PriceEx)price.getData()).descr);
	}

	private Dialog priceDescrDlg() {
		AlertDialog.Builder result = new AlertDialog.Builder(this);
		result.setTitle(R.string.price_descr);
		result.setMessage("");
		result.setPositiveButton(R.string.ok, null);
		return result.create();
	}
	
	@Override
	protected int getInputCost(Price p) {
		int cost = super.getInputCost(p); 
		CheckBox cb = (CheckBox)findViewById(R.id.cbKG);
		if(cb.isChecked()) {
			double val = cost / ((double)(price.getData().weight) / Consts.WEIGHT_SCALE) + 0.5;
			cost = (int)val;
		}			
		return cost;
	}
	
	@Override
	protected int getContentViewId() {
		return R.layout.pricecountex;
	}
	
	@Override
	protected void refreshData() {
		super.refreshData();		

		if( document != null && document instanceof OrderImpl ) {
			OrderItemEx oe = (OrderItemEx)((OrderImpl)document).findItem(price.getData().id);
			if( oe != null ) {
				if ( oe.remark != null ) {
					EditText ed = (EditText)findViewById(R.id.edRemark);
					ed.setText(oe.remark);
				}
				
				if( oe.remarkWh != null ) {
					EditText ed = (EditText)findViewById(R.id.edWhRemark);
					ed.setText(oe.remarkWh);
				}
				CheckBox cb = (CheckBox)findViewById(R.id.cbKG);
				cb.setChecked( oe.inKG > 0 );
				updateCost();
				updateSumTextView();
			}
		}
		btnOK.setEnabled(priceVal > 0);
	}
	
	@Override protected KeypadHelper createKeypadHelper() { return new KeypadHelper(this, R.id.edCount, ((PriceEx)price.getData()).cantdiv > 0); }
}
