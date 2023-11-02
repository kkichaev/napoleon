package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.Accounts;
import com.grsoft.dataobjects.CostTypes;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceActions;
import com.grsoft.dataobjects.impl.AccountsImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.PriceActionImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDocEx;
import com.grsoft.napoleon.documents.SalesHistoryData;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.KeyValue;

public class PriceCountEx extends PriceCount {
	
	int discount = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		StripedLinearLayout sll = (StripedLinearLayout)findViewById(R.id.llSilesHistory);
		sll.paintOdd();
		
		Spinner sp = (Spinner)findViewById(R.id.spCostType);
		sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				KeyValue kv = (KeyValue)arg0.getSelectedItem();
				Price p = price.getData();
				
				OrderEx oe = ((OrderEx)document.getData());
				oe.taxType = kv.key.toString();
				
				@SuppressWarnings("unchecked")
				CostStrategy costStrategy = CostStrategy.getInstance((Class<? extends Document<?>>)document.getClass()); 
				int newCost = costStrategy.getItemCost(p, document);
				if( newCost != priceVal )
					onChangeCost(newCost);
			}

			@Override public void onNothingSelected(AdapterView<?> arg0) { }
		});
		
		TextView dsc = (TextView)findViewById(R.id.tvDiscount);
		if( document instanceof OrderImpl ) {
			((OrderImpl)document).setUpdateQtyHandler(new OrderImplBase.UpdateQtyHandler() {
				@Override
				public void itemUpdated(OrderItem item, Order order, boolean isNew) {
					((OrderItemEx)item).taxType = ((OrderEx)order).taxType;
					((OrderItemEx)item).discount = discount;
				}
			});
			
			dsc.setVisibility(View.VISIBLE);
			dsc.setOnClickListener(new View.OnClickListener() {				
				@Override public void onClick(View v) {
					InputNumberDlg.open(PriceCountEx.this, new InputNumber() {
						@Override public void applayInput(int value, Object... params) {
							discount = value;
							int cost = CostStrategyEx.getCost(price.getData(), (OrderImpl) document, discount);
							updateDiscount();
							onChangeCost(cost); 
						}
						@Override public int getValue() { return discount; }		
					}, Consts.SUM_SCALE, false, "¬ведите наценку"); 
				}
			});
		} else {
			dsc.setVisibility(View.GONE);
		}
		
	}
	
	void updateDiscount() {
		TextView tv = (TextView)findViewById(R.id.tvDiscount);
		SpannableString ss = new SpannableString(Util.IntToScaleStr(discount, Consts.SUM_SCALE, Util.DEC_DELIM, false));
		ss.setSpan(new UnderlineSpan(), 0, ss.length(), 0);
		tv.setText(ss);
	}
	
	@Override protected int getContentViewId() { return R.layout.pricecountex; }
	
	@Override
	protected void refreshData() {
		super.refreshData();

		PriceActionImpl pi = new PriceActionImpl();
		PriceActions pa = pi.getData();
		pa.id = price.getData().id;
		
		int actVisible = View.GONE;
		Date d = new Date(); 
		Date nulldate = new Date(70, 2, 1);
		if( pi.read() && pa.action.length() > 0 && 
				(pa.start == null || pa.start.before(d)) && 
				(pa.end == null || pa.end.after(d) || pa.end.before(nulldate)) ) {
		
			actVisible = View.VISIBLE;
			TextView tv;
			tv = (TextView)findViewById(R.id.tvAction);
			tv.setText(pa.action);
		}
		
		findViewById(R.id.llAction).setVisibility(actVisible);
		pi.close();
	

		Spinner sp = (Spinner)findViewById(R.id.spCostType);
		String priceId = price.getData().id;
		
		if( document instanceof OrderImpl && document.getRowid() != ExtrasConst.INVALID_ID ) {
			sp.setEnabled(true);
			
			ArrayList<KeyValue> values = new ArrayList<KeyValue>();
			CostTypes ct = new CostTypes();
			DbReader r = new DbReader();
			String table = DataObjectInfo.getInstance().getTableName(ct.getClass());
			String where = "id in (select idc from WHCost where id = '" + priceId + "')";

			OrderEx oe = (OrderEx)document.getData();
			OrderItemEx oie = (OrderItemEx)((OrderImpl)document).findItem(priceId);
			String taxType = (oie == null) ? (oe).taxType : oie.taxType;

			AccountsImpl ai = new AccountsImpl();
			Accounts a = ai.getData();
			a.type = oe.account;
			a.ido = oe.ido;
			
			String accType = "";
			if( ai.read() )
				accType = a.taxType;	
			ai.close();
			
//			OrgImpl oi = new OrgImpl();
//			OrgEx oe = (OrgEx)oi.getData();
//			oe.id = document.getId();
//			oi.read();
//			oi.close();
			
			int selected = -1;
			boolean bdo = r.select(ct, table, where, "name");
			while( bdo ) {
				if( ct.id.equals(accType) || ct.userid.length() == 0 ) {
					KeyValue kv = new KeyValue(ct.id, ct.name);
					if( kv.key.equals(taxType) )
						selected = values.size();
					
					values.add(kv);
					ct = new CostTypes();
				}
				bdo = r.selectNext(ct);
			}
			r.close();
			
			ArrayAdapter<KeyValue> aa = new ArrayAdapter<KeyValue>(this, R.layout.simple_spinner_layout, values);
			aa.setDropDownViewResource(R.layout.simple_spinner_layout_drop_down);
			sp.setAdapter(aa);
			if( selected >= 0 && selected < sp.getCount())
				sp.setSelection(selected);
		} else {
			sp.setEnabled(false);
		}
		
		OrderItemEx oe = (OrderItemEx)((OrderImpl)document).findItem(price.getData().id);
		if( oe != null )
			discount = oe.discount;
		else
			discount = 0;
		
		updateDiscount();
	}
	
//	@Override
//	protected void makeSaleHistory(Price p) {
//		if( document == null )
//			return;
//		
//		LinearLayout ll = (LinearLayout) findViewById(R.id.llSilesHistory);
//		ll.removeAllViews();
//		if (isComplexSalesHistory()) {
//			super.makeSaleHistory(p);
//			return; 
//		}
//		
//		List<SalesHistoryData> data = ((OrderDocEx)OrderDocEx.instance()).getHistoryData(document.getId(), p.id);
//		SimpleDateFormat df =  new SimpleDateFormat("dd.MM", Locale.getDefault());
//		for(SalesHistoryData hd : data) {
//			TextView tvSaleItem = new TextView(this);
//			tvSaleItem.setText(Html.fromHtml(
//					String.format("%s<br>%s<br>%s", hd.taxName, df.format(hd.date), Util.IntToScaleStr(hd.qty, Consts.QTY_SCALE) )));
//			tvSaleItem.setLines(3);
//			tvSaleItem.setTextColor(getResources().getColor(R.color.black));
//			tvSaleItem.setPadding(5, 3, 5, 3);
//			ll.addView(tvSaleItem);
//		}
//	}
}
