package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.android.calculator2.Calculator;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.ReturnItemEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.napoleon.documents.DeliveryDoc;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.view.dialog_helper.DialogHelper;

public class ReturnPriceCount extends PriceCount implements com.grsoft.dataobjects.impl.OrderImplBase.UpdateQtyHandler {
	static BroadcastReceiver calcResult;

	@Override protected int getContentViewId() { return R.layout.return_pricecount; }
	@Override protected int getStartValue() { return 0; }
	@Override protected boolean isComplexSalesHistory() { return false; }
	
	public static void open(Context context, long priceRoid, DbObject<? extends DataObject> doc) {
		Intent i = new Intent(context, ReturnPriceCount.class);
		
		i.putExtra(ExtrasConst.PRICE_ROW_ID_STR, priceRoid);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());

		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		edCount.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				Intent data = new Intent(v.getContext(), Calculator.class);
				String val = edCount.getText().toString();
				data.putExtra(Calculator.START_CALC_VAL, val);
				data.putExtra(Calculator.BROADCAST_RESULT, true);
				v.getContext().startActivity(data);
				return false;
			}
		});
	
		calcResult = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent != null){
					edCount.setText(intent.getStringExtra(Calculator.CALCULATOR_RESULT_VALUE));
					updateSumTextView();
				}
			}
		};
		
		((Spinner)findViewById(R.id.spDlv)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				DlvData dd = (DlvData) arg0.getItemAtPosition(arg2);
				onChangeCost(dd.cost);
			}

			@Override public void onNothingSelected(AdapterView<?> arg0) { }
		});
		
		registerReceiver(calcResult, new IntentFilter(Calculator.CALCULATOR_RESULT_ACTION));
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(calcResult);
		calcResult = null;
	}

	@Override
	protected void refreshData() {
		super.refreshData();

		Price p = price.getData();
		ReturnImplEx ret = (ReturnImplEx)document;
		ReturnItemEx ri = (ReturnItemEx)ret.findItem(p.id);
		
		ret.setUpdateQtyHandler(this);
				
		ConfigImpl ci = new ConfigImpl();
		Spinner sp = (Spinner)findViewById(R.id.spRetCause);
		DialogHelper.loadSpinnerFromConfig(ci, "ПричиныВозврата", new ArrayList<CharSequence>(), sp, (ri == null) ? "" : ri.cause);

		String docNumber = "";
		Date docDate = new Date();
		
		if( document != null ) {
			ReturnItemEx ritem = (ReturnItemEx)((ReturnImplEx)document).findItem(price.getData().id);
			if( ritem != null ) {
				docNumber = ritem.dlvNumber;
				docDate = ritem.dlvDate;
			}
		}
		int sel = -1;
		List<DlvData> items = new ArrayList<DlvData>();
		DocList dl = DeliveryDoc.instance().docList(document.getId(), "", "");
		for(Document<?> d : dl) {
			Delivery dlv = (Delivery) d.getData();
			for(DeliveryItem di : dlv.items) {
				if( di.id.compareTo(price.getData().id) != 0 )
					continue;
				
				if( dlv.number.equals(docNumber) && dlv.date.equals(docDate) )
					sel = items.size();
				
				items.add(new DlvData(dlv, (int)((long)di.sum * Consts.QTY_SCALE / di.qty)));
				break;
			}
		}
		dl.close();
		
		sp = (Spinner)findViewById(R.id.spDlv);
		ArrayAdapter<DlvData> aa = new ArrayAdapter<DlvData>(this, R.layout.simple_spinner_layout, items);
		aa.setDropDownViewResource(R.layout.simple_spinner_layout_drop_down);
		sp.setAdapter(aa);
		if( sel >= 0 && sel < sp.getCount())
			sp.setSelection(sel);
		
	}
	
	@Override
	public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
		ReturnItemEx ri = (ReturnItemEx)item;
		
		Spinner sp = (Spinner)findViewById(R.id.spRetCause);
		Object val = sp.getSelectedItem();
		if( val != null )
			ri.cause = val.toString();
		
		DlvData dd = (DlvData) ((Spinner)findViewById(R.id.spDlv)).getSelectedItem();
		if( dd != null ) {
			ri.dlvDate = dd.date;
			ri.dlvNumber = dd.number;
		}
	}
}

class DlvData
{
	public String number = "";
	public Date date = new Date();
	public int cost = 0;
	
	public DlvData(Delivery d, int cost) {
		date = d.date;
		number = d.number;
		this.cost = cost;
	}
	
	@Override
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		return "№ " + number + " от " + sdf.format(date);
	}
}
