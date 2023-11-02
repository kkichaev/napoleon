package com.grsoft.napoleon.documents;

import java.util.Date;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.widget.Adapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.Payment;
import com.grsoft.napoleon.DebtDocAdapter;
import com.grsoft.napoleon.R;
import com.grsoft.util.Consts;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.Util;

public class DebtDocEx extends DebtDoc {
	
	public static void initialize() {
		if( instance != null )
			throw new RuntimeException("DebtDoc уже создан!");
		instance = new DebtDocEx(DOC_NAME, Debt.class);
	}
	
	protected DebtDocEx(String name, Class<? extends Document<?>> docClass) {
		super(name, docClass);
	}

	@Override
	public void viewClosed(Activity documentsView) {
		TextView tv;
		tv = (TextView)documentsView.findViewById(R.id.DateTitle);
		if( tv != null )
			tv.setText("Дата");
		
		documentsView.findViewById(R.id.llGridTitle).setVisibility(View.VISIBLE);
		documentsView.findViewById(R.id.llDebtGridTitle).setVisibility(View.GONE);
		documentsView.findViewById(R.id.btnStart).setVisibility(View.GONE);
	}

	@Override
	public void viewOpened(Activity documentsView) {
		TextView tv;
		tv = (TextView)documentsView.findViewById(R.id.DateTitle);
		if( tv != null )
			tv.setText("Дата/Оплата");
		
		documentsView.findViewById(R.id.llGridTitle).setVisibility(View.GONE);
		documentsView.findViewById(R.id.llDebtGridTitle).setVisibility(View.VISIBLE);
		documentsView.findViewById(R.id.btnStart).setVisibility(View.VISIBLE);
	}
	
	@Override
	public void setView(Adapter adapter, View view, Document<?> doc) {
		DataObject dobj = doc.getData();
		if( dobj instanceof DeliveryEx )
			setViewDelivery((DeliveryEx)dobj, view, adapter);
		else if(dobj instanceof Payment)
			setViewPayment((Payment) dobj, view);
	}

	private void setViewDelivery(final DeliveryEx d, View view, final Adapter adapter) {
		TextView tv;
		
		Date now = Util.getDate();
		tv = (TextView)view.findViewById(R.id.tvDay);
		tv.setText(Long.toString(DatePeriod.daysDiff(now, d.payDate)));
		
		tv = (TextView) view.findViewById(R.id.tvDelay);
		tv.setText(Long.toString(DatePeriod.daysDiff(d.date, d.payDate)));
		
		tv = (TextView) view.findViewById(R.id.tvNumber);
		tv.setText(d.number);
		
		tv = (TextView)view.findViewById(R.id.tvDate);
		tv.setText( Util.simpleDateFormat.format(d.date));
		
		tv = (TextView)view.findViewById(R.id.tvSum);
		tv.setText(Util.IntToScaleStr(d.sum(), Consts.SUM_SCALE));
					
		String sumdtext = Util.IntToScaleStr(d.sumD, Consts.SUM_SCALE);
		tv = (TextView)view.findViewById(R.id.tvDebt);
		tv.setText(sumdtext);
		
		tv = (TextView)view.findViewById(R.id.tvExceed);
		tv.setText( (d.sumD > 0 && d.payDate.compareTo(Util.getDate()) < 0) ? sumdtext : "");
		tv.setTextColor(Color.RED);
		
		CheckBox cbSel = (CheckBox) view.findViewById(R.id.cbSel);
		cbSel.setTag(d.clone());
		cbSel.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				DeliveryEx delex = (DeliveryEx) buttonView.getTag();
				((DebtDocAdapter)adapter).select((int)(isChecked ? delex.sumD : -delex.sumD));
			}
		});;
	}

	private void setViewPayment(Payment p, View view) {
		TextView tv;
		
		tv = (TextView)view.findViewById(R.id.tvDay);
		tv.setText("");
		
		tv = (TextView) view.findViewById(R.id.tvDelay);
		tv.setText("");
		
		tv = (TextView) view.findViewById(R.id.tvNumber);
		tv.setText("");
		
		tv = (TextView)view.findViewById(R.id.tvDate);
		tv.setText( Util.simpleDateFormat.format(p.date));
		
		tv = (TextView)view.findViewById(R.id.tvSum);
		tv.setText(Util.IntToScaleStr(p.sum, Consts.SUM_SCALE));
					
		tv = (TextView)view.findViewById(R.id.tvDebt);
		tv.setText("");
		
		tv = (TextView)view.findViewById(R.id.tvExceed);
		tv.setText("");
		
		CheckBox cbSel = (CheckBox) view.findViewById(R.id.cbSel);
		cbSel.setVisibility(View.INVISIBLE);
	}
}
